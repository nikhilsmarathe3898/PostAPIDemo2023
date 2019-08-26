package com.localgenie.providerdetails;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * <h>ProviderDetailsModules</h>
 * Created by Ali on 2/5/2018.
 */

@Module
public interface ProviderDetailsModules
{

    @Binds
    @ActivityScoped
    ProviderDetailsContract.ProviderPresenter providePresenter(ProviderPresenterImpl providerPresenter);

    @Binds
    @ActivityScoped
    ProviderDetailsContract.ProviderView providerDetailsView(ProviderDetails providerDetails);


}
