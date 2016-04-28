package com.example.mashfique.mapdemo;

/**
 * Created by Ragnarok on 4/14/2016.
 */
public class Alarm {

    private String alarmName;
    private String busStop;
    private String atTime;
    private String beforeTime;
    private String frequency;
    private boolean[] days;

    public Alarm() {
        days = new boolean[7];
        for (int i = 0; i < days.length; i++) {
            days[i] = false;
        }
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public void setBusStop(String busStop) {
        this.busStop = busStop;
    }

    public void setAtTime(String atTime) {
        this.atTime = atTime;
    }

    public void setBeforeTime(String beforeTime) {
        this.beforeTime = beforeTime;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String toString() {
        String alarm = "";
        alarm = busStop + " - " + getShortenDays() + " - " + atTime + ", " + beforeTime + " min before";
        return alarm;
    }

    private String getShortenDays() {
        String shortenDays = "";
        for (int i = 0; i < days.length; i++) {
            if (days[i]) {
                switch (i) {
                    case 0:
                        shortenDays+="Su";
                        break;
                    case 1:
                        shortenDays+="M";
                        break;
                    case 2:
                        shortenDays+="Tu";
                        break;
                    case 3:
                        shortenDays+="W";
                        break;
                    case 4:
                        shortenDays+="Th";
                        break;
                    case 5:
                        shortenDays+="F";
                        break;
                    case 6:
                        shortenDays+="Sa";
                        break;
                }
            }
        }
        return shortenDays;
    }

    public void addDay(int i) {
        days[i] = true;
    }
}
