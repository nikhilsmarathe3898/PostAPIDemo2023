package com.localgenie.confirmbookactivity;


import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.utility.RefreshToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import com.pojo.CartModifiedData;
import retrofit2.Response;

/**
 * <h>ConfirmBookingContractImpl</h>
 * Created by Ali on 2/12/2018.
 */

public class ConfirmBookingContractImpl implements ConfirmBookingContract.ContractPresenter
{
    private String TAG = ConfirmBookingContractImpl.class.getSimpleName();

    @Inject ConfirmBookingContract.ContractView contractView;

    @Inject
    SessionManagerImpl manager;
    @Inject
    LSPServices lspServices;

    @Inject
    Gson gson;

    private boolean isFirstTrue = true;

    @Inject
    public ConfirmBookingContractImpl()
    {

    }
    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }


    @Override
    public void onGetCartId()
    {
        Log.d(TAG, "onGetCartId: "+manager.getAUTH()+" "+
                Constants.selLang+" "+Constants.catId+" "+Constants.proId+" "+Constants.jobType);
        Observable<Response<ResponseBody>> observable = lspServices.getSubCart(manager.getAUTH(),
                Constants.selLang,Constants.catId,Constants.proId,Constants.jobType,Constants.bookingType);
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
                        Log.d(TAG, "onNextConfirmGetCart: "+code);
                        String response;JSONObject jsonObject;

                        try {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:

                                    response = responseBodyResponse.body().string();

                                    Log.d(TAG, "onNextSelectedService: "+response);
                                    CartModifiedData cartmodification = gson.fromJson(response,CartModifiedData.class);
                                    Constants.serviceSelected = cartmodification.getData().getServiceType();
                                    if(Constants.serviceSelected == 2 && cartmodification.getData().getTotalQuntity()==0)
                                        contractView.onHourly();
                                    else
                                        contractView.onCartModification(cartmodification.getData());

                                    jsonObject = new JSONObject(response);
                                    String cartId = jsonObject.getJSONObject("data").getString("_id");
                                    contractView.onSuccessCartId(cartId);
                                    break;

                                case Constants.SESSION_LOGOUT:

                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    contractView.onLogout(jsonObject.getString("message"));

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    RefreshToken.onRefreshToken(jsonObject.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            onGetCartId();

                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            contractView.onLogout(msg);
                                        }
                                    });
                                    break;
                                case 409:

                                    response = responseBodyResponse.errorBody().string();
                                    Log.d(TAG, "onNext: "+response);
                                    JSONObject jsonObjecterrCart = new JSONObject(response);
                                    contractView.onAlreadyCartPresent(jsonObjecterrCart.getString("message"),true);
                                    break;
                                case 416:
                                    response = responseBodyResponse.errorBody().string();
                                    contractView.onHourly();
                                    break;
                                default:
                                    contractView.onHidePro();

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        contractView.onConnectionError(e.getMessage(),"CartId","");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void lastDues() {

        Observable<Response<ResponseBody>>observable = lspServices.lastDues(manager.getAUTH(),
                Constants.selLang);

        Log.d(TAG, "lastDues: "+manager.getAUTH()+" "+
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
                                    contractView.onSessionExpired();
                                    String msg = jsonObject.getString("message");
                                    String addLine1 =  jsonObject.getJSONObject("data").getString("addLine1");
                                    long timeStamp = jsonObject.getJSONObject("data").getLong("bookingRequestedAt");
                                    Date date = new Date(timeStamp * 1000L);

                                    contractView.onDuesFound(msg,addLine1,Utility.getFormattedDate(date));
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    contractView.onSessionExpired();
                                    contractView.onLogout(new JSONObject(response).getString("message"));
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);

                                    RefreshToken.onRefreshToken(jsonObject.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            lastDues();

                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            contractView.onLogout(msg);
                                        }
                                    });

                                    break;
                                case Constants.SESSION_NoDues:
                                    contractView.noDuesFoundLiveBooking();
                                    break;
                                    default:
                                        response = responseBodyResponse.errorBody().string();
                                        contractView.onError(new JSONObject(response).getString("message"));
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

                        contractView.onConnectionError(e.getMessage(),"LastDues","");
                        contractView.onSessionExpired();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void serverTime()
    {
        Log.d(TAG, "serverTime: ");
        Observable<Response<ResponseBody>>observable = lspServices.onTogetServerTime(Constants.selLang);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        try
                        {
                            if(code == Constants.SUCCESS_RESPONSE)
                                Constants.serverTime  = new JSONObject(responseBodyResponse.body().string()).getLong("data");
                            contractView.CallLiveBook();

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
    public void callPromoCodeApi(double bookingLat, double bookingLng, int paymentType, String cartId, String proCode)
    {

        Observable<Response<ResponseBody>> responseObservable = lspServices.postPromoCodeValidation(manager.getAUTH(),Constants.selLang
                ,bookingLat,bookingLng,cartId,paymentType,proCode);
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

                                    contractView.promoCode(amount,proCode);

                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    contractView.onSessionExpired();
                                    contractView.onLogout(new JSONObject(response).getString("message"));

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    RefreshToken.onRefreshToken(new JSONObject(response).getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            callPromoCodeApi(bookingLat,bookingLng,paymentType,cartId,proCode);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            contractView.onLogout(msg);

                                        }
                                    });
                                    break;
                                default:
                                    response = responseBodyResponse.errorBody().string();
                                    contractView.onError(new JSONObject(response).getString("message"));
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
    public void setAddressWithImage(TextView tvConfirmLocation, String bookingAddress)
    {
        tvConfirmLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        /*SpannableString ss1=  new SpannableString(bookingAddres);
        ss1.setSpan(new RelativeSizeSpan(1.1f), 0, bookingAddres.length()-bookingAddress.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, bookingAddres.length()-bookingAddress.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        tvConfirmLocation.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_arrow, 0);
        tvConfirmLocation.setText(bookingAddress);
        tvConfirmLocation.setPadding(120, 0, 0, 0);
       /* LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,0,0,0);
        tvConfirmLocation.setLayoutParams(params);*/

    }

    @Override
    public void onLiveBookingService(final int paymentType, boolean isWalletSelected, final String jobDescription, final String promoCode, final String cardId, final String cartId,
                                     final double bookingLat, final double bookingLng,String address)
    {
        int walletSelected = 0;
        if(isWalletSelected)
            walletSelected = 1;
        Log.d(TAG, "onLiveBookingServiceModel: "+Constants.jobType+" service "+Constants.serviceType+"\ntype "+
                Constants.bookingType+"\npayment "+paymentType+"\naddress "+address+"\nproId "+Constants.proId+"\nCatId "+
                Constants.catId+"\njobDesc "+jobDescription+"\npromoCode "+promoCode+"\n CartId "+cartId
                +"\ncardId "+cardId+"\nscheduledDate "+Constants.scheduledDate+"\nSceduledTime "+Constants.scheduledTime+"\nWallet "+walletSelected
        +"\n"+" lat "+bookingLat+" long: "+bookingLng);

        Observable<Response<ResponseBody>> observable =  lspServices.onLiveBooking(manager.getAUTH(),
                Constants.selLang,Constants.jobType,Constants.serviceType,Constants.bookingType,paymentType, walletSelected,"",
                "",address,bookingLat,bookingLng,Constants.proId, Constants.catId,cartId,
                jobDescription, promoCode,cardId,Constants.scheduledDate, Constants.scheduledTime,Constants.onRepeatEnd,Constants.onRepeatDays,
                Utility.dateInTwentyFour(Constants.serverTime),0.00,Constants.jsonArray);

        observable.subscribeOn(Schedulers.newThread())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {
                        int code = responseBodyResponse.code();
                        Log.d(TAG, "onNextLiveResponse: "+code);
                        try {
                            String response;
                            JSONObject jsonObject;
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextLiveResponse: "+code+" response "+response);
                                    contractView.onHideProgress();
                                    contractView.onSuccessBooking();
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    contractView.onSessionExpired();
                                    contractView.onLogout(jsonObject.getString("message"));

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    RefreshToken.onRefreshToken(jsonObject.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onLiveBookingService(paymentType, isWalletSelected, jobDescription, promoCode, cardId, cartId,
                                                    bookingLat, bookingLng,address);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg)
                                        {

                                            contractView.onSessionExpired();
                                            contractView.onLogout(msg);
                                        }
                                    });
                                    break;

                                case 416:
                                    response = responseBodyResponse.errorBody().string();
                                    new JSONObject(response).getString("message");
                                    contractView.onError(new JSONObject(response).getString("message"));
                                    break;
                                case 400:
                                    response = responseBodyResponse.errorBody().string();
                                    contractView.onError(new JSONObject(response).getString("message"));
                                    break;

                                case 201:
                                    response = responseBodyResponse.body().string();
                                    contractView.onError(new JSONObject(response).getString("message"));
                                    break;

                                default:
                                    response = responseBodyResponse.errorBody().string();
                                    contractView.onError(new JSONObject(response).getString("message"));
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
                        contractView.onSessionExpired();
                        contractView.onConnectionError(e.getMessage(),"LiveBooking","");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onAddSubCartModifyData(String catId, String serviceId, int action, int quantityInHr)
    {

        Log.d(TAG, "onAddSubCartModifyDataHR: "+quantityInHr+" isFIRST "+isFirstTrue+" SERVICEID "+serviceId
        +" SERVICESEL "+Constants.serviceSelected);

        int quantity = 0;
        if(serviceId.equals("1") && Constants.serviceSelected ==2)
        {
            if(!isFirstTrue)
                quantity = 1;
            else {
                    quantity = quantityInHr;
            }
        }else
            quantity = 1;
        Observable<Response<ResponseBody>> observable = lspServices.onCatModification(manager.getAUTH(),
                Constants.selLang,Constants.serviceSelected,Constants.bookingType,catId,serviceId,quantity,action,
                Constants.proId,Constants.jobType);

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
                        Log.d(TAG, "onNextConfirmModifyCart: "+code);
                        String response;
                        try {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:

                                     response = responseBodyResponse.body().string();
                                     isFirstTrue = false;
                                    Log.d(TAG, "onNextAddToCart: "+response );
                                    CartModifiedData cartModifiedData = gson.fromJson(response,CartModifiedData.class);
                                    contractView.onCartModification(cartModifiedData.getData());
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    RefreshToken.onRefreshToken(new JSONObject(response).getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            onAddSubCartModifyData(catId,serviceId,action, quantityInHr);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

                                        }
                                    });
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();

                                    contractView.onSessionExpired();
                                    contractView.onLogout(new JSONObject(response).getString("message"));
                                    break;
                                    default:
                                        response = responseBodyResponse.errorBody().string();
                                        contractView.onError(new JSONObject(response).getString("message"));
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
                        String hourly;
                        if("1".equals(serviceId))
                            hourly = "hourly";
                        else
                            hourly = "";
                        contractView.onConnectionError(e.getMessage(),"AddCart",hourly);
                        contractView.onHidePro();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void timeMethod(TextView tvInConfirmBookingTypeDesc, long fromTime) {

            String time = "";
            try {

                Date date = new Date(fromTime * 1000L);
                   SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
               // SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                sdf.setTimeZone(Utility.getTimeZone());
                tvInConfirmBookingTypeDesc.setText(sdf.format(date));

            } catch (Exception e) {
                Log.d("TAG", "timeMethodException: " + e.toString());
            }

        }

}
