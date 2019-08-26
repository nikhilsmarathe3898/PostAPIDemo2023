package com.localgenie.addTocart;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * <h>AddToCartModule</h>
 * Created by Ali on 2/7/2018.
 */

@Module
public interface AddToCartModule
{
    @Binds
    @ActivityScoped
    AddToCartContractor.presenter provideContract(AddToCartPresenterImpl addToCartPresenter);

    @Binds
    @ActivityScoped
    AddToCartContractor.ContractView provideContractView(AddToCart addToCart);
}
