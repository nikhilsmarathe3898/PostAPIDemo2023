package com.localgenie.SplashScreen;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.localgenie.IntroActivity.IntroActivity;
import com.localgenie.R;
import com.localgenie.RxObservers.RxNetworkObserver;
import com.localgenie.networking.ConnectionType;
import com.localgenie.networking.LSPServices;
import com.localgenie.networking.NetworkStateHolder;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import org.json.JSONObject;

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
 * Created by ${3Embed} on 16/11/17.
 */

public class SplashPresenter implements SplashContract.Presenter {

    private static final String TAG = "SplashPresenter";
    @Inject
    SplashContract.View splashView;
    //LSPServices services;


    @Inject
    LSPServices lspServices;

    @Inject
    SessionManagerImpl manager;
    //@Inject
    RxNetworkObserver rxNetworkObserver;

    //@Inject
    NetworkStateHolder networkStateHolder;

    CompositeDisposable compositeDisposable;

    @Inject
    public SplashPresenter(SplashContract.View splashView){
        this.splashView = splashView;
        this.networkStateHolder = new NetworkStateHolder();
        this.rxNetworkObserver = new RxNetworkObserver();
        //((LSPApplication)context).getAppComponent().inject(this);
    }

    @Override
    public void providerService() {
        splashView.onProviderSuccess();
    }

    @Override
    public void setView(SplashContract.View view) {
      //  this.splashView=view;
    }

    @Override
    public void subscribeNetworkObserver() {
        Observer<NetworkStateHolder> observer = new Observer<NetworkStateHolder>()
        {
            @Override
            public void onSubscribe(Disposable d)
            {
                compositeDisposable.add(d);
            }
            @Override
            public void onNext(NetworkStateHolder value)
            {

                Log.e("NW A","Network Status ::  "+value.getMessage());

                ConnectionType type=value.getConnectionType();

                Log.e("NW A","Conn type ::  "+type);

            }
            @Override
            public void onError(Throwable e)
            {
                e.printStackTrace();
            }
            @Override
            public void onComplete()
            {}
        };
        rxNetworkObserver.subscribeOn(Schedulers.newThread());
        rxNetworkObserver.observeOn(AndroidSchedulers.mainThread());
        rxNetworkObserver.subscribe(observer);

    }

    @Override
    public boolean checkInternet()
    {
        return networkStateHolder.isConnected();
    }

    @Override
    public void releaseSubscriber() {

    }
    AlertProgress alertProgress1;
    @Override
    public void callUpdateVersionApi(Context mContext, String currentVersion, AlertProgress alertProgress) {

         alertProgress1 = alertProgress;
        Observable<Response<ResponseBody>> obsResponseBody = lspServices.onTOGetAppVersion(Constants.selLang
        ,12);

        obsResponseBody.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {


                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code  = responseBodyResponse.code();
                        Resources res = mContext.getResources();

                        try
                        {
                            if(code == 200)
                            {
                                assert responseBodyResponse.body() != null;
                                String respon = responseBodyResponse.body().string();

                                JSONObject jsonObject = new JSONObject(respon);

                                String appVersion = jsonObject.getJSONObject("data").getString("appVersion");
                                boolean isMan = jsonObject.getJSONObject("data").getBoolean("mandatory");

                                Log.d(TAG, "onNextSplash: "+currentVersion +" appVersion "+appVersion);

                                if(currentVersion.compareToIgnoreCase(appVersion)<0)
                                {
                                    if(isMan)
                                    {
                                      alertProgress.alertPositiveOnclick(mContext, res.getString(R.string.msgMandatoryupdate),
                                              res.getString(R.string.app_name), mContext.getResources().getString(R.string.update),new DialogInterfaceListner() {
                                                  @Override
                                                  public void dialogClick(boolean isClicked) {

                                                      if(isClicked)
                                                      {
                                                          final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
                                                          try {
                                                              mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                          } catch (android.content.ActivityNotFoundException anfe) {
                                                              mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                                          }
                                                      }
                                                  }
                                              });
                                    }else
                                    {
                                        alertProgress.alertPositiveNegativeOnclick(mContext, res.getString(R.string.msgMandatoryupdate),
                                                res.getString(R.string.app_name),res.getString(R.string.update),res.getString(R.string.later),false, new DialogInterfaceListner() {
                                                    @Override
                                                    public void dialogClick(boolean isClicked) {

                                                        if(isClicked)
                                                        {
                                                            final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
                                                            try {
                                                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                                            }
                                                        }else
                                                        {
                                                            callNextIntent(mContext);
                                                        }
                                                    }
                                                });
                                    }
                                }else
                                {
                                    callNextIntent(mContext);
                                }


                            }else
                            {
                                String respon = responseBodyResponse.errorBody().string();

                                JSONObject jsonObject = new JSONObject(respon);
                                alertProgress.alertinfo(mContext,jsonObject.getString("message"));
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                        alertProgress.tryAgain(mContext, mContext.getString(R.string.pleaseCheckInternet), mContext.getResources().getString(R.string.try_again), new DialogInterfaceListner() {
                            @Override
                            public void dialogClick(boolean isClicked) {
                                if(isClicked){
                                    callUpdateVersionApi(mContext, currentVersion, alertProgress);
                                }
                            }
                        });
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void callNextIntent(Context mContext)
    {

        if("".equals(manager.getAUTH()) || manager.getAUTH()== null || TextUtils.isEmpty(manager.getSID().trim()))
        {
            Log.d(TAG, "workForOnResumeif: ");
            Intent intent =new Intent(mContext,IntroActivity.class);
            mContext.startActivity(intent);
            splashView.onFinishCalled();
        } else {
           // Constants.selLang = manager.getLanguageSettings().getCode();

            splashView.onToMainActivity();


        }

    }


}
