package com.localgenie.selectPaymentMethod;

import android.content.Context;
import android.util.Log;

import com.localgenie.R;
import com.localgenie.model.payment_method.CardGetData;
import com.localgenie.model.payment_method.CardGetResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.utility.AlertProgress;
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
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * <h>SelectedPaymentTypeImpl</h>
 * Created by Ali on 3/12/2018.
 */

public class SelectedPaymentTypeImpl implements SelectedCardInfoInterface.SelectedPresenter
{
    @Inject
    SessionManagerImpl manager;
    @Inject
    LSPServices lspServices;

    @Inject
    SelectedCardInfoInterface.SelectedView selectedView;

    @Inject
    public SelectedPaymentTypeImpl() {
    }




    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void onGetCards()
    {

        Observable<Response<CardGetResponse>> observable = lspServices.getCard(manager.getAUTH(),Constants.selLang);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<CardGetResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<CardGetResponse> cardGetResponseResponse)
                    {
                        if (200 == cardGetResponseResponse.code()) {
                            CardGetResponse response = cardGetResponseResponse.body();

                            if (response!=null) {
                                ArrayList<CardGetData> cardsList = response.getData();
                                if (cardsList.size()>0) {
                                    Log.d("TAG", "onNextCARDBODY: "+cardsList.get(0).getBrand()
                                            +" "+cardsList.get(0).getFunding());
                                    selectedView.addItems(cardsList);
                                }
                                selectedView.onHideProgress();
                            } else {
                                selectedView.onHideProgress();
                            }
                        }else if(440 == cardGetResponseResponse.code())
                        {
                            if (cardGetResponseResponse.errorBody()!=null) {
                                JSONObject errJson = null;
                                try {
                                    errJson = new JSONObject(cardGetResponseResponse.errorBody().string());

                                    RefreshToken.onRefreshToken(errJson.getString("data"), lspServices
                                            , new RefreshToken.RefreshTokenImple() {
                                                @Override
                                                public void onSuccessRefreshToken(String newToken) {
                                                    manager.setAUTH(newToken);
                                                    //  selectedView.onShowProgress();
                                                    getWalletAmount();
                                                }

                                                @Override
                                                public void onFailureRefreshToken() {

                                                }

                                                @Override
                                                public void sessionExpired(String msg) {
                                                    selectedView.onLogout(msg);
                                                    selectedView.onHideProgress();
                                                }
                                            });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } else if(498 == cardGetResponseResponse.code())
                        {
                            try {
                            JSONObject errJson = new JSONObject(cardGetResponseResponse.errorBody().string());


                                selectedView.onLogout(errJson.getString("message"));
                                selectedView.onHideProgress();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        else{
                            try {
                                if (cardGetResponseResponse.errorBody()!=null) {
                                    assert cardGetResponseResponse.errorBody() != null;
                                    JSONObject errJson = new JSONObject(cardGetResponseResponse.errorBody().string());
                                    selectedView.onError(errJson.getString("message"));
                                    selectedView.onHideProgress();
                                }
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

    @Override
    public void getWalletAmount()
    {
        Observable<Response<ResponseBody>> observable = lspServices.getWalletLimits(manager.getAUTH()
        , Constants.selLang);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {


                        Log.d("TAG" , " getWalletLimits onNext: " + responseBodyResponse.code());
                        String responseString;
                        try {
                            switch (responseBodyResponse.code()) {
                                case 200:

                                     responseString = responseBodyResponse.body().string();
                                    Log.d("responseBodyResponse" , " getWalletLimits onNext: "+responseString);
                                    JSONObject profileObject = new JSONObject(responseString);
                                    JSONObject dataObject = profileObject.getJSONObject("data");
                                    String balance = dataObject.getString("walletAmount");
                                    double softLimit = dataObject.getDouble("softLimit");
                                    double hardLimit = dataObject.getDouble("hardLimit");
                                    String currencySymbol=dataObject.getString("currencySymbol");
                                    Constants.walletCurrency = currencySymbol;
                                    Constants.walletAmount = Double.parseDouble(balance);
                                    selectedView.showWalletAmount(currencySymbol,Double.parseDouble(balance),softLimit,hardLimit);
                                   /* walletView.setBalanceValues(currencySymbol+" "+balance, currencySymbol+" "+hardLimit
                                            , currencySymbol+" "+softLimit);*/


                                    break;
                                case 410:
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    assert responseBodyResponse.errorBody() != null;
                                    responseString = responseBodyResponse.errorBody().string();
                                    RefreshToken.onRefreshToken(new JSONObject(responseString).getString("data"), lspServices
                                            , new RefreshToken.RefreshTokenImple() {
                                                @Override
                                                public void onSuccessRefreshToken(String newToken) {
                                                    manager.setAUTH(newToken);
                                                  //  selectedView.onShowProgress();
                                                    getWalletAmount();
                                                }

                                                @Override
                                                public void onFailureRefreshToken() {

                                                }

                                                @Override
                                                public void sessionExpired(String msg) {
                                                    selectedView.onLogout(msg);
                                                    selectedView.onHideProgress();
                                                }
                                            });
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    assert responseBodyResponse.errorBody() != null;
                                    responseString = responseBodyResponse.errorBody().string();
                                    selectedView.onLogout(new JSONObject(responseString).getString("message"));
                                    break;

                                default:
                                    String error = responseBodyResponse.errorBody().string();
                                    selectedView.onError(new JSONObject(error).getString("message"));
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

    private Context mContext;
    private AlertProgress alertProgress;
    @Override
    public void setCashCardBookingView(int select, double balance, double softLimit, double hardLimit, Context mContext, AlertProgress alertProgress)
    {
        this.alertProgress = alertProgress;
        this.mContext = mContext;
        if(balance<softLimit)
        {
            String message = mContext.getResources().getString(R.string.reachedSoftLimit);
            String title = mContext.getResources().getString(R.string.warning);
            openDialog(select,message,title,true);
        }else if(balance<hardLimit)
        {
            String message = mContext.getResources().getString(R.string.reachedHardLimit) + " "+mContext.getResources().getString(R.string.cashBooking);
            String title = mContext.getResources().getString(R.string.error);
            openDialog(0,message,title,false);
        }else
            selectedView.paymentSelection(select);
    }

    private void openDialog(int i, String message, String title, boolean b) {
        alertProgress.alertPositiveNegativeOnclick(mContext, message, title,
                mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.later), false, isClicked -> {

                    if(isClicked)
                        selectedView.startActivity();
                    else
                    {
                        if(b)
                            selectedView.paymentSelection(i);
                    }
                });
    }
}
