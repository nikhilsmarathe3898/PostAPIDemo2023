package com.pojo;

import java.io.Serializable;

/**
 * <j>LiveTackPojo</j>
 * Created by Ali on 11/10/2017.
 */

public class LiveTackPojo implements Serializable {
    /*{"batteryPercentage":"-100.000000",
    "appVersion":"1.0",
    "longitude":77.589576,
    "status":"1",
    "locationHeading":"0.000000",
    "latitude":13.028637,
    "pid":"59e5ee0c348a5441f36066cf"}*/

    private String status, pid;  //appVersion, locationHeading

    private double latitude, longitude;

/*
    public String getAppVersion() {
        return appVersion;
    }
*/

    public double getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }

/*
    public String getLocationHeading() {
        return locationHeading;
    }
*/

    public double getLatitude() {
        return latitude;
    }

    public String getPid() {
        return pid;
    }
}
