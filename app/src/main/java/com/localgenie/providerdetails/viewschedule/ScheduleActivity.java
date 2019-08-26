package com.localgenie.providerdetails.viewschedule;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.Utility;
import com.pojo.Booked;
import com.pojo.Schedule;
import com.pojo.ScheduleMonthData;
import com.pojo.ScheduleMonthPojo;
import com.pojo.Slot;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import adapters.ScheduleListAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by murashid on 23-Oct-17.
 * <h1>ScheduleActivity</h1>
 * ScheduleActivity for showing the month wise schedule
 */

public class ScheduleActivity extends DaggerAppCompatActivity implements CompactCalendarView.CompactCalendarViewListener, View.OnClickListener, ScheduleContract.ScheduleView {

    private static final String TAG = "ScheduleActivity";
    public boolean isFabOpen = false;
    @BindView(R.id.compactCalendarView)
    CompactCalendarView compactCalendarView;
    @BindView(R.id.tvMonth)
    TextView tvMonth;
    @BindView(R.id.tvNoScheduleMsg)
    TextView tvNoScheduleMsg;
    @BindView(R.id.tvViewSchedule)
    TextView tvViewSchedule;
    @BindView(R.id.tvAddSchedule)
    TextView tvAddSchedule;
    @BindView(R.id.rvShedule)
    RecyclerView rvShedule;
    @BindView(R.id.llSchedule)
    LinearLayout llSchedule;
    @Inject
    SessionManager sessionManager;
    @Inject
    ScheduleContract.SchedulePresenter presenter;
    @Inject
    AppTypeface appTypeface;
    int currentMonth ;
    int selectedMonth ;

