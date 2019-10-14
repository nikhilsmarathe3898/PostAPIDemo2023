package com.pojo;

/**
 * Created by murashid on 23-Oct-17.
 */

public class Slot {
    private String bookingId;
    private String customerId;
    private String slotHour;
    private String slotPeriod;
    private String slotEndHourBooking;
    private String slotEndPeriodBooking;
    private String bookedStartHour;
    private String bookedEndHour;
  //  private boolean isBooked;
    private String cutomerName;
    private String event;
    private String startTimeStamp;

    private String status= "1";

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSlotHour() {
        return slotHour;
    }

    public void setSlotHour(String slotHour) {
        this.slotHour = slotHour;
    }

    public String getSlotPeriod() {
        return slotPeriod;
    }

    public void setSlotPeriod(String slotPeriod) {
        this.slotPeriod = slotPeriod;
    }

    public String getSlotEndHourBooking() {
        return slotEndHourBooking;
    }

    public void setSlotEndHourBooking(String slotEndHourBooking) {
        this.slotEndHourBooking = slotEndHourBooking;
    }

    public String getSlotEndPeriodBooking() {
        return slotEndPeriodBooking;
    }

    public void setSlotEndPeriodBooking(String slotEndPeriodBooking) {
        this.slotEndPeriodBooking = slotEndPeriodBooking;
    }

    public String getBookedStartHour() {
        return bookedStartHour;
    }

    public void setBookedStartHour(String bookedStartHour) {
        this.bookedStartHour = bookedStartHour;
    }

    public String getBookedEndHour() {
        return bookedEndHour;
    }

    public void setBookedEndHour(String bookedEndHour) {
        this.bookedEndHour = bookedEndHour;
    }

   /* public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }*/

    public String getCutomerName() {
        return cutomerName;
    }

    public void setCutomerName(String cutomerName) {
        this.cutomerName = cutomerName;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }


    public String getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(String startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

