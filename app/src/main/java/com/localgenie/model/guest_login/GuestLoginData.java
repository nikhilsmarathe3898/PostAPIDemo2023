
package com.localgenie.model.guest_login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GuestLoginData {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("sid")
    @Expose
    private String sid;

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
}
