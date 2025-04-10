package ru.edi.edi_integration.service.proxy;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import ru.edi.convert_api.ExternalService;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyBuilder {

    private static final Logger log = LoggerFactory.getLogger(DynamicProxyBuilder.class);

    private final EurekaClient eurekaClient;
    private final RestTemplate restTemplate;

    public DynamicProxyBuilder(EurekaClient eurekaClient, RestTemplate restTemplate) {
        this.eurekaClient = eurekaClient;
        this.restTemplate = restTemplate;
    }

    public <T extends ExternalService> T build(Class<T> interfaceClass, String serviceId) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                (proxy, method, args) -> invokeRemote(proxy, serviceId, interfaceClass, method, args)
        );
    }

    private Object invokeRemote(Object proxy, String serviceId, Class<?> clazz, Method method, Object[] args) {
        // Обработка стандартных методов Object
        if (method.getDeclaringClass() == Object.class) {
            return switch (method.getName()) {
                case "toString" -> clazz.getSimpleName() + " proxy";
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> throw new UnsupportedOperationException("Unhandled Object method: " + method.getName());
            };
        }

        // Получаем адрес сервиса из Eureka
        InstanceInfo instance = eurekaClient.getNextServerFromEureka(serviceId, false);
        String baseUrl = instance.getHomePageUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        // Строим endpoint на основе имени интерфейса и метода
        String context = clazz.getSimpleName().replace("Service", "").toLowerCase(); // hello
        String endpoint = "/" + context + "/" + method.getName();
        String fullUrl = baseUrl + endpoint;

        Object body = (args != null && args.length > 0) ? args[0] : null;

        log.info("Calling {} with argument: {}", fullUrl, body);

        return restTemplate.postForObject(fullUrl, body, method.getReturnType());
    }
}
