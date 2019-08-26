package com.pojo.address_pojo;

import java.io.Serializable;

/**
 * <h>ProviderLocation</h>
 * Created by Ali on 10/4/2017.
 */

public class ProviderLocation implements Serializable {
    private double latitude, longitude;
    private double lat,lng;

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
