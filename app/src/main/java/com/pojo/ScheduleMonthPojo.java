package com.pojo;

import java.util.ArrayList;

public class ScheduleMonthPojo {
    private String message;
    private ArrayList<ScheduleMonthData> data ;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ScheduleMonthData> getData() {
        return data;
    }

    public void setData(ArrayList<ScheduleMonthData> data) {
        this.data = data;
    }
}
