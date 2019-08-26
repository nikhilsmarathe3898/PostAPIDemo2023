package com.localgenie.walletTransaction;



import com.localgenie.Dagger2.ActivityScoped;
import com.localgenie.Dagger2.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface WalletTransactionActivityDaggerModule
{
    @Binds
    @ActivityScoped
    WalletTransactionContract.WalletTrasactionView provideWalletTransactionView(WalletTransActivity transActivity);

    @Binds
    @ActivityScoped
    WalletTransactionContract.WalletTransactionPresenter provideWalletTransPresnter(WalletTransactionActivityPresenter transactionActivityPresenter);

    @ContributesAndroidInjector
    @FragmentScoped
    WalletTransactionsFragment provideWalletListFragmentAll();

    /*@ContributesAndroidInjector
    @FragmentScoped
    WalletTransactionsFragment provideWalletListFragmentDebit();

    @ContributesAndroidInjector
    @FragmentScoped
    WalletTransactionsFragment provideWalletListFragmentCredit();*/

}