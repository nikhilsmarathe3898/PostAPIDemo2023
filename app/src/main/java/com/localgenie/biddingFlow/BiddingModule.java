package com.localgenie.biddingFlow;

import com.localgenie.Dagger2.ActivityScoped;
import com.localgenie.Dagger2.FragmentScoped;
import com.localgenie.selectPaymentMethod.SelectedCardInfoInterface;
import com.localgenie.youraddress.YourAddressPresenter;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Ali on 6/9/2018.
 */
@Module
public interface BiddingModule
{
    @FragmentScoped
    @ContributesAndroidInjector
    MyBiddingQuestionFrag provideBiddingFrag();

    @FragmentScoped
    @ContributesAndroidInjector
    MyBiddingBookTypeFrag provideBiddingBookFrag();

    @Binds
    @ActivityScoped
    BiddingContractor.BiddingContractPresent providePresenter(BiddingContractImpl biddingContract);

    @Binds
    @ActivityScoped
    BiddingContractor.BiddingContractView provideView(BiddingQuestions biddingQuestions);
    @Binds
    @ActivityScoped
    YourAddressPresenter provideYourAddressPre(BiddingContractImpl biddingQuestions);

    @Binds
    @ActivityScoped
    SelectedCardInfoInterface.SelectedPresenter provideServicePresenter(MyBiddingFragmentContractImpl presenter);

}
