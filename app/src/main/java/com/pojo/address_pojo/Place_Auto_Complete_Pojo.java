package com.pojo.address_pojo;

/**
 * <h>Place_Auto_Complete_Pojo</h>
 * Created by embed on 27/3/17.
 */

public class Place_Auto_Complete_Pojo
{
    private String address;
    private String ref_key;
    private String lat,lng;
    private String place_id;

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRef_key() {
        return ref_key;
    }

    public void setRef_key(String ref_key) {
        this.ref_key = ref_key;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
