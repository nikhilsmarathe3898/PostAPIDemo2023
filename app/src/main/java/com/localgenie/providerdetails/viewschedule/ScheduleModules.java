package com.localgenie.providerdetails.viewschedule;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * <h>ProviderDetailsModules</h>
 * Created by Ali on 2/5/2018.
 */

@Module
public interface ScheduleModules
{

    @Binds
    @ActivityScoped
    ScheduleContract.SchedulePresenter schedulePresenter(SchedulePresenterImpl schedulePresenter);

    @Binds
    @ActivityScoped
    ScheduleContract.ScheduleView scheduleView(ScheduleActivity scheduleFragment);


}
