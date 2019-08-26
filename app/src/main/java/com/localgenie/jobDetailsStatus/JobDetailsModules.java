package com.localgenie.jobDetailsStatus;

import com.localgenie.Dagger2.ActivityScoped;
import com.localgenie.Dagger2.FragmentScoped;
import com.localgenie.lspapplication.MessagesFragment;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * <h>JobDetailsModules</h>
 * Created by Ali on 2/15/2018.
 */
@Module
public interface JobDetailsModules
{

    @FragmentScoped
    @ContributesAndroidInjector
    JobPostedFrag provideJobPosted();

    @FragmentScoped
    @ContributesAndroidInjector
    ProviderHiredFragment provideHiredFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    ProviderHiredFragmentIn provideHiredFragmentIn();


    @FragmentScoped
    @ContributesAndroidInjector
    MessagesFragment provideMessageFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    ProviderOnTheWayFrag provideOnTheWayFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    ProviderArrivedFrag provideArrivedFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    JobStartedFrag provideJobStartedFragment();

    @Binds
    @ActivityScoped
    JobDetailsContract.Presenter providePresenter(JobDetailsContractImpl jobDetailsContract);

    @Binds
    @ActivityScoped
    JobDetailsContract.JobView provideView(JobDetailsActivity jobDetailsActivity);

    @Binds
    @ActivityScoped
    JobDetailsOnTheWayContract.JobDetailsOnTheWayPresenter provideServicePresenter(JobDetailsOnTheWayContractImpl presenter);

    @Binds
    @ActivityScoped
    JobDetailsOnTheWayContract.CancelBooking provideCancelBooking(CancelBookingImpl cancelBooking);
}
