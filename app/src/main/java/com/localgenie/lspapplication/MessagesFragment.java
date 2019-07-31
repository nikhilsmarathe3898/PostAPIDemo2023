package com.localgenie.lspapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.localgenie.R;
import com.localgenie.jobDetailsStatus.JobDetailsActivity;
import com.localgenie.jobDetailsStatus.JobDetailsOnTheWayContract;
import com.localgenie.jobDetailsStatus.JobProviderInfo;
import com.localgenie.providerdetails.ProviderDetails;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;
import com.pojo.BidDispatchLog;
import com.pojo.BookingAccounting;
import com.pojo.BookingTimer;
import com.pojo.CartInfo;
import com.utility.WaveDrawable;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import adapters.ViewPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends DaggerFragment implements JobProviderInfo,JobProviderInfo.JobBidCalling{//,JobProviderInfo.JobBidCalling


    @BindView(R.id.tabLayoutMyBidding)TabLayout tabLayoutMyBidding;
    @BindView(R.id.viewPagerMyBidding)ViewPager viewPagerMyBidding;
    @BindView(R.id.llJobBidFrag)LinearLayout llJobBidFrag;
    private ViewPagerAdapter viewPagerAdapter;
    private Context mContext;
    private MyBiddingPageFrag biddingResponse, biddingMessage;
    @Inject AppTypeface appTypeface;
    private ArrayList<BidDispatchLog> responseBid = new ArrayList<>();
    private ArrayList<BidDispatchLog> responseMsg = new ArrayList<>();
    JobProviderInfo.JobBidCalling mListener;
    private boolean isBidding = false;
    public String ARG_BOOK_TYPE = "param1";



    /*JobPosted*/
    @BindView(R.id.llJobBidPosted)LinearLayout llJobBidPosted;

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
    @Inject JobDetailsOnTheWayContract.CancelBooking cancelBooking;

    private Handler handler = new Handler();
    private Runnable runnableCode;
    private long bid;
    private WaveDrawable waveDrawable;
    private String pId;
    private  long remainIngTime;
    /*END*/


    @Inject
    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // if (getArguments() != null)
          //  isBidding = getArguments().getBoolean("ISBIDDING",false);
        isBidding = JobDetailsActivity.isBidding;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_messages, container, false);
        ((JobDetailsActivity)mContext).jobDetailProInfo(this);
        ButterKnife.bind(this,layout);
        mContext = getActivity();
        Log.d("TAG", "onCreateViewISBIDDING: "+isBidding);
        if(isBidding)
            initialize();
        else
            initializePostJob();
        return layout;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof JobProviderInfo.JobBidCalling) {
            mListener = (JobProviderInfo.JobBidCalling) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    private void initialize()
    {
        llJobBidPosted.setVisibility(View.GONE);
        llJobBidFrag.setVisibility(View.VISIBLE);
        tabLayoutMyBidding.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayoutMyBidding.setSelectedTabIndicatorHeight(5);
        tabLayoutMyBidding.setSelectedTabIndicatorColor(Utility.getColor(mContext, R.color.parrotGreen));//getResources().getColor(R.color.actionbar_color)
        viewPagerMyBidding.setOffscreenPageLimit(3);
        viewPagerMyBidding.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutMyBidding));
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        tabLayoutMyBidding.setupWithViewPager(viewPagerMyBidding);
        onCreateLoadAdapter();

    }

    private void initializePostJob() {
        llJobBidPosted.setVisibility(View.VISIBLE);
        llJobBidFrag.setVisibility(View.GONE);
        tvJobPostedProName.setTypeface(appTypeface.getHind_medium());
        tvJobPostedProViewPro.setTypeface(appTypeface.getHind_regular());
        tvTimerText.setTypeface(appTypeface.getDigital_clock());
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


    private void onCreateLoadAdapter() {
        biddingResponse = MyBiddingPageFrag.newInstance(0);//pendingPojo
        viewPagerAdapter.addFragment(biddingResponse
                , getActivity().getResources().getString(R.string.BidResponse));

        biddingMessage = MyBiddingPageFrag.newInstance(1);//upComingPojo
        viewPagerAdapter.addFragment(biddingMessage,
                getActivity().getResources().getString(R.string.BidMessage));

        viewPagerMyBidding.setAdapter(viewPagerAdapter);
        ViewGroup vg = (ViewGroup) tabLayoutMyBidding.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int k = 0; k < tabsCount; k++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(k);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(appTypeface.getHind_medium());
                }
            }
        }
    }



    @Override
    public void providerInfo(String providerId, String name, String proPic, int reviewCount, float rating, double amount, String currencySymbol, int status) {

        if(!isBidding)
        {
            pId = providerId;
            if(mContext!=null)
                providerInfoCall(name,proPic);
        }
    }

    private void providerInfoCall(String name, String proPic) {
        if(Constants.bookingModelJobDetails==1)
        {
            waveDrawable = new WaveDrawable(ContextCompat.getColor(mContext, R.color.grey_john), 250);

            tvJobPostedProName.setText("");
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

    @Override
    public void proExpiry(String name, String proPic, long expiryTime, long serverTime, LatLng customerLatLng, LatLng proLatLng) {

        if(!isBidding)
        {
            if(mContext!=null)
            {
                //   providerInfoCall(name,proPic);
                remainIngTime = Utility.timeStamp(expiryTime,serverTime);
                StartTimer();
            }
        }
    }

    private void StartTimer() {
        if(handler!=null && runnableCode!=null)
        {
            handler.removeCallbacks(runnableCode);
        }
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


        int seconds = (int) (timeInSeconds / 1000);
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;


        Log.d("TAG", "formatSeconds: "+hours+" min "+minutes+" sec "+seconds);
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
    @Override
    public void onJobTimer(BookingTimer bookingTimer, long serverTime, String statusMsg, long bid, int status, long totalJobTime) {

        if(!isBidding)
        {
            this.bid = bid;
            tvLookingProvider.setText(statusMsg);
        }
    }

    @Override
    public void onBidJobInfoDetails(ArrayList<BidDispatchLog> bidDispatchLog, int statusCode)
    {
        if(isBidding)
        {
            responseBid.clear();
            responseMsg.clear();
            for(int i = 0 ; i < bidDispatchLog.size(); i++)
            {
                if(bidDispatchLog.get(i).isChat())
                {
                    responseMsg.add(bidDispatchLog.get(i));
                    bidDispatchLog.get(i).setChatAddred(true);
                }
                    bidDispatchLog.get(i).setChatAddred(false);
                responseBid.add(bidDispatchLog.get(i));
            }
            if (biddingResponse != null) {
                biddingResponse.notifyDataAdapter(responseBid,1);
            }
            if (biddingMessage != null) {
                biddingMessage.notifyDataAdapter(responseMsg,2);
            }
        }
    }

    @Override
    public void onJobInfo(int bookingType, BookingAccounting accounting, CartInfo cart, String categoryName) {

    }

    @Override
    public void onJobAddress(String address, Date date) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(!isBidding)
            if(waveDrawable!=null && waveDrawable.isAnimationRunning())
                waveDrawable.stopAnimation();
        mContext = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!isBidding)
        {
            handler.removeCallbacks(runnableCode);
            if(waveDrawable!=null && waveDrawable.isAnimationRunning())
                waveDrawable.stopAnimation();
        }

        mContext = null;
    }

    public void notifyData(ArrayList<BidDispatchLog> bidDispatchLog)
    {
        responseBid.clear();
        responseMsg.clear();
        for(int i = 0 ; i < bidDispatchLog.size(); i++)
        {
            if(bidDispatchLog.get(i).isChat())
            {
                responseMsg.add(bidDispatchLog.get(i));
                bidDispatchLog.get(i).setChatAddred(true);
            }
                bidDispatchLog.get(i).setChatAddred(false);
                responseBid.add(bidDispatchLog.get(i));

        }
        if (biddingResponse != null) {
            biddingResponse.notifyDataAdapter(responseBid,1);
        }
        if (biddingMessage != null) {
            biddingMessage.notifyDataAdapter(responseMsg,2);
        }
    }

    @Override
    public void onIntentCall(Intent intent) {
        mListener.onIntentCall(intent);
    }

}
