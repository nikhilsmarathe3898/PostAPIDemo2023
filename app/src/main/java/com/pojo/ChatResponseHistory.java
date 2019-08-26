package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ali on 1/19/2018.
 */

public class ChatResponseHistory implements Serializable
{
    String message;
    ArrayList<ChatData>data;


    public ArrayList<ChatData> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
