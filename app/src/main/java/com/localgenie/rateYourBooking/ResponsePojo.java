package com.localgenie.rateYourBooking;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ali on 7/2/2018.
 */
public class ResponsePojo implements Serializable
{
    /*"message":"Got The Details.",
"data":[]*/
    private String message;
    private ArrayList<FavProviderData>data;

    public String getMessage() {
        return message;
    }

    public ArrayList<FavProviderData> getData() {
        return data;
    }

    public class FavProviderData implements Serializable
    {
/*"providerId":"5b34b17361596a30f76aa946",
"name":"Nikhil Babu",
"profilePic":"https://s3.amazonaws.com/localserviceprocustomer/ProfilePics/14910071961456.png",
"isOnline":true,
"categoryData":[]*/

        private String providerId,name,profilePic;
        private boolean isOnline;
        private ArrayList<FavProviderCatData>categoryData;

        public String getProviderId() {
            return providerId;
        }

        public String getName() {
            return name;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public boolean isOnline() {
            return isOnline;
        }

        public ArrayList<FavProviderCatData> getCategoryData() {
            return categoryData;
        }
    }

    public class FavProviderCatData implements Serializable
    {
        /*"providerId":"5b34b17361596a30f76aa946",
"categoryName":"Tutor",
"categoryId":"5aaa93be61596a5557059168",
"timestamp":1530520915*/
        private String providerId,categoryName,categoryId;
        private long timestamp;

        public String getProviderId() {
            return providerId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
