package com.curiodesk.scrapperbackend.api.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HtmlLinkedInProfileResponse {

    private String name;
    private String profileUrl;
    private String description;
    private String location;
    private String profileImage;
    private String coverImage;
    private Long followers;

    private List<Experience> experience = new ArrayList<>();
    private List<Education> education = new ArrayList<>();
    private List<Article> articles = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();
    private List<String> links = new ArrayList<>();

    @Data
    public static class Experience {
        private String company;
        private String companyUrl;
        private String location;
    }

    @Data
    public static class Education {
        private String institution;
        private String institutionUrl;
        private String startDate;
        private String endDate;
    }

    @Data
    public static class Article {
        private String title;
        private String url;
        private String publishedDate;
        private String image;
        private Long likes;
    }

    @Data
    public static class Post {
        private String text;
        private String url;
        private String publishedDate;
        private Long likes;
    }
}