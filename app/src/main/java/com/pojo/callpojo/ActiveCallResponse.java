package com.pojo.callpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActiveCallResponse {

    @SerializedName("data")
    @Expose
    private ActiveCallData data;

    public ActiveCallData getData() {
        return data;
    }

    public void setData(ActiveCallData data) {
        this.data = data;
    }

}
