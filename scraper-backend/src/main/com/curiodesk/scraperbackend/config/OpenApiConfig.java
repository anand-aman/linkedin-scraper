package main.com.curiodesk.scraperbackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Scraper Backend API",
                version = "v2",
                description = "REST API for scraping LinkedIn profile data. "
                        + "Use POST /api/linkedin with a LinkedIn profile URL and optional query param "
                        + "type=hybrid|html|parsed (hybrid is the default). "
                        + "Responses return structured profile data or a standard error payload.",
                contact = @Contact(name = "Scraper Backend")
        ),
        servers = @Server(url = "/", description = "Default server")
)
public class OpenApiConfig {
}
