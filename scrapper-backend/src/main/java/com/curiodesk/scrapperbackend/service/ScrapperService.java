package com.curiodesk.scrapperbackend.service;

import com.curiodesk.scrapperbackend.api.response.LinkedInProfile;
import com.curiodesk.scrapperbackend.api.response.ZenRowsResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScrapperService {

    private final ZenRowsClient zenRowsClient;

    public ScrapperService(ZenRowsClient zenRowsClient) {
        this.zenRowsClient = zenRowsClient;
    }

    public LinkedInProfile scrape(String url) {

        ZenRowsResponse response = zenRowsClient.fetchProfile(url);

        return mapToProfile(url, response);
    }

    private LinkedInProfile mapToProfile(String url, ZenRowsResponse response) {

        ZenRowsResponse.Parsed parsed = response.parsed();

        ZenRowsResponse.Member member = parsed.member();

        return LinkedInProfile.builder()
                .profileUrl(url)
                .vanityUrl(member.vanityUrl())
                .fullName(member.fullName())
                .countryCode(member.countryCode())
                .followerCount(member.followerCount())
                .location(member.location())
                .connectionCount(member.connectionCountLabel())
                .profilePhotoUrl(member.profilePhotoUrl())
                .languages(parsed.languages())
                .posts(mapPosts(parsed.posts()))
                .build();
    }

    private List<LinkedInProfile.Post> mapPosts(
            List<ZenRowsResponse.Post> posts
    ) {
        if (posts == null) {
            return List.of();
        }

        return posts.stream()
                .map(post -> LinkedInProfile.Post
                        .builder()
                        .text(post.text())
                        .datePublished(post.datePublished())
                        .likeCount(post.likeCount())
                        .postUrl(post.postUrl())
                        .build())
                .toList();
    }

}
