package com.pojo.address_pojo;

import java.io.Serializable;

/**
 * <h>DbSavedAddress</h>
 * Created by Ali on 1/9/2018.
 */

public class DbSavedAddress implements Serializable
{
    private String address_name,address_formate;
    private int key_id;
    private double address_lat,address_lng;

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getAddress_formate() {
        return address_formate;
    }

    public void setAddress_formate(String address_formate) {
        this.address_formate = address_formate;
    }

    public int getKey_id() {
        return key_id;
    }

    public void setKey_id(int key_id) {
        this.key_id = key_id;
    }

    public double getAddress_lat() {
        return address_lat;
    }

    public void setAddress_lat(double address_lat) {
        this.address_lat = address_lat;
    }

    public double getAddress_lng() {
        return address_lng;
    }

    public void setAddress_lng(double address_lng) {
        this.address_lng = address_lng;
    }
}
