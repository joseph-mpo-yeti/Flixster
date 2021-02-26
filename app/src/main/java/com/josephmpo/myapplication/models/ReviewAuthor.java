package com.josephmpo.myapplication.models;

public class ReviewAuthor {
    private String name, username, avatarPath;
    float rating;

    public ReviewAuthor(String name, String username, String avatarPath, float rating) {
        this.name = name;
        this.username = username;
        this.avatarPath = avatarPath;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public float getRating() {
        return rating;
    }
}
