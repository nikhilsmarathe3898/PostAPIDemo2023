package com.localgenie.faq;


import android.app.Activity;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link FaqPresenter}.
 */
@Module
public abstract class FaqModule {

    @ActivityScoped
    @Binds
    abstract Activity faqActivity(FaqActivity faqActivity);

    @ActivityScoped
    @Binds
    abstract FaqPresenter faqPresenter(FaqPresenterImpl presenter);

   @ActivityScoped
   @Binds
    abstract FaqView faqView(FaqActivity faqActivity);

}
