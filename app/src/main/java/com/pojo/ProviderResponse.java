package com.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ProviderResponse implements Serializable {

    /*private int errNum,errFlag;
      private String errMsg;*/

    private int errNum,errFlag;
    private String errMsg;

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ArrayList<ProviderData> data = null;

   /* private long serverTime;

    public long getServerTime() {
        return serverTime;
    }*/

    public String getMessage() {
        return message;
    }

    public ArrayList<ProviderData> getData() {
        return data;
    }

    public int getErrNum() {
        return errNum;
    }

    public void setErrNum(int errNum) {
        this.errNum = errNum;
    }

    public int getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(int errFlag) {
        this.errFlag = errFlag;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}