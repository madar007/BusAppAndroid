package com.example.mashfique.mapdemo;

import android.app.Application;
import android.widget.ArrayAdapter;

/**
 * Created by Sarin on 4/30/2016 AD.
 */
class GlobalStore extends Application {

    private String mGlobalVarValue;
    private ArrayAdapter<String> mFavoritesAdapter;

    public ArrayAdapter<String> getmFavoritesAdapter(){
        return mFavoritesAdapter;
    }

    public void setmFavoritesAdapter(ArrayAdapter<String> mFavoritesAdapter){
        this.mFavoritesAdapter = mFavoritesAdapter;
    }

    public String getGlobalVarValue() {
        return mGlobalVarValue;
    }

    public void setGlobalVarValue(String str) {
        mGlobalVarValue = str;
    }
}
