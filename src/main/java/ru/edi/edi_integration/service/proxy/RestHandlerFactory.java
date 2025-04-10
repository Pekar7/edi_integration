package ru.edi.edi_integration.service.proxy;

import org.springframework.web.client.RestTemplate;

import java.util.Properties;

public class RestHandlerFactory implements HandlerFactory {

    private final RestTemplate restTemplate;

    public RestHandlerFactory(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public MethodHandler create(String className, Properties parameters) {
        String url = parameters.getProperty("url");
        return (methodName, arg, returnType) ->
            restTemplate.postForObject(url + "/hello/" + methodName, arg, returnType);
    }
}