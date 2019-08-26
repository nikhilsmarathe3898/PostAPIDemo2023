package com.localgenie.jobDetailsStatus;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.localgenie.R;
import com.localgenie.chatting.ChattingActivity;
import com.localgenie.providerdetails.ProviderDetails;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import adapters.SelectedService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerFragment;
import io.reactivex.Observer;
import io.reactivex.observers.DisposableObserver;

import com.pojo.BidDispatchLog;
import com.pojo.BookingAccounting;
import com.pojo.BookingTimer;
import com.pojo.BookingTimerLatLng;
import com.pojo.BookingTimerLatLngObservable;
import com.pojo.CartInfo;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobStartedFrag extends DaggerFragment implements JobProviderInfo,Runnable{


    @BindView(R.id.tv_timer_text)TextView tvTimerText;
    @BindView(R.id.tvJobStarted)TextView tvJobStarted;
    @BindView(R.id.ivProfilePic)ImageView ivProfilePic;
    @BindView(R.id.tvName)TextView tvName;
    @BindView(R.id.tvProStatus)TextView tvProStatus;
    @BindView(R.id.tvViewProfile)TextView tvViewProfile;
    @BindView(R.id.tvMsgCount)TextView tvMsgCount;
    @BindView(R.id.callBtn)Button callBtn;
    @BindView(R.id.msgBtn)Button msgBtn;

    /*******************************************************************/

    @BindView(R.id.tvJobRequestedService)TextView tvJobRequestedService;
    @BindView(R.id.recyclerViewReuestedService)RecyclerView recyclerViewReuestedService;

    @BindView(R.id.rlBiddingService)RelativeLayout rlBiddingService;
    @BindView(R.id.tvRequestedBiddingService)TextView tvRequestedBiddingService;
    @BindView(R.id.tvRequestedBiddingServiceAmt)TextView tvRequestedBiddingServiceAmt;

    @BindView(R.id.rlVisitFee)RelativeLayout rlVisitFee;
    @BindView(R.id.tvVisitFee)TextView tvVisitFee;
    @BindView(R.id.tvVisitFeeAmt)TextView tvVisitFeeAmt;

    @BindView(R.id.rlTravelFeeFee)RelativeLayout rlTravelFeeFee;
    @BindView(R.id.tvTravelFeeFee)TextView tvTravelFeeFee;
    @BindView(R.id.tvTravelFeeFeeAmt)TextView tvTravelFeeFeeAmt;

    @BindView(R.id.rlDiscountFee)RelativeLayout rlDiscountFee;
    @BindView(R.id.tvDiscountFee)TextView tvDiscountFee;
    @BindView(R.id.tvDiscountFeeAmt)TextView tvDiscountFeeAmt;

    @BindView(R.id.rlTotalFee)RelativeLayout rlTotalFee;
    @BindView(R.id.tvTotalFee)TextView tvTotalFee;
    @BindView(R.id.tvTotalFeeAmt)TextView tvTotalFeeAmt;

    @BindView(R.id.tvRequestedPayment)TextView tvRequestedPayment;
    @BindView(R.id.tvRequestedPaymentMethod)TextView tvRequestedPaymentMethod;

    /*******************************************************************/

  //  private Timer jobTimr;
    @Inject
    AppTypeface appTypeface;

    private Handler handler;
    private Runnable runnableCode;
    private Context mContext;
    private long timecunsumdsecond;
    private long bid = 0;
    private String pId;
    @Inject
    SessionManagerImpl manager;

    @Inject
    public JobStartedFrag() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runnableCode = this;
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_job_started, container, false);
        ButterKnife.bind(this,layout);
        //  runnableCode = this;
        mContext = getActivity();

        Log.d("TAG", "onCreateView: "+mContext);
        ((JobDetailsActivity)mContext).jobDetailProInfo(this);
        initializeTypeFace();
        typeFace();
        return layout;
    }



    private void initializeTypeFace()
    {
        tvTimerText.setTypeface(appTypeface.getDigital_clock());
        tvJobStarted.setTypeface(appTypeface.getHind_regular());
        tvName.setTypeface(appTypeface.getHind_semiBold());
        tvProStatus.setTypeface(appTypeface.getHind_medium());
        tvMsgCount.setTypeface(appTypeface.getHind_medium());
        tvViewProfile.setTypeface(appTypeface.getHind_regular());
        callBtn.setTypeface(appTypeface.getHind_regular());
        msgBtn.setTypeface(appTypeface.getHind_regular());

    }

    private void typeFace() {

        LinearLayoutManager llManager = new LinearLayoutManager(mContext);
        recyclerViewReuestedService.setLayoutManager(llManager);
        tvJobRequestedService.setTypeface(appTypeface.getHind_semiBold());
        tvRequestedBiddingService.setTypeface(appTypeface.getHind_light());
        tvRequestedBiddingServiceAmt.setTypeface(appTypeface.getHind_light());
        tvVisitFee.setTypeface(appTypeface.getHind_light());
        tvVisitFeeAmt.setTypeface(appTypeface.getHind_light());
        tvDiscountFee.setTypeface(appTypeface.getHind_light());
        tvDiscountFeeAmt.setTypeface(appTypeface.getHind_light());
        tvTravelFeeFee.setTypeface(appTypeface.getHind_light());
        tvTravelFeeFeeAmt.setTypeface(appTypeface.getHind_light());
        tvTotalFee.setTypeface(appTypeface.getHind_semiBold());
        tvTotalFeeAmt.setTypeface(appTypeface.getHind_semiBold());
        tvRequestedPayment.setTypeface(appTypeface.getHind_semiBold());
        tvRequestedPaymentMethod.setTypeface(appTypeface.getHind_semiBold());
    }


    @OnClick({R.id.msgBtn,R.id.callBtn,R.id.tvViewProfile})
    public void onClickedMsg(View view)
    {
        switch (view.getId())
        {
            case R.id.callBtn:
                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                intentCall.setData(Uri.parse("tel:" +JobDetailsActivity.phoneNumber));
                startActivity(intentCall);
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(bid!=0)
        {
            if(manager.getChatCount(bid)>0)
            {
                String count = ""+manager.getChatCount(bid);
                tvMsgCount.setText(count);
            }else
                tvMsgCount.setVisibility(View.GONE);
        }
        if(handler!=null)
            handler.removeCallbacks(runnableCode);

    }

    private void initializeRxJavaFarg()
    {
        Observer<BookingTimerLatLng> observer = new DisposableObserver<BookingTimerLatLng>() {

            @Override
            public void onNext(BookingTimerLatLng bookingTimerLatLng) {


                Log.d("TAG", "onNextJOBSTARTED: "+bookingTimerLatLng.getStatus());
                tvJobStarted.setText(bookingTimerLatLng.getJobTimerStatus());
                tvProStatus.setText(bookingTimerLatLng.getJobTimerStatus());
                if(bookingTimerLatLng.getStatus()==9)
                {
                    stopJobTimer();
                    /*tvTimerText.setTypeface(appTypeface.getHind_semiBold());
                    tvTimerText.setText(getString(R.string.JobCompleted));*/
                }else
                {
                    if(bookingTimerLatLng.getBookingTimer().getStatus()==1)
                        jobTimer(tvTimerText,bookingTimerLatLng.getBookingTimer().getSecond());
                    else
                        timerPaused(bookingTimerLatLng.getBookingTimer().getSecond());
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        BookingTimerLatLngObservable.getInstance().subscribe(observer);
    }

    @Override
    public void providerInfo(String providerId, String name, String proPic, int reviewCount, float rating, double amount, String currencySymbol, int status) {


        if(mContext!=null)
        {
            providerInfoCall(name,proPic);

            providerInfoCall(name,proPic);
            String review = reviewCount +" "+getString(R.string.reviews);

            String priceQuoted = getString(R.string.quotedPrice)+" "+currencySymbol;
            pId = providerId;
            initializeRxJavaFarg();
        }

    }
    @Override
    public void proExpiry(String name, String proPic, long expiryTime, long serverTime, LatLng customerLatLng, LatLng proLatLng)
    {
        if(mContext!=null)
            providerInfoCall(name,proPic);

    }

    @Override
    public void onJobTimer(BookingTimer bookingTimer, long serverTime, String statusMsg, long bid, int status, long totalJobTime)
    {
        tvJobStarted.setText(statusMsg);
        tvProStatus.setText(statusMsg);

        this.bid = bid;
        if(manager.getChatCount(bid)>0)
        {
            String count = ""+manager.getChatCount(bid);
            tvMsgCount.setText(count);
        }else
            tvMsgCount.setVisibility(View.GONE);
        if(mContext!=null)
        {
            if(status==9)
            {
               // stopJobTimer();
              //  tvTimerText.setTypeface(appTypeface.getHind_semiBold());
                timerPaused(totalJobTime);
              //  tvTimerText.setText(statusMsg);
            }else if(status==8)
            {
                if(bookingTimer.getStatus()==1)
                {
                    long timeDifference;
                    if(bookingTimer.getSecond()==0)
                        timeDifference = serverTime-bookingTimer.getStartTimeStamp();
                    else
                        timeDifference = (serverTime-bookingTimer.getStartTimeStamp())+bookingTimer.getSecond();
                    jobTimer(tvTimerText,timeDifference);
                }else
                    timerPaused(bookingTimer.getSecond());
            }
        }

    }


    private void jobTimer(final TextView tvjobtimer, long timecunsumedsecond)
    {

        timecunsumdsecond = timecunsumedsecond;

        if(handler!=null)
        {
            handler.removeCallbacks(runnableCode);
            handler.postDelayed(runnableCode,0);
        }

       /* if(jobTimr!=null) {

            jobTimr.cancel();
            jobTimr.purge();
        }
        jobTimr = null;

        timecunsumdsecond = timecunsumedsecond;
        jobTimr = new Timer();
        if(mContext!=null)
        {
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    ((Activity) mContext).runOnUiThread(() -> {
                        timecunsumdsecond = timecunsumdsecond + 1;
                        tvjobtimer.setText(formatSeconds(timecunsumdsecond));

                    });
                }
            };
            jobTimr.scheduleAtFixedRate(task, 0, 1000);
        }*/




    }
    private static String formatSeconds(long timeInSeconds)
    {
        int hours = (int)timeInSeconds / 3600;
        int secondsLeft = (int)timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10)
            formattedTime += "0";
        formattedTime += hours + " : ";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + " : ";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds ;

        return formattedTime;
    }

    public void timerPaused(long second)
    {
        if(second>0)
        {
            stopJobTimer();
            timecunsumdsecond = second;
            tvTimerText.setText(formatSeconds(timecunsumdsecond));
        }else
            tvTimerText.setText("00:00:00");
    }

    private void stopJobTimer() {
       /* if(jobTimr!=null)
        {
            jobTimr.cancel();
            jobTimr.purge();
            jobTimr = null;
        }*/
        if(handler!=null)
            handler.removeCallbacks(runnableCode);

    }

    @Override
    public void onBidJobInfoDetails(ArrayList<BidDispatchLog> bidDispatchLog, int statusCode) {

    }

    @Override
    public void onJobInfo(int bookingType, BookingAccounting accounting, CartInfo cart, String categoryName) {
        if(mContext!=null)
        {
            if(bookingType == 3)
            {
                rlBiddingService.setVisibility(View.VISIBLE);
                recyclerViewReuestedService.setVisibility(View.GONE);
                tvRequestedBiddingService.setText(categoryName);
                Utility.setAmtOnRecept(accounting.getBidPrice(),tvRequestedBiddingServiceAmt, Constants.currencySymbol);

            }else
            {
                rlBiddingService.setVisibility(View.GONE);
                recyclerViewReuestedService.setVisibility(View.VISIBLE);
                SelectedService selectedService = new SelectedService(mContext,false);
                selectedService.onCheckOutItem(cart.getCheckOutItem());
                recyclerViewReuestedService.setAdapter(selectedService);
            }


            if(accounting.getTravelFee()>0)
            {
                rlTravelFeeFee.setVisibility(View.VISIBLE);
                Utility.setAmtOnRecept(accounting.getTravelFee(),tvTravelFeeFeeAmt, Constants.currencySymbol);
            }

            if(accounting.getVisitFee()>0)
            {
                rlVisitFee.setVisibility(View.VISIBLE);
                Utility.setAmtOnRecept(accounting.getVisitFee(),tvVisitFeeAmt, Constants.currencySymbol);
            }

            if(accounting.getDiscount()>0)
            {
                rlDiscountFee.setVisibility(View.VISIBLE);
                Utility.setAmtOnRecept(accounting.getDiscount(),tvDiscountFeeAmt, Constants.currencySymbol);

            }

            Utility.setAmtOnRecept(accounting.getTotal(),tvTotalFeeAmt,Constants.currencySymbol);

            if(accounting.getPaidByWallet()==1)
            {
                tvRequestedPaymentMethod.setText(getString(R.string.wallet));
            }else
                tvRequestedPaymentMethod.setText(accounting.getPaymentMethodText());

        }
    }

    @Override
    public void onJobAddress(String address, Date date) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void providerInfoCall(String name, String proPic)
    {
        tvName.setText(name);
        if(!proPic.equals(""))
        {
            Glide.with(mContext)
                    .load(proPic)
                    .apply(Utility.createGlideOption(mContext))
                    .into(ivProfilePic);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopJobTimer();

        mContext = null;
        Log.d("TAG", "onDestroyCALLEDFRAG: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopJobTimer();
        mContext = null;

    }

    @Override
    public void run() {
        Log.d("TAG", "runTimer: "+timecunsumdsecond);
        timecunsumdsecond = timecunsumdsecond+1;
        tvTimerText.setText(formatSeconds(timecunsumdsecond));
        handler.postDelayed(runnableCode,1000);
    }
}
