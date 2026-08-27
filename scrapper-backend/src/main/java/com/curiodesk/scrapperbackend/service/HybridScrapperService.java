package com.curiodesk.scrapperbackend.service;

import com.curiodesk.scrapperbackend.api.response.ParsedLinkedInProfileResponse;
import com.curiodesk.scrapperbackend.api.response.HtmlLinkedInProfileResponse;
import com.curiodesk.scrapperbackend.api.response.HybridLinkedInProfileResponse;
import com.curiodesk.scrapperbackend.api.response.ZenRowsResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class HybridScrapperService {

    private static final Set<String> EDUCATION_HINTS = Set.of(
            "university", "college", "school", "institute", "academy",
            "polytechnic", "faculty", "campus", "bachelor", "master", "phd", "degree"
    );

    private static final Set<String> COMPANY_HINTS = Set.of(
            "inc", "llc", "ltd", "corp", "co.", "technologies", "technology",
            "solutions", "systems", "software", "labs", "consulting", "pvt"
    );

    private final ZenRowsClient zenRowsClient;
    private final ParsedJsonScraperService parsedJsonScraperService;
    private final HtmlScraperService htmlScraperService;

    public HybridScrapperService(
            ZenRowsClient zenRowsClient,
            ParsedJsonScraperService parsedJsonScraperService,
            HtmlScraperService htmlScraperService
    ) {
        this.zenRowsClient = zenRowsClient;
        this.parsedJsonScraperService = parsedJsonScraperService;
        this.htmlScraperService = htmlScraperService;
    }

    public HybridLinkedInProfileResponse scrape(String url) {
        ZenRowsResponse response = zenRowsClient.fetchProfile(url);

        ParsedLinkedInProfileResponse parsedProfile = parsedJsonScraperService.scrapeFromParsedField(
                url,
                response
        );

        HtmlLinkedInProfileResponse htmlProfile = parseHtmlProfile(response);

        List<HybridLinkedInProfileResponse.Experience> mergedExperience = mergeExperience(
                parsedProfile.experiences(),
                htmlProfile.getExperience(),
                htmlProfile.getEducation()
        );

        List<HybridLinkedInProfileResponse.Education> mergedEducation = mergeEducation(
                htmlProfile.getEducation(),
                htmlProfile.getExperience()
        );

        return HybridLinkedInProfileResponse.builder()
                .profileUrl(firstNonBlank(
                        parsedProfile.profileUrl(),
                        firstNonBlank(htmlProfile.getProfileUrl(), url)
                ))
                .vanityUrl(parsedProfile.vanityUrl())
                .fullName(firstNonBlank(parsedProfile.fullName(), htmlProfile.getName()))
                .headline(parsedProfile.headline())
                .description(firstNonBlank(htmlProfile.getDescription(), parsedProfile.headline()))
                .currentPosition(mapCurrentPosition(parsedProfile.currentPosition()))
                .location(firstNonBlank(parsedProfile.location(), htmlProfile.getLocation()))
                .countryCode(parsedProfile.countryCode())
                .followerCount(firstNonNull(parsedProfile.followerCount(), toInteger(htmlProfile.getFollowers())))
                .connectionCount(parsedProfile.connectionCount())
                .profilePhotoUrl(firstNonBlank(parsedProfile.profilePhotoUrl(), htmlProfile.getProfileImage()))
                .coverImageUrl(firstNonBlank(parsedProfile.coverImageUrl(), htmlProfile.getCoverImage()))
                .languages(parsedProfile.languages() != null ? parsedProfile.languages() : List.of())
                .posts(mergePosts(parsedProfile.posts(), htmlProfile.getPosts()))
                .articles(mergeArticles(htmlProfile.getArticles()))
                .experiences(mergedExperience)
                .education(mergedEducation)
                .build();
    }

    private HtmlLinkedInProfileResponse parseHtmlProfile(ZenRowsResponse response) {
        String html = response != null ? response.html() : null;

        if (html == null || html.isBlank()) {
            return new HtmlLinkedInProfileResponse();
        }

        return htmlScraperService.scrapeHtml(html);
    }

    private HybridLinkedInProfileResponse.CurrentPosition mapCurrentPosition(
            ParsedLinkedInProfileResponse.CurrentPosition currentPosition
    ) {
        if (currentPosition == null) {
            return null;
        }

        return HybridLinkedInProfileResponse.CurrentPosition.builder()
                .companyName(currentPosition.companyName())
                .companyLinkedinUrl(currentPosition.companyLinkedinUrl())
                .companyLogoUrl(currentPosition.companyLogoUrl())
                .jobTitle(currentPosition.jobTitle())
                .build();
    }

    private List<HybridLinkedInProfileResponse.Post> mergePosts(
            List<ParsedLinkedInProfileResponse.Post> parsedPosts,
            List<HtmlLinkedInProfileResponse.Post> htmlPosts
    ) {
        List<HybridLinkedInProfileResponse.Post> normalizedHtml = mapHtmlPosts(htmlPosts);

        if (!normalizedHtml.isEmpty()) {
            return dedupePosts(normalizedHtml);
        }

        List<HybridLinkedInProfileResponse.Post> normalizedParsed = mapParsedPosts(parsedPosts);

        if (!normalizedParsed.isEmpty()) {
            return dedupePosts(normalizedParsed);
        }

        return List.of();
    }

    private List<HybridLinkedInProfileResponse.Post> mapParsedPosts(List<ParsedLinkedInProfileResponse.Post> posts) {
        if (posts == null) {
            return List.of();
        }

        return posts.stream()
                .map(post -> HybridLinkedInProfileResponse.Post.builder()
                        .datePublished(post.datePublished())
                        .likeCount(post.likeCount())
                        .postUrl(post.postUrl())
                        .text(post.text())
                        .build())
                .toList();
    }

    private List<HybridLinkedInProfileResponse.Post> mapHtmlPosts(List<HtmlLinkedInProfileResponse.Post> posts) {
        if (posts == null) {
            return List.of();
        }

        return posts.stream()
                .map(post -> HybridLinkedInProfileResponse.Post.builder()
                        .datePublished(post.getPublishedDate())
                        .likeCount(toInteger(post.getLikes()))
                        .postUrl(post.getUrl())
                        .text(post.getText())
                        .build())
                .toList();
    }

    private List<HybridLinkedInProfileResponse.Post> dedupePosts(List<HybridLinkedInProfileResponse.Post> posts) {
        Map<String, HybridLinkedInProfileResponse.Post> deduped = new LinkedHashMap<>();

        for (HybridLinkedInProfileResponse.Post post : posts) {
            String key = firstNonBlank(
                    normalize(post.postUrl()),
                    normalize(post.text())
            );

            if (key == null) {
                continue;
            }

            deduped.putIfAbsent(key, post);
        }

        return new ArrayList<>(deduped.values());
    }

    private List<HybridLinkedInProfileResponse.Article> mergeArticles(List<HtmlLinkedInProfileResponse.Article> htmlArticles) {
        return dedupeArticles(mapHtmlArticles(htmlArticles));
    }

    private List<HybridLinkedInProfileResponse.Article> mapHtmlArticles(List<HtmlLinkedInProfileResponse.Article> articles) {
        if (articles == null) {
            return List.of();
        }

        return articles.stream()
                .map(article -> HybridLinkedInProfileResponse.Article.builder()
                        .title(article.getTitle())
                        .url(article.getUrl())
                        .publishedDate(article.getPublishedDate())
                        .image(article.getImage())
                        .likes(toInteger(article.getLikes()))
                        .build())
                .toList();
    }

    private List<HybridLinkedInProfileResponse.Article> dedupeArticles(List<HybridLinkedInProfileResponse.Article> articles) {
        Map<String, HybridLinkedInProfileResponse.Article> deduped = new LinkedHashMap<>();

        for (HybridLinkedInProfileResponse.Article article : articles) {
            String key = firstNonBlank(
                    normalize(article.url()),
                    normalize(article.title())
            );

            if (key == null) {
                continue;
            }

            deduped.putIfAbsent(key, article);
        }

        return new ArrayList<>(deduped.values());
    }

    private List<HybridLinkedInProfileResponse.Experience> mergeExperience(
            List<ParsedLinkedInProfileResponse.Experience> parsedExperience,
            List<HtmlLinkedInProfileResponse.Experience> htmlExperience,
            List<HtmlLinkedInProfileResponse.Education> htmlEducation
    ) {
        List<HybridLinkedInProfileResponse.Experience> primary = new ArrayList<>();

        if (htmlExperience != null) {
            for (HtmlLinkedInProfileResponse.Experience item : htmlExperience) {
                String company = item.getCompany();

                if (looksLikeEducation(company)) {
                    continue;
                }

                primary.add(HybridLinkedInProfileResponse.Experience.builder()
                        .companyName(company)
                        .companyUrl(item.getCompanyUrl())
                        .location(item.getLocation())
                        .build());
            }
        }

        if (htmlEducation != null) {
            for (HtmlLinkedInProfileResponse.Education item : htmlEducation) {
                String institution = item.getInstitution();

                if (looksLikeEducation(institution)) {
                    continue;
                }

                primary.add(HybridLinkedInProfileResponse.Experience.builder()
                        .companyName(institution)
                        .companyUrl(item.getInstitutionUrl())
                        .build());
            }
        }

        if (!primary.isEmpty()) {
            return dedupeExperience(primary);
        }

        if (parsedExperience == null) {
            return List.of();
        }

        List<HybridLinkedInProfileResponse.Experience> fallback = parsedExperience.stream()
                .map(item -> HybridLinkedInProfileResponse.Experience.builder()
                        .companyName(item.companyName())
                        .description(item.description())
                        .jobTitle(item.jobTitle())
                        .location(item.location())
                        .build())
                .toList();

        return dedupeExperience(fallback);
    }

    private List<HybridLinkedInProfileResponse.Education> mergeEducation(
            List<HtmlLinkedInProfileResponse.Education> htmlEducation,
            List<HtmlLinkedInProfileResponse.Experience> htmlExperience
    ) {
        List<HybridLinkedInProfileResponse.Education> result = new ArrayList<>();

        if (htmlEducation != null) {
            for (HtmlLinkedInProfileResponse.Education item : htmlEducation) {
                if (!looksLikeEducation(item.getInstitution()) && looksLikeCompany(item.getInstitution())) {
                    continue;
                }

                result.add(HybridLinkedInProfileResponse.Education.builder()
                        .institution(item.getInstitution())
                        .institutionUrl(item.getInstitutionUrl())
                        .startDate(item.getStartDate())
                        .endDate(item.getEndDate())
                        .build());
            }
        }

        if (htmlExperience != null) {
            for (HtmlLinkedInProfileResponse.Experience item : htmlExperience) {
                if (!looksLikeEducation(item.getCompany())) {
                    continue;
                }

                result.add(HybridLinkedInProfileResponse.Education.builder()
                        .institution(item.getCompany())
                        .institutionUrl(item.getCompanyUrl())
                        .build());
            }
        }

        return dedupeEducation(result);
    }

    private List<HybridLinkedInProfileResponse.Experience> dedupeExperience(List<HybridLinkedInProfileResponse.Experience> experience) {
        Map<String, HybridLinkedInProfileResponse.Experience> deduped = new LinkedHashMap<>();

        for (HybridLinkedInProfileResponse.Experience item : experience) {
            String key = firstNonBlank(
                    normalize(item.companyName()),
                    normalize(item.companyUrl())
            );

            if (key == null) {
                continue;
            }

            deduped.putIfAbsent(key, item);
        }

        return new ArrayList<>(deduped.values());
    }

    private List<HybridLinkedInProfileResponse.Education> dedupeEducation(List<HybridLinkedInProfileResponse.Education> education) {
        Map<String, HybridLinkedInProfileResponse.Education> deduped = new LinkedHashMap<>();

        for (HybridLinkedInProfileResponse.Education item : education) {
            String key = firstNonBlank(
                    normalize(item.institution()),
                    normalize(item.institutionUrl())
            );

            if (key == null) {
                continue;
            }

            deduped.putIfAbsent(key, item);
        }

        return new ArrayList<>(deduped.values());
    }

    private boolean looksLikeEducation(String value) {
        String normalized = normalize(value);

        if (normalized == null) {
            return false;
        }

        return containsAny(normalized, EDUCATION_HINTS);
    }

    private boolean looksLikeCompany(String value) {
        String normalized = normalize(value);

        if (normalized == null) {
            return false;
        }

        return containsAny(normalized, COMPANY_HINTS);
    }

    private boolean containsAny(String value, Set<String> hints) {
        for (String hint : hints) {
            if (value.contains(hint)) {
                return true;
            }
        }
        return false;
    }

    private Integer toInteger(Long value) {
        if (value == null) {
            return null;
        }

        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return value.intValue();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return normalized.isEmpty() ? null : normalized;
    }

    private <T> T firstNonNull(T first, T second) {
        return first != null ? first : second;
    }

    private String firstNonBlank(String first, String second) {
        String normalizedFirst = normalize(first);
        if (normalizedFirst != null) {
            return first;
        }

        String normalizedSecond = normalize(second);
        if (normalizedSecond != null) {
            return second;
        }

        return null;
    }
}
