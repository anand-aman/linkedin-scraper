package com.curiodesk.scrapperbackend.service;

import com.curiodesk.scrapperbackend.api.response.ZenRowsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Service
public class ZenRowsClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${zenrows.base_url}")
    private String baseUrl;
    @Value("${zenrows.api_key}")
    private String apiKey;


    public ZenRowsClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public ZenRowsResponse fetchProfile(String linkedinUrl) {
        String body = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", apiKey)
                        .queryParam("url", linkedinUrl)
                        .queryParam("extract", "auto")
                        .queryParam("js_render", "true")
                        .queryParam("premium_proxy", "true")
                        .build())
                .retrieve()
                .body(String.class);
        try {
            return objectMapper.readValue(body, ZenRowsResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ZenRows response", e);
        }
    }
}