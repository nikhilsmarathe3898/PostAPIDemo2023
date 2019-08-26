package com.localgenie.rateYourBooking;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * <h>RateYourBookingModule</h>
 * Created by Ali on 2/15/2018.
 */
@Module
public interface RateYourBookingModule
{

    @Binds
    @ActivityScoped
    RateYourProviderContract.Presenter providePresenter(RateYourProviderImpl rateYourProvider);

    @Binds
    @ActivityScoped
    RateYourProviderContract.ViewContract provideView(RateYourBooking rateYourBooking);

}
