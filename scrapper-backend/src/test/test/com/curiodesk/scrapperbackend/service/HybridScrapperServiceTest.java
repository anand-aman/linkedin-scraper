package com.curiodesk.scrapperbackend.service;

import com.curiodesk.scrapperbackend.api.response.ParsedLinkedInProfileResponse;
import com.curiodesk.scrapperbackend.api.response.HtmlLinkedInProfileResponse;
import com.curiodesk.scrapperbackend.api.response.HybridLinkedInProfileResponse;
import com.curiodesk.scrapperbackend.api.response.ZenRowsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HybridScrapperServiceTest {

    @Mock
    private ZenRowsClient zenRowsClient;

    @Mock
    private ParsedJsonScraperService parsedJsonScraperService;

    @Mock
    private HtmlScraperService htmlScraperService;

    private HybridScrapperService service;

    @BeforeEach
    void setUp() {
        service = new HybridScrapperService(
                zenRowsClient,
                parsedJsonScraperService,
                htmlScraperService
        );
    }

    @Test
    void shouldPreferParsedFieldsAndFallbackToHtml() {
        String url = "https://www.linkedin.com/in/example/";
        ZenRowsResponse zenRowsResponse = new ZenRowsResponse("<html>profile</html>", null);

        ParsedLinkedInProfileResponse parsedProfile = ParsedLinkedInProfileResponse.builder()
                .profileUrl(url)
                .fullName("Parsed Name")
                .headline("Parsed Headline")
                .location("Bengaluru, India")
                .countryCode("IN")
                .profilePhotoUrl("https://images.example/parsed-profile.jpg")
                .coverImageUrl("https://images.example/parsed-cover.jpg")
                .posts(List.of(
                        ParsedLinkedInProfileResponse.Post.builder()
                                .text("Parsed post")
                                .postUrl("https://linkedin.com/posts/1")
                                .datePublished("2026-08-01")
                                .likeCount(12)
                                .build()
                ))
                .experiences(List.of())
                .languages(List.of("English"))
                .build();

        HtmlLinkedInProfileResponse htmlProfile = new HtmlLinkedInProfileResponse();
        htmlProfile.setName("HTML Name");
        htmlProfile.setDescription("HTML description");
        htmlProfile.setLocation("Pune, India");
        htmlProfile.setFollowers(321L);
        htmlProfile.setProfileImage("https://images.example/html-profile.jpg");
        htmlProfile.setCoverImage("https://images.example/html-cover.jpg");
        htmlProfile.setPosts(List.of(
                createHtmlPost(
                        "HTML post",
                        "https://linkedin.com/posts/html-1",
                        "2026-08-02",
                        99L
                )
        ));
        htmlProfile.setArticles(List.of(
                createHtmlArticle(
                        "HTML article",
                        "https://linkedin.com/pulse/html-article-1",
                        "2026-08-03",
                        "https://images.example/article.jpg",
                        33L
                )
        ));

        HtmlLinkedInProfileResponse.Experience htmlExperience = new HtmlLinkedInProfileResponse.Experience();
        htmlExperience.setCompany("Contoso Technologies");
        htmlProfile.setExperience(List.of(htmlExperience));

        HtmlLinkedInProfileResponse.Education htmlEducation = new HtmlLinkedInProfileResponse.Education();
        htmlEducation.setInstitution("Stanford University");
        htmlProfile.setEducation(List.of(htmlEducation));

        when(zenRowsClient.fetchProfile(url)).thenReturn(zenRowsResponse);
        when(parsedJsonScraperService.scrapeFromParsedField(url, zenRowsResponse)).thenReturn(parsedProfile);
        when(htmlScraperService.scrapeHtml("<html>profile</html>")).thenReturn(htmlProfile);

        HybridLinkedInProfileResponse result = service.scrape(url);

        assertEquals("Parsed Name", result.fullName());
        assertEquals("Bengaluru, India", result.location());
        assertEquals("IN", result.countryCode());
        assertEquals(321, result.followerCount());
        assertEquals("https://images.example/parsed-profile.jpg", result.profilePhotoUrl());
        assertEquals("https://images.example/parsed-cover.jpg", result.coverImageUrl());
        assertEquals("HTML description", result.description());
        assertEquals(1, result.posts().size());
        assertEquals("HTML post", result.posts().get(0).text());
        assertEquals(1, result.articles().size());
        assertEquals("HTML article", result.articles().get(0).title());
        assertEquals(1, result.experiences().size());
        assertEquals("Contoso Technologies", result.experiences().get(0).companyName());
        assertEquals(1, result.education().size());
        assertEquals("Stanford University", result.education().get(0).institution());
    }

    @Test
    void shouldFixSwappedEducationAndExperienceFromHtml() {
        String url = "https://www.linkedin.com/in/example/";
        ZenRowsResponse zenRowsResponse = new ZenRowsResponse("<html>profile</html>", null);

        ParsedLinkedInProfileResponse parsedProfile = ParsedLinkedInProfileResponse.builder()
                .profileUrl(url)
                .experiences(List.of(
                        ParsedLinkedInProfileResponse.Experience.builder()
                                .companyName("Parsed Fallback Company")
                                .build()
                ))
                .posts(List.of())
                .languages(List.of())
                .build();

        HtmlLinkedInProfileResponse htmlProfile = new HtmlLinkedInProfileResponse();

        HtmlLinkedInProfileResponse.Experience swappedEducation = new HtmlLinkedInProfileResponse.Experience();
        swappedEducation.setCompany("Massachusetts Institute of Technology");
        htmlProfile.setExperience(List.of(swappedEducation));

        HtmlLinkedInProfileResponse.Education swappedExperience = new HtmlLinkedInProfileResponse.Education();
        swappedExperience.setInstitution("Contoso Technologies");
        htmlProfile.setEducation(List.of(swappedExperience));

        when(zenRowsClient.fetchProfile(url)).thenReturn(zenRowsResponse);
        when(parsedJsonScraperService.scrapeFromParsedField(url, zenRowsResponse)).thenReturn(parsedProfile);
        when(htmlScraperService.scrapeHtml("<html>profile</html>")).thenReturn(htmlProfile);

        HybridLinkedInProfileResponse result = service.scrape(url);

        assertEquals(1, result.education().size());
        assertEquals(
                "Massachusetts Institute of Technology",
                result.education().get(0).institution()
        );

        assertEquals(1, result.experiences().size());
        assertEquals("Contoso Technologies", result.experiences().get(0).companyName());
        assertTrue(
                result.experiences().stream()
                        .noneMatch(exp -> "Parsed Fallback Company".equals(exp.companyName()))
        );
    }

    private HtmlLinkedInProfileResponse.Post createHtmlPost(
            String text,
            String url,
            String publishedDate,
            Long likes
    ) {
        HtmlLinkedInProfileResponse.Post post = new HtmlLinkedInProfileResponse.Post();
        post.setText(text);
        post.setUrl(url);
        post.setPublishedDate(publishedDate);
        post.setLikes(likes);
        return post;
    }

    private HtmlLinkedInProfileResponse.Article createHtmlArticle(
            String title,
            String url,
            String publishedDate,
            String image,
            Long likes
    ) {
        HtmlLinkedInProfileResponse.Article article = new HtmlLinkedInProfileResponse.Article();
        article.setTitle(title);
        article.setUrl(url);
        article.setPublishedDate(publishedDate);
        article.setImage(image);
        article.setLikes(likes);
        return article;
    }
}
