package com.curiodesk.scrapperbackend.api.response;

import lombok.Builder;

import java.util.List;

@Builder
public record LinkedInProfile(
        String profileUrl,
        String vanityUrl,
        String fullName,
        String headline,
        String location,
        String countryCode,
        Integer followerCount,
        String connectionCount,
        String profilePhotoUrl,
        String coverImageUrl,
        List<String> languages,
        List<Post> posts
) {
    @Builder
    public record Post(
            String datePublished,
            Integer likeCount,
            String postUrl,
            String text
    ) {
    }
}
