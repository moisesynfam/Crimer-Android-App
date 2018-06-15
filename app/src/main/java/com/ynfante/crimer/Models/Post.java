package com.ynfante.crimer.Models;

import java.io.Serializable;

public class Post implements Serializable{

    private String userId;
    private String imageUrl;
    private String title;
    private String content;

    private User user;
    private PostLocation location;

    public Post() {

    }


    public Post(String userId, String imageUrl, String title, String content, User user, PostLocation location) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.user = user;
        this.location = location;
    }

    public PostLocation getLocation() {
        return location;
    }

    public void setLocation(PostLocation location) {
        this.location = location;
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
