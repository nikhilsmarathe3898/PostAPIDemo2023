package com.localgenie.forgotpassword;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.countrypic.Country;
import com.localgenie.countrypic.CountryPicker;
import com.localgenie.countrypic.CountryPickerListener;
import com.localgenie.otp.OtpActivity;
import com.localgenie.utilities.AppTypeface;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * <h1>ForgotPwdActivity</h1>
 * <p>
 *     This class is used to provide the Forgot Password screen, when user forgets the password,
 *     where user can input mobile number/email to reset his password
 *     our password then here we also can make a request to forgot password
 *     and if verification of OTP/reset link is successful, redirects to Change Password screen.
 * </p>
 * @author Pramod
 * @since 21-12-2017.
 */

public class ForgotPwdActivity extends DaggerAppCompatActivity implements ForgotPwdView {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    public final String TAG = "ForgotPwdActivity";

    boolean api_flag =false;

    String[] perms = {Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS};

    @BindView(R.id.tv_email_select)
    TextView tv_email_select;

    @BindView(R.id.tv_phone_select)
    TextView tv_phone_select;

    @BindView(R.id.et_reg_mobno)
    EditText et_reg_mobno;

    @BindView(R.id.et_reg_email)
    EditText et_reg_email;

    @BindView(R.id.btn_next_confirm)
    Button btn_next_confirm;

    @BindView(R.id.til_reg_email)
    TextInputLayout til_reg_email;

    @BindView(R.id.rl_reg_Mob)
    RelativeLayout rl_reg_Mob;

    @BindView(R.id.countryCode)
    TextView countryCode;
    @BindView(R.id.tv_center)
    TextView tv_center;

    @BindView(R.id.countryFlag)
    ImageView countryFlag;



    @BindString(R.string.invalid_empty_email)
    String invalid_empty_email;

    @BindString(R.string.invalid_empty_phone)
    String invalid_empty_phone;


    @Inject
    ForgotPwdPresenter presenter;

    @Inject AlertProgress alertProgress;

    @Inject
    AppTypeface appTypeface;

    @Inject
    CountryPicker mCountryPicker;


    /**
     * This is the onCreate ForgotPwdActivity method that is called firstly, when user came to forgot password screen.
     * when the user forgets the password and wants to reset his password via an OTP via mobile number/email.
     * @param savedInstanceState contains an instance of Bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpwd);
        ButterKnife.bind(this);
        initialize();
    }

    /**
     * <h2>initialize</h2>
     * <p> method to initialize the views</p>
     */
    private void initialize() {
        Toolbar toolbar = findViewById(R.id.signup_tool);
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tv_center.setText(R.string.forgot_password);
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        setListener();

        et_reg_mobno.requestFocus();

        InputMethodManager inputMethodManager =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager!=null) {
            inputMethodManager.toggleSoftInputFromWindow(et_reg_mobno.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        }


    }

    @OnClick({R.id.tv_email_select, R.id.tv_phone_select, R.id.btn_next_confirm, R.id.countryCode, R.id.countryFlag})
    void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_email_select:
                tv_email_select.setBackgroundResource(R.drawable.parrot_green_stroke_bottom_bg);
                tv_phone_select.setBackgroundResource(R.drawable.white_background);

                et_reg_email.requestFocus();

                tv_email_select.setTextColor(ResourcesCompat.getColor(getResources(), R.color.parrotGreen, null));
                tv_phone_select.setTextColor(ResourcesCompat.getColor(getResources(), R.color.grey_background_vdarker, null));

                rl_reg_Mob.setVisibility(View.GONE);
                til_reg_email.setVisibility(View.VISIBLE);

                btn_next_confirm.setText(getString(R.string.confirm));
                break;
            case R.id.tv_phone_select:
                tv_phone_select.setBackgroundResource(R.drawable.parrot_green_stroke_bottom_bg);
                tv_email_select.setBackgroundResource(R.drawable.white_background);

                tv_phone_select.setTextColor(ResourcesCompat.getColor(getResources(), R.color.parrotGreen, null));
                tv_email_select.setTextColor(ResourcesCompat.getColor(getResources(), R.color.grey_background_vdarker, null));

                rl_reg_Mob.setVisibility(View.VISIBLE);
                til_reg_email.setVisibility(View.GONE);

                btn_next_confirm.setText(getString(R.string.next));

