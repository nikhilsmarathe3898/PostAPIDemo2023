package com.localgenie.selectPaymentMethod;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * <h>DaggerModule</h>
 * Created by Ali on 3/9/2018.
 */

@Module
public interface SelectDaggerModule
{

    @Binds
    @ActivityScoped
    SelectedCardInfoInterface.SelectedView providerView(SelectPayment selectPayment);
    @Binds
    @ActivityScoped
    SelectedCardInfoInterface.SelectedPresenter providePresenter(SelectedPaymentTypeImpl selectedPaymentType);
}
