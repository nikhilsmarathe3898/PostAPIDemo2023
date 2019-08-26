package com.localgenie.selectPaymentMethod;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.home.ServicesFrag;
import com.localgenie.model.payment_method.CardGetData;
import com.localgenie.payment_details.PaymentDetailActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.wallet.WalletActivity;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.CardListAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

public class SelectPayment extends DaggerAppCompatActivity implements SelectedCardInfoInterface.SelectedView{

    //   @BindView(R.id.viewCardPayment)View viewCardPayment;


    private int paymentType;
    @BindView(R.id.tvWalletBalance)TextView tvWalletBalance;
    @BindView(R.id.tvWalletBalanceAmt)TextView tvWalletBalanceAmt;
    @BindView(R.id.tvConfirmAddWalletBalance)TextView tvConfirmAddWalletBalance;
    //  @BindView(R.id.rlConfirmCard)LinearLayout rlConfirmCard;
    @BindView(R.id.llConfirmCard)LinearLayout llConfirmCard;
    @BindView(R.id.tvConfirmPaymentCard)TextView tvConfirmPaymentCard;
    @BindView(R.id.tvConfirmPaymentWallet)TextView tvConfirmPaymentWallet;
    //  @BindView(R.id.tvSelectedWalletPay)TextView tvSelectedWalletPay;

    @BindView(R.id.tvConfirmPaymentCash)TextView tvConfirmPaymentCash;

    @BindView(R.id.tvSelectedCashPay)TextView tvSelectedCashPay;

    @BindView(R.id.tv_center)TextView tv_center;


    @BindView(R.id.toolBarSelect)Toolbar toolBarSelect;

    @BindView(R.id.recyclerViewCard)RecyclerView recyclerViewCard;

    @BindView(R.id.progressPayment)ProgressBar progressPayment;

    @BindView(R.id.tvConfirmAddMoreCard) TextView tvConfirmAddMoreCard;

    @BindView(R.id.llSelectCard) LinearLayout llSelectCard;
    @BindView(R.id.viewSelectCard) View viewSelectCard;
    @BindView(R.id.llSelectWallet) LinearLayout llSelectWallet;
    @BindView(R.id.viewSelectWallet) View viewSelectWallet;
    @BindView(R.id.walletBottom) ConstraintLayout walletBottom;
    @BindView(R.id.tvExcessAmntDesc)TextView tvExcessAmntDesc;
    @BindView(R.id.btn_card)TextView btn_card;
    @BindView(R.id.btn_cash)Button btn_cash;

    /*********************************************/
    //@BindView(R.id.llConfirmCard)LinearLayout llConfirmCard;
    @BindView(R.id.tvPleaseChoosePayment)TextView tvPleaseChoosePayment;
    /*********************************************/


    @Inject AppTypeface appTypeface;
    @Inject SelectedCardInfoInterface.SelectedPresenter presenter;
    @Inject
    AlertProgress alertProgress;
    private double softLimit = 0, hardLimit = 0, balance = 0;

