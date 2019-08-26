package com.localgenie.jobDetailsStatus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.networking.ChatApiService;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.pojo.BookingDetailsPojo;
import com.pojo.ErrorHandel;
import com.pojo.PostCallResponse;
import com.utility.RefreshToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
 * <h>JobDetailsContractImpl</h>
 * Created by Ali on 2/15/2018.
 */

public class JobDetailsContractImpl implements JobDetailsContract.Presenter {
    @Inject
    SessionManagerImpl manager;
    @Inject
    LSPServices lspServices;
    @Inject
    JobDetailsContract.JobView jobView;
    @Inject
    Gson gson;
    private String TAG = JobDetailsContractImpl.class.getSimpleName();
    private String callId;

    @Inject
    public JobDetailsContractImpl() {

    }

    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void onGetBookingDetails(final long bid) {
        Log.d(TAG, "onGetBookingDetails: " + bid);
        Observable<Response<ResponseBody>> observable = lspServices.onToGetBookingDetails(manager.getAUTH()
                , Constants.selLang, bid);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        Log.d(TAG, "onNextJobDetails: " + code);
                        String response;
                        JSONObject jsonObject;
                        try {
                            switch (code) {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextJobDetailsRes: " + response);
                                    try {
                                        BookingDetailsPojo bookingDetailsPojo = gson.fromJson(response, BookingDetailsPojo.class);
                                        BookingDetailsPojo.BookingDetailsData bookDD = bookingDetailsPojo.getData();
                                        Constants.bookingModelJobDetails = bookDD.getBookingModel();
                                        Constants.bookingModel = bookDD.getAccounting().getServiceType();

                                        Constants.isNeedApproveBycustomer = bookDD.isNeedApproveBycustomer();

                                        Constants.bookingTypeNowSchedule = bookDD.getBookingModel();

                                        LatLng customerLatLng = new LatLng(bookDD.getLatitude(), bookDD.getLongitude());
                                        Constants.custLatLng = customerLatLng;
                                        LatLng proLatLng = new LatLng(bookDD.getProviderDetail().getProLocation().getLatitude()
                                                , bookDD.getProviderDetail().getProLocation().getLongitude());
                                        // jobView.onJobStatusTime(liveBookingStatusPojo.getData().getJobStatusLogs());
                                        Constants.catId = bookDD.getCart().getCategoryId();
                                        String currencySymbol = bookDD.getCurrencySymbol();
                                        double amount = bookDD.getAccounting().getAmount();
                                        jobView.setCatDesc(bookDD.getCategoryName(),
                                                bookDD.getCategoryId(), bookDD.getCallType());
                                        jobView.onProviderDtls(bookDD.getProviderDetail(), amount, currencySymbol
                                                , bookDD.getStatus(), bookDD.getReminderId());
                                        String name = bookDD.getProviderDetail().getFirstName() + " " + bookDD.getProviderDetail().getLastName();
                                        manager.setProName(name);
                                        manager.setChatProId(bookDD.getProviderDetail().getProviderId());
                                        manager.setChatBookingID(bookDD.getBookingId());
                                        jobView.onBookingSuccessInfo(bookDD.getBookingExpireTime(),
                                                bookDD.getServerTime(), customerLatLng, proLatLng, bookDD.getStatus());
                                        jobView.onBookingTimer(bookDD.getBookingTimer(), bookDD.getStatus()
                                                , bookDD.getServerTime(), bookDD.getStatusMsg()
                                                , bookDD.getBookingRequestedAt(), bookDD.getAccounting().getTotalJobTime());
                                        jobView.JobDetailsAccounting(bookDD.getJobDescription(),
                                                bookDD.getBookingType(),
                                                bookDD.getBookingRequestedFor()
                                                , bookDD.getAddLine1(), bookDD.getAccounting()
                                                , bookDD.getCart());

                                        jobView.onJobInfo(bookDD.getBookingModel(), bookDD.getAccounting(), bookDD.getCart()
                                                , bookDD.getCategoryName());

                                        jobView.onBidDispatchLog(bookDD.getBidDispatchLog(), bookDD.getStatus());
                                        jobView.onBidQuestionAnswer(bookDD.getQuestionAndAnswer(), bookDD.getBookingModel());
                                        if (bookDD.getCallType() == 1)
                                            jobView.loadFragment();
                                        jobView.onHideProgress();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    // gson
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    jobView.onHideProgress();
                                    jobView.onLogout(jsonObject.getString("message"));
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response, ErrorHandel.class);

                                    // String
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            manager.setAUTH(newToken);
                                            onGetBookingDetails(bid);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

                                            jobView.onHideProgress();
                                            jobView.onLogout(msg);
                                        }
                                    });
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onFragmentTransition(int status, FragmentManager supportFragmentManger, Fragment... mFragment) {

        FragmentTransaction fragmentTransaction = supportFragmentManger.beginTransaction();


        switch (status) {
            case 1:
            case 2:
                fragmentTransaction.replace(R.id.flBookingScreen, mFragment[4], "MessageFragment");
                break;
            case 3:
                fragmentTransaction.replace(R.id.flBookingScreen, mFragment[0], "ProHiredFrag");
                break;
            case 6:
                fragmentTransaction.replace(R.id.flBookingScreen, mFragment[1], "ProOnTHeWayFrag");
                break;
            case 7:
                fragmentTransaction.replace(R.id.flBookingScreen, mFragment[2], "ProArrivedFrag");
                break;
            case 8:
            case 9:
                fragmentTransaction.replace(R.id.flBookingScreen, mFragment[3], "JobStartedFrag");
                break;
            case 17:
                fragmentTransaction.replace(R.id.flBookingScreen, mFragment[4], "MessageFragment");
                break;

        }

        fragmentTransaction.commit();

    }

