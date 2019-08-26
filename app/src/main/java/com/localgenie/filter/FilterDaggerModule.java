package com.localgenie.filter;


import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * <h>FilterDaggerModule</h>
 * Created by Ali on 1/31/2018.
 */

@Module
public interface FilterDaggerModule
{
    @Binds
    @ActivityScoped
    FilterContract.FilterPresent provideFilterPresenter(FilterPresenter filterPresenter);
}
