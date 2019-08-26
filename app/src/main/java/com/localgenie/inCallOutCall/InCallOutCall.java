package com.localgenie.inCallOutCall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.bookingtype.BookingType;
import com.localgenie.model.CallType;
import com.localgenie.model.Category;
import com.localgenie.model.Offers;
import com.localgenie.model.SubCategory;
import com.localgenie.providerList.ProviderList;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InCallOutCall extends AppCompatActivity {

    @BindView(R.id.toolInCallOutCall)Toolbar toolInCallOutCall;
    @BindView(R.id.tvBookingTypeWhenYOu)TextView tvBookingTypeWhenYOu;

    @BindView(R.id.glInCall)GridLayout glInCall;
    @BindView(R.id.ivInCallCheck)ImageView ivInCallCheck;
    @BindView(R.id.tvInCall)TextView tvInCall;
    @BindView(R.id.tvInCallDesc)TextView tvInCallDesc;
    @BindView(R.id.veInCallDivider)View veInCallDivider;

    @BindView(R.id.glOutCall)GridLayout glOutCall;
    @BindView(R.id.ivOutCheck)ImageView ivOutCheck;
    @BindView(R.id.tvOutCall)TextView tvOutCall;
    @BindView(R.id.tvOutCallDesc)TextView tvOutCallDesc;
    @BindView(R.id.veOutCallDivider)View veOutCallDivider;


    @BindView(R.id.glTeleCall)GridLayout glTeleCall;
    @BindView(R.id.ivTeleCallCheck)ImageView ivTeleCallCheck;
    @BindView(R.id.tvTeleCall)TextView tvTeleCall;
    @BindView(R.id.tvTeleCallDesc)TextView tvTeleCallDesc;

    @BindView(R.id.tvINCallOutCallNext)TextView tvINCallOutCallNext;
    @BindView(R.id.tv_center)TextView tv_center;


    private String catId;
    private double minAmount,maxAmount;
    private ArrayList<SubCategory>subCategoryArrayList;
    private ArrayList<Offers>offers;
    private Category.BookingTypeAction bookingTypeAction;
    private CallType callType;
    private Date date;
    private static final String TAG = "InCallOutCall";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_call_out_call);
        ButterKnife.bind(this);
        getIntentValue();
        initializeTypeFace();
        toolBarValue();
        Constants.bookingType=1;
    }

    private void toolBarValue()
    {
        date = new Date();
        tv_center.setText(Constants.catName);
        setSupportActionBar(toolInCallOutCall);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolInCallOutCall.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolInCallOutCall.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getIntentValue() {

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            bookingTypeAction = (Category.BookingTypeAction) bundle.getSerializable("BookingTypeAction");
            callType = (CallType) bundle.getSerializable("CallType");
            offers = (ArrayList<Offers>) bundle.getSerializable("OFFERS");
            subCategoryArrayList = (ArrayList<SubCategory>)bundle.getSerializable("SubCat");
            catId = bundle.getString("CatId");
            minAmount = bundle.getDouble("MinAmount",0);
            maxAmount = bundle.getDouble("MaxAmount",0);
            Log.d("TAG", "getIntentValue: "+minAmount +" "+maxAmount);
            Constants.catId = catId;

        }

        showCallType();
    }

    private void showCallType() {

        if(callType.isIncall() && callType.isOutcall() && !callType.isTelecall())
        {
            glInCall.setVisibility(View.VISIBLE);
            veInCallDivider.setVisibility(View.VISIBLE);
            glOutCall.setVisibility(View.VISIBLE);
            veOutCallDivider.setVisibility(View.VISIBLE);
        }else if(callType.isIncall() && !callType.isOutcall() && callType.isTelecall())
        {
            glInCall.setVisibility(View.VISIBLE);
            veInCallDivider.setVisibility(View.VISIBLE);
            glTeleCall.setVisibility(View.VISIBLE);
        }else if(!callType.isIncall() && callType.isOutcall() && callType.isTelecall())
        {
            glOutCall.setVisibility(View.VISIBLE);
            veOutCallDivider.setVisibility(View.VISIBLE);
            glTeleCall.setVisibility(View.VISIBLE);
        }else
        {
            glInCall.setVisibility(View.VISIBLE);
            veInCallDivider.setVisibility(View.VISIBLE);
            glOutCall.setVisibility(View.VISIBLE);
            veOutCallDivider.setVisibility(View.VISIBLE);
            glTeleCall.setVisibility(View.VISIBLE);
        }

    }

    private void initializeTypeFace()
    {
        AppTypeface appTypeface = AppTypeface.getInstance(this);
        tvBookingTypeWhenYOu.setTypeface(appTypeface.getHind_semiBold());
        tvInCall.setTypeface(appTypeface.getHind_semiBold());
        tvInCallDesc.setTypeface(appTypeface.getHind_regular());
        tvOutCall.setTypeface(appTypeface.getHind_semiBold());
        tvOutCallDesc.setTypeface(appTypeface.getHind_regular());
        tvINCallOutCallNext.setTypeface(appTypeface.getHind_semiBold());
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        tvTeleCall.setTypeface(appTypeface.getHind_semiBold());
        tvTeleCallDesc.setTypeface(appTypeface.getHind_regular());

        inCall();
    }


    @OnClick({R.id.glInCall,R.id.glOutCall,R.id.tvINCallOutCallNext,R.id.glTeleCall})
    public void onClickGrid(View view)
    {
        switch (view.getId())
        {
            case R.id.glInCall:
                inCall();
                break;
            case R.id.glOutCall:
                outCall();
                break;
            case R.id.glTeleCall:
                teleCall();
                break;
            case R.id.tvINCallOutCallNext:
                Bundle bundle = new Bundle();
                Intent intent;
                if(Constants.jobType != 2)
                {
                    Constants.bookingType = 2;
                    Constants.scheduledDate = ""+getStartOfDayInMillisToday(date)/1000;
                    intent = new Intent(this, ProviderList.class);
                    Log.d(TAG, "onClickGrid: CatId:"+catId+" subcat:"+subCategoryArrayList+ " MinAmount:"+minAmount+
                            " maxAmount: "+maxAmount);
                    intent.putExtra("CatId",catId);
                    intent.putExtra("SubCat",subCategoryArrayList);
                    intent.putExtra("MinAmount",minAmount);
                    intent.putExtra("MaxAmount",maxAmount);
                    startActivity(intent);

                }else
                {
                    intent = new Intent(this, BookingType.class);
                    bundle.putString("CatId",catId);
                    bundle.putSerializable("SubCat",subCategoryArrayList);
                    bundle.putDouble("MinAmount",minAmount);
                    bundle.putDouble("MaxAmount",maxAmount);
                    bundle.putSerializable("BookingTypeAction",bookingTypeAction);
                    bundle.putSerializable("OFFERS",offers);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
                break;
        }
    }

    private void inCall()
    {
        Constants.jobType = 1;

        ivInCallCheck.setSelected(true);
        tvInCall.setSelected(true);
        ivOutCheck.setSelected(false);
        tvOutCall.setSelected(false);
        ivTeleCallCheck.setSelected(false);
        tvTeleCall.setSelected(false);
    }

    private void outCall()
    {
        Constants.jobType = 2;

        ivInCallCheck.setSelected(false);
        tvInCall.setSelected(false);
        ivOutCheck.setSelected(true);
        tvOutCall.setSelected(true);
        ivTeleCallCheck.setSelected(false);
        tvTeleCall.setSelected(false);
    }
    private void teleCall()
    {
        Constants.jobType = 3;

        ivInCallCheck.setSelected(false);
        tvInCall.setSelected(false);
        ivOutCheck.setSelected(false);
        tvOutCall.setSelected(false);
        ivTeleCallCheck.setSelected(true);
        tvTeleCall.setSelected(true);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
      //  overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
        overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);

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
}
