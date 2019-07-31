package com.localgenie.Dagger2;


import com.localgenie.IntroActivity.IntroActivity;
import com.localgenie.IntroActivity.IntroModule;
import com.localgenie.Login.LoginActivity;
import com.localgenie.Login.LoginModule;
import com.localgenie.SplashScreen.SplashActivity;
import com.localgenie.SplashScreen.SplashModule;
import com.localgenie.addTocart.AddToCart;
import com.localgenie.addTocart.AddToCartModule;
import com.localgenie.add_address.AddAddressActivity;
import com.localgenie.add_address.AddAddressModule;
import com.localgenie.biddingFlow.BiddingModule;
import com.localgenie.biddingFlow.BiddingQuestions;
import com.localgenie.bookingtype.BookingType;
import com.localgenie.bookingtype.BookingTypeDaggerModule;
import com.localgenie.cancelledBooking.CancelledBooking;
import com.localgenie.cancelledBooking.CancelledBookingModule;
import com.localgenie.change_email.ChangeEmailActivity;
import com.localgenie.change_email.ChangeEmailModule;
import com.localgenie.changepassword.ChangePwdActivity;
import com.localgenie.changepassword.ChangePwdModule;
import com.localgenie.chatting.ChattingActivity;
import com.localgenie.chatting.ChattingModule;
import com.localgenie.confirmbookactivity.ConfirmBookActivity;
import com.localgenie.confirmbookactivity.ConfirmBookingModule;
import com.localgenie.faq.FaqActivity;
import com.localgenie.faq.FaqModule;
import com.localgenie.favouriteProvider.FavouriteProvider;
import com.localgenie.favouriteProvider.FavouriteProviderModule;
import com.localgenie.filter.FilterActivity;
import com.localgenie.filter.FilterDaggerModule;
import com.localgenie.filter.FilterDaggerResponseModule;
import com.localgenie.forgotpassword.ForgotPwdActivity;
import com.localgenie.forgotpassword.ForgotPwdModule;
import com.localgenie.home.MainActivity;
import com.localgenie.home.MainActivityDaggerModule;
import com.localgenie.inCallOutCall.TimeSlots;
import com.localgenie.inCallOutCall.TimeSlotsModule;
import com.localgenie.invoice.InvoiceActivity;
import com.localgenie.invoice.InvoiceDaggerModule;
import com.localgenie.jobDetailsStatus.JobDetailsActivity;
import com.localgenie.jobDetailsStatus.JobDetailsModules;
import com.localgenie.otp.OtpActivity;
import com.localgenie.otp.OtpModule;
import com.localgenie.payment_details.PaymentDetailActivity;
import com.localgenie.payment_details.PaymentDetailsModule;
import com.localgenie.payment_edit_card.CardEditActivity;
import com.localgenie.payment_edit_card.CardEditModule;
import com.localgenie.payment_method.PaymentMethodActivity;
import com.localgenie.payment_method.PaymentMethodModule;
import com.localgenie.profile.ProfileActivity;
import com.localgenie.profile.ProfileModule;
import com.localgenie.promocode.PromoCodeActivity;
import com.localgenie.promocode.PromoCodeDaggerModule;
import com.localgenie.providerList.ProviderList;
import com.localgenie.providerList.ProviderListDaggerModule;
import com.localgenie.providerdetails.ProviderDetails;
import com.localgenie.providerdetails.ProviderDetailsModules;
import com.localgenie.rateYourBooking.RateYourBooking;
import com.localgenie.rateYourBooking.RateYourBookingModule;
import com.localgenie.selectPaymentMethod.SelectDaggerModule;
import com.localgenie.selectPaymentMethod.SelectPayment;
import com.localgenie.share.ShareActivity;
import com.localgenie.share.ShareScreenModule;
import com.localgenie.signup.SignUpActivity;
import com.localgenie.signup.SignUpModule;
import com.localgenie.videocalling.AudioCallModule;
import com.localgenie.videocalling.AudioCallService;
import com.localgenie.videocalling.IncomingCallDaggerModule;
import com.localgenie.videocalling.IncomingCallScreen;

import com.localgenie.videocalling.VideoCallModule;
import com.localgenie.videocalling.VideoCallService;
import com.localgenie.wallet.WalletActivity;
import com.localgenie.wallet.WalletActivityDaggerModule;
import com.localgenie.walletTransaction.WalletTransActivity;
import com.localgenie.walletTransaction.WalletTransactionActivityDaggerModule;
import com.localgenie.youraddress.YourAddressActivity;
import com.localgenie.youraddress.YourAddressModule;
import com.localgenie.zendesk.zendeskHelpIndex.ZendeskAdapterModule;
import com.localgenie.zendesk.zendeskHelpIndex.ZendeskHelpIndex;
import com.localgenie.zendesk.zendeskHelpIndex.ZendeskModule;
import com.localgenie.zendesk.zendeskTicketDetails.HelpIndexAdapterModule;
import com.localgenie.zendesk.zendeskTicketDetails.HelpIndexTicketDetails;
import com.localgenie.zendesk.zendeskTicketDetails.HelpTicketDetailsModule;
import com.utility.NotificationHandler;
import com.utility.OnMyService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * @author Pramod
 * @since 11/12/17.
 */

