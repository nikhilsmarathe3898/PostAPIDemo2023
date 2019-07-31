package com.localgenie.addTocart;

import android.util.Log;

import com.google.gson.Gson;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.pojo.CartModifiedData;
import com.pojo.ErrorHandel;
import com.pojo.ServiceResponse;
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
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import retrofit2.Response;

/**
 * <h>AddToCartPresenterImpl</h>
 * Created by Ali on 2/7/2018.
 */

public class AddToCartPresenterImpl implements AddToCartContractor.presenter
{
    private String TAG = AddToCartPresenterImpl.class.getSimpleName();

    @Inject
    SessionManagerImpl manager;

    @Inject
    LSPServices lspServices;

    @Inject AddToCartContractor.ContractView contractView;

    @Inject
    Gson gson;

    @Inject
    AddToCartPresenterImpl()
    {

    }
    private ArrayList<ServiceResponse.ServiceDataResponse> serviceResponse = new ArrayList<>();

    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }


    @Override
    public void addSubCartData(String catId, String serviceId, int action, final int serviceType)
    {
        Observable<Response<ResponseBody>> observable = lspServices.onCatModification(manager.getAUTH(),
                Constants.selLang,serviceType,Constants.bookingType,catId,serviceId,1,action,Constants.proId
        ,Constants.jobType);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {
                        int code = responseBodyResponse.code();
                        String response;
                        try {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:

                                     response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextAddToCart: "+response );
                                    CartModifiedData cartModifiedData = gson.fromJson(response,CartModifiedData.class);
                                    Constants.serviceSelected = cartModifiedData.getData().getServiceType();
                                    if(Constants.serviceSelected==1)
                                    {
                                        contractView.onCartModifiedSuccess(cartModifiedData.getData());
                                        contractView.removeHourly();
                                    }else {
                                        contractView.addHourly(cartModifiedData.getData());
                                        contractView.removeFixed();
                                    }
                                    contractView.onHideProgress();
                                    break;

                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    contractView.onLogout(new JSONObject(response).getString("message"));
                                    contractView.onHideProgress();
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(),lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken)
                                        {
                                            manager.setAUTH(newToken);
                                            addSubCartData(catId, serviceId, action, serviceType);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            contractView.onLogout(msg);
                                            contractView.onHideProgress();
                                        }
                                    });

                                    break;
                                    default:
                                        response = responseBodyResponse.errorBody().string();
                                        contractView.onError(new JSONObject(response).getString("message"));
                                        contractView.onHideProgress();
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
                        contractView.onHideProgress();
                        contractView.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onSubServiceApiCalled(final String catId, final String proId)
    {

        Log.d(TAG, "onSubServiceApiCalled: "+manager.getAUTH() +" lang "+Constants.selLang
                +" catId "+catId+" proId "+proId);
        Observable<Response<ResponseBody>> observables = lspServices.getSubServices(manager.getAUTH(),
                Constants.selLang,catId,proId);

        observables.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {
                        String responseBody;
                        JSONObject jsonObject;
                        int code = responseBodyResponse.code();

                        try {

                            Log.d(TAG, "onNextOnSubServiceApiCalled: "+code);
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    responseBody = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextOnSubServiceApiCalledRes: "+responseBody);
                                    ServiceResponse response = gson.fromJson(responseBody,ServiceResponse.class);
                                    serviceResponse.addAll(response.getData());
                                    contractView.onSuccessSubService(response.getData());
                                    contractView.onHideProgress();
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    responseBody = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(responseBody);
                                    contractView.onLogout(jsonObject.getString("message"));
                                    contractView.onHideProgress();
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    responseBody = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(responseBody, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(),lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken)
                                        {
                                            manager.setAUTH(newToken);
                                            onSubServiceApiCalled(catId,proId);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            contractView.onLogout(msg);
                                            contractView.onHideProgress();
                                        }
                                    });

                                    break;
                                default:
                                    responseBody = responseBodyResponse.errorBody().string();
                                    contractView.onError(new JSONObject(responseBody).getString("message"));
                                    contractView.onHideProgress();
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
                        contractView.onHideProgress();
                        contractView.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    @Override
    public void getCartServiceCall(final String catId)
    {

        Log.d(TAG, "getCartServiceCall: "+catId);
        Observable<Response<ResponseBody>> observable = lspServices.getSubCart(manager.getAUTH(),
                Constants.selLang,catId,Constants.proId,Constants.jobType,Constants.bookingType);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        Log.d(TAG, "onNextIntCode: "+code);
                        String response;
                        try {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                     response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextgetAddedCart: "+response+" size "+serviceResponse);
                                    CartModifiedData cartModifiedData = gson.fromJson(response,CartModifiedData.class);
                                    if(cartModifiedData.getData().getServiceType()==1)
                                        contractView.onAlreadyAddedCart(cartModifiedData.getData(),serviceResponse,true);
                                    else
                                        contractView.onAlreadyAddedCart(cartModifiedData.getData(),serviceResponse,false);
                                     Constants.serviceSelected = cartModifiedData.getData().getServiceType();
                                    break;

                                case Constants.SESSION_EXPIRED:
                                    String responseError = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(responseError,ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            getCartServiceCall(catId);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {
                                            contractView.onLogout("");
                                            contractView.onHideProgress();
                                        }

                                        @Override
                                        public void sessionExpired(String msg)
                                        {
                                            contractView.onLogout(msg);
                                            contractView.onHideProgress();
                                        }
                                    });
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(response);
                                    contractView.onLogout(jsonObject.getString("message"));
                                    contractView.onHideProgress();
                                    break;
                                case 409:

                                    response = responseBodyResponse.errorBody().string();
                                    JSONObject jsonObjecterrCart = new JSONObject(response);
                                    Log.d(TAG, "onNextErrorResponse: "+jsonObjecterrCart.getString("message"));
                                    contractView.onAlreadyCartPresent(jsonObjecterrCart.getString("message"),true);
                                    break;
                                    default:
                                        response = responseBodyResponse.errorBody().string();
                                        JSONObject jsonObjecterr = new JSONObject(response);
                                        Log.d(TAG, "onNextErrorResponse: "+jsonObjecterr.getString("message"));
                                        break;

                            }
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                        contractView.onHideProgress();
                        contractView.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
