package com.localgenie.cancelledBooking;

import com.localgenie.Dagger2.FragmentScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * <h>CancelledBookingModule</h>
 * Created by Ali on 3/14/2018.
 */

@Module
public interface CancelledBookingModule
{

    @FragmentScoped
    @ContributesAndroidInjector
    ReceiptFragment provideReceiptFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    HelpFragment provideHelpFragment();
}
