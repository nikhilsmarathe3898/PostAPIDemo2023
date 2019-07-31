package com.localgenie.videocalling;


import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
public interface VideoCallModule {

    @Binds
    @ActivityScoped
    VideoCallContract.View bindView(VideoCallService videoCallService);

    @Binds
    @ActivityScoped
    VideoCallContract.Presenter bindPresenter(VideoCallPresenter videoCallPresenter);
}