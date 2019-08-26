package com.localgenie.jobDetailsStatus;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import javax.inject.Inject;

import adapters.SelectedService;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

import com.pojo.BidDispatchLog;
import com.pojo.BookingAccounting;
import com.pojo.BookingTimer;
import com.pojo.CartInfo;

import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProviderHiredFragment extends DaggerFragment implements View.OnClickListener,JobProviderInfo {



    @BindView(R.id.ivProfilePic)ImageView ivProfilePic;
    @BindView(R.id.tvName)TextView tvName;
    @BindView(R.id.callBtn)Button callBtn;
    @BindView(R.id.msgBtn)Button msgBtn;
    @BindView(R.id.tvCancel)TextView tvCancel;
    @BindView(R.id.tvJobPostedProViewPro)TextView tvJobPostedProViewPro;
    @BindView(R.id.tvMsgCount)TextView tvMsgCount;


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


    @Inject JobDetailsOnTheWayContract.CancelBooking cancelBooking;
    @Inject
    AppTypeface appTypeface;
    @Inject
    SessionManagerImpl manager;
    private Context mContext;
    private long bid = 0;
    private  String proId;


    @Inject
    public ProviderHiredFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_provider_hired, container, false);
        mContext = getActivity();
        ((JobDetailsActivity)mContext).jobDetailProInfo(this);
        ButterKnife.bind(this,layout);
        initialize(layout);
        typeFace();
        return layout;
    }

    private void typeFace() {

        LinearLayoutManager llManager = new LinearLayoutManager(mContext);
        recyclerViewReuestedService.setLayoutManager(llManager);

        tvJobRequestedService.setTypeface(appTypeface.getHind_semiBold());
        tvRequestedBiddingService.setTypeface(appTypeface.getHind_light());
        tvRequestedBiddingServiceAmt.setTypeface(appTypeface.getHind_light());
        tvVisitFee.setTypeface(appTypeface.getHind_light());
        tvVisitFeeAmt.setTypeface(appTypeface.getHind_light());
        tvTravelFeeFee.setTypeface(appTypeface.getHind_light());
        tvTravelFeeFeeAmt.setTypeface(appTypeface.getHind_light());
        tvDiscountFee.setTypeface(appTypeface.getHind_light());
        tvDiscountFeeAmt.setTypeface(appTypeface.getHind_light());
        tvTotalFee.setTypeface(appTypeface.getHind_semiBold());
        tvTotalFeeAmt.setTypeface(appTypeface.getHind_semiBold());
        tvRequestedPayment.setTypeface(appTypeface.getHind_semiBold());
        tvRequestedPaymentMethod.setTypeface(appTypeface.getHind_semiBold());
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
    }

    private void initialize(View layout) {
        callBtn.setOnClickListener(this);
        msgBtn.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvJobPostedProViewPro.setOnClickListener(this);
        ivProfilePic.setVisibility(View.VISIBLE);
        tvName.setTypeface(appTypeface.getHind_semiBold());
        callBtn.setTypeface(appTypeface.getHind_regular());
        msgBtn.setTypeface(appTypeface.getHind_regular());
        tvCancel.setTypeface(appTypeface.getHind_regular());
        tvJobPostedProViewPro.setTypeface(appTypeface.getHind_medium());
        tvMsgCount.setTypeface(appTypeface.getHind_medium());



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.callBtn:
                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                intentCall.setData(Uri.parse("tel:" +JobDetailsActivity.phoneNumber));
                startActivity(intentCall);
             //   Toast.makeText(getContext(),"Call Button clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.msgBtn:
                if(!manager.getChatProId().equals(""))
                {
                    Intent intent = new Intent(mContext, ChattingActivity.class);
                    startActivity(intent);
                    ((Activity)mContext).overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
                }
                break;
            case R.id.tvCancel:
                cancelBooking.onToCancelBooking(bid,getActivity(), "");
                break;
            case R.id.tvJobPostedProViewPro:
                Intent intent = new Intent(getActivity(), ProviderDetails.class);
                intent.putExtra("ProId",proId);
                intent.putExtra("isProFileView",true);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void providerInfo(String providerId, String name, String proPic, int reviewCount, float rating, double amount, String currencySymbol, int status) {

        if(mContext!=null){
            proId = providerId;
            providerInfoCall(name,proPic);
        }
    }

    @Override
    public void proExpiry(String name, String proPic, long expiryTime, long serverTime, LatLng customerLatLng, LatLng proLatLng) {

        if(mContext!=null)
            providerInfoCall(name,proPic);
    }

    @Override
    public void onJobTimer(BookingTimer bookingTimer, long serverTime, String statusMsg, long bid, int status, long totalJobTime) {

        if(mContext!=null){
            this.bid = bid;
            if(manager.getChatCount(bid)>0)
            {
                String count = ""+manager.getChatCount(bid);
                tvMsgCount.setText(count);
            }else
                tvMsgCount.setVisibility(View.GONE);
         //   tvYouHired.setText(statusMsg);
        }

    }

    @Override
    public void onBidJobInfoDetails(ArrayList<BidDispatchLog> bidDispatchLog, int statusCode) {

    }

    @Override
    public void onJobInfo(int bookingType, BookingAccounting accounting, CartInfo cart, String categoryName)
    {
        if(mContext!=null)
        {
            Log.d("TAG", "onJobInfo: "+bookingType);
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
        mContext = null;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContext = null;
    }
}
