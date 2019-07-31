package com.localgenie.videocalling;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Ali on 10/29/2018.
 */
@Module
public interface IncomingCallDaggerModule
{
    @ActivityScoped
    @Binds
    IncomingCallPresenterImpl.IncomingCallPresenters InComingPresenters(InComingCallPresenter presenter);

    @ActivityScoped
    @Binds
    IncomingCallPresenterImpl.IncomingCallView InComingView(IncomingCallScreen presenter);
}
