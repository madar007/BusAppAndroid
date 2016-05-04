package com.example.mashfique.mapdemo;

/**
 * Created by Ragnarok on 4/14/2016.
 */
public class Favorite {

    private String favoriteName;

    public Favorite(String favoriteName) {
        this.favoriteName = favoriteName;
    }

    public Favorite() {
        new Favorite("");
    }

    public void setFavoriteName(String favoriteName) {
        this.favoriteName = favoriteName;
    }

    public String getFavoriteName() {
        return favoriteName;
    }

    public String toString() {
        final String DELIMITER = " - ";
        String alarm = "";
        alarm.concat(favoriteName);
        return alarm;
    }

}
