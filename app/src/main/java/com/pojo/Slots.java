package com.pojo;

import java.io.Serializable;

/**
 * Created by Ali on 10/30/2018.
 */
public class Slots implements Serializable
{
 private long from,to;
    public int isBook,duration; //0 booked 1 available

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    public int getDuration() {
        return duration;
    }

    public int getIsBook() {
        return isBook;
    }

    public void setIsBook(int isBook) {
        this.isBook = isBook;
    }
}
