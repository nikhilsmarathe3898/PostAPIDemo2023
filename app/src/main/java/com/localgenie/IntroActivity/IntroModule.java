package com.localgenie.IntroActivity;


import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;


/**
 * @author Pramod
 * @since 11/12/17.

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link IntroActivityContract.IntroPresenter}.
 */
@Module
public abstract class IntroModule {


    @ActivityScoped
    @Binds
    abstract IntroActivityContract.IntroPresenter introPresenter(IntroActivityPresenter presenter);

    @ActivityScoped
    @Binds
    abstract IntroActivityContract.IntroView introView(IntroActivity introActivity);

}
