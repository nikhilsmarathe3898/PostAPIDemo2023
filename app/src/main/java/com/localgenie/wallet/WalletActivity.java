package com.localgenie.wallet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import com.localgenie.R;
import com.localgenie.payment_method.PaymentMethodActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.walletTransaction.WalletTransActivity;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;


import static com.stripe.android.model.Card.BRAND_RESOURCE_MAP;

public class WalletActivity extends DaggerAppCompatActivity implements WalletActivityContract.WalletView
{
    @BindView(R.id.tv_wallet_balance)  TextView tv_wallet_balance;
    @BindView(R.id.tv_wallet_softLimitValue) TextView tv_wallet_softLimitValue;
    @BindView(R.id.tv_wallet_hardLimitValue) TextView tv_wallet_hardLimitValue;
    @BindView(R.id.tv_wallet_cardNo) TextView tv_wallet_cardNo;
    @BindView(R.id.tv_wallet_currencySymbol) TextView tv_wallet_currencySymbol;
    @BindView(R.id.et_wallet_payAmountValue) EditText et_wallet_payAmountValue;
    @BindView(R.id.tv_center) TextView tv_custom_toolBarTitle;
    @BindView(R.id.tv_wallet_curCredit_label) TextView tv_wallet_curCredit_label;
    @BindView(R.id.tv_wallet_softLimitLabel) TextView tv_wallet_softLimitLabel;
    @BindView(R.id.tv_wallet_hardLimitLabel) TextView tv_wallet_hardLimitLabel;
    @BindView(R.id.btn_wallet_recentTransactions) Button btn_wallet_recentTransactions;
    @BindView(R.id.tv_wallet_payUsing_cardLabel) TextView tv_wallet_payUsing_cardLabel;
    @BindView(R.id.tv_wallet_payAmountLabel) TextView tv_wallet_payAmountLabel;
    @BindView(R.id.tv_wallet_softLimitMsgLabel) TextView tv_wallet_softLimitMsgLabel;
    @BindView(R.id.tv_wallet_softLimitMsg) TextView tv_wallet_softLimitMsg;
    @BindView(R.id.tv_wallet_hardLimitMsgLabel) TextView tv_wallet_hardLimitMsgLabel;
    @BindView(R.id.tv_wallet_hardLimitMsg) TextView tv_wallet_hardLimitMsg;
    @BindView(R.id.tv_wallet_credit_desc) TextView tv_wallet_credit_desc;
    @BindView(R.id.btn_wallet_ConfirmAndPay) Button btn_wallet_ConfirmAndPay;
    @BindString(R.string.cancel) String action_cancel;
    @BindString(R.string.confirm) String confirm;
    @BindString(R.string.paymentMsg1) String paymentMsg1;
    @BindString(R.string.app_name) String app_name;
    @BindString(R.string.paymentMsg2) String paymentMsg2;
    @BindString(R.string.confirmPayment) String confirmPayment;
    @BindString(R.string.stars) String cardNoHidden;
    @BindString(R.string.wait) String wait;
    @BindString(R.string.hardLimitMsg) String hardLimitMsg;
    @BindString(R.string.softLimitMsg) String softLimitMsg;
    @BindString(R.string.wallet) String wallet;
    @BindString(R.string.choose_card) String choose_card;

    @BindColor(R.color.black) int black;
    @BindColor(R.color.saffron) int yellow_light;
    @BindColor(R.color.red_google) int red_light;
    @BindColor(R.color.grey_background_darker) int gray;

    @BindDrawable(R.drawable.ic_visa) Drawable ic_payment_card_icon;
    @BindDrawable(R.drawable.ic_arrow) Drawable home_next_arrow_icon_off;

    @Inject WalletActivityContract.WalletPresenter walletPresenter;
    @Inject
    AppTypeface fontUtils;
    @Inject
    AlertProgress alerts;
    private String cardId;

