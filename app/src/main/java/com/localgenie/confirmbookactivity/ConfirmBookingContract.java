package com.localgenie.confirmbookactivity;

import android.widget.TextView;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;

import com.pojo.CartModifiedData;

/**
 * <h>ConfirmBookingContract</h>
 * Created by Ali on 2/12/2018.
 */

public interface ConfirmBookingContract
{
    interface ContractPresenter extends BasePresenter
    {
        void onLiveBookingService(int paymentType, boolean isWalletSelected, String s, String toString, String cardId, String cartId, double bookingLat, double bookingLng
        ,String address);
        void onAddSubCartModifyData(String catId, String serviceId, int action, int quantityInHr);
        void onGetCartId();

        void lastDues();

        void serverTime();

        void setAddressWithImage(TextView tvConfirmLocation, String bookingAddress);

        void callPromoCodeApi(double bookingLat, double bookingLng, int paymentType, String cartId, String trim);

        void timeMethod(TextView tvInConfirmBookingTypeDesc, long fromTime);
    }
    interface ContractView extends BaseView
    {

        void onSuccessBooking();

        void onSuccessCartId(String cartId);

        void onCartModification(CartModifiedData.DataSelected data);

        void onCartModified(String itemSelected, int i);

        void onHidePro();

        void onHourly();

        void onAlreadyCartPresent(String message, boolean b);

        void noDuesFoundLiveBooking();

        void onDuesFound(String msg, String addLine1, String formattedDate);

        void onConnectionError(String message, String cartId, String hourly);

        void CallLiveBook();

        void promoCode(double amount, String proCode);
    }
}
