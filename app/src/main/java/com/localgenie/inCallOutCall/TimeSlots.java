package com.localgenie.inCallOutCall;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.pojo.SlotDetails;
import com.pojo.Slots;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;
import com.utility.RefreshToken;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import adapters.CustomScrollDateAdapter;
import adapters.SlotsTimingAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class TimeSlots extends DaggerAppCompatActivity {


    private String TAG = TimeSlots.class.getSimpleName();
    @BindView(R.id.toolBarTimeSlot)Toolbar toolBarTimeSlot;
    @BindView(R.id.tv_center)TextView tvCenter;

    @BindView(R.id.recyclerViewDate)RecyclerView recyclerViewDate;
    @BindView(R.id.scrollLeft)ImageView scrollLeft;
    @BindView(R.id.scrollRight)ImageView scrollRight;
    @BindView(R.id.recyclerViewMorning)RecyclerView recyclerViewMorning;
    @BindView(R.id.recyclerViewAfterNoon)RecyclerView recyclerViewAfterNoon;
    @BindView(R.id.recyclerViewEvening)RecyclerView recyclerViewEvening;
    @BindView(R.id.recyclerViewNight)RecyclerView recyclerViewNight;
    @BindView(R.id.llMainTimeSlots)LinearLayout llMainTimeSlots;

    @BindView(R.id.ivMyEventProPic)ImageView ivMyEventProPic;
    @BindView(R.id.tvMyEventProName)TextView tvMyEventProName;
    //  @BindView(R.id.viewStatus)View viewStatus;
    @BindView(R.id.tvMyEvnetProStatus)TextView tvMyEvnetProStatus;
    //  @BindView(R.id.rtProvidrDtls)RatingBar rtProvidrDtls;

    @BindView(R.id.llMorning)LinearLayout llMorning;
    @BindView(R.id.llAfterNoon)LinearLayout llAfterNoon;
    @BindView(R.id.llEvening)LinearLayout llEvening;
    @BindView(R.id.llNight)LinearLayout llNight;
    @BindView(R.id.llMainInfoSlots)LinearLayout llMainInfoSlots;
    @BindView(R.id.llNoSlotsAvailable)LinearLayout llNoSlotsAvailable;
    @BindView(R.id.tvSlotsNotAvailable)TextView tvSlotsNotAvailable;

    @Inject
    LSPServices lspServices;
    @Inject
    SessionManagerImpl manager;
    @Inject
    Gson gson;
    @Inject AppTypeface appTypeface;

    @Inject
    AlertProgress alertProgress;
    private AlertDialog dialog;

    private HorizontalCalendar horizontalCalendar;
    Calendar startDate,endDate,defaultSelectedDate,currentTimePlus1hr,currentTime,tempTestingFromDate;
    long fromDate,toDate;
    String proId;
    long morningMin = 1;long morningMax = 11;
    long afterNoonMin = 11;long afterNoonMax = 15;
    long eveningMin = 15;long eveningMax = 19;
    // long nightMin = 19;long nightMax = 23;
    int hours;
    SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    String currentDate,selectedDate;
    private CustomScrollDateAdapter customScrollDateAdapter;
    ArrayList<Long> arrayList = new ArrayList<>();
    boolean isTrue= true;;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_time_slots);
        ButterKnife.bind(this);
        getIntentValue();
        setToolBar();
        onCreateSetDate();
        setCalendarValue();
        customCalendar();
        typeFace();
        fromDate = getStartOfDayInMillisToday(new Date(System.currentTimeMillis()))/1000;
        currentDate = formatted.format(getDateOfTheDay(getStartOfDayInMillisToday(new Date(System.currentTimeMillis()))));
        selectedDate = formatted.format(getDateOfTheDay(getStartOfDayInMillisToday(new Date(System.currentTimeMillis()))));
        dialog = alertProgress.getProgressDialog(this,getString(R.string.wait));
        dialog.show();
        callApi(fromDate);

    }



    private void callApi(long fromDate) {

        Log.d(TAG, "callApi: "+fromDate +" ProviderId "+proId +" CatID "+Constants.catId);
        if(Utility.isNetworkAvailable(this))
        {
            Observable<Response<ResponseBody>> observable = lspServices.getSlotsTimes(manager.getAUTH(),Constants.selLang,
                    proId,Constants.catId,fromDate,Constants.jobType);

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                        @Override
                        public void onNext(Response<ResponseBody> responseBodyResponse) {

                            int code = responseBodyResponse.code();
                            String response;
                            try
                            {
                                switch (code)
                                {
                                    case 200:
                                        response = responseBodyResponse.body().string();
                                        Log.d(TAG, "onNextResponseTime: "+response);
                                        SlotDetails slotDetails = gson.fromJson(response,SlotDetails.class);
                                        slotDet(slotDetails.getData());
                                        break;
                                    case Constants.SESSION_EXPIRED:
                                        response = responseBodyResponse.errorBody().string();
                                        RefreshToken.onRefreshToken(new JSONObject(response).getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                            @Override
                                            public void onSuccessRefreshToken(String newToken) {
                                                manager.setAUTH(newToken);
                                                callApi(fromDate);
                                            }

                                            @Override
                                            public void onFailureRefreshToken() {

                                            }

                                            @Override
                                            public void sessionExpired(String msg) {
                                                alertProgress.alertPositiveOnclick(TimeSlots.this, msg,
                                                        getString(R.string.logout), getString(R.string.ok), new DialogInterfaceListner() {
                                                            @Override
                                                            public void dialogClick(boolean isClicked) {
                                                                Utility.setMAnagerWithBID(TimeSlots.this,manager);
                                                            }
                                                        });
                                            }
                                        });
                                        break;
                                    case Constants.SESSION_LOGOUT:
                                        if(dialog!=null && dialog.isShowing())
                                            dialog.dismiss();
                                        response = responseBodyResponse.errorBody().string();

                                        alertProgress.alertPositiveOnclick(TimeSlots.this, new JSONObject(response).getString("message"),
                                                getString(R.string.logout), getString(R.string.ok), new DialogInterfaceListner() {
                                                    @Override
                                                    public void dialogClick(boolean isClicked) {
                                                        Utility.setMAnagerWithBID(TimeSlots.this,manager);
                                                    }
                                                });
                                        break;
                                    default:
                                        if(dialog!=null && dialog.isShowing())
                                            dialog.dismiss();
                                        break;
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {

                            if(dialog!=null && dialog.isShowing())
                                dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }else
            Utility.checkAndShowNetworkError(this);
    }


    private void getIntentValue() {

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String image = bundle.getString("ImagePro");
        String name = bundle.getString("NAME");
        proId = bundle.getString("ProviderId");
        String expertise = bundle.getString("expertise");
        Glide.with(TimeSlots.this)
                .load(image)
                .apply(Utility.createGlideOption(this))
                .into(ivMyEventProPic);
        tvMyEventProName.setText(name);
        tvMyEvnetProStatus.setText(expertise);
    }

    private void setToolBar() {
        //tvCenter.setText(Constants.catName);
        tvCenter.setText(getString(R.string.selectTimeSlots));
        setSupportActionBar(toolBarTimeSlot);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBarTimeSlot.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolBarTimeSlot.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void typeFace() {
        recyclerViewMorning.setNestedScrollingEnabled(false);
        recyclerViewAfterNoon.setNestedScrollingEnabled(false);
        recyclerViewEvening.setNestedScrollingEnabled(false);
        recyclerViewNight.setNestedScrollingEnabled(false);
        AppTypeface appTypeface = AppTypeface.getInstance(this);
        tvCenter.setTypeface(appTypeface.getHind_semiBold());
        tvMyEventProName.setTypeface(appTypeface.getHind_semiBold());
        tvSlotsNotAvailable.setTypeface(appTypeface.getHind_semiBold());
        tvMyEvnetProStatus.setTypeface(appTypeface.getHind_regular());

    }

    private void onCreateSetDate() {
        startDate = Calendar.getInstance();
        startDate.setTimeZone(Utility.getTimeZone());
        startDate.add(Calendar.DAY_OF_MONTH, -1);

        endDate = Calendar.getInstance();
        endDate.setTimeZone(Utility.getTimeZone());
        endDate.add(Calendar.DAY_OF_MONTH, 15);

        defaultSelectedDate = Calendar.getInstance();
        defaultSelectedDate.setTimeZone(Utility.getTimeZone());

        tempTestingFromDate=Calendar.getInstance();
        tempTestingFromDate.setTimeZone(Utility.getTimeZone());

        currentTime=Calendar.getInstance();
        currentTime.setTimeZone(Utility.getTimeZone());

        currentTimePlus1hr=Calendar.getInstance();
        currentTimePlus1hr.setTimeZone(Utility.getTimeZone());
        currentTimePlus1hr.setTimeInMillis(currentTimePlus1hr.getTimeInMillis()+ TimeUnit.HOURS.toMillis(1));

        hours = timeMethod(System.currentTimeMillis()/1000) ;
        Log.d(TAG, "onCreateSetDate: startdate: "+startDate+" End date: "+endDate+" hours: "+hours);
        if(hours ==23)
        {
            hours = 0;
        }else {
            hours = hours + 1;
            Log.d(TAG, "onCreateSetDate: startdate: " + startDate + " End date: " + endDate + " hours: hoursIncreased: " + hours);
        }

    }

    private void customCalendar()
    {
        arrayList.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewDate.setLayoutManager(linearLayoutManager);
        Calendar cal = Calendar.getInstance();
        int j = 0;
        for(int i=0;i<5;i++) {
            if (i == 1) {
                j++;
            }
            cal.add(Calendar.DATE, j);
            arrayList.add(cal.getTimeInMillis()/1000);
        }
        customScrollDateAdapter = new CustomScrollDateAdapter(arrayList,this);
        recyclerViewDate.setAdapter(customScrollDateAdapter);

        scrollLeft.setOnClickListener(v -> {

            if (linearLayoutManager.findFirstVisibleItemPosition() > 0) {
                recyclerViewDate.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - 1);
                int pos = linearLayoutManager.findLastVisibleItemPosition()-1;
                fromDate = arrayList.get(pos);
                Date date = new Date(fromDate * 1000L);
                //  fromDate = getStartOfDayInMillis(date)/1000;
                fromDate = getStartOfDayInMillisToday(date)/1000;
                selectedDate = formatted.format(getDateOfTheDay(getStartOfDayInMillisToday(date)));

                callApi(fromDate);

            } else {
                recyclerViewDate.smoothScrollToPosition(0);
                scrollLeft.setVisibility(View.INVISIBLE);
            }
            if(linearLayoutManager.findFirstVisibleItemPosition() == 0)
                scrollLeft.setVisibility(View.INVISIBLE);


        });

        scrollRight.setOnClickListener(v -> {

            if(linearLayoutManager.findLastVisibleItemPosition()<arrayList.size()-1)
            {
                fromDate = arrayList.get(linearLayoutManager.findLastVisibleItemPosition());

                recyclerViewDate.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
                Date date = new Date(fromDate * 1000L);
                fromDate = getStartOfDayInMillis(date)/1000;
                selectedDate = formatted.format(getDateOfTheDay(getStartOfDayInMillis(date)));
                isTrue = true;

                callApi(fromDate);
            }else if(linearLayoutManager.findLastVisibleItemPosition()==arrayList.size())
            {
                if(isTrue)
                {
                    fromDate = arrayList.get(linearLayoutManager.findLastVisibleItemPosition());
                    Date date = new Date(fromDate * 1000L);
                    fromDate = getStartOfDayInMillis(date)/1000;
                    selectedDate = formatted.format(getDateOfTheDay(getStartOfDayInMillis(date)));
                    callApi(fromDate);
                    isTrue = false;
                }
            }
            if(linearLayoutManager.findLastVisibleItemPosition()==0)
                scrollLeft.setVisibility(View.VISIBLE);
        });
    }

    private void setCalendarValue() {

        /* start 2 months ago from now */


     /*   horizontalCalendar = new HorizontalCalendar.Builder(llMainTimeSlots, R.id.calendarViewSlot)
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

                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                //   SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
                // String selectedDateStr =  sdf.format(date);

                String selectedDateStr = DateFormat.format("EEE, MMM d, yyyy", date).toString();
                Toast.makeText(TimeSlots.this, selectedDateStr, Toast.LENGTH_SHORT).show();

                //  defaultSelectedDate
                horizontalCalendar.post(() -> {

                    fromDate = getStartOfDayInMillis(date)/1000;
                    toDate = getEndOfDayInMillis(date);

                    selectedDate = formatted.format(getDateOfTheDay(getStartOfDayInMillis(date)));
                    dialog = alertProgress.getProgressDialog(TimeSlots.this,getString(R.string.wait));
                    dialog.show();
                    callApi(fromDate);

                    //  mListener.onDateSelectedApi(fromDate,toDate,true);
                });

            }

        });*/
    }

    public long getStartOfDayInMillisToday(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);//.getTime()
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    private Date getDateOfTheDay(long startOfDayInMillisToday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startOfDayInMillisToday);
        calendar.setTimeZone(Utility.getTimeZone());
        return calendar.getTime();
    }

    public long getStartOfDayInMillis(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));//
        calendar.setTime(date);//.getTime()
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        //  if(isStartTime)
        return  calendar.getTimeInMillis()+(24 * 60 * 60 * 1000);
       /* else
            return calendar.getTimeInMillis();*/
    }

    public long getEndOfDayInMillis(Calendar date) {
        return getStartOfDayInMillis(date.getTime()) + (24 * 60 * 60 * 1000)-1000;
    }


    private void slotDet(ArrayList<Slots> slotDetails) {
        ArrayList<Slots>morningSlots = new ArrayList<>();
        ArrayList<Slots>afterNoonSlots = new ArrayList<>();
        ArrayList<Slots>eveningSlots = new ArrayList<>();
        ArrayList<Slots>nightSlots = new ArrayList<>();
        for(int i = 0; i<slotDetails.size();i++) {
            int hr = timeMethod(slotDetails.get(i).getFrom());
            if(hr>=morningMin && hr<=morningMax) {
                if(currentDate.equals(selectedDate)) {
                    tempTestingFromDate.setTimeInMillis(slotDetails.get(i).getFrom()*1000L);
                    Log.d(TAG, "slotDet: temptestingFromDate:"+tempTestingFromDate+" currenttimePlushr:"+currentTimePlus1hr);
                    Log.d(TAG, "slotDet: "+tempTestingFromDate.after(currentTimePlus1hr));
                    if(currentTimePlus1hr.after(tempTestingFromDate))//if(hr<hours)
                        slotDetails.get(i).setIsBook(0);
                }
                if(slotDetails.get(i).getIsBook()==1)
                    morningSlots.add(slotDetails.get(i));
            } else if(hr>afterNoonMin && hr<=afterNoonMax) {
                if(currentDate.equals(selectedDate)) {
                    tempTestingFromDate.setTimeInMillis(slotDetails.get(i).getFrom()*1000L);
                    if(currentTimePlus1hr.after(tempTestingFromDate))//if(hr<hours)
                        slotDetails.get(i).setIsBook(0);
                }
                if(slotDetails.get(i).getIsBook()==1)
                    afterNoonSlots.add(slotDetails.get(i));
            }

            else if(hr>eveningMin && hr<=eveningMax) {
                if(currentDate.equals(selectedDate)) {
                    tempTestingFromDate.setTimeInMillis(slotDetails.get(i).getFrom()*1000L);
                    if(currentTimePlus1hr.after(tempTestingFromDate))//if(hr<hours)
                        slotDetails.get(i).setIsBook(0);
                }
                if(slotDetails.get(i).getIsBook()==1)
                    eveningSlots.add(slotDetails.get(i));
            }

            else {
                if(currentDate.equals(selectedDate)) {
                    tempTestingFromDate.setTimeInMillis(slotDetails.get(i).getFrom()*1000L);
                    if(currentTimePlus1hr.after(tempTestingFromDate))//if(hr<hours)
                        slotDetails.get(i).setIsBook(0);
                }
                if(slotDetails.get(i).getIsBook()==1)
                    nightSlots.add(slotDetails.get(i));
            }

        }

        GridLayoutManager gridLayoutManagerM = new GridLayoutManager(this,3);
        SlotsTimingAdapter adapter = new SlotsTimingAdapter(this,false,morningSlots);
        recyclerViewMorning.setLayoutManager(gridLayoutManagerM);
        recyclerViewMorning.setAdapter(adapter);


        if(morningSlots.size()>0)
        {
            llMorning.removeAllViews();
            TextView tvDayTime,tvDayTimeSlotsNo;
            ImageView ivPeriod;
            View v = LayoutInflater.from(this).inflate(R.layout.time_slot_info_header,llMainInfoSlots,false);
            llMorning.addView(v);
            tvDayTime = v.findViewById(R.id.tvDayTime);
            tvDayTimeSlotsNo = v.findViewById(R.id.tvDayTimeSlotsNo);
            ivPeriod = v.findViewById(R.id.ivPeriod);
            tvDayTime.setTypeface(appTypeface.getHind_semiBold());
            tvDayTimeSlotsNo.setTypeface(appTypeface.getHind_regular());
            tvDayTimeSlotsNo.setText("("+morningSlots.size()+")");
            tvDayTime.setText(getString(R.string.morning_slots));
            ivPeriod.setImageResource(R.drawable.ic_morning);
            adapter.notifyDataSetChanged();
        }else
        {
            llMorning.removeAllViews();
        }

        GridLayoutManager gridLayoutManagerA = new GridLayoutManager(this,3);

        SlotsTimingAdapter adapterAfterNoon = new SlotsTimingAdapter(this,false,afterNoonSlots);
        recyclerViewAfterNoon.setLayoutManager(gridLayoutManagerA);
        recyclerViewAfterNoon.setAdapter(adapterAfterNoon);

        if(afterNoonSlots.size()>0)
        {
            llAfterNoon.removeAllViews();
            TextView tvDayTime,tvDayTimeSlotsNo;
            ImageView ivPeriod;
            View v = LayoutInflater.from(this).inflate(R.layout.time_slot_info_header,llMainInfoSlots,false);
            llAfterNoon.addView(v);
            tvDayTime = v.findViewById(R.id.tvDayTime);
            tvDayTimeSlotsNo = v.findViewById(R.id.tvDayTimeSlotsNo);
            ivPeriod = v.findViewById(R.id.ivPeriod);
            tvDayTime.setTypeface(appTypeface.getHind_semiBold());
            tvDayTimeSlotsNo.setTypeface(appTypeface.getHind_regular());
            tvDayTimeSlotsNo.setText("("+afterNoonSlots.size()+")");
            tvDayTime.setText(getString(R.string.after_noon_slots));
            ivPeriod.setImageResource(R.drawable.ic_afternoon);
            adapterAfterNoon.notifyDataSetChanged();

        } else
        {
            llAfterNoon.removeAllViews();
        }
        GridLayoutManager gridLayoutManagerE = new GridLayoutManager(this,3);

        SlotsTimingAdapter adapterEvening = new SlotsTimingAdapter(this,false,eveningSlots);
        recyclerViewEvening.setLayoutManager(gridLayoutManagerE);
        recyclerViewEvening.setAdapter(adapterEvening);

        if(eveningSlots.size()>0)
        {
            llEvening.removeAllViews();
            TextView tvDayTime,tvDayTimeSlotsNo;
            ImageView ivPeriod;
            View v = LayoutInflater.from(this).inflate(R.layout.time_slot_info_header,llMainInfoSlots,false);
            llEvening.addView(v);
            tvDayTime = v.findViewById(R.id.tvDayTime);
            ivPeriod = v.findViewById(R.id.ivPeriod);
            tvDayTimeSlotsNo = v.findViewById(R.id.tvDayTimeSlotsNo);
            tvDayTime.setTypeface(appTypeface.getHind_semiBold());
            tvDayTimeSlotsNo.setTypeface(appTypeface.getHind_regular());
            tvDayTimeSlotsNo.setText("("+eveningSlots.size()+")");
            tvDayTime.setText(getString(R.string.evening_slots));
            ivPeriod.setImageResource(R.drawable.ic_evening);
            adapterEvening.notifyDataSetChanged();


        }else
        {
            llEvening.removeAllViews();
        }
        GridLayoutManager gridLayoutManagerN = new GridLayoutManager(this,3);

        SlotsTimingAdapter adapterNight = new SlotsTimingAdapter(this,false,nightSlots);
        recyclerViewNight.setLayoutManager(gridLayoutManagerN);
        recyclerViewNight.setAdapter(adapterNight);

        if(nightSlots.size()>0)
        {
            llNight.removeAllViews();
            TextView tvDayTime,tvDayTimeSlotsNo;
            ImageView ivPeriod;
            View v = LayoutInflater.from(this).inflate(R.layout.time_slot_info_header,llMainInfoSlots,false);
            llNight.addView(v);
            tvDayTime = v.findViewById(R.id.tvDayTime);
            ivPeriod = v.findViewById(R.id.ivPeriod);
            tvDayTimeSlotsNo = v.findViewById(R.id.tvDayTimeSlotsNo);
            tvDayTime.setTypeface(appTypeface.getHind_semiBold());
            tvDayTimeSlotsNo.setTypeface(appTypeface.getHind_regular());
            tvDayTimeSlotsNo.setText("("+nightSlots.size()+")");
            tvDayTime.setText(getString(R.string.night_slots));
            ivPeriod.setImageResource(R.drawable.ic_night);
            adapterNight.notifyDataSetChanged();

        }else
        {
            llNight.removeAllViews();
        }
        if(morningSlots.size()>0 || afterNoonSlots.size()>0 || eveningSlots.size()>0 || nightSlots.size()>0)
            llNoSlotsAvailable.setVisibility(View.GONE);
        else if(morningSlots.size()<=0 && afterNoonSlots.size()<=0 && eveningSlots.size()<=0 && nightSlots.size()<=0)
            llNoSlotsAvailable.setVisibility(View.VISIBLE);

        if(dialog!=null && dialog.isShowing())
            dialog.dismiss();


    }

    /**
     * <h1>timeMethod</h1>
     * @param bookingRequestedFor
     * @return the hour value of timestamp
     */
    public int timeMethod(long bookingRequestedFor)
    {
        int hour = 0;
        try {
            Date date = new Date(bookingRequestedFor * 1000L);
            //   SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
            SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.US);
            //  int hour = sdf
            sdf.setTimeZone(Utility.getTimeZone());
            String formattedDate = sdf.format(date);
            Date d = sdf.parse(formattedDate);

            Log.d(TAG, "timeMethod: date.getHours:"+date.getHours()+ " d.getHours:"+d.getHours());
            hour = d.getHours();


        } catch (Exception e) {
            Log.d("TAG", "timeMethodException: " + e.toString());
        }
        return hour;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //  overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
        overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);
    }


}
