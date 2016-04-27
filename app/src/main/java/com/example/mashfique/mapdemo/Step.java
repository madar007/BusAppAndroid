package com.example.mashfique.mapdemo;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Ragnarok on 4/24/2016.
 */
public class Step {

    String distance;
    String duration;
    String instructions;
    String travelMode;
    PolylineOptions polylineOptions;
    LatLng startLocation;
    LatLng endLocation;

    LatLng transit_arrival_latlng;
    String transit_arrival_name;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public void setPolylineOptions(PolylineOptions polylineOptions) {
        this.polylineOptions = polylineOptions;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public LatLng getTransit_arrival_latlng() {
        return transit_arrival_latlng;
    }

    public void setTransit_arrival_latlng(LatLng transit_arrival_latlng) {
        this.transit_arrival_latlng = transit_arrival_latlng;
    }

    public String getTransit_arrival_name() {
        return transit_arrival_name;
    }

    public void setTransit_arrival_name(String transit_arrival_name) {
        this.transit_arrival_name = transit_arrival_name;
    }

    public String toString() {
        switch (travelMode) {
            case "WALKING":
                return instructions + " - " + distance;
            case "TRANSIT":
                return instructions + " and get off at " + transit_arrival_name;
            default:
                return "Head into the danger zone!";
        }
    }
}
