package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ali on 5/28/2018.
 */
public class BookingChatPojo implements Serializable
{
/*"message":"Got The Details.",
"data":{
"past":[],
"accepted":[]
}*/
String message;
private BookingChatData data;

    public String getMessage() {
        return message;
    }

    public BookingChatData getData() {
        return data;
    }

    public class BookingChatData implements Serializable
    {
        ArrayList<BookingChatHistory>past;
        ArrayList<BookingChatHistory>accepted;

        public ArrayList<BookingChatHistory> getPast() {
            return past;
        }

        public ArrayList<BookingChatHistory> getAccepted() {
            return accepted;
        }
    }
}
