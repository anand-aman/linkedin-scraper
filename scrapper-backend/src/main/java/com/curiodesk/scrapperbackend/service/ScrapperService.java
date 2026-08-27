package com.curiodesk.scrapperbackend.service;

import com.curiodesk.scrapperbackend.api.response.LinkedInProfileV1;
import com.curiodesk.scrapperbackend.api.response.ZenRowsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ScrapperService {

    private final ZenRowsClient zenRowsClient;

    public ScrapperService(ZenRowsClient zenRowsClient) {
        this.zenRowsClient = zenRowsClient;
    }

    public LinkedInProfileV1 scrape(String url) {
        log.info("Scraping LinkedIn profile for URL: {}", url);
        ZenRowsResponse response = zenRowsClient.fetchProfile(url);

        return mapToProfile(url, response);
    }

    private LinkedInProfileV1 mapToProfile(String url, ZenRowsResponse response) {

        ZenRowsResponse.Parsed parsed = response.parsed();

        ZenRowsResponse.Member member = parsed.member();

        return LinkedInProfileV1.builder()
                .profileUrl(url)
                .vanityUrl(member.vanityUrl())
                .fullName(member.fullName())
                .headline(parsed.currentPosition() != null ? parsed.currentPosition().jobTitle() : null)
                .currentPosition(mapCurrentPosition(parsed.currentPosition()))
                .countryCode(member.countryCode())
                .followerCount(member.followerCount())
                .location(member.location())
                .connectionCount(member.connectionCountLabel())
                .profilePhotoUrl(member.profilePhotoUrl())
                .coverImageUrl(member.coverImageUrl())
                .languages(parsed.languages())
                .experiences(mapExperiences(parsed.experience()))
                .posts(mapPosts(parsed.posts()))
                .build();
    }

    private LinkedInProfileV1.CurrentPosition mapCurrentPosition(
            ZenRowsResponse.CurrentPosition currentPosition
    ) {
        if (currentPosition == null) {
            return null;
        }

        return LinkedInProfileV1.CurrentPosition.builder()
                .companyName(currentPosition.companyName())
                .companyLinkedinUrl(currentPosition.companyLinkedinUrl())
                .companyLogoUrl(currentPosition.companyLogoUrl())
                .jobTitle(currentPosition.jobTitle())
                .build();
    }

    private List<LinkedInProfileV1.Experience> mapExperiences(List<ZenRowsResponse.Experience> experience) {
        if (experience == null) {
            return List.of();
        }

        return experience.stream()
                .map(exp -> LinkedInProfileV1.Experience
                        .builder()
                        .companyName(exp.companyName())
                        .description(exp.description())
                        .jobTitle(exp.jobTitle())
                        .location(exp.location())
                        .build())
                .toList();
    }

    private List<LinkedInProfileV1.Post> mapPosts(
            List<ZenRowsResponse.Post> posts
    ) {
        if (posts == null) {
            return List.of();
        }

        return posts.stream()
                .map(post -> LinkedInProfileV1.Post
                        .builder()
                        .text(post.text())
                        .datePublished(post.datePublished())
                        .likeCount(post.likeCount())
                        .postUrl(post.postUrl())
                        .build())
                .toList();
    }

}
