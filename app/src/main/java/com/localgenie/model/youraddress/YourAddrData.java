package com.localgenie.model.youraddress;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class YourAddrData implements Serializable {


    /*"_id":"5b6ebf9b28b2c9c12e37b10a",
"taggedAs":"btm layout",
"user_Id":"5af56ff9fd702c20b2beef43",
"houseNo":"#34",
"name":"",
"addLine1":"75, 19th D Cross Road, Sunshine Colony, Stage 2, BTM Layout, Bengaluru, Karnataka 560076, India",
"addLine2":"",
"city":"Bengaluru",
"state":"Karnataka",
"country":"India",
"placeId":"",
"pincode":"560076",
"latitude":12.911306399999999,
"longitude":77.6067265,
"userType":1*/


    @SerializedName("_id")
    @Expose
    private String id;

    private String taggedAs;

    @SerializedName("user_Id")
    @Expose
    private String userId;

    @SerializedName("houseNo")
    @Expose
    private String houseNo;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("addLine1")
    @Expose
    private String addLine1;

    @SerializedName("addLine2")
    @Expose
    private String addLine2;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("country")
    @Expose
    private String country;

   /* @SerializedName("placeId")
    @Expose
    private String placeId;*/


    private String pincode;

    @SerializedName("latitude")
    @Expose
    private Double latitude;

    @SerializedName("longitude")
    @Expose
    private Double longitude;

/*    @SerializedName("userType")
    @Expose
    private Integer userType;*/


    public String getHouseNo() {
        return houseNo;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaggedAs() {
        return taggedAs;
    }

/*
    public void setTaggedAs(String taggedAs) {
        this.taggedAs = taggedAs;
    }
*/

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddLine1() {
        return addLine1;
    }

    public void setAddLine1(String addLine1) {
        this.addLine1 = addLine1;
    }

    public String getAddLine2() {
        return addLine2;
    }

/*
    public void setAddLine2(String addLine2) {
        this.addLine2 = addLine2;
    }
*/

    public String getCity() {
        return city;
    }

/*
    public void setCity(String city) {
        this.city = city;
    }
*/

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }


  /*  public String getPlaceId() {
        return placeId;
    }*/


    public String getPincode() {
        return pincode;
    }


    public Double getLatitude() {
        return latitude;
    }


    public Double getLongitude() {
        return longitude;
    }


}