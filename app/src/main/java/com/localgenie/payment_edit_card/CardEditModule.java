package com.localgenie.payment_edit_card;


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
 * {@link CardEditPresenter}.
 */
@Module
public abstract class CardEditModule {

    @ActivityScoped
    @Binds
    abstract Activity cardEditActivity(CardEditActivity cardEditActivity);

    @ActivityScoped
    @Binds
    abstract CardEditPresenter cardEditPresenter(CardEditPresenterImpl presenter);

   @ActivityScoped
   @Binds
    abstract CardEditView cardEditView(CardEditActivity cardEditActivity);

}
