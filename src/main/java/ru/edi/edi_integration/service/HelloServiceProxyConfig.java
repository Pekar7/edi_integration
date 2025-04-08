package ru.edi.edi_integration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.edi.convert_api.HelloService;
import java.lang.reflect.Proxy;

@Configuration
public class HelloServiceProxyConfig {

    @Value("${serviceUrls.hello}")
    private String helloServiceUrl;

    @Bean
    public HelloService helloService(RestTemplate restTemplate) {
        return (HelloService) Proxy.newProxyInstance(
                HelloService.class.getClassLoader(),
                new Class[]{HelloService.class},
                new ExternalServiceInvocationHandler(helloServiceUrl, restTemplate)
        );
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

