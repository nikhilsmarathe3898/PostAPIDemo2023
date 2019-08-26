package com.localgenie.rateYourBooking;

import android.content.Context;
import android.widget.TextView;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;

import java.util.ArrayList;

import com.localgenie.utilities.AppTypeface;
import com.pojo.AdditionalService;
import com.pojo.BookingAccounting;
import com.pojo.CartInfo;
import com.pojo.InvoiceDetails;

/**
 * <h>RateYourProviderContract</h>
 * Created by Ali on 2/22/2018.
 */

public interface RateYourProviderContract
{
    interface Presenter extends BasePresenter
    {
        void onInvoiceDetailsCalled(long bId);

        void onUpdateReview(long bId, ArrayList<InvoiceDetails.CustomerRating> stringList, String reviewMsg);

        void timeMethod(TextView tbServiceAvailable, long bookingRequestedFor);

        void getStringList();

        void openDialog(RateYourBooking rateYourBooking,int calltype, AppTypeface appTypeface, CartInfo cartInfo, String signURL, BookingAccounting accounting, String addressLine, ArrayList<AdditionalService> additionalServices, int bookingModel, String currencySymbol, String categoryName, ArrayList<String> pickuplist, ArrayList<String> dropImageList, Context context);

        void onAddToFav(String providerId, String catId);

        void removeFromFav(String providerId, String catId);
    }
    interface ViewContract extends BaseView
    {

        void onGetInvoiceDetails(InvoiceDetails.InvoiceData data);

        void onRateProviderSuccess();

        void onGetStarList(ArrayList<String> stringList);

        void onFavAdded(String message);

        void removeFromFav(String message);
    }
}
