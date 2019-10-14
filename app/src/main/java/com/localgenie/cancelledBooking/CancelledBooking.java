package com.localgenie.cancelledBooking;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Utility;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import com.pojo.AllBookingEventPojo;

public class CancelledBooking extends DaggerAppCompatActivity {

    @BindView(R.id.toolbarCancel)Toolbar toolbarCancel;
    @BindView(R.id.bookingStatusId)TextView bookingStatusId;
    @BindView(R.id.bookingStatusDate)TextView bookingStatusDate;

    @BindView(R.id.tvCanceledBookingStatus)TextView tvCanceledBookingStatus;
    @BindView(R.id.tvCancelledTotalAmount)TextView tvCancelledTotalAmount;
    @BindView(R.id.rlCancelledProviderInfo)RelativeLayout rlCancelledProviderInfo;
    @BindView(R.id.ivCancelledProImage)ImageView ivCancelledProImage;
    @BindView(R.id.tvCancelledProName)TextView tvCancelledProName;
    @BindView(R.id.tvCancelledCategory)TextView tvCancelledCategory;
    @BindView(R.id.tvCanceledBookingLocation)TextView tvCanceledBookingLocation;
    @BindView(R.id.tvCanceledBookingLocationInfo)TextView tvCanceledBookingLocationInfo;
    @BindView(R.id.tabs)TabLayout tabs;
    @BindView(R.id.llCancelledBookingReasons)LinearLayout llCancelledBookingReasons;
    @BindView(R.id.tvCancelledBookingRes)TextView tvCancelledBookingRes;
    @BindView(R.id.tvCancelledBookingResInfo)TextView tvCancelledBookingResInfo;
    @BindView(R.id.frame_contain)LinearLayout frame_contain;
    @BindView(R.id.ivJobDetailsCardImageInfo)ImageView ivJobDetailsCardImageInfo;
    @BindView(R.id.lljobDetailsCardPayment)RelativeLayout lljobDetailsCardPayment;
    @BindView(R.id.tvJobDetailsCardInfo)TextView tvJobDetailsCardInfo;
    @BindView(R.id.tvJobDetailsCardInfoAmt)TextView tvJobDetailsCardInfoAmt;
    @BindView(R.id.lljobDetailsWalletPayment)RelativeLayout lljobDetailsWalletPayment;
    @BindView(R.id.ivJobDetailsWalletImageInfo)ImageView ivJobDetailsWalletImageInfo;
    @BindView(R.id.tvJobDetailsWalletInfo)TextView tvJobDetailsWalletInfo;
    @BindView(R.id.tvJobDetailsWalletInfoAmt)TextView tvJobDetailsWalletInfoAmt;
    @BindView(R.id.viewProvider)View viewProvider;
    @BindView(R.id.v_divider)View v_divider;
    private Bundle bundle;

    @Inject
    AppTypeface appTypeface;

