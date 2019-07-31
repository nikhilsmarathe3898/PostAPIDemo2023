package com.localgenie.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import adapters.MyBookingsPageAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

import com.localgenie.utilities.Utility;
import com.pojo.AllBookingEventPojo;

/**
 * <h>MyBookingPageFrag</h>
 * Created by Ali on 2/12/2018.
 */

public class  MyBookingPageFrag extends Fragment {
    private static final String TAG = "MyEVENTPAGEFRAG";
    private static final String PAGE_COUNT= "pagecount";
    private Context mcontext;
    private ArrayList<AllBookingEventPojo> eventsAL = new ArrayList<>();
    private MyBookingsPageAdapter adapterAssign;
    private int pageCount = 0;

    @BindView(R.id.tvMyEventNoBooking)TextView tvMyEventNoBooking;
    @BindView(R.id.tvMyEventBook)TextView tvMyEventBook;
    @BindView(R.id.rlMyEventNoBokinScheduled)RelativeLayout rlMyEventNoBokinScheduled;
    @BindView(R.id.recyclerMyEvents)RecyclerView recyclerMyEvents;

    @BindView(R.id.llMyBookingPageFarg)LinearLayout llMyBookingPageFarg;
    private HorizontalCalendar horizontalCalendar;
    @BindView(R.id.calendarView)HorizontalCalendarView calendarView;
    @BindView(R.id.fab)FloatingActionButton fab;

    ArrayList<CalendarEvent> events = new ArrayList<>();
    Calendar startDate,endDate,defaultSelectedDate;
    long fromDate,toDate;

    private OnFragmentInteractionListener mListener;
    private boolean isFirstTime = true;

    //ArrayList<AllBookingEventPojo> bookingEvents,
    public static MyBookingPageFrag newInstance(int pageValue)
    {
        Bundle args = new Bundle();
        // args.putSerializable(PENDINGALL_JOBS, bookingEvents);
        args.putInt(PAGE_COUNT,pageValue);
        MyBookingPageFrag fragment = new MyBookingPageFrag();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle=getArguments();

        pageCount = bundle.getInt(PAGE_COUNT,0);
        mcontext = getContext();
        onCreateSetDate();
    }

    private void onCreateSetDate() {
        startDate = Calendar.getInstance();
        startDate.setTimeZone(Utility.getTimeZone());
        startDate.add(Calendar.MONTH, -2);

        endDate = Calendar.getInstance();
        endDate.setTimeZone(Utility.getTimeZone());
        endDate.add(Calendar.MONTH, 2);

        defaultSelectedDate = Calendar.getInstance();
        defaultSelectedDate.setTimeZone(Utility.getTimeZone());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_bookng_page, container, false);
        ButterKnife.bind(this,view);
        // calendarView = view.findViewById(R.id.calendarView);
        initializeView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @OnClick({R.id.tvMyEventBook,R.id.fab})
    public void onBookMyEvent(View v)
    {
        switch (v.getId())
        {
            case R.id.fab:

                horizontalCalendar.goToday(false);

                break;
            case R.id.tvMyEventBook:
                Intent intent = new Intent(mcontext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }

    }


    private void initializeView() {
        LinearLayoutManager llManager = new LinearLayoutManager(mcontext);
        AppTypeface typeface = AppTypeface.getInstance(mcontext);
        int resId = R.anim.layoutanimation_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mcontext, resId);
        recyclerMyEvents.setLayoutAnimation(animation);
        recyclerMyEvents.setLayoutManager(llManager);
        adapterAssign = new MyBookingsPageAdapter(mcontext, eventsAL);
        recyclerMyEvents.setAdapter(adapterAssign);
        textValueToBeSet();
    }

    public void notifyDataAdapter(ArrayList<AllBookingEventPojo> eventData, int bookingType,boolean isFirstOrItemAdded) {
        eventsAL.clear();
        eventsAL.addAll(eventData);

        if (eventsAL.size() > 0) {
            if(rlMyEventNoBokinScheduled!=null){
                rlMyEventNoBokinScheduled.setVisibility(View.GONE);
            }
            if(recyclerMyEvents!=null)
                recyclerMyEvents.setVisibility(View.VISIBLE);
            /*if(bookingType == 2)
            {
                if(isFirstOrItemAdded)
                    addEvent();
            }*/

        } else {
            if(recyclerMyEvents!=null ){
                recyclerMyEvents.setVisibility(View.GONE);
            }
            if(rlMyEventNoBokinScheduled!=null)
                rlMyEventNoBokinScheduled.setVisibility(View.VISIBLE);
        }

        adapterAssign.notifyDataSetChanged();
        //  adapterAssign.isActivityCalled(true);

        if(bookingType == 2)
        {
            if(isFirstTime)
            {
                isFirstTime = false;
                todayDate();
            }
        }

    }
    public void isCalledActivity()
    {
        if(adapterAssign!=null)
            adapterAssign.isActivityCalled(true);
    }

