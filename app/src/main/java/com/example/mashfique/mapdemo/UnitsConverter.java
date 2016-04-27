package com.example.mashfique.mapdemo;

import android.content.res.Resources;

/**
 * Created by Ragnarok on 4/26/2016.
 */
public class UnitsConverter {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
