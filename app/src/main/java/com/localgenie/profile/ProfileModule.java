package com.localgenie.profile;


import android.app.Activity;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;


/**
 * Created by Pramod on 11/12/17.
 */

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link ProfilePresenter}.
 */
@Module
public abstract class ProfileModule {

    @ActivityScoped
    @Binds
    abstract Activity profileActivity(ProfileActivity profileActivity);

    @ActivityScoped
    @Binds
    abstract ProfilePresenter profilePresenter(ProfilePresenterImpl presenter);

   @ActivityScoped
   @Binds
    abstract ProfileView profileView(ProfileActivity profileActivity);

}
