package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <h>MyBookingStatus</h>
 * Created by Ali on 2/14/2018.
 */

public class MyBookingStatus implements Serializable {
    private DataEvent data;

    public MyBookingStatus(DataEvent data) {
        this.data = data;
    }

    public DataEvent getData() {
        return data;
    }

    public class DataEvent {
        /*"bookingId":1509723436076,"status":3,"statusMsg":"Accepted","statusUpdateTime":1509723754,
        "proProfilePic":"https:\/\/s3.amazonaws.com\/livemapplication\/Provider\/ProfilePics\/LIVEMProfile1509030309086.png"
        ,"firstName":"Dhaval","lastName":"Test"}*/

        /*"bookingId":1519136552701,
"status":3,
"statusMsg":"Request Accepted by provider.",
"statusUpdateTime":1519136568,
"proProfilePic":"https://s3.amazonaws.com/localserviceprocustomer/Provider/ProfilePics/1519105091181.png",
"firstName":"Mehboob",
"lastName":"Ali",
"phone":[],
"bidProvider":[]*/
        String statusMsg, proProfilePic,firstName,lastName,msg,reminderId;

        long bookingRequestedFor, bookingId;  //statusUpdateTime
        int status,bookingModel,bookingType,callType;


        BookingTimer bookingTimer;
        ArrayList<PhoneContact>phone;
        ArrayList<BidDispatchLog>bidProvider;

        public int getCallType() {
            return callType;
        }

        public ArrayList<BidDispatchLog> getBidProvider() {
            return bidProvider;
        }

        public String getReminderId() {
            return reminderId;
        }

        public int getBookingType() {
            return bookingType;
        }

        public long getBookingRequestedFor() {
            return bookingRequestedFor;
        }

        public int getBookingModel() {
            return bookingModel;
        }

        public String getMsg() {
            return msg;
        }

        public BookingTimer getBookingTimer() {
            return bookingTimer;
        }

        public void setBookingTimer(BookingTimer bookingTimer) {
            this.bookingTimer = bookingTimer;
        }

        public void setPhone(ArrayList<PhoneContact> phone) {
            this.phone = phone;
        }

        public String getStatusMsg() {
            return statusMsg;
        }

        public void setStatusMsg(String statusMsg) {
            this.statusMsg = statusMsg;
        }

        public String getProProfilePic() {
            return proProfilePic;
        }

      /*  public void setProProfilePic(String proProfilePic) {
            this.proProfilePic = proProfilePic;
        }

        public long getStatusUpdateTime() {
            return statusUpdateTime;
        }

        public void setStatusUpdateTime(long statusUpdateTime) {
            this.statusUpdateTime = statusUpdateTime;
        }*/

        public long getBookingId() {
            return bookingId;
        }

        public void setBookingId(long bookingId) {
            this.bookingId = bookingId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public ArrayList<PhoneContact> getPhone() {
            return phone;
        }
    }

}
