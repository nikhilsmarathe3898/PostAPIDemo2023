package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pramod on 14/12/17.
 */

public class OtpData {

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


    private  LoginCalls call;

    public LoginCalls getCall() {
        return call;
    }

    public String getRequester_id() {
        return requesterId;
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

/*
    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }
*/

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getFcmTopic() {
        return fcmTopic;
    }


}
