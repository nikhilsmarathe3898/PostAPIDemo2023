package com.localgenie.home;

import android.content.Context;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;
import com.localgenie.model.CatDataArray;
import com.localgenie.model.Category;

import java.util.ArrayList;

/**
 * <h>ServiceFragContract</h>
 * Created by Ali on 1/29/2018.
 */

public interface ServiceFragContract
{
    interface ServicePresenter extends BasePresenter{
        void onGetCategory(double lat, double lng, ArrayList<LatLng> latLngs);

        void onAddress(Context mContext, double latitude, double longitude);

        void onPendingBooking();

        void onPendingInvocieBooking();


        void onTogetServerTime();

        void showStatusBar(LinearLayout llHOmeAddress, boolean b);

        void categorySuccess(String resultBody, double lat, double lng, ArrayList<LatLng> latLngs);
    }
    interface ServiceView extends BaseView{

        void onSuccess(ArrayList<CatDataArray> data, ArrayList<LatLng> latLngs);
        void onLessData();

        void onAddressTOShow(String featureName);

        void onPendingBooking(long aLong);

        void onPendingInvocieBooking(long aLong);



        void onRecommendedService(ArrayList<Category> recommendedArr);

        void onTrendingService(ArrayList<Category> trendingArr);

        void onNotOperational(String message);

        void onConnectionError(String message, boolean isPendingBooking);

        void onLocationListener(boolean isAllowed);

        void onNoRecommendedService();

        void onNoTrendingService();

        void onPositionSelected(int adapterPosition);
    }

}
