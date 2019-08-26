package com.pojo;

import java.io.Serializable;

/**
 * <h>MyBookingPojo</h>
 * Created by Ali on 2/12/2018.
 */

public class MyBookingPojo implements Serializable
{
    private String message;
    private MyBookingDataPojo data;

    public String getMessage() {
        return message;
    }

    public MyBookingDataPojo getData() {
        return data;
    }
}
