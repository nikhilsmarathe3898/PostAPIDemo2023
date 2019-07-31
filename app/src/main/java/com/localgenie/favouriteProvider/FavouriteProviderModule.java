package com.localgenie.favouriteProvider;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Ali on 7/2/2018.
 */
@Module
public interface FavouriteProviderModule
{
    @Binds
    @ActivityScoped
    FavouriteProviderContract.FavouriteProvider provideContractor(FavouriteProviderContractImpl favouriteProviderContract);

    @Binds
    @ActivityScoped
    FavouriteProviderContract.FavouriteProviderView provideView(FavouriteProvider favouriteProvider);
}
