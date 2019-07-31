package com.localgenie.biddingFlow;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;
import com.localgenie.model.youraddress.YourAddrData;
import com.localgenie.utilities.HandlePictureEvents;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ali on 6/12/2018.
 */
public interface BiddingContractor
{
    interface BiddingContractPresent extends BasePresenter
    {
        void onLiveBookingCalled(int paymentType, LatLng latLng, String cardId, String catId, boolean isWalletSelected, String bidPrice, String promoCode, JSONArray jsonArray, long visitingTime, String bidAddress);

        void callPromoValidation(String code, String catId, double bidLatitude, double bidLongitude, int paymentType, Context mContext);
        /**
         * <h2>checkPermission</h2>
         * <p>checking for the permission for camera and file storage at run time for
         * build version more than 22
         */
        void checkPermission(Context mContext, Activity mActivity, HandlePictureEvents handlePicEvent);

        void selectImage(HandlePictureEvents handlePicEvent);

        long getStartOfDayInMillisToday();

        void onNowLaterSelected(boolean isNow, long selectedScheduledDateTime, int selectedDuration);

        void onRepeatSelected(long selectedScheduledDateTime, long selectedEndDate, int selectedDuration, ArrayList<String> repeatBooking);
        void onLastDues();
    }
    interface BiddingContractView extends BaseView
    {
        void onAnswerSelected(String answer);

        void onConnectionError(String message, String liveBooking, String s);

        void onSuccessBooking();

        void addItems(ArrayList<YourAddrData> yourAddrDataList);

        void setNoAddressAvailable();

        void setError(String message);

        void onAddressSelected(int adapterPosition);

        void refreshItems(YourAddrData rowItem, int adapterPosition);
        void onShowProgressPromo();
        void onHideProgressPromo();

        void onPromoCodeError(String message);

        void onPromoCodeSuccess(double amount, String codes);
        void onSessionExpired(String errorMsg);

        void onToTakeImage(int adapterPosition);

        void deletePhoto(int adapterPosition, int imagePostion);

        void noDuesFoundLiveBooking();

        void onDuesFound(String msg, String addLine1, String formattedDate);

    }
    interface BiddingDateTIme
    {
        void onDateTimeSel(Date uri, boolean isSchedule);
    }
}
