package com.curiodesk.scraperbackend.api.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Hybrid LinkedIn profile response merged from parsed and HTML data")
public record HybridLinkedInProfileResponse(
        @Schema(description = "Original LinkedIn URL that was scraped")
        String profileUrl,
        @Schema(description = "LinkedIn vanity URL")
        String vanityUrl,
        @Schema(description = "Profile full name")
        String fullName,
        @Schema(description = "Profile headline")
        String headline,
        @Schema(description = "Profile description/about")
        String description,
        @Schema(description = "Current position")
        CurrentPosition currentPosition,
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
        @ArraySchema(arraySchema = @Schema(description = "Recent articles"))
        List<Article> articles,
        @ArraySchema(arraySchema = @Schema(description = "Work experiences"))
        List<Experience> experiences,
        @ArraySchema(arraySchema = @Schema(description = "Education records"))
        List<Education> education
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
    @Schema(description = "LinkedIn article")
    public record Article(
            @Schema(description = "Article title")
            String title,
            @Schema(description = "Article URL")
            String url,
            @Schema(description = "Article publication date")
            String publishedDate,
            @Schema(description = "Article image URL")
            String image,
            @Schema(description = "Article like count")
            Integer likes
    ) {}

    @Builder
    @Schema(description = "LinkedIn work experience")
    public record Experience(
            @Schema(description = "Company name")
            String companyName,
            @Schema(description = "Company URL")
            String companyUrl,
            @Schema(description = "Experience description")
            String description,
            @Schema(description = "Job title")
            String jobTitle,
            @Schema(description = "Location for this role")
            String location
    ) {}

    @Builder
    @Schema(description = "LinkedIn education")
    public record Education(
            @Schema(description = "Institution name")
            String institution,
            @Schema(description = "Institution URL")
            String institutionUrl,
            @Schema(description = "Start date")
            String startDate,
            @Schema(description = "End date")
            String endDate
    ) {}

    @Builder
    @Schema(description = "Current position details")
    public record CurrentPosition(
            @Schema(description = "Current company name")
            String companyName,
            @Schema(description = "Current company LinkedIn URL")
            String companyLinkedinUrl,
            @Schema(description = "Current company logo URL")
            String companyLogoUrl,
            @Schema(description = "Current role title")
            String jobTitle
    ) {}
}
