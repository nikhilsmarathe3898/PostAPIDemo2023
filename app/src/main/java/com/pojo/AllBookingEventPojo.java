package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <h>AllBookingEventPojo</h>
 * Created by Ali on 2/12/2018.
 */

public class AllBookingEventPojo implements Serializable
{


    /*"bookingId":1518508016667,
"bookingRequestedFor":1518508016,
"bookingRequestedAt":1518508016,
"bookingEndtime":"",
"bookingExpireTime":1518508076,
"currencySymbol":"$",
"currency":"USD",
"distanceMatrix":1,
"serverTime":1518509549,
"bookingType":1,
"distance":9.22,
"status":1,
"statusMsg":"Booking Requested",
"firstName":"Noah",
"lastName":"Lawrence",
"profilePic":"https://s3.amazonaws.com/livemapplication/Provider/ProfilePics/1514352731486_0_01.png",
"phone":"",
"proLocation":{
"longitude":77.6179,
"latitude":12.8984
},
"averageRating":0,
"addLine1":"Creative Villa Apartment, 44 RBI Colony, Vishveshvaraiah Nagar, Ganga Nagar, Bengaluru, Karnataka 560024, India",
"addLine2":"",
"city":"",
"state":"",
"country":"",
"placeId":"",
"pincode":"",
"latitude":13.0286146,
"longitude":77.5893902,
"typeofEvent":"",
"gigTime":[
],
"category":"Artist",
"paymentMethod":"cash",
"totalAmount":0,
"jobDescription":""
"cart" :""*/

   // private long bookingEndtime;
    private int distanceMatrix,bookingType;
    private float averageRating;
    private String currency,lastName,
            category,jobDescription;


    private String state, addLine2, addLine1, statusMsg, country, city,currencySymbol,cancellationReason;

    //  private ProLocation proLocation;

    private String pincode, placeId, profilePic,phone;

    private double distance,totalAmount;

    private long bookingExpireTime, serverTime, bookingId, bookingRequestedFor,bookingRequestedAt;
    private int status,bookingModel,callType;
    private String longitude, latitude, firstName, paymentMethod;

    //private TypeofEvent typeofEvent;
    private ReviewByCustomer reviewByCustomer ;
    private BookingAccounting accounting;
    private CartInfo cart;
    private ArrayList<AdditionalService> additionalService;
    private ArrayList<HelpReason>helpReasons;
    private String reminderId;
    private boolean needApproveBycustomer;


    private ArrayList<BidDispatchLog>bidDispatchLog;

    public ArrayList<BidDispatchLog> getBidDispatchLog() {
        return bidDispatchLog;
    }

    public void setBidDispatchLog(ArrayList<BidDispatchLog> bidDispatchLog) {
        this.bidDispatchLog = bidDispatchLog;
    }

    public String getReminderId() {
        return reminderId;
    }


    public boolean isNeedApproveBycustomer() {
        return needApproveBycustomer;
    }

    public void setNeedApproveBycustomer(boolean needApproveBycustomer) {
        this.needApproveBycustomer = needApproveBycustomer;
    }

    public ArrayList<AdditionalService> getAdditionalService() {
        return additionalService;
    }


    public ArrayList<HelpReason> getHelpReasons() {
        return helpReasons;
    }

    public int getBookingModel() {
        return bookingModel;
    }

    public int getCallType() {
        return callType;
    }

/*
    public long getBookingEndtime() {
        return bookingEndtime;
    }
*/

    public String getCurrency() {
        return currency;
    }

    public CartInfo getCart() {
        return cart;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDistanceMatrix() {
        return distanceMatrix;
    }

    public int getBookingType() {
        return bookingType;
    }

    public String getCategory() {
        return category;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public ReviewByCustomer getReviewByCustomer() {
        return reviewByCustomer;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public BookingAccounting getAccounting() {
        return accounting;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public ReviewByCustomer getReviewByProvider() {
        return reviewByCustomer;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

/*
    public TypeofEvent getTypeofEvent() {
        return typeofEvent;
    }
*/


    public long getBookingRequestedAt() {
        return bookingRequestedAt;
    }

    public long getBookingRequestedFor() {
        return bookingRequestedFor;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public String getAddLine2() {
        return addLine2;
    }

    public String getAddLine1() {
        return addLine1;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public long getBookingExpireTime() {
        return bookingExpireTime;
    }

    public long getServerTime() {
        return serverTime;
    }

    public String getPincode() {
        return pincode;
    }

    public double getDistance() {
        return distance;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public class TypeofEvent implements Serializable {
        /*"_id":"59cc98e4b05549255330ae12",
"name":"Birthday Party",
"selectImage":"https://s3.amazonaws.com/iserve/8988260408472.png",
"unselectImage":"https://s3.amazonaws.com/iserve/4937064970285.png",
"status":1*/

        private String _id, name;

        public String get_id() {
            return _id;
        }

        public String getName() {
            return name;
        }
    }


    public class ReviewByCustomer implements Serializable
    {
        /*   "rating" : 4,
                "review" : "xfhhvyff",
                "userId" : ObjectId("5a50e8b3b121331d381cd93d"),
       "reviewAt" : 1515409348*/

        private float rating;
        public float getRating() {
            return rating;
        }
    }

}
