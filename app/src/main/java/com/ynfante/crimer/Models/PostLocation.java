package com.ynfante.crimer.Models;

public class PostLocation {

    private String place;
    private Double longitude;
    private Double latitude;

    public PostLocation() {

    }

    public PostLocation(String place, Double longitude, Double latitude) {
        this.place = place;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
