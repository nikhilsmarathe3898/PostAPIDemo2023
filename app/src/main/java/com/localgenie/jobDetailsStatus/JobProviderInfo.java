package com.localgenie.jobDetailsStatus;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;

import com.pojo.BidDispatchLog;
import com.pojo.BookingAccounting;
import com.pojo.BookingTimer;
import com.pojo.CartInfo;

import java.util.ArrayList;
import java.util.Date;

/**
 * <h>JobProviderInfo</h>
 * Created by Ali on 2/16/2018.
 */

public interface JobProviderInfo
{
    void providerInfo(String providerId, String name, String proPic, int reviewCount, float rating, double amount, String currencySymbol, int status);
    /*void providerTimer(BookingDetailsPojo.BookingTimer bookingTimer);
    void providerLatLng(LatLng latLng);*/
    void proExpiry(String name, String proPic, long expiryTime, long serverTime, LatLng customerLatLng, LatLng proLatLng);

    void onJobTimer(BookingTimer bookingTimer, long serverTime, String statusMsg, long bid, int status, long totalJobTime);

    void onBidJobInfoDetails(ArrayList<BidDispatchLog> bidDispatchLog, int statusCode);

    void onJobInfo(int bookingType, BookingAccounting accounting, CartInfo cart, String categoryName);
    void onJobAddress(String address, Date date);

    //void onJobBiddingFrag(BookingTimer bookingTimer, long serverTime, String statusMsg, long bid, int status);
    interface JobBidCalling
    {
        void onIntentCall(Intent intent);
    }


}
