package com.localgenie.bookingtype;

import android.app.Activity;
import android.content.Context;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h>BookingTypePresenter</h>
 * Created by Ali on 2/9/2018.
 */

public interface BookingTypePresenter
{
    interface presenter extends BasePresenter {

        void openDatePicker(Activity bookingType, boolean b);

        void calltimepicker(Activity bookingType, boolean b);

        void onOpenEndDate(Date date, Context bookingType);

        void listOfDates(List<Date> datesBetweenUsingJava7);

    }
    interface viewPresenter extends BaseView
    {

        void onRepeatEndDateTime(String time, String date, String dateTime);

        void onDateSelected(Date time);

        void onRepeatDateSelected(Date time);

        void onRepeatEndDate(Date time);

        void onSelectTime(boolean isScheduled, Date calendar);

        void setDateOnMap(ArrayList<Integer> dayInArray);

        void dateSelectionCancelled();

        void setTextAsEdit();
    }

}
