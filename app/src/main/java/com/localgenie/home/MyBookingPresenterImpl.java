package com.localgenie.home;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.localgenie.jobDetailsStatus.JobDetailsActivity;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.utility.AlertProgress;
import com.utility.CalendarEventHelper;
import com.utility.NotificationUtils;
import com.utility.RefreshToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import com.pojo.AllBookingEventPojo;
import com.pojo.ErrorHandel;
import com.pojo.MyBookingPojo;
import com.pojo.MyBookingStatus;
import retrofit2.Response;

/**
 * <h>MyBookingPresenterImpl</h>
 * Created by Ali on 2/12/2018.
 */

public class MyBookingPresenterImpl implements MyBookingFragContract.MyProjectPresenter
{

    private String TAG = MyBookingPresenterImpl.class.getSimpleName();
    @Inject
    SessionManagerImpl manager;
    @Inject
    LSPServices lspServices;
    @Inject
    AlertProgress alertProgress;
    @Inject
    Gson gson;
    private MyBookingFragContract.MyProjectView myProjectView;
    private boolean isPopUpShowing = true;
    private Context mContext;
    @Inject
    public MyBookingPresenterImpl()
    {

    }
    @Override
    public void attachView(Object view) {
        myProjectView = (MyBookingFragContract.MyProjectView) view;
    }

    @Override
    public void detachView() {
        //   myProjectView = null;
    }

