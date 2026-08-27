package com.curiodesk.scrapperbackend.controller.v2;

import com.curiodesk.scrapperbackend.api.request.ScrapeProfileRequest;
import com.curiodesk.scrapperbackend.api.response.LinkedInProfileV2;
import com.curiodesk.scrapperbackend.service.HtmlScraperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
public class ScraperController {

    private final HtmlScraperService scraperService;

    public ScraperController(
            HtmlScraperService scraperService) {

        this.scraperService = scraperService;
    }

    @PostMapping("/linkedin")
    public ResponseEntity<LinkedInProfileV2> scrape(@RequestBody ScrapeProfileRequest request) {

        LinkedInProfileV2 profile = scraperService.scrapeProfileFromHtml(request.url());

        return ResponseEntity.ok(profile);
    }
}
