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
    private String[] days;

    public Alarm(String alarmName, String busStop, String atTime, String beforeTime, String frequency, String[] days) {
        this.alarmName = alarmName;
        this.busStop = busStop;
        this.atTime = atTime;
        this.beforeTime = beforeTime;
        this.frequency = frequency;
        this.days = days;
    }

    public Alarm() {
        this("","","","","",new String[0]);
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

    public void setDays(String[] days) {
        this.days = days;
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

    public String getBeforeTime() {
        return beforeTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public String[] getDays() {
        return days;
    }

    public String toString() {
        final String DELIMITER = " - ";
        String alarm = "";
        alarm.concat(alarmName)
                .concat(DELIMITER)
                .concat(frequency)
                .concat("\n")
                .concat(busStop)
                .concat(DELIMITER)
                .concat(getShortenDays())
                .concat(DELIMITER)
                .concat(atTime)
                .concat(DELIMITER)
                .concat(beforeTime);
        return alarm;
    }

    private String getShortenDays() {
        String shortenDays = "";
        for (String  day : days) {
            switch (day) {
                case "Sun":
                    shortenDays+="Su";
                    break;
                case "Mon":
                    shortenDays+="M";
                    break;
                case "Tue":
                    shortenDays+="Tu";
                    break;
                case "Wed":
                    shortenDays+="W";
                    break;
                case "Thu":
                    shortenDays+="Th";
                    break;
                case "Fri":
                    shortenDays+="F";
                    break;
                case "Sat":
                    shortenDays+="Sa";
                    break;
                default:
                    break;
            }
        }
        return shortenDays;
    }
}
