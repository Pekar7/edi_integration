package ru.edi.edi_integration.service;

import com.netflix.discovery.EurekaClient;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.edi.convert_api.HelloService;
import ru.edi.edi_integration.service.proxy.DynamicProxyBuilder;


@Configuration
public class ProxyConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DynamicProxyBuilder proxyBuilder(EurekaClient eurekaClient, RestTemplate restTemplate) {
        return new DynamicProxyBuilder(eurekaClient, restTemplate);
    }

    @Bean
    public HelloService helloService(DynamicProxyBuilder proxyBuilder) {
        return proxyBuilder.build(HelloService.class, "intConvert"); // правильно
    }
}
