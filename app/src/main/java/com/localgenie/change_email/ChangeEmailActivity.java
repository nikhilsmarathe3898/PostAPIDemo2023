package com.localgenie.change_email;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.countrypic.Country;
import com.localgenie.countrypic.CountryPicker;
import com.localgenie.countrypic.CountryPickerListener;
import com.localgenie.otp.OtpActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManager;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * @author Pramod
 * @since 01-03-2018.
 *
 *  ------------------------------------
 *
 * <h1>ChangeEmailActivity</h1>
 * This class is used to provide the Change Email/ Mobile screen, where we can change a user's email ID or mobile number
 * which gets updated verification link for email/ post verification by OTP for mobile /
 * this class gives a call to ChangeEmailPresenterImpl class.
 */

public class ChangeEmailActivity extends DaggerAppCompatActivity implements ChangeEmailView {

    @BindView(R.id.etEidtMobileNumber)
    EditText eiEidtMobileNumber;

    @BindView(R.id.etEditEmail)
    EditText etEditEmail;

    @BindView(R.id.llEditPromobile)
    LinearLayout editMobileRow;

    @BindView(R.id.llEditEmail)
    LinearLayout editEmailRow;

    @BindView(R.id.countryCode)
    TextView countryCode;

    @BindView(R.id.countryFlag)
    ImageView countryFlag;

    @BindString(R.string.invalid_empty_email)
    String invalid_empty_email;

    @BindString(R.string.invalid_empty_phone)
    String invalid_empty_phone;

    @BindString(R.string.emailEditInfo)
    String emailSuccess;

    @Inject
    SessionManager sessionManager;

    @Inject
    CountryPicker mCountryPicker;

    @Inject
    ChangeEmailPresenter changeEmailPresenter;
    @Inject
    AppTypeface appTypeface;

    private boolean isEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_email_phone);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            isEmail = getIntent().getBooleanExtra("IS_EMAIL", false);
        }
        initialize();
    }

    private void initialize() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolEditEmailPhone);
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        setListener();

        TextView tvTbTitle=toolbar.findViewById(R.id.tv_center);
        if (isEmail) {
            tvTbTitle.setText(getResources().getString(R.string.changeEmail));

            editEmailRow.setVisibility(View.VISIBLE);
            editMobileRow.setVisibility(View.GONE);

            etEditEmail.requestFocus();
            try {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.toggleSoftInputFromWindow(etEditEmail.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
            } catch (Exception e) {
                System.out.println("e");
            }
        } else {
            tvTbTitle.setText(getResources().getString(R.string.changePhone));
            tvTbTitle.setTypeface(appTypeface.getHind_semiBold());

            editEmailRow.setVisibility(View.GONE);
            editMobileRow.setVisibility(View.VISIBLE);

            eiEidtMobileNumber.requestFocus();
            try {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.toggleSoftInputFromWindow(eiEidtMobileNumber.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
            } catch (Exception e) {
                System.out.println("e");
            }
        }
    }

    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode,
                                        int flagDrawableResID,int max) {
                countryCode.setText(dialCode);
                countryFlag.setImageResource(flagDrawableResID);
                mCountryPicker.dismiss();
                eiEidtMobileNumber.setFilters(new InputFilter[] { new InputFilter.LengthFilter(max)});
            }
        });
        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Country country = mCountryPicker.getUserCountryInfo(this);
        countryFlag.setImageResource(country.getFlag());
        countryCode.setText(country.getDial_code());
        if (country.getMax_digits()!=null) {
            eiEidtMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(country.getMax_digits()))});
        }
    }

    @OnClick({R.id.countryCode,R.id.countryFlag,R.id.tvEditSave})
    void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.countryCode:
            case R.id.countryFlag:
                mCountryPicker.show(getSupportFragmentManager(), getResources().getString(R.string.Countrypicker));
                break;

            case R.id.tvEditSave:
                if (isEmail) {
                    boolean flag = changeEmailPresenter.validateEmail(etEditEmail.getText().toString());
                    if (!flag)
                        changeEmailPresenter.changeEmail(etEditEmail.getText().toString());
                    else
                        Toast.makeText(this, invalid_empty_email, Toast.LENGTH_SHORT).show();
                } else {
                    boolean flag = changeEmailPresenter.validatePhone(eiEidtMobileNumber.getText().toString());
                    if (!flag)
                        changeEmailPresenter.changeMobile(countryCode.getText().toString(),eiEidtMobileNumber.getText().toString());
                    else
                        Toast.makeText(this, invalid_empty_phone, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void setSuccessEmail() {
        Toast.makeText(ChangeEmailActivity.this,emailSuccess,Toast.LENGTH_LONG).show();
    }

    @Override
    public void setError(String message) {
        Toast.makeText(ChangeEmailActivity.this,""+message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void navToOTP() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, OtpActivity.class);
        bundle.putString("forgot_pwd","change_phone");
      //  intent.putExtra("forgot_pwd","change_phone");
        String phone_str = eiEidtMobileNumber.getText().toString();
        if (!TextUtils.isEmpty(phone_str))
        {
           // intent.putExtra("MobileNumber",phone_str);
           // intent.putExtra("CountryCode",countryCode.getText().toString());
            bundle.putString("MobileNumber",phone_str);
            bundle.putString("CountryCode",countryCode.getText().toString());
        }
        intent.putExtras(bundle);

        startActivity(intent);
        finish();
    }

    @Override
    public void navToProfile() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}