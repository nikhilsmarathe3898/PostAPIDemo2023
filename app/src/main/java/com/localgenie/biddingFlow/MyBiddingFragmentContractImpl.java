package com.localgenie.biddingFlow;

import android.content.Context;
import android.util.Log;

import com.localgenie.R;
import com.localgenie.model.payment_method.CardGetData;
import com.localgenie.model.payment_method.CardGetResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.selectPaymentMethod.SelectedCardInfoInterface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.utility.AlertProgress;

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
 * Created by Ali on 7/6/2018.
 */
public class MyBiddingFragmentContractImpl implements SelectedCardInfoInterface.SelectedPresenter
{

    private SelectedCardInfoInterface.SelectedView selectedView;

    @Inject
    LSPServices lspServices;
    @Inject
    SessionManagerImpl manager;
    @Inject
    public MyBiddingFragmentContractImpl() {
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
                        } else {
                            try {
                                if (cardGetResponseResponse.errorBody()!=null) {
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
                        try {
                            switch (responseBodyResponse.code()) {
                                case 200:

                                    String responseString = responseBodyResponse.body().string();
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

    @Override
    public void attachView(Object view) {
        selectedView = (SelectedCardInfoInterface.SelectedView) view;
    }

    @Override
    public void detachView()
    {

    }
}