    private AlertDialog pDialog;
    private boolean isRecharged = false;
    private String message;
    @Inject
    SessionManagerImpl manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wallet);
        ButterKnife.bind(this);
        pDialog = alerts.getProgressDialog(this,wait);
        initToolBar();
        initViews();
        initPayViews();
        initSoftHardLimitDescriptionsView();
        if(!"".equals(manager.getDefaultCardNum()))
        {
            cardId = manager.getDefaultCardId();
            setCardInfo(manager.getDefaultCardName(),manager.getDefaultCardNum());
        }
    }

    private void setCardInfo(String cardType, String last4)
    {
        Bitmap bitmap = Utility.setCreditCardLogo(cardType, this);
      //  ivCardImageInfo.setImageBitmap(bitmap);
        String cardLast4 = getString(R.string.stars)+" "+last4;
   //     tvcardInfo.setText(cardLast4);
        tv_wallet_cardNo.setText(cardLast4);
        tv_wallet_cardNo.setCompoundDrawablesWithIntrinsicBounds(ic_payment_card_icon,null,home_next_arrow_icon_off,null);
    }


    /**<h2>initToolBar</h2>
     * <p> method to initialize customer toolbar </p>
     */
    private void initToolBar()
    {

        Toolbar toolbar = findViewById(R.id.incABar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tv_custom_toolBarTitle.setTypeface(fontUtils.getHind_semiBold());
        tv_custom_toolBarTitle.setText(wallet);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @OnClick({R.id.btn_wallet_recentTransactions,R.id.tv_wallet_cardNo,R.id.btn_wallet_ConfirmAndPay})
    public void clickEvent(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_wallet_recentTransactions:
                Intent intent = new Intent(this, WalletTransActivity.class);
                startActivity(intent);
               // this.overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
                break;

            case R.id.tv_wallet_cardNo:
                Intent cardsIntent = new Intent(this, PaymentMethodActivity.class);
                cardsIntent.putExtra("isNotFormPayment",false);
                startActivityForResult(cardsIntent, 1);
                this.overridePendingTransition(R.anim.slide_in_up, R.anim.stay_still);
                break;

            case R.id.btn_wallet_ConfirmAndPay:
                if(cardId!=null)
                Log.d("TAG", "clickEventAmount: "+et_wallet_payAmountValue.getText().toString());
                showRechargeConfirmationAlert(et_wallet_payAmountValue.getText().toString());
                break;

        }
    }


    /**
     *<h2>initViews</h2>
     * <P> custom method to load tooth_top views of the screen </P>
     */
    private void initViews()
    {
        walletPresenter.getLastCardNo();
        tv_wallet_credit_desc.setTypeface(fontUtils.getHind_regular());
        tv_wallet_curCredit_label.setTypeface(fontUtils.getHind_regular());
        tv_wallet_balance.setTypeface(fontUtils.getHind_regular());
        tv_wallet_softLimitLabel.setTypeface(fontUtils.getHind_regular());
        tv_wallet_softLimitValue.setTypeface(fontUtils.getHind_regular());
        tv_wallet_hardLimitLabel.setTypeface(fontUtils.getHind_regular());
        tv_wallet_hardLimitValue.setTypeface(fontUtils.getHind_regular());
        btn_wallet_recentTransactions.setTypeface(fontUtils.getHind_regular());
        btn_wallet_ConfirmAndPay.setTypeface(fontUtils.getHind_medium());
        et_wallet_payAmountValue.setTypeface(fontUtils.getHind_regular());

        et_wallet_payAmountValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(et_wallet_payAmountValue.getText().toString().isEmpty())
                {
                    btn_wallet_ConfirmAndPay.setSelected(false);
                }else
                    btn_wallet_ConfirmAndPay.setSelected(true);
            }
        });
    }

    @Override
    public void setBalanceValues(String balance,String hardLimit,String softLimit)
    {
        if(isRecharged)
        {
            Utility.hideKeyboard(this);
            et_wallet_payAmountValue.setText("");
            alerts.alertinfo(WalletActivity.this,message);
            isRecharged = false;
        }
        tv_wallet_softLimitValue.setText(softLimit);
        tv_wallet_hardLimitValue.setText(hardLimit);
        tv_wallet_balance.setText(balance);
        tv_wallet_currencySymbol.setText(Constants.walletCurrency);

    }


    /**
     *<h2>initPayViews</h2>
     * <P> custom method to load payment views of the screen </P>
     */
    private void initPayViews()
    {
        tv_wallet_payUsing_cardLabel.setTypeface(fontUtils.getHind_medium());
        tv_wallet_cardNo.setTypeface(fontUtils.getHind_regular());
        tv_wallet_payAmountLabel.setTypeface(fontUtils.getHind_medium());
        tv_wallet_currencySymbol.setTypeface(fontUtils.getHind_regular());
        tv_wallet_hardLimitLabel.setTypeface(fontUtils.getHind_regular());
        tv_wallet_hardLimitValue.setTypeface(fontUtils.getHind_regular());
    }


    /**
     *<h2>initSoftHardLimitDescriptionsView</h2>
     * <P> custom method to init soft and hard limit description views of the screen </P>
     */
    private void initSoftHardLimitDescriptionsView()
    {
        tv_wallet_softLimitMsgLabel.setTypeface(fontUtils.getHind_regular());
        tv_wallet_softLimitMsg.setTypeface(fontUtils.getHind_regular());
        tv_wallet_softLimitMsg.setText(softLimitMsg);
        tv_wallet_hardLimitMsgLabel.setTypeface(fontUtils.getHind_regular());
        tv_wallet_hardLimitMsg.setTypeface(fontUtils.getHind_regular());
        tv_wallet_hardLimitMsg.setText(hardLimitMsg);
        btn_wallet_ConfirmAndPay.setTypeface(fontUtils.getHind_regular());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        walletPresenter.getWalletLimits();
//        Constants.isWalletFragActive = true;
    }


    @Override
    public void hideProgressDialog()
    {
        if(pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }



    @Override
    public void showProgressDialog()
    {

        if(!pDialog.isShowing())
        {
            pDialog.show();
        }
    }


    @Override
    public void showToast(String msg, int duration)
    {
        hideProgressDialog();
        Toast.makeText(this, msg, duration).show();
    }


    @Override
    public void showAlert(String title, String msg)
    {
        hideProgressDialog();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void noInternetAlert()
    {
        alerts.showNetworkAlert(this);
    }

    /**
     *<h2>walletDetailsApiErrorViewNotifier</h2>
     * <p> method to notify api error </p>
     */
    @Override
    public void walletDetailsApiErrorViewNotifier(String error)
    {
        alerts.alertinfo(this,error);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            Bundle bundle;
            bundle=data.getExtras();
            assert bundle != null;
            String cardNum = bundle.getString("LAST4");
            String cardType = bundle.getString("CARDTYPE");
            cardId = bundle.getString("CARDID");
            setCard(cardNum,cardType);
        }
        else
            setNoCard();


    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void setNoCard()
    {
        btn_wallet_ConfirmAndPay.setEnabled(false);
       // btn_wallet_ConfirmAndPay.setBackgroundColor(gray);
        Drawable cardBrand = ic_payment_card_icon;
        tv_wallet_cardNo.setText(choose_card);
        tv_wallet_cardNo.setCompoundDrawablesWithIntrinsicBounds(cardBrand,null,home_next_arrow_icon_off,null);
    }

    @Override
    public void walletRecharged(boolean recharged, String message)
    {
        isRecharged = recharged;
        this.message = message;
        walletPresenter.getWalletLimits();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void setCard(String cardNum,String cardType)
    {
        btn_wallet_ConfirmAndPay.setEnabled(true);
      //  btn_wallet_ConfirmAndPay.setBackground(this.getResources().getDrawable(R.drawable.selector_layout));
        Drawable cardBrand = ic_payment_card_icon;
        if(cardType!=null){
            cardBrand = getResources().getDrawable(BRAND_RESOURCE_MAP.get(cardType));
            tv_wallet_cardNo.setText(cardNoHidden+" "+cardNum);
        }
        tv_wallet_cardNo.setCompoundDrawablesWithIntrinsicBounds(cardBrand,null,home_next_arrow_icon_off,null);
    }


    @Override
    public void onPause()
    {
        super.onPause();
//        Constants.isWalletFragActive = false;
    }


    /**
     * <h2>showRechargeConfirmationAlert</h2>
     * <p> method to show an alert dialog to take user confirmation to proceed to recharge </p>
     */
    @SuppressLint("SetTextI18n")
    public void showRechargeConfirmationAlert(String amount)
    {
        if(cardId!=null)
        {
           /* if(Double.parseDouble(amount)>=200)
            {*/
           if(!amount.isEmpty())
           {
               alerts.alertPositiveNegativeOnclick(this, paymentMsg1
                       + " " + app_name + " " + paymentMsg2 + " " + Constants.walletCurrency + amount, confirmPayment, getString(R.string.confirm), getString(R.string.cancel), true, new DialogInterfaceListner() {
                   @Override
                   public void dialogClick(boolean isClicked) {
                       if(isClicked)
                           walletPresenter.rechargeWallet(amount,cardId);
                   }
               });

            }else
            {
                alerts.alertinfo(this,getString(R.string.amountToRecharge));
            }
        }else
        {
            alerts.alertinfo(this,getString(R.string.pleaseChooseCardToPay));
        }
    }

    @Override
    public void onLogout(String msg)
    {
        alerts.alertPositiveOnclick(this, message, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(WalletActivity.this,manager);
            }
        });
    }

    @Override
    public void onError(String message) {
        alerts.alertinfo(this,message);

    }
}

