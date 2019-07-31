package com.localgenie.model.youraddress;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class YourAddressResponse implements Serializable {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ArrayList<YourAddrData> data = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<YourAddrData> getData() {
        return data;
    }

    public void setData(ArrayList<YourAddrData> data) {
        this.data = data;
    }

}