                break;
            case R.id.btn_next_confirm:
                if (rl_reg_Mob.getVisibility() == View.VISIBLE) {
                    boolean flag = presenter.validatePhone(et_reg_mobno.getText().toString());
                    if (!flag) {

                            if (btn_next_confirm.isEnabled()) {
                                btn_next_confirm.setEnabled(false);
                                presenter.forgotPassword(et_reg_mobno.getText().toString(),countryCode.getText().toString(), 1);
                            }
                    } else {
                        alertProgress.alertinfo(ForgotPwdActivity.this,invalid_empty_phone);
                    }
                } else {
                    boolean flag = presenter.validateEmail(et_reg_email.getText().toString());
                    if (!flag) {
                        presenter.forgotPassword(et_reg_email.getText().toString(),countryCode.getText().toString(),2);
                    } else {
                        alertProgress.alertinfo(ForgotPwdActivity.this,invalid_empty_email);
                    }
                }
                break;
            case R.id.countryCode:
            case R.id.countryFlag:
                mCountryPicker.show(getSupportFragmentManager(), getResources().getString(R.string.Countrypicker));
                break;
        }
    }

    /**
     * <h2>setListener</h2>
     *    <p>This is to set listener for CountryPicker to get all the countryCodes,Flags and map them accordingly</p>
     */
    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID, int max) {
                countryCode.setText(dialCode);
                countryFlag.setImageResource(flagDrawableResID);
                mCountryPicker.dismiss();
                et_reg_mobno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
            }
        });
        //By Default, Current country
        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Country country = mCountryPicker.getUserCountryInfo(this);
        countryFlag.setImageResource(country.getFlag());
        countryCode.setText(country.getDial_code());
        if (country.getMax_digits()!=null) {
            et_reg_mobno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(country.getMax_digits()))});
        }

    }

    @Override
    public void setError(String message) {
        alertProgress.alertinfo(ForgotPwdActivity.this,message);
        btn_next_confirm.setEnabled(true);
    }

    @Override
    public void navtoOTP(String sid,long expireOtp) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(ForgotPwdActivity.this, OtpActivity.class);
     //   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        bundle.putString("sid",sid);
        bundle.putLong("expireOtp",expireOtp);
        bundle.putString("forgot_pwd","y");
        bundle.putString("MobileNumber",et_reg_mobno.getText().toString());
        bundle.putString("CountryCode",countryCode.getText().toString());
       intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void navToLogin(String message) {
      /*  Intent intent = new Intent(ForgotPwdActivity.this, LoginActivity.class);
        startActivity(intent);*/
      alertProgress.alertPositiveOnclick(this, message, getString(R.string.helpwithpassword),getString(R.string.ok), new DialogInterfaceListner() {
          @Override
          public void dialogClick(boolean isClicked) {
              finish();
          }
      });


    }

    /**
     * <h2>checkAndReqSmsPerms</h2>
     * <p> method to Check and Request SMS permissions for sending the SMS and read the OTP SMS
     *     sent by the system and validate the OTP</p>
     */
    private void checkAndReqSmsPerms() {

        int readSmsPerm = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);

        int sendSmsPerm = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (readSmsPerm != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }

        if (sendSmsPerm != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS : {
                boolean permissionAllowed=false;
                String deniedPermission="";
                int i = 0;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        permissionAllowed = true;
                        i++;
                    } else {
                        deniedPermission = permissions[i];
                        permissionAllowed = false;
                        break;
                    }
                }
                if(permissionAllowed){
                    presenter.forgotPassword(et_reg_mobno.getText().toString(),countryCode.getText().toString(), 1);
                } else {
                    Log.e(TAG, deniedPermission + " Permission is denied");

                    boolean somePermissionsForeverDenied = false;
                    for(String permission: permissions) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            //denied
                            Log.e("denied", permission);
                            somePermissionsForeverDenied = true;
                        } else {
                            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                                //allowed
                                Log.e("allowed", permission);
                            } else {
                                //set to never ask again
                                Log.e("set to never ask again", permission);
                                somePermissionsForeverDenied = true;
                            }
                        }
                    }
                    if (somePermissionsForeverDenied)
                        alertProgress.alertinfo(ForgotPwdActivity.this,"Permission is denied, Please grant SMS permission to proceed further!");
                }
                break;
            }
        }
    }
}

