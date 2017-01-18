package com.example.faizrehman.geofence_broadcast;

/**
 * Created by faizrehman on 1/13/17.
 */

public class Model {
    private double Longitude;
    private double Latitude;
    private float Radius;
    private String fenceKey;
    private String Place;
    private String date;

    public Model() {
    }


    public Model(double longitude, double latitude, float radius, String fenceKey, String place) {
        Longitude = longitude;
        Latitude = latitude;
        Radius = radius;
        this.fenceKey = fenceKey;
        Place = place;
    }

    public Model(double longitude, double latitude, String place, String date) {
        Longitude = longitude;
        Latitude = latitude;
        Place = place;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public float getRadius() {
        return Radius;
    }

    public void setRadius(float radius) {
        Radius = radius;
    }

    public String getFenceKey() {
        return fenceKey;
    }

    public void setFenceKey(String fenceKey) {
        this.fenceKey = fenceKey;
    }
}
