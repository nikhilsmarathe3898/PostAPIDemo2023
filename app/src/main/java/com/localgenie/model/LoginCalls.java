package com.localgenie.model;

import java.io.Serializable;

/**
 * Created by Ali on 3/20/2019.
 */
public class LoginCalls implements Serializable
{

    String authToken;
    String willTopic;

    public String getAuthToken() {
        return authToken;
    }

    public String getWillTopic() {
        return willTopic;
    }
}