    @Override
    public void onBookingService()
    {
        Observable<Response<ResponseBody>> observable = lspServices.onToGetAllBookings(manager.getAUTH()
                ,Constants.selLang);
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
                        JSONObject jsonObject;
                        try
                        {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextBOOKINGIMPL: "+response);
                                    Constants.isLoggedIn = true;
                                    MyBookingPojo resp = gson.fromJson(response,MyBookingPojo.class);
                                    myProjectView.onBookingResponseSuccess(resp.getData());
                                    myProjectView.onHideProgress();
                                    if(resp.getData().getUpcoming().size()>0)
                                    {
                                        for(int i = 0; i<resp.getData().getUpcoming().size();i++)
                                        {
                                            if(resp.getData().getUpcoming().get(i).getBookingType()==3)
                                            {
                                                if(manager.getBookingStatus(resp.getData().getUpcoming().get(i).getBookingId())<resp.getData().getUpcoming().get(i).getStatus()) {
                                                    manager.setBookingStatus(resp.getData().getUpcoming().get(i).getBookingId(), resp.getData().getUpcoming().get(i).getStatus());
                                                }
                                            }
                                        }
                                    }

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response,ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onBookingService();
                                        }


                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            myProjectView.onHideProgress();
                                            myProjectView.onLogout(msg);
                                        }
                                    });
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    myProjectView.onHideProgress();
                                    myProjectView.onLogout(jsonObject.getString("message"));
                                    break;
                                    default:
                                        response = responseBodyResponse.errorBody().string();
                                        jsonObject = new JSONObject(response);
                                        myProjectView.onHideProgress();
                                        myProjectView.onError(jsonObject.getString("message"));
                                         break;
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        myProjectView.onHideProgress();
                        myProjectView.onConnectionError(e.getMessage(),true);

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onUpcommingApi(long fromDate, long toDate) {
        long fDate = fromDate/1000;
        long toDates = toDate/1000;
        Log.d(TAG, "onUpcommingApi: "+fDate+" toDate "+toDates);
        Observable<Response<ResponseBody>> observable = lspServices.onToGetAllBookingsUpComing(manager.getAUTH()
                ,Constants.selLang,fDate,toDates);
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
                        JSONObject jsonObject;
                        try
                        {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    assert responseBodyResponse.body() != null;
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextBOOKINGIMPLUP: "+response);
                                    MyBookingPojo resp = gson.fromJson(response,MyBookingPojo.class);
                                  //  myProjectView.onBookingResponseSuccess(resp.getData());
                                    myProjectView.onHideProgress();

                                       myProjectView.onUpComingBooking(resp.getData().getUpcoming());


                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response,ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onBookingService();
                                        }


                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            myProjectView.onHideProgress();
                                            myProjectView.onLogout(msg);
                                        }
                                    });
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    myProjectView.onHideProgress();
                                    myProjectView.onLogout(jsonObject.getString("message"));
                                    break;
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        myProjectView.onHideProgress();
                        myProjectView.onConnectionError(e.getMessage(),false);

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onMqttJobStatus(MyBookingStatus myBookingStatus, final ArrayList<AllBookingEventPojo> pendingPojo, final ArrayList<AllBookingEventPojo> upComingPojo, final ArrayList<AllBookingEventPojo> pastPojo, Context mContext, NotificationUtils notificationUtils)
    {

        Log.d("TAG", "onNext: " + myBookingStatus.getData().getStatus());
        int dataIndex = myBookingStatus.getData().getStatus();

        CalendarEventHelper calendarEventHelper = new CalendarEventHelper(mContext);
        // int index;
        final MyBookingStatus myEvent = myBookingStatus;
        Log.d("TAG", "onNextisPopUpShowing: "+isPopUpShowing+" isOpen "+Constants.isJobDetailsOpen);
        switch (dataIndex)
        {
            case 17:
                for (int i = 0; i < pendingPojo.size(); i++) {
                    if (myEvent.getData().getBookingId() == pendingPojo.get(i).getBookingId()) {
                        //  index = i;

                        pendingPojo.get(i).setBidDispatchLog(myEvent.getData().getBidProvider());
                        pendingPojo.get(i).setStatus(myEvent.getData().getStatus());
                        pendingPojo.get(i).setStatusMsg(myEvent.getData().getStatusMsg());

                        pendingPojo.get(i).setBookingId(myEvent.getData().getBookingId());

                        myProjectView.onNotifyAdapter(pendingPojo,upComingPojo,pastPojo, true);
                        isPopUpShowing = true;
                        break;
                    }
                }
                break;
            case 3://booking accepted

                Log.d("TAG", "onNextBookingStatusCancel3: " + myBookingStatus.getData().getStatus()
                        +" bookingId3 "+myBookingStatus.getData().getBookingId());
                if(!Constants.isJobDetailsOpen)
                {

                    if(manager.getBookingStatus(myEvent.getData().getBookingId())<myEvent.getData().getStatus()){
                        manager.setBookingStatus(myEvent.getData().getBookingId(),myEvent.getData().getStatus());
                        Intent intent = new Intent(mContext,JobDetailsActivity.class);
                        intent.putExtra("BID", myEvent.getData().getBookingId());
                        intent.putExtra("STATUS", myEvent.getData().getStatus());
                        intent.putExtra("ImageUrl",myEvent.getData().getProProfilePic());
                        intent.putExtra("CallType",myEvent.getData().getCallType());
                        intent.putExtra("BookingModel",myEvent.getData().getBookingModel());
                        notificationUtils.showJustNotification
                                ("LIVESTATUS", myEvent.getData().getStatusMsg(),myEvent.getData().getMsg(), intent);
                    }
                 /*   if(myEvent.getData().getBookingType()==2)
                        checkAndAddEvent(mContext,myEvent.getData().getBookingId(),myEvent.getData().getBookingRequestedFor(),manager);*/

                }
                for (int i = 0; i < pendingPojo.size(); i++) {
                    if (myEvent.getData().getBookingId() == pendingPojo.get(i).getBookingId()) {
                        //  index = i;
                        pendingPojo.get(i).setStatus(myEvent.getData().getStatus());
                        pendingPojo.get(i).setStatusMsg(myEvent.getData().getStatusMsg());
                        pendingPojo.get(i).setFirstName(myEvent.getData().getFirstName());
                        pendingPojo.get(i).setLastName(myEvent.getData().getLastName());
                        pendingPojo.get(i).setProfilePic(myEvent.getData().getProProfilePic());
                        pendingPojo.get(i).setBookingId(myEvent.getData().getBookingId());
                        pendingPojo.get(i).setPhone(myEvent.getData().getPhone().get(0).getCountryCode()+""+
                                myEvent.getData().getPhone().get(0).getPhone());
                        upComingPojo.add(0, pendingPojo.get(i));
                        pendingPojo.remove(i);
                        myProjectView.onNotifyAdapter(pendingPojo,upComingPojo,pastPojo, true);
                        isPopUpShowing = true;
                        break;
                    }
                }

                break;
            case 4:// booking rejected
            case 5:// booking ignored

                /*if(!Constants.isJobDetailsOpen)
                {
                    if(isPopUpShowing)
                    {
                        isPopUpShowing = false;
                        alertProgress.alertinfo(this.mContext,myBookingStatus.getData().getStatusMsg());
                    }
                }*/
                if(manager.getBookingStatus(myEvent.getData().getBookingId())<myEvent.getData().getStatus()){
                    manager.setBookingStatus(myEvent.getData().getBookingId(),myEvent.getData().getStatus());
                    notificationUtils.showJustNotification
                            ("", myEvent.getData().getStatusMsg(),myEvent.getData().getMsg(), new Intent());
                }
                for (int i = 0; i < pendingPojo.size(); i++) {
                    if (myEvent.getData().getBookingId() == pendingPojo.get(i).getBookingId()) {
                        //   index = i;
                        pendingPojo.get(i).setStatus(myEvent.getData().getStatus());
                        pendingPojo.get(i).setStatusMsg(myEvent.getData().getStatusMsg());
                        pendingPojo.get(i).setFirstName(myEvent.getData().getFirstName());
                        pendingPojo.get(i).setLastName(myEvent.getData().getLastName());
                        pendingPojo.get(i).setProfilePic(myEvent.getData().getProProfilePic());
                        //  pendingPojo.get(i).setPhone(myEvent.getData().getPhone());
                        pastPojo.add(0, pendingPojo.get(i));
                        pendingPojo.remove(i);
                        myProjectView.onNotifyAdapter(pendingPojo,upComingPojo,pastPojo, true);
                        isPopUpShowing = true;
                        break;
                    }

                }

                break;
            case 6:
            case 7:
            case 8:
            case 9:
                changeAssignedStatus(myBookingStatus,pendingPojo,upComingPojo,pastPojo);
                if(manager.getBookingStatus(myEvent.getData().getBookingId())<myEvent.getData().getStatus()){
                    manager.setBookingStatus(myEvent.getData().getBookingId(),myEvent.getData().getStatus());
                    Intent intent = new Intent(mContext,JobDetailsActivity.class);
                    intent.putExtra("BID", myEvent.getData().getBookingId());
                    intent.putExtra("STATUS", myEvent.getData().getStatus());
                    intent.putExtra("ImageUrl",myEvent.getData().getProProfilePic());
                    intent.putExtra("BookingModel",myEvent.getData().getBookingModel());
                    intent.putExtra("CallType",myEvent.getData().getCallType());
                    notificationUtils.showJustNotification
                            ("LIVESTATUS", myEvent.getData().getStatusMsg(),myEvent.getData().getMsg(), intent);
                }
                break;
            case 10:// booking completed
                break;
            case 11:
                if(!Constants.isJobDetailsOpen)
                {

                    if(manager.getBookingStatus(myEvent.getData().getBookingId())<myEvent.getData().getStatus()){
                        manager.setBookingStatus(myEvent.getData().getBookingId(),myEvent.getData().getStatus());
                        notificationUtils.showJustNotification
                                ("", myEvent.getData().getStatusMsg(),myEvent.getData().getMsg(), new Intent());
                    }
                }

                Log.d("TAG", "onNextBookingStatusCancel: " + myBookingStatus.getData().getStatus()
                        +" bookingId "+myBookingStatus.getData().getBookingId());

                for (int i = 0; i < upComingPojo.size(); i++) {
                    if (myEvent.getData().getBookingId() == upComingPojo.get(i).getBookingId()) {
                        upComingPojo.get(i).setStatus(myEvent.getData().getStatus());
                        upComingPojo.get(i).setStatusMsg(myEvent.getData().getStatusMsg());
                        pastPojo.add(0, upComingPojo.get(i));
                        upComingPojo.remove(i);
                        myProjectView.onNotifyAdapter(pendingPojo, upComingPojo, pastPojo, true);
                        isPopUpShowing = true;
                        break;
                    }
                }
                break;

            case 15:
                if(!Constants.isJobDetailsOpen) {
                    if (isPopUpShowing) {
                        isPopUpShowing = false;
                        alertProgress.alertinfo(this.mContext, myBookingStatus.getData().getStatusMsg());
                    }
                }
                for (int i = 0; i < upComingPojo.size(); i++) {
                    if (myEvent.getData().getBookingId() == upComingPojo.get(i).getBookingId()) {
                        //  index = i;
                        upComingPojo.get(i).setStatus(myEvent.getData().getStatus());
                        upComingPojo.get(i).setStatusMsg(myEvent.getData().getStatusMsg());
                        upComingPojo.get(i).setFirstName("");
                        upComingPojo.get(i).setLastName("");
                        upComingPojo.get(i).setProfilePic("");
                        upComingPojo.get(i).setPhone("");
                        if(myEvent.getData().getReminderId()!=null && !"".equals(myEvent.getData().getReminderId()))
                        {
                            calendarEventHelper.deleteEvent(Long.parseLong(myEvent.getData().getReminderId()));

                           // removeReminderId(myEvent.getData().getReminderId(),myEvent.getData().getBookingId());
                        }
                        pendingPojo.add(0, upComingPojo.get(i));
                        upComingPojo.remove(i);
                        myProjectView.onNotifyAdapter(pendingPojo,upComingPojo,pastPojo, true);
                        //  isPopUpShowing = true;
                        break;
                    }
                }
                break;
        }
    }

    private static void checkAndAddEvent(Context context, long bookingId, long bookingTime, SessionManagerImpl manager) {
        CalendarEventHelper calendarEventHelper=new CalendarEventHelper(context);
        Log.d("TAG", "checkAndAddEvent: ");
        if(manager.getBookingStatus(bookingId)<3){
            calendarEventHelper.addEvent(bookingTime,bookingId);
        }
    }

    @Override
    public void onContextReceived(Context mContext)
    {
        this.mContext = mContext;
    }

    private void changeAssignedStatus(MyBookingStatus myEvents, ArrayList<AllBookingEventPojo> pendingPojo, ArrayList<AllBookingEventPojo> upComingPojo, ArrayList<AllBookingEventPojo> pastPojo) {

        for (int i = 0; i < upComingPojo.size(); i++) {
            if (myEvents.getData().getBookingId() == upComingPojo.get(i).getBookingId()) {
                upComingPojo.get(i).setStatusMsg(myEvents.getData().getStatusMsg());
                upComingPojo.get(i).setStatus(myEvents.getData().getStatus());
                myProjectView.onNotifyAdapter(pendingPojo, upComingPojo, pastPojo, true);
                break;
            }
        }
    }
}
