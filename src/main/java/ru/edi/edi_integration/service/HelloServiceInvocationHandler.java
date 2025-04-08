package ru.edi.edi_integration.service;

import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HelloServiceInvocationHandler implements InvocationHandler {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public HelloServiceInvocationHandler(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Обработка служебных методов (Object)
        if (method.getDeclaringClass() == Object.class) {
            switch (method.getName()) {
                case "toString": return "Proxy for HelloService";
                case "hashCode": return System.identityHashCode(proxy);
                case "equals": return proxy == args[0];
                default: return null;
            }
        }

        // Если метод — из интерфейса, но аргументов нет — безопасно игнорируем
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Proxy method " + method.getName() + " called without arguments");
        }

        String url = baseUrl + "/send";
        return restTemplate.postForObject(url, args[0], method.getReturnType());
    }
}
