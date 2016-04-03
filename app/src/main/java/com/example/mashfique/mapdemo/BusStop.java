package com.example.mashfique.mapdemo;

/**
 * Created by Mashfique on 4/2/2016.
 */
public class BusStop {

    private String tag = "";
    private String title = "";
    private String shortTitle = "";
    private double lat = 0;
    private double lon = 0;

    public BusStop(String tag,String title, String shortTitle, double lat, double lon) {
        this.tag = tag;
        this.title = title;
        this.shortTitle = shortTitle;
        this.lat = lat;
        this.lon = lon;
    }

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortTitle() {
        return shortTitle;
    }
    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
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
