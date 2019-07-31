package com.pojo;

import java.io.Serializable;

/**
 * Created by Ali on 5/28/2018.
 */
public class BookingChatHistory implements Serializable
{
    /*"bookingId":1526714427837,
"providerId":"5ae080d4fd702c20b2bee360",
"firstName":"abhi",
"lastName":"Test",
"profilePic":"https://s3.us-east-2.amazonaws.com/notaryapp/Provider/ProfilePics/Profile1524662289996.png",
"bookingRequestedFor":1526714427,
"bookingRequestedAt":1526714428,
"status":10,
"catName":"Tutor"
"amount":279.53,
"lastCahtMsgTimeStamp":0,
"currency":"INR",
"currencySymbol":"₹",*/
    private long bookingId,bookingRequestedFor,bookingRequestedAt,lastCahtMsgTimeStamp;
    private int status,callType;
    private double amount;
    private String providerId,firstName,lastName,profilePic,catName,currencySymbol;

    public long getLastCahtMsgTimeStamp() {
        return lastCahtMsgTimeStamp;
    }

    public double getAmount() {
        return amount;
    }

    public int getCallType() {
        return callType;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public long getBookingId() {
        return bookingId;
    }

    public long getBookingRequestedFor() {
        return bookingRequestedFor;
    }

    public long getBookingRequestedAt() {
        return bookingRequestedAt;
    }

    public String getCatName() {
        return catName;
    }

    public int getStatus() {
        return status;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfilePic() {
        return profilePic;
    }
}
