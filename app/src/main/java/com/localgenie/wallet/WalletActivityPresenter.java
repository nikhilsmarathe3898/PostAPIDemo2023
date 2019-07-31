package com.localgenie.wallet;



import android.util.Log;

import com.google.gson.Gson;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.pojo.ErrorHandel;
import com.utility.RefreshToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;


public class WalletActivityPresenter implements WalletActivityContract.WalletPresenter
{
    private final String TAG = "WalletDetailsProvider";

    @Inject WalletActivityContract.WalletView walletView;
    @Inject
    SessionManagerImpl preferenceHelperDataSource;
    @Inject LSPServices networkService;
    @Inject
    Gson gson;

    @Inject
    public WalletActivityPresenter() {
    }


    @Override
    public void getWalletLimits()
    {
        walletView.showProgressDialog();
        Observable<Response<ResponseBody>> request = networkService.getWalletLimits(
                preferenceHelperDataSource.getAUTH(), Constants.selLang);

        request.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                      //  compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ResponseBody> value) {
                        Log.d(TAG , " getWalletLimits onNext: " + value.code());
                        String responseString;
                        try {
                            switch (value.code()) {
                                case 200:

                                     responseString = value.body().string();
                                    Log.d(TAG , " getWalletLimits onNext: "+responseString);
                                    JSONObject profileObject = new JSONObject(responseString);
                                    JSONObject dataObject = profileObject.getJSONObject("data");
                                    String balance = dataObject.getString("walletAmount");
                                    String softLimit = dataObject.getString("softLimit");
                                    String hardLimit = dataObject.getString("hardLimit");
                                    String currencySymbol=dataObject.getString("currencySymbol");
                                    Constants.walletAmount = Double.parseDouble(balance);
                                    Constants.walletCurrency = currencySymbol;
                                    walletView.setBalanceValues(currencySymbol+" "+balance, currencySymbol+" "+hardLimit
                                            , currencySymbol+" "+softLimit);


                                    break;
                                case 410:
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    responseString =value.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    walletView.onLogout(jsonObject.getString("message"));
                                    break;

                                case Constants.SESSION_EXPIRED:
                                    responseString = value.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(responseString, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(),networkService, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            preferenceHelperDataSource.setAUTH(newToken);
                                            getWalletLimits();
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            assert walletView != null;
                                            walletView.onLogout(msg);
                                        }
                                    });
                                    break;

                                default:
                                    String error = value.errorBody().string();
                                    walletView.walletDetailsApiErrorViewNotifier(new JSONObject(error).getString("message"));
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
                        Log.d(TAG , "getWalletLimits error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        walletView.hideProgressDialog();
                    }
                });

    }

    @Override
    public void rechargeWallet(String amount, String cardId)
    {
        Log.d(TAG, "rechargeWallet: "+preferenceHelperDataSource.getAUTH()
        +" cardId "+cardId +" amount  "+amount);
        walletView.showProgressDialog();
        Observable<Response<ResponseBody>> request = networkService.rechargeWallet(
                preferenceHelperDataSource.getAUTH(),
                Constants.selLang, cardId, amount);   //card_1CAvIg2876tVKl2MlPy06DBq

        request.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                      //  compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ResponseBody> value) {
                        Log.d(TAG , " RechargeWallet onNext: " + value.code());
                        try {
                            switch (value.code()) {
                                case 200:

                                    String responseString = value.body().string();
                                    Log.d(TAG , " RechargeWallet onNext: " + responseString);

                                    walletView.walletRecharged(true,new JSONObject(responseString).getString("message"));
                                    /*JSONObject profileObject = new JSONObject(responseString);
                                    JSONObject dataObject = profileObject.getJSONObject("data");
                                    String balance=dataObject.getString("walletAmount");
                                    String softLimit=dataObject.getString("softLimit");
                                    String hardLimit=dataObject.getString("hardLimit");
                                    String currencySymbol=dataObject.getString("currencySymbol");
                                    walletView.setBalanceValues(currencySymbol+" "+balance, currencySymbol+" "+hardLimit
                                            , currencySymbol+" "+softLimit);*/

                                    break;
                                case Constants.SESSION_LOGOUT:
                                    responseString =value.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    walletView.onLogout(jsonObject.getString("message"));
                                    break;

                                case Constants.SESSION_EXPIRED:
                                    responseString = value.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(responseString, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(),networkService, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            preferenceHelperDataSource.setAUTH(newToken);
                                            rechargeWallet(amount, cardId);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            assert walletView != null;
                                            walletView.onLogout(msg);
                                        }
                                    });
                                    break;
                                default:
                                    String responseError = value.errorBody().string();
                                    walletView.walletDetailsApiErrorViewNotifier(new JSONObject(responseError).getString("message"));
                                    break;
                            } } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG , "RechargeWallet error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        walletView.hideProgressDialog();
                    }
                });

    }

    @Override
    public void getLastCardNo()
    {
       /* if(preferenceHelperDataSource.getDefaultCardDetails()!=null) {
            Utility.printLog("MyCardNumber"+preferenceHelperDataSource.getDefaultCardDetails().getLast4());
            walletView.setCard(preferenceHelperDataSource.getDefaultCardDetails().getLast4(),
                    preferenceHelperDataSource.getDefaultCardDetails().getBrand());
        }
        else
            walletView.setNoCard();*/
    }

    @Override
    public String getCurrencySymbol() {
        return null;
    }
}
