package com.localgenie.home;

import com.localgenie.Dagger2.ActivityScoped;
import com.localgenie.Dagger2.FragmentScoped;
import com.localgenie.sidescreens.SidescreensFrag;
import com.localgenie.sidescreens.SidescreensPresenter;
import com.localgenie.wallet.WalletActivityContract;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * <h>MainActivityDaggerModule</h>
 * Created by Ali on 1/24/2018.
 */

@Module
public interface MainActivityDaggerModule
{

    @Binds
    @ActivityScoped
    MainActivityContract.MainPresenter provideMainPresenter(MainActivityPresenter presenter);

    @FragmentScoped
    @ContributesAndroidInjector
    ServicesFrag provideServiceFragment();


    @FragmentScoped
    @ContributesAndroidInjector
    MyBookingsFrag provideProjectFrag();


    @FragmentScoped
    @ContributesAndroidInjector
    SidescreensFrag provideSideScreensFrag();

    @FragmentScoped
    @ContributesAndroidInjector
    ChatFragment provideChatFragment();

    @Binds
    @ActivityScoped
    ServiceFragContract.ServicePresenter provideServicePresenter(ServicesFragPresenter presenter);

   /* @Binds
    @ActivityScoped
    ServiceFragContract.ServiceView provideServiceFrag(ServicesFrag servicesFrag);*/

    @Binds
    @ActivityScoped
    MyBookingFragContract.MyProjectPresenter provideMyProjectPresenter(MyBookingPresenterImpl presenter);


    @Binds
    @ActivityScoped
    WalletActivityContract.WalletPresenterBalance providePresenterWallet(SidescreensPresenter sidescreensPresenter);

    @Binds
    @ActivityScoped
    ChattingFragPresenter.Presenter providePresenter(ChattingFragPresenterImpl chattingFragPresenter);
}
