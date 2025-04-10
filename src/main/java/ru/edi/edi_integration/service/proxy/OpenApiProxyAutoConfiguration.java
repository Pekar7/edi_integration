//package ru.edi.edi_integration.service.proxy;
//
//import com.netflix.discovery.EurekaClient;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestTemplate;
//
//@Configuration
//@ConditionalOnClass({EurekaClient.class, RestTemplate.class})
//public class OpenApiProxyAutoConfiguration {
//
//    @Bean
//    @ConditionalOnMissingBean
//    public OpenApiRouteResolver openApiRouteResolver(EurekaClient eurekaClient, RestTemplate restTemplate) {
//        return new OpenApiRouteResolver(eurekaClient, restTemplate);
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public DynamicProxyBuilder proxyBuilder(EurekaClient eurekaClient, RestTemplate restTemplate, OpenApiRouteResolver routeResolver) {
//        return new DynamicProxyBuilder(eurekaClient, restTemplate, routeResolver);
//    }
//}
//
