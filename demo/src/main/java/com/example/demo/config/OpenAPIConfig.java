package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

@Configuration
public class OpenAPIConfig {

    @Value("${project.name}")
    private String name;
    @Value("${project.version}")
    private String version;

    @Bean
    public OpenAPI getOpenApiDefinition() {
        return new OpenAPI()
                    .info(info());
    }

    private Info info() {
        return new Info()
                .title(name + " OpenAPI Specification documentation")
                .version(version)
                .description("API REST, which is the backend of " + name + "'s management software. " +
                    "Describes the API endpoints, input and output parameters, response codes and data schemas used. It also " +
                    "includes detailed information on the authentication and authorization required to access the API. " +
                    "It provides developers with all the information needed to integrate the " + name + " API into their " +
                    "own applications. This includes code samples, detailed instructions and descriptions of each API endpoint.")
                .contact(contact())
                .license(license());
    }


    private Contact contact() {
        return new Contact()
                .name("XXXXXXXXXX")
                .email("XXXXXXXXXX");
    }

    private License license() {
        return new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html");
    }
}
