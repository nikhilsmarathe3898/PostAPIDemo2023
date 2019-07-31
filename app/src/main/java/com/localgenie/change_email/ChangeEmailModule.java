package com.localgenie.change_email;


import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link ChangeEmailActivity}.
 */
@Module
public interface ChangeEmailModule {

    @ActivityScoped
    @Binds
     ChangeEmailPresenter changeEmailPresenter(ChangeEmailPresenterImpl presenter);

    @ActivityScoped
    @Binds
     ChangeEmailView changeEmailView(ChangeEmailActivity changeEmailActivity);

}
