package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <h>MyBookingDataPojo</h>
 * Created by Ali on 2/12/2018.
 */

public class MyBookingDataPojo implements Serializable {

    private ArrayList<AllBookingEventPojo> pending;
    private ArrayList<AllBookingEventPojo> past;
    private ArrayList<AllBookingEventPojo> upcoming;

    public ArrayList<AllBookingEventPojo> getPending() {
        return pending;
    }

    public ArrayList<AllBookingEventPojo> getPast() {
        return past;
    }

    public ArrayList<AllBookingEventPojo> getUpcoming() {
        return upcoming;
    }
}