    private void addEvent() {
        events.clear();
        for(int i =0 ;i<eventsAL.size();i++)
        {
            Log.d(TAG, "addEventDate: "+eventsAL.get(i).getBookingRequestedFor()*1000);
            events.add(new CalendarEvent(Color.argb(255, 141, 198, 65),(eventsAL.get(i).getBookingRequestedFor()*1000),eventsAL.get(i)));
            if(horizontalCalendar!=null)
                horizontalCalendar.setCalendarEvents(events);
        }

    }

    private void textValueToBeSet()
    {
        if(pageCount==0)
        {
            tvMyEventNoBooking.setText(getString(R.string.youHaveNoPendingEvent));
            calendarView.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }else if(pageCount==1)
        {
            tvMyEventNoBooking.setText(getString(R.string.youHaveNoUpcomingEvent));
            calendarView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            setCalendarValue();
        }else
        {
            tvMyEventNoBooking.setText(getString(R.string.youHaveNoPastEvent));
            calendarView.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }
    }
    public void todayDate()
    {
        fromDate = getStartOfDayInMillisToday();
        toDate = getEndOfDayInMillisToday();
        mListener.onDateSelectedApi(getStartOfDayInMillisToday(),getEndOfDayInMillisToday(),false);
    }
    private void setCalendarValue() {

        /* start 2 months ago from now */


        horizontalCalendar = new HorizontalCalendar.Builder(llMyBookingPageFarg, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .configure()
                .formatTopText("MMM")
                .formatMiddleText("dd")
                .formatBottomText("EEE")
                .showTopText(true)
                .showBottomText(true)
                .textColor(Color.parseColor("#3A3A3A"),Color.parseColor("#FF61B32A"))
                .colorTextMiddle(Color.parseColor("#3A3A3A"), Color.parseColor("#FF61B32A"))
                .end()
                .defaultSelectedDate(this.defaultSelectedDate)
                /* .addEvents(new CalendarEventsPredicate() {

                     Random rnd = new Random();
                     @Override
                     public List<CalendarEvent> events(Calendar date)
                     {
                         date.getTime();
                         events.add(new CalendarEvent(Color.argb(255, 141, 198, 65),date.getTimeInMillis(),eventsAL.get(0)));
                         return events;
                     }
                 })*/
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
                // String selectedDateStr =  sdf.format(date);

                String selectedDateStr = DateFormat.format("EEE, MMM d, yyyy", date).toString();
                Toast.makeText(mcontext, selectedDateStr + " selected!", Toast.LENGTH_SHORT).show();

                //  defaultSelectedDate
                horizontalCalendar.post(new Runnable() {
                    @Override
                    public void run() {

                        fromDate = getStartOfDayInMillis(date);
                        toDate = getEndOfDayInMillis(date);

                        mListener.onDateSelectedApi(fromDate,toDate,true);
                    }
                });
               /* ArrayList<AllBookingEventPojo> eventsALS = new ArrayList<>();
                for(CalendarEvent event : events)
                {
                    if(sdf.format(event.getDateInMillis()).equals(selectedDateStr))
                    {
                        Log.d(TAG, "onDateSelectedEqual: "+ event.getDateInMillis());
                        eventsALS.add((AllBookingEventPojo) event.getData());
                    }
                }
                if(eventsALS.size()>0)
                {
                    eventsAL.clear();
                    eventsAL.addAll(eventsALS);
                    rlMyEventNoBokinScheduled.setVisibility(View.GONE);
                    recyclerMyEvents.setVisibility(View.VISIBLE);
                }else
                {
                    recyclerMyEvents.setVisibility(View.GONE);
                    rlMyEventNoBokinScheduled.setVisibility(View.VISIBLE);
                }
                adapterAssign.notifyDataSetChanged();*/
            }

        });
    }

    public long getStartOfDayInMillis(Calendar date) {
        // Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));//
        Calendar calendar = Calendar.getInstance(Utility.getTimeZone());//
        calendar.setTime(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        //  if(isStartTime)
        return  calendar.getTimeInMillis();//+(24 * 60 * 60 * 1000);
       /* else
            return calendar.getTimeInMillis();*/
    }

    public long getEndOfDayInMillis(Calendar date) {
        return getStartOfDayInMillis(date) + (24 * 60 * 60 * 1000)-1000;
    }

    public long getStartOfDayInMillisToday() {
        // Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance(Utility.getTimeZone());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    public long getEndOfDayInMillisToday() {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
        return getStartOfDayInMillisToday() + (24 * 60 * 60 * 1000)-1000;
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

        void onDateSelectedApi(long fromDate,long toDate,boolean ApiCalled);
    }
}
