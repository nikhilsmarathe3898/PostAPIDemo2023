package com.localgenie.bookingtype;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.utility.AlertProgress;

import java.util.ArrayList;

import adapters.RepeatDaysAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ali on 6/4/2018.
 *
 * This activity is used to select the number of days
 */
public class RepeatDays extends AppCompatActivity implements RepeatDaysAdapter.RepeatDaysCallback
{
    @BindView(R.id.checkBoxAllDays)CheckBox checkBoxAllDays;
    @BindView(R.id.checkBoxWeekendDays)CheckBox checkBoxWeekendDays;
    @BindView(R.id.checkBoxWeekDays)CheckBox checkBoxWeekDays;
    @BindView(R.id.tvRepeatDays)TextView tvRepeatDays;
    @BindView(R.id.tv_center)TextView tv_center;
    @BindView(R.id.tvSelectRepeatDays)TextView tvSelectRepeatDays;
    @BindView(R.id.toolBarRepeatDays)Toolbar toolBarRepeatDays;
    @BindView(R.id.recyclerRepeatDays)RecyclerView recyclerRepeatDays;
    private LinearLayoutManager layoutManager;
    private ArrayList<String>selectedArrayList = new ArrayList<>();
    private ArrayList<String>allDaysArray = new ArrayList<>();
    private AppTypeface appTypeface;
    private RepeatDaysAdapter repeatDaysAdapter;
    private AlertProgress alertProgress;
    private static final String TAG = "RepeatDays";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_days);
        ButterKnife.bind(this);
        appTypeface = AppTypeface.getInstance(this);
        if(getIntent().getExtras()!=null)
        {
            ArrayList<String> selectedDays = getIntent().getStringArrayListExtra("SelectedDays");
            allDaysArray = getIntent().getStringArrayListExtra("allDaysOfTheWeek");
            selectedArrayList.addAll(selectedDays);

        }
        Log.d(TAG, "onCreate: arrayList:"+selectedArrayList);
        Log.d(TAG, "onCreate: allDaysArray"+allDaysArray);
        alertProgress = new AlertProgress(this);
        setToolBar();
        setRecyclerView();
    }

    private void setRecyclerView() {

        layoutManager = new LinearLayoutManager(this);
        repeatDaysAdapter = new RepeatDaysAdapter(this,allDaysArray,selectedArrayList,this);
        recyclerRepeatDays.setLayoutManager(layoutManager);
        recyclerRepeatDays.setAdapter(repeatDaysAdapter);
        if(selectedArrayList.size()==allDaysArray.size())
        {
            checkBoxAllDays.setChecked(true);
            repeatDaysAdapter.notifyDataSetChanged();
        }
        checkBoxAllDays.setTypeface(appTypeface.getHind_semiBold());
        checkBoxWeekendDays.setTypeface(appTypeface.getHind_semiBold());
        checkBoxWeekDays.setTypeface(appTypeface.getHind_semiBold());
        tvRepeatDays.setTypeface(appTypeface.getHind_semiBold());

    }

    private void setToolBar() {
        tv_center.setText(R.string.shift_RepeatDays);
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        tvSelectRepeatDays.setTypeface(appTypeface.getHind_semiBold());
        setSupportActionBar(toolBarRepeatDays);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBarRepeatDays.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolBarRepeatDays.setNavigationOnClickListener(view -> onBackPressed());
    }

    @OnClick ({R.id.checkBoxAllDays})
    public void onCheckBoxCheck(CompoundButton v)
    {
        switch (v.getId())
        {
            case R.id.checkBoxAllDays:
                checkBoxWeekDays.setChecked(false);
                checkBoxWeekendDays.setChecked(false);
                if(checkBoxAllDays.isChecked()) {
                    selectedArrayList.clear();
                    //repeatDaysAdapter.makeFlag(true);
                    selectedArrayList.addAll(allDaysArray);
                    repeatDaysAdapter.notifyDataSetChanged();
                    Log.d(TAG, "onCheckBoxCheck: "+selectedArrayList);
                }else {
                    selectedArrayList.clear();
                    //repeatDaysAdapter.makeFlag(false);
                    repeatDaysAdapter.notifyDataSetChanged();
                    Log.d(TAG, "onCheckBoxCheck: "+selectedArrayList);
                }
                break;

        }
    }

    @OnClick(R.id.tvRepeatDays)
    public void onSaveClicked()
    {
        if(allDaysArray.size()>6)
            callNextIntent();
        else {
            if(selectedArrayList.size()>1)
                callNextIntent();
            else
                alertProgress.alertinfo(this,getString(R.string.shift_requirement));
        }

    }

    public void callNextIntent()
    {
        Intent intent = new Intent();
        intent.putExtra("SELECTEDDAys",selectedArrayList);
        setResult(RESULT_OK,intent);
        Log.d(TAG, "callNextIntent: "+selectedArrayList);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onSelectedDaysChanged(ArrayList<String> selectList)
    {
        //selectedArrayList.addAll(selectList);
        Log.d(TAG, "onSelectedDaysChanged: "+selectedArrayList);
        if(selectedArrayList.size()==allDaysArray.size()){
            if(!checkBoxAllDays.isChecked()){
                checkBoxAllDays.setSelected(true);
                checkBoxAllDays.setChecked(true);
            }
        }else{
            if(checkBoxAllDays.isChecked()){
                checkBoxAllDays.setSelected(false);
                checkBoxAllDays.setChecked(false);
            }
        }
    }

}
