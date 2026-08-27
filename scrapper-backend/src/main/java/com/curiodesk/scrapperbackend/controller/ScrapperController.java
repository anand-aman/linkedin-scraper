package com.curiodesk.scrapperbackend.controller;

import com.curiodesk.scrapperbackend.api.request.ScrapeProfileRequest;
import com.curiodesk.scrapperbackend.service.ScrapperService;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ScrapperController {

    private final ScrapperService service;

    public ScrapperController(ScrapperService service) {
        this.service = service;
    }

    @GetMapping("/linkedin")
    public ResponseEntity<?> scrapProfile(@RequestBody ScrapeProfileRequest request) {

        return ResponseEntity.ok(service.scrape(request.url()));
    }
}
