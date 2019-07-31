package com.localgenie.biddingFlow;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.model.ServerResponse;
import com.localgenie.model.youraddress.YourAddrData;
import com.localgenie.model.youraddress.YourAddressResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.HandlePictureEvents;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.youraddress.YourAddressPresenter;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;
import com.utility.PermissionsManager;
import com.utility.RefreshToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
 * Created by Ali on 6/13/2018.
 */
public class BiddingContractImpl implements BiddingContractor.BiddingContractPresent,YourAddressPresenter {


    private String TAG = BiddingContractImpl.class.getSimpleName();
    @Inject
    BiddingContractor.BiddingContractView biddingView;
    @Inject
    LSPServices lspServices;
    @Inject
    SessionManagerImpl manager;
    @Inject
    Gson gson;

    @Inject
    AlertProgress alertProgress;
    @Inject
    PermissionsManager permissionsManager;
    private Context mContext;
    @Inject
    public BiddingContractImpl() {
    }

    @Override
    public void onLastDues() {

        Observable<Response<ResponseBody>>observable = lspServices.lastDues(manager.getAUTH(),
                Constants.selLang);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        Log.d(TAG, "onNextDuesCode: "+code);
                        String response;
                        try
                        {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextDues: "+response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    biddingView.onSessionExpired();
                                    String msg = jsonObject.getString("message");
                                    String addLine1 =  jsonObject.getJSONObject("data").getString("addLine1");
                                    long timeStamp = jsonObject.getJSONObject("data").getLong("bookingRequestedAt");
                                    Date date = new Date(timeStamp * 1000L);

                                    biddingView.onDuesFound(msg,addLine1,Utility.getFormattedDate(date));
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    biddingView.onSessionExpired();
                                    biddingView.onLogout(new JSONObject(response).getString("message"));
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    //  Log.d(TAG, "onNextSess: "+jsonObject.getString("data"));

                                    RefreshToken.onRefreshToken(jsonObject.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            onLastDues();

                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            biddingView.onLogout(msg);
                                        }
                                    });

                                    break;
                                case Constants.SESSION_NoDues:
                                    biddingView.noDuesFoundLiveBooking();
                                    break;
                                default:
                                    response = responseBodyResponse.errorBody().string();
                                    Log.d(TAG, "onNextONError: "+response);
                                    biddingView.onSessionExpired(new JSONObject(response).getString("message"));
                                   /* biddingView.onError(new JSONObject(response).getString("message"));
                                    biddingView.onSessionExpired();*/
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

