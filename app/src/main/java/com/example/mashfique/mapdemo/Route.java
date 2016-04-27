package com.example.mashfique.mapdemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ragnarok on 4/26/2016.
 */
public class Route {

    private List<Step> listOfSteps;
    private String startAddress;
    private String endAddress;
    private String arrival_time;
    private String departure_time;
    private String distance;
    private String duration;

    public Route() {
        listOfSteps = new ArrayList<>();
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public void addAllSteps(List<Step> steps) {
        listOfSteps.addAll(steps);
    }

    public List<Step> getListOfSteps() {
        return listOfSteps;
    }

    public void setListOfSteps(List<Step> listOfSteps) {
        this.listOfSteps = listOfSteps;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

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

    public String toString() {
        String routeSummary = "";
        routeSummary = routeSummary + "ROUTE SUMMARY" + "\n";
        routeSummary = routeSummary + "STARTING LOCATION - " + startAddress + "\n";
        routeSummary = routeSummary + "ENDING LOCATION - " + endAddress + "\n";
        routeSummary = routeSummary + "DEPART AT - " + departure_time + "\n";
        routeSummary = routeSummary + "ARRIVE AT - " + arrival_time + "\n";
        routeSummary = routeSummary + "DISTANCE - " + distance + "\n";
        routeSummary = routeSummary + "DURATION - " + duration;
        return routeSummary;
    }
}
