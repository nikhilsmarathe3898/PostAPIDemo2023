package com.localgenie.providerList;

import android.util.Log;

import com.google.gson.Gson;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;
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
import com.pojo.ErrorHandel;
import com.pojo.ProviderResponse;
import retrofit2.Response;

/**
 * <h>ProviderPresenter</h>
 * Created by Ali on 1/29/2018.
 */

public class ProviderPresenter implements ProviderListContract.providerPresenter
{

    @Inject LSPServices lspService;
    @Inject SessionManagerImpl manager;
    private String TAG = ProviderPresenter.class.getSimpleName();


    @Inject
    ProviderListContract.providerView providerView;

    @Inject Gson gson;
    @Inject
    MQTTManager mqttManager;
    @Inject
    ProviderPresenter()
    {}
    @Override
    public void onGetProviderService(final String catId)
    {
        double lat = 0.0,lng = 0.0;
        String auth = manager.getAUTH();
        // ProviderResponse
        if(!manager.getLatitude().equals("") && !manager.getLongitude().equals(""))
        {
            lat = Double.parseDouble(manager.getLatitude());
            lng = Double.parseDouble(manager.getLongitude());
        }

        Log.d(TAG, " onGetProviderService: "+lat +" long: "+lng+" auth: "+auth+" constants.sellang: "+Constants.selLang+
                " catId:"+catId+" Constants.subCatId"+Constants.subCatId+" Constants.distance:"+
                Constants.distance+" Constants.minPrice:"+Constants.minPrice+" Constants.maxPrice:"+Constants.maxPrice+" Constants.bookingType:"+Constants.bookingType+
                " Constants.scheduledDate: "+Constants.scheduledDate+" Constants.scheduledTime: "+Constants.scheduledTime+" Constants.onRepeatEnd:"+Constants.onRepeatEnd+" Constants.onRepeatDays:"+Constants.onRepeatDays+" Utility.dateInTwentyFour(Constants.serverTime):"+Utility.dateInTwentyFour(Constants.serverTime)+
                " manager.getIpAddress():"+manager.getIpAddress()+" Constants.jobtype:"+Constants.jobType);

        Observable<Response<ResponseBody>> observable = lspService.getProviders(auth,
                Constants.selLang,lat,lng,catId,Constants.subCatId,
                Constants.distance,Constants.minPrice,Constants.maxPrice,Constants.bookingType,
                Constants.scheduledDate,Constants.scheduledTime,Constants.onRepeatEnd,Constants.onRepeatDays,Utility.dateInTwentyFour(Constants.serverTime),
                manager.getIpAddress(),Constants.jobType);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        String  responseBody;
                        JSONObject jsonObject;
                        int responseCode = +responseBodyResponse.code();
                        try {
                            Log.d(TAG, "onNextProviderCode: "+responseCode);
                            switch (responseCode) {
                                case 200:
                                    responseBody = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextProviderResponse: "+responseBody);
                                    ProviderResponse response = gson.fromJson(responseBody,ProviderResponse.class);
                                    response.setErrFlag(0);
                                    response.setErrNum(200);
                                    response.setErrMsg(response.getMessage());
                                    JSONObject jsonObject1 = new JSONObject(responseBody);
                                    Constants.serverTime =  jsonObject1.getLong("serverTime");
                                    //   Constants.serverTime = response.getServerTime();
                                    if(providerView!=null)
                                    {
                                        providerView.onSuccessData(response.getData());
                                        providerView.onHideProgress();
                                    }
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    responseBody =responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(responseBody);
                                    providerView.onHideProgress();
                                    providerView.onLogout(jsonObject.getString("message"));
                                    break;
                                case 404:
                                    responseBody =responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(responseBody);
                                    providerView.onHideProgress();
                                    providerView.onError(jsonObject.getString("message"));
                                    providerView.noProviderAvailable(jsonObject.getString("message"));
                                    // providerView.onLogout(jsonObject.getString("message"));
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    responseBody = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(responseBody, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(),lspService, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onGetProviderService(catId);

                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            assert providerView != null;
                                            providerView.onHideProgress();
                                            providerView.onLogout(msg);
                                        }
                                    });
                                    break;

                                    default:
                                        responseBody =responseBodyResponse.errorBody().string();
                                        jsonObject = new JSONObject(responseBody);
                                        providerView.onHideProgress();
                                        providerView.onError(jsonObject.getString("message"));
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


                        assert providerView != null;
                        providerView.onNoConnectionAvailable(e.getMessage(),false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onGetProviderLocation(final String catId)
    {
        if(!mqttManager.isMQTTConnected())
        {
            mqttManager.createMQttConnection(manager.getSID(),false);
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mqttManager.subscribeToTopic(MqttEvents.Provider.value + "/" + manager.getSID(), 1);

                }
            },1000);
        }
        //Log.d(TAG, "onGetProviderLocation: Constants.calltype:"+Constants.callType);
        Log.d(TAG, "onGetProviderLocationLat: "+Constants.lat+" lan "+Constants.lng
                +"\n catId "+catId+" subCatId "+Constants.subCatId +" distance "+Constants.distance
                +"\n minPrice "+Constants.minPrice+" maxPrice "+Constants.maxPrice +" bookingType "+Constants.bookingType
                +"\nScheduledDate"+Constants.scheduledDate+" durationTime "+Constants.scheduledTime
        +"\ncallType "+Constants.jobType);

        Observable<Response<ResponseBody>> observable = lspService.getLocation(manager.getAUTH()
                ,Constants.selLang,Constants.lat,Constants.lng,catId,Constants.subCatId,
                Constants.distance,Constants.minPrice,Constants.maxPrice,Constants.bookingType,
                Constants.scheduledDate,Constants.scheduledTime,Constants.onRepeatEnd,Constants.onRepeatDays,Utility.dateInTwentyFour(Constants.serverTime),
                manager.getIpAddress(),Constants.jobType);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {

                        String responseBody = "";

                        try {

                            Log.w(TAG, "onNextLocation: "+responseBodyResponse.code()
                                    +" "+responseBody);

                            if(responseBodyResponse.code()==Constants.SESSION_EXPIRED)
                            {
                                responseBody = responseBodyResponse.errorBody().string();
                                Log.w(TAG, "onNextLocationResponse: "+responseBody);
                                ErrorHandel errorHandel = gson.fromJson(responseBody, ErrorHandel.class);
                                RefreshToken.onRefreshToken(errorHandel.getData(),lspService, new RefreshToken.RefreshTokenImple() {
                                    @Override
                                    public void onSuccessRefreshToken(String newToken) {
                                        manager.setAUTH(newToken);
                                        onGetProviderLocation(catId);
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
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {

                        if(providerView!=null)
                            providerView.onNoConnectionAvailable(e.getMessage(),true);
                    }

                    @Override
                    public void onComplete()
                    {

                    }
                });

    }

    @Override
    public void attachView(Object view) {
        //   providerView = (ProviderListContract.providerView) view;
    }

    @Override
    public void detachView() {
        //   providerView = null;
    }
}
