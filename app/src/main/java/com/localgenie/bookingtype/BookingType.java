package com.localgenie.bookingtype;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateTimeDialogReturn;
import com.localgenie.Login.LoginActivity;
import com.localgenie.R;
import com.localgenie.addTocart.AddToCart;
import com.localgenie.confirmbookactivity.ConfirmBookActivity;
import com.localgenie.model.Category;
import com.localgenie.model.Offers;
import com.localgenie.model.SortByMinOffer;
import com.localgenie.model.SubCategory;
import com.localgenie.providerList.ProviderList;
import com.localgenie.repeatDaysTime.RepeatDateTime;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.utility.AlertProgress;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import adapters.OfferAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

public class BookingType extends DaggerAppCompatActivity  implements BookingTypePresenter.viewPresenter,DialogTimeFragment.OnFragmentInteractionListener {

    private String TAG = BookingType.class.getSimpleName();
    @BindView(R.id.tvBookingTypeWhenYOu)TextView tvBookingTypeWhenYOu;
    @BindView(R.id.tvBookTypeSave)TextView tvBookTypeSave;

    @BindView(R.id.tv_center)TextView tv_center;
    @BindView(R.id.toolBookingType)Toolbar toolBookingType;


    @BindView(R.id.glNowGrid)GridLayout glNowGrid;
    @BindView(R.id.glNowPartTimeGrid)GridLayout glNowPartTimeGrid;
    @BindView(R.id.tvNowSelTime)TextView tvNowSelTime;
    @BindView(R.id.tvNowTodaysTime)TextView tvNowTodaysTime;

    @BindView(R.id.ivNowCheck)ImageView ivNowCheck;
    @BindView(R.id.tvNow)TextView tvNow;
    @BindView(R.id.tvNowDesc)TextView tvNowDesc;
    @BindView(R.id.tvNowDecSelected)TextView tvNowDecSelected;
    @BindView(R.id.spaceNow)Space spaceNow;

    @BindView(R.id.glLaterGrid)GridLayout glLaterGrid;
    @BindView(R.id.ivLaterCheck)ImageView ivLaterCheck;
    @BindView(R.id.tvLater)TextView tvLater;
    @BindView(R.id.tvLaterDesc)TextView tvLaterDesc;

    @BindView(R.id.glRepeatGrid)GridLayout glRepeatGrid;
    @BindView(R.id.ivRepeatCheck)ImageView ivRepeatCheck;
    @BindView(R.id.tvRepeat)TextView tvRepeat;
    @BindView(R.id.tvRepeatDesc)TextView tvRepeatDesc;

    //Later Part
    @BindView(R.id.glLaterPartGrid)GridLayout glLaterPartGrid;
    @BindView(R.id.tvSelDate)TextView tvSelDate;
    @BindView(R.id.tvTodaysDate)TextView tvTodaysDate;

    @BindView(R.id.glLaterPartTimeGrid)GridLayout glLaterPartTimeGrid;
    @BindView(R.id.tvSelTime)TextView tvSelTime;
    @BindView(R.id.tvTodaysTime)TextView tvTodaysTime;

    @BindView(R.id.glLaterPartDurationGrid)GridLayout glLaterPartDurationGrid;
    @BindView(R.id.tvSelDuration)TextView tvSelDuration;
    @BindView(R.id.tvTodaysDuration)TextView tvTodaysDuration;

    //Repeat Part
    @BindView(R.id.glRepeatPartGrid)GridLayout glRepeatPartGrid;
    @BindView(R.id.tvSelRepeatStartDate)TextView tvSelRepeatStartDate;
    @BindView(R.id.tvTodaysRepeatStartDate)TextView tvTodaysRepeatStartDate;

    @BindView(R.id.glRepeatPartTimeGrid)GridLayout glRepeatPartTimeGrid;
    @BindView(R.id.tvRepeatSelTime)TextView tvRepeatSelTime;
    @BindView(R.id.tvRepeatStartTime)TextView tvRepeatStartTime;

    /*@BindView(R.id.glRepeatPartEndDateGrid)GridLayout glRepeatPartEndDateGrid;
    @BindView(R.id.tvSelRepeatEndDate)TextView tvSelRepeatEndDate;
    @BindView(R.id.tvTodaysRepeatEndDate)TextView tvTodaysRepeatEndDate;*/

    @BindView(R.id.glRepeatPartDurationGrid)GridLayout glRepeatPartDurationGrid;
    @BindView(R.id.tvSelRepeatDuration)TextView tvSelRepeatDuration;
    @BindView(R.id.tvSelRepeatDurationTime)TextView tvSelRepeatDurationTime;

    @BindView(R.id.glRepeatPartDaysGrid)GridLayout glRepeatPartDaysGrid;
    @BindView(R.id.tvSelRepeatDays)TextView tvSelRepeatDays;
    @BindView(R.id.tvTodaysRepeatDays)TextView tvTodaysRepeatDays;

    @BindView(R.id.rlMainBookingType)RelativeLayout rlMainBookingType;


    /*******************************MultiShift********************************/

    @BindView(R.id.multipleShiftBooking)LinearLayout multipleShiftBooking;
    @BindView(R.id.tvConfirmMultiShift)TextView tvConfirmMultiShift;
    @BindView(R.id.tvConfirmStartTime)TextView tvConfirmStartTime;
    @BindView(R.id.tvConfirmStartDate)TextView tvConfirmStartDate;
    @BindView(R.id.tvConfirmEndTime)TextView tvConfirmEndTime;
    @BindView(R.id.tvConfirmEndDate)TextView tvConfirmEndDate;
    @BindView(R.id.tvToConfirm)TextView tvToConfirm;
    @BindView(R.id.tvConfirmNumberOfShift)TextView tvConfirmNumberOfShift;
    @BindView(R.id.tvConfirmShift)TextView tvConfirmShift;
    @BindView(R.id.tvConfirmAmountScheduleCharge)TextView tvConfirmAmountScheduleCharge;
    @BindView(R.id.tvEditShiftDetails)TextView tvEditShiftDetails;
    @BindView(R.id.llConfirmScheduledDays)LinearLayout llConfirmScheduledDays;

