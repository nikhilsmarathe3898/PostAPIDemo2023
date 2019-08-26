package com.localgenie.providerList;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * <h>ProviderListDaggerModule</h>
 * Created by Ali on 1/29/2018.
 */

@Module
public interface ProviderListDaggerModule
{
    @Binds
    @ActivityScoped
    ProviderListContract.providerPresenter providerPresenter(ProviderPresenter providerController);

    @Binds
    @ActivityScoped
    ProviderListContract.providerView providerViewPresent(ProviderList providerList);

}
