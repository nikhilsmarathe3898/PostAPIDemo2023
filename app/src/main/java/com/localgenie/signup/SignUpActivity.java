package com.localgenie.signup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.localgenie.R;
import com.localgenie.countrypic.Country;
import com.localgenie.countrypic.CountryPicker;
import com.localgenie.countrypic.CountryPickerListener;
import com.localgenie.faq_detail.WebViewActivity;
import com.localgenie.home.MainActivity;
import com.localgenie.model.UserDetailsDataModel;
import com.localgenie.otp.OtpActivity;
import com.localgenie.utilities.AppPermissionsRunTime;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.HandlePictureEvents;
import com.localgenie.utilities.ImageUploadedAmazon;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.utility.AlertProgress;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import dagger.android.support.DaggerAppCompatActivity;
import eu.janmuller.android.simplecropimage.CropImage;

/**
 * @author Pramod
 * @since 21-12-2017.
 *
 *  ------------------------------------
 *
 * <h1>SignUp Activity</h1>
 * This class is used to provide the SignUp screen, where we can register a user and after that we get an OTP on our mobile and
 * this class gives a call to SignUpPresenterImpl class.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class SignUpActivity extends DaggerAppCompatActivity implements SignUpView {

        private static final int REQUEST_CODE_PERMISSION_MULTIPLE = 143;

    private static final int REQUEST_ID_SMS_PERMISSION = 12;
    public final String TAG = "SignUpActivity";
    boolean api_flag =false;

    String[] perms = {Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS};

    View focusView;

    String reg_fname,reg_lname,reg_email,reg_password,reg_countryCode,reg_mobile_no;

    @BindView(R.id.iv_reg_profile)
    ImageView iv_reg_profile;

    @BindView(R.id.tv_clicktermscondition)
    TextView tv_clicktermscondition;

    @BindView(R.id.countryCode)
    TextView countryCode;

    @BindView(R.id.countryFlag)
    ImageView countryFlag;

    @BindView(R.id.et_reg_email)
    EditText et_reg_email;

    @BindView(R.id.til_reg_email)
    TextInputLayout til_reg_email;

    @BindView(R.id.et_reg_fname)
    EditText et_reg_fname;

    @BindView(R.id.til_reg_fnm)
    TextInputLayout til_reg_fname;

    @BindView(R.id.til_reg_lnm)
    TextInputLayout til_reg_lname;

    @BindView(R.id.et_reg_lname)
    EditText et_reg_lname;

    @BindView(R.id.et_reg_passwd)
    EditText et_reg_passwd;

    @BindView(R.id.til_reg_paswrd)
    TextInputLayout til_reg_password;

    @BindView(R.id.et_reg_mobno)
    EditText et_reg_mobno;

    @BindView(R.id.til_reg_MobNo)
    TextInputLayout til_reg_mobile;

    @BindView(R.id.et_reg_referral)
    EditText et_reg_referral;

    @BindView(R.id.rlFbLogin)
    RelativeLayout rlFbLogin;

    @BindView(R.id.rlGoogleLogin)
    RelativeLayout rlGoogleLogin;

    @BindView(R.id.btn_signup)
    Button btn_signup;
    //PercentageButton btn_signup;

    @BindView(R.id.fb_signup_or)
    View fb_signup_or;

    @BindString(R.string.signup_with_google)
    String signup_with_google;

    @BindString(R.string.signup_with_facebook)
    String signup_with_facebook;

    @Inject
    SignUpPresenter presenter;

    @Inject
    CountryPicker mCountryPicker;

    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;

    @Inject
    AlertProgress alertProgress;

    private CallbackManager callbackManager;

    String[] perms_fb = {Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int REQUEST_ID_READ_STORAGE_PERMISSION = 116;

    private static final int REQUEST_ID_GOOGLE_SIGNIN = 66;
    private String takenNewImageFileName;
    private boolean isProfilePicSelected;
    private boolean isFbClicked;
    private String takenNewImage;
    private boolean isPPSelected;

    HandlePictureEvents handlePicEvent;
    private boolean imageFlag = true;
    String profilePicUrl = "";
    @Inject
    AppTypeface appTypeface;
    @BindView(R.id.tv_termsclick)TextView tv_termsclick;
    @BindString(R.string.privacyPolicy)String privacyPolicy;
    @BindString(R.string.termsAndCondition)String termsCondition;
    private String referralCode = "";
    @Inject
    SessionManagerImpl manager;

    boolean isNameValid, isEmailValid, isPasswordValid, isPhoneValid = false;

    /**
     * This is the onCreateSignUpActivity method that is called firstly, when user came to signup screen.
     * @param savedInstanceState contains an instance of Bundle.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        handlePicEvent = new HandlePictureEvents(SignUpActivity.this);
        initialize();
        getIntentValue();


        termsConditionReferals();
    }

    private void termsConditionReferals()
    {
        presenter.setTermsCondition(tv_termsclick,privacyPolicy);
        presenter.setTermsCondition(tv_termsclick,termsCondition);
        presenter.setOnclickHighlighted(tv_termsclick, privacyPolicy, view -> callTermsAndCondition(Constants.PRIVECY_LINK,privacyPolicy));
        presenter.setOnclickHighlighted(tv_termsclick, termsCondition, view -> callTermsAndCondition(Constants.TERMS_LINK,termsCondition));
        et_reg_referral.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String code = et_reg_referral.getText().toString().trim();
                if("".equalsIgnoreCase(referralCode))
                {
                    if(code.length()==4 ||code.length()==5 ||code.length()==6)
                    {
                        presenter.checkReferralCode(et_reg_referral.getText().toString().trim());
                    }
                }

            }
        });
    }

    private void callTermsAndCondition(String link, String title)
    {
        Intent intent = new Intent(SignUpActivity.this, WebViewActivity.class);
        intent.putExtra("Link", link);
        intent.putExtra("Title", title);
        intent.putExtra("COMINFROM", termsCondition);
        startActivity(intent);
        overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (handlePicEvent.newFile != null)
            outState.putString("cameraImage", handlePicEvent.newFile.getPath());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("cameraImage")) {
                Uri uri = Uri.parse(savedInstanceState.getString("cameraImage"));
                handlePicEvent.newFile = new File(uri.getPath());
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void getIntentValue() {

        Bundle bundle = this.getIntent().getExtras();
        if (bundle!=null) {
            UserDetailsDataModel userDetailsDataModel = (UserDetailsDataModel) bundle.getSerializable("user_details");
            if (userDetailsDataModel!=null) {
                if (userDetailsDataModel.getLoginType() == 2) {
                    presenter.storeLoginType(2);
                    presenter.storeFbId(userDetailsDataModel.getFacebookId());
                    setValuesFromFbGoogle(userDetailsDataModel);

                } else if (userDetailsDataModel.getLoginType() == 3)
                {
                    presenter.storeGoogleId(userDetailsDataModel.getGoogleId());
                    presenter.storeLoginType(3);
                    setValuesFromFbGoogle(userDetailsDataModel);

                }
            }
        }
    }


    private void setValuesFromFbGoogle(UserDetailsDataModel userDetailsDataModel) {
        if (!TextUtils.isEmpty(userDetailsDataModel.getFirstName()))
            et_reg_fname.setText(userDetailsDataModel.getFirstName());

        if (!TextUtils.isEmpty(userDetailsDataModel.getLastName()))
            et_reg_lname.setText(userDetailsDataModel.getLastName());

        if (!TextUtils.isEmpty(userDetailsDataModel.getEmailOrPhone()))
            et_reg_email.setText(userDetailsDataModel.getEmailOrPhone());

        if (!TextUtils.isEmpty(userDetailsDataModel.getProfilePicUrl())) {

            profilePicUrl = userDetailsDataModel.getProfilePicUrl();

            Glide.with(this)
                    .load(userDetailsDataModel.getProfilePicUrl())
                    .apply(Utility.createGlideOptionCall(this))
                    .into(iv_reg_profile);

        }
    }

    /**
     * <h2>initialize</h2>
     * <p> method to initialize the views and other resources required for the class</p>
     */
    private void initialize() {
        Toolbar toolbar = findViewById(R.id.signup_tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textView = toolbar.findViewById(R.id.tv_center);
        textView.setText(R.string.signup);
        textView.setTypeface(appTypeface.getHind_semiBold());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        takenNewImageFileName ="LSPProfilepic" + ".png";

        callbackManager = CallbackManager.Factory.create();

        TextView tv_fb_signup =  fb_signup_or.findViewById(R.id.tv_login_withfb);
        TextView tv_google_signup =  fb_signup_or.findViewById(R.id.tv_login_Google);

        tv_fb_signup.setText(signup_with_facebook);
        tv_google_signup.setText(signup_with_google);

        Animation lAnimation= AnimationUtils.loadAnimation(this,R.anim.lfade);

        rlFbLogin.startAnimation(lAnimation);
        btn_signup.startAnimation(lAnimation);

        Animation rAnimation = AnimationUtils.loadAnimation(this,R.anim.rfade);

        rlGoogleLogin.startAnimation(rAnimation);

        setListener();

        onEditChangeListner();
    }

    private void onEditChangeListner() {

        et_reg_mobno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                focusViewNull();
            }
        });
    }


    @OnClick({R.id.rlFbLogin,R.id.rlGoogleLogin,R.id.btn_signup,R.id.iv_reg_profile,R.id.countryCode,R.id.countryFlag,R.id.tv_clicktermscondition})
    void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.rlFbLogin:
                //Toast.makeText(this, "Signup with Facebook", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(SignUpActivity.this, perms_fb[0]) == PackageManager.PERMISSION_GRANTED) {
                        presenter.storeLoginType(2);
                        presenter.handleResultFromFB(callbackManager);
                    } else {
                        isFbClicked = true;
                        checkAndReqReadPerms();
                    }
                } else {
                    presenter.storeLoginType(2);
                    presenter.handleResultFromFB(callbackManager);
                }
                break;
            case R.id.rlGoogleLogin:
                Log.e("TAG", "onClickEvent: Login with Google");
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(SignUpActivity.this, perms_fb[0]) == PackageManager.PERMISSION_GRANTED) {
                        presenter.storeLoginType(3);
                        presenter.googleLogin();
                    } else {
                        checkAndReqReadPerms();
                    }
                } else {
                    presenter.storeLoginType(3);
                    presenter.googleLogin();
                }
                break;
            case R.id.btn_signup:
              /*  try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm!=null) {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                reg_fname = et_reg_fname.getText().toString();
                reg_lname = et_reg_lname.getText().toString();
                reg_email = et_reg_email.getText().toString();
                reg_password = et_reg_passwd.getText().toString();
                reg_countryCode = countryCode.getText().toString();
                reg_mobile_no = et_reg_mobno.getText().toString();

                Utility.hideKeyboard(this);

                /*if (!(presenter.validateCreds(reg_fname, reg_lname,reg_email, reg_password, reg_countryCode, reg_mobile_no,maxx)))
                {*/
                    /*for (String permission : perms) {
                        if (ContextCompat.checkSelfPermission(SignUpActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
                            api_flag = true;
                        } else {
                            checkAndReqSmsPerms();
                        }
                    }*/
                   // if (api_flag) {
                        if(focusView!=null)
                            focusView.requestFocus();
                        else
                            presenter.doRegister(profilePicUrl,reg_fname, reg_lname, reg_email, reg_password, reg_countryCode, reg_mobile_no, referralCode);
                   // }
               // }
                break;
            case R.id.iv_reg_profile:
                checkForPermission();
                break;
            case R.id.countryCode:
            case R.id.countryFlag:
                mCountryPicker.show(getSupportFragmentManager(), getResources().getString(R.string.Countrypicker));
                break;

            case R.id.tv_clicktermscondition:
                Log.e("SIGNUP","Terms n Condition");
                break;
        }
    }

    @OnFocusChange({R.id.et_reg_fname,R.id.et_reg_lname,R.id.et_reg_email,R.id.et_reg_passwd,R.id.et_reg_mobno})
    void onFocusChangeEvent(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.et_reg_fname:
                if(!hasFocus) {
                    focusViewNull();
                    //Check First name
                    presenter.validateFname(et_reg_fname.getText().toString());
                }
                break;
            case R.id.et_reg_email:
                if(!hasFocus) {
                    focusViewNull();
                    //Checks email
                    presenter.validateEmail("y",et_reg_email.getText().toString().toLowerCase(Locale.getDefault()));
                }
                break;

            case R.id.et_reg_passwd:
                if(!hasFocus) {
                    focusViewNull();
                    //Validation for password
                    presenter.validatePassword(et_reg_passwd.getText().toString());
                }

                break;
            case R.id.et_reg_mobno:
                if(!hasFocus) {
                    presenter.validatePhone("y", countryCode.getText().toString().trim(),et_reg_mobno.getText().toString().trim());
                }
                break;
        }
    }



    @OnTextChanged(value = R.id.et_reg_fname,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onFnameTextChange(Editable editable) {
        Log.e("TAG", "onTextChanged last name");
        int fname_str_len = editable.toString().length();
        if (fname_str_len <= 0) {
            til_reg_fname.setError(null);
            setFirstNameError();
        }
        if (fname_str_len > 0) {
            til_reg_fname.setError(null);
        }
    }


    @Override
    public void showProgress(String text) {
        if (!isFinishing()) {

            switch (text) {
                case "EMAIL":
                    alertDialog =  alertProgress.getProgressDialog(this,getString(R.string.wait_email));
                    break;
                case "PHONE":
                    alertDialog =  alertProgress.getProgressDialog(this,getString(R.string.wait_phone));
                    break;
                default:
                    alertDialog =  alertProgress.getProgressDialog(this,getString(R.string.wait_register));
                    }
            alertDialog.show();
        }

    }

    @Override
    public void hideProgress() {
        if (alertDialog!=null && !isFinishing())
            alertDialog.dismiss();
    }


    @Override
    public void onLoginSuccess(String auth, String email, String password) {
        if(manager.getGuestLogin())
            manager.setGuestLogin(false);
        else
        {
            manager.setGuestLogin(false);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
        Log.d("TAG", "onLoginSuccess: "+manager.getGuestLogin());
        finish();
    }

    @Override
    public void setFieldsFromFB(String first_name,String last_name,String email,String phone_no,String profilePic) {
        et_reg_fname.setText(first_name);
        et_reg_lname.setText(last_name);
        if (!TextUtils.isEmpty(email))
            et_reg_email.setText(email);
        if (!TextUtils.isEmpty(phone_no))
            et_reg_mobno.setText(phone_no);
        if (!TextUtils.isEmpty(profilePic))
            profilePicUrl = profilePic;
        et_reg_email.requestFocus();
        Glide.with(this)
                .load(profilePic)
                .apply(Utility.createGlideOptionCall(this))
                .into(iv_reg_profile);

    }


    @Override
    public void navigateToLogin() {

        Bundle bundle =new Bundle();
        Intent intent = new Intent(this,OtpActivity.class);

        bundle.putString("ComingFrom","SignUp");
        bundle.putString("MobileNumber",reg_mobile_no);
        bundle.putString("CountryCode",reg_countryCode);

        intent.putExtras(bundle);
        startActivity(intent);

        // finish();

    }

    // Check and Request for SMS Permissions for sending OTP while register
    private void checkAndReqSmsPerms() {
        /*if (Build.VERSION.SDK_INT >= 23) {

            int readSmsPerm = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_SMS);

            int senddSmsPerm = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS);

            List<String> listPermissionsNeeded = new ArrayList<>();

            if (readSmsPerm != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_SMS);
            }

            if (senddSmsPerm!= PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_ID_SMS_PERMISSION);
            }
        }else
        {*/
            if(focusView!=null)
                focusView.requestFocus();
            else
                presenter.doRegister(profilePicUrl,reg_fname, reg_lname, reg_email, reg_password, reg_countryCode, reg_mobile_no, referralCode);
       // }
    }

    private void checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            ArrayList<AppPermissionsRunTime.MyPermissionConstants> myPermissionConstantsArrayList = new ArrayList<>();
            myPermissionConstantsArrayList.clear();
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_CAMERA);
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_WRITE_EXTERNAL_STORAGE);
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_READ_EXTERNAL_STORAGE);
            if (AppPermissionsRunTime.checkPermission(this, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE)) {

                if (!isFbClicked)
                    selectImage();
                else {
                    //   new DownloadImage().execute(fb_pic);
                }
                //}
            }
        } else {
            selectImage();
        }
    }


    /**
     * <h2>setListener</h2>
     *    <p>This is to set listener for CountryPicker to get all the countryCodes,Flags and map them accordingly</p>
     */
    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode,
                                        int flagDrawableResID,int max) {
                countryCode.setText(dialCode);
                countryFlag.setImageResource(flagDrawableResID);
                mCountryPicker.dismiss();
              //  et_reg_mobno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});

                // et_reg_mobno.setFilters(new InputFilter[] { new InputFilter.LengthFilter(max)});
            }
        });
        countryCode.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(), getResources().getString(R.string.Countrypicker)));
        //By Default, Current country
        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Country country = mCountryPicker.getUserCountryInfo(this);
        countryFlag.setImageResource(country.getFlag());
        countryCode.setText(country.getDial_code());
       /* if (country.getMax_digits()!=null) {
            et_reg_mobno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(country.getMax_digits()))});
        }*/

    }

    private void selectImage() {
        handlePicEvent.openDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.CAMERA_PIC:
                handlePicEvent.startCropImage(handlePicEvent.newFile);
                break;
            case Constants.GALLERY_PIC:
                if(data!=null)
                    handlePicEvent.gallery(data.getData());
                break;

            case Constants.CROP_IMAGE:
                if (data!=null) {
                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    if (path != null) {
                        try {
                            File fileExist = new File(path);

                            imageFlag = true;
                            handlePicEvent.uploadToAmazon(Constants.Amazonbucket + "/" + Constants.AmazonProfileFolderName, fileExist, new ImageUploadedAmazon() {
                                @Override
                                public void onSuccessAdded(String image) {
                                    Log.d("TAG", "onSuccessAdded: " + image);
                                    profilePicUrl = image;

                                }

                                @Override
                                public void onerror(String errormsg) {
                                    Log.d("TAG", "onerror: " + errormsg);
                                }

                            });
                            Glide.with(SignUpActivity.this)
                                    .load(fileExist)
                                    .apply(Utility.createGlideOptionCall(SignUpActivity.this))
                                    .into(iv_reg_profile);

                       /* iv_reg_profile.setImageURI(Uri.parse(path));
                        iv_reg_profile.setImageURI(Uri.parse(fileExist.toString()));*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            case REQUEST_ID_GOOGLE_SIGNIN:
                Log.e(TAG, "onActivityResult: "+" entered google" );
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.e(TAG, "onActivityResult: "+" entered googleRes "+result.isSuccess());
                presenter.handleResultFromGoogle(result);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * predefined method to check run time permissions list call back
     *
     * @param requestCode   request code
     * @param permissions:  contains the list of requested permissions
     * @param grantResults: contains granted and un granted permissions result list
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isDenine = false;
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_MULTIPLE:
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isDenine = true;
                    }
                }
                if (isDenine) {
                    Toast.makeText(this, "Permission denied by the user", Toast.LENGTH_SHORT).show();
                } else {
                    if (isProfilePicSelected) {
                        Drawable myDrawable = getResources().getDrawable(R.drawable.register_profile_default_image);
                        Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();
                    } else {
                        if (!isFbClicked)
                            selectImage();
                        else {
                            //new DownloadImage().execute(fb_pic);
                        }
                    }
                }
                break;

            case REQUEST_ID_SMS_PERMISSION:

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
                    if(focusView!=null)
                        focusView.requestFocus();
                    else
                        presenter.doRegister(profilePicUrl,reg_fname, reg_lname, reg_email, reg_password, reg_countryCode, reg_mobile_no, referralCode);
                 //   presenter.doRegister(profilePicUrl,reg_fname,reg_lname, reg_email, reg_password, reg_countryCode, reg_mobile_no, referralCode);

                } else{
                    //Toast.makeText(this,deniedPermission+" Permission is denied",Toast.LENGTH_SHORT).show();
                    Log.e(TAG,deniedPermission+" Permission is denied");
                    boolean somePermissionsForeverDenied = false;
                    for(String permission: permissions) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            //denied
                            Log.e("denied", permission);
                            somePermissionsForeverDenied = true;
                            //Toast.makeText(this,"Permission Denied! Please grant SMS permissions to proceed further!",Toast.LENGTH_LONG).show();
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
                        Toast.makeText(this,"Permission is denied, Please grant SMS permission to proceed further!",Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_ID_READ_STORAGE_PERMISSION:

                boolean permissionAllowFlag=false;
                String deniedPermissions="";
                int k = 0;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        permissionAllowFlag = true;
                        k++;
                    } else {

                        deniedPermissions = permissions[k];
                        permissionAllowFlag = false;
                        break;
                    }
                }
                if(permissionAllowFlag){
                    if (isFbClicked) {
                        presenter.storeLoginType(2);
                        presenter.handleLoginType(callbackManager);
                    } else {
                        presenter.storeLoginType(3);
                        presenter.googleLogin();
                    }
                } else {
                    //Toast.makeText(this,deniedPermission+" Permission is denied",Toast.LENGTH_SHORT).show();
                    Log.e("SIGNUP",deniedPermissions+" Permission is denied");
                    boolean somePermissionsForeverDenied = false;
                    for(String permission: permissions) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            //denied
                            Log.e("denied", permission);
                            somePermissionsForeverDenied = true;
                            //Toast.makeText(this,"Permission Denied! Please grant SMS permissions to proceed further!",Toast.LENGTH_LONG).show();
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
                        Toast.makeText(this,"Permission is denied, Please grant READ permission to proceed further!",Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }



    /*<h2>checkAndReqReadPerms</h2>
     * <p> Check and Request for READ EXTERNAL STORAGE Permissions for facebook login. </p>
     */
    private void checkAndReqReadPerms() {
        if (Build.VERSION.SDK_INT >= 23) {

            int readPerm = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            List<String> listPermissionsNeeded = new ArrayList<>();

            if (readPerm != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_ID_READ_STORAGE_PERMISSION);
            }
        }
    }

    @Override
    public void openGoogleActivity(Intent intent)
    {
        startActivityForResult(intent,REQUEST_ID_GOOGLE_SIGNIN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.initializeFacebook();
    }

    @Override
    public void showPro() {
        showProgress(getString(R.string.wait_register));
    }


    @Override
    public void onReferral(String trim) {
        et_reg_referral.setText(trim);
        referralCode = trim;
    }

    /**
     *<h1>focusVieW</h1>
     * this set the focus to the number
     * @param til is a TextInputLayout
     */
    private void focusVieW(TextInputLayout til) {
        focusView = til;
    }

    /**
     * <h1>focusViewNull</h1>
     * set the focus view to null
     */
    private void focusViewNull() {
        focusView = null;
    }

    @Override
    public void setFirstNameError() {
        focusVieWRequestFocus(til_reg_fname,getString(R.string.err_fname));
    }

    @Override
    public void setLastNameError() {
        //  til_reg_lname.setError(getString(R.string.err_lname));
    }





    @Override
    public void setMobileErrorMsg(String message) {
        // If it returns success from the Phone Validation API- message would be empty string, Setting error to null
        if ("".equals(message))
        {
            et_reg_mobno.setError(null);
            focusView = null;
        }

        else
        {
            focusView = et_reg_mobno;
            et_reg_mobno.setError(message);
            et_reg_mobno.setText("");
            //   et_reg_mobno.requestFocus();
        }
    }
    @Override
    public void setMobileInvalid() {
        et_reg_mobno.setError(getString(R.string.err_mobile_invalid));
        focusView = et_reg_mobno;

    }

    @Override
    public void setEmailInvalid(String error) {
        if("".equals(error))
            focusVieWRequestFocus(til_reg_email,getString(R.string.err_invalid_email));
        else
            focusVieWRequestFocus(til_reg_email,error);
    }
    @Override
    public void setEmailError() {
        focusVieWRequestFocus(til_reg_email,getString(R.string.err_email));
    }



    @Override
    public void setPasswordInvalid() {
        focusVieWRequestFocus(til_reg_password,getString(R.string.invalid_password));
    }

    @Override
    public void setPasswordError() {
        focusVieWRequestFocus(til_reg_password,getString(R.string.err_password));
    }


    @Override
    public void setMobileError() {
        et_reg_mobno.setError(getString(R.string.err_mobile));
    }

    @Override
    public void onError(String msg) {
        alertProgress.alertinfo(this,msg);
    }

    @Override
    public void onBusinessNameIsEmpty() {
       // focusVieWRequestFocus(til_reg_BusinessName,getString(R.string.err_Business));
    }

    @Override
    public void clearError(int i) {

        switch (i)
        {
            case 1:
                til_reg_fname.setErrorEnabled(false);
                til_reg_fname.setError(null);
                focusView = null;
                break;
            case 2:
                til_reg_email.setErrorEnabled(false);
                til_reg_email.setError(null);
                focusView = null;
                break;
            case 3:
                til_reg_password.setErrorEnabled(false);
                til_reg_password.setError(null);
                focusView = null;
                break;
           /* case 5:
                til_reg_BusinessName.setErrorEnabled(false);
                til_reg_BusinessName.setError(null);
                focusView = null;
                break;*/
        }
    }

    /**
     * this is to request the focus view
     * @param til_reg_fnm is a TextInputLayout
     * @param error Error Message
     */
    private void focusVieWRequestFocus(TextInputLayout til_reg_fnm,String error)
    {
        focusView = til_reg_fnm;
        //  focusView.requestFocus();
        til_reg_fnm.setErrorEnabled(true);
        til_reg_fnm.setError(error);


    }
}

