package com.curiodesk.scrapperbackend.service;

import com.curiodesk.scrapperbackend.api.response.ZenRowsResponse;
import com.curiodesk.scrapperbackend.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.ObjectMapper;

@Service
public class ZenRowsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZenRowsClient.class);
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public ZenRowsClient(
            ObjectMapper objectMapper,
            @Value("${zenrows.base_url}") String baseUrl,
            @Value("${zenrows.api_key}") String apiKey
    ) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public ZenRowsResponse fetchProfile(String linkedinUrl) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ExternalServiceException("ZenRows API key is not configured");
        }

        try {
            LOGGER.info("Requesting profile data from ZenRows for {}", linkedinUrl);
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

            if (body == null || body.isBlank()) {
                throw new ExternalServiceException("ZenRows returned an empty response");
            }

            return objectMapper.readValue(body, ZenRowsResponse.class);
        } catch (RestClientResponseException ex) {
            LOGGER.error("ZenRows API returned status {} for {}", ex.getStatusCode().value(), linkedinUrl);
            throw new ExternalServiceException(
                    "ZenRows API request failed with status " + ex.getStatusCode().value(),
                    ex
            );
        } catch (RestClientException ex) {
            LOGGER.error("ZenRows API call failed for {}", linkedinUrl, ex);
            throw new ExternalServiceException("Unable to reach ZenRows API", ex);
        } catch (Exception ex) {
            LOGGER.error("Failed to parse ZenRows response for {}", linkedinUrl, ex);
            throw new ExternalServiceException("Failed to parse ZenRows response", ex);
        }
    }
}
