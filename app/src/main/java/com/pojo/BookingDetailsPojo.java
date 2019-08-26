package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <h>BookingDetailsPojo</h>
 * Created by Ali on 2/19/2018.
 */

public class BookingDetailsPojo implements Serializable
{
    private BookingDetailsData data;

    public BookingDetailsData getData() {
        return data;
    }

    public class BookingDetailsData
    {
        private int bookingType,status,bookingModel,callType;
        private long bookingId,bookingRequestedFor,bookingRequestedAt,bookingExpireTime,serverTime;
        private String currencySymbol,currency,statusMsg,addLine1,paymentMethod,signURL,cancellationReason
                ,jobDescription,categoryId,categoryName;
        private ProviderDetailsBooking providerDetail;
        private BookingJobStatusLogs jobStatusLogs;
        private BookingTimer bookingTimer;
        private BookingAccounting accounting;
        private double latitude,longitude;
        private String reminderId;
        private ArrayList<BidDispatchLog>bidDispatchLog;
        private ArrayList<BidQuestionAnswer>questionAndAnswer;
        private boolean needApproveBycustomer;
        private CartInfo cart;

        public boolean isNeedApproveBycustomer() {
            return needApproveBycustomer;
        }

        public void setNeedApproveBycustomer(boolean needApproveBycustomer) {
            this.needApproveBycustomer = needApproveBycustomer;
        }

        public ArrayList<BidDispatchLog> getBidDispatchLog() {
            return bidDispatchLog;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public ArrayList<BidQuestionAnswer> getQuestionAndAnswer() {
            return questionAndAnswer;
        }

        public int getBookingModel() {
            return bookingModel;
        }

        public String getReminderId() {
            return reminderId;
        }

        public int getCallType() {
            return callType;
        }

        public CartInfo getCart() {
            return cart;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public int getBookingType() {
            return bookingType;
        }

        public int getStatus() {
            return status;
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

        public long getBookingExpireTime() {
            return bookingExpireTime;
        }

        public long getServerTime() {
            return serverTime;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public String getCurrency() {
            return currency;
        }

        public String getStatusMsg() {
            return statusMsg;
        }

        public String getAddLine1() {
            return addLine1;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public String getSignURL() {
            return signURL;
        }

        public String getCancellationReason() {
            return cancellationReason;
        }

        public String getJobDescription() {
            return jobDescription;
        }

        public ProviderDetailsBooking getProviderDetail() {
            return providerDetail;
        }

        public BookingJobStatusLogs getJobStatusLogs() {
            return jobStatusLogs;
        }

        public BookingTimer getBookingTimer() {
            return bookingTimer;
        }

        public BookingAccounting getAccounting() {
            return accounting;
        }
    }



    private class BookingJobStatusLogs
    {
        private long requestedTime,receivedTime;

        public long getRequestedTime() {
            return requestedTime;
        }

        public long getReceivedTime() {
            return receivedTime;
        }
    }





    /*"bookingId":1518773566771,
"bookingRequestedFor":1518773566,
"bookingRequestedAt":1518773566,
"bookingExpireTime":1518773806,
"currencySymbol":"$",
"currency":"USD",
"serverTime":1518773573,
"bookingType":1,
"status":2,
"statusMsg":"Request Received",
"addLine1":"Creative Villa Apartment, 44 RBI Colony, Vishveshvaraiah Nagar, Ganga Nagar, Bengaluru, Karnataka 560024, India",
"addLine2":"",
"city":"",
"state":"",
"country":"",
"placeId":"",
"pincode":"",
"latitude":13.0286175,
"longitude":77.5894104,
"typeofEvent":"",
"gigTime":[],
"paymentMethod":"cash",
"providerDetail":{},
"jobStatusLogs":{},
"bookingTimer":{},
"signURL":"",
"reviewByProvider":{},
"reviewByCustomer":{},
"accounting":{},
"cancellationReason":"",
"jobDescription":""*/

}
