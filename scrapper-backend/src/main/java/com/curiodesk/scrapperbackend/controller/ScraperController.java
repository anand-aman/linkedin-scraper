package com.curiodesk.scrapperbackend.controller;

import com.curiodesk.scrapperbackend.api.request.ScrapeProfileRequest;
import com.curiodesk.scrapperbackend.api.response.ErrorResponse;
import com.curiodesk.scrapperbackend.api.response.ParsedLinkedInProfileResponse;
import com.curiodesk.scrapperbackend.api.response.HtmlLinkedInProfileResponse;
import com.curiodesk.scrapperbackend.api.response.HybridLinkedInProfileResponse;
import com.curiodesk.scrapperbackend.exception.BadRequestException;
import com.curiodesk.scrapperbackend.service.HtmlScraperService;
import com.curiodesk.scrapperbackend.service.HybridScrapperService;
import com.curiodesk.scrapperbackend.service.ParsedJsonScraperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(
        name = "LinkedIn Scrapper",
        description = "Endpoint for collecting profile details from a LinkedIn public profile URL "
                + "using one of the available parser modes."
)
public class ScraperController {

    private final ParsedJsonScraperService parsedService;
    private final HtmlScraperService htmlService;
    private final HybridScrapperService hybridService;

    public ScraperController(
            ParsedJsonScraperService parsedService,
            HtmlScraperService htmlService,
            HybridScrapperService hybridService
    ) {
        this.parsedService = parsedService;
        this.htmlService = htmlService;
        this.hybridService = hybridService;
    }

    @PostMapping("/linkedin")
    @Operation(
            summary = "Scrape LinkedIn profile by parser type",
            description = "Submits a LinkedIn profile URL for scraping and returns profile details. "
                    + "Parser mode is controlled with the optional query parameter 'type': "
                    + "'hybrid' (default, merged output), 'html' (HTML-based extraction), "
                    + "or 'parsed' (normalized parsed payload)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile data scraped successfully. "
                            + "The response schema depends on the selected parser type.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    oneOf = {
                                            HybridLinkedInProfileResponse.class,
                                            HtmlLinkedInProfileResponse.class,
                                            ParsedLinkedInProfileResponse.class
                                    }
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or unsupported type parameter value.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Failed to fetch or parse profile data from the upstream scraping provider.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server-side error while processing the scrape request.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Object> scrapeProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Request payload containing the LinkedIn profile URL to scrape."
            )
            @RequestBody ScrapeProfileRequest request,
            @Parameter(
                    description = "Optional parser mode. "
                            + "Frontend calls omit this parameter and default to 'hybrid'. "
                            + "'hybrid' returns merged data, 'html' returns HTML-derived data, "
                            + "and 'parsed' returns normalized parsed data. Defaults to 'hybrid'.",
                    example = "hybrid",
                    schema = @Schema(
                            allowableValues = {"hybrid", "html", "parsed"},
                            defaultValue = "hybrid"
                    )
            )
            @RequestParam(required = false) String type
    ) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        String normalizedType = normalizeType(type);

        return switch (normalizedType) {
            case "html" -> ResponseEntity.ok(
                    htmlService.scrapeProfileFromHtml(request.url())
            );
            case "parsed" -> ResponseEntity.ok(
                    parsedService.scrape(request.url())
            );
            case "hybrid" -> ResponseEntity.ok(
                    hybridService.scrape(request.url())
            );
            default -> throw new BadRequestException(
                    "Invalid scrape type '" + normalizedType + "'. Allowed values: parsed, html, hybrid"
            );
        };
    }

    private String normalizeType(String type) {
        if (type == null) {
            return "hybrid";
        }

        String normalized = type.trim().toLowerCase();
        return normalized.isEmpty() ? "hybrid" : normalized;
    }
}
