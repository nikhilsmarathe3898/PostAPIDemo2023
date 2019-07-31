package com.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostCallResponse {

@SerializedName("message")
@Expose
private String message;
@SerializedName("data")
@Expose
private PostCallData data;

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public PostCallData getData() {
return data;
}

public void setData(PostCallData data) {
this.data = data;
}

}