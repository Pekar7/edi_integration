package ru.edi.edi_integration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.edi.convert_api.HelloService;
import java.lang.reflect.Proxy;

@Configuration
public class HelloServiceProxyConfig {

    @Value("${serviceUrls.com.example.edi_intconvert.service.HelloService}")
    private String helloServiceUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HelloService helloService(RestTemplate restTemplate) {
        return (HelloService) Proxy.newProxyInstance(
                HelloService.class.getClassLoader(),
                new Class[]{HelloService.class},
                new ru.edi.edi_integration.service.HelloServiceInvocationHandler(helloServiceUrl, restTemplate)
        );
    }
}
