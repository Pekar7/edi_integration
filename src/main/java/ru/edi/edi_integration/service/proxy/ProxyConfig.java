package ru.edi.edi_integration.service.proxy;

import com.netflix.discovery.EurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.edi.convert_api.HelloService;

@Configuration
public class ProxyConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OpenApiRouteResolver openApiRouteResolver(EurekaClient eurekaClient, RestTemplate restTemplate) {
        return new OpenApiRouteResolver(eurekaClient, restTemplate);
    }

    @Bean
    public DynamicProxyBuilder proxyBuilder(
            EurekaClient eurekaClient,
            RestTemplate restTemplate,
            OpenApiRouteResolver routeResolver
    ) {
        return new DynamicProxyBuilder(eurekaClient, restTemplate, routeResolver);
    }

    @Bean
    public HelloService helloService(DynamicProxyBuilder proxyBuilder) {
        return proxyBuilder.build(HelloService.class, "intConvert");
    }
}
