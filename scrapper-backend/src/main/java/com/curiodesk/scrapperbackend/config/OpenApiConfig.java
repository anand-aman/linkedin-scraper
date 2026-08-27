package com.curiodesk.scrapperbackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Scrapper Backend API",
                version = "v2",
                description = "API for scraping LinkedIn profile data with parser types: parsed, html, hybrid (default)",
                contact = @Contact(name = "Scrapper Backend")
        ),
        servers = @Server(url = "/", description = "Default server")
)
public class OpenApiConfig {
}
