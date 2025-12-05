package com.example.papiro3.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookDetailResponse {
    @SerializedName("title")
    private String title;

    @SerializedName("authors")
    private List<AuthorName> authors;

    @SerializedName("description")
    private String description;

    @SerializedName("publishers")
    private List<String> publishers;

    @SerializedName("publish_date")
    private String publish_date;

    @SerializedName("number_of_pages")
    private int number_of_pages;

    @SerializedName("subjects")
    private List<String> subjects;

    @SerializedName("rating")
    private double rating;

    @SerializedName("ratingCount")
    private int ratingCount;

    @SerializedName("cover")
    private Cover cover;

    // Getters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AuthorName> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorName> authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public int getNumber_of_pages() {
        return number_of_pages;
    }

    public void setNumber_of_pages(int number_of_pages) {
        this.number_of_pages = number_of_pages;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    // Clase interna para Cover
    public static class Cover {
        @SerializedName("small")
        private String small;

        @SerializedName("medium")
        private String medium;

        @SerializedName("large")
        private String large;

        public String getSmall() {
            return small;
        }

        public String getMedium() {
            return medium;
        }

        public String getLarge() {
            return large;
        }
    }
}
