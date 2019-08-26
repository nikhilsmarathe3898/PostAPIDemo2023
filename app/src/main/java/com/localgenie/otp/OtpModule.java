package com.localgenie.otp;


import android.app.Activity;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;


/**
 * <h>OtpModule</h>
 * Created by Pramod on 11/12/17.
 */

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link OtpPresenter}.
 */
@Module
public abstract class OtpModule {

    @ActivityScoped
    @Binds
    abstract Activity otpActivity(OtpActivity otpActivity);

    @ActivityScoped
    @Binds
    abstract OtpPresenter otpPresenter(OtpPresenterImpl presenter);

   @ActivityScoped
   @Binds
    abstract OtpView otpView(OtpActivity otpActivity);

}
