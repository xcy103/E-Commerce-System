package com.ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Swagger/OpenAPI documentation.
 */
@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI ecommerceOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("E-Commerce Order and Inventory Management API")
                                                .description("Backend API for E-Commerce system with transactional checkout and async order fulfillment")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("API Support")
                                                                .email("support@ecommerce.com")));
        }

        @Bean
        public GroupedOpenApi publicApi() {
                return GroupedOpenApi.builder()
                                .group("public")
                                .pathsToMatch("/**")
                                .build();
        }
}
