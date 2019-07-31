package com.localgenie.SplashScreen;

import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.localgenie.BuildConfig;
import com.localgenie.home.MainActivity;
import com.localgenie.networking.ChatApiService;
import com.localgenie.networking.ServiceFactory;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;
import com.utility.OnMyService;


import org.eclipse.paho.android.service.MqttService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class SplashActivity extends DaggerAppCompatActivity implements SplashContract.View {

    private static final String TAG = "SPLASHSCREEN";

    @Inject
    SplashContract.Presenter presenter;

    @Inject AlertProgress alertProgress;

    @Inject
    SessionManagerImpl manager;

    private String currentVersion;
    private boolean updateflag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // presenter.setView(this);
        manager.setProfileCalled(false);
        //startService(new Intent(getBaseContext(), OnMyService.class));
        updateflag = true;
        alertProgress.IPAddress((ipAddress, lat, lng) -> {
            manager.setIpAddress(ipAddress);
            Constants.latitude = lat;
            Constants.longitude = lng;
            Constants.currentLat = lat;
            Constants.currentLng = lng;
            manager.setLatitude(lat+"");
            manager.setLongitude(lng+"");
            Log.d(TAG, "onCreate: "+ manager.getIpAddress());
        });

        Log.d(TAG, "onCreate: "+ manager.getIpAddress());
        ChatApiService chatApiService = ServiceFactory.createRetrofitChatService(ChatApiService.class);
        Log.d(TAG, "onCreate: "+chatApiService);
        /* Observable<Response<ResponseBody>> responseObservable= chatApiService.getCty();
          responseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {
                        try {
                            Log.d(TAG, "onNext: "+responseBodyResponse.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        Log.d(TAG, "onNext: "+e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
*/
        presenter.providerService();
        presenter.subscribeNetworkObserver();
      /*  try {
            Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Utility.statusbar(this);
        printHashKey();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        currentVersion = BuildConfig.VERSION_NAME;
        if(updateflag)
        {
            updateflag = false;
            if(alertProgress.isNetworkAvailable(this))
            {
                presenter.callUpdateVersionApi(this,currentVersion,alertProgress);
            }else
            {
                alertProgress.showNetworkAlert(this);
                updateflag = true;
            }
        }


        //   updateflag =true;

    }



    @Override
    public void onProviderSuccess() {
        Log.d(TAG, "onProviderSuccess: DaggerSuccess");
    }

    @Override
    public void onProviderFailure() {

    }

    @Override
    public void onToMainActivity()
    {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        Constants.isJobDetailsOpen = false;
        startActivity(intent);
        onFinishCalled();
        //onAppOpen();
    }
    private void onAppOpen() {
        manager.setAppOpenTime(manager.getAppOpenTime() + 1);
        if(manager.getAppOpenTime() % 5 == 0 && !manager.getDontShowRate())
        {
            alertProgress.rateApp(SplashActivity.this, manager, new DialogInterfaceListner() {
                @Override
                public void dialogClick(boolean isClicked) {

                    if(isClicked)
                    {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        Constants.isJobDetailsOpen = false;
                        startActivity(intent);
                        onFinishCalled();
                    }

                }
            });
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            Constants.isJobDetailsOpen = false;
            startActivity(intent);
            onFinishCalled();
        }
    }

    @Override
    public void onFinishCalled() {
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        presenter.releaseSubscriber();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateflag = true;
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.d("Facebook", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("Facebook", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("Facebook", "printHashKey()", e);
        }
    }
}
