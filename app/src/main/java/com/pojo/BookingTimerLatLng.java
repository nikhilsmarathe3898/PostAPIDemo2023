package com.pojo;

/**
 * <h>BookingTimerLatLng</h>
 * Created by Ali on 2/19/2018.
 */

public class BookingTimerLatLng
{
    private BookingTimer bookingTimer;

   // private ProLocation providerLocation;

    private String jobTimerStatus;

    private int status;

    public String getJobTimerStatus() {
        return jobTimerStatus;
    }

    public void setJobTimerStatus(String jobTimerStatus) {
        this.jobTimerStatus = jobTimerStatus;
    }

    public BookingTimer getBookingTimer() {
        return bookingTimer;
    }

    public void setBookingTimer(BookingTimer bookingTimer) {
        this.bookingTimer = bookingTimer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /*public ProLocation getProviderLocation() {
        return providerLocation;
    }

    public void setProviderLocation(ProLocation providerLocation) {
        this.providerLocation = providerLocation;
    }*/
}
