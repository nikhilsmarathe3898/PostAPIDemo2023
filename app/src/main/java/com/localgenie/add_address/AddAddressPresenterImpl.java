package com.localgenie.add_address;

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
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Pramod on 19/12/17.
 */

public class AddAddressPresenterImpl implements AddAddressPresenter {

    @Inject
    AddressView addressView;

    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    LSPServices lspServices;

    @Inject
    Gson gson;
    @Inject
    SessionManagerImpl manager;


    @Inject
    AddAddressPresenterImpl(AddressView addressView) {
        this.addressView = addressView;
        //this.compositeDisposable = new CompositeDisposable();
    }


    @Override
    public void addAddress(String auth, String addLine1, String addLine2, String city, String state, String country, String placeId, String pinCode, Double latitude, Double longitude, String taggedAs, Integer userType, String houseNo, String addressName) {
        Observable<Response<ServerResponse>> response = lspServices.addAddress(auth,addLine1,addLine2,houseNo,addressName,city,state,country,placeId,pinCode,latitude,longitude,taggedAs,userType);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ServerResponse>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ServerResponse> value)
                    {
                        JSONObject errJson;
                        try {
                            switch (value.code()) {
                                case Constants.SUCCESS_RESPONSE:
                                    Log.e("ADD_ADDR", "Value : " + value.body().getMessage());
                                    addressView.hideProgress();
                                    addressView.navToAddressScreen();
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    addressView.hideProgress();
                                    errJson = new JSONObject(value.errorBody().string());
                                    addressView.hideProgress();
                                    addressView.logout(errJson.getString("message"));

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    String responseBody = value.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(responseBody, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(),lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                           addAddress(newToken,addLine1,addLine2,city,state,country,
                                                   placeId,pinCode,latitude,longitude,taggedAs,userType,houseNo,addressName );

                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            assert addressView != null;
                                            addressView.hideProgress();
                                            addressView.logout(msg);
                                        }
                                    });
                                    break;
                                default:
                                    JSONObject errJsonD = new JSONObject(value.errorBody().string());
                                    addressView.setError(errJsonD.getString("message"));

                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    @Override
    public void editAddress(String auth, String addressId, String houseNo, String addressName, String addrLine1, String addrLine2, String city, String state, String country, String s, String pinCode, double new_lat, double new_long, String tag, int i) {

        Log.d("TAG", "editAddress: "+tag);
        Observable<Response<ResponseBody>> observable = lspServices.editAddress(auth, Constants.selLang,
                addressId,houseNo,addressName,addrLine1,addrLine2,city,state,country,s, pinCode,new_lat,new_long,tag,i);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        Log.d("TAG", "onNextResponseBody: "+code);

                        try {


                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    Log.d("TAG", "onNextResponseBody: "+responseBodyResponse.body().string());
                                    addressView.hideProgress();
                                    addressView.navToAddressScreen();

                                    break;
                                case Constants.SESSION_LOGOUT:
                                    addressView.hideProgress();
                                    String responseBodyS = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandelS = gson.fromJson(responseBodyS, ErrorHandel.class);
                                    addressView.logout(errorHandelS.getMessage());
                                    break;
                                case  Constants.SESSION_EXPIRED:
                                    String responseBody = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(responseBody, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(),lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            editAddress(newToken,addressId,houseNo,addressName , addrLine1, addrLine2,
                                                    city, state, country, s, pinCode, new_lat, new_long, tag, i);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            assert addressView != null;
                                            addressView.hideProgress();
                                            addressView.logout(msg);
                                        }
                                    });
                                    break;
                                default:
                                    addressView.hideProgress();
                                    String responseBodys = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandels = gson.fromJson(responseBodys, ErrorHandel.class);
                                    addressView.setError(errorHandels.getMessage());
                                    Log.d("TAG", "onNextRESPONSE: "+responseBodyResponse.errorBody().string());
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
}
