package com.localgenie.payment_method;

import android.util.Log;

import com.localgenie.model.payment_method.CardGetData;
import com.localgenie.model.payment_method.CardGetResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.utility.RefreshToken;

import org.json.JSONObject;

import java.util.ArrayList;
import javax.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author Pramod
 * @since 31-01-2018.
 */
public class PaymentMethodPresenterImpl implements PaymentMethodPresenter{

    @Inject
    PaymentMethodView paymentMethodView;

    @Inject
    LSPServices lspServices;


    @Inject
    SessionManagerImpl manager;

    @Inject
    PaymentMethodPresenterImpl(PaymentMethodView paymentMethodView) {
        this.paymentMethodView = paymentMethodView;
    }

    @Override
    public void getCard(String auth) {
        if (paymentMethodView!=null) {
            paymentMethodView.showProgress();
        }
        Observable<Response<CardGetResponse>> bad = lspServices.getCard(auth, Constants.selLang);
        bad.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<CardGetResponse>>() {

                    @Override
                    public void onNext(Response<CardGetResponse> value)
                    {
                        try {
                            if (200 == value.code()) {
                                CardGetResponse response = value.body();

                                if (response != null) {
                                    ArrayList<CardGetData> cardsList = response.getData();
                                    if (cardsList.size() > 0) {
                                        Log.d("TAG", "onNextCARDBODY: " + cardsList.get(0).getBrand()
                                                + " " + cardsList.get(0).getFunding());

                                        manager.setDefaultCardId(cardsList.get(0).getId());
                                        manager.setDefaultCardNum(cardsList.get(0).getLast4());
                                        manager.setDefaultCardName(cardsList.get(0).getBrand());

                                        paymentMethodView.addItems(cardsList);
                                    } else
                                        paymentMethodView.cardNotFound();
                                    paymentMethodView.hideProgress();
                                } else {
                                    paymentMethodView.hideProgress();
                                }
                            } else if (498 == value.code()) {
                                JSONObject errJson = new JSONObject(value.errorBody().string());
                                paymentMethodView.logout(errJson.getString("message"));
                                paymentMethodView.hideProgress();
                            } else if(440 == value.code())
                            {
                                JSONObject errJsonD = new JSONObject(value.errorBody().string());
                                RefreshToken.onRefreshToken(errJsonD.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                    @Override
                                    public void onSuccessRefreshToken(String newToken) {

                                        manager.setAUTH(newToken);
                                        getCard(newToken);
                                    }

                                    @Override
                                    public void onFailureRefreshToken() {

                                    }

                                    @Override
                                    public void sessionExpired(String msg) {
                                        paymentMethodView.logout(msg);
                                        paymentMethodView.hideProgress();
                                    }
                                });
                            }
                            else{

                                if (value.errorBody() != null) {
                                    JSONObject errJson = new JSONObject(value.errorBody().string());
                                    paymentMethodView.setErrorMsg(errJson.getString("message"));
                                    paymentMethodView.hideProgress();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Throwable e)
                    {
                        Log.e("Error","error"+e.getMessage());
                        e.printStackTrace();
                        paymentMethodView.setErrorMsg(e.getMessage());
                        paymentMethodView.hideProgress();
                    }
                    @Override
                    public void onComplete() {
                        paymentMethodView.hideProgress();
                    }
                });


    }

   /* @Override
    public void editCard(String auth) {

    }*/
}
