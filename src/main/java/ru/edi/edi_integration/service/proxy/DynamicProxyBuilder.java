package ru.edi.edi_integration.service.proxy;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.edi.convert_api.ExternalService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyBuilder {

    private final EurekaClient eurekaClient;
    private final RestTemplate restTemplate;
    private final OpenApiRouteResolver routeResolver; // добавь в конструктор

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

    private Object invokeRemote(String serviceId, Class<?> clazz, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        OpenApiRouteResolver.Operation op = routeResolver.resolve(serviceId, method.getName());
        if (op == null) {
            throw new IllegalStateException("No OpenAPI mapping found for method " + method.getName());
        }

        InstanceInfo instance = eurekaClient.getNextServerFromEureka(serviceId, false);
        String baseUrl = instance.getHomePageUrl();
        String fullUrl = baseUrl + op.path();

        HttpMethod httpMethod = HttpMethod.valueOf(op.httpMethod());

        System.out.printf("Calling [%s] %s with args: %s%n", httpMethod, fullUrl, args != null && args.length > 0 ? args[0] : "none");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE) {
            String queryParam = (args != null && args.length > 0) ? "?arg=" + args[0] : "";
            fullUrl += queryParam;
            return restTemplate.exchange(fullUrl, httpMethod, new HttpEntity<>(headers), method.getReturnType()).getBody();
        } else {
            Object body = (args != null && args.length > 0) ? args[0] : null;
            HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
            return restTemplate.exchange(fullUrl, httpMethod, requestEntity, method.getReturnType()).getBody();
        }
    }
}
