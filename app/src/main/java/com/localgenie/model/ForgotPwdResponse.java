package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pramod on 14/12/17.
 */

public class ForgotPwdResponse {

    @SerializedName("message")
    @Expose
    private String message;
    private ForgotPwdData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ForgotPwdData getData() {
        return data;
    }

    public void setData(ForgotPwdData data) {
        this.data = data;
    }
}
