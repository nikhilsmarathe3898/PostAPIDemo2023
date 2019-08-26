package com.localgenie.biddingFlow;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.bookingtype.BookingType;
import com.localgenie.bookingtype.DialogTimeFragment;
import com.localgenie.bookingtype.RepeatDays;
import com.localgenie.bookingtype.SelectTimeAndDuration;
import com.localgenie.model.Category;
import com.localgenie.model.Offers;
import com.localgenie.model.SortByMinOffer;
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
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerFragment;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyBiddingBookTypeFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyBiddingBookTypeFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyBiddingBookTypeFrag extends DaggerFragment implements BiddingContractor.BiddingDateTIme {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_BOOK_TYPE = "param1";

    // TODO: Rename and change types of parameters
    //  private String mParam1;
    private Category.BookingTypeAction bookingTypeAction;

    private OnFragmentInteractionListener mListener;

    /*Start*/

    private String TAG = BookingType.class.getSimpleName();
    @BindView(R.id.tvBookingTypeWhenYOu)TextView tvBookingTypeWhenYOu;


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

    @BindView(R.id.glRepeatPartTimeGrid)GridLayout glRepeatPartTimeGrid;
    @BindView(R.id.tvRepeatSelTime)TextView tvRepeatSelTime;
    @BindView(R.id.tvRepeatStartTime)TextView tvRepeatStartTime;

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

   /* @BindView(R.id.glRepeatPartEndDateGrid)GridLayout glRepeatPartEndDateGrid;
    @BindView(R.id.tvSelRepeatEndDate)TextView tvSelRepeatEndDate;
    @BindView(R.id.tvTodaysRepeatEndDate)TextView tvTodaysRepeatEndDate;*/

    @BindView(R.id.glRepeatPartDurationGrid)GridLayout glRepeatPartDurationGrid;
    @BindView(R.id.tvSelRepeatDuration)TextView tvSelRepeatDuration;
    @BindView(R.id.tvSelRepeatDurationTime)TextView tvSelRepeatDurationTime;

    @BindView(R.id.glRepeatPartDaysGrid)GridLayout glRepeatPartDaysGrid;
    @BindView(R.id.tvSelRepeatDays)TextView tvSelRepeatDays;
    @BindView(R.id.tvTodaysRepeatDays)TextView tvTodaysRepeatDays;

    @BindView(R.id.rlBookingQuestionFrag)RelativeLayout rlBookingQuestionFrag;


    @BindView(R.id.tvBookTypeSave)TextView tvBookTypeSave;


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
    /** **/

    @Inject
    AlertProgress alertProgress;

    @Inject
    AppTypeface appTypeface;

    @Inject
    SessionManagerImpl manager;
    Context mContext;

    /*End*/


    private boolean isAnyTypeSelected, isNowSelected,isLaterSelected,isRepeatSelected,isLater;
    private int onSelectedScheduledDuration;
    private long onSelectedScheduledDateTime;
    private SimpleDateFormat simpleDateFormat;


    private Calendar calendar;
    private TimeZone timeZone;
    private Date date;
    private long repeatStartDateTime,repeatEndDate;
    private ArrayList<String>selectedDays = new ArrayList<>();
    private int durationHour=1,durationMin;

    private ArrayList<String>allDayOfTheWeek = new ArrayList<>();
    HashMap<Integer,String> dayMap = new HashMap<>();

    ArrayList<String> maxNameOfDays = new ArrayList<>();
    int numOfShifts = 0;
    private boolean isCustomSelected = false;
    private String currentDay;
    private boolean durationSet=false;
    private boolean isDurationActivityCalled=false;
    private Date schedulebookingDate;
    private SimpleDateFormat timeFormat=new SimpleDateFormat("h:mm a");
    private SimpleDateFormat dateFormat=new SimpleDateFormat("d-MMMM-yyyy");
    private SimpleDateFormat simpleDateOldFormat=new SimpleDateFormat("MMMM d, yyyy',' h:mm a",Locale.getDefault());;


    @Inject
    public MyBiddingBookTypeFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyBiddingBookTypeFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static MyBiddingBookTypeFrag newInstance(String param1, String param2) {
        MyBiddingBookTypeFrag fragment = new MyBiddingBookTypeFrag();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_TYPE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            bookingTypeAction = (Category.BookingTypeAction) getArguments().getSerializable(ARG_BOOK_TYPE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bidding_book_type, container, false);
        ButterKnife.bind(this,view);
        typefaceSet();
        setTheViewAccordingly();
        multipleShiftTypeFace();
        this.simpleDateFormat = new SimpleDateFormat("d-MMMM-yyyy',' h:mm a",Locale.getDefault());
        return view;
    }


    private void multipleShiftTypeFace() {

        // tvConfirmMultiShift.setTypeface(appTypeface.getHind_light());
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

        //Single shift
/** Single shift **/

        tvConfirmSingleShift.setTypeface(appTypeface.getHind_light());
        tvEditSingleShiftDetails.setTypeface(appTypeface.getHind_light());
        tvSingleConfirmStartTime.setTypeface(appTypeface.getHind_light());
        tvSingleConfirmStartDate.setTypeface(appTypeface.getHind_light());
        tvSingleConfirmEndTime.setTypeface(appTypeface.getHind_light());
        tvSingleConfirmEndDate.setTypeface(appTypeface.getHind_light());
        tvSingleConfirmNumberOfHours.setTypeface(appTypeface.getHind_semiBold());
        /** **/
    }

    private void typefaceSet() {
        tvBookingTypeWhenYOu.setTypeface(appTypeface.getHind_medium());
        tvBookingTypeWhenYOu.setVisibility(View.GONE);
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
        tvSelDate.setTypeface(appTypeface.getHind_medium());
        tvTodaysDate.setTypeface(appTypeface.getHind_medium());
        tvSelTime.setTypeface(appTypeface.getHind_medium());
        tvTodaysTime.setTypeface(appTypeface.getHind_medium());
        tvSelDuration.setTypeface(appTypeface.getHind_medium());
        tvRepeatSelTime.setTypeface(appTypeface.getHind_medium());
        tvRepeatStartTime.setTypeface(appTypeface.getHind_medium());
        tvTodaysDuration.setTypeface(appTypeface.getHind_medium());
        tvSelRepeatStartDate.setTypeface(appTypeface.getHind_medium());
        tvTodaysRepeatStartDate.setTypeface(appTypeface.getHind_medium());
        /*tvSelRepeatEndDate.setTypeface(appTypeface.getHind_medium());
        tvTodaysRepeatEndDate.setTypeface(appTypeface.getHind_medium());*/
        tvSelRepeatDuration.setTypeface(appTypeface.getHind_medium());
        tvSelRepeatDurationTime.setTypeface(appTypeface.getHind_medium());
        tvSelRepeatDays.setTypeface(appTypeface.getHind_medium());
        tvTodaysRepeatDays.setTypeface(appTypeface.getHind_medium());

    }

    private void setTheViewAccordingly()
    {

        if(!bookingTypeAction.isNow() && !bookingTypeAction.isSchedule() && !bookingTypeAction.isRepeat())
        {
            tvBookTypeSave.setVisibility(View.GONE);
        }
//service_type
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

    }

    @OnClick(R.id.tvBookTypeSave)
    public void onSaveClicked()
    {
        if(mListener!=null)
        {
            if(isAnyTypeSelected)
            {
                if(isNowSelected)
                {
                    mListener.onFragmentNowLater(true,0,0);
                }else if(isLaterSelected)
                {
                    mListener.onFragmentNowLater(false,onSelectedScheduledDateTime,onSelectedScheduledDuration);
                }else
                {
                    mListener.onFragmentRepeat(onSelectedScheduledDateTime,repeatEndDate,onSelectedScheduledDuration,selectedDays);
                }
            }else {
                if(isDurationActivityCalled==true && durationSet==false && isRepeatSelected){
                    alertProgress.alertinfo(getActivity(),getString(R.string.for_multiple_shift_minimum_two));
                }else{
                    Toast.makeText(mContext,"please fill all mandatory data",Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @OnClick({R.id.glNowGrid,R.id.glLaterGrid,R.id.glRepeatGrid,R.id.tvEditShiftDetails})
    public void onClickGrid(View view)
    {
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
            case R.id.tvEditShiftDetails:
                selectedDays.clear();
                setShiftVisibility(true);
                break;
        }
    }

    private void nowSelected() {
        currentDay = "";
        isAnyTypeSelected = true;
        isNowSelected = true;
        isLaterSelected = false;
        isRepeatSelected = false;
        onSelectedScheduledDuration = 0;
        onSelectedScheduledDateTime = 0;
        tvNowTodaysTime.setText("--------");
        onViewSet(1);
        onViewSelected(1);
    }

    private void laterSelected()
    {
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
        openDatePicker(this,true);
    }
    private void repeatSelected()
    {
        currentDay = "";
        isAnyTypeSelected = false;
        onSelectedScheduledDuration = 0;
        onSelectedScheduledDateTime = 0;
        tvTodaysRepeatStartDate.setText("--------");
        //    tvTodaysRepeatEndDate.setText("--------");
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

    private void onViewSelected(int i)
    {
        tvNowDecSelected.setVisibility(View.GONE);
        spaceNow.setVisibility(View.GONE);
        glLaterPartGrid.setVisibility(View.GONE);
        glLaterPartDurationGrid.setVisibility(View.GONE);
        glRepeatPartGrid.setVisibility(View.GONE);
        //  glRepeatPartEndDateGrid.setVisibility(View.GONE);
        glRepeatPartDurationGrid.setVisibility(View.GONE);
        glRepeatPartDaysGrid.setVisibility(View.GONE);
        glLaterPartTimeGrid.setVisibility(View.GONE);
        glNowPartTimeGrid.setVisibility(View.GONE);
        glRepeatPartTimeGrid.setVisibility(View.GONE);
        multipleShiftBooking.setVisibility(View.GONE);
        singleShiftBooking.setVisibility(View.GONE);
        switch (i)
        {
            case 1:
                if(Constants.jobType==2)
                    tvNowDecSelected.setVisibility(View.GONE);
                spaceNow.setVisibility(View.GONE);
                // glNowPartTimeGrid.setVisibility(View.VISIBLE);
                Constants.bookingType = 1;
                break;
            case 2:
                //glLaterPartGrid.setVisibility(View.VISIBLE);
                singleShiftBooking.setVisibility(View.VISIBLE);
                Constants.bookingType = 2;
                break;
            case 3:
                //glRepeatPartGrid.setVisibility(View.VISIBLE);
                //multipleShiftBooking.setVisibility(View.VISIBLE);
                Constants.bookingType = 3;
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
                tvNow.setSelected(true);//tvNowDesc.setSelected(true);
                break;
            case 2:
                ivLaterCheck.setSelected(true);
                tvLater.setSelected(true);//tvLaterDesc.setSelected(true);
                break;
            case 3:
                ivRepeatCheck.setSelected(true);
                tvRepeat.setSelected(true);//tvRepeatDesc.setSelected(true);
                break;
        }
    }
    /*****************Later Booking******************/   /****************************************Repeat Booking**************************************************/
    @OnClick({R.id.glLaterPartGrid,R.id.glLaterPartDurationGrid,  R.id.glRepeatPartGrid,R.id.glRepeatPartDurationGrid,
            R.id.glRepeatPartDaysGrid,R.id.glNowPartTimeGrid,R.id.glLaterPartTimeGrid,R.id.glRepeatPartTimeGrid,R.id.tvEditSingleShiftDetails})
    //,R.id.glRepeatPartEndDateGrid
    public void onRepeatClicked(View v)
    {
        switch (v.getId())
        {
            case R.id.glNowPartTimeGrid:
                showDurationDialog(false,true);
                break;
            case R.id.tvEditSingleShiftDetails:
                openDatePicker(this,true);
                break;
            /*case R.id.glLaterPartGrid:
                openDatePicker(this,true);
                break;
            case R.id.glLaterPartTimeGrid:
                callTimePicker(mContext,true);
                break;

            case R.id.glLaterPartDurationGrid:
                onSelectedDuration(false);
                break;*/

            case R.id.glRepeatPartGrid: //Shift date clicked
                CallRepeatDateTimeIntent(false);
                break;

            case R.id.glRepeatPartTimeGrid:
                onSelectTime(false,startDates);
                break;

            case R.id.glRepeatPartDurationGrid:
                onSelectedDuration(true);
                break;
            case R.id.glRepeatPartDaysGrid:
                selectDays();
                break;

        }
    }



    private void onSelectedDuration(boolean isRepeat)
    {
        showDurationDialog(isRepeat,false);
    }


    private void showDurationDialog(boolean isRepeat,boolean isNowBooking) {
        final Dialog dialog =new Dialog(mContext);
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.duration_picker,rlBookingQuestionFrag,false);
        final NumberPicker month=  inflate.findViewById(R.id.numberPickerMonth);
        final NumberPicker year=  inflate.findViewById(R.id.numberPickerYear);
        final TextView tvMonthDialog=  inflate.findViewById(R.id.dialogMonth);
        final TextView tvYearDialog=  inflate.findViewById(R.id.dialogYear);
        final TextView tvSelectDuration=  inflate.findViewById(R.id.tvSelectDuration);
        Button done= inflate.findViewById(R.id.done);
        String[] strings = {"00"};//,"30"
        tvSelectDuration.setTypeface(appTypeface.getHind_semiBold());
        done.setTypeface(appTypeface.getHind_semiBold());
        month.setMinValue(1);
        month.setValue(1);
        durationHour = month.getValue();
        tvMonthDialog.setText(""+month.getValue());
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
        month.setMinValue(1);
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
                } else {
                    String hours = durationHour +" hr : "+durationMin +" mn";
                    Constants.selectedDuration=hours;
                    tvTodaysDuration.setText(hours);
                    tvSelRepeatDurationTime.setText(hours);
                    if(isRepeat)
                    {
                        isAnyTypeSelected = true;
                        isRepeatSelected = true;
                        // glRepeatPartDaysGrid.setVisibility(View.VISIBLE);
                        setShiftVisibility(false);
                        setValueOnTheShifts();

                    }
                    else
                    {
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
                Toast.makeText(mContext,mContext.getString(R.string.minimumBookingDuration),Toast.LENGTH_SHORT).show();

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        ((BiddingQuestions)mContext).biddingSelected(this);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDateTimeSel(Date calendar, boolean isSchedule) {
        Log.d(TAG, "onFragmentInteraction: "+isSchedule);
        if(isSchedule)
            onDateSelected(calendar);
        else
            onRepeatDateSelected(calendar);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name

        void onFragmentNowLater(boolean isNow,long selectedScheduledDateTime,int selectedDuration);
        void onFragmentRepeat(long selectedScheduledDateTime,long selectedEndDate,int selectedDuration,
                              ArrayList<String>repeatBooking);
    }


    Date startDates;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //
        Log.d(TAG, "onActivityResult: "+requestCode +" result "+resultCode +" isTrue "+isAnyTypeSelected);
        if(requestCode == 4) // Selected all days (RepeatDays)
        {
            if(resultCode == RESULT_OK)
            {
                StringBuilder days = new StringBuilder();
                selectedDays = data.getStringArrayListExtra("SELECTEDDAys");
                Log.d(TAG, " RepeatDays repeatDaonActivityResult: selectedDays:"+selectedDays);
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

                    numOfShifts = 0;
                    for(int nod = 0; nod< maxNameOfDays.size(); nod++)
                    {
                        for(int nsl = 0; nsl<selectedDays.size();nsl++)
                        {
                            if(selectedDays.get(nsl).equals(maxNameOfDays.get(nod)))
                                numOfShifts++;
                        }
                    }
                }

            }
        }else if(requestCode == Constants.REPEAT_RESULT_CODE) {
            if(resultCode == RESULT_OK) {
                dayMap.clear();
                String startDate =  data.getStringExtra("StartDate");
                String endDate = data.getStringExtra("EndDate");
                isCustomSelected = data.getBooleanExtra("isCustomSelected",false);
                Log.d(TAG, "onActivityResult: "+startDate +" endDate "+endDate);
                glRepeatPartDaysGrid.setVisibility(View.VISIBLE);
                try {
                    startDates = simpleDateOldFormat.parse(startDate);
                    Date end = simpleDateOldFormat.parse(endDate);
                    String startDateNewFormatted = simpleDateFormat.format(startDates);
                    String endDateNewFormatted = simpleDateFormat.format(end);
                    listOfDates(Utility.getDatesBetweenUsingJava7(startDates,end));
                    String startDatesAre[] = startDateNewFormatted.split(",");
                    String endDatesAre[] =  endDateNewFormatted.split(",");
                    String dateAre = startDatesAre[0]+", "+startDatesAre[1]+" : "+endDatesAre[0]+", "+endDatesAre[1];
                    tvTodaysRepeatStartDate.setText(dateAre);
                    repeatEndDate = end.getTime()/1000;
                    onSelectedScheduledDateTime = startDates.getTime()/1000;
                    Log.d(TAG, "onActivityResult: "+repeatEndDate+" onSelectedSchedule "+onSelectedScheduledDateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else if(requestCode == Constants.TIME_RESULT_CODE) {
            if(resultCode == RESULT_OK)
            {
                boolean mParamIsSchedule;
                durationSet=true;
                boolean isNowBookingSele = data.getBooleanExtra("isNow",false);
                mParamIsSchedule =  data.getBooleanExtra("isSchedule",false);
                durationHour = data.getIntExtra("durationHour",0);
                durationMin = data.getIntExtra("durationMin",0);
                String dateTIme = data.getStringExtra("SelectedTime");
                long selectedTimestamp = data.getLongExtra("selectedTimestamp",0);
                Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(selectedTimestamp);
                checkIfCurrentDayAvailable(calendar);

                Log.d(TAG, "onActivityResult: "+dateTIme +"\nisNow "+isNowBookingSele+"\nisSchedule "
                        +mParamIsSchedule+"\nduration "+durationHour+"\ndurationMin "+durationMin);

                String hours = durationHour +" hr : "+durationMin +" mn";
                durationSet=true;
                onSelectedScheduledDuration = (durationHour*60)+durationMin;
                try {
                    Date dateTImeFromat = simpleDateOldFormat.parse(dateTIme);
                    Log.d(TAG, "onActivityResult: onDateTimeFormat: "+dateTImeFromat.getTime());
                    onDateTimeSel(dateTImeFromat,mParamIsSchedule);
                    /**
                     * Setting the long value as the selected time as this was not set after adding the time
                     * as result provider list was showing wrong.
                     */
                    onSelectedScheduledDateTime=(dateTImeFromat.getTime()/1000);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                tvTodaysDuration.setText(hours);
                tvSelRepeatDurationTime.setText(hours);
                if(!mParamIsSchedule) {
                    isAnyTypeSelected = true;
                    isRepeatSelected = true;
                    setShiftVisibility(false);
                    setValueOnTheShifts();
                }

                //  }
            }else{
                durationSet=false;
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
        Log.d(TAG, " RepeatDays: listOfDates: dayInArray:"+dayInArray);
        setDateOnMap(dayInArray);
    }

    public void setDateOnMap(ArrayList<Integer> dayInArray) {
        maxNameOfDays.clear();
        for(int i = 0; i<dayInArray.size();i++)
        {
            dayMap.put(dayInArray.get(i),Utility.dayOfTheWeek(dayInArray.get(i)));
            maxNameOfDays.add(Utility.dayOfTheWeek(dayInArray.get(i)));
            // Log.d("TAG", "onRepeatEndDateDayFor: "+dayInArray.get(i));
        }
        if(!isCustomSelected)
        {
            currentDay = Utility.dayOfTheWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        }
        selectDays();
    }

    private void selectDays() {
        allDayOfTheWeek.clear();
        if(dayMap.size()>0)
        {
            ArrayList<Integer> keyset = new ArrayList<>(dayMap.keySet());

            for(int i = 0; i<keyset.size();i++)
            {
                allDayOfTheWeek.add(dayMap.get(keyset.get(i)));
            }
            Intent intent = new Intent(mContext,RepeatDays.class);
            intent.putExtra("SelectedDays",selectedDays);
            intent.putExtra("allDaysOfTheWeek",allDayOfTheWeek);
            Log.d(TAG, " Repeatdays: selectDays: selectedDays:"+selectedDays+" alldaysoftheweek:"+allDayOfTheWeek);
            startActivityForResult(intent,4); //RepeatDays
        }

    }


    /**
     * <h1>OpenDatePicker</h1>
     * this open picker dialog for the date
     * @param myBiddingBookTypeFrag context
     * @param isLater isLater true only when this method is called for later booking
     */
    private void openDatePicker(MyBiddingBookTypeFrag myBiddingBookTypeFrag, boolean isLater)
    {
        this.isLater = isLater;
        timeZone = Utility.getTimeZone();
        calendar = Calendar.getInstance(timeZone);
        calendar.setTimeZone(timeZone);
        Calendar calendarMin = Calendar.getInstance(timeZone);
        Calendar calendarMax = Calendar.getInstance(timeZone);
        long systemCurrenttime=System.currentTimeMillis();
        long diff=Constants.diffServerTime;
        calendarMin.setTime(new Date(systemCurrenttime-diff + TimeUnit.HOURS.toMillis(1))); // Set min now
        calendarMax.setTime(new Date(systemCurrenttime-diff + TimeUnit.DAYS.toMillis(150)));
        Date minDate = calendarMin.getTime();
        Date maxDate = calendarMax.getTime();

        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(mContext, (view, year, monthOfYear, dayOfMonth) -> {
            tvEditSingleShiftDetails.setText("Edit");
            calendar.set(year,monthOfYear,dayOfMonth);
            callTimePicker(mContext,isLater);
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        TextView tv = makeTextView();
        if(isLater)
            tv.setText(R.string.select_date);
        else
            tv.setText(R.string.startDate);
        fromDatePickerDialog.setCustomTitle(tv);
        fromDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        fromDatePickerDialog.getDatePicker().updateDate(minDate.getYear(),minDate.getMonth(),minDate.getDay());
        fromDatePickerDialog.getDatePicker().setMinDate(minDate.getTime()-1000);
        fromDatePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                tvEditSingleShiftDetails.setText("Edit");
                Log.d("TAG", "onCancel: "+ " Cancelled");
            }
        });
        fromDatePickerDialog.show();
    }

    //End of selecting the date

    private void callTimePicker(Context mContext, boolean isLater)
    {
        String time = simpleDateFormat.format(calendar.getTime());
        DialogTimeFragment dialogTimeFragment = DialogTimeFragment.newInstance(isLater,time,isCustomSelected
                ,selectedDays,currentDay);
        assert getFragmentManager() != null;
        dialogTimeFragment.setCancelable(false);
        dialogTimeFragment.show(getFragmentManager(),"Timer");//getSupportFragmentManager()

    }

    public void onSelectTime(boolean isScheduled, Date calendar) {
        String time = simpleDateFormat.format(calendar.getTime());
        if(isScheduled)
        {
            DialogTimeFragment dialogTimeFragment = DialogTimeFragment.newInstance(true,time,isCustomSelected
                    ,selectedDays,currentDay);
            assert getFragmentManager() != null;
            dialogTimeFragment.show(getFragmentManager(),"Timer");
        }else {
            callIntent(time);
        }

    }

    private void callIntent(String time) {

        Intent intent = new Intent(mContext,SelectTimeAndDuration.class);
        isDurationActivityCalled=true;
        Bundle args = new Bundle();
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
        startActivityForResult(intent,Constants.TIME_RESULT_CODE); //Time And Duration
        ((Activity)mContext).overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);

    }

    public void onDateSelected(Date time) {

        schedulebookingDate=time;
        String dateSel = simpleDateFormat.format(time.getTime());
        String splitDate[] = dateSel.split(",");
        String selDateTime = splitDate[0]+", "+splitDate[1];
        tvTodaysDate.setText(selDateTime);
        tvTodaysTime.setText(splitDate[1]);
        onSelectedScheduledDateTime = time.getTime()/1000;
        tvSingleConfirmStartTime.setText(timeFormat.format(time.getTime()));
        tvSingleConfirmStartDate.setText(dateFormat.format(time.getTime()));
        //glLaterPartDurationGrid.setVisibility(View.VISIBLE);
        //glLaterPartTimeGrid.setVisibility(View.VISIBLE);
        onSelectedDuration(false);

    }
    Date dateForEndTime;
    public void onRepeatDateSelected(Date time) {
        dateForEndTime = time;

        //String dateSel = simpleDateFormat.format(time.getTime());
        //String splitDate[] = dateSel.split(",");
        tvRepeatStartTime.setText(timeFormat.format(time.getTime()));
        //String selDateTime = splitDate[0]+", "+splitDate[1];
        //   tvTodaysRepeatStartDate.setText(selDateTime);
        //   onSelectedScheduledDateTime = time.getTime()/1000;
        //    date = time;
        //  glRepeatPartEndDateGrid.setVisibility(View.VISIBLE);
        glRepeatPartTimeGrid.setVisibility(View.VISIBLE);
        glRepeatPartDurationGrid.setVisibility(View.VISIBLE);
        // onOpenEndDate(date,mContext);
        //   onSelectedDuration(true);

    }

    /*public void onOpenEndDate(Date date, Context bookingType) {

        mContext = bookingType;
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
            onRepeatEndDate(calendar.getTime());
            onSelectedDuration(true);

        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        TextView tv = makeTextView();
        tv.setText(R.string.selectEndDate);
        fromDatePickerDialog.setCustomTitle(tv);
        fromDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        fromDatePickerDialog.getDatePicker().updateDate(minDate.getYear(),minDate.getMonth(),minDate.getDay());

        fromDatePickerDialog.getDatePicker().setMinDate(minDate.getTime()-1000);
        fromDatePickerDialog.setCancelable(false);
        fromDatePickerDialog.setOnCancelListener(dialogInterface -> Log.d("TAG", "onCancel: "+ " Cancelled"));
        fromDatePickerDialog.show();

    }
*/
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

    /*public void onRepeatEndDate(Date time) {
        //    tvTodaysRepeatEndDate.setText(simpleDateFormat.format(time.getTime()).split(" ")[0]);
        glRepeatPartDurationGrid.setVisibility(View.VISIBLE);
        repeatEndDate = time.getTime()/1000;
        Log.d(TAG, "onRepeatEndDate: "+repeatEndDate);

    }*/
    private void setShiftVisibility(boolean b) {
        if(b) {
            glRepeatPartGrid.setVisibility(View.VISIBLE);
            multipleShiftBooking.setVisibility(View.GONE);
            CallRepeatDateTimeIntent(true);
        }else {
            multipleShiftBooking.setVisibility(View.VISIBLE);
            glRepeatPartGrid.setVisibility(View.GONE);
            glRepeatPartDaysGrid.setVisibility(View.GONE);
            glRepeatPartTimeGrid.setVisibility(View.GONE);
            glRepeatPartDurationGrid.setVisibility(View.GONE);
        }
    }
    private void CallRepeatDateTimeIntent(boolean isEdit) {

        Intent intent = new Intent(mContext,RepeatDateTime.class);
        intent.putExtra("isEditTrue",isEdit);
        startActivityForResult(intent,Constants.REPEAT_RESULT_CODE);
        ((Activity)mContext).overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
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

        for(int i = 0; i<selectedDays.size();i++)
        {
            TextView tvWeekDays;
            View view  = LayoutInflater.from(mContext).inflate(R.layout.booking_dates,multipleShiftBooking,false);
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
        tvEditShiftDetails.setText(R.string.edit);
        Log.d(TAG, "setValueOnTheShifts: Constants.scheduletdate:"+Constants.scheduledDate);
        Constants.bookingOffer=0;
        offersValue();
    }
    private void offersValue() {
        if(Constants.offers!=null && Constants.offers.size()>0) {
            Collections.sort(Constants.offers, new SortByMinOffer());

            for(int i = 0 ; i<Constants.offers.size();i++)
            {
                if(numOfShifts>=Constants.offers.get(i).getMinShiftBooking() && numOfShifts<=Constants.offers.get(i).getMaxShiftBooking())
                {
                    Constants.bookingOffer = Constants.offers.get(i).getValue();
                    Constants.offerType = Constants.offers.get(i).getDiscountType();
                    Constants.offerName = Constants.offers.get(i).getTitle();
                }
            }

            Log.d(TAG, "offersValue: "+Constants.bookingOffer+" offerType "+Constants.offerType
                    +"NumerOf "+numOfShifts);
        }

    }
}