    @BindView(R.id.recyclerViewBookingType)RecyclerView recyclerViewBookingType;

    /****************************End MultiShifts************************************/

    /** Single shift **/
    @BindView(R.id.singleShiftBooking)LinearLayout singleShiftBooking;
    @BindView(R.id.tvConfirmSingleShift)TextView tvConfirmSingleShift;
    @BindView(R.id.tvEditSingleShiftDetails)TextView tvEditSingleShiftDetails;
    @BindView(R.id.tvSingleConfirmStartTime)TextView tvSingleConfirmStartTime;
    @BindView(R.id.tvSingleConfirmStartDate)TextView tvSingleConfirmStartDate;
    @BindView(R.id.tvSingleConfirmEndTime)TextView tvSingleConfirmEndTime;
    @BindView(R.id.tvSingleConfirmEndDate)TextView tvSingleConfirmEndDate;
    @BindView(R.id.tvSingleConfirmNumberOfHours)TextView tvSingleConfirmNumberOfHours;
    @BindView(R.id.llSingleConfirmScheduledDays)LinearLayout llSingleConfirmScheduledDays;
    /** **/


    @Inject
    AlertProgress alertProgress;

    @Inject SessionManagerImpl manager;


    public SingleDateTimeDialogReturn.Builder singleBuilderReturn;
    private SingleDateAndTimePickerDialog.Builder singleBuilder;
    private SimpleDateFormat simpleDateFormat;

    private long onSelectedScheduledDateTime;
    private int onSelectedScheduledDuration = 30;

    private String catId;
    private double minAmount,maxAmount;
    private ArrayList<SubCategory>subCategoryArrayList;
    private ArrayList<String>selectedDays = new ArrayList<>();
    private Category.BookingTypeAction bookingTypeAction;
    @Inject
    AppTypeface appTypeface;
    @Inject
    BookingTypePresenter.presenter presenter;
    private boolean isAnyTypeSelected = false,isNowSelected,isLaterSelected,isRepeatSelected;
    private long repeatStartDateTime,repeatEndDate;
    private int durationHour=1,durationMin;

