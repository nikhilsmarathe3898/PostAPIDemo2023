
package com.localgenie.model.guest_login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GuestLoginResponse implements Serializable {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private GuestLoginData guestLoginData;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GuestLoginData getGuestLoginData() {
        return guestLoginData;
    }

    public void setGuestLoginData(GuestLoginData guestLoginData) {
        this.guestLoginData = guestLoginData;
    }
}
