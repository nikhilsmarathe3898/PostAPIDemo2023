package com.localgenie.jobDetailsStatus;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.localgenie.R;
import com.localgenie.providerdetails.ProviderDetails;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

import com.pojo.BidDispatchLog;
import com.pojo.BookingAccounting;
import com.pojo.BookingTimer;
import com.pojo.CartInfo;
import com.utility.WaveDrawable;

import java.util.ArrayList;
import java.util.Date;


//import at.grabner.circleprogress.CircleProgressView;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobPostedFrag extends DaggerFragment implements JobProviderInfo {


    @BindView(R.id.proFilePic)ImageView proFilePic;
    @BindView(R.id.ivPicWave)ImageView ivPicWave;
    @BindView(R.id.tvJobPostedProName)TextView tvJobPostedProName;
    @BindView(R.id.tvJobPostedProViewPro)TextView tvJobPostedProViewPro;
    @BindView(R.id.tvTimerText)TextView tvTimerText;
    @BindView(R.id.tvProHrText)TextView tvProHrText;
    @BindView(R.id.tvProMinText)TextView tvProMinText;
    @BindView(R.id.tvProSecText)TextView tvProSecText;
    @BindView(R.id.tvCancel)TextView tvCancel;
    @BindView(R.id.tvLookingProvider)TextView tvLookingProvider;
    @Inject AppTypeface appTypeface;
    @Inject JobDetailsOnTheWayContract.CancelBooking cancelBooking;

    private Handler handler = new Handler();
    private Runnable runnableCode;
    private Context mContext;
    private long bid;
    private WaveDrawable waveDrawable;
    private String pId;
    private  long remainIngTime;

    @Inject
    public JobPostedFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_job_posted, container, false);
        ((JobDetailsActivity)mContext).jobDetailProInfo(this);
        mContext = getActivity();
        ButterKnife.bind(this,layout);
        initTypeFace();

        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void initTypeFace()
    {
        tvJobPostedProName.setTypeface(appTypeface.getHind_medium());
        tvJobPostedProViewPro.setTypeface(appTypeface.getHind_regular());
        tvTimerText.setTypeface(appTypeface.getHind_semiBold());
        tvProHrText.setTypeface(appTypeface.getHind_regular());
        tvProMinText.setTypeface(appTypeface.getHind_regular());
        tvProSecText.setTypeface(appTypeface.getHind_regular());
        tvCancel.setTypeface(appTypeface.getHind_regular());
        tvLookingProvider.setTypeface(appTypeface.getHind_medium());
        tvCancel.setOnClickListener(view -> {
            Log.d("TAG", "onClickCancel: ");
            cancelBooking.onToCancelBooking(bid,getActivity(), "");
        });
    }

    @Override
    public void providerInfo(String providerId, String name, String proPic, int reviewCount, float rating, double amount, String currencySymbol, int status)
    {
        pId = providerId;
        if(mContext!=null)
            providerInfoCall(name,proPic);
    }


    @Override
    public void proExpiry(String name, String proPic, long expiryTime, long serverTime, LatLng customerLatLng, LatLng proLatLng)
    {

        if(mContext!=null)
        {
         //   providerInfoCall(name,proPic);
            remainIngTime = Utility.timeStamp(expiryTime,serverTime);
            StartTimer();
        }

    }

    @Override
    public void onJobTimer(BookingTimer bookingTimer, long serverTime, String statusMsg, long bid, int status, long totalJobTime) {

        this.bid = bid;
        tvLookingProvider.setText(statusMsg);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnableCode);
        mContext = null;
        if(waveDrawable!=null && waveDrawable.isAnimationRunning())
            waveDrawable.stopAnimation();

    }

    private void providerInfoCall(String name, String proPic)
    {
        if(Constants.bookingModelJobDetails==1)
        {
            waveDrawable = new WaveDrawable(ContextCompat.getColor(mContext, R.color.grey_john), 250);

            tvJobPostedProName.setText("------");
            tvJobPostedProViewPro.setVisibility(View.INVISIBLE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                ivPicWave.setBackgroundDrawable(waveDrawable);
            } else {
                ivPicWave.setBackground(waveDrawable);
            }


            LinearInterpolator interpolator = new LinearInterpolator();
            waveDrawable.setWaveInterpolator(interpolator);
            waveDrawable.startAnimation();

        }else
        {
            ivPicWave.setVisibility(View.GONE);
            tvJobPostedProName.setText(name);
            if(!proPic.equals(""))
            {
                Glide.with(mContext)
                        .load(proPic)
                        .apply(Utility.createGlideOption(mContext))
                        .into(proFilePic);
            }
            tvJobPostedProViewPro.setOnClickListener(view -> {

                Intent intent=new Intent(getActivity(),ProviderDetails.class);
                intent.putExtra("ProId",pId);
                intent.putExtra("isProFileView",true);
                startActivity(intent);

            });
        }

    }

    private void StartTimer()
    {
        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {

                if(remainIngTime>0)
                {
                    handler.postDelayed(this,1000);
                    remainIngTime = remainIngTime-1000;
                    timeRemain(remainIngTime);
                }else
                {
                    handler.removeCallbacks(runnableCode);
                }

            }
        };
        handler.post(runnableCode);
    }

    private void timeRemain(long l) {
        tvTimerText.setText(formatSeconds(l));
    }


    @SuppressLint("DefaultLocale")
    private String formatSeconds(long timeInSeconds) {

       /* long second = (timeInSeconds / 1000) % 60;
        long minute = (timeInSeconds / (1000 * 60)) % 60;
        return twoDigitString(minute) + " : " + twoDigitString(second);*/

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

    private String twoDigitString(long number) {

        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(waveDrawable!=null && waveDrawable.isAnimationRunning())
            waveDrawable.stopAnimation();
        mContext = null;

    }

}

