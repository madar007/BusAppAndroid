package com.example.mashfique.mapdemo;

import java.io.Serializable;

/**
 * Created by Ragnarok on 4/14/2016.
 */
public class Favorite implements Serializable{

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
        String favorite = "";
        favorite.concat(favoriteName);
        return favorite;
    }

}
