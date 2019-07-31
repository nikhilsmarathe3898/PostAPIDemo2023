package com.localgenie.forgotpassword;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.localgenie.model.ForgotPwdData;
import com.localgenie.model.ForgotPwdReq;
import com.localgenie.model.ForgotPwdResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManager;

import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author Pramod
 * @since 21-12-2017
 */

public class ForgotPwdPresenterImpl implements ForgotPwdPresenter {

    @Inject
    ForgotPwdView forgotPwdView;


    @Inject
    LSPServices lspServices;

    @Inject
    SessionManager sessionManager;


    @Inject
    ForgotPwdPresenterImpl() {

    }

    @Override
    public boolean validateEmail(String email) {
        return email == null || email.length() == 0 || "".equals(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches() && !Patterns.DOMAIN_NAME.matcher(email).matches();
    }

    @Override
    public boolean validatePhone(String phone) {
        return TextUtils.isEmpty(phone) || "".equals(phone) || !Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public void forgotPassword(String emailOrPhone,String countryCode, final int type) {
        ForgotPwdReq req = new ForgotPwdReq(emailOrPhone, countryCode, 1, type);
        Observable<Response<ForgotPwdResponse>> response = lspServices.forgotPassword(Constants.selLang,req);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ForgotPwdResponse>>() {


                    @Override
                    public void onNext(Response<ForgotPwdResponse> value)
                    {
                        //switch
                        switch (value.code()) {

                            case 200:
                                ForgotPwdResponse forgotPwdResponse = value.body();
                                if (forgotPwdResponse!=null) {
                                    ForgotPwdData forgotPwdData = forgotPwdResponse.getData();
                                    if (forgotPwdData != null) {
                                        Log.e("FPWD", "sid from response :: " + forgotPwdData.getSid());
                                        String sid = forgotPwdData.getSid();
                                        long expireOtp = forgotPwdData.getExpireOtp();
                                        sessionManager.setExpireOtp(expireOtp);
                                        if (type == 2)
                                            forgotPwdView.navToLogin(forgotPwdResponse.getMessage());
                                        else
                                            forgotPwdView.navtoOTP(sid,expireOtp);
                                    }
                                }
                                break;

                            default:
                                try {
                                    if (value.errorBody()!=null) {
                                        JSONObject errJson = new JSONObject(value.errorBody().string());
                                        forgotPwdView.setError(errJson.getString("message"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }
}
