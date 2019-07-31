package com.localgenie.favouriteProvider;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;
import com.localgenie.rateYourBooking.ResponsePojo;

import java.util.ArrayList;

/**
 * Created by Ali on 7/2/2018.
 */
public interface FavouriteProviderContract
{
    interface FavouriteProvider extends BasePresenter
    {
       void onToGetFavouriteProvider();
    }
    interface FavouriteProviderView extends BaseView
    {
        void onResponseSuccess(ArrayList<ResponsePojo.FavProviderData> data);
        void onResponseEmpty();

        void onRetry(String message);
    }
}
