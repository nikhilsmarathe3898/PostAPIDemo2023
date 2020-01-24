package com.localgenie.signup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.localgenie.Login.FacebookLoginHelper;
import com.localgenie.countrypic.CountryPicker;
import com.localgenie.model.Data;
import com.localgenie.model.FacebookLoginPojo;
import com.localgenie.model.LoginResponse;
import com.localgenie.model.ServerResponse;
import com.localgenie.model.SignUpReq;
import com.localgenie.model.SignUpResponse;
import com.localgenie.model.ValidationReq;
import com.localgenie.model.UserDetailsDataModel;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.Utility;
import com.pojo.CheckFlagForValidity;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * <h1>SignUpPresenterImpl</h1>
 * <h4>This is a Controller class for SignUp Activity</h4>
 * This class is used for performing the business logic for our Activity/View and
 * this class is getting called from SignUpActivity and give a call to SignUpPresenter class.
 * @author Pramod
 * @version 1.0
 * @since 21-12-2017.
 */

public class SignUpPresenterImpl implements SignUpPresenter,
        GoogleApiClient.OnConnectionFailedListener {

    private static String device_id = "";
    @Inject
    LSPServices lspServices;
    @Inject
    SignUpActivity mContext;
    @Inject
    SessionManager sessionManager;
    @Inject
    SignUpView signUpView;
    @Inject
    FacebookLoginHelper facebookLoginHelper;
    @Inject
    SignUpModel model;
    @Inject Gson gson;
    private CompositeDisposable compositeDisposable;
    private CompositeDisposable emailDisposable;
    private CompositeDisposable phoneDisposable;
    private CheckFlagForValidity checklag;


    private UserDetailsDataModel userDetailsDataModel;
    private String socialMediaId;
    private GoogleApiClient mGoogleApiClient;

    @Inject
    SignUpPresenterImpl() {
        this.compositeDisposable = new CompositeDisposable();
        this.emailDisposable = new CompositeDisposable();
        this.phoneDisposable = new CompositeDisposable();
        this.userDetailsDataModel = new UserDetailsDataModel();
        checklag = new CheckFlagForValidity();


    }

    @Override
    public void validateFname(String firstName)
    {
        if (model.validateFname(firstName)) {
            signUpView.setFirstNameError();
            checklag.setnFlg(false);
        }else
        {
            signUpView.clearError(1);
            checklag.setnFlg(true);
        }
    }

    @Override
    public void validateLname(String lastname) {
        if (model.validateLname(lastname)) {
            signUpView.setLastNameError();
        }
    }

    @Override
    public boolean irregularPhone(CountryPicker mCountryPicker, Context context, String phone) {
        int max = Utility.getCountryMax(mCountryPicker, context);
        if (model.irregularPhone(phone, max)) {
            signUpView.setMobileInvalid();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void validatePhone(String flag_show_dialog, String countryCode, String phone) {

        if (TextUtils.isEmpty(phone)) {
            signUpView.setMobileError();
            checklag.setmFlg(false);
        } else if (validatePhoneNumber(countryCode,phone)) {
            signUpView.setMobileErrorMsg("");
            phoneAlreadyExists(flag_show_dialog, countryCode, phone);
        } else
        {
            checklag.setmFlg(false);
            signUpView.setMobileInvalid();
        }

    }

    @Override
    public void validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            signUpView.setPasswordError();
            checklag.setpFlg(false);
        } else if (model.validPassword(password)) {
            signUpView.setPasswordInvalid();
            checklag.setpFlg(false);
        }else {
            signUpView.clearError(3);
            checklag.setpFlg(true);
        }
    }

    @Override
    public void handleLoginType(CallbackManager callbackManager) {
        switch (model.getLoginType()) {
            //2 - Facebook , 3 - Google
            case 0:
                handleResultFromFB(callbackManager);
                break;

            case 2:
                handleResultFromFB(callbackManager);
                break;

            case 3:
                googleLogin();
                Log.e("SIGNUP", "Google Login");
                break;
        }
    }

    @Override
    public void storeLoginType(int loginType) {
        model.setLoginType(loginType);
        userDetailsDataModel.setLoginType(loginType);
    }

    @Override
    public void storeFbId(String facebookId) {
        userDetailsDataModel.setFacebookId(facebookId);
    }

    @Override
    public void storeGoogleId(String googleId) {
        userDetailsDataModel.setGoogleId(googleId);
    }

    @Override
    public void initializeFacebook() {
        facebookLoginHelper.initializeFacebookSdk(mContext);
    }

    @Override
    public void googleLogin() {
        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected())
        {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);/*.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
            }
        });*/
            signUpView.openGoogleActivity(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient));
        } else
        {
            initializeFBGoogle();
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mGoogleApiClient.connect();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    signUpView.openGoogleActivity(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient));
                }
            },1000);
        }

    }

    @Override
    public void handleResultFromGoogle(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();

            if (account != null) {

                Log.e("TAG", "success: " + account.getFamilyName() + "  HHH  " + account.getDisplayName());
                String firstName = account.getGivenName();
                String lastName = account.getFamilyName();
                String email = account.getEmail();

                userDetailsDataModel.setLoginType(3);
                userDetailsDataModel.setEmailOrPhone(account.getEmail());

                socialMediaId = account.getId();

                String profilePic = "";
                if (account.getPhotoUrl() != null)
                    profilePic = account.getPhotoUrl().toString();

                signUpView.setFieldsFromFB(firstName, lastName, email, "", profilePic);
            }
        }
    }

    @Override
    public void handleResultFromFB(CallbackManager callbackManager) {
        facebookLoginHelper.refreshToken();
        Log.d("TAG", "handleResultFromFB: " + facebookLoginHelper);
        facebookLoginHelper.facebookLogin(callbackManager, facebookLoginHelper.createFacebook_requestData(),
                new FacebookLoginHelper.Facebook_callback() {
                    @Override
                    public void success(JSONObject json) {
                        Gson gson = new Gson();
                        FacebookLoginPojo facebookLogin_pojo = gson.fromJson(json.toString(), FacebookLoginPojo.class);

                        Log.e("TAG", "success: " + facebookLogin_pojo.getEmail() + facebookLogin_pojo.getName());

                        userDetailsDataModel.setLoginType(2);
                        userDetailsDataModel.setEmailOrPhone(facebookLogin_pojo.getEmail());

                        String firstName = facebookLogin_pojo.getFirst_name();
                        String lastName = facebookLogin_pojo.getLast_name();

                        String email = facebookLogin_pojo.getEmail();

                        String profilePic = "https://graph.facebook.com/" + facebookLogin_pojo.getId() + "/picture?type=large";

                        socialMediaId = facebookLogin_pojo.getId();

                        // sessionManager.setProfilePicUrl(profilePic);

                        signUpView.setFieldsFromFB(firstName, lastName, email, "", profilePic);

                    }

                    @Override
                    public void error(String error) {
                        Log.e("FB", "result facebook error: " + error);
                    }

                    @Override
                    public void cancel(String cancel) {
                        Log.e("FB", "result facebook cancel: " + cancel);
                    }
                });

    }

    @Override
    public void initializeFBGoogle() {
        FacebookSdk.sdkInitialize(mContext.getApplicationContext());

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder
                (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("813316455969-spdmpj5i6jcd1v34h3pedkoekqun7k4h.apps.googleusercontent.com")
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(mContext, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("TAG", "onConnectionFailed: " + connectionResult);
    }

    @Override
    public void validateEmail(String flag_to_dialog, String email) {

        if (TextUtils.isEmpty(email)) {
            signUpView.setEmailError();
            checklag.seteFlg(false);
        } else if (model.validEmail(email)) {
            signUpView.setEmailInvalid("");
            checklag.seteFlg(false);
        } else {
            emailAlreadyExists(flag_to_dialog, email);
        }
    }

    @Override
    public void emailAlreadyExists(String dia, String email) {
        if (signUpView != null) {
            if ("y".equals(dia)) {
                signUpView.showProgress("EMAIL");
            }
        }

        ValidationReq req = new ValidationReq(email);
        final Observable<Response<ServerResponse>> response = lspServices.checkEmailExists(Constants.selLang,req);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ServerResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        emailDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ServerResponse> value) {
                        switch (value.code()) {
                            case 200:
                                Log.e("code", "Email Exists " + value.code() + "  msg  " + value.message());
                                // Passing empty string to make the setError to null as this means the Email is valid i.e.,. it is not present in our system.
                                signUpView.clearError(2);
                                signUpView.hideProgress();

                                checklag.seteFlg(true);
                                if("n".equals(dia))
                                {
                                    if(!checklag.ispFlg())
                                    {
                                        checkPasFlag();
                                    }else
                                        checkMobFlag("n");
                                }
                                break;

                            default:
                                signUpView.hideProgress();
                                try {
                                    if (value.errorBody() != null) {
                                        JSONObject errJson = new JSONObject(value.errorBody().string());
                                        signUpView.setEmailInvalid(errJson.getString("message"));
                                        checklag.seteFlg(false);
                                    }
                                    //       isEmailValid[0] = false;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        signUpView.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        signUpView.hideProgress();
                    }
                });
        //return isEmailValid[0];
    }

    @Override
    public void phoneAlreadyExists(String dia, String countryCode, String phone) {
        if (signUpView != null) {
            if ("y".equals(dia)) {
                signUpView.showProgress("PHONE");
            }
        }
        final Observable<Response<ServerResponse>> response = lspServices.checkPhoneExists(Constants.selLang,countryCode, phone);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ServerResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        phoneDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ServerResponse> value) {
                        Log.e("code", "Phone Exists " + value.code() + "  msg  " + value.message());
                        switch (value.code()) {
                            case 200:
                                // Passing empty string to make the setError to null as this means the mobile number is valid as its not present in our system.
                                signUpView.setMobileErrorMsg("");
                                checklag.setmFlg(true);
                                if("n".equals(dia))
                                {
                                    checkPasBusinessFlag();
                                }else
                                    signUpView.hideProgress();
                                Log.e("code", "Phone Exists " + value.code() + "  msg  " + value.message());
                                break;

                            default:
                                checklag.setmFlg(false);
                                signUpView.hideProgress();
                                try {
                                    if (value.errorBody() != null) {
                                        JSONObject errJson = new JSONObject(value.errorBody().string());
                                        signUpView.setMobileErrorMsg(errJson.getString("message"));


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Error", "error" + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        signUpView.hideProgress();

                    }
                });
    }
    String profilePic,  firstname,  lastName,  email,  password,  countryCode,  phone,  referralCode,bussinessName;

    @Override
    public void doRegister(String profilePic, String firstname, String lastName, final String email, String password,
                           String countryCode, String phone, String referralCode)
    {

        signUpView.showPro();
        if (userDetailsDataModel.getLoginType() == null || userDetailsDataModel.getLoginType() == 0)
            userDetailsDataModel.setLoginType(1);

        switch (userDetailsDataModel.getLoginType()) {
            case 2:
                userDetailsDataModel.setFacebookId(socialMediaId);

                break;
            case 3:
                userDetailsDataModel.setGoogleId(socialMediaId);
                break;

        }
        this.profilePic = profilePic;
        this.firstname = firstname;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.countryCode = countryCode;
        this.phone = phone;
        this.referralCode = referralCode;

        Log.e("SIGNUP", "ipAdress :  " + sessionManager.getIpAddress());

        try {
            device_id = Utility.getDeviceId(this.mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!checklag.isnFlg())
        {
            if (model.validateFname(firstname))
            {
                checklag.setnFlg(false);
                signUpView.setFirstNameError();
                signUpView.hideProgress();

            } else
            {
                checklag.setnFlg(true);
                signUpView.clearError(1);
                checkEmailFlag(email);
            }

        }
        else if(!checklag.iseFlg())
        {
            checkEmailFlag(email);

        }
        else if(!checklag.ispFlg())
        {
            checkPasFlag();

        }
        else if(!checklag.ismFlg())
        {
            checkMobFlag("n");
        }
       /* else if (!checklag.isbNFlg())
        {
            checkPasBusinessFlag();
        }*/
        else
        {
            signUp();
        }

    }

    private void checkEmailFlag(String email) {

        if (!checklag.iseFlg()) {
            if (validateEmailFlag(email)) {
                emailAlreadyExists("n", email);
            }
        } else {
            checkPasFlag();
        }
    }
    private boolean validateEmailFlag(String email)
    {
        if (TextUtils.isEmpty(email)) {
            signUpView.setEmailError();
            checklag.seteFlg(false);
            signUpView.hideProgress();
            return false;
        } else if (model.validEmail(email)) {
            signUpView.setEmailInvalid("");
            checklag.seteFlg(false);
            signUpView.hideProgress();
            return false;
        } else {
            checklag.seteFlg(true);
            return true;
        }
    }

    private void checkPasFlag(){

        if (!checklag.ispFlg()) {

            if (checkPasswordVal(password)) {
                checkMobFlag("n");
            } /*else
                signUpView.onValidationError(3);*/
        } else {
            checkMobFlag("n");
        }
    }

    private boolean checkPasswordVal(String password)
    {
        if (TextUtils.isEmpty(password)) {
            signUpView.setPasswordError();
            checklag.setpFlg(false);
            signUpView.hideProgress();
            return false;
        } else if (model.validPassword(password)) {
            signUpView.setPasswordInvalid();
            checklag.setpFlg(false);
            signUpView.hideProgress();
            return false;
        }else
        {
            checklag.setpFlg(true);
            signUpView.clearError(3);
            return true;
        }


    }


    private void checkMobFlag(String isDialog) {

        if (!checklag.ismFlg()) {
            checkMobileValidation(isDialog);
        } else {
            checkPasBusinessFlag();
        }
    }

    private void checkMobileValidation(String dialog)
    {
        if (TextUtils.isEmpty(phone)) {
            signUpView.setMobileError();
            checklag.setmFlg(false);
            signUpView.hideProgress();

        } else if (validatePhoneNumber(countryCode,phone)) {

            signUpView.setMobileErrorMsg("");
            phoneAlreadyExists(dialog, countryCode, phone);
        } else
        {
            signUpView.setMobileInvalid();
            checklag.setmFlg(false);
            signUpView.hideProgress();

            //  return true;

        }
    }

    private void checkPasBusinessFlag() {

        //for now pu business flag always true
        checklag.setbNFlg(true);

        if (!checklag.isbNFlg()) {
            if (checkBusinessVal()) {
                signUpView.onBusinessNameIsEmpty();
                signUpView.hideProgress();
            } else
            {
                signUpView.clearError(5);
                checklag.setbNFlg(true);
                signUp();
            }

        } else {
            signUp();
        }
    }

    private boolean checkBusinessVal() {

        return model.validateFname(bussinessName);

    }

    @Override
    public void validateBusiness(String business) {
        if (model.validateFname(business)) {
            signUpView.onBusinessNameIsEmpty();
            checklag.setbNFlg(false);
        }else
        {
            signUpView.clearError(5);
            checklag.setbNFlg(true);

        }
    }

    private void signUp() {

        Date currentTime = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String deviceTime = df.format(currentTime);
        deviceTime = deviceTime.replace('T', ' ');
        Log.e("DATE", "date :: " + deviceTime +" profilePic "+profilePic +" UserType "
                +userDetailsDataModel.getLoginType() +" Fid "+
                userDetailsDataModel.getFacebookId()+" gId "+userDetailsDataModel.getGoogleId());
        SignUpReq req = new SignUpReq(firstname, lastName, email, password, countryCode, phone, profilePic,
                userDetailsDataModel.getLoginType(), userDetailsDataModel.getFacebookId(),
                userDetailsDataModel.getGoogleId(), Constants.latitude, Constants.longitude,
                "", 1, Constants.DEVICE_TYPE, device_id, "",
                Constants.APP_VERSION, Constants.DEVICE_OS_VERSION, Constants.DEVICE_MAKER,
                Constants.DEVICE_MODEL, deviceTime, referralCode, sessionManager.getIpAddress());
        final Observable<Response<SignUpResponse>> response = lspServices.doRegister(Constants.selLang,req);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<SignUpResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<SignUpResponse> value) {

                        Log.d("TAG", "onNext: "+value.code());
                        switch (value.code()) {
                            case 200:
                                SignUpResponse signUpResponse = value.body();

                                if (signUpResponse != null) {
                                    String sid = signUpResponse.getData().getSid();
                                    long expireOtp = signUpResponse.getData().getExpireOtp();
                                    sessionManager.setSID(sid);
                                    sessionManager.setExpireOtp(expireOtp);
                                    sessionManager.setGuestLogin(false);
                                    signUpView.hideProgress();
                                    signUpView.navigateToLogin();
                                }
                                break;

                            case 412:
                                if(userDetailsDataModel.getLoginType()==2 || userDetailsDataModel.getLoginType()==3)
                                {
                                    loginApi(email,"");
                                }
                                break;
                            default:

                                try {
                                    if (value.errorBody() != null) {

                                        JSONObject errJson = new JSONObject(value.errorBody().string());
                                        signUpView.onError(errJson.getString("message"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                signUpView.hideProgress();
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //Log.e("Error","error"+e.getGuestLoginMessage());
                        e.printStackTrace();
                        signUpView.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        signUpView.hideProgress();
                    }
                });
    }

    private void loginApi(String email, String password) {
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String deviceTime = df.format(currentTime);
        deviceTime = deviceTime.replace('T',' ');
        lspServices.performLogin(Constants.selLang,email,password,device_id,"",
                Constants.APP_VERSION, Constants.DEVICE_MAKER, Constants.DEVICE_MODEL,
                2,deviceTime,userDetailsDataModel.getLoginType(),
                Constants.DEVICE_OS_VERSION,userDetailsDataModel.getFacebookId(),
                userDetailsDataModel.getGoogleId(),Constants.latitude,
                Constants.longitude).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> value) {

                        int code = value.code();
                        Log.e("TAG", "onNextLogin: " + code);

                        try {

                            switch (code) {
                                case 200:
                                    String response = value.body().string();
                                    Log.d("TAG", "onNextSign: "+response);
                                    LoginResponse loginResponse = gson.fromJson(response,LoginResponse.class);
                                    if (loginResponse != null) {
                                        Data loginData = loginResponse.getData();
                                        if (loginData != null) {
                                            String auth = loginData.getToken();
                                            String sid = loginData.getSid();
                                            String refCode = loginData.getReferralCode();

                                            sessionManager.setSID(sid);
                                            sessionManager.setReferralCode(refCode);
                                            sessionManager.setRegisterId(loginData.getRequesterId());
                                            sessionManager.setFirstName(loginData.getFirstName());
                                            sessionManager.setLastName(loginData.getLastName());
                                            sessionManager.setEmail(loginData.getEmail());
                                            sessionManager.setAUTH(auth);
                                            sessionManager.setMobileNo(loginData.getPhone());
                                            sessionManager.setCountryCode(loginData.getCountryCode());
                                            sessionManager.setProfilePicUrl(loginData.getProfilePic());

                                            sessionManager.setFcmTopic(loginData.getFcmTopic());
                                            sessionManager.setCountryCode(loginData.getCountryCode());
                                            sessionManager.setDefaultCardId(loginData.getCardDetail().getId());
                                            sessionManager.setDefaultCardNum(loginData.getCardDetail().getLast4());
                                            sessionManager.setDefaultCardName(loginData.getCardDetail().getBrand());
                                            if(!sessionManager.getFcmTopic().equals(""))
                                                FirebaseMessaging.getInstance().subscribeToTopic(sessionManager.getFcmTopic());
                                            signUpView.hideProgress();
                                            signUpView.onLoginSuccess(auth, email, password);
                                        }
                                        signUpView.hideProgress();
                                    }
                                    break;

                                case 404:
                                    if (userDetailsDataModel.getLoginType() == 2 || userDetailsDataModel.getLoginType() == 3) {
                                        Log.e("FB_LOGIN", "onNext: " + userDetailsDataModel.getEmailOrPhone());
                                        //   signUpView.navToSignUp(userDetailsDataModel.getLoginType(), userDetailsDataModel,mGoogleApiClient);
                                    } else {
                                        try {
                                            if (value.errorBody()!=null) {
                                                JSONObject errJson = new JSONObject(value.errorBody().string());
                                                signUpView.onError(errJson.getString("message"));
                                                signUpView.hideProgress();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    signUpView.hideProgress();
                                    break;
                                case Constants.SESSION_NoDues:

                                    try {
                                        if (value.errorBody() != null) {
                                            JSONObject errJson = new JSONObject(value.errorBody().string());
                                            signUpView.onError(errJson.getString("message"));
                                            //  signUpView.setEmailView(username);
                                            signUpView.hideProgress();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        signUpView.hideProgress();
                                    }
                                    signUpView.hideProgress();
                                    break;
                                default:
                                    try {
                                        if (value.errorBody() != null) {
                                            JSONObject errJson = new JSONObject(value.errorBody().string());
                                            signUpView.onError(errJson.getString("message"));
                                            signUpView.hideProgress();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        signUpView.hideProgress();
                                    }
                                    signUpView.hideProgress();
                                    break;
                            }
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                            signUpView.hideProgress();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.e("Error","error"+e.getMessage());
                        e.printStackTrace();
                        signUpView.hideProgress();
                    }

                    @Override
                    public void onComplete()
                    {
                        signUpView.hideProgress();

                    }
                });
    }

    @Override
    public boolean validatePhoneNumber(String countryCode, String mobileNumber)
    {
        String phoneNumberE164Format = countryCode.concat(mobileNumber);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumberProto = phoneUtil.parse(phoneNumberE164Format, null);
            boolean isValid = phoneUtil.isValidNumber(phoneNumberProto); // returns true if valid

            if (isValid  &&  ( phoneUtil.getNumberType(phoneNumberProto)== PhoneNumberUtil.PhoneNumberType.MOBILE||phoneUtil.getNumberType(phoneNumberProto)== PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE)  )
            {
                return true;
            }else
                return false;

        }catch (NumberParseException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setTermsCondition(TextView tv, String privacyTerms)
    {
        String tvt = tv.getText().toString();
        int ofe = tvt.indexOf(privacyTerms, 0);
        Spannable wordToSpan = new SpannableString(tv.getText());
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(privacyTerms, ofs);
            if (ofe == -1)
                break;
            else {
                wordToSpan.setSpan(new BackgroundColorSpan(0xFFFFFFFF), ofe, ofe + privacyTerms.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
            }
        }
    }

    @Override
    public void setOnclickHighlighted(TextView tv, String textToHighlight, View.OnClickListener onClickListener) {
        String tvt = tv.getText().toString();
        int ofe = tvt.indexOf(textToHighlight, 0);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (onClickListener != null) onClickListener.onClick(textView);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(0xff0000ff);
                ds.setUnderlineText(true);
            }
        };
        SpannableString wordToSpan = new SpannableString(tv.getText());
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(textToHighlight, ofs);
            if (ofe == -1)
                break;
            else {
                wordToSpan.setSpan(clickableSpan, ofe, ofe + textToHighlight.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    @Override
    public void checkReferralCode(String trim) {

        Observable<Response<ResponseBody>>responseObservable = lspServices.onReferralCodeCheck(Constants.selLang,
                trim);

        responseObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();

                        if(code == 200)
                        {
                            try {
                                String response = responseBodyResponse.body().string();
                                Log.d("TAGSIGNUP", "onNextRESp: "+response);
                                signUpView.onReferral(trim);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void call()
    {
        int n = 7;
        int a = 1;
        int b= n-1;

        char[][] ch  = new char[n][n];

        for(int i = 0;i<1;i++)
        {
            for(int j = 0;j<n;j++)
            {
                ch[i][j] = '*';
                System.out.print( ch[i][j]);
            }
        }
        for(int i = 0;i<n-1;i++) {
            System.out.print("*");
            if (a < n && a != b)
            {
                ch[i][a] = '*';
                System.out.print(ch[i][a]);
                a++;
            }
            if(b>1 && a!=b)
            {
                ch[i][a] = '*';
                System.out.print(ch[i][b]);
                b--;
            }

            if( a==b)
            {
                ch[i][a] = '3';
                System.out.print(ch[a][b]);

            }
        }
        for(int i = n;i<n+1;i++)
        {
            for(int j = 0;j<n;j++)
            {
                ch[i][a] = '*';
                System.out.print(ch[i][j]);
            }
        }
    }
}
