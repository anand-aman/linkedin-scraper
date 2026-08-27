package com.curiodesk.scrapperbackend.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ZenRowsResponse(
        String html,
        Parsed parsed
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Parsed(
            @JsonProperty("current_position")
            CurrentPosition currentPosition,

            List<Education> education,

            List<Experience> experience,

            List<String> languages,

            Member member,

            List<Post> posts
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Member(
            @JsonProperty("connection_count_label")
            String connectionCountLabel,

            @JsonProperty("country_code")
            String countryCode,

            @JsonProperty("cover_image_url")
            String coverImageUrl,

            @JsonProperty("follower_count")
            Integer followerCount,

            @JsonProperty("full_name")
            String fullName,

            String location,

            @JsonProperty("profile_photo_url")
            String profilePhotoUrl,

            @JsonProperty("vanity_url")
            String vanityUrl
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Post(
            @JsonProperty("date_published")
            String datePublished,

            @JsonProperty("like_count")
            Integer likeCount,

            @JsonProperty("post_url")
            String postUrl,

            String text
    ) {
    }

    public record Education() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentPosition(
            @JsonProperty("company_linkedin_url")
            String companyLinkedinUrl,

            @JsonProperty("company_logo_url")
            String companyLogoUrl,

            @JsonProperty("company_name")
            String companyName,

            @JsonProperty("job_title")
            String jobTitle
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Experience(
            @JsonProperty("company_linkedin_url")
            String companyLinkedinUrl,

            @JsonProperty("company_logo_url")
            String companyLogoUrl,

            @JsonProperty("company_name")
            String companyName,

            String description,

            @JsonProperty("job_title")
            String jobTitle,

            String location
    ) {}
}