package com.localgenie.invoice;

import android.app.Activity;
import android.widget.TextView;
import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;
import com.localgenie.utilities.HandlePictureEvents;
import com.pojo.BidQuestionAnswer;
import com.pojo.BookingAccounting;
import com.pojo.ProviderDetailsBooking;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ali on 10/18/2018.
 */
public interface InvoiceModel
{
    interface InvoicePre extends BasePresenter
    {

        void getBookingDetailsById(long bId);

        void timeMethod(TextView tvDate, TextView tvTime, long bookingRequestedFor);

        void updateStatus(JSONArray jsonArray, String signatureUrl, HandlePictureEvents handlePicEvent, File signatureFile, long bId, String promoCode);

        void showPromoCodeDialog(Activity invoiceActivity, long bId);
    }
    interface InvoiceView extends BaseView
    {

        void networkUnReachable(String message, boolean isGetDetails);

        void onProviderDetails(String currencySymbol, ProviderDetailsBooking providerDetail,
                               String categoryName, long bookingRequestedFor, ArrayList<BidQuestionAnswer> questionAndAnswer);

        void onAccounting(BookingAccounting accounting);

        void alertDialogMethod(String msg);
        void viewRatingScreen();

        void callOnResultActivity(double amount, String trim);
    }
}