    private long bid;
    private int status;
    private ArrayList<AllBookingEventPojo>allBookingEventPojos;
    private static final String TAG = "CancelledBooking";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_booking);
        ButterKnife.bind(this);
        getIntentValue();
        initializeToolBar();
        setupViewPager();
        setCommonValue();
        stTypeFaceValue();

    }


    private void getIntentValue()
    {
        bid = getIntent().getLongExtra("BID",0);
        status = getIntent().getIntExtra("STATUS",0);
        allBookingEventPojos = (ArrayList<AllBookingEventPojo>) getIntent().getSerializableExtra("ALLPOJO");
        String imageUrl = getIntent().getStringExtra("ImageUrl");
        if(imageUrl!=null && !imageUrl.equals(""))
        {
            Glide.with(this)
                    .load(imageUrl)
                    .apply(Utility.createGlideOption(this))
                    .into(ivCancelledProImage);
        }
        if(allBookingEventPojos.get(0).getCallType()==3){
            tvCanceledBookingLocation.setVisibility(View.GONE);
            tvCanceledBookingLocationInfo.setVisibility(View.GONE);
            v_divider.setVisibility(View.GONE);
        }
    }

    private void setCommonValue()
    {
        String name = allBookingEventPojos.get(0).getFirstName()+" "+allBookingEventPojos.get(0).getLastName() ;
        tvCancelledProName.setText(name);
        tvCanceledBookingStatus.setText(allBookingEventPojos.get(0).getStatusMsg());
        tvCancelledBookingRes.setText(allBookingEventPojos.get(0).getStatusMsg());
        String currencySymbol = allBookingEventPojos.get(0).getCurrencySymbol();
        try{
            Utility.setAmtOnRecept(allBookingEventPojos.get(0).getAccounting().getTotal(),tvCancelledTotalAmount,currencySymbol);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        tvCanceledBookingLocationInfo.setText(allBookingEventPojos.get(0).getAddLine1());
        if(status==4 || status == 5 || status == 10)
        {
            tvCancelledBookingResInfo.setVisibility(View.GONE);
        }else
            tvCancelledBookingResInfo.setText(allBookingEventPojos.get(0).getCancellationReason());

       /* if(allBookingEventPojos.get(0).getCancellationReason().equals(""))
        else
            tvCancelledBookingResInfo.setText(allBookingEventPojos.get(0).getCancellationReason());*/

        Log.d("TAG", "setCommonValueSTATUS: "+allBookingEventPojos.get(0).getStatus());


        if(allBookingEventPojos.get(0).getStatus()!=5)
        {
            rlCancelledProviderInfo.setVisibility(View.VISIBLE);
            viewProvider.setVisibility(View.VISIBLE);

            if(allBookingEventPojos.get(0).getStatus()==10)
            {
                tvCancelledCategory.setText(allBookingEventPojos.get(0).getCart().getCategoryName());
            }
        }

        if(allBookingEventPojos.get(0).getAccounting().getPaidByWallet()==1)
        {
            lljobDetailsWalletPayment.setVisibility(View.VISIBLE);
            ivJobDetailsWalletImageInfo.setImageResource(R.drawable.ic_account_balance_wallet_black_24dp);
            tvJobDetailsWalletInfo.setText(getString(R.string.wallet));
            if(status==4 || status == 5 || status == 10 || status == 11 )
            {
                Utility.setAmtOnRecept(allBookingEventPojos.get(0).getAccounting().getCaptureAmount(),tvJobDetailsWalletInfoAmt,
                        allBookingEventPojos.get(0).getCurrencySymbol());
                if(allBookingEventPojos.get(0).getAccounting().getRemainingAmount()>0) {
                    Log.d(TAG, "setCommonValue: "+allBookingEventPojos.get(0).getAccounting().getRemainingAmount());
                    Utility.setAmtOnRecept(allBookingEventPojos.get(0).getAccounting().getRemainingAmount(),
                            tvJobDetailsCardInfoAmt,allBookingEventPojos.get(0).getCurrencySymbol());
                    callCashCard();
                }else {
                    lljobDetailsCardPayment.setVisibility(View.GONE);
                }
            }

        }else
            callCashCard();


        setUiOnStatus();

    }

    private void callCashCard()
    {
        if(allBookingEventPojos.get(0).getAccounting().getPaymentMethod()==1)
        {
            //cash
            ivJobDetailsCardImageInfo.setImageResource(R.drawable.ic_cash_icon);
            tvJobDetailsCardInfo.setText(getString(R.string.cash));


        }else if(allBookingEventPojos.get(0).getAccounting().getPaymentMethod()==2)
        {
            // card
            ivJobDetailsCardImageInfo.setImageResource(R.drawable.ic_menu_payment);
            String jobDetailsInfo = getString(R.string.stars) +" "+allBookingEventPojos.get(0)
                    .getAccounting().getLast4();
            tvJobDetailsCardInfo.setText(jobDetailsInfo);

        }
    }

    private void stTypeFaceValue() {
        tvCancelledBookingRes.setTypeface(appTypeface.getHind_medium());
        tvCancelledBookingResInfo.setTypeface(appTypeface.getHind_regular());
        tvCancelledProName.setTypeface(appTypeface.getHind_regular());
        tvCanceledBookingLocationInfo.setTypeface(appTypeface.getHind_regular());
        tvCanceledBookingLocation.setTypeface(appTypeface.getHind_semiBold());
        tvCancelledTotalAmount.setTypeface(appTypeface.getHind_semiBold());
        tvCanceledBookingStatus.setTypeface(appTypeface.getHind_semiBold());
        tvJobDetailsCardInfo.setTypeface(appTypeface.getHind_regular());
        tvJobDetailsWalletInfo.setTypeface(appTypeface.getHind_regular());
        tvJobDetailsCardInfoAmt.setTypeface(appTypeface.getHind_regular());
        tvJobDetailsWalletInfoAmt.setTypeface(appTypeface.getHind_regular());
        tvCancelledCategory.setTypeface(appTypeface.getHind_regular());
    }

    private void setupViewPager()
    {

        int status = allBookingEventPojos.get(0).getStatus() ;
        TextView tabReceipt;
        if(status == 4 || status == 5 || status == 11)//|| status == 12
        {
            // tabs.addTab(tabs.newTab().setText(getString(R.string.requestedService)));
            tabs.addTab(tabs.newTab().setText(getString(R.string.detailsCaps)));
            tabReceipt = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_custom_text, null);
            /*if(status == 4 || status == 5 || status == 11  || status == 12)*/
            //  tabReceipt.setText(getString(R.string.requestedService));
            tabReceipt.setText(getString(R.string.detailsCaps));
        }
        else
        {
            //tabs.addTab(tabs.newTab().setText("Receipt"));
            tabs.addTab(tabs.newTab().setText(getString(R.string.detailsCaps)));

            tabs.addTab(tabs.newTab().setText(getString(R.string.help)));
            tabReceipt = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_custom_text, null);
            //  tabReceipt.setText("Receipt");
            tabReceipt.setText(getString(R.string.detailsCaps));
            TextView tabHelp = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_custom_text, null);
            tabHelp.setText(getString(R.string.help));
            tabHelp.setTypeface(appTypeface.getHind_semiBold());
            tabs.getTabAt(1).setCustomView(tabHelp);
        }

        tabReceipt.setTypeface(appTypeface.getHind_semiBold());
        tabs.getTabAt(0).setCustomView(tabReceipt);

        setReceipt();
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    setBundle();
                } else if (tab.getPosition() == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putString("edtText", "To Help");
                    bundle.putSerializable("HelpText",allBookingEventPojos.get(0).getHelpReasons());
                    bundle.putLong("BID",allBookingEventPojos.get(0).getBookingId());
                    HelpFragment helpFragment = new HelpFragment();
                    helpFragment.setArguments(bundle);
                    inflateLayout(helpFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setReceipt() {
        bundle = new Bundle();
        bundle.putSerializable("AccountService",allBookingEventPojos.get(0).getCart().getCheckOutItem());
        bundle.putDouble("cancellationFee",allBookingEventPojos.get(0).getAccounting().getCancellationFee());
        bundle.putDouble("AccountDiscountFee",allBookingEventPojos.get(0).getAccounting().getDiscount());
        bundle.putDouble("AccountVisitFee",allBookingEventPojos.get(0).getAccounting().getVisitFee());
        bundle.putDouble("AccountTotalFee",allBookingEventPojos.get(0).getAccounting().getTotal());
        bundle.putDouble("TRAVELFEE",allBookingEventPojos.get(0).getAccounting().getTravelFee());
        bundle.putDouble("BidAmount",allBookingEventPojos.get(0).getAccounting().getBidPrice());
        bundle.putInt("AccountStatus",allBookingEventPojos.get(0).getStatus());
        bundle.putDouble("CartTotal",allBookingEventPojos.get(0).getCart().getTotalAmount());
        bundle.putInt("bookingModel",allBookingEventPojos.get(0).getBookingModel());
        bundle.putString("CartName",allBookingEventPojos.get(0).getCategory());
        bundle.putDouble("RequestedTotal",allBookingEventPojos.get(0).getTotalAmount());
        bundle.putSerializable("AddtionalService",allBookingEventPojos.get(0).getAdditionalService());
        bundle.putInt("CallType",allBookingEventPojos.get(0).getCallType());

        setBundle();
    }
    private void setBundle()
    {
        if(bundle!=null)
        {
            Bundle bundles = new Bundle();

            bundles.putAll(bundle);
            ReceiptFragment receiptFragment = new ReceiptFragment();
            receiptFragment.setArguments(bundle);
            inflateLayout(receiptFragment);

        }
    }

    private void inflateLayout(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_contain, fragment);
        transaction.commit();
    }

    private void initializeToolBar()
    {

        setSupportActionBar(toolbarCancel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");
        String eventBid = getResources().getString(R.string.bookingId)+" "+bid;
        bookingStatusId.setText(eventBid);
        bookingStatusId.setTypeface(appTypeface.getHind_semiBold());
        bookingStatusDate.setTypeface(appTypeface.getHind_regular());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarCancel.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbarCancel.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        timeMethod(bookingStatusDate);
    }



    private void setUiOnStatus()
    {

        switch (status)
        {
            case 4:
                tvCanceledBookingStatus.setTextColor(Utility.getColor(this, R.color.red_login_dark));
                llCancelledBookingReasons.setBackgroundColor(Utility.getColor(this,R.color.red_login_dark));
                break;
            case 5:
                tvCanceledBookingStatus.setTextColor(Utility.getColor(this, R.color.saffron));
                tvCancelledBookingResInfo.setText(getString(R.string.bookingIgnored));
                llCancelledBookingReasons.setBackgroundColor(Utility.getColor(this,R.color.saffron));

                break;
            case 10:
                tvCanceledBookingStatus.setTextColor(Utility.getColor(this, R.color.parrotGreen));
                llCancelledBookingReasons.setBackgroundColor(Utility.getColor(this,R.color.parrotGreen));

                break;
            case 11:
                /*tvCanceledBookingStatus.setTextColor(Utility.getColor(this, R.color.blue_facebook));
                llCancelledBookingReasons.setBackgroundColor(Utility.getColor(this,R.color.blue_facebook));*/
            case 12:
                tvCanceledBookingStatus.setTextColor(Utility.getColor(this, R.color.blue_facebook));
                llCancelledBookingReasons.setBackgroundColor(Utility.getColor(this,R.color.blue_facebook));

                break;
        }
    }

    private void timeMethod(TextView bookingStatusDate)
    {

        try {
            Date date = new Date(allBookingEventPojos.get(0).getBookingRequestedFor() * 1000L);
            bookingStatusDate.setText(Utility.getFormattedDate(date));
        } catch (Exception e) {
            Log.d("TAG", "timeMethodException: " + e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
    }
}
