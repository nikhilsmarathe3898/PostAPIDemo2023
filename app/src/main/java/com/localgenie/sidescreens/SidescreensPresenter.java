package com.localgenie.sidescreens;

import android.util.Log;

import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.wallet.WalletActivityContract;
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

/**
 * Created by ${3Embed} on 4/10/17.
 */

public class SidescreensPresenter implements WalletActivityContract.WalletPresenterBalance {


    private String TAG = SidescreensPresenter.class.getSimpleName();
    @Inject
    LSPServices lspServices;
    @Inject
    SessionManagerImpl manager;
    WalletActivityContract.WalletView walletView;

    @Inject
    public SidescreensPresenter() {
    }

    @Override
    public void getWalletLimits()
    {

        Observable<Response<ResponseBody>> request = lspServices.getWalletLimits(
                manager.getAUTH(), Constants.selLang);

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
                        JSONObject jsonObject;
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
                                    String currencySymbol=dataObject.optString("currencySymbol");
                                    Constants.walletCurrency = currencySymbol;
                                    Constants.walletAmount = Double.parseDouble(balance);
                                    if(walletView!=null){
                                        walletView.setBalanceValues(currencySymbol+" "+balance, currencySymbol+" "+hardLimit
                                                , currencySymbol+" "+softLimit);
                                    }

                                    break;
                                case 410:
                                    break;

                                case Constants.SESSION_LOGOUT:
                                    responseString =value.errorBody().string();
                                    jsonObject = new JSONObject(responseString);
                                    //  walletView.onHideProgress();
                                    walletView.onLogout(jsonObject.getString("message"));
                                    break;
                                case 404:
                                    responseString =value.errorBody().string();
                                    jsonObject = new JSONObject(responseString);

                                    walletView.onError(jsonObject.getString("message"));
                                    // providerView.onLogout(jsonObject.getString("message"));
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    responseString = value.errorBody().string();
                                    jsonObject = new JSONObject(responseString);
                                    RefreshToken.onRefreshToken(jsonObject.getString("data"),lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            getWalletLimits();

                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            assert walletView != null;
                                            //  providerView.onHideProgress();
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
                        if(walletView!=null)
                            walletView.hideProgressDialog();
                    }
                });
    }

    @Override
    public void attachView(Object view)
    {
        walletView = (WalletActivityContract.WalletView) view;
    }

    @Override
    public void detachView()
    {
        walletView = null;
    }
}
