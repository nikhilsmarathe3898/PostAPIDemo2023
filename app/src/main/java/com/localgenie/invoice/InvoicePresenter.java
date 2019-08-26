package com.localgenie.invoice;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.HandlePictureEvents;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.UploadAmazonS3;
import com.localgenie.utilities.Utility;
import com.pojo.BookingDetailsPojo;
import com.pojo.ErrorHandel;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;
import com.utility.RefreshToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Ali on 10/18/2018.
 */

public class InvoicePresenter implements InvoiceModel.InvoicePre
{

    private String TAG = InvoiceActivity.class.getSimpleName();
    @Inject
    SessionManagerImpl manager;

    @Inject
    LSPServices lspServices;

    @Inject
    AlertProgress alertProgress;

    @Inject InvoiceModel.InvoiceView invoiceView;
    @Inject
    Gson gson;

    @Inject
    public InvoicePresenter() {
    }

    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void getBookingDetailsById(long bId)
    {
        Observable<Response<ResponseBody>> observable = lspServices.onToGetBookingDetails(manager.getAUTH()
                , Constants.selLang,bId);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        Log.d(TAG, "onNextJobDetails: "+code);
                        String response;
                        JSONObject jsonObject;
                        try {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    assert responseBodyResponse.body() != null;
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextJobDetailsRes: "+response);
                                    BookingDetailsPojo bData = gson.fromJson(response,BookingDetailsPojo.class);
                                    bData.getData().getAccounting();
                                    invoiceView.onProviderDetails(bData.getData().getCurrencySymbol()
                                            ,bData.getData().getProviderDetail(),bData.getData().getCategoryName()
                                            ,bData.getData().getBookingRequestedFor(),bData.getData().getQuestionAndAnswer());

                                    invoiceView.onAccounting(bData.getData().getAccounting());
                                    invoiceView.onHideProgress();

                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    invoiceView.onHideProgress();
                                    invoiceView.onLogout(jsonObject.getString("message"));
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response,ErrorHandel.class);

                                    // String
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            manager.setAUTH(newToken);
                                            getBookingDetailsById(bId);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

                                            invoiceView.onHideProgress();
                                            invoiceView.onLogout(msg);
                                        }
                                    });
                                    break;
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        invoiceView.onHideProgress();
                        invoiceView.networkUnReachable(e.getMessage(),true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void timeMethod(TextView tvDate,TextView tvTime, long bookingRequestedFor)
    {
        try {


            Log.d("TAGTIME", " expireTime " + bookingRequestedFor);
            Date date = new Date(bookingRequestedFor * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
            sdf.setTimeZone(Utility.getTimeZone());
            String formattedDate = sdf.format(date);

            String formattedDated = Utility.getFormattedDate(date);
            String dat1[] = formattedDated.split(",");
            String splitDate = dat1[0]+","+dat1[1];

            // String fullDate = splitDate[1]+" "+splitDate[2]+" "+splitDate[3];
            //   tbServiceAvailable.setText(dat1[0]);
            tvDate.setText(splitDate);
            tvTime.setText(dat1[2]);

        } catch (Exception e) {
            Log.d("TAG", "timeMethodException: " + e.toString());
        }
    }

    @Override
    public void updateStatus(JSONArray jsonArray, String signatureUrl, HandlePictureEvents handlePicEvent, File signatureFile, long bId, String promoCode) {


        String[] strArr = new String[jsonArray.length()];

        Observable<Response<ResponseBody>> observable = lspServices.bookingstatus(manager.getAUTH()
                , Constants.selLang,bId,signatureUrl, strArr);//,promoCode
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {
                        Log.d(TAG, "onNext: "+signatureUrl);
                        Log.d(TAG, "onNext: "+responseBodyResponse.code());
                        try {
                            invoiceView.onHideProgress();
                            if(responseBodyResponse.code() == 200)
                            {
                               // invoiceView.viewRatingScreen();

                                raisedInvoice(bId);
                            }else
                            {
                                String response = null;

                                response = responseBodyResponse.errorBody().string();

                                JSONObject jsonObject = new JSONObject(response);
                                invoiceView.onError(jsonObject.getString("message"));
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        invoiceView.onHideProgress();
                        invoiceView.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void raisedInvoice(long bId) {
        Observable<Response<ResponseBody>> observable = lspServices.onToGetInvoiceDetails(manager.getAUTH()
                , Constants.selLang,bId);//,promoCode
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {
                        Log.d(TAG, "onNext: "+responseBodyResponse);
                        Log.d(TAG, "onNext: "+responseBodyResponse.code());
                        try {
                            invoiceView.onHideProgress();
                            if(responseBodyResponse.code() == 200)
                            {
                                invoiceView.viewRatingScreen();

                            }else
                            {
                                String response = null;

                                response = responseBodyResponse.errorBody().string();

                                JSONObject jsonObject = new JSONObject(response);
                                invoiceView.onError(jsonObject.getString("message"));
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        invoiceView.onHideProgress();
                        invoiceView.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    Dialog indialog;
    Activity mActivity;
    @Override
    public void showPromoCodeDialog(Activity invoiceActivity, long bId)
    {
        mActivity = invoiceActivity;
        TextView etPromoCode,tvApply,youhaveAPRomoCode;
        ImageView crossButton;
        AppTypeface appTypeface;
        appTypeface = AppTypeface.getInstance(invoiceActivity);
        indialog = new Dialog(invoiceActivity);
        indialog.setCanceledOnTouchOutside(true);
        indialog.setCancelable(true);
        indialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        indialog.setContentView(R.layout.promo_code_dialog);
        indialog.show();
        etPromoCode = indialog.findViewById(R.id.etPromoCode);
        tvApply = indialog.findViewById(R.id.tvApply);
        youhaveAPRomoCode = indialog.findViewById(R.id.tvyouHavePromo);
        crossButton = indialog.findViewById(R.id.crossButton);
        etPromoCode.setTypeface(appTypeface.getHind_regular());
        tvApply.setTypeface(appTypeface.getHind_medium());
        youhaveAPRomoCode.setTypeface(appTypeface.getHind_semiBold());

        tvApply.setOnClickListener(view -> {
            if(!"".equals(etPromoCode.getText().toString().trim()))
            {
                invoiceView.alertDialogMethod(mActivity.getResources().getString(R.string.wait));
                invoiceView.onShowProgress();
                //Removed as promocode was not added
                //callPromoCode(bId,etPromoCode.getText().toString().trim());
            }else
                alertProgress.alertinfo(mActivity,mActivity.getString(R.string.pleaseEndterPromoCode));
        });
        crossButton.setOnClickListener(view -> indialog.dismiss());
    }

    private void callPromoCode(long bId, String trim)
    {
        Log.d(TAG, "callPromoCode: "+bId+" trim "+trim);
        /*Observable<Response<ResponseBody>>observable = lspServices.postPromoCodeValidation(manager.getAUTH()
                ,Constants.selLang,bId,trim);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {
                        int code = responseBodyResponse.code();

                        Log.d(TAG, "onNextCodePr: "+code);
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

                                    alertProgress.alertPositiveOnclick(mActivity, jsonObject.getString("message"), mActivity.getResources().getString(R.string.promocode), new DialogInterfaceListner() {
                                        @Override
                                        public void dialogClick(boolean isClicked) {
                                            invoiceView.callOnResultActivity(amount,trim);
                                            if(indialog!=null && indialog.isShowing())
                                                indialog.dismiss();
                                        }
                                    });
                                    invoiceView.onHideProgress();

                                    break;
                                case Constants.SESSION_LOGOUT:
                                    Utility.setMAnagerWithBID(mActivity,manager);
                                    invoiceView.onHideProgress();
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    RefreshToken.onRefreshToken(new JSONObject(response).getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            callPromoCode(bId,trim);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg)
                                        {
                                            Utility.setMAnagerWithBID(mActivity,manager);
                                            invoiceView.onHideProgress();
                                            if(indialog!=null && indialog.isShowing())
                                                indialog.dismiss();
                                        }
                                    });
                                    break;
                                default:
                                    response = responseBodyResponse.errorBody().string();
                                    alertProgress.alertinfo(mActivity,new JSONObject(response).getString("message"));
                                    invoiceView.onHideProgress();
                                  *//*  if(indialog!=null && indialog.isShowing())
                                        indialog.dismiss();*//*
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
                        alertProgress.alertinfo(mActivity,e.getMessage());
                        invoiceView.onHideProgress();

                        if(indialog!=null && indialog.isShowing())
                            indialog.dismiss();
                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
    }
}
