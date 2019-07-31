package com.localgenie.zendesk.zendeskTicketDetails;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.zendesk.zendeskpojo.ZendeskHistory;
import com.utility.RefreshToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import com.pojo.ErrorHandel;
import retrofit2.Response;

/**
 * <h>HelpIndexContractImpl</h>
 * Created by Ali on 2/26/2018.
 */

public class HelpIndexContractImpl implements HelpIndexContract.presenter
{
    @Inject
    LSPServices lspServices;
    @Inject HelpIndexContract.HelpView helpView;
    @Inject Gson gson;
    @Inject
    SessionManagerImpl manager;
    @Inject
    public HelpIndexContractImpl() {
    }

    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void onPriorityImage(Context mContext, String priority, ImageView ivHelpCenterPriorityPre) {

        if(priority.equalsIgnoreCase(mContext.getString(R.string.priorityUrgent)))
            ivHelpCenterPriorityPre.setBackgroundColor(Utility.getColor(mContext,R.color.green_continue));
        else if(priority.equalsIgnoreCase(mContext.getString(R.string.priorityHigh)))
            ivHelpCenterPriorityPre.setBackgroundColor(Utility.getColor(mContext,R.color.livemblue3498));
        else if(priority.equalsIgnoreCase(mContext.getString(R.string.priorityNormal)))
            ivHelpCenterPriorityPre.setBackgroundColor(Utility.getColor(mContext,R.color.red_login_dark));
        else if(priority.equalsIgnoreCase(mContext.getString(R.string.priorityLow)))
            ivHelpCenterPriorityPre.setBackgroundColor(Utility.getColor(mContext,R.color.saffron));

    }

    @Override
    public void callApiToCommentOnTicket(String trim, int zenId)
    {
        Observable<Response<ResponseBody>>observable = lspServices.commentOnTicket(manager.getAUTH(),
                Constants.selLang,zenId,trim,manager.getRegisterId());

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d)
                    {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();

                        switch (code)
                        {
                            case Constants.SUCCESS_RESPONSE:

                                break;
                            case Constants.SESSION_LOGOUT:

                                break;
                            case Constants.SESSION_EXPIRED:

                                break;
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
    public void callApiToCreateTicket(final String trim, final String subject, final String priority)
    {

        Observable<Response<ResponseBody>>observable = lspServices.createTicket(manager.getAUTH(),
                Constants.selLang,subject,trim,"open",priority,"problem",manager.getRegisterId());

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {
                        int code = responseBodyResponse.code();
                        String response;
                        try {


                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d("TAG", "RESULTonSuccess: "+response);
                                    helpView.onZendeskTicketAdded(response);
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response,ErrorHandel.class);

                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            callApiToCreateTicket(trim, subject, priority);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

                                            helpView.onLogout(msg);
                                        }
                                    });
                                    break;
                            }
                        } catch (IOException e) {
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
    public void callApiToGetTicketInfo(final int zenId)
    {
        Observable<Response<ResponseBody>> observable = lspServices.onToGetZendeskHistory(manager.getAUTH(),
                Constants.selLang,zenId);

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
                                    Log.d("HELPINDEX", "onNextResponseINFOTICKET: "+response);
                                    ZendeskHistory zendeskHistory = gson.fromJson(response,ZendeskHistory.class);

                                    Date date = new Date(zendeskHistory.getData().getTimeStamp() * 1000L);
                                    String dateTime[] = Utility.getFormattedDate(date).split(",");
                                    String timeToSet =  dateTime[0]+" | "+dateTime[1];
                                    helpView.onTicketInfoSuccess(zendeskHistory.getData().getEvents(),timeToSet,
                                            zendeskHistory.getData().getSubject(),zendeskHistory.getData().getPriority(),zendeskHistory.getData().getType());
                                    helpView.onHideProgress();
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    helpView.onLogout(jsonObject.getString("message"));
                                    helpView.onHideProgress();
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response,ErrorHandel.class);

                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            callApiToGetTicketInfo(zenId);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

                                            helpView.onLogout(msg);
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
}
