package com.josephmpo.myapplication.models;

public class Review {
    ReviewAuthor reviewAuthor;
    String author, content, created_at, updated_at, id, url;

    public Review(ReviewAuthor reviewAuthor, String author, String content, String created_at, String updated_at, String id, String url) {
        this.reviewAuthor = reviewAuthor;
        this.author = author;
        this.content = content;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.id = id;
        this.url = url;
    }

    public ReviewAuthor getReviewAuthor() {
        return reviewAuthor;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
