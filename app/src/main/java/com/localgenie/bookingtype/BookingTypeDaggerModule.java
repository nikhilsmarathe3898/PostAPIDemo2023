package com.localgenie.bookingtype;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * <h>BookingTypeDaggerModule</h>
 * Created by Ali on 2/3/2018.
 */
@Module
public interface BookingTypeDaggerModule {
    @Binds
    @ActivityScoped
    BookingTypePresenter.presenter providerBookingTypePresenter(BookingTypePresenterImpl bookingTypePresenter);

    @Binds
    @ActivityScoped
    BookingTypePresenter.viewPresenter provideView(BookingType bookingType);
}
