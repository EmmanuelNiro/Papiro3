package com.example.papiro3.model;

public class Model {
    public class Book {
        private String title;
        private String author;
        private String coverUrl;
        private String isbn;
        private String publishYear;
        private String key; // Open Library key

        public Book() {
        }

        public Book(String title, String author, String coverUrl, String isbn, String publishYear, String key) {
            this.title = title;
            this.author = author;
            this.coverUrl = coverUrl;
            this.isbn = isbn;
            this.publishYear = publishYear;
            this.key = key;
        }

        // Getters
        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getPublishYear() {
            return publishYear;
        }

        public String getKey() {
            return key;
        }

        // Setters
        public void setTitle(String title) {
            this.title = title;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public void setPublishYear(String publishYear) {
            this.publishYear = publishYear;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
