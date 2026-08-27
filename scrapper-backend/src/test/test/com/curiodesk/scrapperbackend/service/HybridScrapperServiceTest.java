package com.curiodesk.scrapperbackend.service;

import com.curiodesk.scrapperbackend.api.response.LinkedInProfileV1;
import com.curiodesk.scrapperbackend.api.response.LinkedInProfileV2;
import com.curiodesk.scrapperbackend.api.response.LinkedInProfileV3;
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

        LinkedInProfileV1 parsedProfile = LinkedInProfileV1.builder()
                .profileUrl(url)
                .fullName("Parsed Name")
                .headline("Parsed Headline")
                .location("Bengaluru, India")
                .countryCode("IN")
                .profilePhotoUrl("https://images.example/parsed-profile.jpg")
                .coverImageUrl("https://images.example/parsed-cover.jpg")
                .posts(List.of(
                        LinkedInProfileV1.Post.builder()
                                .text("Parsed post")
                                .postUrl("https://linkedin.com/posts/1")
                                .datePublished("2026-08-01")
                                .likeCount(12)
                                .build()
                ))
                .experiences(List.of())
                .languages(List.of("English"))
                .build();

        LinkedInProfileV2 htmlProfile = new LinkedInProfileV2();
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

        LinkedInProfileV2.Experience htmlExperience = new LinkedInProfileV2.Experience();
        htmlExperience.setCompany("Contoso Technologies");
        htmlProfile.setExperience(List.of(htmlExperience));

        LinkedInProfileV2.Education htmlEducation = new LinkedInProfileV2.Education();
        htmlEducation.setInstitution("Stanford University");
        htmlProfile.setEducation(List.of(htmlEducation));

        when(zenRowsClient.fetchProfile(url)).thenReturn(zenRowsResponse);
        when(parsedJsonScraperService.scrapeFromParsedField(url, zenRowsResponse)).thenReturn(parsedProfile);
        when(htmlScraperService.scrapeHtml("<html>profile</html>")).thenReturn(htmlProfile);

        LinkedInProfileV3 result = service.scrape(url);

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

        LinkedInProfileV1 parsedProfile = LinkedInProfileV1.builder()
                .profileUrl(url)
                .experiences(List.of(
                        LinkedInProfileV1.Experience.builder()
                                .companyName("Parsed Fallback Company")
                                .build()
                ))
                .posts(List.of())
                .languages(List.of())
                .build();

        LinkedInProfileV2 htmlProfile = new LinkedInProfileV2();

        LinkedInProfileV2.Experience swappedEducation = new LinkedInProfileV2.Experience();
        swappedEducation.setCompany("Massachusetts Institute of Technology");
        htmlProfile.setExperience(List.of(swappedEducation));

        LinkedInProfileV2.Education swappedExperience = new LinkedInProfileV2.Education();
        swappedExperience.setInstitution("Contoso Technologies");
        htmlProfile.setEducation(List.of(swappedExperience));

        when(zenRowsClient.fetchProfile(url)).thenReturn(zenRowsResponse);
        when(parsedJsonScraperService.scrapeFromParsedField(url, zenRowsResponse)).thenReturn(parsedProfile);
        when(htmlScraperService.scrapeHtml("<html>profile</html>")).thenReturn(htmlProfile);

        LinkedInProfileV3 result = service.scrape(url);

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

    private LinkedInProfileV2.Post createHtmlPost(
            String text,
            String url,
            String publishedDate,
            Long likes
    ) {
        LinkedInProfileV2.Post post = new LinkedInProfileV2.Post();
        post.setText(text);
        post.setUrl(url);
        post.setPublishedDate(publishedDate);
        post.setLikes(likes);
        return post;
    }

    private LinkedInProfileV2.Article createHtmlArticle(
            String title,
            String url,
            String publishedDate,
            String image,
            Long likes
    ) {
        LinkedInProfileV2.Article article = new LinkedInProfileV2.Article();
        article.setTitle(title);
        article.setUrl(url);
        article.setPublishedDate(publishedDate);
        article.setImage(image);
        article.setLikes(likes);
        return article;
    }
}
