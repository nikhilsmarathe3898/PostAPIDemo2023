package com.localgenie.otp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.localgenie.model.OtpData;
import com.localgenie.model.ServerOtpResponse;
import com.localgenie.model.ServerResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.networking.ServiceFactory;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;

import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by ${3Embed} on 4/11/17.
 */

public class OtpPresenterImpl implements OtpPresenter {

    private OtpView otpView;

    @Inject
    SessionManagerImpl manager;

    @Inject
    OtpPresenterImpl(OtpView otpView) {
        this.otpView = otpView;


    }

    @Override
    public void resendOtp(final String otp,final String sid, final int trigger, final boolean resend_flag) {
        LSPServices service = ServiceFactory.createRetrofitService(LSPServices.class);
        Log.e("resendOTP"," SID got is :: "+sid);
        if (otpView != null) {
            if (resend_flag) {
                otpView.showProgress("RESEND");
            } else {
                otpView.showProgress("OTP_VERIFY");
            }
        }
        Observable<Response<ServerResponse>> response = service.resendOtp(Constants.selLang,sid,1,trigger);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ServerResponse>>() {

                    @Override
                    public void onNext(Response<ServerResponse> value)
                    {
                        Log.e("RSND","Resend OTP ::  "+value.code()+"  msg  "+value.message());
                        if (200 == value.code()) {
                            Log.e("TAG",value.body().getMessage());
                            //otpView.setErrorMsg(value.body().getGuestLoginMessage());
                            otpView.hideProgress();
                            if (trigger == 2) {
                                if (resend_flag) {
                                    Log.e("TAG","Resent OTP");
                                } else {
                                    if ("".equals(otp)) {
                                        Log.e("PRAMOD","For resend OTP");
                                    } else {
                                        verifyOtp(otp,sid,"y");
                                    }
                                    //otpView.navigateToChangePwd(sid);
                                }
                            } /*else {
                                otpView.navigateToLogin();
                            }*/
                        } else  {
                            Log.e("TAG",value.message());
                            otpView.hideProgress();
                            try {
                                if (value.errorBody() != null) {
                                    JSONObject errJson = new JSONObject(value.errorBody().toString());
                                    otpView.setErrorMsg(errJson.getString("message"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        Log.e("Error","error"+e.getMessage());
                        e.printStackTrace();
                        //signUpView.onError(e.getGuestLoginMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void verifyOtp(String otp, final String sid, final String flag) {
        // Retrofit instance creation
        if (otpView != null) {
            if ("y".equals(flag)) {
                Log.e("PRAMOD","Forgot PWD");
            } else {
                otpView.showProgress("OTP_VERIFY");
            }
        }
        LSPServices service = ServiceFactory.createRetrofitService(LSPServices.class);
        Log.e("VerifyOTP"," SID got is :: "+sid);
        int trigger = 2;
        if("change_phone".equals(flag))
        {
            trigger = 3;
        }

        Observable<Response<ServerResponse>> response =service.verifyOtp(Constants.selLang,otp,sid,trigger,1);

        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ServerResponse>>() {
                    @Override
                    public void onNext(Response<ServerResponse> value) {

                        Log.e("code","OTP req ::  "+value.code()+"  msg  "+value.message());
                        if (200 == value.code()) {
                            Log.e("TAG",value.body().getMessage());
                            otpView.setError();
                            otpView.hideProgress();
                            if (flag == null || "".equals(flag)) {
                                otpView.navigateToLogin();

                            }
                            else if("change_phone".equals(flag))
                            {
                                otpView.navToProfile();
                            }
                            else
                            {
                                otpView.navigateToChangePwd(sid);
                            }
                        } else  {
                            otpView.hideProgress();
                            try {
                                if (value.errorBody() != null) {
                                    JSONObject errJson = new JSONObject(value.errorBody().string());
                                    otpView.setErrorMsg(errJson.getString("message"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Error","error"+e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    @Override
    public void verifyPhone(final String otp, String sid, final String flag) {
        // Retrofit instance creation
        if (otpView!=null) {
            otpView.showProgress("PHONE_VERIFY");
        }
        LSPServices service = ServiceFactory.createRetrofitService(LSPServices.class);
        Log.e("VerifyPhone"," SID got is :: "+sid);
        Observable<Response<ServerOtpResponse>> response =service.verifyPhone(Constants.selLang,otp,sid);

        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ServerOtpResponse>>() {

                    @Override
                    public void onNext(Response<ServerOtpResponse> value)
                    {
                        Log.e("code","Verify otp req ::  "+value.code()+"  msg  "+value.message());
                        if (200 == value.code()) {
                            ServerOtpResponse serverOtpResponse = value.body();
                            if (serverOtpResponse!=null) {
                                Log.e("TAG", serverOtpResponse.getMessage());
                                //otpView.setError();
                                OtpData otpData = serverOtpResponse.getData();
                                if (otpData!=null) {
                                    String auth = otpData.getToken();
                                    /*if("profile".equals(flag))
                                        otpView.navToProfile();*/
                                    if("change_phone".equals(flag))
                                    {
                                        otpView.navToProfile();
                                    }
                                    else
                                    {
                                        otpView.navToHome(auth);
                                        manager.setProfilePicUrl(otpData.getProfilePic());
                                        manager.setSID(otpData.getSid());
                                        manager.setReferralCode(otpData.getReferralCode());
                                        manager.setGuestLogin(false);
                                        manager.setRegisterId(otpData.getRequester_id());
                                        manager.setFirstName(otpData.getFirstName());
                                        manager.setLastName(otpData.getLastName());
                                        manager.setEmail(otpData.getEmail());
                                        manager.setAUTH(auth);
                                        manager.setMobileNo(otpData.getPhone());
                                        manager.setFcmTopic(otpData.getFcmTopic());
                                        manager.setCountryCode(otpData.getCountryCode());
                                        manager.setCallToken(otpData.getCall().getAuthToken());
                                        if(!manager.getFcmTopic().equals(""))
                                            FirebaseMessaging.getInstance().subscribeToTopic(manager.getFcmTopic());
                                    }

                                }
                                otpView.hideProgress();
                            }
                            //otpView.navigateToLogin();

                        } else  {
                            otpView.hideProgress();
                            try {
                                if (value.errorBody()!=null) {
                                    JSONObject errJson = new JSONObject(value.errorBody().string());
                                    otpView.setErrorMsg(errJson.getString("message"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        Log.e("Error","error"+e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