    @Override
    public void onFragmentTransitionIn(int status, FragmentManager supportFragmentManger, Fragment... mFragment) {

        FragmentTransaction fragmentTransaction = supportFragmentManger.beginTransaction();
        Log.d(TAG, "onFragmentTransitionIn: bidding test status " + status);
        switch (status) {
            case 1:
            case 2:
                fragmentTransaction.replace(R.id.flBookingScreen, mFragment[0], "MessageFragment");
                break;
            case 3:
            case 6:
            case 7:
            case 8:
            case 9:
            case 17:
                fragmentTransaction.replace(R.id.flBookingScreen, mFragment[1], "ProHiredIn");
                break;

        }

        fragmentTransaction.commit();

    }

    @Override
    public void onUiStatusChange(Context mContext, int status, View... views) {

        Log.d(TAG, "onUiStatusChange: " + status);
        switch (status) {
            case 1:
            case 2:
            case 17:
                break;
                   /* views[0].setBackgroundResource(R.drawable.ic_action_check);
                    views[1].setBackgroundResource(R.color.parrotGreen);*/
            // break;
            case 3:
                views[0].setBackgroundResource(R.color.parrotGreen);
                views[1].setBackgroundResource(R.drawable.ic_action_check);
                break;
            case 6:
                views[0].setBackgroundResource(R.color.parrotGreen);
                views[1].setBackgroundResource(R.drawable.ic_action_check);
                views[2].setBackgroundResource(R.color.parrotGreen);
                views[3].setBackgroundResource(R.color.parrotGreen);
                views[4].setBackgroundResource(R.drawable.ic_action_check);
                views[5].setBackgroundResource(R.color.parrotGreen);
                views[6].setBackgroundResource(R.color.parrotGreen);
                break;
            case 7:
                views[0].setBackgroundResource(R.color.parrotGreen);
                views[1].setBackgroundResource(R.drawable.ic_action_check);
                views[2].setBackgroundResource(R.color.parrotGreen);
                views[3].setBackgroundResource(R.color.parrotGreen);
                views[4].setBackgroundResource(R.drawable.ic_action_check);
                views[5].setBackgroundResource(R.color.parrotGreen);
                views[6].setBackgroundResource(R.color.parrotGreen);
                views[7].setBackgroundResource(R.drawable.ic_action_check);
                views[8].setBackgroundResource(R.color.parrotGreen);

                break;
            case 8:
                views[0].setBackgroundResource(R.color.parrotGreen);
                views[1].setBackgroundResource(R.drawable.ic_action_check);
                views[2].setBackgroundResource(R.color.parrotGreen);
                views[3].setBackgroundResource(R.color.parrotGreen);
                views[4].setBackgroundResource(R.drawable.ic_action_check);
                views[5].setBackgroundResource(R.color.parrotGreen);
                views[6].setBackgroundResource(R.color.parrotGreen);
                views[7].setBackgroundResource(R.drawable.ic_action_check);
                views[8].setBackgroundResource(R.color.parrotGreen);
                views[9].setBackgroundResource(R.color.parrotGreen);
                views[10].setBackgroundResource(R.drawable.ic_action_check);
                break;
            case 9:
                views[0].setBackgroundResource(R.color.parrotGreen);
                views[1].setBackgroundResource(R.drawable.ic_action_check);
                views[2].setBackgroundResource(R.color.parrotGreen);
                views[3].setBackgroundResource(R.color.parrotGreen);
                views[4].setBackgroundResource(R.drawable.ic_action_check);
                views[5].setBackgroundResource(R.color.parrotGreen);
                views[6].setBackgroundResource(R.color.parrotGreen);
                views[7].setBackgroundResource(R.drawable.ic_action_check);
                views[8].setBackgroundResource(R.color.parrotGreen);
                views[9].setBackgroundResource(R.color.parrotGreen);
                views[10].setBackgroundResource(R.drawable.ic_action_check);
                break;
        }
    }

