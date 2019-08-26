package com.localgenie.zendesk.zendeskHelpIndex;

import android.util.Log;

import com.google.gson.Gson;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.zendesk.zendeskpojo.AllTicket;
import com.localgenie.zendesk.zendeskpojo.OpenClose;
import com.localgenie.zendesk.zendeskpojo.TicketClose;
import com.localgenie.zendesk.zendeskpojo.TicketOpen;
import com.utility.RefreshToken;

import java.io.IOException;
import java.util.ArrayList;

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
 * <h>ZendeskHelpIndexImpl</h>
 * Created by Ali on 2/26/2018.
 */

public class ZendeskHelpIndexImpl implements ZendeskHelpIndexContract.Presenter
{

    /*http://45.77.190.140:9999/zendesk/user/akbar%40gmail.com*/

    @Inject ZendeskHelpIndexContract.ZendeskView zendeskView;
    @Inject
    LSPServices lspServices;
    @Inject
    Gson gson;
    @Inject
    SessionManagerImpl manager;

    @Inject
    public ZendeskHelpIndexImpl()
    {

    }
    @Override
    public void onToGetZendeskTicket()
    {
        Observable<Response<ResponseBody>> observable = lspServices.onToGetZendeskTicket(manager.getAUTH(),
                Constants.selLang,manager.getEmail());

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
                        ErrorHandel errorHandel;
                        try{


                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d("TAG", "onNextTICKETSuccess: "+response);
                                    AllTicket allTicket = gson.fromJson(response,AllTicket.class);

                                    ArrayList<OpenClose> alOpenClose = new ArrayList<>();
                                    if(allTicket.getData().getClose().size()>0 || allTicket.getData().getOpen().size()>0)
                                    {
                                        if(allTicket.getData().getOpen().size()>0)
                                        {
                                            for(int i = 0;i<allTicket.getData().getOpen().size();i++)
                                            {
                                                TicketOpen ticketOpen = allTicket.getData().getOpen().get(i);
                                                OpenClose openClose = new OpenClose(ticketOpen.getId(),ticketOpen.getTimeStamp()
                                                        ,ticketOpen.getStatus(),ticketOpen.getSubject(),ticketOpen.getType(),
                                                        ticketOpen.getPriority(),ticketOpen.getDescription());

                                                if(i==0)
                                                    openClose.setFirst(true);
                                                // openCloses.add(openClose);
                                                zendeskView.onTicketStatus(openClose,allTicket.getData().getOpen().size(),true);
                                                alOpenClose.add(openClose);
                                            }
                                        }

                                        if(allTicket.getData().getClose().size()>0)
                                        {
                                            for(int i = 0;i<allTicket.getData().getClose().size();i++)
                                            {
                                                TicketClose ticketClose = allTicket.getData().getClose().get(i);
                                                OpenClose openClose = new OpenClose(ticketClose.getId(),ticketClose.getTimeStamp()
                                                        ,ticketClose.getStatus(),ticketClose.getSubject(),ticketClose.getType(),ticketClose.getPriority()
                                                        ,ticketClose.getDescription());
                                                if(i==0)
                                                {
                                                    openClose.setFirst(true);
                                                }
                                                // openCloses.add(openClose);
                                               // zendeskView.onTicketStatus(openClose, allTicket.getData().getClose().size());
                                                zendeskView.onTicketStatus(openClose,allTicket.getData().getClose().size(),false);
                                                alOpenClose.add(openClose);
                                            }
                                        }

                                        zendeskView.onNotifyData(alOpenClose);
                                        zendeskView.onHideProgress();
                                        zendeskView.onRefreshing(false);
                                    }else
                                    {
                                        zendeskView.onEmptyTicket();
                                        zendeskView.onHideProgress();
                                        zendeskView.onRefreshing(false);
                                    }
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                     errorHandel = gson.fromJson(response,ErrorHandel.class);
                                    zendeskView.onError(errorHandel.getData());
                                    zendeskView.onRefreshing(false);

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();

                                    Log.d("TAG", "onNextError: "+response);
                                     errorHandel = gson.fromJson(response,ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onToGetZendeskTicket();
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

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
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }
}
