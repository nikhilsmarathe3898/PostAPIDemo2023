package com.localgenie.providerdetails.viewschedule;

import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;
import com.pojo.ProviderDetailsResponse;
import com.pojo.ReviewPojo;
import com.pojo.ScheduleMonthPojo;

import java.util.List;

/**
 * <h>ProviderDetailsContract</h>
 * Created by Ali on 2/5/2018.
 */

public interface ScheduleContract
{
    interface SchedulePresenter extends BasePresenter
    {
       void getSchedule(String sessionToken, String date, boolean isCurrentMonth,String providerId);
       void onSuccessGetSchedule(String result);
    }
    interface ScheduleView extends BaseView
    {
        void onLogout(String message);
       void showProgress();
       void hideProgress();
        void onSuccessGetSchedule(ScheduleMonthPojo scheduleMonthPojo);

    }
}
