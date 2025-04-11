package ru.edi.edi_integration.service.proxy;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.ResolvableType;
import org.springframework.web.client.RestTemplate;
import ru.edi.convert_api.ExternalService;
import ru.edi.edi_integration.service.proxy.discovery.ApplicationContextProvider;

import java.beans.Introspector;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@Import(ApplicationContextProvider.class)
public class ProxyConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OpenApiRouteResolver openApiRouteResolver(EurekaClient eurekaClient, RestTemplate restTemplate) {
        return new OpenApiRouteResolver(eurekaClient, restTemplate);
    }

    @Bean
    public DynamicProxyBuilder proxyBuilder(EurekaClient eurekaClient, RestTemplate restTemplate, OpenApiRouteResolver routeResolver) {
        return new DynamicProxyBuilder(eurekaClient, restTemplate, routeResolver);
    }

    @Bean
    public BeanDefinitionRegistryPostProcessor externalServiceAutoRegistrar() {
        return registry -> {
            // Типизированный биндинг из properties
            ResolvableType type = ResolvableType.forClassWithGenerics(
                    List.class,
                    ResolvableType.forClassWithGenerics(Map.class, String.class, Object.class)
            );

            Bindable<List<Map<String, Object>>> target = Bindable.of(type);

            List<Map<String, Object>> configs = Binder.get(applicationContext.getEnvironment())
                    .bind("external-services", target)
                    .orElse(Collections.emptyList());

            for (Map<String, Object> item : configs) {
                try {
                    String alias = (String) item.get("alias");
                    String className = (String) item.get("class-name");

                    Map<String, Object> parameters = (Map<String, Object>) item.get("parameters");
                    String serviceId = (String) parameters.get("service-id");

                    Class<?> rawClass = Class.forName(className);
                    if (!rawClass.isInterface() || !ExternalService.class.isAssignableFrom(rawClass)) continue;

                    @SuppressWarnings("unchecked")
                    Class<? extends ExternalService> clazz = (Class<? extends ExternalService>) rawClass;

                    String beanName = alias != null ? alias : Introspector.decapitalize(clazz.getSimpleName());

                    BeanDefinitionBuilder builder = createProxyBeanDefinition(clazz, serviceId, applicationContext);
                    registry.registerBeanDefinition(beanName, builder.getBeanDefinition());

                } catch (Exception e) {
                    throw new RuntimeException("Failed to create proxy bean from config", e);
                }
            }
        };
    }

    private static <T extends ExternalService> BeanDefinitionBuilder createProxyBeanDefinition(
            Class<T> clazz,
            String serviceId,
            ApplicationContext context
    ) {
        return BeanDefinitionBuilder.genericBeanDefinition(clazz, () -> {
            DynamicProxyBuilder builder = context.getBean(DynamicProxyBuilder.class);
            return builder.build(clazz, serviceId);
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }
}