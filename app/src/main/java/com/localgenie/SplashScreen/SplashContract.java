package com.localgenie.SplashScreen;

import android.content.Context;

import com.utility.AlertProgress;

/**
 * Created by ${3Embed} on 16/11/17.
 */

public interface SplashContract {
    interface View{
        void onProviderSuccess();
        void onProviderFailure();

        void onFinishCalled();

        void onToMainActivity();

    }
    interface Presenter{
        void subscribeNetworkObserver();
        void releaseSubscriber();
        boolean checkInternet();
        void providerService();
        void setView(View view);

        void callUpdateVersionApi(Context mContext, String currentVersion, AlertProgress alertProgress);

    }
}