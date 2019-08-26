package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pramod on 14/12/17.
 */

public class ServerOtpResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private  OtpData data;

    public String getMessage() {
        return message;
    }
    public OtpData getData() {
        return data;
    }

}
