package com.localgenie.cancelledBooking;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;
import com.pojo.AdditionalService;
import com.pojo.CartInfo;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.SelectedService;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

/**
 * <h>ReceiptFragment</h>
 * Created by Ali on 3/14/2018.
 */

public class ReceiptFragment extends DaggerFragment
{
    Context mContext;


    @BindView(R.id.tvConfirmVisitFee)TextView tvConfirmVisitFee;
    @BindView(R.id.tvConfirmVisitFeeAmt)TextView tvConfirmVisitFeeAmt;
    @BindView(R.id.tvConfirmDiscountFee)TextView tvConfirmDiscountFee;
    @BindView(R.id.tvConfirmDiscountFeeAmt)TextView tvConfirmDiscountFeeAmt;
    @BindView(R.id.tvConfirmTotalFee)TextView tvConfirmTotalFee;
    @BindView(R.id.tvConfirmTotalFeeAmt)TextView tvConfirmTotalFeeAmt;
    @BindView(R.id.tvConfirmTimeFee)TextView tvCancelFee;
    @BindView(R.id.tvConfirmTimeFeeAmt)TextView tvCancelFeeAmt;
    @BindView(R.id.rlCancelFee)RelativeLayout rlCancelFee;
    @BindView(R.id.rlTotalFee)RelativeLayout rlTotalFee;
    @BindView(R.id.recyclerViewService)RecyclerView recyclerViewService;
    @BindView(R.id.rlInvoiceSubTotal)RelativeLayout rlInvoiceSubTotal;
    @BindView(R.id.tvConfirmSubTotal)TextView tvConfirmSubTotal;
    @BindView(R.id.tvConfirmSubTotalFee)TextView tvConfirmSubTotalFee;
    @BindView(R.id.tvRequestedService)TextView tvRequestedService;
    @BindView(R.id.tvConfirmReceipt)TextView tvConfirmReceipt;
    @BindView(R.id.tvConfirmBidAmount)TextView tvConfirmBidAmount;
    @BindView(R.id.rlReceipt)RelativeLayout rlReceipt;
    @BindView(R.id.rlVisitFee)RelativeLayout rlVisitFee;

    @BindView(R.id.rlAdditionalService)RelativeLayout rlAdditionalService;
    @BindView(R.id.containerAdditional)LinearLayout containerAdditional;
    @BindView(R.id.rlTravelFee)RelativeLayout rlTravelFee;
    @BindView(R.id.tvConfirmTravelFee)TextView tvConfirmTravelFee;
    @BindView(R.id.tvConfirmTravelFeeAmt)TextView tvConfirmTravelFeeAmt;



    @Inject
    AppTypeface appTypeface;

    private ArrayList<CartInfo.CheckOutItem> checkOutItems;
    private ArrayList<AdditionalService>additionalServices;
    private double cancelFee,discountFee,totalFee,totalCart, visitFee = 0,travelFee = 0;
    private int status,bookingModel,callType;
    private String catName;
    private double totalBidAmount,bidAmount;



