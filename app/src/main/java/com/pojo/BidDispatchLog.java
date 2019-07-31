package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ali on 7/9/2018.
 */
public class BidDispatchLog implements Serializable
{
    /*{
"providerId":"5b17b4a2fd702c20b2bef62a",
"firstName":"Ankita",
"lastName":"AS",
"providerName":"Ankita AS",
"profilePic":"https://s3-ap-southeast-1.amazonaws.com/appscrip/localGenie/ProfilePics/1529489844142.png",
"phone":"+918908908908",
"averageRating":1.9,
"location":{
"longitude":77.5894546508789,
"latitude":13.02860260009766
},
"deviceTypeText":"IOS",
"status":3,
"statusMsg":"Accept by provider",
"offerdAt":1531118221,
"quotedPrice":240,
"bidDescription":"this is",
"catName":"painter",
"lastMsg":[
],
"chat":false
}*/



    private String providerId,firstName,lastName,providerName,profilePic,phone,
            statusMsg,bidDescription,catName;
    private float averageRating;
    private long offerdAt,bid;
    private double quotedPrice;
    private ProLocation location;
    private int status;
    private boolean chat,chatAddred;
    private ArrayList<BidLastMsg>lastMsg;


    public boolean isChatAddred() {
        return chatAddred;
    }

    public void setChatAddred(boolean chatAddred) {
        this.chatAddred = chatAddred;
    }

    public ArrayList<BidLastMsg> getLastMsg() {
        return lastMsg;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public long getBid() {
        return bid;
    }

    public void setBid(long bid) {
        this.bid = bid;
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

    public String getProviderName() {
        return providerName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public String getBidDescription() {
        return bidDescription;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public long getOfferdAt() {
        return offerdAt;
    }

    public double getQuotedPrice() {
        return quotedPrice;
    }

    public ProLocation getLocation() {
        return location;
    }

    public int getStatus() {
        return status;
    }

    public boolean isChat() {
        return chat;
    }

    public class BidLastMsg implements Serializable
    {

        /*"_id":"5b712f9951cde95ef2084469",
"type":1,
"timestamp":1534144408966,
"content":"hi m good",
"fromID":"5b4ee8b4fd702c20b2bf0138",
"bid":"1534144339052",
"targetId":"5af56ff9fd702c20b2beef43",
"status":17,
"timeStamp":1534144408,
"userType":"provider",
"userId":"5b4ee8b4fd702c20b2bf0138"*/

        private String content;

        public String getContent() {
            return content;
        }
    }
}
