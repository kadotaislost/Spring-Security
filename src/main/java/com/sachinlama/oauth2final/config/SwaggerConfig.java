package com.sachinlama.oauth2final.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myCustomConfig(){
        return new OpenAPI()
                .info(new Info().title("BASIC AND OAUTH2")
                        .description("by Sachin Lama"))
                .servers(Arrays.asList(new Server().url("http://localhost:8080").description("Local Server"),
                        new Server().url("http://localhost:8081").description("Live")));

    }
}
