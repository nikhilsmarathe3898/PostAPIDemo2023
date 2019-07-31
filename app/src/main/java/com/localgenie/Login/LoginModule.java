package com.localgenie.Login;


import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;


/**
 * @author Pramod
 * @since 11/12/17.

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link LoginPresenter}.
 */
@Module
public abstract class LoginModule {


    @ActivityScoped
    @Binds
    abstract LoginPresenter loginPresenter(LoginPresenterImpl presenter);

    @ActivityScoped
    @Binds
    abstract LoginView loginView(LoginActivity loginActivity);

}
