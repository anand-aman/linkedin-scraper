package com.curiodesk.scrapperbackend.controller;

import com.curiodesk.scrapperbackend.api.request.ScrapeProfileRequest;
import com.curiodesk.scrapperbackend.api.response.LinkedInProfile;
import com.curiodesk.scrapperbackend.exception.BadRequestException;
import com.curiodesk.scrapperbackend.service.ScrapperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ScrapperController {

    private final ScrapperService service;

    public ScrapperController(ScrapperService service) {
        this.service = service;
    }

    @PostMapping("/linkedin")
    public ResponseEntity<LinkedInProfile> scrapProfile(@RequestBody ScrapeProfileRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        return ResponseEntity.ok(service.scrape(request.url()));
    }

    @GetMapping("/linkedin")
    public ResponseEntity<LinkedInProfile> scrapeProfileByQueryParam(@RequestParam("url") String url) {
        return ResponseEntity.ok(service.scrape(url));
    }
}
