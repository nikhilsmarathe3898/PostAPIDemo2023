package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ali on 10/30/2018.
 */
public class SlotDetails implements Serializable
{
    /*"message":"Provider details received successfully.",
"data"*/
    String message;
    ArrayList<Slots>data;

    public String getMessage() {
        return message;
    }

    public ArrayList<Slots> getData() {
        return data;
    }
}
