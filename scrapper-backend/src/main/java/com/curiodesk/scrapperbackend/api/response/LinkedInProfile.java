package com.curiodesk.scrapperbackend.api.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Normalized LinkedIn profile response")
public record LinkedInProfile(
        @Schema(description = "Original LinkedIn URL that was scraped")
        String profileUrl,
        @Schema(description = "LinkedIn vanity URL")
        String vanityUrl,
        @Schema(description = "Profile full name")
        String fullName,
        @Schema(description = "Profile headline")
        String headline,
        @Schema(description = "Profile location")
        String location,
        @Schema(description = "Country code from LinkedIn profile")
        String countryCode,
        @Schema(description = "Follower count")
        Integer followerCount,
        @Schema(description = "Connection count label")
        String connectionCount,
        @Schema(description = "Profile photo URL")
        String profilePhotoUrl,
        @Schema(description = "Cover image URL")
        String coverImageUrl,
        @ArraySchema(arraySchema = @Schema(description = "Languages listed on profile"))
        List<String> languages,
        @ArraySchema(arraySchema = @Schema(description = "Recent posts"))
        List<Post> posts,
        @ArraySchema(arraySchema = @Schema(description = "Work experiences"))
        List<Experience> experiences
) {
    @Builder
    @Schema(description = "LinkedIn post")
    public record Post(
            @Schema(description = "Post publication date")
            String datePublished,
            @Schema(description = "Post like count")
            Integer likeCount,
            @Schema(description = "Post URL")
            String postUrl,
            @Schema(description = "Post text")
            String text
    ) {}

    @Builder
    @Schema(description = "LinkedIn work experience")
    public record Experience(
            @Schema(description = "Company name")
            String companyName,
            @Schema(description = "Experience description")
            String description,
            @Schema(description = "Job title")
            String jobTitle,
            @Schema(description = "Location for this role")
            String location
    ) {}
}
