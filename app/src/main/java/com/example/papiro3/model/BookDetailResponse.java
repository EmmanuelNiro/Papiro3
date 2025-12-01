package com.example.papiro3.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookDetailResponse {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private Object description;

    @SerializedName("authors")
    private List<AuthorInfo> authors;

    @SerializedName("subjects")
    private List<String> subjects;

    @SerializedName("subject_places")
    private List<String> subjectPlaces;

    @SerializedName("subject_times")
    private List<String> subjectTimes;

    @SerializedName("first_publish_date")
    private String firstPublishDate;

    @SerializedName("covers")
    private List<Integer> covers;

    @SerializedName("number_of_pages")
    private Integer numberOfPages;

    @SerializedName("isbn_10")
    private List<String> isbn10;

    @SerializedName("isbn_13")
    private List<String> isbn13;

    @SerializedName("publish_date")
    private String publishDate;

    @SerializedName("publishers")
    private List<String> publishers;

    @SerializedName("publish_places")
    private List<String> publishPlaces;

    @SerializedName("languages")
    private List<LanguageInfo> languages;

    // Clase interna para información de autor
    public static class AuthorInfo {
        @SerializedName("author")
        private AuthorKey author;

        @SerializedName("type")
        private TypeInfo type;

        public AuthorKey getAuthor() {
            return author;
        }

        public static class AuthorKey {
            @SerializedName("key")
            private String key;

            public String getKey() {
                return key;
            }
        }

        public static class TypeInfo {
            @SerializedName("key")
            private String key;

            public String getKey() {
                return key;
            }
        }
    }

    // Clase interna para información de idioma
    public static class LanguageInfo {
        @SerializedName("key")
        private String key;

        public String getKey() {
            return key;
        }
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        if (description == null) {
            return null;
        }
        // La descripción puede venir como String o como objeto con campo "value"
        if (description instanceof String) {
            return (String) description;
        }
        return null;
    }

    public List<AuthorInfo> getAuthors() {
        return authors;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public List<String> getSubjectPlaces() {
        return subjectPlaces;
    }

    public List<String> getSubjectTimes() {
        return subjectTimes;
    }

    public String getFirstPublishDate() {
        return firstPublishDate;
    }

    public List<Integer> getCovers() {
        return covers;
    }

    public String getCoverUrl() {
        if (covers != null && !covers.isEmpty()) {
            return "https://covers.openlibrary.org/b/id/" + covers.get(0) + "-L.jpg";
        }
        return null;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public List<String> getIsbn10() {
        return isbn10;
    }

    public List<String> getIsbn13() {
        return isbn13;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public List<String> getPublishPlaces() {
        return publishPlaces;
    }

    public List<LanguageInfo> getLanguages() {
        return languages;
    }

    public String getLanguageString() {
        if (languages != null && !languages.isEmpty()) {
            String langKey = languages.get(0).getKey();
            // Convertir /languages/eng a "Inglés"
            if (langKey.contains("eng")) return "Inglés";
            if (langKey.contains("spa")) return "Español";
            if (langKey.contains("fre")) return "Francés";
            if (langKey.contains("ger")) return "Alemán";
            return langKey;
        }
        return "Desconocido";
    }
}