    @Override
    public void onUiInCallCahnge(Context mContext, int status, View... views) {


        Log.d(TAG, "onUiStatusChange: " + status);
        switch (status) {
            case 1:
            case 2:
            case 17:
                break;

            case 3:
                views[0].setBackgroundResource(R.drawable.ic_action_check);
                break;
            case 6:
                views[0].setBackgroundResource(R.drawable.ic_action_check);
                views[1].setBackgroundResource(R.color.parrotGreen);
                views[2].setBackgroundResource(R.color.parrotGreen);

                break;
            case 7:
                views[0].setBackgroundResource(R.drawable.ic_action_check);
                views[1].setBackgroundResource(R.color.parrotGreen);
                views[2].setBackgroundResource(R.color.parrotGreen);


                break;
            case 8:
                views[0].setBackgroundResource(R.drawable.ic_action_check);
                views[1].setBackgroundResource(R.color.parrotGreen);
                views[2].setBackgroundResource(R.color.parrotGreen);

                break;
            case 9:
                views[0].setBackgroundResource(R.drawable.ic_action_check);
                views[1].setBackgroundResource(R.color.parrotGreen);
                views[2].setBackgroundResource(R.color.parrotGreen);
                views[3].setBackgroundResource(R.drawable.ic_action_check);
                break;
        }
    }

    @Override
    public void changeWaitSum(Context mContext, int status, LinearLayout llJobContactInfo, TextView... textViews) {

        textViews[0].setVisibility(View.VISIBLE); // call
        textViews[1].setVisibility(View.VISIBLE); // message
        textViews[2].setVisibility(View.VISIBLE); // cancel
        textViews[3].setVisibility(View.VISIBLE); // details
        switch (status) {
            case 1:
            case 2:
            case 17:
                //llJobContactInfo.setWeightSum(2);
                textViews[0].setVisibility(View.GONE);
                textViews[1].setVisibility(View.GONE);
                break;
            case 3:
            case 6:
                //llJobContactInfo.setWeightSum(4);
                break;
            case 7:
            case 8:
            case 9:
                //llJobContactInfo.setWeightSum(3);
                textViews[2].setVisibility(View.GONE);
                break;

        }
    }

