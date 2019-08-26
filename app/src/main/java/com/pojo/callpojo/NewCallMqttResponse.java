package com.pojo.callpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewCallMqttResponse {

@SerializedName("data")
@Expose
private NewCallData data;

public NewCallData getData() {
return data;
}

public void setData(NewCallData data) {
this.data = data;
}

}