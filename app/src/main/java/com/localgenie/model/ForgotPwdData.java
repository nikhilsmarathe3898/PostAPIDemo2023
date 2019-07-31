package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pramod on 14/12/17.
 */

public class ForgotPwdData {

    @SerializedName("sid")
    @Expose
    private String sid;

    @SerializedName("expireOtp")
    @Expose
    private long expireOtp;

    public String getSid() {
        return sid;
    }

    public long getExpireOtp() {
        return expireOtp;
    }
}
