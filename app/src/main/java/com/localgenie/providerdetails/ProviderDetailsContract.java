package com.localgenie.providerdetails;

import android.widget.TextView;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;

import com.pojo.ProviderDetailsResponse;
import com.pojo.ReviewPojo;

/**
 * <h>ProviderDetailsContract</h>
 * Created by Ali on 2/5/2018.
 */

public interface ProviderDetailsContract
{
    interface ProviderPresenter extends BasePresenter
    {
       void onProviderDetailService(String proId);

        void moreReadable(TextView tvProAbout);

        void callReviewApi(int pageCount, String proId);

        void hireProvider(long bid, String proId);

    }
    interface ProviderView extends BaseView
    {
        void onSuccess(ProviderDetailsResponse.ProviderResponseDetails data);

        void onReviewSuccess(ReviewPojo.SignUpDataSid data);

        void onErrorNotConnected(String message);

        void onBookingHired();

    }
}
