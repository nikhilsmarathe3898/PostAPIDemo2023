package com.localgenie.payment_details;


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
 * {@link PaymentDetailPresenter}.
 */
@Module
public interface  PaymentDetailsModule {

    @ActivityScoped
    @Binds
     Activity paymentMethodActivity(PaymentDetailActivity paymentMethodActivity);

    @ActivityScoped
    @Binds
     PaymentDetailPresenter paymentMethodPresenter(PaymentDetailPresenterImpl presenter);

   @ActivityScoped
   @Binds
     PaymentDetailView paymentMethodView(PaymentDetailActivity paymentMethodActivity);

}
