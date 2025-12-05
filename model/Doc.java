package com.example.papiro3.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Doc {

    @SerializedName("key")
    private String key;

    @SerializedName("title")
    private String title;

    @SerializedName("author_name")
    private List<String> authorName;

    @SerializedName("first_publish_year")
    private Integer firstPublishYear;

    @SerializedName("cover_i")
    private Long coverId;

    @SerializedName("isbn")
    private List<String> isbn;

    @SerializedName("edition_count")
    private Integer editionCount;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthorName() {
        return authorName;
    }

    public void setAuthorName(List<String> authorName) {
        this.authorName = authorName;
    }

    public Integer getFirstPublishYear() {
        return firstPublishYear;
    }

    public void setFirstPublishYear(Integer firstPublishYear) {
        this.firstPublishYear = firstPublishYear;
    }

    public Long getCoverId() {
        return coverId;
    }

    public void setCoverId(Long coverId) {
        this.coverId = coverId;
    }

    public List<String> getIsbn() {
        return isbn;
    }

    public void setIsbn(List<String> isbn) {
        this.isbn = isbn;
    }

    public Integer getEditionCount() {
        return editionCount;
    }

    public void setEditionCount(Integer editionCount) {
        this.editionCount = editionCount;
    }

    // Método auxiliar para obtener el autor principal
    public String getAuthor() {
        if ( authorName != null && !authorName.isEmpty() ) {
            return authorName.get(0);
        }
        return "Autor desconocido";
    }

    // Método auxiliar para obtener la URL de la portada
    public String getCoverUrl() {
        if ( coverId != null ) {
            return "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";
        }
        return null;
    }
}