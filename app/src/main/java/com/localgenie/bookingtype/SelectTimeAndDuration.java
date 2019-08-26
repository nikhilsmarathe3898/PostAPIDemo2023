package com.localgenie.bookingtype;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.utility.AlertProgress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectTimeAndDuration extends AppCompatActivity implements DialogTimeFragment.OnFragmentInteractionListener
{
    @BindView(R.id.toolBarDuration)Toolbar toolBarDuration;
    @BindView(R.id.tv_center)TextView tv_center;
    @BindView(R.id.tvDone)TextView tvDone;
  //  @BindView(R.id.timePicker)TimePicker timePicker;



    @BindView(R.id.glRepTimeGrid)GridLayout glRepTimeGrid;
    @BindView(R.id.tvRepSelTime)TextView tvRepSelTime;
    @BindView(R.id.tvRepTime)TextView tvRepTime;
    @BindView(R.id.tvTimeDurationInfo)TextView tvTimeDurationInfo;

    @BindView(R.id.glRepDurationGrid)GridLayout glRepDurationGrid;
    @BindView(R.id.tvRepSelDuration)TextView tvRepSelDuration;
    @BindView(R.id.tvRepDuration)TextView tvRepDuration;
    @BindView(R.id.llMainDuration)LinearLayout llMainDuration;


    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";
    public static final String ARG_PARAM_NOW = "param3";
    public static final String ARG_CUSTOME = "isCustomSelected";
    public static final String ARG_SELECTED = "selectedDays";
    public static final String ARG_CurrentDay = "currentDay";
    private String mParam1,currentDay = "";
    private boolean mParamIsSchedule;
    private boolean mParamIsNow,isCustomSelected;
    private boolean isBackPressed = false;
    private ArrayList<String> selectedDays;
    private AppTypeface appTypeface;
    private AlertProgress alertProgress;
    String dateTime;
    private static final String TAG = "SelectTimeAndDuration";
    private int durationHour=1,durationMin;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy',' h:mm a", Locale.getDefault());
    private Date selectedDateAndTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time_and_duration);
        ButterKnife.bind(this);
        appTypeface = AppTypeface.getInstance(this);
        alertProgress = new AlertProgress(this);
        getIntentValue();
       /* mParamIsSchedule = getIntent().getBooleanExtra(ARG_PARAM2,false);
        mParam1 = getIntent().getStringExtra(ARG_PARAM1);
        mParamIsNow = getIntent().getBooleanExtra(ARG_PARAM_NOW,false);
        isCustomSelected = getIntent().getBoolean(ARG_CUSTOME);
        currentDay = getIntent().getString(ARG_CurrentDay);
        selectedDays = getIntent().getStringArrayList(ARG_SELECTED);*/
        initializeToolBar();
        initialize();
        typeFace();
      // String time = simpleDateFormat.format(calendar.getTime());
    }

    private void getIntentValue() {
        Bundle getArguments = getIntent().getExtras();
        if (getArguments != null) {
            mParamIsSchedule = getArguments.getBoolean(ARG_PARAM2);
            mParam1 = getArguments.getString(ARG_PARAM1);
            mParamIsNow = getArguments.getBoolean(ARG_PARAM_NOW);
            isCustomSelected = getArguments.getBoolean(ARG_CUSTOME);
            currentDay = getArguments.getString(ARG_CurrentDay);
            selectedDays = getArguments.getStringArrayList(ARG_SELECTED);
            Log.d("TAG", "onCreate: "+isCustomSelected +" currentDays "+currentDay
                    +" SelectedDays "+selectedDays);

        }
    }

    private void typeFace() {

        tvRepSelTime.setTypeface(appTypeface.getHind_semiBold());
        tvRepTime.setTypeface(appTypeface.getHind_semiBold());
        tvRepSelDuration.setTypeface(appTypeface.getHind_semiBold());
        tvRepDuration.setTypeface(appTypeface.getHind_semiBold());
        tvTimeDurationInfo.setTypeface(appTypeface.getHind_bold());
        tvTimeDurationInfo.setText(getString(R.string.select_shift_time));

    }

    private void initializeToolBar() {
        setSupportActionBar(toolBarDuration);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tv_center.setText(getString(R.string.shift_time));
        toolBarDuration.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolBarDuration.setNavigationOnClickListener(view -> onBackPressed());

    }
    private void initialize() {

        tv_center.setTypeface(appTypeface.getHind_semiBold());
        tvDone.setTypeface(appTypeface.getHind_semiBold());
        dateTime = simpleDateFormat.format(System.currentTimeMillis());

    }
    String statusAmPm;
    @OnClick ({R.id.tvDone,R.id.glRepTimeGrid,R.id.glRepDurationGrid})
    public void onDoneClick(View v)
    {
        switch (v.getId())
        {
            case R.id.glRepTimeGrid:
                   DialogTimeFragment dialogTimeFragment = DialogTimeFragment.newInstance(false,mParam1,isCustomSelected
                   ,selectedDays,currentDay);
                           dialogTimeFragment.show(getSupportFragmentManager(),"Timer");
                break;
            case R.id.glRepDurationGrid:
                showDurationDialog();
                break;
            case R.id.tvDone:
                if(tvDone.getText().toString().trim().equals(getString(R.string.next)))
                {
                    if(!"".equals(timeSetValue))
                    {
                        tvDone.setText(R.string.done);
                        glRepDurationGrid.setVisibility(View.VISIBLE);
                        glRepTimeGrid.setVisibility(View.GONE);
                        tv_center.setText(getString(R.string.shift_Duration));
                        tvTimeDurationInfo.setText(getString(R.string.select_shift_Duration));
                        isBackPressed = true;
                    }else
                        alertProgress.alertinfo(this,getString(R.string.please_shift_time));

                }else
                {
                    onButtonPressed(timeSetValue,mParamIsSchedule);

                }
                break;
        }


    }

    private void onButtonPressed(String timeDone, boolean mParamIsSchedule) {

        if(durationHour!=0 || durationMin!=0)
            onFragmentInteraction(timeDone,mParamIsSchedule);
        else
            alertProgress.alertinfo(this,getString(R.string.minimumBookingDuration));

    }
    int hrDur=1,minDur;
    private void showDurationDialog() {

        final Dialog dialog =new Dialog(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.duration_picker,llMainDuration,false);
        final NumberPicker month=  inflate.findViewById(R.id.numberPickerMonth);
        final NumberPicker year=  inflate.findViewById(R.id.numberPickerYear);
        final TextView tvMonthDialog=  inflate.findViewById(R.id.dialogMonth);
        final TextView tvYearDialog=  inflate.findViewById(R.id.dialogYear);
        final TextView tvSelectDuration=  inflate.findViewById(R.id.tvSelectDuration);
        Button done=  inflate.findViewById(R.id.done);
        tvSelectDuration.setTypeface(appTypeface.getHind_semiBold());
        tvSelectDuration.setText(R.string.shift_Duration);

        done.setTypeface(appTypeface.getHind_semiBold());
        String[] strings = {"00"};//,"30"
        month.setMinValue(1);
        month.setValue(1);
        durationHour = month.getValue();
        tvMonthDialog.setText(""+month.getValue());
        month.setOnValueChangedListener((picker, oldVal, newVal) -> {
          //  durationHour
            if(newVal==0){
                picker.setValue(1);
                newVal=1;
            }
            hrDur= newVal;
            tvMonthDialog.setText(""+newVal);
        });
        year.setOnValueChangedListener((picker, oldVal, newVal) -> {
          //  durationMin
                   minDur = Integer.parseInt(strings[newVal]);
            tvYearDialog.setText(""+durationMin);
        });

        year.setMinValue(0);
        year.setMaxValue(strings.length-1);
        year.setDisplayedValues(strings);
        month.setMaxValue(23);
        //month.setMinValue(0);
        done.setOnClickListener(v -> {

            durationHour = hrDur;
            durationMin = minDur;
            if(durationHour!=0 || durationMin!=0)
            {
                String hr = durationHour+" hr : "+durationMin+" mn";
                tvRepDuration.setText(hr);
                dialog.dismiss();
            }
            else
                alertProgress.alertinfo(this,getString(R.string.minimumBookingDuration));
        });
        dialog.setContentView(inflate);
        dialog.show();
    }

    private void onFragmentInteraction(String dateTIme, boolean mParamIsSchedule)
    {

        //            Date dateTIme = simpleDateFormat.parse(timeDone);
        Log.d(TAG, "onFragmentInteraction: selectedtime" +dateTIme +" durationhr: "+durationHour+" durationmin:"+durationMin);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isNow",mParamIsNow);
        bundle.putBoolean("isSchedule",mParamIsSchedule);
        bundle.putString("SelectedTime",dateTIme);
        bundle.putInt("durationHour",durationHour);
        bundle.putInt("durationMin",durationMin);
        bundle.putLong("selectedTimestamp",selectedDateAndTime.getTime());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);

    }

    String timeSetValue = "";
    @Override
    public void onFragmentInteraction(Date dateObj, boolean isSchedule)
    {
        selectedDateAndTime=dateObj;
        timeSetValue = simpleDateFormat.format(dateObj);
        String textSet[] = timeSetValue.split(",");
        tvRepTime.setText(textSet[2]);
    }


    @Override
    public void onBackPressed() {
       // super.onBackPressed();

        if(isBackPressed)
        {
            isBackPressed = false;
            tvDone.setText(R.string.next);
            glRepDurationGrid.setVisibility(View.GONE);
            glRepTimeGrid.setVisibility(View.VISIBLE);
            tv_center.setText(getString(R.string.shift_time));
            tvTimeDurationInfo.setText(getString(R.string.select_shift_time));

        }else
            setResult(RESULT_CANCELED);
            finish();
    }
}
