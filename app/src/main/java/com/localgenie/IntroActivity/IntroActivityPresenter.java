package com.localgenie.IntroActivity;

import android.util.Log;

import com.google.gson.Gson;
import com.localgenie.model.guest_login.GuestLoginData;
import com.localgenie.model.guest_login.GuestLoginResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.LocaleUtil;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.pojo.LanguageResponse;
import com.pojo.LanguagesList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author Pramod
 * @since  16/11/17.
 */

public class IntroActivityPresenter implements IntroActivityContract.IntroPresenter {
    private static String device_id = "";

    @Inject
    IntroActivityContract.IntroView introView;

    @Inject
    IntroActivity mContext;

    @Inject
    LSPServices lspServices;
    @Inject
    Gson gson;

    @Inject
    SessionManagerImpl sessionManager;


    @Inject
    IntroActivityPresenter() {

    }

    @Override
    public void doGuestLogin() {
        if (introView != null) {
            introView.showProgress();
        }

        try {
            device_id = Utility.getDeviceId(this.mContext);
        } catch (Exception e) {
            e.printStackTrace();
            introView.hideProgress();
        }

        Date currentTime = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String deviceTime = df.format(currentTime);
        deviceTime = deviceTime.replace('T',' ');

        Observable<Response<GuestLoginResponse>> bad = lspServices.doGuestLogin(Constants.selLang,device_id, Constants.APP_VERSION, Constants.DEVICE_MAKER, Constants.DEVICE_MODEL,2,deviceTime, Constants.DEVICE_OS_VERSION,sessionManager.getIpAddress());
        bad.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<GuestLoginResponse>>() {

                    @Override
                    public void onNext(Response<GuestLoginResponse> value) {
                        Log.e("TAG","Req URL :   "+value.raw().request().url());

                        try {
                            switch (value.code()) {
                                case 200:
                                    //try {
                                        GuestLoginResponse loginResponse = value.body();
                                        if (loginResponse != null) {
                                            GuestLoginData loginData = loginResponse.getGuestLoginData();
                                            if (loginData != null) {
                                                String auth = loginData.getToken();
                                                String sid = loginData.getSid();

                                                sessionManager.setAUTH(auth);
                                                sessionManager.setSID(sid);
                                                sessionManager.setEmail(device_id);
                                                sessionManager.setGuestLogin(true);

                                                introView.loginSuccess(auth,device_id);
                                            } else {
                                                introView.hideProgress();
                                            }
                                        }
                                    /*} catch (Exception e) {
                                        introView.hideProgress();
                                    }*/
                                    break;

                                default:
                                    try {
                                        if (value.errorBody()!=null) {
                                            JSONObject errJson = new JSONObject(value.errorBody().string());
                                            introView.onError(errJson.getString("message"));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Throwable e)
                    {
                        Log.e("Error","Intro error "+e.getMessage());
                        e.printStackTrace();
                        introView.hideProgress();
                    }
                    @Override
                    public void onComplete() {
                        introView.hideProgress();
                    }
                });
    }

    @Override
    public void onLanguageCalled()
    {
      Observable<Response<ResponseBody>> languageResponse = lspServices.onLanguageCalled(Constants.selLang);

      languageResponse.subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                  @Override
                  public void onNext(Response<ResponseBody> responseBodyResponse) {

                      int code = responseBodyResponse.code();
                      try {
                      if(code==200)
                      {

                              String response = responseBodyResponse.body().string();

                          Log.d("TAG", "onNextLAnguage: "+response);
                          LanguageResponse languageResponses = gson.fromJson(response,LanguageResponse.class);
/*
                          Log.d("TAG", "onNext: "+languageResponses.getLanguagesLists().get(0).getCode()
                          +" sess "+sessionManager.getLanguageSettings().getCode());*/

                          boolean isLanguage = false;
                          for(int i = 0;i<languageResponses.getLanguagesLists().size(); i++)
                          {
                              if(Constants.selLang.equals(languageResponses.getLanguagesLists().get(i).getCode()))
                              {
                                  isLanguage = true;
                                  introView.showLanguagesDialog(languageResponses.getLanguagesLists().indexOf(languageResponses.getLanguagesLists().get(i)),languageResponses.getLanguagesLists());
                                  break;
                              }
                          }
                          if(!isLanguage)
                              introView.showLanguagesDialog(-1,languageResponses.getLanguagesLists());
                      }else
                      {
                          String response = responseBodyResponse.errorBody().string();

                          introView.onError(new JSONObject(response).getString("message"));
                      }
                      } catch (IOException e) {
                          e.printStackTrace();
                      } catch (JSONException e) {
                          e.printStackTrace();
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

    @Override
    public void changeLanguage(String langCode, String langName, int currentLanguage)
    {
      if (currentLanguage != -1) {
        LocaleUtil.changeAppLanguage(mContext, currentLanguage,true);
      }
       // sessionManager.setLanguageSettings(new LanguagesList(langCode,langName,direction));
        introView.setLanguage(langName,true);
    }
}
