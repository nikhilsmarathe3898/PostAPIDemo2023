package com.localgenie.model;

import java.io.Serializable;

/**
 * Created by Ali on 9/26/2018.
 */
public class Offers implements Serializable
{
    /*"title":"test",
"discountType":2,
"value":10,
"shiftBooking":3*/

    private int discountType,value,minShiftBooking,maxShiftBooking;
    private String title,offerDescription ;


    public String getOfferDescription() {
        return offerDescription;
    }

    public int getDiscountType() {
        return discountType;
    }

    public int getValue() {
        return value;
    }

    public int getMinShiftBooking() {
        return minShiftBooking;
    }

    public int getMaxShiftBooking() {
        return maxShiftBooking;
    }

    public String getTitle() {
        return title;
    }
}
