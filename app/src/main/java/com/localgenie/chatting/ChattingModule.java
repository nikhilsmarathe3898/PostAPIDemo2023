package com.localgenie.chatting;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Ali on 4/18/2018.
 */
@Module
public interface ChattingModule
{
    @Binds
    @ActivityScoped
    ChattingPresenter.Presenter providePresenter(ChattingPresenterImpl chattingPresenter);

    @Binds
    @ActivityScoped
    ChattingPresenter.ViewChatting provideViews(ChattingActivity chattingActivity);
}
