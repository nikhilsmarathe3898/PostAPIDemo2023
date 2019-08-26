package com.localgenie.signup;


import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;


/**
 * Created by Pramod on 11/12/17.
 */

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link SignUpPresenter}.
 */
@Module
public abstract class SignUpModule {

    @ActivityScoped
    @Binds
    abstract SignUpPresenter signUpPresenter(SignUpPresenterImpl presenter);

   @ActivityScoped
   @Binds
    abstract SignUpView signUpView(SignUpActivity signUpActivity);

}
