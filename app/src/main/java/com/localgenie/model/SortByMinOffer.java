package com.localgenie.model;

import java.util.Comparator;

/**
 * Created by Ali on 10/5/2018.
 */
public class SortByMinOffer implements Comparator<Offers> {
    @Override
    public int compare(Offers offers, Offers t1) {
        return offers.getMinShiftBooking()-t1.getMinShiftBooking();
    }
}
