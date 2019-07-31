package com.localgenie.home;

import android.util.Log;

import com.google.gson.Gson;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.pojo.BookingChatPojo;
import com.pojo.ErrorHandel;
import com.utility.RefreshToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Ali on 5/28/2018.
 */
public class ChattingFragPresenterImpl implements ChattingFragPresenter.Presenter
{

    private ChattingFragPresenter.ViewPresent viewPresent;
    @Inject
    SessionManagerImpl manager;
    @Inject
    LSPServices lspServices;
    @Inject
    Gson gson;

    @Inject
    public ChattingFragPresenterImpl() {
    }

    @Override
    public void onChattingActiveNonActive()
    {
        Observable<Response<ResponseBody>> responseObservable = lspServices.getBookingChat(manager.getAUTH(), Constants.selLang);
        responseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {
                            int code =  responseBodyResponse.code();
                            String response;
                        JSONObject jsonObject;
                            try
                            {
                                switch (code)
                                {
                                    case Constants.SUCCESS_RESPONSE:
                                        response = responseBodyResponse.body().string();
                                        Log.d("TAG", "onNextBOOKINGCHAT: "+response);
                                        BookingChatPojo resp = gson.fromJson(response,BookingChatPojo.class);
                                        viewPresent.onSuccess(resp.getData());
                                        viewPresent.onHideProgress();
                                        break;
                                    case Constants.SESSION_LOGOUT:
                                        response = responseBodyResponse.errorBody().string();
                                         jsonObject = new JSONObject(response);
                                        if(viewPresent!=null) {
                                            viewPresent.onHideProgress();
                                            viewPresent.onLogout(jsonObject.getString("message"));
                                        }
                                        break;

                                    case Constants.SESSION_EXPIRED:
                                        response = responseBodyResponse.errorBody().string();
                                        ErrorHandel errorHandel = gson.fromJson(response, ErrorHandel.class);
                                        RefreshToken.onRefreshToken(errorHandel.getData(),lspServices, new RefreshToken.RefreshTokenImple() {
                                            @Override
                                            public void onSuccessRefreshToken(String newToken) {

                                                manager.setAUTH(newToken);
                                                onChattingActiveNonActive();

                                            }

                                            @Override
                                            public void onFailureRefreshToken() {

                                            }

                                            @Override
                                            public void sessionExpired(String msg)
                                            {
                                                if(viewPresent!=null) {


                                                    viewPresent.onHideProgress();
                                                    viewPresent.onLogout(msg);
                                                }
                                            }
                                        });
                                        break;
                                }
                            }catch (IOException e)
                            {
                                e.printStackTrace();
                                viewPresent.onErrorNotConnected(e.getMessage());
                                viewPresent.onHideProgress();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        viewPresent.onErrorNotConnected(e.getMessage());
                        viewPresent.onHideProgress();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void attachView(Object view)
    {
        viewPresent = (ChattingFragPresenter.ViewPresent) view;
    }

    @Override
    public void detachView() {

    }
}
