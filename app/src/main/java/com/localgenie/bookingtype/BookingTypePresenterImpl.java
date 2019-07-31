package com.localgenie.bookingtype;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * <h>BookingTypePresenterImpl</h>
 * Created by Ali on 2/9/2018.
 */

public class BookingTypePresenterImpl implements BookingTypePresenter.presenter
{
    @Inject
    BookingTypePresenter.viewPresenter bookingView;

    @Inject
    AppTypeface appTypeface;
    private Calendar calendar;
    private TimeZone timeZone;
    private boolean isScheduled;
    @Inject
    public BookingTypePresenterImpl() {
    }



    private Context mContext;




    /***********************************************************************************8*/

    @Override
    public void openDatePicker(Activity mContext, boolean isScheduled) {

        this.mContext = mContext;
        this.isScheduled = isScheduled;
        timeZone = Utility.getTimeZone();
        calendar = Calendar.getInstance(timeZone);
        calendar.setTimeZone(timeZone);
        Calendar calendarMin = Calendar.getInstance(timeZone);
        Calendar calendarMax = Calendar.getInstance(timeZone);
        long systemCurrenttime=System.currentTimeMillis();
        long diff=Constants.diffServerTime;
        calendarMin.setTime(new Date(systemCurrenttime-diff + TimeUnit.HOURS.toMillis(1))); // Set min now
        calendarMax.setTime(new Date(systemCurrenttime-diff + TimeUnit.DAYS.toMillis(150)));
        Log.d("TAG", "singlewindowBootomSheet: "+calendarMin.getTimeInMillis());
        Date minDate = calendarMin.getTime();
        Date maxDate = calendarMax.getTime();
        Log.d("TAG", "singlewindowBootomSheet: "+minDate.toString()+" Date:"+minDate.getTime() +" TimeZone " +timeZone);
        Log.d("TAG", "singlewindowBootomSheet: "+maxDate.toString()+" Date:"+maxDate.getTime() +" TimeZone " +timeZone);
        //simpleDateFormat.setTimeZone(VariableConstant.BOOKINGTIMEZONE);

        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                bookingView.setTextAsEdit();
                calendar.set(year, monthOfYear, dayOfMonth);
                BookingTypePresenterImpl.this.calltimepicker(mContext, isScheduled);
            }
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        TextView tv = makeTextView();
        if(isScheduled)
            tv.setText(mContext.getResources().getString(R.string.select_date));
        else
            tv.setText(mContext.getResources().getString(R.string.startDate));
        Log.d("TAG", "openDatePicker: "+tv.getText().toString());
        fromDatePickerDialog.setCustomTitle(tv);
        fromDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        fromDatePickerDialog.getDatePicker().updateDate(minDate.getYear(),minDate.getMonth(),minDate.getDay());
        fromDatePickerDialog.getDatePicker().setMinDate(minDate.getTime()-1000);
        fromDatePickerDialog.setCancelable(false);
        fromDatePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TAG", "onCancel: " + " Cancelled");
                bookingView.dateSelectionCancelled();
            }
        });
        fromDatePickerDialog.show();
    }

    //End of selecting the date

    @Override
    public void calltimepicker(Activity mContext,boolean isScheduled)
    {
        bookingView.onSelectTime(isScheduled,calendar.getTime());
    }


    /********************************************************************************8*/

    @Override
    public void onOpenEndDate(Date date, Context bookingType) {

        mContext = bookingType;
     //   this.isScheduled = isScheduled;
        timeZone = Utility.getTimeZone();
        calendar = Calendar.getInstance(timeZone);
        calendar.setTimeZone(timeZone);

        Calendar calendarMin = Calendar.getInstance(timeZone);
        Calendar calendarMax = Calendar.getInstance(timeZone);
        long systemCurrenttime=date.getTime();
        long diff=Constants.diffServerTime;
        calendarMin.setTime(new Date(systemCurrenttime-diff + TimeUnit.DAYS.toMillis(1))); // Set min now
        calendarMax.setTime(new Date(systemCurrenttime-diff + TimeUnit.DAYS.toMillis(150)));
        Log.d("TAG", "singlewindowBootomSheet: "+calendarMin.getTimeInMillis());
        Date minDate = calendarMin.getTime();
        Date maxDate = calendarMax.getTime();
        Log.d("TAG", "singlewindowBootomSheet: "+minDate.toString()+" Date:"+minDate.getTime() +" TimeZone " +timeZone);
        Log.d("TAG", "singlewindowBootomSheet: "+maxDate.toString()+" Date:"+maxDate.getTime() +" TimeZone " +timeZone);
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(mContext, (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(year,monthOfYear,dayOfMonth);
            bookingView.onRepeatEndDate(calendar.getTime());

        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        TextView tv = makeTextView();
        tv.setText(mContext.getResources().getString(R.string.selectEndDate));
        fromDatePickerDialog.setCustomTitle(tv);
        fromDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        fromDatePickerDialog.getDatePicker().updateDate(minDate.getYear(),minDate.getMonth(),minDate.getDay());
        fromDatePickerDialog.getDatePicker().setMinDate(minDate.getTime()-1000);
        fromDatePickerDialog.setOnCancelListener(dialogInterface -> Log.d("TAG", "onCancel: "+ " Cancelled"));
        fromDatePickerDialog.show();

    }

    private TextView makeTextView() {
        TextView tv = new TextView(mContext);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
        tv.setLayoutParams(lp);
        tv.setPadding(16, 16, 16, 16);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
        tv.setTextColor(Color.parseColor("#FF3A3A3A"));
        tv.setTypeface(appTypeface.getHind_semiBold());
        return tv;
    }


    @Override
    public void listOfDates(List<Date> datesBetweenUsingJava7) {

        Calendar cal = Calendar.getInstance();
        ArrayList<Integer> dayInArray = new ArrayList<>();
        for(int i = 0; i<datesBetweenUsingJava7.size();i++)
        {
            cal.setTime(datesBetweenUsingJava7.get(i));
            int dayIn = cal.get(Calendar.DAY_OF_WEEK);
            dayInArray.add(dayIn);
        }

        if(dayInArray.get(dayInArray.size()-1) == 7)
            dayInArray.add(1);
        else
            dayInArray.add(dayInArray.get(dayInArray.size()-1)+1);

        bookingView.setDateOnMap(dayInArray);
    }

    /***********************************************************************************8*/

    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }


}
