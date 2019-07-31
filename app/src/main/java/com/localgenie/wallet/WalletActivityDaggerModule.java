package com.localgenie.wallet;



import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
public interface  WalletActivityDaggerModule
{
    @Binds
    @ActivityScoped
    WalletActivityContract.WalletView provideWalletView(WalletActivity activity);

    @Binds
    @ActivityScoped
     WalletActivityContract.WalletPresenter provideWalletPresenter(WalletActivityPresenter presenter);

}
