package com.localgenie.Dagger2;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.localgenie.Login.LoginModel;
import com.localgenie.biddingFlow.AnswerHashMap;
import com.localgenie.countrypic.CountryPicker;
import com.localgenie.signup.SignUpModel;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManagerImpl;
import com.mqtt.MQTTManager;
import com.utility.AlertProgress;
import com.utility.PermissionsManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import com.pojo.MyBookingObservable;
import com.pojo.ProviderObservable;

import java.util.HashMap;

/**
 * @author Pramod
 * @since 05-01-2018
 */
@Module
public class UtilsModule {

    @Provides
    @Singleton
    CompositeDisposable provideDisposable(){
        return new CompositeDisposable();
    }

    @Provides
    @Singleton
    LoginModel loginModel() { return new LoginModel();}

    @Provides
    @Singleton
    HashMap<String,AnswerHashMap> answerHashMap() { return new HashMap<>();}

    @Provides
    @Singleton
    SignUpModel provideSingUpModel()
    {
        return new SignUpModel();
    }

    @Provides
    @Singleton
    CountryPicker countryPicker() { return new CountryPicker();}

    @Provides
    @Singleton
    ProviderObservable provideObservable(){return new ProviderObservable();}

    @Named("Booking")
    @Provides
    @Singleton
    MyBookingObservable provideMyBookngObservable(){return new MyBookingObservable();}

   //, ProviderObservable rxProviderObserver,
    @Provides
    @Singleton
    MQTTManager  mqttManager(Context context,
                             SessionManagerImpl sessionManagerImpl, Gson gson,MyBookingObservable rxMyBookingObservable
    ,ProviderObservable providerObservable)//,MyBookingObservable rxMyBookingObservable
    {
        return new MQTTManager(context,sessionManagerImpl,gson,rxMyBookingObservable,providerObservable);//rxProviderObserver,
    }


    @Provides
    @Singleton
    Gson provideGSON(){return new Gson();}

    @Provides
    @Singleton
    AppTypeface provideAppTypeFace(Context mContext){return AppTypeface.getInstance(mContext);}

    @Provides
    @Singleton
    PermissionsManager providePermissionMgr(Context mContext) {return new PermissionsManager();}

    @Provides
    @Singleton
    AlertProgress provideAlertProgress(Context mContext) {return new AlertProgress(mContext);}

    @Provides
    @Singleton
    LinearLayoutManager provideLinearLayoutManager(Context mContext)
    {
        return new LinearLayoutManager(mContext);

    }


    /*@Provides
    @Singleton
    SessionManager provideSessionManager(Context mContext)
    {
        return new SessionManager(mContext);
    }*/

   /* @Provides
    @Singleton
    LocationUtil provideLocationUtil(Activity mContext){return new LocationUtil(mContext);}*/

}
