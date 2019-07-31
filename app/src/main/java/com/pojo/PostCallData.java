package com.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostCallData {

@SerializedName("callId")
@Expose
private String callId;

public String getCallId() {
return callId;
}

public void setCallId(String callId) {
this.callId = callId;
}

}