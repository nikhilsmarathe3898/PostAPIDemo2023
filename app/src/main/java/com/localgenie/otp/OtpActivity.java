package com.localgenie.otp;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.localgenie.Login.LoginActivity;
import com.localgenie.home.MainActivity;
import com.localgenie.utilities.AppTypeface;
//import com.localgenie.utilities.MySMSBroadcastReceiver;
import com.localgenie.R;
import com.localgenie.changepassword.ChangePwdActivity;
import com.localgenie.profile.ProfileActivity;
import com.localgenie.utilities.MySMSBroadcastReceiver;
import com.localgenie.utilities.SessionManagerImpl;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import dagger.android.support.DaggerAppCompatActivity;

public class OtpActivity extends DaggerAppCompatActivity implements OtpView {


    @BindView(R.id.btn_otp_verify) Button btn_otp_verify;
    @BindView(R.id.tv_heading_2)TextView tv_heading_2;
    @BindView(R.id.tv_heading_1)TextView tv_heading_1;
    @BindView(R.id.tv_timer_text)TextView tv_timer_text;
    @BindView(R.id.tv_otp_resend) TextView tv_otp_resend;

    @BindView(R.id.et_otp1) EditText et_otp1;

    @BindView(R.id.et_otp2) EditText et_otp2;

    @BindView(R.id.et_otp3) EditText et_otp3;

    @BindView(R.id.et_otp4) EditText et_otp4;

    @Inject
    OtpPresenter presenter;

    @Inject
    SessionManagerImpl sessionManager;

    private String flag = "";
    private String sid;

    private String old_phone;

    private AlertDialog alertDialog;
    private AlertDialog.Builder dialogBuilder;

    CountDownTimer mCountDownTimer;
    AccountManager accountManager;
    private IntentFilter intentFilter;
    private MySMSBroadcastReceiver otpReceiver;
    private String countryCode;
    private long expireOtp;

    @Inject
    AppTypeface appTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if("SignUp".equals(bundle.getString("ComingFrom"))) {
            flag= "";
            btn_otp_verify.setText(R.string.verify_phone);
            old_phone =   bundle.getString("MobileNumber");
            countryCode =   bundle.getString("CountryCode");

        } else {
            flag= bundle.getString("forgot_pwd");
            if ("change_phone".equals(flag)) {
                old_phone = bundle.getString("MobileNumber");
                countryCode =   bundle.getString("CountryCode");
                btn_otp_verify.setText(R.string.verify_phone);
            } else {
                expireOtp = bundle.getLong("expireOtp");
                sid = bundle.getString("sid");
                old_phone = bundle.getString("MobileNumber");
                countryCode =   bundle.getString("CountryCode");
                btn_otp_verify.setText(R.string.verify);
            }
        }

        initialize();
        getSmsRetrival();
        sessionManager.setGuestLogin(true);
/*
        intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        otpReceiver = new MySMSBroadcastReceiver() {
            @Override
            protected void onSmsReceived(String s) {
                Log.e("SMS","received sms :: "+s);
                String[] str_arr = s.split(" ");

            }
        };*/





        setTypeFaceValue();