@Module
public interface ActivityBindingModule
{
    @ContributesAndroidInjector
    OnMyService service();

    @ActivityScoped
    @ContributesAndroidInjector(modules = AudioCallModule.class)
    AudioCallService audioService();

    @ActivityScoped
    @ContributesAndroidInjector(modules = VideoCallModule.class)
    VideoCallService videoService();

    @ActivityScoped
    @ContributesAndroidInjector(modules = IntroModule.class)
    IntroActivity provideIntroActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = LoginModule.class)
     LoginActivity loginActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = FaqModule.class)
    FaqActivity provideFaqActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = SplashModule.class)
     SplashActivity splashScreen();

    @ActivityScoped
    @ContributesAndroidInjector(modules = SignUpModule.class)
     SignUpActivity signUpActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = ForgotPwdModule.class)
     ForgotPwdActivity forgotPwdActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = OtpModule.class)
     OtpActivity otpActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = ChangePwdModule.class)
     ChangePwdActivity changePwdActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = MainActivityDaggerModule.class)
     MainActivity provideMainActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = BookingTypeDaggerModule.class)
    BookingType provideBookingType();

    @ActivityScoped
    @ContributesAndroidInjector(modules = ProviderListDaggerModule.class)
    ProviderList provideProviderList();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {FilterDaggerModule.class, FilterDaggerResponseModule.class})
    FilterActivity provideFilterActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = ProviderDetailsModules.class)
    ProviderDetails provideProviderDetails();

    @ActivityScoped
    @ContributesAndroidInjector(modules = ConfirmBookingModule.class)
    ConfirmBookActivity provideConfirmBookingActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = AddToCartModule.class)
    AddToCart provideAddToCart();

    @ActivityScoped
    @ContributesAndroidInjector(modules = ProfileModule.class)
     ProfileActivity profileActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = YourAddressModule.class)
     YourAddressActivity yourAddressActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = AddAddressModule.class)
     AddAddressActivity addAddressActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = CardEditModule.class)
    CardEditActivity provideCardActivity();
    @ActivityScoped
    @ContributesAndroidInjector(modules = PaymentDetailsModule.class)
    PaymentDetailActivity providePaymentDetailsActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = PaymentMethodModule.class)
    PaymentMethodActivity providePaymentMethodActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = RateYourBookingModule.class)
    RateYourBooking provideRateYourBooking();

    @ActivityScoped
    @ContributesAndroidInjector(modules = JobDetailsModules.class)
    JobDetailsActivity provideJobDetails();


    @ActivityScoped
    @ContributesAndroidInjector(modules = {ZendeskModule.class, ZendeskAdapterModule.class})
    ZendeskHelpIndex provideZendeskHelp();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {HelpTicketDetailsModule.class, HelpIndexAdapterModule.class})
    HelpIndexTicketDetails provideHelpIndexDetails();

    @ActivityScoped
    @ContributesAndroidInjector(modules = SelectDaggerModule.class)
    SelectPayment provideSelectPayment();

    @ActivityScoped
    @ContributesAndroidInjector(modules = CancelledBookingModule.class)
    CancelledBooking provideCancelledBooking();

    @ActivityScoped
    @ContributesAndroidInjector(modules = ChangeEmailModule.class)
    ChangeEmailActivity provideChangeEmail();

    @ActivityScoped
    @ContributesAndroidInjector(modules = ChattingModule.class)
    ChattingActivity provideChatting();

    @ActivityScoped
    @ContributesAndroidInjector(modules = WalletActivityDaggerModule.class)
    WalletActivity provideWalletActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = WalletTransactionActivityDaggerModule.class)
    WalletTransActivity provideWalletTransaction();

    @ActivityScoped
    @ContributesAndroidInjector(modules = PromoCodeDaggerModule.class)
    PromoCodeActivity providePromoCode();

    @ActivityScoped
    @ContributesAndroidInjector(modules = ShareScreenModule.class)
    ShareActivity provideShareScree();

     @ActivityScoped
    @ContributesAndroidInjector(modules = BiddingModule.class)
     BiddingQuestions provideBiddingQuestion();

     @ActivityScoped
    @ContributesAndroidInjector(modules = FavouriteProviderModule.class)
     FavouriteProvider provideFavouritePro();



    @ActivityScoped
    @ContributesAndroidInjector(modules = IncomingCallDaggerModule.class)
    IncomingCallScreen provideIncomingCalling();

    @ActivityScoped
    @ContributesAndroidInjector(modules = TimeSlotsModule.class)
    TimeSlots provideTimeSlots();

    @ActivityScoped
    @ContributesAndroidInjector(modules = InvoiceDaggerModule.class)
    InvoiceActivity provideInvoiceActivity();


   /* @ActivityScoped
    @ContributesAndroidInjector(modules = ActivityAudioCallModule.class)
    AudioCallActivity provideAudioCall();*/




}
