package com.localgenie.payment_details;

import android.util.Log;

import com.google.gson.Gson;
import com.localgenie.model.ServerResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.pojo.ErrorHandel;
import com.utility.RefreshToken;

import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author Pramod
 * @since 31-01-2018.
 */
public class PaymentDetailPresenterImpl implements PaymentDetailPresenter {

    @Inject
    PaymentDetailView paymentDetailView;

    @Inject
    LSPServices lspServices;

    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    SessionManagerImpl manager;
    @Inject
    Gson gson;

    @Inject
    PaymentDetailPresenterImpl(PaymentDetailView paymentDetailView) {
        this.paymentDetailView = paymentDetailView;
    }

    @Override
    public void addCard(String auth,String email,String stripe_token) {
        /*if (paymentDetailView !=null) {
            paymentDetailView.showProgress();
        }*/
        Observable<Response<ServerResponse>> bad = lspServices.addCard(auth, Constants.selLang,email,stripe_token);
        bad.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ServerResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }
                    @Override
                    public void onNext(Response<ServerResponse> value)
                    {
                        Log.e("CARD","Req url "+value.raw().request().url());

                        try{

                            switch (value.code())
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    ServerResponse serverResponse = value.body();
                                    if (serverResponse!=null) {
                                        Log.e("CARD",serverResponse.getMessage());
                                        paymentDetailView.hideProgress();
                                        paymentDetailView.navToPaymentScreen();
                                    } else {
                                        paymentDetailView.hideProgress();
                                    }
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    JSONObject errJson = new JSONObject(value.errorBody().string());
                                    paymentDetailView.logout(errJson.getString("message"));
                                    paymentDetailView.hideProgress();
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    String responseBody = value.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(responseBody, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(),lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            addCard(newToken, email, stripe_token);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            paymentDetailView.hideProgress();
                                            paymentDetailView.logout(msg);
                                        }
                                    });
                                    break;
                                    default:
                                        String responseBodyEr = value.errorBody().string();
                                        ErrorHandel errorHandelEr = gson.fromJson(responseBodyEr, ErrorHandel.class);
                                        paymentDetailView.hideProgress();
                                        paymentDetailView.setErrorMsg(errorHandelEr.getMessage());
                                        break;
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(Throwable e)
                    {
                        Log.e("Error","error"+e.getMessage());
                        e.printStackTrace();
                        paymentDetailView.setErrorMsg(e.getMessage());
                        paymentDetailView.hideProgress();
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }
}
