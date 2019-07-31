package com.localgenie.confirmbookactivity;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * <hConfirmBookingModule
 * Created by Ali on 2/6/2018.
 */

@Module
public interface ConfirmBookingModule
{
    @Binds
    @ActivityScoped
    ConfirmBookingContract.ContractPresenter providerConfirmBooking(ConfirmBookingContractImpl confirmBookingContract);

    @Binds
    @ActivityScoped
    ConfirmBookingContract.ContractView provideConfirmBookingView(ConfirmBookActivity confirmBookActivity);
}
