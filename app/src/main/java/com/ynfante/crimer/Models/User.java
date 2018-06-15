package com.ynfante.crimer.Models;

import java.io.Serializable;

public class User implements Serializable {

    private String username;
    private String name;
    private String photoUrl;
    private String photoThumbnailUrl;


    public User() {
    }

    public User(String username, String name, String photoUrl, String photoThumbnailUrl) {
        this.username = username;
        this.name = name;
        this.photoUrl = photoUrl;
        this.photoThumbnailUrl = photoThumbnailUrl;

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoThumbnailUrl() {
        return photoThumbnailUrl;
    }

    public void setPhotoThumbnailUrl(String photoThumbnailUrl) {
        this.photoThumbnailUrl = photoThumbnailUrl;
    }

}