                        biddingView.onConnectionError(e.getMessage(),"LastDues","");
                        biddingView.onSessionExpired();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onLiveBookingCalled(int paymentType, LatLng latLng, String cardId, String catId, boolean isWalletSelected, String bidPrice,
                                    String promoCode, JSONArray jsonArray, long visitingTime, String bidAddress)
    {

        double bookingLat = latLng.latitude;
        double bookingLng = latLng.longitude;
        double bidPrices;
        if(!"".equals(bidPrice))
        {
            bidPrices = Double.parseDouble(bidPrice);
        }else
        {
            bidPrices = 0.00;
        }

        int walletSelected = 0;
        if(isWalletSelected)
            walletSelected = 1;
        Log.d(TAG, "onLiveBookingServiceModel: "+ Constants.jobType+" service "+Constants.serviceType+"\ntype "+
                Constants.bookingType+"\npayment "+paymentType+"\naddress "+bidAddress+"\nlat "
                +bookingLat+"\nlng "+bookingLng+"\nproId "+Constants.proId+"\nCatId "+
                Constants.catId+"\npromoCode "+promoCode
                +"\ncardId "+cardId+"\nscheduledDate "+Constants.scheduledDate+"\nSceduledTime "+Constants.scheduledTime+"\nWallet "+walletSelected
                +" JsonArray "+jsonArray+" onRepeatEnd "+Constants.onRepeatEnd+" onRepeatDays "+Constants.onRepeatDays);

        Observable<Response<ResponseBody>> observable =  lspServices.onLiveBooking(manager.getAUTH(),
                Constants.selLang,Constants.jobType,3,Constants.bookingType,paymentType, walletSelected
                ,"", "",bidAddress,bookingLat,bookingLng,"0",
                catId,"", "", promoCode,cardId,Constants.scheduledDate , Constants.scheduledTime,
                Constants.onRepeatEnd,Constants.onRepeatDays, Utility.dateInTwentyFour(Constants.serverTime),bidPrices,jsonArray);

        observable.subscribeOn(Schedulers.newThread())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {
                        int code = responseBodyResponse.code();
                        Log.d(TAG, "onNextLiveResponse: "+code);
                        try {
                           /* String response = responseBodyResponse.body().string();
                            Log.d(TAG, "onNextLiveResponse: "+code + " response "+response);*/
                            String response;
                            JSONObject jsonObject;
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextLiveResponse: "+code+" response "+response);
                                    biddingView.onHideProgress();
                                    biddingView.onSuccessBooking();
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    biddingView.onSessionExpired();
                                    biddingView.onLogout(jsonObject.getString("message"));

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    RefreshToken.onRefreshToken(jsonObject.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onLiveBookingCalled(paymentType, latLng, cardId, catId, isWalletSelected, bidPrice, promoCode, jsonArray, visitingTime, bidAddress);
                                            //  onLiveBookingCalled(paymentType, latLng, cardId, cardId, catId, isWalletSelected);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg)
                                        {

                                            biddingView.onSessionExpired();
                                            biddingView.onLogout(msg);
                                        }
                                    });
                                    break;

                                case 416:
                                    response = responseBodyResponse.errorBody().string();
                                    new JSONObject(response).getString("message");
                                    Log.d(TAG, "onNextONError: "+response);
                                    biddingView.onSessionExpired(new JSONObject(response).getString("message"));
                                    break;
                                case 400:
                                    response = responseBodyResponse.errorBody().string();
                                    Log.d(TAG, "onNextONError: "+response);
                                    biddingView.onSessionExpired(new JSONObject(response).getString("message"));
                                    break;

                                case 201:
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextONError: "+response);
                                    biddingView.onSessionExpired(new JSONObject(response).getString("message"));
                                    break;

                                default:
                                    response = responseBodyResponse.errorBody().string();
                                    Log.d(TAG, "onNextONError: "+response);
                                    biddingView.onSessionExpired(new JSONObject(response).getString("message"));
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
                        biddingView.onSessionExpired();
                        biddingView.onConnectionError(e.getMessage(),"LiveBooking","");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public void callPromoValidation(String codes, String catId, double bidLatitude, double bidLongitude, int paymentType, Context mContext) {

        this.mContext = mContext;
        Log.d(TAG, "callApiService: "+bidLatitude+" lng "+bidLongitude+" cartId "+catId+" payment "+paymentType+" code "+codes);
        Observable<Response<ResponseBody>> responseObservable = lspServices.postPromoCodeValidation(manager.getAUTH(),Constants.selLang
                ,bidLatitude,bidLongitude,catId,paymentType,codes);
        responseObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();

                        Log.d(TAG, "onNextCode: "+code);
                        try
                        {
                            String response;
                            switch (code)
                            {
                                case  Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextPROMOCODE: "+response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject jsonData = jsonObject.getJSONObject("data");
                                    double amount = jsonData.getDouble("discountAmount");

                                    alertProgress.alertPositiveOnclick(mContext, jsonObject.getString("message"), mContext.getResources().getString(R.string.promocode),mContext.getResources().getString(R.string.ok), new DialogInterfaceListner() {
                                        @Override
                                        public void dialogClick(boolean isClicked) {
                                            biddingView.onPromoCodeSuccess(amount,codes);
                                        }
                                    });
                                    biddingView.onHideProgressPromo();
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    biddingView.onLogout(new JSONObject(response).getString("message"));
                                    biddingView.onHideProgressPromo();
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    RefreshToken.onRefreshToken(new JSONObject(response).getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            callPromoValidation(codes, catId, bidLatitude, bidLongitude, paymentType, mContext);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg)
                                        {
                                            biddingView.onLogout(msg);
                                            biddingView.onHideProgressPromo();
                                        }
                                    });
                                    break;
                                default:
                                    response = responseBodyResponse.errorBody().string();
                                    //  biddingView.onError(new JSONObject(response).getString("message"));
                                    biddingView.onHideProgressPromo();
                                    biddingView.onPromoCodeError(new JSONObject(response).getString("message"));

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

                        e.printStackTrace();
                        biddingView.onHideProgressPromo();
                        alertProgress.tryAgain(mContext, e.getMessage() + ", " + mContext.getResources().getString(R.string.pleaseCheckInternet), mContext.getResources().getString(R.string.system_error), new DialogInterfaceListner() {
                            @Override
                            public void dialogClick(boolean isClicked) {
                                if(isClicked)
                                    biddingView.onShowProgressPromo();
                                callPromoValidation(codes, catId, bidLatitude, bidLongitude, paymentType, mContext);
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        biddingView.onHideProgressPromo();
                    }
                });
    }

    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void getAddress(final String auth, Context yourAddressActivity) {
       /* if (yourAddressView!=null) {
            yourAddressView.showProgress();
        }*/
        mContext = yourAddressActivity;
        Observable<Response<ResponseBody>> response = lspServices.getAddress(auth, Constants.selLang);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {


                    @Override
                    public void onNext(Response<ResponseBody> value)
                    {

                        int code  = value.code();

                        try {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:

                                    String response =  value.body().string();

                                    Log.d("TAG", "onNextGEYTADDRES: "+response);
                                    YourAddressResponse yourAddrResponse = gson.fromJson(response,YourAddressResponse.class);

                                    if (yourAddrResponse!=null) {
                                        ArrayList<YourAddrData> yourAddrDataList = yourAddrResponse.getData();
                                        if (yourAddrDataList!=null) {
                                            if (yourAddrDataList.size()>0) {
                                                biddingView.addItems(yourAddrDataList);
                                                biddingView.onHideProgressPromo();
                                            } else {
                                                biddingView.setNoAddressAvailable();
                                                biddingView.onHideProgressPromo();
                                            }
                                        }
                                    }
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    JSONObject errLogout = new JSONObject(value.errorBody().string());
                                    biddingView.onLogout(errLogout.getString("message"));
                                    biddingView.onHideProgressPromo();
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    JSONObject errJson = new JSONObject(value.errorBody().string());
                                    RefreshToken.onRefreshToken(errJson.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            //  biddingView.onHideProgressPromo();
                                            getAddress(auth, yourAddressActivity);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            biddingView.onLogout(msg);
                                            biddingView.onHideProgressPromo();
                                        }
                                    });
                                    break;
                                default:
                                    try {
                                        if (value.errorBody().string()!=null) {
                                            JSONObject errDefault = new JSONObject(value.errorBody().string());
                                            biddingView.setError(errDefault.getString("message"));
                                            //yourAddressView.hideProgress();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        //yourAddressView.hideProgress();
                                    }
                                    biddingView.onHideProgressPromo();
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (NumberFormatException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                        biddingView.onHideProgressPromo();
                        alertProgress.tryAgain(mContext, e.getMessage() + ", " + mContext.getResources().getString(R.string.pleaseCheckInternet), mContext.getResources().getString(R.string.system_error), new DialogInterfaceListner() {
                            @Override
                            public void dialogClick(boolean isClicked) {
                                if(isClicked) {
                                    biddingView.onShowProgressPromo();
                                    getAddress(auth, yourAddressActivity);
                                }
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        //yourAddressView.hideProgress();
                        biddingView.onHideProgressPromo();
                    }
                });

    }

    @Override
    public void deleteAddress(final String auth, final String cardId, final YourAddrData rowItem, int adapterPosition)
    {

        alertProgress.alertPositiveNegativeOnclick(mContext, mContext.getResources().getString(R.string.areYouSureYouWantOTDelete), mContext.getResources().getString(R.string.system_error),mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.cancel),false , (DialogInterfaceListner) isClicked -> {
            if(isClicked)
            {

                biddingView.onShowProgressPromo();
                Observable<Response<ServerResponse>> response = lspServices.deleteAddress(auth,Constants.selLang,cardId);
                response.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<Response<ServerResponse>>() {


                            @Override
                            public void onNext(Response<ServerResponse> value) {
                                Log.e("DelAddr", "Delete Server Req URL :: " + value.raw().request().url() + " code : " + value.code());
                                try {
                                    switch (value.code()) {
                                        case Constants.SUCCESS_RESPONSE:
                                            ServerResponse serverResponse = value.body();
                                            if (serverResponse != null) {
                                                String message = serverResponse.getMessage();
                                                Log.e("DEL_ADDDR", message);
                                                biddingView.refreshItems(rowItem, adapterPosition);
                                                biddingView.onHideProgressPromo();
                                            }
                                            break;
                                        case Constants.SESSION_LOGOUT:
                                            JSONObject errLogout = new JSONObject(value.errorBody().string());
                                            biddingView.onLogout(errLogout.getString("message"));
                                            biddingView.onHideProgressPromo();
                                            break;
                                        case Constants.SESSION_EXPIRED:
                                            JSONObject errJson = new JSONObject(value.errorBody().string());
                                            RefreshToken.onRefreshToken(errJson.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                                @Override
                                                public void onSuccessRefreshToken(String newToken) {
                                                    biddingView.onHideProgressPromo();
                                                    deleteAddress(auth, cardId, rowItem, adapterPosition);
                                                }

                                                @Override
                                                public void onFailureRefreshToken() {

                                                }

                                                @Override
                                                public void sessionExpired(String msg) {
                                                    biddingView.onLogout(msg);
                                                    biddingView.onHideProgressPromo();
                                                }
                                            });
                                            break;
                                        default:
                                            JSONObject errDefault = new JSONObject(value.errorBody().string());
                                            biddingView.onError(errDefault.getString("message"));
                                            biddingView.onHideProgressPromo();
                                            break;
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(Throwable e)
                            {
                                e.printStackTrace();
                                biddingView.onHideProgressPromo();
                                alertProgress.tryAgain(mContext, e.getMessage() + ", " + mContext.getResources().getString(R.string.pleaseCheckInternet), mContext.getResources().getString(R.string.system_error), new DialogInterfaceListner() {
                                    @Override
                                    public void dialogClick(boolean isClicked) {
                                        if(isClicked)
                                        {
                                            biddingView.onShowProgressPromo();
                                            deleteAddress(auth, cardId, rowItem, adapterPosition);
                                        }

                                    }
                                });
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });


    }
    @Override
    public void onItemClicked(int adapterPosition)
    {
        biddingView.onAddressSelected(adapterPosition);
    }


    @Override
    public void checkPermission(Context mContext, Activity mActivity, HandlePictureEvents handlePicEvent) {
        if(permissionsManager.areRuntimePermissionsRequired())
        {
            if(permissionsManager.areCameraFilePermissionGranted(mContext))
                permissionsManager.requestCameraPermissions(mActivity);
            else
                selectImage(handlePicEvent);
        }else
            selectImage(handlePicEvent);
    }
    @Override
    public void selectImage(HandlePictureEvents handlePicEvent) {

        handlePicEvent.openDialog();
    }

    @Override
    public long getStartOfDayInMillisToday() {
        Date date =  new Date();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);//.getTime()
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();

    }

    @Override
    public void onNowLaterSelected(boolean isNow, long selectedScheduledDateTime, int selectedDuration) {

        if(isNow)
        {
            Constants.bookingType = 1;
            Constants.scheduledDate = "";
            Constants.scheduledTime = selectedDuration;
            Constants.onRepeatEnd = 0;
            Constants.onRepeatDays = new ArrayList<>();

        }else
        {

            Constants.bookingType = 2;
            Constants.scheduledDate = selectedScheduledDateTime+"";
            Constants.scheduledTime = selectedDuration;
            Constants.onRepeatEnd = 0;
            Constants.onRepeatDays = new ArrayList<>();
        }
    }

    @Override
    public void onRepeatSelected(long selectedScheduledDateTime, long selectedEndDate, int selectedDuration, ArrayList<String> repeatBooking) {
        Constants.bookingType = 3;
        Constants.scheduledDate = selectedScheduledDateTime+"";
        Constants.scheduledTime = selectedDuration;
        Constants.onRepeatEnd = selectedEndDate;
        Constants.onRepeatDays = repeatBooking;
    }
}
