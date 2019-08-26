package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <h>SignUpData</h>
 * Created by Pramod on 14/12/17.
 */

public class SignUpData {

    @SerializedName("sid")
    @Expose
    private String sid;

    @SerializedName("expireOtp")
    @Expose
    private int expireOtp;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setExpireOtp(int expireOtp) {
        this.expireOtp = expireOtp;
    }

    public long getExpireOtp() {
        return expireOtp;
    }
}
