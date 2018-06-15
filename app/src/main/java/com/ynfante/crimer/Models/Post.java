package com.ynfante.crimer.Models;

public class Post {

    private String userId;
    private String imageUrl;
    private String title;
    private String content;

    private User user;

    public Post() {

    }

    public Post(String userId, String imageUrl, String title, String content, User user) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        String postString = title ;

        if(user != null) {
            postString += " - " + user.getPhotoUrl();
        }

        return postString;
    }
}
