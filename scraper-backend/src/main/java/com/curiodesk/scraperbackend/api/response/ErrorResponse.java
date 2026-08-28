package com.curiodesk.scraperbackend.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Standard API error payload")
public record ErrorResponse(
        @Schema(description = "Timestamp of the error")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "HTTP status text", example = "Bad Request")
        String error,
        @Schema(description = "Human-readable error message", example = "Malformed request")
        String message,
        @Schema(description = "Request path that produced the error", example = "/api/linkedin")
        String path
) {
}
