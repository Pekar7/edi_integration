package ru.edi.edi_integration.service;

import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ExternalServiceInvocationHandler implements InvocationHandler {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public ExternalServiceInvocationHandler(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return switch (method.getName()) {
                case "toString" -> proxy.getClass().getName();
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> null;
            };
        }

        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("Expected exactly one argument for method " + method.getName());
        }

        String url = baseUrl + method.getName();
        return restTemplate.postForObject(url, args[0], method.getReturnType());
    }
}