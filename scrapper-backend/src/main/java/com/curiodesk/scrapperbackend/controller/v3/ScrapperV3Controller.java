package com.curiodesk.scrapperbackend.controller.v3;

import com.curiodesk.scrapperbackend.api.request.ScrapeProfileRequest;
import com.curiodesk.scrapperbackend.api.response.ErrorResponse;
import com.curiodesk.scrapperbackend.api.response.LinkedInProfileV3;
import com.curiodesk.scrapperbackend.exception.BadRequestException;
import com.curiodesk.scrapperbackend.service.HybridScrapperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3")
@Tag(name = "LinkedIn Scrapper v3", description = "Hybrid endpoint combining parsed and HTML extraction")
public class ScrapperV3Controller {

    private final HybridScrapperService service;

    public ScrapperV3Controller(HybridScrapperService service) {
        this.service = service;
    }

    @PostMapping("/linkedin")
    @Operation(summary = "Scrape LinkedIn profile with hybrid parsing")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "LinkedIn profile scraped successfully",
                    content = @Content(schema = @Schema(implementation = LinkedInProfileV3.class))
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
    public ResponseEntity<LinkedInProfileV3> scrapeProfile(@RequestBody ScrapeProfileRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        return ResponseEntity.ok(service.scrape(request.url()));
    }
}
