package com.localgenie.payment_method;


import android.app.Activity;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;


/**
 * @author Pramod
 * @since  31-01-2018.
 */

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link PaymentMethodPresenter}.
 */
@Module
public interface PaymentMethodModule {

    @ActivityScoped
    @Binds
     Activity paymentMethodActivity(PaymentMethodActivity paymentMethodActivity);

    @ActivityScoped
    @Binds
     PaymentMethodPresenter paymentMethodPresenter(PaymentMethodPresenterImpl presenter);

   @ActivityScoped
   @Binds
     PaymentMethodView paymentMethodView(PaymentMethodActivity paymentMethodActivity);

}