    private ArrayList<String>allDayOfTheWeek = new ArrayList<>();
    HashMap<Integer,String> dayMap = new HashMap<>();
    ArrayList<String> maxNameOfDays = new ArrayList<>();
    ArrayList<Offers> offers;
    int numOfShifts = 0;
    private boolean isCustomSelected = false;
    private String currentDay;
    private boolean durationSet=false;
    private boolean durationActivityCalled=false;
    private Date schedulebookingDate;
    private SimpleDateFormat timeFormat=new SimpleDateFormat("h:mm a");
    private SimpleDateFormat dateFormat=new SimpleDateFormat("d-MMMM-yyyy");
    private SimpleDateFormat simpleDateOldFormat=new SimpleDateFormat("MMMM d, yyyy',' h:mm a",Locale.getDefault());;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_type);
        ButterKnife.bind(this);
        Constants.bookingTypeNowSchedule = 0;
        getIntentValue();
        typefaceSet();
        toolBarSetting();
        setTheViewAccordingly();
        multipleShiftTypeFace();

    }

    private void multipleShiftTypeFace() {

        tvConfirmStartTime.setTypeface(appTypeface.getHind_light());
        tvConfirmStartDate.setTypeface(appTypeface.getHind_light());
        tvConfirmEndTime.setTypeface(appTypeface.getHind_light());
        tvConfirmEndDate.setTypeface(appTypeface.getHind_light());
        tvToConfirm.setTypeface(appTypeface.getHind_light());
        tvConfirmShift.setTypeface(appTypeface.getHind_light());
        tvConfirmAmountScheduleCharge.setTypeface(appTypeface.getHind_light());
        tvConfirmNumberOfShift.setTypeface(appTypeface.getHind_semiBold());
        tvEditShiftDetails.setTypeface(appTypeface.getHind_regular());
        tvConfirmMultiShift.setVisibility(View.GONE);
        tvEditShiftDetails.setVisibility(View.VISIBLE);



        tvConfirmSingleShift.setTypeface(appTypeface.getHind_light());
        tvEditSingleShiftDetails.setTypeface(appTypeface.getHind_light());
        tvSingleConfirmStartTime.setTypeface(appTypeface.getHind_light());
        tvSingleConfirmStartDate.setTypeface(appTypeface.getHind_light());
        tvSingleConfirmEndTime.setTypeface(appTypeface.getHind_light());
        tvSingleConfirmEndDate.setTypeface(appTypeface.getHind_light());
        tvSingleConfirmNumberOfHours.setTypeface(appTypeface.getHind_semiBold());

    }

    private void setTheViewAccordingly()
    {
        if(!bookingTypeAction.isNow() && !bookingTypeAction.isSchedule() && !bookingTypeAction.isRepeat())
        {
            tvBookTypeSave.setVisibility(View.GONE);
        }
        if(bookingTypeAction.isNow() && bookingTypeAction.isSchedule() && bookingTypeAction.isRepeat())
        {
            glNowGrid.setVisibility(View.VISIBLE);
            glLaterGrid.setVisibility(View.VISIBLE);
            glRepeatGrid.setVisibility(View.VISIBLE);
            nowSelected();

        }else if(bookingTypeAction.isNow() && bookingTypeAction.isSchedule())
        {
            glNowGrid.setVisibility(View.VISIBLE);
            glLaterGrid.setVisibility(View.VISIBLE);
            nowSelected();
        }else if(bookingTypeAction.isSchedule() && bookingTypeAction.isRepeat())
        {
            glLaterGrid.setVisibility(View.VISIBLE);
            glRepeatGrid.setVisibility(View.VISIBLE);
            laterSelected();
        }else if(bookingTypeAction.isNow() &&  bookingTypeAction.isRepeat())
        {
            glNowGrid.setVisibility(View.VISIBLE);
            glRepeatGrid.setVisibility(View.VISIBLE);
            nowSelected();
        }else if(bookingTypeAction.isNow())
        {
            glNowGrid.setVisibility(View.VISIBLE);
            nowSelected();
        }
        else if(bookingTypeAction.isSchedule())
        {
            glLaterGrid.setVisibility(View.VISIBLE);
            laterSelected();
        }
        else if(bookingTypeAction.isRepeat())
        {
            glRepeatGrid.setVisibility(View.VISIBLE);
            repeatSelected();
        }

        setOfferAdapter();

    }

    private void setOfferAdapter() {
        if(bookingTypeAction.isSchedule()) {
            if(offers.size()>0) {
                recyclerViewBookingType.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BookingType.this,LinearLayoutManager.HORIZONTAL,false);
                OfferAdapter offerAdapter = new OfferAdapter(this,offers);
                recyclerViewBookingType.setLayoutManager(linearLayoutManager);
                recyclerViewBookingType.setAdapter(offerAdapter);
            }
        }
    }

    private void getIntentValue() {
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            bookingTypeAction = (Category.BookingTypeAction) bundle.getSerializable("BookingTypeAction");
            subCategoryArrayList = (ArrayList<SubCategory>)bundle.getSerializable("SubCat");
            offers = (ArrayList<Offers>) bundle.getSerializable("OFFERS");
            catId = bundle.getString("CatId");
            minAmount = bundle.getDouble("MinAmount",0);
            maxAmount = bundle.getDouble("MaxAmount",0);
            Constants.catId = catId;

        }
        this.simpleDateFormat = new SimpleDateFormat("d-MMMM-yyyy',' h:mm a", Locale.getDefault());
        singleBuilderReturn = new SingleDateTimeDialogReturn.Builder(this);
    }


    private void toolBarSetting() {
        tv_center.setText(Constants.catName);//getString(R.string.bookingType)
        setSupportActionBar(toolBookingType);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBookingType.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolBookingType.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @OnClick(R.id.tvBookTypeSave)
    public void onSaveClicked()
    {
        //  Constants.serverTime = Utility.dateInTwentyFour(0);
        Constants.serverTime = System.currentTimeMillis();
        if(isAnyTypeSelected)
        {
            Constants.scheduledDate = onSelectedScheduledDateTime+"";
            Constants.scheduledTime = onSelectedScheduledDuration;
            Constants.onRepeatEnd = repeatEndDate;
            Constants.onRepeatDays = selectedDays;
            Constants.selectedDate = tvTodaysDate.getText().toString().trim();
            Constants.selectedDuration = tvTodaysDuration.getText().toString().trim();

            if(isRepeatSelected)
            {
                if(numOfShifts>1)
                    BookingNxtIntent();
                else
                    alertProgress.alertinfo(this,getString(R.string.shift_requirement));
            }else
                BookingNxtIntent();
        }else if(durationSet==false && durationActivityCalled==true && isRepeatSelected ){
            alertProgress.alertinfo(this,getString(R.string.for_multiple_shift_minimum_two));
        }else {

            alertProgress.alertinfo(this, "Please select date and time");
        }
    }

    public void BookingNxtIntent()
    {
        Intent intent;
        if(Constants.serviceType==2)
        {
            intent = new Intent(this, ProviderList.class);
            intent.putExtra("CatId",catId);
            intent.putExtra("SubCat",subCategoryArrayList);
            intent.putExtra("MinAmount",minAmount);
            intent.putExtra("MaxAmount",maxAmount);
            startActivity(intent);
        }else if(Constants.serviceType == 1)
        {

            if(Constants.bookingModel==3)
            {
                if(manager.getGuestLogin())
                    intent = new Intent(this, LoginActivity.class);
                else
                    intent = new Intent(this, ConfirmBookActivity.class);
            }else
                intent = new Intent(this, AddToCart.class);

            startActivity(intent);
        }else
            Toast.makeText(this,"Bidding ",Toast.LENGTH_SHORT).show();

        overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
    }

    @OnClick({ // Date part time and duration //R.id.glLaterPartGrid,R.id.glLaterPartDurationGrid,R.id.glLaterPartTimeGrid,
            R.id.glNowPartTimeGrid,R.id.tvEditShiftDetails,R.id.tvEditSingleShiftDetails})//R.id.glLaterPartTimeGrid,
    public void onScheduledClicked(View v)
    {
        switch (v.getId())
        {
            case R.id.glNowPartTimeGrid:
                showDurationDialog(false,true);
                //    callIntent(false,true,"");
                break;
            /*case R.id.glLaterPartGrid: //Date
                presenter.openDatePicker(this,true);
                break;
            case R.id.glLaterPartTimeGrid: //time
                presenter.calltimepicker(this,true);
                break;
            case R.id.glLaterPartDurationGrid: // duration
                onSelectedDuration(false);
                //  callIntent(true,false,"");
                break;*/
            case R.id.singleShiftBooking:
            case R.id.tvEditSingleShiftDetails:// edit
                presenter.openDatePicker(this,true);
                //  callIntent(true,false,"");
                break;
            case R.id.tvEditShiftDetails:
                selectedDays.clear();
                setShiftVisibility(true);
                break;

        }
    }

    private void setShiftVisibility(boolean b) {
        if(b)
        {
            glRepeatPartGrid.setVisibility(View.VISIBLE);
            multipleShiftBooking.setVisibility(View.GONE);
            CallRepeatDateTimeIntent(true);
        }else
        {
            multipleShiftBooking.setVisibility(View.VISIBLE);
            glRepeatPartGrid.setVisibility(View.GONE);
            glRepeatPartDaysGrid.setVisibility(View.GONE);
            glRepeatPartTimeGrid.setVisibility(View.GONE);
            glRepeatPartDurationGrid.setVisibility(View.GONE);
        }
    }

    private void onSelectedDuration(boolean isRepeat)
    {
        //showDialogFragment(isRepeat);
        showDurationDialog(isRepeat,false);

    }

    private void showDurationDialog(boolean isRepeat,boolean isNowBooking) {
        final Dialog dialog =new Dialog(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.duration_picker,rlMainBookingType,false);
        final NumberPicker month=  inflate.findViewById(R.id.numberPickerMonth);
        final NumberPicker year=  inflate.findViewById(R.id.numberPickerYear);
        final TextView tvMonthDialog=  inflate.findViewById(R.id.dialogMonth);
        final TextView tvYearDialog=  inflate.findViewById(R.id.dialogYear);
        final TextView tvSelectDuration=  inflate.findViewById(R.id.tvSelectDuration);
        Button done=  inflate.findViewById(R.id.done);
        tvSelectDuration.setTypeface(appTypeface.getHind_semiBold());
        if(isRepeat) {
            tvSelRepeatDuration.setText(R.string.shift_Duration);
            tvSelectDuration.setText(R.string.shift_Duration);
        }else
            tvSelRepeatDuration.setText(R.string.job_duration);
        done.setTypeface(appTypeface.getHind_semiBold());
        String[] strings = {"00"};//,"30"
        month.setMinValue(1);
        month.setValue(1);
        durationHour = month.getValue();
        tvMonthDialog.setText(""+month.getValue());

       // Log.d(TAG, "showDurationDialog: "+month.getValue() +"  "+durationHour);
        month.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if(newVal==0){
                picker.setValue(1);
                newVal=1;
            }
            durationHour = newVal;
            tvMonthDialog.setText(""+newVal);
        });
        year.setOnValueChangedListener((picker, oldVal, newVal) -> {
            durationMin = Integer.parseInt(strings[newVal]);
            tvYearDialog.setText(""+durationMin);
        });

        year.setMinValue(0);
        year.setMaxValue(strings.length-1);
        year.setDisplayedValues(strings);
        month.setMaxValue(23);
        //month.setMinValue(0);
        dialog.setCancelable(false);
        done.setOnClickListener(v -> {
            if(durationHour!=0 || durationMin!=0)
            {
                if(isNowBooking)
                {
                    String hours = durationHour +" hr : "+durationMin +" mn";
                    tvNowTodaysTime.setText(hours);
                    isNowSelected = true;
                    isAnyTypeSelected = true;
                }else {
                    String hours = durationHour +" hr : "+durationMin +" mn";
                    tvTodaysDuration.setText(hours);
                    tvSelRepeatDurationTime.setText(hours);
                    if(isRepeat) {
                        isAnyTypeSelected = true;
                        isRepeatSelected = true;
                        setShiftVisibility(false);
                        setValueOnTheShifts();
                    } else {
                        calculateAndSetEndDate(durationHour,durationMin);
                        isAnyTypeSelected = true;
                        isNowSelected = false;
                        isLaterSelected = true;
                        isRepeatSelected = false;
                    }
                }
                onSelectedScheduledDuration = (durationHour*60)+durationMin;
                dialog.dismiss();
            }
            else
                Toast.makeText(BookingType.this,getString(R.string.minimumBookingDuration),Toast.LENGTH_SHORT).show();

        });
        dialog.setContentView(inflate);
        dialog.show();
    }

    private void calculateAndSetEndDate(long durationHr,long durationMin) {
        Log.d(TAG, "calculateAndSetEndDate: durationhr,durationmin"+durationHr+","+durationMin);
        Calendar tempEndDate=Calendar.getInstance();
        tempEndDate.setTime(schedulebookingDate);
        tempEndDate.setTimeInMillis(schedulebookingDate.getTime()+TimeUnit.HOURS.toMillis(durationHr)+TimeUnit.MINUTES.toMillis(durationMin));
        String dateSel = simpleDateFormat.format(tempEndDate.getTime());
        String splitDate[] = dateSel.split(",");
        String selDateTime = splitDate[0]+", "+splitDate[1];
        tvEditSingleShiftDetails.setVisibility(View.VISIBLE);
        tvSingleConfirmEndTime.setText(timeFormat.format(tempEndDate.getTime()));
        if(schedulebookingDate.getDate()<tempEndDate.get(Calendar.DATE)){
            selDateTime=selDateTime+"(+1)";
        }
        tvSingleConfirmEndDate.setText(dateFormat.format(tempEndDate.getTime()));
        tvSingleConfirmNumberOfHours.setText(String.format("%s:%s", String.format("%02d", durationHour), String.format("%02d", durationMin)));
    }

    private void setValueOnTheShifts() {
        tvConfirmStartTime.setTypeface(appTypeface.getHind_light());
        tvConfirmStartDate.setTypeface(appTypeface.getHind_light());
        tvConfirmEndTime.setTypeface(appTypeface.getHind_light());
        tvConfirmEndDate.setTypeface(appTypeface.getHind_light());
        Log.d(TAG, "setValueOnTheShifts: "+tvTodaysRepeatStartDate.getText().toString());
        String splitDate[] = tvTodaysRepeatStartDate.getText().toString().split(" : ");
        Date parse=new Date();
        try {
            parse = simpleDateFormat.parse(splitDate[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvConfirmStartDate.setText(dateFormat.format(parse.getTime()));
        tvConfirmEndDate.setText(splitDate[1]);
        tvConfirmStartTime.setText(tvRepeatStartTime.getText().toString());
        Log.d(TAG, "setValueOnTheShifts: dateforendtime"+dateForEndTime.getTime());
        long endTime = (durationHour*60 +durationMin)*60;
        Calendar instance = Calendar.getInstance();
        Calendar cDateforEndtime = Calendar.getInstance();
        cDateforEndtime.setTimeInMillis(dateForEndTime.getTime());
        Calendar repeatEndTime=Calendar.getInstance();
        instance.setTimeInMillis(repeatEndDate*1000);
        repeatEndTime.set(instance.get(Calendar.YEAR),instance.get(Calendar.MONTH),instance.get(Calendar.DATE),cDateforEndtime.get(Calendar.HOUR_OF_DAY),cDateforEndtime.get(Calendar.MINUTE));
        Log.d(TAG, "setValueOnTheShifts:repeatEndTime.gettime "+repeatEndTime.getTime().getTime());
        long endTimes = ((repeatEndTime.getTimeInMillis())+(endTime*1000));
        Date date = new Date(endTimes);
        tvConfirmEndTime.setText(timeFormat.format(date.getTime()));
        tvConfirmEndDate.setText(dateFormat.format(repeatEndDate*1000));
        llConfirmScheduledDays.removeAllViews();
        for(int i = 0; i<selectedDays.size();i++) {
            TextView tvWeekDays;
            View view  = LayoutInflater.from(this).inflate(R.layout.booking_dates,multipleShiftBooking,false);
            llConfirmScheduledDays.addView(view);
            tvWeekDays = view.findViewById(R.id.tvWeekDays);
            tvWeekDays.setTypeface(appTypeface.getHind_light());
            String valueSelected = selectedDays.get(i).charAt(0)+"";
            tvWeekDays.setText(valueSelected);
        }
        String numberOfShift = numOfShifts+"";
        tvConfirmNumberOfShift.setText(numberOfShift);

        Constants.repeatDays.clear();
        Constants.repeatStartTime = tvConfirmStartTime.getText().toString();
        Constants.repeatStartDate = tvConfirmStartDate.getText().toString();
        Constants.repeatEndTime = tvConfirmEndTime.getText().toString();
        Constants.repeatEndDate = tvConfirmEndDate.getText().toString();
        Constants.repeatNumOfShift = numOfShifts;
        Constants.repeatDays.addAll(selectedDays);
        Constants.bookingOffer = 0;
        offersValue();
    }

    private void offersValue() {
        if(offers.size()>0) {
            Collections.sort(offers, new SortByMinOffer());

            for(int i = 0 ; i<offers.size();i++)
            {
                if(numOfShifts>=offers.get(i).getMinShiftBooking() && numOfShifts<=offers.get(i).getMaxShiftBooking()) {
                    Constants.bookingOffer = offers.get(i).getValue();
                    Constants.offerType = offers.get(i).getDiscountType();
                    Constants.offerName = offers.get(i).getTitle();
                }
            }
            Log.d(TAG, "offersValue: "+Constants.bookingOffer+" offerType "+Constants.offerType
                    +"NumerOf "+numOfShifts);
        }
    }

    @OnClick({R.id.glRepeatPartGrid,R.id.glRepeatPartDurationGrid,
            R.id.glRepeatPartDaysGrid,R.id.glRepeatPartTimeGrid})//R.id.glRepeatPartEndDateGrid,
    public void onRepeatClicked(View v)
    {

        switch (v.getId())
        {
            case R.id.glRepeatPartGrid:
                //presenter.openDatePicker(this,false);
                CallRepeatDateTimeIntent(false);

                break;
           /* case R.id.glRepeatPartEndDateGrid:
                presenter.onOpenEndDate(date,this);
                break;*/
            case R.id.glRepeatPartTimeGrid:
                // presenter.calltimepicker(this,false);
                onSelectTime(false,startDates);
                break;
            case R.id.glRepeatPartDurationGrid:
                //   onSelectedDuration(true);
                //  callIntent(false,"");
                break;
            case R.id.glRepeatPartDaysGrid:
                selectDays();
                break;

        }
    }

    private void CallRepeatDateTimeIntent(boolean isEdit) {


        Intent intent = new Intent(this,RepeatDateTime.class);
        intent.putExtra("isEditTrue",isEdit);
        startActivityForResult(intent,Constants.REPEAT_RESULT_CODE);
        overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
    }

    private void selectDays()
    {
        allDayOfTheWeek.clear();
        if(dayMap.size()>0)
        {
            ArrayList<Integer> keyset = new ArrayList<>(dayMap.keySet());

            for(int i = 0; i<keyset.size();i++)
            {
                allDayOfTheWeek.add(dayMap.get(keyset.get(i)));
            }
            Intent intent = new Intent(this,RepeatDays.class);
            intent.putExtra("SelectedDays",selectedDays);
            intent.putExtra("allDaysOfTheWeek",allDayOfTheWeek);
            startActivityForResult(intent,4);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Constants.filteredAddress = "";
        Constants.filteredLat = 0;
        Constants.filteredLng = 0;
    }

    private void typefaceSet() {
        tvBookingTypeWhenYOu.setTypeface(appTypeface.getHind_medium());
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        tvBookTypeSave.setTypeface(appTypeface.getHind_semiBold());
        tvNow.setTypeface(appTypeface.getHind_medium());
        tvNowDesc.setTypeface(appTypeface.getHind_light());
        tvNowDecSelected.setTypeface(appTypeface.getHind_medium());
        tvLater.setTypeface(appTypeface.getHind_medium());
        tvLaterDesc.setTypeface(appTypeface.getHind_light());
        tvRepeat.setTypeface(appTypeface.getHind_medium());
        tvRepeatDesc.setTypeface(appTypeface.getHind_light());

        tvNowSelTime.setTypeface(appTypeface.getHind_medium());
        tvNowTodaysTime.setTypeface(appTypeface.getHind_medium());

        tvRepeatSelTime.setTypeface(appTypeface.getHind_medium());
        tvRepeatStartTime.setTypeface(appTypeface.getHind_medium());

        tvSelDate.setTypeface(appTypeface.getHind_medium());
        tvTodaysDate.setTypeface(appTypeface.getHind_medium());
        tvSelTime.setTypeface(appTypeface.getHind_medium());
        tvTodaysTime.setTypeface(appTypeface.getHind_medium());
        tvSelDuration.setTypeface(appTypeface.getHind_medium());
        tvTodaysDuration.setTypeface(appTypeface.getHind_medium());

        tvSelRepeatStartDate.setTypeface(appTypeface.getHind_medium());
        tvTodaysRepeatStartDate.setTypeface(appTypeface.getHind_medium());
        //    tvSelRepeatEndDate.setTypeface(appTypeface.getHind_medium());
        //    tvTodaysRepeatEndDate.setTypeface(appTypeface.getHind_medium());
        tvSelRepeatDuration.setTypeface(appTypeface.getHind_medium());
        tvSelRepeatDurationTime.setTypeface(appTypeface.getHind_medium());
        tvSelRepeatDays.setTypeface(appTypeface.getHind_medium());
        tvTodaysRepeatDays.setTypeface(appTypeface.getHind_medium());

    }

    @OnClick({R.id.glNowGrid,R.id.glLaterGrid,R.id.glRepeatGrid})
    public void onClickGrid(View view) {
        switch (view.getId())
        {
            case R.id.glNowGrid:
                nowSelected();
                break;
            case R.id.glLaterGrid:
                laterSelected();
                break;
            case R.id.glRepeatGrid:
                repeatSelected();
                break;
        }
    }

    private void nowSelected()
    {
        if(!tvNow.isSelected()){
            currentDay = "";
            isAnyTypeSelected = true;
            isNowSelected = false;
            isLaterSelected = false;
            isRepeatSelected = false;
            onSelectedScheduledDuration = 0;
            onSelectedScheduledDateTime = 0;
            tvNowTodaysTime.setText("--------");
            onViewSet(1);
            onViewSelected(1);

        }

    }
    private void laterSelected()
    {
        if(!tvLater.isSelected()){
            currentDay = "";
            onSelectedScheduledDuration = 0;
            onSelectedScheduledDateTime = 0;
            isAnyTypeSelected = false;
            isNowSelected = false;
            isLaterSelected = false;
            isRepeatSelected = false;
            tvTodaysDate.setText("--------");
            tvTodaysDuration.setText("--------");
            tvTodaysTime.setText("--------");
            onViewSet(2);
            onViewSelected(2);
            presenter.openDatePicker(this,true); // 2nd param isschedule
        }

    }
    private void repeatSelected()
    {if(!tvRepeat.isSelected()){
        currentDay = "";
        isAnyTypeSelected = false;
        onSelectedScheduledDuration = 0;
        onSelectedScheduledDateTime = 0;
        tvTodaysRepeatStartDate.setText("--------");
        tvSelRepeatDurationTime.setText("--------");
        tvRepeatStartTime.setText("--------");
        tvTodaysRepeatDays.setText("--------");
        isNowSelected = false;
        isLaterSelected = false;
        isRepeatSelected = false;
        onViewSet(3);
        onViewSelected(3);
        CallRepeatDateTimeIntent(false);
    }
    }

    private void onViewSelected(int i) {
        tvNowDecSelected.setVisibility(View.GONE);
        spaceNow.setVisibility(View.GONE);
        glLaterPartGrid.setVisibility(View.GONE);
        glNowPartTimeGrid.setVisibility(View.GONE);
        glLaterPartDurationGrid.setVisibility(View.GONE);
        glRepeatPartGrid.setVisibility(View.GONE);
        glRepeatPartDaysGrid.setVisibility(View.GONE);
        glRepeatPartDurationGrid.setVisibility(View.GONE);
        glLaterPartTimeGrid.setVisibility(View.GONE);
        glRepeatPartTimeGrid.setVisibility(View.GONE);
        multipleShiftBooking.setVisibility(View.GONE);
        singleShiftBooking.setVisibility(View.GONE);

        switch (i) {
            case 1:
                if(Constants.jobType == 2)
                    tvNowDecSelected.setVisibility(View.GONE);
                spaceNow.setVisibility(View.GONE);
                //  glNowPartTimeGrid.setVisibility(View.VISIBLE);
                Constants.bookingType = 1;
                break;
            case 2:
                //glLaterPartGrid.setVisibility(View.VISIBLE);
                singleShiftBooking.setVisibility(View.VISIBLE);
                Constants.bookingType = 2;
                break;
            case 3:
               // glRepeatPartGrid.setVisibility(View.VISIBLE);
                Constants.bookingType = 3;
                multipleShiftBooking.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void onViewSet(int i)
    {
        ivNowCheck.setSelected(false);
        ivLaterCheck.setSelected(false);
        ivRepeatCheck.setSelected(false);
        tvNow.setSelected(false);tvNowDesc.setSelected(false);
        tvLater.setSelected(false);tvLaterDesc.setSelected(false);
        tvRepeat.setSelected(false);tvRepeatDesc.setSelected(false);
        switch (i)
        {
            case 1:
                ivNowCheck.setSelected(true);
                tvNow.setSelected(true);
                tvNowDesc.setSelected(true);
                Constants.bookingType = 1;
                break;
            case 2:
                ivLaterCheck.setSelected(true);
                tvLater.setSelected(true);
                tvLaterDesc.setSelected(true);
                Constants.bookingType = 2;
                break;
            case 3:
                ivRepeatCheck.setSelected(true);
                tvRepeat.setSelected(true);
                tvRepeatDesc.setSelected(true);
                Constants.bookingType = 3;
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);
    }



    @Override
    public void onDateSelected(Date time) {
        schedulebookingDate=time;
        String dateSel = simpleDateFormat.format(time.getTime());
        String splitDate[] = dateSel.split(",");
        Log.d(TAG, "onDateSelected: "+dateSel);
        String selDateTime = splitDate[0]+", "+splitDate[1];
        tvTodaysDate.setText(selDateTime);
        tvTodaysTime.setText(splitDate[1]);
        onSelectedScheduledDateTime = time.getTime()/1000;
        tvSingleConfirmStartTime.setText(timeFormat.format(time.getTime()));
        tvSingleConfirmStartDate.setText(dateFormat.format(time.getTime()));
        //glLaterPartDurationGrid.setVisibility(View.VISIBLE);
        //glLaterPartTimeGrid.setVisibility(View.VISIBLE);
        onSelectedDuration(false);

        //   callIntent(true,false,"");
    }

    Date dateForEndTime;
    @Override
    public void onRepeatDateSelected(Date time) {
        dateForEndTime = time;
        //String dateSel = simpleDateFormat.format(time.getTime());
        //String splitDate[] = dateSel.split(",");
        tvRepeatStartTime.setText(timeFormat.format(time.getTime()));
        glRepeatPartDurationGrid.setVisibility(View.VISIBLE);
        glRepeatPartTimeGrid.setVisibility(View.VISIBLE);
        //  onSelectedDuration(true);
        //   callIntent(false,false,"");
    }

    @Override
    public void onRepeatEndDate(Date time) {
        String dateSel = simpleDateFormat.format(time.getTime());
        String splitDate[] = dateSel.split(",");
        glRepeatPartDurationGrid.setVisibility(View.VISIBLE);
        onSelectedDuration(true);
        //   callIntent(false,"");

    }

    Date startDates;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //
        if(requestCode == 4) //Repeat da
        {
            if(resultCode == RESULT_OK)
            {
                StringBuilder days = new StringBuilder();
                selectedDays = data.getStringArrayListExtra("SELECTEDDAys");

                if(selectedDays.size()>0)
                {

                    //isAnyTypeSelected = true;
                    isNowSelected = false;
                    isLaterSelected = false;
                    isRepeatSelected = true;

                    for(int i = 0;i<selectedDays.size();i++)
                    {
                        if(selectedDays.size()==1)
                            days.append(selectedDays.get(i));
                        else
                        {
                            if(i == selectedDays.size()-1)
                                days.append(selectedDays.get(i));
                            else
                                days.append(selectedDays.get(i)).append(", ");
                        }
                    }
                    tvTodaysRepeatDays.setText(days);
                    glRepeatPartTimeGrid.setVisibility(View.VISIBLE);
                    onSelectTime(false,startDates);
                    // i am doing
                    numOfShifts = 0;
                    for(int nod = 0; nod< maxNameOfDays.size(); nod++) {
                        if(selectedDays.contains(maxNameOfDays.get(nod))){
                            numOfShifts++;
                        }
                        /*for(int nsl = 0; nsl<selectedDays.size();nsl++) {
                            if(selectedDays.get(nsl).equals(maxNameOfDays.get(nod)))
                        }*/
                    }

                }
            }
        }else if(requestCode == Constants.REPEAT_RESULT_CODE) {
            if(resultCode == RESULT_OK)
            {
                dayMap.clear();
                String startDate =  data.getStringExtra("StartDate");
                String endDate = data.getStringExtra("EndDate");
                isCustomSelected = data.getBooleanExtra("isCustomSelected",false);

                glRepeatPartDaysGrid.setVisibility(View.VISIBLE);

                try {
                    startDates = simpleDateOldFormat.parse(startDate);
                    Date end = simpleDateOldFormat.parse(endDate);
                    String startDateNewFormatted = simpleDateFormat.format(startDates);
                    String endDateNewFormatted = simpleDateFormat.format(end);
                    presenter.listOfDates(Utility.getDatesBetweenUsingJava7(startDates,end));
                    String startDatesAre[] = startDateNewFormatted.split(",");
                    String endDatesAre[] =  endDateNewFormatted.split(",");
                    String dateAre = startDatesAre[0]+", "+startDatesAre[1]+" : "+endDatesAre[0]+", "+endDatesAre[1];
                    tvTodaysRepeatStartDate.setText(dateAre);
                    repeatEndDate = end.getTime()/1000;
                    onSelectedScheduledDateTime = startDates.getTime()/1000;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
        else if(requestCode == Constants.TIME_RESULT_CODE) {
            if(resultCode == RESULT_OK) {
                boolean mParamIsSchedule;
                durationSet=true;
                boolean isNowBookingSele = data.getBooleanExtra("isNow",false);
                mParamIsSchedule =  data.getBooleanExtra("isSchedule",false);
                durationHour = data.getIntExtra("durationHour",0);
                durationMin = data.getIntExtra("durationMin",0);
                long selectedTimestamp = data.getLongExtra("selectedTimestamp",0);
                Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(selectedTimestamp);
                checkIfCurrentDayAvailable(calendar);
                String dateTIme = data.getStringExtra("SelectedTime");

                Log.d(TAG, "onActivityResult: "+dateTIme +"\nisNow "+isNowBookingSele+"\nisSchedule "
                        +mParamIsSchedule+"\nduration "+durationHour+"\ndurationMin "+durationMin+" datetime:"+dateTIme);

                String hours = durationHour +" hr : "+durationMin +" mn";
                onSelectedScheduledDuration = (durationHour*60)+durationMin;
                if(isNowBookingSele) {
                    tvNowTodaysTime.setText(hours);
                    isNowSelected = true;
                    isAnyTypeSelected = true;
                }else {
                    try {
                        Date dateTImeFromat = simpleDateOldFormat.parse(dateTIme);
                        onFragmentInteraction(dateTImeFromat,mParamIsSchedule);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    tvTodaysDuration.setText(hours);
                    tvSelRepeatDurationTime.setText(hours);
                    if(!mParamIsSchedule)
                    {
                        isAnyTypeSelected = true;
                        isRepeatSelected = true;
                        setShiftVisibility(false);
                        setValueOnTheShifts();

                    }
                    else
                    {
                        isAnyTypeSelected = true;
                        isNowSelected = false;
                        isLaterSelected = true;
                        isRepeatSelected = false;
                    }
                }
            }
        }
    }

    private void checkIfCurrentDayAvailable(Calendar selectedTime) {
        Calendar currentTime=Calendar.getInstance();
        Calendar currentTimeWithBuffer=Calendar.getInstance();
        currentTimeWithBuffer.setTimeInMillis(currentTime.getTimeInMillis()+ TimeUnit.HOURS.toMillis(1));
        Log.d(TAG, "checkIfCurrentDayAvailable: currenttimewithBuffer:"+currentTimeWithBuffer.getTime()+ " selectedtime: "+selectedTime.getTime());
        if(selectedTime.before(currentTimeWithBuffer) && selectedDays.contains(Utility.dayOfTheWeek(selectedTime.get(Calendar.DAY_OF_WEEK)))){
            Log.d(TAG, "checkIfCurrentDayAvailable: remove currentDay" );
            numOfShifts--;
            if(maxNameOfDays.size()<=7){
                selectedDays.remove(currentDay);
            }
        }else{
            Log.d(TAG, "checkIfCurrentDayAvailable: include  currentDay" );
        }
    }

    @Override
    public void setDateOnMap(ArrayList<Integer> dayInArray) {


        maxNameOfDays.clear();
        for(int i = 0; i<dayInArray.size();i++)
        {
            Log.d(TAG, "setDateOnMap: "+dayInArray.get(i));
            dayMap.put(dayInArray.get(i),Utility.dayOfTheWeek(dayInArray.get(i)));
            maxNameOfDays.add(Utility.dayOfTheWeek(dayInArray.get(i)));
        }
        Log.d(TAG, "setDateOnMap: "+maxNameOfDays);
        if(!isCustomSelected)
        {
            currentDay = Utility.dayOfTheWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        }

        selectDays();
    }

    @Override
    public void dateSelectionCancelled() {
        tvEditSingleShiftDetails.setText("Add");
    }

    @Override
    public void setTextAsEdit() {
        tvEditSingleShiftDetails.setText("Edit");
    }


    @Override
    public void onRepeatEndDateTime(String time, String date, String dateTime) {

        //tvTodaysRepeatEndDate.setText(dateTime);
    }



    @Override
    public void onSelectTime(boolean isScheduled, Date calendar) {
        // i am doing
        String time = simpleDateFormat.format(calendar.getTime());
        Log.d(TAG, "onSelectTime: "+time);
        if(isScheduled)
        {
            DialogTimeFragment dialogTimeFragment = DialogTimeFragment.newInstance(true,time,
                    isCustomSelected,selectedDays,currentDay);
            dialogTimeFragment.setCancelable(false);
            dialogTimeFragment.show(getSupportFragmentManager(),"Timer");
        }else
            callIntent(time);



    }

    private void callIntent(String time) {

        //isCustomSelected,selectedDays,currentDay
        Intent intent = new Intent(this,SelectTimeAndDuration.class);
        Bundle args = new Bundle();
        durationActivityCalled=true;
        args.putString(SelectTimeAndDuration.ARG_PARAM1,time);
        args.putBoolean(SelectTimeAndDuration.ARG_PARAM2, false);
        args.putBoolean(SelectTimeAndDuration.ARG_PARAM_NOW, false);
        args.putBoolean(SelectTimeAndDuration.ARG_CUSTOME, isCustomSelected);
        args.putStringArrayList(SelectTimeAndDuration.ARG_SELECTED, selectedDays);
        args.putString(SelectTimeAndDuration.ARG_CurrentDay, currentDay);

        /*intent.putExtra(SelectTimeAndDuration.ARG_PARAM1,time);
        intent.putExtra(SelectTimeAndDuration.ARG_PARAM2, false);
        intent.putExtra(SelectTimeAndDuration.ARG_PARAM_NOW, false);
        intent.putExtra(SelectTimeAndDuration.ARG_CUSTOME, isCustomSelected);
        intent.putExtra(SelectTimeAndDuration.ARG_SELECTED, selectedDays);
        intent.putExtra(SelectTimeAndDuration.ARG_CurrentDay, currentDay);*/
        intent.putExtras(args);
        startActivityForResult(intent,Constants.TIME_RESULT_CODE);
        overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
    }

    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message) {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onShowProgress() {

    }

    @Override
    public void onHideProgress() {

    }

    @Override
    public void onFragmentInteraction(Date calendar,boolean isSchedule) {
        if(isSchedule)
            onDateSelected(calendar);
        else
            onRepeatDateSelected(calendar);

    }
}
