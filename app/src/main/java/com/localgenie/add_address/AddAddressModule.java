package com.localgenie.add_address;


import android.app.Activity;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link AddAddressPresenter}.
 */
@Module
public abstract class AddAddressModule {

    @ActivityScoped
    @Binds
    abstract Activity addAddressActivity(AddAddressActivity addAddressActivity);

    @ActivityScoped
    @Binds
    abstract AddAddressPresenter addAddressPresenter(AddAddressPresenterImpl presenter);

   @ActivityScoped
   @Binds
    abstract AddressView addressView(AddAddressActivity addAddressActivity);

}
