package com.pojo;

/**
 * <h>BookingTimer</h>
 * Created by Ali on 2/20/2018.
 */

public class BookingTimer
{
    /*"status":1,"second":0,"startTimeStamp":1519034597*/

    private int status;
    private long second,startTimeStamp;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getSecond() {
        return second;
    }

    public void setSecond(long second) {
        this.second = second;
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }
}
