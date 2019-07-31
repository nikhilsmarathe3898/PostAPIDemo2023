package com.localgenie.providerdetails;


import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.ReadMoreSpannable;
import com.localgenie.utilities.SessionManagerImpl;
import com.pojo.ReviewPojo;
import com.utility.RefreshToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import com.pojo.ErrorHandel;
import com.pojo.ProviderDetailsResponse;
import retrofit2.Response;


/**
 * <h>ProviderPresenterImpl</h>
 * Created by Ali on 2/5/2018.
 */

public class ProviderPresenterImpl implements ProviderDetailsContract.ProviderPresenter
{

    @Inject
    ProviderDetailsContract.ProviderView providerView;

    @Inject
    SessionManagerImpl manager;
    @Inject
    Gson gson;

    @Inject
    LSPServices lspServices;


    @Inject
    public ProviderPresenterImpl()
    {

    }

    @Override
    public void onProviderDetailService(final String proId)
    {
        double lat =0,lng =0;
        if(!manager.getLatitude().equals("") && !manager.getLongitude().equals(""))
        {
            lat = Double.parseDouble(manager.getLatitude());
            lng = Double.parseDouble(manager.getLongitude());
        }

        Log.d("TAG", "onProviderDetailService: "+Constants.catId);
        Observable<Response<ResponseBody>> observable = lspServices.getProviderDetails(manager.getAUTH()
                , Constants.selLang,proId,Constants.catId,lat,lng,Constants.jobType);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("TAG", "onNextonSubscribe: ");
                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {
                        try {
                            int responseCode = responseBodyResponse.code();
                            String responseBody;
                            JSONObject jsonObject;

                            Log.d("TAG", "onNextProviderDetails: "+responseBodyResponse.code());
                            switch (responseCode)
                            {
                                case Constants.SUCCESS_RESPONSE:

                                    responseBody = responseBodyResponse.body().string();
                                    Log.d("TAG", "onNextProviderDetails: "+responseBody);

                                    providerView.onHideProgress();
                                    ProviderDetailsResponse providerDetailsResponse = gson.fromJson(responseBody,ProviderDetailsResponse.class);
                                    providerView.onSuccess(providerDetailsResponse.getData());
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    responseBody = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(responseBody, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(),lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onProviderDetailService(proId);

                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg)
                                        {
                                            providerView.onHideProgress();
                                            providerView.onLogout(msg);
                                        }
                                    });
                                    break;

                                case Constants.SESSION_LOGOUT:
                                    responseBody = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(responseBody);
                                    providerView.onHideProgress();
                                    providerView.onLogout(jsonObject.getString("message"));
                                    break;

                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        Log.d("TAG", "onNextonError: "+e.getMessage());
                        providerView.onErrorNotConnected(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "onNextonComplete: ");
                    }
                });
    }

    @Override
    public void moreReadable(TextView tvProAbout)
    {
        String readMore = "read more";
        String readLess = "read less";


        new ReadMoreSpannable(readMore, readLess);
        if (tvProAbout.getText().toString().length() > 100)
            ReadMoreSpannable.makeTextViewResizable(tvProAbout, 3, readMore, true);

    }

    @Override
    public void callReviewApi(int pageCount, String proId)
    {

        Observable<Response<ResponseBody>>  observable = lspServices.getReviews(manager.getAUTH(),
                Constants.selLang,proId,Constants.catId,pageCount);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        String response;
                        try {

                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    assert responseBodyResponse.body() != null;
                                    response = responseBodyResponse.body().string();

                                    Log.d("TAG", "onNextResponseReview: "+response);
                                    ReviewPojo reviewPojo = gson.fromJson(response,ReviewPojo.class);
                                    providerView.onReviewSuccess(reviewPojo.getData());

                                    break;

                                case Constants.SESSION_LOGOUT:
                                    assert responseBodyResponse.errorBody() != null;
                                    response = responseBodyResponse.errorBody().string();
                                    providerView.onLogout(new JSONObject(response).getString("message"));
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    assert responseBodyResponse.errorBody() != null;
                                    response = responseBodyResponse.errorBody().string();
                                    RefreshToken.onRefreshToken(new JSONObject(response).getString("data"), lspServices
                                            , new RefreshToken.RefreshTokenImple() {
                                                @Override
                                                public void onSuccessRefreshToken(String newToken) {
                                                    manager.setAUTH(newToken);
                                                //    providerView.onShowProgress();
                                                    callReviewApi(pageCount, proId);
                                                }

                                                @Override
                                                public void onFailureRefreshToken() {

                                                }

                                                @Override
                                                public void sessionExpired(String msg) {
                                                    providerView.onLogout(msg);

                                                }
                                            });
                                    break;
                                default:
                                    assert responseBodyResponse.errorBody() != null;
                                    response = responseBodyResponse.errorBody().string();
                                    providerView.onError(new JSONObject(response).getString("message"));
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

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void hireProvider(long bid, String proId) {

        Observable<Response<ResponseBody>> observable = lspServices.onBookingHire(manager.getAUTH(),
                Constants.selLang,bid,proId);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {
                        providerView.onHideProgress();
                        int code = responseBodyResponse.code();

                        String response;
                        try
                        {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:


                                    assert responseBodyResponse.body() != null;
                                    response = responseBodyResponse.body().string();
                                    Log.d("TAG", "onNextOnHire: "+response);
                                    providerView.onBookingHired();
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    assert responseBodyResponse.errorBody() != null;
                                    response = responseBodyResponse.errorBody().string();
                                    providerView.onLogout(new JSONObject(response).getString("message"));
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    assert responseBodyResponse.errorBody() != null;
                                    response = responseBodyResponse.errorBody().string();
                                    RefreshToken.onRefreshToken(new JSONObject(response).getString("data"), lspServices
                                            , new RefreshToken.RefreshTokenImple() {
                                                @Override
                                                public void onSuccessRefreshToken(String newToken) {
                                                    manager.setAUTH(newToken);
                                                    providerView.onShowProgress();
                                                    hireProvider(bid,proId);
                                                }

                                                @Override
                                                public void onFailureRefreshToken() {

                                                }

                                                @Override
                                                public void sessionExpired(String msg) {
                                                    providerView.onLogout(msg);
                                                    providerView.onHideProgress();
                                                }
                                            });
                                    break;
                                default:
                                    assert responseBodyResponse.errorBody() != null;
                                    response = responseBodyResponse.errorBody().string();
                                    providerView.onError(new JSONObject(response).getString("message"));
                                    break;
                            }
                        }catch (Exception e)
                        {
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
    public void attachView(Object view)
    {
        //providerView = (ProviderDetailsContract.ProviderView) view;
    }

    @Override
    public void detachView()
    {
        //  providerView = null;
    }
}
