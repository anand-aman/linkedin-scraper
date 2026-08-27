package com.curiodesk.scrapperbackend.controller;

import com.curiodesk.scrapperbackend.api.request.ScrapeProfileRequest;
import com.curiodesk.scrapperbackend.api.response.ErrorResponse;
import com.curiodesk.scrapperbackend.api.response.LinkedInProfileV1;
import com.curiodesk.scrapperbackend.exception.BadRequestException;
import com.curiodesk.scrapperbackend.service.ScrapperService;
import io.swagger.v3.oas.annotations.Operation;
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
public class ScrapperController {

    private final ScrapperService service;

    public ScrapperController(ScrapperService service) {
        this.service = service;
    }

    @PostMapping("/linkedin")
    @Operation(summary = "Scrape LinkedIn profile using request body")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "LinkedIn profile scraped successfully",
                    content = @Content(schema = @Schema(implementation = LinkedInProfileV1.class))
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
    public ResponseEntity<LinkedInProfileV1> scrapProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "LinkedIn scrape request payload"
            )
            @RequestBody ScrapeProfileRequest request
    ) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        return ResponseEntity.ok(service.scrape(request.url()));
    }
}