    @Override
    public void changeViews(Context mContext, int status, View... views) {
        views[0].setVisibility(View.VISIBLE); // call
        views[1].setVisibility(View.VISIBLE); // message
        views[2].setVisibility(View.VISIBLE); // cancel
        // textViews[3].setVisibility(View.VISIBLE); // details
        switch (status) {
            case 1:
            case 2:
            case 17:
                // llJobContactInfo.setWeightSum(2);
                views[0].setVisibility(View.GONE);
                views[1].setVisibility(View.GONE);
                break;
            case 3:
            case 6:
                // llJobContactInfo.setWeightSum(4);
                break;
            case 7:
            case 8:
            case 9:
                //   llJobContactInfo.setWeightSum(3);
                views[2].setVisibility(View.GONE);
                //  views[2].setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    public void changeWaitSumInCall(JobDetailsActivity jobDetailsActivity, int statusCode, boolean isVisible, LinearLayout llJobContactInfo, TextView... textViews) {

        textViews[0].setVisibility(View.VISIBLE); // call
        textViews[1].setVisibility(View.VISIBLE); // message
        textViews[2].setVisibility(View.VISIBLE); // cancel
        textViews[3].setVisibility(View.VISIBLE); // details
        switch (statusCode) {
            case 1:
            case 2:
            case 17:
                if (isVisible) {
                    //llJobContactInfo.setWeightSum(4);
                    textViews[0].setVisibility(View.VISIBLE);
                } else {
                    //llJobContactInfo.setWeightSum(3);
                    textViews[0].setVisibility(View.GONE);
                }


                break;
            case 3:
            case 6:
                if (isVisible) {
                    //llJobContactInfo.setWeightSum(4);
                    textViews[0].setVisibility(View.VISIBLE);
                } else {
                    //llJobContactInfo.setWeightSum(3);
                    textViews[0].setVisibility(View.GONE);
                }

                break;
            case 7:
            case 8:
            case 9:
                if (isVisible) {
                    //llJobContactInfo.setWeightSum(4);
                    textViews[0].setVisibility(View.VISIBLE);
                } else {
                    //llJobContactInfo.setWeightSum(3);
                    textViews[0].setVisibility(View.GONE);
                }

                break;

        }
    }

    @Override
    public void changeViewsInCall(JobDetailsActivity jobDetailsActivity, int statusCode, boolean isVisible, View... views) {
        views[0].setVisibility(View.VISIBLE); // call
        views[1].setVisibility(View.VISIBLE); // message
        views[2].setVisibility(View.VISIBLE); // cancel
        // textViews[3].setVisibility(View.VISIBLE); // details
        switch (statusCode) {
            case 1:
            case 2:
            case 17:
                if (isVisible) {
                    views[2].setVisibility(View.VISIBLE);
                } else {
                    views[2].setVisibility(View.GONE);
                }


                break;
            case 3:
            case 6:
                if (isVisible) {
                    views[2].setVisibility(View.VISIBLE);
                } else {
                    views[2].setVisibility(View.GONE);
                }
                break;
            case 7:
            case 8:
            case 9:
                if (isVisible) {
                    views[2].setVisibility(View.VISIBLE);
                } else {
                    views[2].setVisibility(View.GONE);
                }
                break;

        }
    }

    @Override
    public void onPendingBiddingBooking(long bId, int status) {
        Observable<Response<ResponseBody>> observable = lspServices.acceptBidBooking(manager.getAUTH(),
                Constants.selLang, bId, status);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        String response;
                        try {

                            switch (code) {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextPendingBooking: " + response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray arry = jsonObject.getJSONArray("data");
                                    if (arry != null && jobView != null && arry.length() > 0)
                                        jobView.onPendingBiddingBooking(arry.getLong(0));

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onPendingBiddingBooking(bId, status);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            if (jobView != null)
                                                jobView.onLogout(msg);
                                        }
                                    });
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    if (jobView != null)
                                        jobView.onLogout(jsonObject.getString("message"));
                                    break;

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                        if (jobView != null) {
                            jobView.onConnectionError(e.getMessage(), true);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void showAnimationCardVied(boolean isVisible, CardView cardJobViewContactInfo) {
        float alpha;
        int height;
        if (isVisible) {
            alpha = 1.0f;
            height = 0;
        } else {
            height = cardJobViewContactInfo.getHeight();
            alpha = 0.0f;
        }

        cardJobViewContactInfo.animate()
                .translationY(height)
                .alpha(alpha)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (isVisible)
                            cardJobViewContactInfo.setVisibility(View.VISIBLE);
                        else
                            cardJobViewContactInfo.setVisibility(View.GONE);

                    }
                });

    }

    @Override
    public void callInitCallApi(String callProId, String randomString, ChatApiService chatApiService, String bookingId, String callTypeValue) {

        callId = null;
        chatApiService.initCall(manager.getCallToken(), Constants.selLang, callTypeValue, randomString, callProId, bookingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {


                    @Override
                    public void onNext(Response<ResponseBody> value) {
                        Log.d(TAG, "onNext: " + value.code());


                        if (value.code() == 200) {

                            try {

                                String response = value.body().string();
                                //  Log.d(TAG, "onNext: initCall Response" + response);
                                PostCallResponse postCallResponse = gson.fromJson(response, PostCallResponse.class);
                                callId = postCallResponse.getData().getCallId();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!TextUtils.isEmpty(callId)) {

                                if (jobView != null)
                                    jobView.launchCallsScreen(callId, randomString);
                            }


                        } else {


                            //No 200 code
                            try {

                                String response = value.errorBody().string();
                                Log.d(TAG, "onNext: initCall Response" + response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
