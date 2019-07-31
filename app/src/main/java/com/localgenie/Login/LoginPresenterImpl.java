package com.localgenie.Login;

import android.support.annotation.NonNull;
import android.util.Log;

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
import com.localgenie.model.Data;
import com.localgenie.model.FacebookLoginPojo;
import com.localgenie.model.LoginResponse;
import com.localgenie.model.UserDetailsDataModel;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.Utility;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author Pramod
 * @since 16/12/2017.
 */

public class LoginPresenterImpl implements LoginPresenter,
        GoogleApiClient.OnConnectionFailedListener {

    private static String device_id = "";
    @Inject
    LoginView loginView;
    @Inject
    LoginActivity mContext;
    @Inject
    FacebookLoginHelper facebookLoginHelper;
    @Inject
    LSPServices lspServices;

    @Inject
    LoginModel model;
    @Inject
    SessionManager sessionManager;

    @Inject Gson gson;
    //@Inject
    private UserDetailsDataModel userDetailsDataModel;
    private String socialMediaId ,firstName , profilePic;

    private boolean flag_guest_login;

    private GoogleApiClient mGoogleApiClient;

    @Inject
    LoginPresenterImpl() {
        this.userDetailsDataModel = new UserDetailsDataModel();
    }

    @Override
    public void validateUname(String uname) {
        if (model.validateUserName(uname)) {
            loginView.setInvalidUname();
        }
    }

    @Override
    public void validateCredentials(String username, String password) {
        if (model.emptyUname(username)) {
            loginView.setUsernameError();
        } else if (model.validateUserName(username)) {
            //Log.e("LOGIN","Invalid uname");
            loginView.setInvalidUname();
        } else if (model.validatePassword(password)){
            //Log.e("LOGIN","Invalid pwd");
            loginView.setPasswordError();
        } else {
            normalLogin(username,password);
            //doLogin(username,password);
        }
    }

    /**
     * <h2>doLogin</h2>
     * <p> Calling login service and if success storing values in session manager and start main activity </p>
     * login_type -> 1- normal login, 2- Fb , 3-google
     * @param username: email id / phone number of user
     * @param password: password to login
     */
    @Override
    public void doLogin(final String username, String password) {
        if (loginView != null) {
            loginView.showProgress();
        }

        try {
            device_id = Utility.getDeviceId(this.mContext);
        } catch (Exception e) {
            e.printStackTrace();
            loginView.hideProgress();
        }

        flag_guest_login = sessionManager.getGuestLogin();

        Log.e("TAG", "doLogin: GuestLoggedIn "+flag_guest_login );

        if (flag_guest_login) {
            loginView.revokeGuestLogin(device_id);
            sessionManager.setAUTH(null);
        }

        switch (userDetailsDataModel.getLoginType())
        {
            case 2:
                password = "";
                userDetailsDataModel.setFacebookId(socialMediaId);
                break;
            case 3:
                password = "";
                userDetailsDataModel.setGoogleId(socialMediaId);
                break;
        }


        Date currentTime = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        String deviceTime = df.format(currentTime);
        deviceTime = deviceTime.replace('T',' ');
        final  String pass = password;

        /*Observable<Response<ResponseBody>> bad = */lspServices.performLogin(Constants.selLang,username,password,device_id,"",
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
                                            sessionManager.setCallToken(loginData.getCall().getAuthToken());
                                            if(!sessionManager.getFcmTopic().equals(""))
                                                FirebaseMessaging.getInstance().subscribeToTopic(sessionManager.getFcmTopic());
                                            loginView.hideProgress();
                                            loginView.onLoginSuccess(auth, username, pass);
                                        }
                                    }
                                    break;

                                case 404:
                                    if (userDetailsDataModel.getLoginType() == 2 || userDetailsDataModel.getLoginType() == 3) {
                                        Log.e("FB_LOGIN", "onNext: " + userDetailsDataModel.getEmailOrPhone());
                                        loginView.navToSignUp(userDetailsDataModel.getLoginType(), userDetailsDataModel,mGoogleApiClient);
                                    } else {
                                        try {
                                            if (value.errorBody()!=null) {
                                                JSONObject errJson = new JSONObject(value.errorBody().string());
                                                loginView.onError(errJson.getString("message"));
                                                loginView.hideProgress();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;
                                case Constants.SESSION_NoDues:

                                    try {
                                        if (value.errorBody() != null) {
                                            JSONObject errJson = new JSONObject(value.errorBody().string());
                                            loginView.onError(errJson.getString("message"));
                                            loginView.setEmailView(username);
                                            loginView.hideProgress();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        loginView.hideProgress();
                                    }
                                    break;
                                default:
                                    try {
                                        if (value.errorBody() != null) {
                                            JSONObject errJson = new JSONObject(value.errorBody().string());
                                            loginView.onError(errJson.getString("message"));
                                            loginView.hideProgress();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        loginView.hideProgress();
                                    }
                                    break;
                            }
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                            loginView.hideProgress();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.e("Error","error"+e.getMessage());
                        e.printStackTrace();
                        loginView.hideProgress();
                    }

                    @Override
                    public void onComplete()
                    {
                        loginView.hideProgress();

                    }
                });


    }

    @Override
    public void initializeFBGoogle() {
        FacebookSdk.sdkInitialize(mContext.getApplicationContext());

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder
                (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(mContext, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }


    @Override
    public void handleLoginType(CallbackManager callbackManager)
    {
        switch (model.getLoginType())
        {
            case 0:
                handleResultFromFB(callbackManager);
                break;

            case 2:
                handleResultFromFB(callbackManager);
                break;

            case 3:
                googleLogin();
                Log.e("LOGIN","Google Login");
                break;
        }
    }

    @Override
    public void storeLoginType(int loginType)
    {
        model.setLoginType(loginType);
    }

    @Override
    public void initializeFacebook() {
        facebookLoginHelper.initializeFacebookSdk(mContext);
    }

    @Override
    public void normalLogin(String mailId, String password) {
        userDetailsDataModel.setLoginType(1);
        doLogin(mailId, password);
    }

    @Override
    public void handleResultFromGoogle(GoogleSignInResult result) {
        if (result.isSuccess())
        {
            String personPhoto = "";
            GoogleSignInAccount account = result.getSignInAccount();
            if (account.getPhotoUrl() != null)
                personPhoto = account.getPhotoUrl().toString();

            userDetailsDataModel.setLoginType (3);
            userDetailsDataModel.setEmailOrPhone(account.getEmail());
            userDetailsDataModel.setPassword(account.getId());
            firstName = account.getDisplayName();
            socialMediaId = account.getId();
            profilePic = personPhoto;
            userDetailsDataModel.setFirstName(firstName);
            userDetailsDataModel.setLastName(account.getFamilyName());
            userDetailsDataModel.setProfilePicUrl(profilePic);
            doLogin(account.getEmail(), account.getId());
        }else{
            Log.d("TAG", "handleResultFromGoogle: "+result.getStatus().getStatusMessage());
        }
    }

    @Override
    public void handleResultFromFB(CallbackManager callbackManager) {
        facebookLoginHelper.refreshToken();
        Log.d("TAG", "handleResultFromFB: "+facebookLoginHelper);
        facebookLoginHelper.facebookLogin(callbackManager, facebookLoginHelper.createFacebook_requestData(),
                new FacebookLoginHelper.Facebook_callback() {
                    @Override
                    public void success(JSONObject json) {
                        Gson gson = new Gson();
                        Log.e("FB_DATA", "onSuccess: "+json);
                        FacebookLoginPojo facebookLogin_pojo = gson.fromJson(json.toString(), FacebookLoginPojo.class);

                        userDetailsDataModel.setLoginType(2);
                        userDetailsDataModel.setEmailOrPhone(facebookLogin_pojo.getEmail());
                        socialMediaId = facebookLogin_pojo.getId();
                        Log.e("FB","HASHID"+socialMediaId);
                        String firstName = facebookLogin_pojo.getFirst_name();
                        String lastName = facebookLogin_pojo.getLast_name();
                        profilePic = "https://graph.facebook.com/"+facebookLogin_pojo.getId()+"/picture?type=large";

                        userDetailsDataModel.setFirstName(firstName);
                        userDetailsDataModel.setLastName(lastName);
                        userDetailsDataModel.setProfilePicUrl(profilePic);

                        doLogin(facebookLogin_pojo.getEmail(), facebookLogin_pojo.getId());
                    }

                    @Override
                    public void error(String error) {
                        Log.e("FB","result facebook error: "+error);
                    }

                    @Override
                    public void cancel(String cancel) {
                        Log.e("FB","result facebook cancel: "+cancel);
                    }
                });
    }

    @Override
    public void googleLogin(){
      //  Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {});

      /*  Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

                Log.d("TAG", "onResult: "+status);
            }
        });*/
      try
      {
          if(mGoogleApiClient!=null)
          {
              Auth.GoogleSignInApi.signOut(mGoogleApiClient);
              loginView.openGoogleActivity(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient));
          }else
          {
              loginView.googleClientIsNotConnected();
          }
      }catch (Exception e)
      {
          loginView.googleClientIsNotConnected();
      }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("TAG", "onConnectionFailed: "+ connectionResult);
    }
}
