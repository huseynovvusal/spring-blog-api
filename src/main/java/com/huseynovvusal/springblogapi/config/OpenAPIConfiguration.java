package com.huseynovvusal.springblogapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {
    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8082");
        server.setDescription("Development");

        Info information = new Info()
                .title("Spring Blog API")
                .version("1.0")
                .description("Find all API enpoints and related info here...");
        return new OpenAPI().info(information).servers(List.of(server));
    }
}
