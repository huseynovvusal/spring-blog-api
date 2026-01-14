package com.huseynovvusal.springblogapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Value("${openapi.server.url}")
    private String serverUrl;

    @Value("${openapi.server.description}")
    private String serverDescription;

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl(serverUrl);
        server.setDescription(serverDescription);

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        Info information = new Info()
                .title("Spring Blog API")
                .version("1.0")
                .description("Find all API endpoints and related info here...");
        return new OpenAPI().info(information)
                .servers(List.of(server))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme));
    }
}