    @Inject
    public ReceiptFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.receipt_fragment, container, false);
        if (getArguments() != null) {

            checkOutItems = (ArrayList<CartInfo.CheckOutItem>) getArguments().getSerializable("AccountService");
            cancelFee = getArguments().getDouble("cancellationFee",0);
            discountFee = getArguments().getDouble("AccountDiscountFee",0);
            visitFee = getArguments().getDouble("AccountVisitFee",0);
            totalFee = getArguments().getDouble("AccountTotalFee",0);
            status = getArguments().getInt("AccountStatus",0);
            totalCart = getArguments().getInt("CartTotal",0);
            additionalServices = (ArrayList<AdditionalService>) getArguments().getSerializable("AddtionalService");
            travelFee = getArguments().getDouble("TRAVELFEE",0);
            bookingModel = getArguments().getInt("bookingModel",0);
            totalBidAmount = getArguments().getDouble("RequestedTotal",0);
            catName = getArguments().getString("CartName");
            bidAmount = getArguments().getDouble("BidAmount",0);
            callType=getArguments().getInt("CallType",0);
            bidAmount = getArguments().getDouble("BidAmount",0);

            Log.i("TAG", " checkOutItems "+checkOutItems);
        }
        ButterKnife.bind(this,view);
        mContext = getActivity();
        setTypeFace();
        setValueToText();
        return view;
    }

    private void setTypeFace()
    {
        tvConfirmTotalFee.setTypeface(appTypeface.getHind_semiBold());
        tvConfirmTotalFeeAmt.setTypeface(appTypeface.getHind_semiBold());
        tvConfirmDiscountFeeAmt.setTypeface(appTypeface.getHind_light());
        tvConfirmDiscountFee.setTypeface(appTypeface.getHind_light());
        tvConfirmVisitFeeAmt.setTypeface(appTypeface.getHind_light());
        tvConfirmTravelFeeAmt.setTypeface(appTypeface.getHind_light());
        tvConfirmTravelFee.setTypeface(appTypeface.getHind_light());
        tvConfirmVisitFee.setTypeface(appTypeface.getHind_light());
        tvCancelFeeAmt.setTypeface(appTypeface.getHind_light());
        tvCancelFee.setTypeface(appTypeface.getHind_light());
        tvConfirmSubTotal.setTypeface(appTypeface.getHind_medium());
        tvConfirmSubTotalFee.setTypeface(appTypeface.getHind_medium());
        tvRequestedService.setTypeface(appTypeface.getHind_medium());
        tvConfirmReceipt.setTypeface(appTypeface.getHind_medium());
        tvConfirmBidAmount.setTypeface(appTypeface.getHind_medium());



        //  tvCancelFee.setVisibility(View.VISIBLE);
        rlTotalFee.setVisibility(View.VISIBLE);
        // tvCancelFeeAmt.setVisibility(View.VISIBLE);

    }

    private void setValueToText()
    {
        rlReceipt.setVisibility(View.VISIBLE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerViewService.setLayoutManager(linearLayoutManager);

        recyclerViewService.setNestedScrollingEnabled(false);

        tvRequestedService.setVisibility(View.VISIBLE);

        if(additionalServices.size()>0)
        {
            rlAdditionalService.setVisibility(View.VISIBLE);
            onAdditionalServiceSet();
        }

        if(status==12)
        {
            rlCancelFee.setVisibility(View.VISIBLE);
            Utility.setAmtOnRecept(cancelFee,tvCancelFeeAmt, Constants.bookingcurrencySymbol);
            rlInvoiceSubTotal.setVisibility(View.VISIBLE);
            Utility.setAmtOnRecept(totalCart,tvConfirmSubTotalFee,Constants.bookingcurrencySymbol);
        }
        if(discountFee>0)
            Utility.setAmtOnRecept(discountFee,tvConfirmDiscountFeeAmt, Constants.bookingcurrencySymbol);
        else
        {
            tvConfirmDiscountFeeAmt.setVisibility(View.GONE);
            tvConfirmDiscountFee.setVisibility(View.GONE);
        }
        if(travelFee>0)
        {
            rlTravelFee.setVisibility(View.VISIBLE);
            Utility.setAmtOnRecept(travelFee,tvConfirmTravelFeeAmt,Constants.bookingcurrencySymbol);
        }
        if(status == 4 || status ==5)
            rlReceipt.setVisibility(View.GONE);
        if(status==4 || status == 5 || status == 11 || status == 12)
        {
            tvConfirmTotalFeeAmt.setVisibility(View.GONE);
            tvConfirmTotalFee.setVisibility(View.GONE);
            rlVisitFee.setVisibility(View.GONE);
            rlReceipt.setVisibility(View.GONE);
        }else
        {
            if(visitFee>0)
                Utility.setAmtOnRecept(visitFee,tvConfirmVisitFeeAmt, Constants.bookingcurrencySymbol);
            else
                rlVisitFee.setVisibility(View.GONE);
            Utility.setAmtOnRecept(totalFee,tvConfirmTotalFeeAmt, Constants.bookingcurrencySymbol);
        }
        if(bookingModel!=3)
        {
            SelectedService selectedService = new SelectedService(getActivity(),false);
            selectedService.onCheckOutItem(checkOutItems);
            if(callType==1||callType==3){
                selectedService.onInCallValue(1);
            }
            recyclerViewService.setAdapter(selectedService);
        }else {
            rlReceipt.setVisibility(View.VISIBLE);
            tvConfirmBidAmount.setVisibility(View.VISIBLE);
            tvConfirmReceipt.setText(catName);
            Utility.setAmtOnRecept(bidAmount,tvConfirmBidAmount, Constants.bookingcurrencySymbol);
        }

    }

    private void onAdditionalServiceSet()
    {
        for(int i = 0 ; i<additionalServices.size(); i++)
        {
            TextView tvAddtionName,tvAddtionPrice;
            View view = LayoutInflater.from(mContext).inflate(R.layout.additiona_service,rlAdditionalService,false);
            containerAdditional.addView(view);

            tvAddtionPrice = view.findViewById(R.id.tvAddtionPrice);
            tvAddtionName = view.findViewById(R.id.tvAddtionName);
            tvAddtionPrice.setTypeface(appTypeface.getHind_light());
            tvAddtionName.setTypeface(appTypeface.getHind_light());
            Log.d("Shijen", "onAdditionalServiceSet: "+additionalServices.get(i).getServiceName());
            tvAddtionName.setText(additionalServices.get(i).getServiceName());
            Utility.setAmtOnRecept(additionalServices.get(i).getPrice(),tvAddtionPrice,Constants.bookingcurrencySymbol);
        }
    }
}
