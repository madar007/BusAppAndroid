package com.example.mashfique.mapdemo;

/**
 * Created by Mashfique on 4/2/2016.
 */
/* This class will represent a Bus */
public class Bus {
    private String id = "";         // Each bus has a unique vehicle id
    private double angle = 0;       // angle- will be used for rotating buses.
    private double lat = 0;
    private double lon = 0;

    public Bus(String id, double angle, double lat, double lon) {
        this.id = id;
        this.angle = angle;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public double getAngle() {
        return angle;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }
}
