package com.example.papiro3.model;

import com.google.gson.annotations.SerializedName;

public class AuthorName {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
