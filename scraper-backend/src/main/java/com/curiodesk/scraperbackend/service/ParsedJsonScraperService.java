package com.curiodesk.scraperbackend.service;

import com.curiodesk.scraperbackend.api.response.ParsedLinkedInProfileResponse;
import com.curiodesk.scraperbackend.api.response.ZenRowsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ParsedJsonScraperService {

    private final ZenRowsClient zenRowsClient;

    public ParsedJsonScraperService(ZenRowsClient zenRowsClient) {
        this.zenRowsClient = zenRowsClient;
    }

    public ParsedLinkedInProfileResponse scrape(String url) {
        log.info("Scraping LinkedIn profile for URL: {}", url);
        ZenRowsResponse response = zenRowsClient.fetchProfile(url);

        return scrapeFromParsedField(url, response);
    }

    ParsedLinkedInProfileResponse scrapeFromParsedField(String url, ZenRowsResponse response) {

        ZenRowsResponse.Parsed parsed = response != null
                ? response.parsed()
                : null;

        ZenRowsResponse.Member member = parsed != null
                ? parsed.member()
                : null;
        ZenRowsResponse.CurrentPosition currentPosition = parsed != null
                ? parsed.currentPosition()
                : null;

        return ParsedLinkedInProfileResponse.builder()
                .profileUrl(url)
                .vanityUrl(member != null ? member.vanityUrl() : null)
                .fullName(member != null ? member.fullName() : null)
                .headline(currentPosition != null ? currentPosition.jobTitle() : null)
                .currentPosition(mapCurrentPosition(currentPosition))
                .countryCode(member != null ? member.countryCode() : null)
                .followerCount(member != null ? member.followerCount() : null)
                .location(member != null ? member.location() : null)
                .connectionCount(member != null ? member.connectionCountLabel() : null)
                .profilePhotoUrl(member != null ? member.profilePhotoUrl() : null)
                .coverImageUrl(member != null ? member.coverImageUrl() : null)
                .languages(parsed != null && parsed.languages() != null ? parsed.languages() : List.of())
                .experiences(mapExperiences(parsed != null ? parsed.experience() : null))
                .posts(mapPosts(parsed != null ? parsed.posts() : null))
                .build();
    }

    private ParsedLinkedInProfileResponse.CurrentPosition mapCurrentPosition(
            ZenRowsResponse.CurrentPosition currentPosition
    ) {
        if (currentPosition == null) {
            return null;
        }

        return ParsedLinkedInProfileResponse.CurrentPosition.builder()
                .companyName(currentPosition.companyName())
                .companyLinkedinUrl(currentPosition.companyLinkedinUrl())
                .companyLogoUrl(currentPosition.companyLogoUrl())
                .jobTitle(currentPosition.jobTitle())
                .build();
    }

    private List<ParsedLinkedInProfileResponse.Experience> mapExperiences(List<ZenRowsResponse.Experience> experience) {
        if (experience == null) {
            return List.of();
        }

        return experience.stream()
                .map(exp -> ParsedLinkedInProfileResponse.Experience
                        .builder()
                        .companyName(exp.companyName())
                        .description(exp.description())
                        .jobTitle(exp.jobTitle())
                        .location(exp.location())
                        .build())
                .toList();
    }

    private List<ParsedLinkedInProfileResponse.Post> mapPosts(
            List<ZenRowsResponse.Post> posts
    ) {
        if (posts == null) {
            return List.of();
        }

        return posts.stream()
                .map(post -> ParsedLinkedInProfileResponse.Post
                        .builder()
                        .text(post.text())
                        .datePublished(post.datePublished())
                        .likeCount(post.likeCount())
                        .postUrl(post.postUrl())
                        .build())
                .toList();
    }

}
