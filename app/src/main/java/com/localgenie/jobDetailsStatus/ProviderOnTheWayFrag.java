package com.localgenie.jobDetailsStatus;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.localgenie.R;
import com.localgenie.chatting.ChattingActivity;
import com.localgenie.providerdetails.ProviderDetails;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.CircleTransform;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerFragment;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import com.pojo.BidDispatchLog;
import com.pojo.BookingAccounting;
import com.pojo.BookingTimer;
import com.pojo.CartInfo;
import com.pojo.LiveTackPojo;
import com.pojo.LiveTrackObservable;

import java.util.ArrayList;
import java.util.Date;

import static com.localgenie.utilities.Constants.METER;
import static com.localgenie.utilities.Utility.distance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProviderOnTheWayFrag extends DaggerFragment implements JobProviderInfo,OnMapReadyCallback{


    CompositeDisposable disposable;

    private Context mContext;

    @BindView(R.id.ivProviderPic)ImageView ivProviderPic;
    @BindView(R.id.tvName)TextView tvName;
    @BindView(R.id.tvRating)TextView tvRating;
    @BindView(R.id.tvNoOfReview)TextView tvNoOfReview;
    @BindView(R.id.tvViewProfile)TextView tvViewProfile;
    @BindView(R.id.callBtn)Button callBtn;
    @BindView(R.id.msgBtn)Button msgBtn;
    @BindView(R.id.rbProfile)RatingBar rbProfile;
    @BindView(R.id.bottom_sheetOnTheWay)RelativeLayout bottom_sheetOnTheWay;
    @BindView(R.id.tvCancel)TextView tvCancel;
    @BindView(R.id.tvMsgCount)TextView tvMsgCount;
    @Inject AppTypeface appTypeface;
    @Inject
    SessionManagerImpl manager;
    private Handler locatingUpdateHandler,plotHandler;
    private Runnable locationUpdateRunnable,plotRunnable;
    private GoogleMap mMap;
    private Marker proMarker = null;
    private LatLng proLatLng,customerLatLng;
    private String proProfilePic;
    private String pId = "";
    @Inject JobDetailsOnTheWayContract.JobDetailsOnTheWayPresenter jobDetailsOnTheWayPresenter;
    @Inject MQTTManager mqttManager;
    @Inject JobDetailsOnTheWayContract.CancelBooking cancelBooking;
    private BottomSheetBehavior sheetBehavior;
    private float slideOff = 80;

    private long bid = 0;
    private int plotCount = 0;
    private double proTempLat,proTempLng;

    @Inject
    public ProviderOnTheWayFrag() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_provider_onthe_way, container, false);
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        ((JobDetailsActivity)getActivity()).jobDetailProInfo(this);
        mContext = getActivity();
        disposable = new CompositeDisposable();

        // SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFrag);

        Log.d("ONTHEWAY", "onCreateView: "+mMapFragment);
        mMapFragment.getMapAsync(this);
        ButterKnife.bind(this,layout);
        initialize();
        animateMarker();
        initializeLiveRxJava();
        sheetBehavior = BottomSheetBehavior.from(bottom_sheetOnTheWay);
        showExpended();
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void showExpended() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }


    private void initializeLiveRxJava()
    {
        Observer<LiveTackPojo> liveObserver = new Observer<LiveTackPojo>() {
            @Override
            public void onSubscribe(Disposable d)
            {
                disposable.add(d);
            }

            @Override
            public void onNext(LiveTackPojo liveTackPojo)
            {

                Log.d("LIVESTATUS", "onNextLIVE: "+liveTackPojo.getLatitude());

                proLatLng = new LatLng(liveTackPojo.getLatitude(),liveTackPojo.getLongitude());
                if(mContext!=null)
                {
                    if(plotCount!=2)
                    {
                        proTempLat = liveTackPojo.getLatitude();
                        proTempLng = liveTackPojo.getLongitude();
                        plotMarker();
                        plotCount++;
                    }else if(distance(proTempLat,proTempLng,liveTackPojo.getLatitude(),liveTackPojo.getLongitude(),METER)>20)
                    {
                        proTempLat = liveTackPojo.getLatitude();
                        proTempLng = liveTackPojo.getLongitude();
                        plotMarker();
                    }

                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                disposable.clear();
                disposable.dispose();
            }
        };
        LiveTrackObservable.getInstance().subscribe(liveObserver);
    }

    private void initialize()
    {

        tvName.setTypeface(appTypeface.getHind_medium());
        tvRating.setTypeface(appTypeface.getHind_medium());
        tvMsgCount.setTypeface(appTypeface.getHind_medium());
        tvNoOfReview.setTypeface(appTypeface.getHind_regular());
        callBtn.setTypeface(appTypeface.getHind_regular());
        msgBtn.setTypeface(appTypeface.getHind_regular());
        tvCancel.setTypeface(appTypeface.getHind_regular());

    }

    @OnClick({R.id.tvCancel,R.id.callBtn,R.id.msgBtn,R.id.tvViewProfile})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.callBtn:

                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                intentCall.setData(Uri.parse("tel:" +JobDetailsActivity.phoneNumber));
                startActivity(intentCall);
                //Toast.makeText(getContext(),"Call Button",Toast.LENGTH_SHORT).show();
                break;
            case R.id.msgBtn:
                if(!manager.getChatProId().equals(""))
                {
                    Intent intent = new Intent(mContext, ChattingActivity.class);
                    startActivity(intent);
                    ((Activity)mContext).overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);

                }
                break;
            case R.id.tvViewProfile:
                Intent intent=new Intent(getActivity(),ProviderDetails.class);
                intent.putExtra("ProId",pId);
                intent.putExtra("isProFileView",true);
                startActivity(intent);
                break;
            case R.id.tvCancel:
                cancelBooking.onToCancelBooking(bid,getActivity(), "");
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // initializeRxJava();
        if(bid!=0)
        {
            if(manager.getChatCount(bid)>0)
            {
                String count = ""+manager.getChatCount(bid);
                tvMsgCount.setText(count);
            }else
                tvMsgCount.setVisibility(View.GONE);
        }
    }

    private void plotMarker() {

        //   proMarker = liveBookingController.plotMArker(LiveStatus.this, customerLatLng, proLatLng, mMap, proProfilePic);
        proMarker =  jobDetailsOnTheWayPresenter.plotMarker(mContext,proLatLng,customerLatLng,mMap);

    }
    private void animateMarker() {

        locatingUpdateHandler = new Handler();
        final Handler markeMoveHandler = new Handler();
        final float durationInMs = 4000;
        locationUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                locatingUpdateHandler.postDelayed(this,5000);

                if(mMap !=null && proMarker!=null)
                {
                    final LatLng startPosition = proMarker.getPosition();

                    final long start = SystemClock.uptimeMillis();
                    final Interpolator interpolator = new AccelerateDecelerateInterpolator();

                    markeMoveHandler.post(new Runnable() {
                        long elapsed;
                        float t;
                        float v;
                        @Override
                        public void run() {
                            // Calculate progress using interpolator
                            elapsed = SystemClock.uptimeMillis() - start;
                            t = elapsed / durationInMs;
                            v = interpolator.getInterpolation(t);

                            LatLng currentPosition = new LatLng(startPosition.latitude*(1-t)+ proLatLng.latitude*t,
                                    startPosition.longitude*(1-t)+ proLatLng.longitude*t);

                            proMarker.setPosition(currentPosition);

                            // Repeat till progress is complete.
                            if (t < 1) {
                                // Post again 16ms later.
                                markeMoveHandler.postDelayed(this, 16);
                            }
                        }
                    });
                }
            }
        };

    }

    private void removeLocationHandler()
    {

        if(locationUpdateRunnable!=null && locatingUpdateHandler!=null)
            locatingUpdateHandler.removeCallbacks(locationUpdateRunnable);
    }

    private void removePlotHandler()
    {

        if(plotRunnable!=null && plotHandler!=null)
            plotHandler.removeCallbacks(plotRunnable);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if(Constants.custLatLng!=null)
        {
            customerLatLng = Constants.custLatLng;
            Log.d("TAG", "onMapReady: "+googleMap+" location "+customerLatLng
                    +" customerLatitude "+customerLatLng.latitude);

            if(customerLatLng.latitude!=0 || customerLatLng!=null  )
            {
                CameraUpdate center = CameraUpdateFactory.newLatLng(customerLatLng);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(13.0f);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
            }
        }

        locatingUpdateHandler.postDelayed(locationUpdateRunnable,5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
        disposable.dispose();
        mqttManager.unSubscribeToTopic(MqttEvents.LiveTrack.value + "/" + pId);
        removeLocationHandler();
        removePlotHandler();
        mContext = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mqttManager.unSubscribeToTopic(MqttEvents.LiveTrack.value + "/" + pId);
        mContext = null;

    }

    @Override
    public void providerInfo(String providerId, String name, String proPic, int reviewCount, float rating, double amount, String currencySymbol, int status)
    {
        if(mContext!=null)
        {
            callMethod(name,proPic);
            tvRating.setText(""+rating);
            String reviewCounts = "("+reviewCount+" "+getString(R.string.reviews)+")";
            tvNoOfReview.setText(reviewCounts);
            rbProfile.setRating(rating);
            pId =  providerId;
            Log.d("", "providerInfo: "+pId);
            if(mqttManager.isMQTTConnected())
            {
                mqttManager.subscribeToTopic(MqttEvents.LiveTrack.value + "/" + pId, 1);
            }
        }

    }
    @Override
    public void proExpiry(String name, String proPic, long expiryTime, long serverTime, LatLng customerLatLng, LatLng proLatLng)
    {

        if(mContext!=null)
        {
            plotHandler = new Handler();
            this.customerLatLng =customerLatLng;
            this.proLatLng = proLatLng;

            if(mMap!=null)
            {
                if(customerLatLng.latitude!=0 && customerLatLng!=null)
                {
                    CameraUpdate center = CameraUpdateFactory.newLatLng(customerLatLng);
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(13.0f);
                    mMap.moveCamera(center);
                    mMap.animateCamera(zoom);
                }
            }


            StartTimer();
            callMethod(name,proPic);
        }

    }

    private void StartTimer()
    {

        Log.d("TAG", "runRunnableCode: ");


        new Handler().postDelayed(() -> {
            Log.d("TAG", "runRunnableCode: ");
            if(mMap!=null)
            {
                Log.d("TAG", "runRunnableCode1: ");
                if(mContext!=null)
                {
                    plotMarker();
                    removePlotHandler();
                }

            }
            Log.d("TAG", "runRunnableCode2: ");
        },2000);


    }

    @Override
    public void onJobTimer(BookingTimer bookingTimer, long serverTime, String statusMsg, long bid, int status, long totalJobTime) {
        this.bid = bid;

        if(manager.getChatCount(bid)>0)
        {
            String count = ""+manager.getChatCount(bid);
            tvMsgCount.setText(count);
        }else
            tvMsgCount.setVisibility(View.GONE);
    }

    @Override
    public void onBidJobInfoDetails(ArrayList<BidDispatchLog> bidDispatchLog, int statusCode) {

    }

    @Override
    public void onJobInfo(int bookingType, BookingAccounting accounting, CartInfo cart, String categoryName) {

    }

    @Override
    public void onJobAddress(String address, Date date) {

    }

    private void callMethod(String name, String proPic)
    {
        proProfilePic = proPic;
        tvName.setText(name);
        if(!proPic.equals(""))
        {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.profile_price_bg)
                    .error(R.drawable.profile_price_bg)
                    .transform(new CircleTransform(mContext))
                    .priority(Priority.HIGH);
            if(mContext!=null)
            {
                Glide.with(mContext)
                        .load(proPic)
                        .apply(options)
                        .into(ivProviderPic);
            }

        }
    }

}
