package com.localgenie.changepassword;


import android.app.Activity;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;


/**
 * Created by Pramod on 11/12/17.
 */

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link ChangePwdPresenter}.
 */
@Module
public abstract class ChangePwdModule {

    @ActivityScoped
    @Binds
    abstract Activity changePwdActivity(ChangePwdActivity changePwdActivity);

    @ActivityScoped
    @Binds
    abstract ChangePwdPresenter changePwdPresenter(ChangePwdPresenterImpl presenter);

   @ActivityScoped
   @Binds
    abstract ChangePwdView changePwdView(ChangePwdActivity changePwdActivity);

}
