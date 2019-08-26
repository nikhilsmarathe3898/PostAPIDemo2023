package com.localgenie.providerdetails.viewschedule;


import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.gson.Gson;
import com.localgenie.model.ProfileData;
import com.localgenie.model.ProfileResponse;
import com.localgenie.model.faq.FAQResponse;
import com.localgenie.model.faq.FaqData;
import com.localgenie.networking.LSPServices;
import com.localgenie.providerdetails.ProviderDetailsContract;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.ReadMoreSpannable;
import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.pojo.Booked;
import com.pojo.ErrorHandel;
import com.pojo.ProviderDetailsResponse;
import com.pojo.ReviewPojo;
import com.pojo.Schedule;
import com.pojo.ScheduleMonthPojo;
import com.pojo.Slot;
import com.utility.RefreshToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * <h>ProviderPresenterImpl</h>
 * Created by Ali on 2/5/2018.
 */

public class SchedulePresenterImpl implements ScheduleContract.SchedulePresenter
{

    @Inject
    ScheduleContract.ScheduleView scheduleView;

   /* @Inject
    ScheduleContract.SchedulePresenter presenterImple;
*/
    @Inject
    SessionManagerImpl sessionManager;
    @Inject
    Gson gson;

    @Inject
    LSPServices lspServices;

    private SimpleDateFormat serverFormat,displayHourFormat, displayHourFormatInBooked, displayPeriodFormat;

    private boolean isFragmentAttached = false, isCurrentMonth = false;
    private String scheduleData="";

    @Inject
    SchedulePresenterImpl()
    {
       // this.scheduleView = new ScheduleFramentModel(this);
        isFragmentAttached = true;

        displayHourFormat = new SimpleDateFormat("h:mm", Locale.US);
        displayHourFormatInBooked = new SimpleDateFormat("hh:mm a", Locale.US);
        displayPeriodFormat = new SimpleDateFormat("a", Locale.US);
        serverFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US);
    }


    @Override
    public void getSchedule(String auth, String date, boolean isCurrentMonth,String providerId) {

        this.isCurrentMonth = isCurrentMonth;
        if(!isCurrentMonth || sessionManager.getScheduleData().equals(""))
        {
            scheduleView.showProgress();
        }
        else
        {
            onSuccessGetSchedule(sessionManager.getScheduleData());
        }
       // model.getShedule(sessionToken,date);

        Observable<Response<ResponseBody>> request = lspServices.getSchdeuleSlots(auth,Constants.selLang,date,providerId);

        request.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                       // compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ResponseBody> value) {
                        JSONObject errJsonD;
                        Log.e("SIDE_PROF", "Reqq URL :: " + value.raw().request().url());
                        Log.e("SIDE_PROF", "code :: " + value.code() + " msg " + value.message());
                        try
                        {
                            switch (value.code())
                            {
                                case Constants.SUCCESS_RESPONSE:
                               /* if(){
                                        //profileView.onLogout(email);
                                    }else*/
                                    String responseBody = value.body().string();
                                        scheduleView.hideProgress();
                                        onSuccessGetSchedule(responseBody);
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    errJsonD = new JSONObject(value.errorBody().string());

                                    RefreshToken.onRefreshToken(errJsonD.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            sessionManager.setAUTH(newToken);
                                            getSchedule(auth,date,isCurrentMonth,providerId);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            scheduleView.onLogout(msg);
                                            scheduleView.hideProgress();
                                        }
                                    });
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    scheduleView.hideProgress();
                                    errJsonD = new JSONObject(value.errorBody().string());
                                    scheduleView.onError(errJsonD.getString("message"));
                                    break;
                                default:
                                    errJsonD = new JSONObject(value.errorBody().string());
                                    scheduleView.hideProgress();
                                    scheduleView.onError(errJsonD.getString("message"));
                                    break;
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        scheduleView.hideProgress();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        scheduleView.hideProgress();
                    }
                });
    }

    @Override
    public void onSuccessGetSchedule(String result) {
        if(isFragmentAttached)
        {
            scheduleView.hideProgress();
        }
        if(scheduleData.equals(result))
        {
            return;
        }
        scheduleData = result;
        if(isCurrentMonth)
        {
            sessionManager.setScheduleData(result);
        }
        ScheduleMonthPojo scheduleMonthPojo = gson.fromJson(result, ScheduleMonthPojo.class);
        if(isFragmentAttached)
        {
            scheduleView.onSuccessGetSchedule(scheduleMonthPojo);
        }
    }


    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {
        isFragmentAttached = false;

    }

}
