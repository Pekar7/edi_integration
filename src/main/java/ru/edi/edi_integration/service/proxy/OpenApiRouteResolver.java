package ru.edi.edi_integration.service.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class OpenApiRouteResolver {

    private final EurekaClient eurekaClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Кэш: Map<serviceId, Map<operationId, (method + path)>>
    private final Map<String, Map<String, Operation>> cache = new HashMap<>();

    public OpenApiRouteResolver(EurekaClient eurekaClient, RestTemplate restTemplate) {
        this.eurekaClient = eurekaClient;
        this.restTemplate = restTemplate;
    }

    public Operation resolve(String serviceId, String operationId) {
        Map<String, Operation> ops = cache.computeIfAbsent(serviceId, this::fetchOperations);
        return ops.get(operationId);
    }

    private Map<String, Operation> fetchOperations(String serviceId) {
        InstanceInfo instance = eurekaClient.getNextServerFromEureka(serviceId, false);
        String url = instance.getHomePageUrl() + "/v3/api-docs";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        Map<String, Operation> result = new HashMap<>();

        try {
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
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAPI docs for service: " + serviceId, e);
        }

        return result;
    }

    public record Operation(String httpMethod, String path) {}
}