    private CardListAdapter cardListAdapter;
    private ArrayList<CardGetData> cardItem = new ArrayList<>();
    private BottomSheetBehavior sheetBehavior;
    private int paymentTypeSelected = 0;
    private int cardSelected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payment);
        ButterKnife.bind(this);
        typeFaceSetValue();
        toolBarSetValue();
        setVisiblity();

    }

    private void setVisiblity()
    {

        if(ServicesFrag.paymentMode.isCard())
        {

            llSelectCard.setVisibility(View.VISIBLE);
            viewSelectCard.setVisibility(View.VISIBLE);
        }
        if(ServicesFrag.paymentMode.isWallet())
        {
            if(Constants.bookingType!=3)
            {
                llSelectWallet.setVisibility(View.VISIBLE);
                viewSelectWallet.setVisibility(View.VISIBLE);
            }

        }
        if(ServicesFrag.paymentMode.isCash())
        {
            if(Constants.callType != 3 && Constants.jobType != 3)
                tvConfirmPaymentCash.setVisibility(View.VISIBLE);


            /*if(Constants.jobType != 3)
                tvConfirmPaymentCash.setVisibility(View.VISIBLE);*/
        }


        sheetBehavior = BottomSheetBehavior.from(walletBottom);
    }

    private void toolBarSetValue()
    {
        setSupportActionBar(toolBarSelect);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_center.setText(getString(R.string.payment_method));
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        toolBarSelect.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolBarSelect.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void typeFaceSetValue()
    {
        tvConfirmPaymentCard.setTypeface(appTypeface.getHind_bold());
        tvConfirmPaymentCash.setTypeface(appTypeface.getHind_bold());
        tvConfirmPaymentWallet.setTypeface(appTypeface.getHind_bold());
        tvWalletBalanceAmt.setTypeface(appTypeface.getHind_regular());

        tvPleaseChoosePayment.setTypeface(appTypeface.getHind_semiBold());
        tvConfirmAddMoreCard.setTypeface(appTypeface.getHind_semiBold());
        tvConfirmAddWalletBalance.setTypeface(appTypeface.getHind_semiBold());
        tvSelectedCashPay.setTypeface(appTypeface.getHind_semiBold());

        //    tvSelectedWalletPay.setTypeface(appTypeface.getHind_semiBold());
        tvExcessAmntDesc.setTypeface(appTypeface.getHind_medium());
        btn_cash.setTypeface(appTypeface.getHind_semiBold());
        btn_card.setTypeface(appTypeface.getHind_semiBold());

        LinearLayoutManager llManager = new LinearLayoutManager(this);
        recyclerViewCard.setLayoutManager(llManager);
        cardListAdapter = new CardListAdapter(this,cardItem,this);
        recyclerViewCard.setAdapter(cardListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utility.isNetworkAvailable(this))
        {
            onShowProgress();
            presenter.onGetCards();
            presenter.getWalletAmount();
        }else
            alertProgress.showNetworkAlert(this);

    }

    @OnClick({R.id.tvConfirmPaymentCard,R.id.tvConfirmPaymentCash,
            R.id.tvConfirmPaymentWallet,R.id.tvConfirmAddMoreCard,
            R.id.tvSelectedCashPay,R.id.tvConfirmAddWalletBalance
            ,R.id.btn_card,R.id.btn_cash})// R.id.tvSelectedWalletPay,
    //,R.id.tvConfirmPaymentWallet,
    public void onPaymentSelection(View v)
    {
        Intent intent;
        Context mContext;
        switch (v.getId())
        {
            case R.id.tvSelectedCashPay:

                onIntentCallForThePay();

                break;
            case R.id.btn_card:
                if(cardItem.size()>0) {
                    paymentType = 2;
                    callCollapse(paymentType);
                }else
                    alertProgress.alertinfo(this,getString(R.string.pleaseAddCardToPay));
                break;
            case R.id.btn_cash:
                paymentType = 1;
                callCollapse(paymentType);
                break;

            /*case R.id.tvSelectedWalletPay:
                callExpandMethod();
                break;*/

            case R.id.tvConfirmAddMoreCard:
                onToCallIntent();
                break;
            case R.id.tvConfirmAddWalletBalance:
                startActivity();
                break;
            case R.id.tvConfirmPaymentCard:
                setPaymentSelectionType(1);
                paymentTypeSelected = 1;
                cardSelected = -1;
                break;
            case R.id.tvConfirmPaymentCash:
                setPaymentSelectionType(2);
                cardSelected = -1;
                mContext = this;
                presenter.setCashCardBookingView(2,balance,softLimit,hardLimit,mContext,alertProgress);
                break;
            case R.id.tvConfirmPaymentWallet:
                setPaymentSelectionType(3);
                cardSelected = -1;
                //for wallet payment

                mContext = this;
                presenter.setCashCardBookingView(3,balance,softLimit,hardLimit,mContext,alertProgress);

                break;
        }
    }

    private void onIntentCallForThePay()
    {
        Intent intent;
        Context mContext;
        Log.d("TAG", "onIntentCallForThePay: "+paymentTypeSelected);
        switch (paymentTypeSelected)
        {
            case 2:
                paymentType = 1;
                intent = new Intent();
                intent.putExtra("PAYMENTTYPE",paymentType);
                intentSetValue(intent);
                break;
            case 3:
                callExpandMethod();
                break;
            case 1:
                if(cardSelected!=-1)
                {
                    nextIntentForCar(cardSelected);
                }
                break;

        }
    }

    private void callCollapse(int paymentType) {

        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        callIntentForNext(paymentType);
    }

    private void callExpandMethod() {

        String cashText;
        if(Constants.jobType == 3)
            cashText = getString(R.string.cancel);
        else
            cashText = getString(R.string.cash);

        alertProgress.alertPositiveNegativeOnclick(this, getString(R.string.fare_may_vary), getString(R.string.wallet),
                getString(R.string.card), cashText, true, new DialogInterfaceListner() {
                    @Override
                    public void dialogClick(boolean isClicked) {
                        if(isClicked)
                            if(cardItem.size()>0) {
                                paymentType = 2;
                                callIntentForNext(paymentType);

                            }else
                                alertProgress.alertinfo(SelectPayment.this,getString(R.string.pleaseAddCardToPay));
                        else{
                            if(Constants.jobType !=3)
                            {
                                paymentType = 1;
                                callIntentForNext(paymentType);
                            }
                        }
                    }
                });
    }

    private void callIntentForNext(int paymentType) {
        Intent intent = new Intent();
        intent.putExtra("ISWallet",true);
        if(paymentType == 2)
        {
            if(!"".equals(manager.getDefaultCardId()))
                intent.putExtra("CARDID",manager.getDefaultCardId());
            else
                intent.putExtra("CARDID",cardItem.get(0).getId());
        }

        intent.putExtra("PAYMENTTYPE",paymentType);
        intentSetValue(intent);

    }

    private void intentSetValue(Intent intent)
    {
        setResult(RESULT_OK,intent);
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
    }

    private void setPaymentSelectionType(int i)
    {
        tvConfirmPaymentCard.setSelected(false);
        tvConfirmPaymentCash.setSelected(false);
        tvConfirmPaymentWallet.setSelected(false);
        //  tvSelectedWalletPay.setVisibility(View.GONE);
        //   tvSelectedCashPay.setVisibility(View.GONE);
        llConfirmCard.setVisibility(View.GONE);
        tvConfirmAddWalletBalance.setVisibility(View.GONE);
        switch (i)
        {
            case 1:
                tvConfirmPaymentCard.setSelected(true);
                llConfirmCard.setVisibility(View.VISIBLE);
                paymentTypeSelected = 1;
                //   rlConfirmCard.setVisibility(View.VISIBLE);
                break;
            case 2:
                paymentType = 1;
                paymentTypeSelected = 2;
                tvConfirmPaymentCash.setSelected(true);
                //   tvSelectedCashPay.setVisibility(View.GONE);
                break;
            case 3:
                // paymentType = 3;
                paymentTypeSelected = 3;
                tvConfirmPaymentWallet.setSelected(true);
                //  tvSelectedWalletPay.setVisibility(View.VISIBLE);
                tvConfirmAddWalletBalance.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
    }

    @Override
    public void onSessionExpired() {

    }

    @Inject
    SessionManagerImpl manager;
    @Override
    public void onLogout(String message) {

        alertProgress.alertPositiveOnclick(this, message, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(SelectPayment.this,manager);
            }
        });
    }

    @Override
    public void onError(String error) {
        alertProgress.alertinfo(this,error);
    }

    @Override
    public void onShowProgress() {
        progressPayment.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgress() {
        progressPayment.setVisibility(View.GONE);
    }

    @Override
    public void onToCallIntent() {

        Intent intent = new Intent(this, PaymentDetailActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
    }

    @Override
    public void onToBackIntent(int adapterPosition)
    {
        cardSelected = adapterPosition;
    }
    public void nextIntentForCar(int adapterPosition)
    {
        paymentType = 2;
        CardGetData data = cardItem.get(adapterPosition);

        Intent intent = new Intent();
        intent.putExtra("PAYMENTTYPE",paymentType);
        intent.putExtra("CARDID",data.getId());
        intent.putExtra("CARDTYPE",data.getBrand());
        intent.putExtra("LAST4",data.getLast4());
        setResult(RESULT_OK,intent);
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
    }

    @Override
    public void addItems(ArrayList<CardGetData> cardsList)
    {
        cardItem.clear();
        cardItem.addAll(cardsList);
        cardListAdapter.notifyDataSetChanged();
        setPaymentSelectionType(1);
        cardSelected = 0;
    }

    @Override
    public void onVisibilitySet() {

        // tvSelectedWalletPay.setVisibility(View.GONE);
        tvConfirmAddWalletBalance.setVisibility(View.GONE);
        //   tvSelectedCashPay.setVisibility(View.GONE);
        tvConfirmPaymentWallet.setSelected(false);
        tvConfirmPaymentCash.setSelected(false);
    }

    @Override
    public void showWalletAmount(String currencySymbol, double balance, double softLimit, double hardLimit) {

        this.hardLimit = hardLimit;
        this.softLimit = softLimit;
        this.balance = balance;
        Utility.setAmtOnRecept(balance,tvWalletBalanceAmt,currencySymbol,"");
    }

    @Override
    public void paymentSelection(int selectedCell) {
        setPaymentSelectionType(selectedCell);
        cardListAdapter.onAdapterChanged(-1);
    }

    @Override
    public void startActivity() {
        Intent intentq = new Intent(this, WalletActivity.class);
        startActivityForResult(intentq,Constants.WALLETCALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
        {
            if(requestCode==Constants.WALLETCALL)
            {

            }
        }


    }
}
