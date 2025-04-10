package ru.edi.edi_integration.service.proxy;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.edi.convert_api.ExternalService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class DynamicProxyBuilder {

    private static final Logger log = LoggerFactory.getLogger(DynamicProxyBuilder.class);

    private final EurekaClient eurekaClient;
    private final RestTemplate restTemplate;
    private final OpenApiRouteResolver routeResolver;

    public DynamicProxyBuilder(EurekaClient eurekaClient, RestTemplate restTemplate, OpenApiRouteResolver routeResolver) {
        this.eurekaClient = eurekaClient;
        this.restTemplate = restTemplate;
        this.routeResolver = routeResolver;
    }

    public <T extends ExternalService> T build(Class<T> interfaceClass, String serviceId) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                (proxy, method, args) -> invokeRemote(serviceId, interfaceClass, method, args)
        );
    }

    private Object invokeRemote(String serviceId, Class<?> clazz, Method method, Object[] args)
            throws InvocationTargetException, IllegalAccessException {

        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        String traceId = UUID.randomUUID().toString();
        Instant start = Instant.now();

        OpenApiRouteResolver.Operation operation = routeResolver.resolve(serviceId, method.getName());
        if (operation == null) {
            throw new IllegalStateException("No OpenAPI mapping found for method " + method.getName());
        }

        InstanceInfo instance = eurekaClient.getNextServerFromEureka(serviceId, false);
        String baseUrl = instance.getHomePageUrl();
        String fullUrl = baseUrl + operation.path();
        HttpMethod httpMethod = HttpMethod.valueOf(operation.httpMethod());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Object response;
        try {
            log.info("{} Вызов метода {}#{}({})", traceId, clazz.getName(), method.getName(), args != null && args.length > 0 ? args[0] : "");

            if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE) {
                String queryParam = (args != null && args.length > 0) ? "?arg=" + args[0] : "";
                fullUrl += queryParam;
                response = restTemplate.exchange(fullUrl, httpMethod, new HttpEntity<>(headers), method.getReturnType()).getBody();
            } else {
                Object body = (args != null && args.length > 0) ? args[0] : null;
                HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
                response = restTemplate.exchange(fullUrl, httpMethod, requestEntity, method.getReturnType()).getBody();
            }

            Duration duration = Duration.between(start, Instant.now());
            log.info("{} Вызов метода завершился. Время выполнения: {} ms", traceId, duration.toMillis());

            return response;
        } catch (Exception e) {
            log.error("{} Ошибка при вызове метода {}#{}: {}", traceId, clazz.getName(), method.getName(), e.getMessage(), e);
            throw e;
        }
    }
}
