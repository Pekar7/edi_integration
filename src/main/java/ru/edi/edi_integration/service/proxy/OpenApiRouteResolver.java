package ru.edi.edi_integration.service.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class OpenApiRouteResolver {

    private static final Logger log = LoggerFactory.getLogger(OpenApiRouteResolver.class);

    private final EurekaClient eurekaClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Map<String, Operation>> cache = new HashMap<>();

    public OpenApiRouteResolver(EurekaClient eurekaClient, RestTemplate restTemplate) {
        this.eurekaClient = eurekaClient;
        this.restTemplate = restTemplate;
    }

    public Operation resolve(String serviceId, String operationId) {
        try {
            Map<String, Operation> ops = cache.computeIfAbsent(serviceId, sid -> {
                try {
                    return fetchOperations(sid);
                } catch (Exception e) {
                    // логируем, но не кладем пустую мапу в кэш!
                    log.error("Не удалось получить OpenAPI операции для сервиса '{}': {}", sid, e.getMessage());
                    return null;
                }
            });

            if (ops == null || !ops.containsKey(operationId)) {
                throw new IllegalStateException("No OpenAPI mapping found for method " + operationId);
            }

            return ops.get(operationId);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при разрешении маршрута для сервиса: " + serviceId + ", метод: " + operationId, e);
        }
    }

    private Map<String, Operation> fetchOperations(String serviceId) {
        int retries = 10;
        int delayMillis = 1000;

        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                InstanceInfo instance = eurekaClient.getNextServerFromEureka(serviceId, false);
                String url = instance.getHomePageUrl() + "/v3/api-docs";
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                Map<String, Operation> result = new HashMap<>();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode paths = root.get("paths");

                if (paths != null) {
                    paths.fields().forEachRemaining(entry -> {
                        String path = entry.getKey();
                        JsonNode methodsNode = entry.getValue();

                        methodsNode.fields().forEachRemaining(methodEntry -> {
                            String httpMethod = methodEntry.getKey().toUpperCase();
                            JsonNode methodDetails = methodEntry.getValue();
                            String opId = methodDetails.get("operationId").asText();
                            result.put(opId, new Operation(httpMethod, path));
                        });
                    });
                }

                return result;
            } catch (Exception e) {
                if (attempt == retries) {
                    log.error("Не удалось получить OpenAPI операции для сервиса '{}': {}", serviceId, e.getMessage());
                    throw new RuntimeException("Ошибка при разрешении маршрута для сервиса: " + serviceId, e);
                } else {
                    log.warn("Попытка {} из {}: сервис '{}' не найден. Повтор через {}мс", attempt, retries, serviceId, delayMillis);
                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        return Map.of(); // если всё совсем плохо
    }

    public record Operation(String httpMethod, String path) {}
}
