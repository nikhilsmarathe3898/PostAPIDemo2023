package com.pojo;

import java.util.ArrayList;

public class Schedule {
    private String providerId;
   // private String scheduleId;
    //private String long;
    private String lat;
    private String address;
    private String startTime;
    private String endTime;
    private ArrayList<Booked> booked;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

   /* public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }
*/
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStartTime() {
        return startTime;
    }

/*
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
*/

    public String getEndTime() {
        return endTime;
    }

/*
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
*/

    public ArrayList<Booked> getBooked() {
        return booked;
    }

/*
    public void setBooked(ArrayList<Booked> booked) {
        this.booked = booked;
    }
*/
}
