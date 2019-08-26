package com.pojo;

import java.io.Serializable;

/**
 * <h>ProLocation</h>
 * Created by Ali on 2/19/2018.
 */

public class ProLocation implements Serializable
{
    double longitude,latitude;

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
