package com.curiodesk.scrapperbackend.service;

import com.curiodesk.scrapperbackend.api.response.ZenRowsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ZenRowsClient {

    private final RestClient restClient;

    private final String baseUrl = "https://api.zenrows.com/v1/";
    private final String apiKey = "";

    public ZenRowsClient(
            RestClient.Builder builder
    ) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public ZenRowsResponse fetchProfile(String linkedinUrl) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", apiKey)
                        .queryParam("url", linkedinUrl)
                        .queryParam("extract", "auto")
                        .queryParam("js_render", "true")
                        .queryParam("premium_proxy", "true")
                        .build())
                .retrieve()
                .body(ZenRowsResponse.class);
    }
}