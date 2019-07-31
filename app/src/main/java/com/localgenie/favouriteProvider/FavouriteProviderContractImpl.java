package com.localgenie.favouriteProvider;

import android.util.Log;

import com.google.gson.Gson;
import com.localgenie.networking.LSPServices;
import com.localgenie.rateYourBooking.ResponsePojo;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
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
 * Created by Ali on 7/2/2018.
 */
public class FavouriteProviderContractImpl implements FavouriteProviderContract.FavouriteProvider
{

    @Inject
    SessionManagerImpl manager;
    @Inject
    LSPServices lspServices;
    @Inject
    Gson gson;
    @Inject FavouriteProviderContract.FavouriteProviderView providerView;

    @Inject
    public FavouriteProviderContractImpl() {

    }

    @Override
    public void onToGetFavouriteProvider()
    {

        providerView.onShowProgress();
        Observable<Response<ResponseBody>> observable = lspServices.onToGetFavProvider(manager.getAUTH(), Constants.selLang);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        String response;
                        try
                        {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d("TAG", "onNext: "+response);
                                    ResponsePojo responsePojo = gson.fromJson(response,ResponsePojo.class);
                                    providerView.onResponseSuccess(responsePojo.getData());
                                    providerView.onHideProgress();
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    providerView.onHideProgress();
                                    providerView.onLogout(new JSONObject(responseBodyResponse.errorBody().string()).getString("message"));
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    String sid = new JSONObject(responseBodyResponse.errorBody().string()).getString("data");
                                    RefreshToken.onRefreshToken(sid, lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onToGetFavouriteProvider();
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            providerView.onHideProgress();
                                            providerView.onLogout(msg);

                                        }
                                    });
                                    break;
                                    default:
                                        providerView.onHideProgress();
                                        providerView.onError(new JSONObject(responseBodyResponse.errorBody().string()).getString("message"));
                                        break;
                            }
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        providerView.onHideProgress();
                        providerView.onRetry(e.getMessage());
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
