package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.localgenie.model.payment_method.CardGetData;

/**
 * Created by Pramod on 14/12/17.
 */

public class Data {

    /*"call":{
"authToken":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1YmQ4MzQ1ZjBjZTZhOTBmM2E0ODUxOGMiLCJrZXkiOiJhY2MiLCJhY2Nlc3NDb2RlIjo2NzUzLCJpYXQiOjE1NTMwNjE2NTAsImV4cCI6MTU1MzA2MTczNiwic3ViIjoidXNlciJ9.LTnJCcD2wkSOu8NIm2vZjUU7iIijN5XN3zeebP5VTFA",
"willTopic":"lastWill"
}*/

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("sid")
    @Expose
    private String sid;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("referralCode")
    @Expose
    private String referralCode;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;
    @SerializedName("paymentId")
    @Expose
    private String paymentId;
    @SerializedName("fcmTopic")
    @Expose
    private String fcmTopic;
    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;
    @SerializedName("PublishableKey")
    @Expose
    private String publishableKey;

    @SerializedName("requester_id")
    @Expose
    private String requesterId;



    private CardGetData cardDetail;
    private  LoginCalls call;

    public LoginCalls getCall() {
        return call;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public CardGetData getCardDetail() {
        return cardDetail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getReferralCode() {
        return referralCode;
    }


    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getPaymentId() {
        return paymentId;
    }


    public String getFcmTopic() {
        return fcmTopic;
    }


    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getPublishableKey() {
        return publishableKey;
    }

    public void setPublishableKey(String publishableKey) {
        this.publishableKey = publishableKey;
    }

}
