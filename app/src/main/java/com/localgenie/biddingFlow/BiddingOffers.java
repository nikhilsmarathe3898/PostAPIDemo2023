package com.localgenie.biddingFlow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.model.QuestionList;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;
import com.utility.AlertProgress;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BiddingOffers extends AppCompatActivity {


    @BindView(R.id.llMainOfferBidding)LinearLayout llMainOfferBidding;

    @BindView(R.id.biddingOffer)Toolbar biddingOffer;
    @BindView(R.id.tv_center)TextView tvCenter;
    @BindView(R.id.tvQuestionDesc)TextView tvQuestionDesc;

    @BindView(R.id.glBudgetAll)GridLayout glBudgetAll;
    @BindView(R.id.ivBudgetAllCheck)ImageView ivBudgetAllCheck;
    @BindView(R.id.tvBudgetAllNow)TextView tvBudgetAllNow;
    @BindView(R.id.tvBudgetAllNowDesc)TextView tvBudgetAllNowDesc;
    @BindView(R.id.rlAddAtRunTime)RelativeLayout rlAddAtRunTime;

    @BindView(R.id.glBudgetPerShift)GridLayout glBudgetPerShift;
    @BindView(R.id.ivBudgetPerShiftCheck)ImageView ivBudgetPerShiftCheck;
    @BindView(R.id.tvBudgetPerShift)TextView tvBudgetPerShift;
    @BindView(R.id.tvBudgetPerShiftDesc)TextView tvBudgetPerShiftDesc;
    @BindView(R.id.rlPerShiftAddAtRunTime)RelativeLayout rlPerShiftAddAtRunTime;

    @BindView(R.id.glBudgetPerHr)GridLayout glBudgetPerHr;
    @BindView(R.id.ivBudgetPerHrCheck)ImageView ivBudgetPerHrCheck;
    @BindView(R.id.tvBudgetPerHr)TextView tvBudgetPerHr;
    @BindView(R.id.tvBudgetPerHrDesc)TextView tvBudgetPerHrDesc;
    @BindView(R.id.rlPerHrAddAtRunTime)RelativeLayout rlPerHrAddAtRunTime;

    @BindView(R.id.tvBiddingOfferNxt)TextView tvBiddingOfferNxt;
    private AlertProgress alertProgress;

    private ArrayList<QuestionList>questionLists;
    private AppTypeface appTypeface;
    private int questionPosition;
    private double totalAmount;
    private  int hour, hrNHalf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidding_offers);
        ButterKnife.bind(this);
        appTypeface = AppTypeface.getInstance(this);
        getIntentValue();
        toolBar();
        setTypeFace();
        alertProgress = new AlertProgress(this);

    }

    private void getIntentValue() {
        questionLists = (ArrayList<QuestionList>) getIntent().getSerializableExtra("QUESTIONS");
        questionPosition = getIntent().getIntExtra("questionPosition",0);

    }

    private void toolBar() {
        setSupportActionBar(biddingOffer);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvCenter.setText(Constants.catName);
        biddingOffer.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        biddingOffer.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void setTypeFace() {

        tvCenter.setTypeface(appTypeface.getHind_semiBold());
        tvQuestionDesc.setTypeface(appTypeface.getHind_semiBold());
        tvBiddingOfferNxt.setTypeface(appTypeface.getHind_semiBold());
        tvBudgetAllNow.setTypeface(appTypeface.getHind_medium());
        tvBudgetAllNowDesc.setTypeface(appTypeface.getHind_light());
        tvBudgetPerShift.setTypeface(appTypeface.getHind_medium());
        tvBudgetPerShiftDesc.setTypeface(appTypeface.getHind_light());
        tvBudgetPerHr.setTypeface(appTypeface.getHind_medium());
        tvBudgetPerHrDesc.setTypeface(appTypeface.getHind_light());
        tvQuestionDesc.setText(questionLists.get(questionPosition).getQuestion());

    }

    @OnClick ({R.id.glBudgetAll,R.id.glBudgetPerShift,R.id.glBudgetPerHr,R.id.tvBiddingOfferNxt})
    public void onClickGrid(View v)
    {
        switch (v.getId())
        {
            case R.id.glBudgetAll:
                onViewSet(1);
                onInflateView(1);
                break;

            case R.id.glBudgetPerShift:
                onViewSet(2);
                onInflateView(2);
                break;

            case R.id.glBudgetPerHr:
                onViewSet(3);
                onInflateView(3);
                break;
            case R.id.tvBiddingOfferNxt:
                Utility.hideKeyboard(this);
                if(totalAmount>0)
                {
                    Intent intent = new Intent();
                    intent.putExtra("TATALBIDDINGAMT",totalAmount);
                    setResult(RESULT_OK,intent);
                    overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);
                    finish();
                }else
                    alertProgress.alertinfo(this,getString(R.string.pleaseSetBiddingAmount));
                //  overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
                break;
        }
    }

    private void onInflateView(int i)
    {
        rlAddAtRunTime.removeAllViews();
        rlPerShiftAddAtRunTime.removeAllViews();
        rlPerHrAddAtRunTime.removeAllViews();
        switch (i)
        {
            case 1:
                inflater(rlAddAtRunTime,"b");
                break;

            case 2:
                inflater(rlPerShiftAddAtRunTime,"s");
                break;
            case 3:
                inflater(rlPerHrAddAtRunTime,"h");
                break;
        }
    }
    private String pricePerShifts;
    private double amountPerShift;
    private void inflater(RelativeLayout rlAddAtRunTime, String isA)
    {
        EditText etOfferAmount;
        ImageView nextImage;
        CardView cardAmountTotal;

        TextView tvAmountTotal,  NoOfBiddingShift,NoOfBiddingShifts,  tvPricePerShifts,tvPricePerShiftsAmt,
                tvBiddingDuration,  tvBiddingDurationHR,tvTotalInfo;
        View v = LayoutInflater.from(this).inflate(R.layout.offer_bidding_edit,llMainOfferBidding,false);
        rlAddAtRunTime.addView(v);
        tvTotalInfo = v.findViewById(R.id.tvTotalInfo);
        etOfferAmount = v.findViewById(R.id.etOfferAmount);
        nextImage = v.findViewById(R.id.nextImage);
        cardAmountTotal = v.findViewById(R.id.cardAmountTotal);
        tvAmountTotal = v.findViewById(R.id.tvAmountTotal);

        NoOfBiddingShift = v.findViewById(R.id.NoOfBiddingShift);
        NoOfBiddingShifts = v.findViewById(R.id.NoOfBiddingShifts);

        tvPricePerShifts = v.findViewById(R.id.tvPricePerShifts);
        tvPricePerShiftsAmt = v.findViewById(R.id.tvPricePerShiftsAmt);

        tvBiddingDuration = v.findViewById(R.id.tvBiddingDuration);
        tvBiddingDurationHR = v.findViewById(R.id.tvBiddingDurationHR);
        NoOfBiddingShifts.setText(Constants.repeatNumOfShift+"");
        hour = Constants.scheduledTime/60;
        hrNHalf = Constants.scheduledTime%60;

        String duration = hour+" hr : "+hrNHalf+" mn";
        tvBiddingDurationHR.setText(duration);

        if(isA.equals("b"))
        {
            nextImage.setVisibility(View.INVISIBLE);
            cardAmountTotal.setVisibility(View.INVISIBLE);
            tvTotalInfo.setVisibility(View.INVISIBLE);
        }
        etOfferAmount.setText(Constants.currencySymbol+" ");
        Selection.setSelection(etOfferAmount.getText(), etOfferAmount.getText().length());

        etOfferAmount.setTypeface(appTypeface.getHind_bold());
        tvAmountTotal.setTypeface(appTypeface.getHind_bold());

        NoOfBiddingShift.setTypeface(appTypeface.getHind_regular());
        NoOfBiddingShifts.setTypeface(appTypeface.getHind_semiBold());

        tvPricePerShifts.setTypeface(appTypeface.getHind_regular());
        tvPricePerShiftsAmt.setTypeface(appTypeface.getHind_semiBold());

        tvBiddingDuration.setTypeface(appTypeface.getHind_regular());
        tvBiddingDurationHR.setTypeface(appTypeface.getHind_semiBold());
        tvTotalInfo.setTypeface(appTypeface.getHind_light());


        etOfferAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith(Constants.currencySymbol+" ")){
                    etOfferAmount.setText(Constants.currencySymbol+" ");
                    Selection.setSelection(etOfferAmount.getText(), etOfferAmount.getText().length());
                }

                String splitAmount[] = etOfferAmount.getText().toString().split(" ");

                try
                {
                    if(splitAmount.length>1)
                    {

                        int intAmount = Integer.parseInt(splitAmount[1]);
                        switch (isA)
                        {
                            case "b":
                                amountPerShift = intAmount/Constants.repeatNumOfShift;
                                pricePerShifts = Constants.currencySymbol +" "+ amountPerShift;
                                tvPricePerShiftsAmt.setText(pricePerShifts);
                                totalAmount = amountPerShift;
                                break;
                            case "s":
                                amountPerShift = intAmount*Constants.repeatNumOfShift;
                                pricePerShifts = Constants.currencySymbol +" "+ amountPerShift;
                                String totalHrAmtShifts = Constants.currencySymbol +" "+ intAmount;
                                tvAmountTotal.setText(pricePerShifts);
                                tvPricePerShiftsAmt.setText(totalHrAmtShifts);
                                totalAmount = intAmount;
                                break;
                            case "h":
                                double  totalHrAmt = (intAmount*hour)+(intAmount*((double)hrNHalf/(double) 60));
                                amountPerShift = totalHrAmt ;//intAmount/Constants.repeatNumOfShift;
                                totalHrAmt = totalHrAmt * Constants.repeatNumOfShift;
                                pricePerShifts = Constants.currencySymbol +" "+ amountPerShift;
                                String totalHrAmount = Constants.currencySymbol +" "+totalHrAmt;
                                tvAmountTotal.setText(totalHrAmount);
                                tvPricePerShiftsAmt.setText(pricePerShifts);
                                totalAmount = amountPerShift;// (int) totalHrAmt;
                                break;
                        }

                    }else
                    {
                        tvAmountTotal.setText(Constants.currencySymbol+" 0");
                        tvPricePerShiftsAmt.setText(Constants.currencySymbol+" 0");
                        totalAmount = 0;
                    }
                }catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }



            }
        });

    }

    private void onViewSet(int i)
    {
        ivBudgetAllCheck.setSelected(false);
        ivBudgetPerShiftCheck.setSelected(false);
        ivBudgetPerHrCheck.setSelected(false);
        tvBudgetAllNow.setSelected(false);
        tvBudgetPerShift.setSelected(false);
        tvBudgetPerHr.setSelected(false);
        tvBudgetAllNowDesc.setVisibility(View.GONE);
        tvBudgetPerShiftDesc.setVisibility(View.GONE);
        tvBudgetPerHrDesc.setVisibility(View.GONE);
        switch (i)
        {
            case 1:
                ivBudgetAllCheck.setSelected(true);
                tvBudgetAllNow.setSelected(true);
                tvBudgetAllNowDesc.setVisibility(View.VISIBLE);
                break;

            case 2:
                ivBudgetPerShiftCheck.setSelected(true);
                tvBudgetPerShift.setSelected(true);
                tvBudgetPerShiftDesc.setVisibility(View.VISIBLE);
                break;
            case 3:
                ivBudgetPerHrCheck.setSelected(true);
                tvBudgetPerHr.setSelected(true);
                tvBudgetPerHrDesc.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        Utility.hideKeyboard(this);
        Intent intent = new Intent();
        setResult(RESULT_FIRST_USER,intent);
        overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);
        finish();

    }
}
