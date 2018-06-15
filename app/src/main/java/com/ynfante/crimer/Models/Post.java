package com.ynfante.crimer.Models;

public class Post {

    private String userId;
    private String imageUrl;
    private String title;
    private String content;

    public Post() {

    }

    public Post(String userId, String imageUrl, String title, String content) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}