package com.curiodesk.scrapperbackend.controller;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ScrapperController {

    @GetMapping("/linkedin")
    public ResponseEntity<?> scrapProfile(@RequestParam String url) {
        return ResponseEntity.ok("Scraping LinkedIn profile for URL: " + url);
    }
}
