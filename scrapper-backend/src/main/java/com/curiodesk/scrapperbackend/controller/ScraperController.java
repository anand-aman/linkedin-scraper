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
@Tag(name = "LinkedIn Scrapper", description = "Endpoints to scrape LinkedIn profile data")
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
            description = "Set type=html for HTML parser, type=parsed for parsed JSON response, or omit type/use hybrid for the hybrid response."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "LinkedIn profile scraped successfully",
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
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "External scraping provider error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Object> scrapeProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "LinkedIn scrape request payload"
            )
            @RequestBody ScrapeProfileRequest request,
            @Parameter(
                    description = "Scrape type. Defaults to hybrid when omitted/blank.",
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
