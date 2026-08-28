package com.curiodesk.scraperbackend.service;

import com.curiodesk.scraperbackend.api.response.HtmlLinkedInProfileResponse;
import com.curiodesk.scraperbackend.api.response.ZenRowsResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class HtmlScraperService {

    private final ObjectMapper objectMapper;
    private final ZenRowsClient zenRowsClient;

    public HtmlScraperService(ObjectMapper objectMapper, ZenRowsClient zenRowsClient) {
        this.objectMapper = objectMapper;
        this.zenRowsClient = zenRowsClient;
    }

    public HtmlLinkedInProfileResponse scrapeProfileFromHtml(String url) {

        ZenRowsResponse response = zenRowsClient.fetchProfile(url);

        String html = response.html();

        if (html == null || html.isBlank()) {
            throw new IllegalArgumentException("html field is missing");
        }

        return scrapeHtml(html);
    }

    public HtmlLinkedInProfileResponse scrapeHtml(String html) {

        Document document = Jsoup.parse(html);

        HtmlLinkedInProfileResponse profile = new HtmlLinkedInProfileResponse();

        extractBasicProfile(document, profile);
        extractImages(document, profile);
        extractJsonLd(document, profile);
        extractLinks(document, profile);

        return profile;
    }

    private void extractBasicProfile(
            Document document,
            HtmlLinkedInProfileResponse profile) {

        // Name
        Element name = document.selectFirst(
                "h1.top-card-layout__title"
        );

        if (name != null) {
            profile.setName(name.text());
        }

        // Canonical profile URL
        Element canonical = document.selectFirst(
                "link[rel=canonical]"
        );

        if (canonical != null) {
            profile.setProfileUrl(
                    canonical.attr("abs:href")
            );
        }

        // Description
        Element description = document.selectFirst(
                "meta[name=description]"
        );

        if (description != null) {
            profile.setDescription(
                    description.attr("content")
            );
        }

        // Location can also be obtained from JSON-LD.
    }

    private void extractImages(
            Document document,
            HtmlLinkedInProfileResponse profile) {

        // Profile image
        Element profileImage = document.selectFirst(
                "img.top-card__profile-image"
        );

        if (profileImage != null) {
            profile.setProfileImage(
                    profileImage.attr("abs:src")
            );
        }

        // Cover image
        Element coverImage = document.selectFirst(
                "img.cover-img__image"
        );

        if (coverImage != null) {
            profile.setCoverImage(
                    coverImage.attr("abs:src")
            );
        }
    }

    private void extractJsonLd(
            Document document,
            HtmlLinkedInProfileResponse profile) {

        for (Element script :
                document.select("script[type=application/ld+json]")) {

            String json = script.html();

            if (json.isBlank()) {
                continue;
            }

            JsonNode root;

            try {
                root = objectMapper.readTree(json);
            } catch (Exception e) {
                // Ignore malformed/non-standard JSON-LD blocks
                continue;
            }

            processJsonLdNode(root, profile);
        }
    }

    private void processJsonLdNode(
            JsonNode node,
            HtmlLinkedInProfileResponse profile) {

        if (node == null || node.isNull()) {
            return;
        }

        // Handle @graph
        if (node.has("@graph")) {

            for (JsonNode item : node.get("@graph")) {
                processJsonLdNode(item, profile);
            }

            return;
        }

        String type = node.path("@type").asText("");

        switch (type) {

            case "Person" ->
                    extractPerson(node, profile);

            case "Article" ->
                    extractArticle(node, profile);

            case "DiscussionForumPosting" ->
                    extractPost(node, profile);

            default -> {
                // Ignore other JSON-LD types
            }
        }
    }

    private void extractPerson(
            JsonNode person,
            HtmlLinkedInProfileResponse profile) {

        if (profile.getName() == null) {
            profile.setName(
                    text(person, "name")
            );
        }

        if (profile.getProfileUrl() == null) {
            profile.setProfileUrl(
                    text(person, "url")
            );
        }

        profile.setDescription(
                firstNonBlank(
                        profile.getDescription(),
                        text(person, "description")
                )
        );

        // Image
        JsonNode image = person.path("image");

        if (image.isObject()) {

            String imageUrl = firstNonBlank(
                    text(image, "contentUrl"),
                    text(image, "url")
            );

            profile.setProfileImage(
                    firstNonBlank(
                            profile.getProfileImage(),
                            imageUrl
                    )
            );
        }

        // Location
        JsonNode address = person.path("address");

        if (address.isObject()) {

            String locality = text(
                    address,
                    "addressLocality"
            );

            String country = text(
                    address,
                    "addressCountry"
            );

            if (locality != null || country != null) {

                profile.setLocation(
                        join(locality, country)
                );
            }
        }

        // Followers
        JsonNode interaction =
                person.path("interactionStatistic");

        if (interaction.isObject()) {

            String interactionType =
                    text(interaction, "interactionType");

            if (interactionType != null &&
                    interactionType.contains("FollowAction")) {

                profile.setFollowers(
                        longValue(
                                interaction,
                                "userInteractionCount"
                        )
                );
            }
        }

        // Education
        JsonNode alumni =
                person.path("alumniOf");

        if (alumni.isArray()) {

            for (JsonNode educationNode : alumni) {

                HtmlLinkedInProfileResponse.Education education =
                        new HtmlLinkedInProfileResponse.Education();

                education.setInstitution(
                        text(educationNode, "name")
                );

                education.setInstitutionUrl(
                        text(educationNode, "url")
                );

                JsonNode member =
                        educationNode.path("member");

                education.setStartDate(
                        text(member, "startDate")
                );

                education.setEndDate(
                        text(member, "endDate")
                );

                profile.getEducation().add(
                        education
                );
            }
        }

        // Employment
        JsonNode worksFor =
                person.path("worksFor");

        if (worksFor.isArray()) {

            for (JsonNode companyNode : worksFor) {

                HtmlLinkedInProfileResponse.Experience experience =
                        new HtmlLinkedInProfileResponse.Experience();

                experience.setCompany(
                        text(companyNode, "name")
                );

                experience.setCompanyUrl(
                        text(companyNode, "url")
                );

                experience.setLocation(
                        text(companyNode, "location")
                );

                profile.getExperience().add(
                        experience
                );
            }
        }
    }

    private void extractArticle(
            JsonNode article,
            HtmlLinkedInProfileResponse profile) {

        HtmlLinkedInProfileResponse.Article result =
                new HtmlLinkedInProfileResponse.Article();

        result.setTitle(
                text(article, "headline")
        );

        result.setUrl(
                text(article, "url")
        );

        result.setPublishedDate(
                text(article, "datePublished")
        );

        JsonNode image =
                article.path("image");

        if (image.isObject()) {
            result.setImage(
                    firstNonBlank(
                            text(image, "url"),
                            text(image, "contentUrl")
                    )
            );
        } else if (image.isTextual()) {
            result.setImage(image.asText());
        }

        JsonNode interaction =
                article.path("interactionStatistic");

        if (interaction.isObject()) {

            result.setLikes(
                    longValue(
                            interaction,
                            "userInteractionCount"
                    )
            );
        }

        profile.getArticles().add(result);
    }

    private void extractPost(
            JsonNode post,
            HtmlLinkedInProfileResponse profile) {

        HtmlLinkedInProfileResponse.Post result =
                new HtmlLinkedInProfileResponse.Post();

        result.setText(
                text(post, "text")
        );

        result.setUrl(
                text(post, "url")
        );

        result.setPublishedDate(
                text(post, "datePublished")
        );

        JsonNode interaction =
                post.path("interactionStatistic");

        if (interaction.isObject()) {

            result.setLikes(
                    longValue(
                            interaction,
                            "userInteractionCount"
                    )
            );
        }

        profile.getPosts().add(result);
    }

    private void extractLinks(
            Document document,
            HtmlLinkedInProfileResponse profile) {

        for (Element link :
                document.select("a[href]")) {

            String url = link.attr("abs:href");

            if (url != null &&
                    !url.isBlank() &&
                    !profile.getLinks().contains(url)) {

                profile.getLinks().add(url);
            }
        }
    }

    private String text(
            JsonNode node,
            String field) {

        JsonNode value = node.path(field);

        if (value.isMissingNode() ||
                value.isNull()) {

            return null;
        }

        String result = value.asText();

        return result.isBlank() ? null : result;
    }

    private Long longValue(
            JsonNode node,
            String field) {

        JsonNode value = node.path(field);

        if (value.isNumber()) {
            return value.asLong();
        }

        if (value.isTextual()) {

            try {
                return Long.parseLong(
                        value.asText()
                );
            } catch (NumberFormatException ignored) {
            }
        }

        return null;
    }

    private String firstNonBlank(
            String first,
            String second) {

        if (first != null &&
                !first.isBlank()) {

            return first;
        }

        return second;
    }

    private String join(
            String first,
            String second) {

        if (first == null) {
            return second;
        }

        if (second == null) {
            return first;
        }

        return first + ", " + second;
    }
}