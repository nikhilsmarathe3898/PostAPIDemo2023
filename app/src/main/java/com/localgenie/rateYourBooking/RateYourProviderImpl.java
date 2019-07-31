package com.localgenie.rateYourBooking;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.pojo.AdditionalService;
import com.pojo.BookingAccounting;
import com.pojo.CartInfo;
import com.pojo.ErrorHandel;
import com.pojo.InvoiceDetails;
import com.utility.RefreshToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import adapters.CourierFlowJobPhotosAdapter;
import adapters.SelectedService;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * <h>RateYourProviderImpl</h>
 * Created by Ali on 2/22/2018.
 */

public class RateYourProviderImpl implements RateYourProviderContract.Presenter {
    @Inject
    LSPServices lspServices;
    @Inject
    SessionManagerImpl manager;

    @Inject
    Gson gson;

    @Inject
    RateYourProviderContract.ViewContract viewContract;
    private Dialog indialog;

    @Inject
    public RateYourProviderImpl() {
    }

    public static String formatHoursAndMinutes(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void onInvoiceDetailsCalled(final long bId) {
        Log.d("SHIJEN ", "onInvoiceDetailsCalled: " + manager.getAUTH());
        Observable<Response<ResponseBody>> observable = lspServices.onToGetInvoiceDetails(manager.getAUTH(),
                Constants.selLang, bId);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        Log.d("TAG", "onNextInvoiceCode: " + code);
                        String response;
                        JSONObject jsonObject;
                        try {
                            switch (code) {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d("TAG", "onNextInvoice: " + response);

                                    InvoiceDetails invoiceDetails = new Gson().fromJson(response, InvoiceDetails.class);

                                    viewContract.onGetInvoiceDetails(invoiceDetails.getData());
                                    viewContract.onHideProgress();
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            manager.setAUTH(newToken);
                                            onInvoiceDetailsCalled(bId);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

                                            viewContract.onLogout(msg);
                                            viewContract.onHideProgress();
                                        }
                                    });
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    viewContract.onLogout(jsonObject.getString("message"));
                                    viewContract.onHideProgress();
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            viewContract.onHideProgress();
                            viewContract.onError(e.getMessage());
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
    public void onUpdateReview(final long bId, final ArrayList<InvoiceDetails.CustomerRating> stringList, final String reviewMsg) {

        JSONArray jsonArray = new JSONArray();
        for (InvoiceDetails.CustomerRating strList : stringList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", strList.getName());
                jsonObject.put("_id", strList.get_id());
                jsonObject.put("rating", strList.getRatings());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);
        }


        Observable<Response<ResponseBody>> observable = lspServices.onUpdateReview(manager.getAUTH(),
                Constants.selLang, bId, jsonArray, reviewMsg);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {


                        int code = responseBodyResponse.code();
                        Log.d("TAG", "onNextUpdateReview: " + code);
                        String response;
                        JSONObject jsonObject;
                        try {
                            switch (code) {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d("TAG", "onNextInvoice: " + response);
                                    viewContract.onHideProgress();
                                    viewContract.onRateProviderSuccess();
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            manager.setAUTH(newToken);
                                            onUpdateReview(bId, stringList, reviewMsg);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

                                            viewContract.onLogout(msg);
                                            viewContract.onHideProgress();
                                        }
                                    });
                                    // viewContract.onHideProgress();
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    viewContract.onHideProgress();
                                    viewContract.onLogout(jsonObject.getString("message"));

                                    break;
                                default:
                                    response = responseBodyResponse.errorBody().string();
                                    viewContract.onHideProgress();
                                    viewContract.onError(new JSONObject(response).getString("message"));

                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            viewContract.onHideProgress();
                            viewContract.onError(e.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        viewContract.onHideProgress();
                        viewContract.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void timeMethod(TextView tbServiceAvailable, long bookingRequestedFor) {
        try {


            Log.d("TAGTIME", " expireTime " + bookingRequestedFor);
            Date date = new Date(bookingRequestedFor * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
            sdf.setTimeZone(Utility.getTimeZone());
            String formattedDate = sdf.format(date);

            String formattedDated = Utility.getFormattedDate(date);
            String dat1[] = formattedDated.split(",");
            String splitDate[] = dat1[0].split(" ");

            String fullDate = splitDate[1] + " " + splitDate[2] + " " + splitDate[3];
            //   tbServiceAvailable.setText(dat1[0]);
            tbServiceAvailable.setText(fullDate);

        } catch (Exception e) {
            Log.d("TAG", "timeMethodException: " + e.toString());
        }
    }

    @Override
    public void getStringList() {
        ArrayList<String> stringList = new ArrayList();
        stringList.add("service");
        stringList.add("quality");
        stringList.add("behaviour");
        stringList.add("on time");
        stringList.add("other");
        stringList.add("cool");
        stringList.add("awesome");
        // viewContract.onGetStarList(stringList);
    }


/*
    public static String formatHoursAndMinutes(int totalMinutes) {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (totalMinutes / 60) + ":" + minutes;
    }
*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void openDialog(RateYourBooking rateYourBooking, int callType, AppTypeface appTypeface, CartInfo cartInfo, String signURL, BookingAccounting accounting, String addressLine,
                           ArrayList<AdditionalService> additionalServices, int bookingModel, String currencySymbol, String categoryName, ArrayList<String> pickUpList, ArrayList<String> dropImageList, Context context) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rateYourBooking);
        TextView tvInvoiceDialog, tvInvoiceDialogDismiss, tvInvGigTimeFee,
                tvInvGigTimeFeeAmt, tvInvDiscount, tvInvDiscountAmount, tvInvoiceTotal, tvInvoicePayDialog,
                tvInvoiceTotalAmt, tvVisitAmount, tvLocation, tvReceiptDetailsHead, tvTravelLabel, tvTravelAmount,
                tvDropPhotos, tvPickupPhotos, tvJobDialog, tvInvCard, tvInvAmount;
        ImageView ivInvoiceSignature;
        RelativeLayout rlAdditionalService, rldiscount, rlVisitFee, rltravelLayout, rlInvGigTimeFee, rlpayment;
        RecyclerView rv_jobDropPhotos, rv_jobPickUpPhotos;
        LinearLayout containerAdditional;
        Log.d("abc", "openDialog: " + accounting.getLast4());
        indialog = new Dialog(rateYourBooking);
        indialog.setCanceledOnTouchOutside(true);
        indialog.setCancelable(true);
        indialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        indialog.setContentView(R.layout.popup_invoice_fee_breakdown);
        indialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        indialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tvInvoiceDialog = indialog.findViewById(R.id.tvInvoiceDialog);
        tvInvAmount = indialog.findViewById(R.id.tvInvAmount);
        tvInvCard = indialog.findViewById(R.id.tvInvCard);
        rlpayment = indialog.findViewById(R.id.rlpayment);
        tvInvoiceDialogDismiss = indialog.findViewById(R.id.tvInvoiceDialogDismiss);
        tvInvGigTimeFee = indialog.findViewById(R.id.tvInvGigTimeFee);
        tvInvGigTimeFeeAmt = indialog.findViewById(R.id.tvInvGigTimeFeeAmt);
        tvInvDiscount = indialog.findViewById(R.id.tvInvDiscount);
        tvInvDiscountAmount = indialog.findViewById(R.id.tvInvDiscountAmount);
        tvInvoiceTotal = indialog.findViewById(R.id.tvInvoiceTotal);
        tvInvoiceTotalAmt = indialog.findViewById(R.id.tvInvoiceTotalAmt);
        ivInvoiceSignature = indialog.findViewById(R.id.ivInvoiceSignature);
        tvVisitAmount = indialog.findViewById(R.id.tvVisitAmount);
        tvLocation = indialog.findViewById(R.id.tvLocation);
        tvReceiptDetailsHead = indialog.findViewById(R.id.tvReceiptDetailsHead);
        RecyclerView recyclerView = indialog.findViewById(R.id.llLiveFee);
        rldiscount = indialog.findViewById(R.id.rldiscount);
        rlVisitFee = indialog.findViewById(R.id.rlVisitFee);
        rltravelLayout = indialog.findViewById(R.id.rltravelLayout);
        tvTravelLabel = indialog.findViewById(R.id.tvTravelLabel);
        tvTravelAmount = indialog.findViewById(R.id.tvTravelAmount);
        rlAdditionalService = indialog.findViewById(R.id.rlAdditionalService);
        containerAdditional = indialog.findViewById(R.id.containerAdditional);
        rlInvGigTimeFee = indialog.findViewById(R.id.rlInvGigTimeFee);
        rv_jobPickUpPhotos = indialog.findViewById(R.id.rv_jobPickUpPhotos);
        rv_jobDropPhotos = indialog.findViewById(R.id.rv_jobDropPhotos);
        tvPickupPhotos = indialog.findViewById(R.id.tvPickupPhotos);
        tvDropPhotos = indialog.findViewById(R.id.tvDropPhotos);
        tvJobDialog = indialog.findViewById(R.id.tvJobDialog);
        tvInvoicePayDialog = indialog.findViewById(R.id.tvInvoicePayDialog);

        if (pickUpList != null && pickUpList.size() > 0) {
            tvPickupPhotos.setVisibility(View.VISIBLE);
        }
        if (dropImageList != null && dropImageList.size() > 0) {
            tvDropPhotos.setVisibility(View.VISIBLE);
        }
        rv_jobPickUpPhotos.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_jobPickUpPhotos.setAdapter(new CourierFlowJobPhotosAdapter(pickUpList, null, false));

        rv_jobDropPhotos.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_jobDropPhotos.setAdapter(new CourierFlowJobPhotosAdapter(dropImageList, null, false));

        if (callType == 3) {
            tvJobDialog.setText("TIME SLOTS");
            tvLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            tvLocation.setText(formatHoursAndMinutes(accounting.getTotalActualJobTimeMinutes()));
            Log.d("abc", "openDialog: " + formatHoursAndMinutes(accounting.getTotalActualJobTimeMinutes()));
            tvInvCard.setText("Card ending by " + "" + accounting.getLast4());
            if (accounting.getDiscount() > 0) {
                Utility.setAmtOnRecept(accounting.getVisitFee(), tvInvAmount, currencySymbol);
                // rlVisitFee.setVisibility(View.VISIBLE);
            }

            Utility.setAmtOnRecept(accounting.getTotal(), tvInvAmount, currencySymbol);

        } else {
            tvJobDialog.setText("JOB LOCATION");
            tvLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_place_black_24dp, 0, 0, 0);
            tvLocation.setText(addressLine);

        }
        if (bookingModel != 3) {
            try {
                SelectedService selectedService = new SelectedService(rateYourBooking, false);
                ArrayList<CartInfo.CheckOutItem> cartInfoItem = cartInfo.getCheckOutItem();
                Constants.bookingcurrencySymbol = currencySymbol;
                recyclerView.setLayoutManager(linearLayoutManager);
                selectedService.onCheckOutItem(cartInfoItem);
                if (callType == 1 || callType == 3) {
                    selectedService.onInCallValue(1);
                }
                recyclerView.setAdapter(selectedService);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utility.setAmtOnRecept(accounting.getBidPrice(), tvInvGigTimeFeeAmt, currencySymbol);
            rlInvGigTimeFee.setVisibility(View.VISIBLE);
            tvInvGigTimeFee.setText(categoryName);
        }

        tvReceiptDetailsHead.setTypeface(appTypeface.getHind_regular());
        tvVisitAmount.setTypeface(appTypeface.getHind_regular());
        tvInvoiceDialog.setTypeface(appTypeface.getHind_bold());
        tvJobDialog.setTypeface(appTypeface.getHind_bold());
        tvInvoiceDialogDismiss.setTypeface(appTypeface.getHind_bold());
        tvInvoicePayDialog.setTypeface(appTypeface.getHind_bold());
        tvInvoiceTotal.setTypeface(appTypeface.getHind_bold());
        tvInvoiceTotalAmt.setTypeface(appTypeface.getHind_bold());
        tvInvDiscountAmount.setTypeface(appTypeface.getHind_regular());
        tvInvDiscount.setTypeface(appTypeface.getHind_regular());
        tvInvGigTimeFee.setTypeface(appTypeface.getHind_regular());
        tvInvGigTimeFeeAmt.setTypeface(appTypeface.getHind_regular());
        tvLocation.setTypeface(appTypeface.getHind_regular());
        Log.d("TAG", "openDialog: " + signURL);
        if (!signURL.equals("")) {
            Glide.with(rateYourBooking)
                    .load(signURL)
                    .into(ivInvoiceSignature);
        }

        tvInvoiceDialogDismiss.setOnClickListener(view -> indialog.dismiss());
        Utility.setAmtOnRecept(accounting.getTotal(), tvInvoiceTotalAmt, currencySymbol);
        if (accounting.getDiscount() > 0)
            Utility.setAmtOnRecept(accounting.getDiscount(), tvInvDiscountAmount, currencySymbol);
        else
            rldiscount.setVisibility(View.GONE);
        if (accounting.getDiscount() > 0) {
            Utility.setAmtOnRecept(accounting.getVisitFee(), tvVisitAmount, currencySymbol);
            rlVisitFee.setVisibility(View.VISIBLE);
        } else
            rlVisitFee.setVisibility(View.GONE);
        if (accounting.getTravelFee() > 0) {
            Utility.setAmtOnRecept(accounting.getTravelFee(), tvTravelAmount, currencySymbol);
            rltravelLayout.setVisibility(View.VISIBLE);
        }
        if (additionalServices.size() > 0) {
            rlAdditionalService.setVisibility(View.VISIBLE);
            onAdditionalServiceSet(appTypeface, rateYourBooking, containerAdditional, additionalServices, rlAdditionalService);
        }

        /*  private void onAdditionalServiceSet()
    {

    }*/

        indialog.show();
    }

    private void onAdditionalServiceSet(AppTypeface appTypeface, RateYourBooking rateYourBooking, LinearLayout containerAdditional, ArrayList<AdditionalService> additionalServices, RelativeLayout rlAdditionalService) {
        for (int i = 0; i < additionalServices.size(); i++) {
            TextView tvAddtionName, tvAddtionPrice;
            View view = LayoutInflater.from(rateYourBooking).inflate(R.layout.additiona_service, rlAdditionalService, false);
            containerAdditional.addView(view);

            tvAddtionPrice = view.findViewById(R.id.tvAddtionPrice);
            tvAddtionName = view.findViewById(R.id.tvAddtionName);
            tvAddtionPrice.setTypeface(appTypeface.getHind_light());
            tvAddtionName.setTypeface(appTypeface.getHind_light());
            Log.d("Shijen", "onAdditionalServiceSet: " + additionalServices.get(i).getServiceName());
            tvAddtionName.setText(additionalServices.get(i).getServiceName());
            Utility.setAmtOnRecept(additionalServices.get(i).getPrice(), tvAddtionPrice, Constants.bookingcurrencySymbol);
        }
    }

    @Override
    public void onAddToFav(String providerId, String catId) {
        Observable<Response<ResponseBody>> responseObservable = lspServices.addTOFav(manager.getAUTH(), Constants.selLang, catId,
                providerId);

        responseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {
                        int code = responseBodyResponse.code();
                        JSONObject jsonObject;
                        String response;
                        try {


                            switch (code) {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d("TAG", "onNext: " + response);
                                    viewContract.onFavAdded(new JSONObject(response).getString("message"));
                                    viewContract.onHideProgress();

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            manager.setAUTH(newToken);
                                            onAddToFav(providerId, catId);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

                                            viewContract.onLogout(msg);
                                            viewContract.onHideProgress();
                                        }
                                    });
                                    // viewContract.onHideProgress();
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    viewContract.onHideProgress();
                                    viewContract.onLogout(jsonObject.getString("message"));

                                    break;
                                case 411:
                                    response = responseBodyResponse.errorBody().string();
                                    viewContract.onFavAdded(new JSONObject(response).getString("message"));
                                    viewContract.onHideProgress();
                                    break;
                                default:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    viewContract.onHideProgress();
                                    viewContract.onLogout(jsonObject.getString("message"));
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

                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void removeFromFav(String providerId, String catId) {
        Observable<Response<ResponseBody>> responseObservable = lspServices.removeFromFav(manager.getAUTH(), Constants.selLang, catId,
                providerId);

        responseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {
                        int code = responseBodyResponse.code();
                        String response;
                        try {


                            switch (code) {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d("TAG", "onNext: " + response);
                                    viewContract.removeFromFav(new JSONObject(response).getString("message"));
                                    viewContract.onHideProgress();

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            manager.setAUTH(newToken);
                                            onAddToFav(providerId, catId);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

                                            viewContract.onLogout(msg);
                                            viewContract.onHideProgress();
                                        }
                                    });
                                    // viewContract.onHideProgress();
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    viewContract.onHideProgress();
                                    viewContract.onLogout(new JSONObject(response).getString("message"));
                                    break;
                                default:
                                    response = responseBodyResponse.errorBody().string();
                                    viewContract.onError(new JSONObject(response).getString("message"));
                                    viewContract.onHideProgress();
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

                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
