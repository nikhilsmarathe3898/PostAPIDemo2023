package com.localgenie.youraddress;


import android.app.Activity;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;


/**
 * Created by Pramod on 11/12/17.
 */

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link YourAddressPresenter}.
 */
@Module
public abstract class YourAddressModule {

    @ActivityScoped
    @Binds
    abstract Activity yourAddressActivity(YourAddressActivity yourAddressActivity);

    @ActivityScoped
    @Binds
    abstract YourAddressPresenter yourAddressPresenter(YourAddressPresenterImpl presenter);

   @ActivityScoped
   @Binds
    abstract YourAddressView yourAddressView(YourAddressActivity yourAddressActivity);

}
