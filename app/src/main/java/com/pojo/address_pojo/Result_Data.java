package com.pojo.address_pojo;

/**
 * <h>Result_Data</h>
 * Created by user on 6/12/2017.
 */

public class Result_Data
{
    private String formatted_address,name;
    private GoemetryCordinate geometry;

    public String getFormatted_address() {
        return formatted_address;
    }

    public String getName() {
        return name;
    }

    public GoemetryCordinate getGeometry() {
        return geometry;
    }
}
