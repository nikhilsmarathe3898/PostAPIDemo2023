package com.localgenie.forgotpassword;


import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;


/**
 * @author Pramod
 * @since 11-12-2017.
 *
 *
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link ForgotPwdPresenter}.
 */
@Module
public abstract class ForgotPwdModule {

    @ActivityScoped
    @Binds
    abstract ForgotPwdPresenter forgotPwdPresenter(ForgotPwdPresenterImpl presenter);

   @ActivityScoped
   @Binds
    abstract ForgotPwdView forgotPwdView(ForgotPwdActivity forgotPwdActivity);

}
