package com.pojo;

import java.io.Serializable;

/**
 * Created by Ali on 12/6/2017.
 */

public class NotificationPojo implements Serializable
{
    /*{action=3, msg=Akbar Attar has accepted your booking request for July 24th 2018, 12:30 pm,
    data={"msg":"Akbar Attar has accepted your booking request for July 24th 2018, 12:30 pm",
    "firstName":"Akbar","lastName":"Attar","statusMsg":"Accepted",
    "phone":[{"isCurrentlyActive":true,"phone":"8880675443","countryCode":"+91"}],
    "bookingType":1,"bookingRequestedFor":1532415607,"bookingModel":2,
    "bookingId":1532415607356,"statusUpdateTime":1532415623,
    "proProfilePic":"https:\/\/s3-ap-southeast-1.amazonaws.com\/appscrip\/iserve2.0\/Provider\/ProfilePics\/Profile1531897704802.png",
    "status":3},title=Booking accepted by the Provider, pushType=1}*/
    private long bookingId,statusUpdateTime,timestamp,bid;
    private int type,bookingModel,bookingType,bookingRequestedFor,callType;
    private String targetId,_id,fromID,content,name,profilePic,statusMsg;


    public int getCallType() {
        return callType;
    }

    public int getBookingType() {
        return bookingType;
    }

    public int getBookingRequestedFor() {
        return bookingRequestedFor;
    }

    public int getBookingModel() {
        return bookingModel;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public String getName() {
        return name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getBid() {
        return bid;
    }

    public int getType() {
        return type;
    }

    public String getTargetId() {
        return targetId;
    }

    public String get_id() {
        return _id;
    }

    public String getFromID() {
        return fromID;
    }

    public String getContent() {
        return content;
    }

    public long getBookingId() {
        return bookingId;
    }

    public long getStatusUpdateTime() {
        return statusUpdateTime;
    }
}