        if (TextUtils.isEmpty(sid)) {
            sid = sessionManager.getSID();
            expireOtp = sessionManager.getExpireOtp();
        }
    }

    private void getSmsRetrival() {
        otpReceiver = new MySMSBroadcastReceiver();
        intentFilter = new IntentFilter("com.google.android.gms.auth.api.phone.SMS_RETRIEVED");

        // Get an instance of SmsRetrieverClient, used to start listening for a matching
// SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

// Starts SmsRetriever, which waits for ONE matching SMS message until timeout
// (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
// action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

// Listen for success/failure of the start Task. If in a background thread, this
// can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                // ...
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
            }
        });

    }

    private void setTypeFaceValue() {
        tv_heading_2.setTypeface(appTypeface.getHind_regular());
        tv_heading_1.setTypeface(appTypeface.getHind_medium());
        tv_timer_text.setTypeface(appTypeface.getHind_semiBold());
        tv_otp_resend.setTypeface(appTypeface.getHind_medium());
        btn_otp_verify.setTypeface(appTypeface.getHind_semiBold());
        String infoString  = getString(R.string.we_sent_4_digit)+"\n "+countryCode +"-"+old_phone;
        tv_heading_2.setText(infoString);
    }

    private void initialize() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbarLayout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textCenter = toolbar.findViewById(R.id.tv_center);
        if (flag == null || "".equals(flag)) {
            textCenter.setText(R.string.verify_phone);
        } else if ("change_phone".equals(flag)) {
            textCenter.setText(R.string.verify_phone);
        } else {
            textCenter.setText(R.string.forgot_password);
        }
        textCenter.setTypeface(appTypeface.getHind_semiBold());
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
               /* if (flag == null || "".equals(flag)) {
                  *//*  Intent signUpIntent = new Intent(OtpActivity.this, SignUpActivity.class);
                    startActivity(signUpIntent);*//*
                    onBackPressed();
                } else if ("change_phone".equals(flag)) {
                   *//* Intent changePhoneIntent = new Intent(OtpActivity.this, ChangeEmailActivity.class);
                    startActivity(changePhoneIntent);*//*
                    finish();
                } else {
                    onBackPressed();
                }*/
            }
        });
        accountManager = AccountManager.get(this);
        mCountDownTimer = new CountDownTimer(1000*sessionManager.getExpireOtp(), 1000) {

            public void onTick(long millisUntilFinished) {
                //%02d
                /*long milliSec = (expireOtp - millisUntilFinished) / 1000;

                long sec = milliSec % 60;
                long min = milliSec / 60;*/

               // Log.d("abc", "onTick: "+milliSec);

                tv_timer_text.setText(""+String.format("%d : %d",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            }

            public void onFinish() {
                tv_timer_text.setText("00:00");
                tv_timer_text.setVisibility(View.GONE);
                tv_otp_resend.setEnabled(true);
                tv_otp_resend.setVisibility(View.VISIBLE);

            }
        }.start();

        et_otp1.requestFocus();

    }

    @OnClick({R.id.tv_otp_resend,R.id.btn_otp_verify})
    public void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.tv_otp_resend:
                if (flag == null || "".equals(flag)) {
                    presenter.resendOtp("",sid,1,true);
                } /*else if ("change_phone".equals(flag)) {
                    Intent changePhoneIntent = new Intent(OtpActivity.this, ProfileActivity.class);
                    startActivity(changePhoneIntent);
                    finish();
                }*/ else {
                    String otp = et_otp1.getText().toString() + et_otp2.getText().toString() + et_otp3.getText().toString() + et_otp4.getText().toString();
                    presenter.resendOtp(otp,sid,2,true);
                }

                mCountDownTimer.start();
                tv_timer_text.setVisibility(View.VISIBLE);
                tv_otp_resend.setVisibility(View.GONE);

                break;

            case R.id.btn_otp_verify:
                String otp = et_otp1.getText().toString() + et_otp2.getText().toString() + et_otp3.getText().toString() + et_otp4.getText().toString();
                if (otp ==null || "".equals(otp.trim())) {
                    System.out.println();
                } else {
                    if (flag == null || "".equals(flag)) {
                        presenter.verifyPhone(otp, sid,flag);
                    } else if ("change_phone".equals(flag)) {
                        presenter.verifyOtp(otp, sid,flag);
                        sessionManager.setMobileNo(old_phone);
                        sessionManager.setCountryCode(countryCode);
                    } else {
                        //presenter.verifyOtp(otp, sid, flag);
                        presenter.resendOtp(otp,sid, 2, false);
                    }
                }
                break;
        }

    }


    @OnTextChanged({R.id.et_otp1,R.id.et_otp2,R.id.et_otp3,R.id.et_otp4})
    public void afterTextChanged(Editable editable){
        if (editable == et_otp1.getEditableText()) {
            int str_len = editable.length();
            if (str_len > 0) {
                et_otp2.requestFocus();
            }
        }
        if (editable == et_otp2.getEditableText()) {
            int str_len = editable.length();
            if (str_len > 0) {
                et_otp3.requestFocus();
            } else {
                et_otp1.requestFocus();
            }
        }
        if (editable == et_otp3.getEditableText()) {
            int str_len = editable.length();
            if (str_len > 0) {
                et_otp4.requestFocus();
            } else {
                et_otp2.requestFocus();
            }
        }
        if (editable == et_otp4.getEditableText()) {
            int str_len = editable.length();
            if (str_len > 0) {
                String otp = et_otp1.getText().toString() + et_otp2.getText().toString() + et_otp3.getText().toString() + et_otp4.getText().toString();
                if (flag == null || "".equals(flag)) {
                    presenter.verifyPhone(otp, sid,flag);
                } else if ("change_phone".equals(flag)) {
                    presenter.verifyOtp(otp, sid,flag);
                    sessionManager.setMobileNo(old_phone);
                    sessionManager.setCountryCode(countryCode);
                } else {
                    presenter.resendOtp(otp, sid, 2, false);
                }
            }
            if (str_len == 0) {
                et_otp3.requestFocus();
            }
        }
    }

    @Override
    public void showProgress(String text) {
        //progressBar.setVisibility(View.VISIBLE);
        if (!isFinishing()) {
            dialogBuilder = new AlertDialog.Builder(OtpActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
            TextView tv_progress = dialogView.findViewById(R.id.tv_progress);
            if ("RESEND".equals(text)) {
                tv_progress.setText(getString(R.string.wait_resend));
            } else if ("OTP_VERIFY".equals(text)) {
                tv_progress.setText(getString(R.string.wait_otp_verify));
            } else if ("PHONE_VERIFY".equals(text)) {
                tv_progress.setText(getString(R.string.wait_verify_signup));
            } else {
                tv_progress.setText(getString(R.string.wait_first_signin));
            }
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            alertDialog = dialogBuilder.create();
            alertDialog.show();
        }

    }

    @Override
    public void hideProgress() {
        //progressBar.setVisibility(View.GONE);
        alertDialog.dismiss();
    }

    @Override
    public void navigateToLogin() {
        Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void navToProfile() {
        Intent intent = new Intent(OtpActivity.this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void setError() {
        Toast.makeText(this,getString(R.string.verification_success),Toast.LENGTH_LONG).show();

    }

    @Override
    public void setErrorMsg(String errorMsg) {
        Toast.makeText(this,""+errorMsg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void navigateToChangePwd(String sid) {
        sessionManager.setSID(sid);

        Log.e("OTP_CHG_PWD","SID :: "+sid);

        Intent intent = new Intent(OtpActivity.this, ChangePwdActivity.class);
        intent.putExtra("coming_from","forgot_pwd");
        startActivity(intent);

    }

    @Override
    public void navToHome(String auth) {
        sessionManager.setSID(sid);
      //  setAuthToken(sessionManager.getUSER_NAME(),"",auth);

        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
      //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * <h2>setAuthToken</h2>
     * This method is used to set the auth token by creating the account manager with the account
     */
   /* public void setAuthToken(String emailID, String password, String authToken) {
        Account account = new Account(emailID, AccountGeneral.ACCOUNT_TYPE);
        try {
            accountManager.addAccountExplicitly(account, password, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        accountManager.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, authToken);
    }*/

    @Override
    public void onResume() {
        this.registerReceiver(otpReceiver, intentFilter);
        super.onResume();
    }

    @Override
    public void onPause() {
        this.unregisterReceiver(otpReceiver);
        super.onPause();

    }
}
