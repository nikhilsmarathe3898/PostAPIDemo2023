package com.localgenie.videocalling;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Ali on 3/20/2019.
 */
@Module
public interface AudioCallModule
{
    @Binds
    @ActivityScoped
    VideoCallContract.View bindView(AudioCallService videoCallService);

    @Binds
    @ActivityScoped
    VideoCallContract.Presenter bindPresenter(AudioCallPresenter videoCallPresenter);
}
