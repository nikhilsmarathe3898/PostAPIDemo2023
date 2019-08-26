package com.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.localgenie.model.Location;

import java.io.Serializable;

public class ProviderData implements Serializable {

    /*"id":"5a41fd7c0f94fa11ec236f79",
"firstName":"Raj",
"lastName":"Singh",
"image":"https://s3.amazonaws.com/livemapplication/Provider/ProfilePics/1514274171998_0_01.png",
"location":{
"longitude":77.5895080566406,
"latitude":13.0286455154419
},
"bannerImage":"",
"status":1,
"distance":0.01,
"youtubeUrlLink":"www.youtube.com",
"amount":45,
"currencySymbol":"$",
"averageRating":0,
"currency":"USD",
"distanceMatrix":0,
"providerType":1
"availableNow":0*/


    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("bannerImage")
    @Expose
    private String bannerImage;

    @SerializedName("distanceMatrix")
    @Expose
    private int distanceMatrix;

    @SerializedName("distance")
    @Expose
    private double distance;

    @SerializedName("amount")
    @Expose
    private double amount;

    @SerializedName("currencySymbol")
    @Expose
    private String currencySymbol;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("status")
    @Expose
    private int status;

    private boolean isHeader;
    private String headerText;
    private float averageRating;
    private boolean favouriteProvider;
    private int availableNow;

    public int isAvailableNow() {
        return availableNow;
    }

    public boolean isFavouriteProvider() {
        return favouriteProvider;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getBannerImage() {
        return bannerImage;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public double getDistance() {
        return distance;
    }



    public double getAmount() {
        return amount;
    }


    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getDistanceMatrix() {
        return distanceMatrix;
    }
}