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

    public static String militaryTo12Hour(int hour, int minutes) {
        int t_hour;
        int t_min = minutes;
        String period;

        if (hour < 12) {
            period = "AM";
            if (hour == 0) {
                t_hour = 12;
            } else {
                t_hour = hour;
            }
        } else {
            period = "PM";
            if (hour == 12) {
                t_hour = 12;
            } else {
                t_hour = hour - 12;
            }
        }

        if (minutes < 10) {
            return t_hour + ":" + "0" + t_min + period;
        } else {
            return t_hour + ":" + t_min + period;
        }
    }
}