    @Inject
    AlertProgress alertProgress;
    private SimpleDateFormat displayMonthFormat, sendingMonthFormat;
    private ScheduleListAdapter scheduleListAdapter;
    private ArrayList<Slot> slots;
    private ArrayList<Event> calendarEvents;
    private SimpleDateFormat serverFormat, displayHourFormat, displayHourFormatInBooked, displayPeriodFormat;
    // private FloatingActionButton fabAddSchedule, fabView, fabAdd;
    private Animation fade_open, fade_close, rotate_forward, rotate_backward;
    private Animation fade_half_open, fade_half_close;
    private ProgressDialog progressDialog;
    private boolean isFirstTime;
    private String currentDate = "";
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    ImageView ivPrevBtn;
    ImageView ivnextBtn;
    /*public static ScheduleActivity newInstance() {
        return new ScheduleActivity();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_schedule);
        ButterKnife.bind(this);

        init();
    }


    /**
     * init the views
     */
    private void init() {
       /* filter = new IntentFilter();
        filter.addAction(VariableConstant.INTENT_ACTION_REFRESH_BOOKING);
        filter.addAction(VariableConstant.INTENT_ACTION_CANCEL_BOOKING);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                presenter.getSchedule(AppController.getInstance().getAccountManagerHelper().getAuthToken(sessionManager.getEmail()), sendingMonthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth()), false);
            }
        };*/

        // sessionManager = SessionManager.getSessionManager(getActivity());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.gettingSchedule));
        progressDialog.setCancelable(false);

      /*  Typeface fontBold = Utility.getFontBold(getActivity());
        Typeface fontMedium = Utility.getFontMedium(getActivity());
        Typeface fontRegular = Utility.getFontRegular(getActivity());
      */
        displayMonthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        sendingMonthFormat = new SimpleDateFormat("MM-yyyy", Locale.getDefault());

        calendarEvents = new ArrayList<>();
        slots = new ArrayList<>();
        scheduleListAdapter = new ScheduleListAdapter(this, slots);

        TextView tvTitle = findViewById(R.id.tvTitle);
        ivPrevBtn = findViewById(R.id.ivPrevBtn);
        ivnextBtn = findViewById(R.id.ivnextBtn);
       /* tvMonth = findViewById(R.id.tvMonth);
        tvNoScheduleMsg = findViewById(R.id.tvNoScheduleMsg);
        TextView tvTitle = findViewById(R.id.tvTitle);
        compactCalendarView = findViewById(R.id.compactCalendarView);
        rvShedule = findViewById(R.id.rvShedule);
        ImageView ivPrevBtn = findViewById(R.id.ivPrevBtn);
        ImageView ivnextBtn = findViewById(R.id.ivnextBtn);
        fabAddSchedule = findViewById(R.id.fabAddSchedule);
        fabView = findViewById(R.id.fabView);
        fabAdd = findViewById(R.id.fabAdd);
        tvViewSchedule = findViewById(R.id.tvViewSchedule);
        tvAddSchedule = findViewById(R.id.tvAddSchedule);
        llSchedule = findViewById(R.id.llSchedule);*/

       /* fade_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        fade_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_center_to_left);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_left_to_center);
        fade_half_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_half_open);
        fade_half_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_half_close);
*/
        rvShedule.setLayoutManager(new LinearLayoutManager(this));
        rvShedule.setAdapter(scheduleListAdapter);
        tvMonth.setTypeface(appTypeface.getHind_regular());
        tvNoScheduleMsg.setTypeface(appTypeface.getHind_regular());
        tvTitle.setTypeface(appTypeface.getHind_bold());
        tvMonth.setText(displayMonthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setDayColumnNames(getResources().getStringArray(R.array.calendarDays));
        compactCalendarView.setListener(this);
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        currentMonth = month+1;

        ivPrevBtn.setOnClickListener(this);
        ivnextBtn.setOnClickListener(this);
      /*  fabAddSchedule.setOnClickListener(this);
        fabView.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        tvAddSchedule.setOnClickListener(this);
        tvViewSchedule.setOnClickListener(this);*/

        isFirstTime = true;
        presenter.getSchedule(sessionManager.getAUTH(), sendingMonthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth()), true, Constants.providerId);
        currentDate = sendingMonthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth());

    }


    @Override
    public void onSessionExpired() {
        //  Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLogout(String message) {
        alertProgress.alertPositiveOnclick(this, message, getString(R.string.logout), getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(getApplicationContext(), sessionManager);
            }
        });
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onShowProgress() {

    }

    @Override
    public void onHideProgress() {

    }

    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void onSuccessGetSchedule(ScheduleMonthPojo scheduleMonthPojo) {
        Log.d(TAG, "onSuccessGetSchedule: " + scheduleMonthPojo.getData().size());
        calendarEvents.clear();
        compactCalendarView.removeAllEvents();
        compactCalendarView.addEvents(getEvents(scheduleMonthPojo.getData(), calendarEvents));
        if (isFirstTime) {
            isFirstTime = false;
            onDayClick(new Date());
        } else {
            onDayClick(compactCalendarView.getFirstDayOfCurrentMonth());
        }
    }

    ArrayList<Event> getEvents(ArrayList<ScheduleMonthData> scheduleMonthDatas, ArrayList<Event> calendarEvents) {
        for (ScheduleMonthData scheduleMonthData : scheduleMonthDatas) {
            for (Schedule schedule : scheduleMonthData.getSchedule()) {
                Event event;
                if (schedule.getBooked().size() == 0) {
                    //color primary for created schedule
                    event = new Event(Color.parseColor("#01B5F6"), Utility.convertUTCToTimeStamp(scheduleMonthData.getDate()), schedule);
                } else {
                    if (!schedule.getBooked().get(schedule.getBooked().size() - 1).getStatus().equals(Constants.JOB_COMPLETED_RAISE_INVOICE)) {
                        //color green for upcoming and ongoing shedule booking,
                        event = new Event(Color.parseColor("#5BC24F"), Utility.convertUTCToTimeStamp(scheduleMonthData.getDate()), schedule);
                    } else {
                        //color red for completed booking
                        event = new Event(Color.parseColor("#EB4942"), Utility.convertUTCToTimeStamp(scheduleMonthData.getDate()), schedule);
                    }
                }
                calendarEvents.add(event);
            }
        }
        return calendarEvents;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivnextBtn:
                compactCalendarView.showNextMonth();
                break;

            case R.id.ivPrevBtn:
                    compactCalendarView.showPreviousMonth();
                    break;

           /* case R.id.fabAddSchedule:
                animateFAB();
                break;

            case R.id.fabAdd:
            case R.id.tvAddSchedule:
                animateFAB();
                Intent intentAdd = new Intent(getActivity(), ScheduleAddActivity.class);
                startActivity(intentAdd);
                getActivity().overridePendingTransition(R.anim.bottom_to_top, R.anim.stay);
                break;

            case R.id.fabView:
            case R.id.tvViewSchedule:
                animateFAB();
                Intent intentView = new Intent(getActivity(), ScheduleViewActivity.class);
                startActivity(intentView);
                getActivity().overridePendingTransition(R.anim.bottom_to_top, R.anim.stay);
                break;*/
        }
    }


    @Override
    public void onDayClick(Date dateClicked) {
        slots.clear();
        slots.addAll(createSlotFromSchedules(compactCalendarView.getEvents(dateClicked)));
        scheduleListAdapter.notifyDataSetChanged();

        if (slots.size() == 0) {
            tvNoScheduleMsg.setVisibility(View.VISIBLE);
        } else {
            tvNoScheduleMsg.setVisibility(View.GONE);
        }
    }

    /**
     * Create slot for Each based on booked and arrange according to the time
     *
     * @param eventList calendar event list
     * @return list of Slot
     */
    ArrayList<Slot> createSlotFromSchedules(List<Event> eventList) {
        ArrayList<Slot> slots = new ArrayList<>();
        ArrayList<Schedule> schedules = new ArrayList<>();
        displayHourFormat = new SimpleDateFormat("h:mm", Locale.US);
        displayHourFormatInBooked = new SimpleDateFormat("hh:mm a", Locale.US);
        displayPeriodFormat = new SimpleDateFormat("a", Locale.US);
        serverFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US);

        for (Event event : eventList) {
            schedules.add((Schedule) event.getData());
        }

        try {
            for (Schedule schedule : schedules) {
                Slot slotStartTime = new Slot();
                slotStartTime.setSlotHour(displayHourFormat.format(serverFormat.parse(Utility.convertUTCToServerFormat(schedule.getStartTime()))));
                slotStartTime.setSlotPeriod(displayPeriodFormat.format(serverFormat.parse(Utility.convertUTCToServerFormat(schedule.getStartTime()))));
                slotStartTime.setStartTimeStamp(schedule.getStartTime());
                slots.add(slotStartTime);
                if (schedule.getBooked().size() > 0) {
                    for (Booked booked : schedule.getBooked()) {
                        Slot bookedSlot = new Slot();
                        bookedSlot.setStatus(booked.getStatus());
                        bookedSlot.setBookingId(booked.getBookingId());
                        bookedSlot.setCustomerId(booked.getCustomerId());
                        bookedSlot.setSlotHour(displayHourFormat.format(serverFormat.parse(Utility.convertUTCToServerFormat(booked.getStart()))));
                        bookedSlot.setSlotPeriod(displayPeriodFormat.format(serverFormat.parse(Utility.convertUTCToServerFormat(booked.getStart()))));
                        bookedSlot.setSlotEndHourBooking(displayHourFormat.format(serverFormat.parse(Utility.convertUTCToServerFormat(booked.getEnd()))));
                        bookedSlot.setSlotEndPeriodBooking(displayPeriodFormat.format(serverFormat.parse(Utility.convertUTCToServerFormat(booked.getEnd()))));
                        bookedSlot.setBookedStartHour(displayHourFormatInBooked.format(serverFormat.parse(Utility.convertUTCToServerFormat(booked.getStart()))));
                        bookedSlot.setBookedEndHour(displayHourFormatInBooked.format(serverFormat.parse(Utility.convertUTCToServerFormat(booked.getEnd()))));
                        bookedSlot.setEvent(booked.getEvent());
                        bookedSlot.setCutomerName(booked.getFirstName() + " " + booked.getLastName());
                        bookedSlot.setStartTimeStamp(booked.getStart());
                        slots.add(bookedSlot);
                    }
                }
                Slot slotEndTime = new Slot();
                slotEndTime.setSlotHour(displayHourFormat.format(serverFormat.parse(Utility.convertUTCToServerFormat(schedule.getEndTime()))));
                slotEndTime.setSlotPeriod(displayPeriodFormat.format(serverFormat.parse(Utility.convertUTCToServerFormat(schedule.getEndTime()))));
                slotEndTime.setStartTimeStamp(schedule.getEndTime());
                slots.add(slotEndTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Collections.sort(slots, new Comparator<Slot>() {
            @Override
            public int compare(Slot o1, Slot o2) {
                return o1.getStartTimeStamp().compareTo(o2.getStartTimeStamp());
            }
        });

        //Remove repeated Slot time (i.e) if 5 to 6 and 6 to 8 then show only 5 6 8 no need to show 6 again
       /*

       ArrayList<Integer> removablePosition = new ArrayList<>();
        int j = 0;
        for( int i=0 ; i < slots.size()-1 ; i++)
        {
            if(slots.get(i).getSlotHour().equals(slots.get(i+1).getSlotHour()))
            {
                removablePosition.add(i);
            }
        }

        for(int i = 0 ; i < removablePosition.size() ; i++)
        {
            slots.remove((int)removablePosition.get(i) - j);
            j++;
        }*/

        return slots;
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        tvMonth.setText(displayMonthFormat.format(firstDayOfNewMonth));
        if (currentDate.equals(sendingMonthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth()))) {
            presenter.getSchedule(sessionManager.getAUTH(), sendingMonthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth()), true, Constants.providerId);
            String month = sendingMonthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth());

            String date=month;
            String[] items1 = date.split("-");
            String month1=items1[0];
            selectedMonth = Integer.parseInt(month1);
            showIconVisiblity();
        } else {
            presenter.getSchedule(sessionManager.getAUTH(), sendingMonthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth()), true, Constants.providerId);

            String month = sendingMonthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth());

            Log.i("SAITESTING","currrentmonth -->"+month);

            String date=month;
            String[] items1 = date.split("-");
            String month2=items1[0];
            selectedMonth = Integer.parseInt(month2);
            showIconVisiblity();
        }
    }

    private void showIconVisiblity()
    {
        if(selectedMonth>currentMonth)
        {
            ivPrevBtn.setVisibility(View.VISIBLE);
            compactCalendarView.shouldScrollMonth(true);
        }else{
            ivPrevBtn.setVisibility(View.GONE);
            compactCalendarView.shouldScrollMonth(false);
        }
    }
}

