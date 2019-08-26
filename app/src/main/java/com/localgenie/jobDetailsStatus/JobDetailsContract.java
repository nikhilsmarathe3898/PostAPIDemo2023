package com.localgenie.jobDetailsStatus;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;

import com.localgenie.networking.ChatApiService;
import com.pojo.BidDispatchLog;
import com.pojo.BidQuestionAnswer;
import com.pojo.BookingAccounting;
import com.pojo.BookingTimer;
import com.pojo.CartInfo;
import com.pojo.ProviderDetailsBooking;

import java.util.ArrayList;

/**
 * <h>JobDetailsContract</h>
 * Created by Ali on 2/15/2018.
 */

public interface JobDetailsContract
{
    interface Presenter extends BasePresenter
    {
        void onFragmentTransition(int status, FragmentManager supportFragmentManger, Fragment... mFragment);
        void onFragmentTransitionIn(int status, FragmentManager supportFragmentManger, Fragment... mFragment);
        void onUiStatusChange(Context mContext, int status, View... views);
        void onUiInCallCahnge(Context mContext, int status, View... views);
        void changeWaitSum(Context mContext, int status, LinearLayout llJobContactInfo, TextView... textViews);
        void changeViews(Context mContext, int status, View... views);
        void onGetBookingDetails(long bid);

        void showAnimationCardVied(boolean isVisible, CardView cardJobViewContactInfo);

        void onPendingBiddingBooking(long bId,int status);

        void changeWaitSumInCall(JobDetailsActivity jobDetailsActivity, int statusCode,boolean isVisible, LinearLayout llJobContactInfo, TextView... textViews);

        void changeViewsInCall(JobDetailsActivity jobDetailsActivity, int statusCode,boolean isVisible, View... views);

        void callInitCallApi(String callProId, String randomString, ChatApiService chatApiService, String bookingId, String callTypeValue);
    }
    interface JobView extends BaseView
    {
        void onSuccessBooking();
        void onPendingBiddingBooking(long aLong);
        void onConnectionError(String message, boolean isPendingBooking);

        //proExpiry
        void onBookingTimer(BookingTimer bookingTimer, int status, long serverTime, String statusMsg, long bookingRequestedAt, long totalJobTime);

        void onProviderDtls(ProviderDetailsBooking providerDetail, double amount, String currencySymbol, int status, String reminderId);

        void onBookingSuccessInfo(long bookingExpireTime, long serverTime, LatLng customerLatLng, LatLng proLatLng, int status);

        void JobDetailsAccounting(String jobDescription, int bookingType, long bookingRequestedFor,
                                  String addLine1, BookingAccounting accounting, CartInfo cart);

        void onBidQuestionAnswer(ArrayList<BidQuestionAnswer> questionAndAnswer, int status);

        void onBidDispatchLog(ArrayList<BidDispatchLog> bidDispatchLog, int status);

        void setCatDesc(String categoryName, String categoryId, int callType);

        void onJobInfo(int bookingType, BookingAccounting accounting, CartInfo cart, String categoryName);

        void loadFragment();

        void launchCallsScreen(String callId, String randomString);
    }
}
