package com.localgenie.model.payment_method;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CardGetResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ArrayList<CardGetData> data = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<CardGetData> getData() {
        return data;
    }

    public void setData(ArrayList<CardGetData> data) {
        this.data = data;
    }

}