package com.example.papiro3.model;

public class Category {
    private String name;
    private String query;
    private boolean isSelected;

    public Category(String query, String name) {
        this.query = query;
        this.name = name;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
