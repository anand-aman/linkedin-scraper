package com.curiodesk.scraperbackend.api.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload containing a LinkedIn profile URL to scrape")
public record ScrapeProfileRequest(
        @Schema(
                description = "LinkedIn profile URL",
                example = "https://www.linkedin.com/in/john-doe/",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String url
) {
}
