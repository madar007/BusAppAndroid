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
    private int atHour;
    private int atMin;
    private boolean[] days;
    private boolean onOff;

    public Alarm() {
        alarmName = "";
        busStop = "";
        atTime = "";
        beforeTime = "";
        frequency = "";
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

    public void setAtHour(int atHour) {
        this.atHour = atHour;
    }

    public void setAtMin(int atMin) {
        this.atMin = atMin;
    }

    public void setBeforeTime(String beforeTime) {
        this.beforeTime = beforeTime;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public String getBusStop() {
        return busStop;
    }

    public String getAtTime() {
        return atTime;
    }

    public int getAtHour() {
        return atHour;
    }

    public int getAtMin() {
        return atMin;
    }

    public String getBeforeTime() {
        return beforeTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public boolean[] getDays() {
        return days;
    }

    public String toString() {
        String alarm = "";
        alarm = busStop + " - " + getShortenDays() + " - " + atTime + ", " + beforeTime +
                " min before" + " - ";

        if (isOn()) {
            alarm += "On";
        } else {
            alarm += "Off";
        }
        return alarm;
    }

    public void addDay(int i) {
        days[i] = true;
    }

    public void removeDay(int i) {
        days[i] = false;
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

    public void turnOn() {
        onOff = true;
    }

    public void turnOff() {
        onOff = false;
    }

    public boolean isOn() {
        return onOff;
    }
}
