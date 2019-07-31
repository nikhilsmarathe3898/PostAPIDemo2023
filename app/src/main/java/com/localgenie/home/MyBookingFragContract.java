package com.localgenie.home;

import android.content.Context;

import java.util.ArrayList;

import com.pojo.AllBookingEventPojo;
import com.pojo.MyBookingDataPojo;
import com.pojo.MyBookingStatus;
import com.utility.NotificationUtils;

/**
 * <h>MyBookingFragContract</h>
 * Created by Ali on 2/12/2018.
 */

public interface MyBookingFragContract
{
    interface MyProjectPresenter extends BasePresenter
    {
        void onBookingService();

        void onMqttJobStatus(MyBookingStatus myBookingStatus, ArrayList<AllBookingEventPojo> pendingPojo, ArrayList<AllBookingEventPojo> upComingPojo, ArrayList<AllBookingEventPojo> pastPojo, Context mContext, NotificationUtils notificationUtils);

        void onContextReceived(Context mContext);

        void onUpcommingApi(long fromDate, long toDate);
    }
    interface MyProjectView extends BaseView
    {

        void onBookingResponseSuccess(MyBookingDataPojo data);
        void onNotifyAdapter(ArrayList<AllBookingEventPojo> pendingPojo, ArrayList<AllBookingEventPojo> upComingPojo, ArrayList<AllBookingEventPojo> pastPojo, boolean isFirstOrItemAdded);
        Context onContext();

        void onConnectionError(String message,boolean isGetIs);

        void onUpComingBooking(ArrayList<AllBookingEventPojo> upcoming);
    }
}
