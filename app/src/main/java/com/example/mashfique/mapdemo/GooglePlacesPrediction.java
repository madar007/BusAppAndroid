package com.example.mashfique.mapdemo;

/**
 * Created by Ragnarok on 4/24/2016.
 */
public class GooglePlacesPrediction {

    private String fullDescription;
    private String placeID;
    private String buildingName;
    private String street;
    private String city;
    private String stateAbrrev;
    private String country;

    public GooglePlacesPrediction(String fullDescription, String placeID, String buildingName, String street, String city, String stateAbrrev, String country) {
        this.fullDescription = fullDescription;
        this.placeID = placeID;
        this.buildingName = buildingName;
        this.street = street;
        this.city = city;
        this.stateAbrrev = stateAbrrev;
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public String getStateAbrrev() {
        return stateAbrrev;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getPlaceID() {
        return placeID;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public String toString() {
        String strValue = "";
        strValue = strValue.concat(buildingName);
        strValue = strValue.concat("\n");
        strValue = strValue.concat(" ").concat(street);
        strValue = strValue.concat(", ").concat(city);
        return strValue;
    }

}
