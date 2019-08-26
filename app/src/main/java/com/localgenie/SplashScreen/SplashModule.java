package com.localgenie.SplashScreen;

import android.app.Activity;

import com.localgenie.Dagger2.ActivityScoped;
import com.utility.NotificationHandler;

import dagger.Binds;
import dagger.Module;



/**
 * Created by Pramod on 11/12/17.
 */

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link SplashPresenter}.
 */

@Module
public interface  SplashModule {

    @ActivityScoped
    @Binds
     Activity SplashActivity(SplashActivity splashActivity);

    @ActivityScoped
    @Binds
     Activity ProvideNotificationActivity(NotificationHandler splashActivity);

    @ActivityScoped
    @Binds
     SplashContract.Presenter presenter(SplashPresenter presenter);

    @ActivityScoped
    @Binds
     SplashContract.View splashView(SplashActivity splashActivity);
}

