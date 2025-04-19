package ru.edi.edi_integration.cfg.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("keycloak", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl("http://localhost:8080/realms/edi/protocol/openid-connect/auth")
                                                .tokenUrl("http://localhost:8080/realms/edi/protocol/openid-connect/token")
                                                .scopes(new Scopes().addString("openid", "OpenID scope"))
                                        )
                                )
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("keycloak"));
    }
}
