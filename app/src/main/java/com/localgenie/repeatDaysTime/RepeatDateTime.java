package com.localgenie.repeatDaysTime;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;
import com.utility.AlertProgress;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.github.florent37.singledateandtimepicker.dialog.SingleDateTimeDialogReturn.timeZone;
//This activity is used to select this week or this Month or between a custom date;
//This week
//This month
//Custom date and time

public class RepeatDateTime extends AppCompatActivity {

    @BindView(R.id.tvRepeatThisWeek)TextView tvRepeatThisWeek;
    @BindView(R.id.tvRepeatThisMonth)TextView tvRepeatThisMonth;
    @BindView(R.id.tvRepeatCustomDays)TextView tvRepeatCustomDays;
    @BindView(R.id.tvNextDays)TextView tvNextDays;
    @BindView(R.id.tvSelectedDatesToEnd)TextView tvSelectedDatesToEnd;
    @BindView(R.id.tv_center)TextView tv_center;
    @BindView(R.id.toolbarRepeatDays)Toolbar toolbarRepeatDays;
    private AppTypeface appTypeface;
    private SimpleDateFormat sdf;
    private Date startDate,endDate;
    private Calendar calendar,calendarStrt;
    private AlertProgress alertProgress;
    private boolean isEditTrue = false;
    private boolean isCustomSelected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_date_time);
        ButterKnife.bind(this);
        alertProgress = new AlertProgress(this);
        appTypeface = AppTypeface.getInstance(this);
        sdf = new SimpleDateFormat("MMM d, yyyy',' h:mm a", Locale.getDefault());

        toolBarDays();
        typeFace();
        calendarSet();
        getIntentsValue();
    }

    private void getIntentsValue() {

        if(getIntent().getExtras()!=null)
        isEditTrue  = getIntent().getBooleanExtra("isEditTrue",false);
    }

    private void calendarSet() {
        calendar = Calendar.getInstance();
        calendar.setTimeZone(Utility.getTimeZone());
        Log.d("TAG","DAYS "+calendar.get(Calendar.DAY_OF_WEEK));
        int daysOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(daysOfWeek == 7)
            tvRepeatThisWeek.setVisibility(View.GONE);

            String today = sdf.format(calendar.getTime());
            Date lastDate = lastDaysOfMonth();
        assert lastDate != null;
        String last = sdf.format(lastDate.getTime());
        String toDays[] = today.split(",");
        String lstDays[] = last.split(",");
        Log.d("TAG", "calendarSet: "+toDays[0]+" "+toDays[1]+" \n "+lstDays[0]+" "+lstDays[1]);
        if((toDays[0]+" "+toDays[1]).equals(lstDays[0]+" "+lstDays[1]))
        {
            tvRepeatThisMonth.setVisibility(View.GONE);
        }

    }

    private void typeFace() {

        tvNextDays.setTypeface(appTypeface.getHind_semiBold());
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        tvRepeatThisWeek.setTypeface(appTypeface.getHind_medium());
        tvRepeatThisMonth.setTypeface(appTypeface.getHind_medium());
        tvRepeatCustomDays.setTypeface(appTypeface.getHind_medium());
        tvSelectedDatesToEnd.setTypeface(appTypeface.getHind_medium());
    }

    private void toolBarDays() {
        setSupportActionBar(toolbarRepeatDays);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tv_center.setText(getString(R.string.shift_date));
        toolbarRepeatDays.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbarRepeatDays.setNavigationOnClickListener(view -> onBackPressed());
    }

    @OnClick({R.id.tvRepeatThisWeek,R.id.tvRepeatThisMonth,R.id.tvRepeatCustomDays,R.id.tvNextDays})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tvNextDays:
                if(startDate!=null)
                {
                    Intent intent = new Intent();
                    intent.putExtra("StartDate",sdf.format(startDate));
                    intent.putExtra("EndDate",sdf.format(endDate));
                    intent.putExtra("isCustomSelected",isCustomSelected);
                    setResult(RESULT_OK,intent);
                    finish();
                    overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
                }else
                    alertProgress.alertinfo(this,getString(R.string.pleaseSelectSuitableDate));


                break;
            case R.id.tvRepeatThisWeek:
                startDate = calendar.getTime();
                endDate =  getNearestWeekend();
                setTextInValue();
                isCustomSelected = false;
                setSelection(1);
                break;
            case R.id.tvRepeatThisMonth:
                startDate = calendar.getTime();
                endDate = lastDaysOfMonth();
                isCustomSelected = false;
                setTextInValue();
                setSelection(2);
                break;
            case R.id.tvRepeatCustomDays:
                startDate = null;
                isCustomSelected = true;
                tvSelectedDatesToEnd.setText("");
                calendarStrt = null;
                openDatePicker();
                setSelection(3);
                break;
        }
    }

    private void setTextInValue() {
        tvNextDays.setVisibility(View.VISIBLE);
        String strtweek = sdf.format(startDate);
        String endweek = sdf.format(endDate);
        String startWeek[] = strtweek.split(",");
        String endWeek[] = endweek.split(",");
        String selectedTime = startWeek[0]+", "+startWeek[1]+" - "+endWeek[0]+", "+endWeek[1];
        tvSelectedDatesToEnd.setText(selectedTime);
    }

    private void setSelection(int i)
    {

        tvRepeatThisWeek.setSelected(false);
        tvRepeatThisMonth.setSelected(false);
        tvRepeatCustomDays.setSelected(false);

        switch (i)
        {
            case 1:
                tvRepeatThisWeek.setSelected(true);
                break;
            case 2:
                tvRepeatThisMonth.setSelected(true);
                break;
            case 3:
                tvRepeatCustomDays.setSelected(true);
                break;
        }
    }

    private Date getNearestWeekend()
    {
        try {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            return sdf.parse(sdf.format(c.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Date lastDaysOfMonth()
    {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        System.out.println(sdf.format(c.getTime()));
        Log.d("TAG", "lastDaysOfMonth: "+sdf.format(c.getTime())+" \n "+
                Calendar.YEAR+" \n "+Calendar.MONTH+" \n "+Calendar.DATE);
        try {
            return sdf.parse(sdf.format(c.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * <h1>OpenDatePicker</h1>
     * this open picker dialog for the date

     */
    private void openDatePicker()
    {

        timeZone = Utility.getTimeZone();
        calendarStrt = Calendar.getInstance(timeZone);
        calendarStrt.setTimeZone(timeZone);
        Calendar calendarMin = Calendar.getInstance(timeZone);
        Calendar calendarMax = Calendar.getInstance(timeZone);
        long systemCurrenttime=System.currentTimeMillis();
        long diff= Constants.diffServerTime;
        calendarMin.setTime(new Date(systemCurrenttime-diff + TimeUnit.HOURS.toMillis(1))); // Set min now
        calendarMax.setTime(new Date(systemCurrenttime-diff + TimeUnit.DAYS.toMillis(150)));
        Date minDate = calendarMin.getTime();
        Date maxDate = calendarMax.getTime();

        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            calendarStrt.set(year,monthOfYear,dayOfMonth);
            startDate = calendarStrt.getTime();
            onOpenEndDate(calendarStrt.getTime());

        },calendarStrt.get(Calendar.YEAR), calendarStrt.get(Calendar.MONTH), calendarStrt.get(Calendar.DAY_OF_MONTH));
        TextView tv = makeTextView();

            tv.setText(R.string.startDate);
        fromDatePickerDialog.setCustomTitle(tv);
        fromDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        fromDatePickerDialog.getDatePicker().updateDate(minDate.getYear(),minDate.getMonth(),minDate.getDay());
        fromDatePickerDialog.getDatePicker().setMinDate(minDate.getTime()-1000);
        fromDatePickerDialog.setOnCancelListener(dialogInterface -> Log.d("TAG", "onCancel: "+ " Cancelled"));
        fromDatePickerDialog.show();
    }

    public void onOpenEndDate(Date date) {

        timeZone = Utility.getTimeZone();
        calendarStrt = Calendar.getInstance(timeZone);
        calendarStrt.setTimeZone(timeZone);

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
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            calendarStrt.set(year,monthOfYear,dayOfMonth);
           /* onRepeatEndDate(calendar.getTime());
            onSelectedDuration(true);*/
            endDate = calendarStrt.getTime();
            setTextInValue();

        },calendarStrt.get(Calendar.YEAR), calendarStrt.get(Calendar.MONTH), calendarStrt.get(Calendar.DAY_OF_MONTH));
        TextView tv = makeTextView();
        tv.setText(R.string.selectEndDate);
        fromDatePickerDialog.setCustomTitle(tv);
        fromDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        fromDatePickerDialog.getDatePicker().updateDate(minDate.getYear(),minDate.getMonth(),minDate.getDay());

        fromDatePickerDialog.getDatePicker().setMinDate(minDate.getTime()-1000);
        fromDatePickerDialog.setOnCancelListener(dialogInterface -> Log.d("TAG", "onCancel: "+ " Cancelled"));
        fromDatePickerDialog.show();

    }

    private TextView makeTextView() {
        TextView tv = new TextView(this);
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
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);

    }
}