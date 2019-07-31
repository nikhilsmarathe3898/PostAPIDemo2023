package com.localgenie.jobDetailsStatus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.home.MainActivity;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.utility.AlertProgress;

import java.io.IOException;
import java.util.ArrayList;
import javax.inject.Inject;

import adapters.CancelAdapter;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import com.pojo.CancelReasonPojo;
import com.pojo.ErrorHandel;
import com.utility.CalendarEventHelper;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Response;

/**
 * <h>CancelBookingImpl</h>
 * Created by Ali on 3/14/2018.
 */

public class CancelBookingImpl implements JobDetailsOnTheWayContract.CancelBooking
{

    private long bid;
    @Inject
    SessionManagerImpl manager;

    @Inject
    LSPServices lspServices;



    @Inject
    Gson gson;
    @Inject
    AppTypeface appTypeface;
    private Context mContext;

    private ProgressDialog pDialog;
    private Dialog indialog;
    private int resId = -1;
    String reminderId = "";

    @Inject
    AlertProgress alertProgress;
    @Inject
    public CancelBookingImpl()
    {

    }

    @Override
    public void onToCancelBooking(long bid, Context mContext, String reminderId)
    {
        this.bid = bid;
        this.mContext = mContext;
        this.reminderId = reminderId;
        pDialog = new ProgressDialog(mContext);
        pDialog.setCancelable(false);
        pDialog.setMessage(mContext.getString(R.string.wait));
        if(alertProgress.isNetworkAvailable(mContext))
        {
            pDialog.show();
            callCancelApi();
        }else
            alertProgress.showNetworkAlert(mContext);


    }

    @Override
    public void onSelectedReason(int res_id)
    {
        resId = res_id;
    }

    private void callCancelApi() {

        Log.d("TAG", "callCancelApi: "+bid);
        Observable<Response<ResponseBody>> observable = lspServices.onCancelReasons(manager.getAUTH(),
                Constants.selLang,bid+"");

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code  = responseBodyResponse.code();
                        Log.d("TAG", "onNext: "+code);
                        String response;
                        try {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d("TAG", "onNextCancelReason: "+response);
                                    CancelReasonPojo cancel = gson.fromJson(response, CancelReasonPojo.class);
                                    cancelReasonDialog(cancel.getData().getReason(),cancel.getData().isCancellationFeeApplied()
                                            ,cancel.getData().getCancellationFee(),cancel.getMessage());//, isPending
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    break;
                                default:

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        pDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {

                        alertProgress.alertinfo(mContext,e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void cancelReasonDialog(ArrayList<CancelReasonPojo.CancelReasonData> reason, boolean cancellationFeeApplied, double cancellationFee, String message)
    {


        if(cancellationFeeApplied)
        {
            alertProgress.alertPositiveNegativeOnclick(mContext, message, mContext.getResources().getString(R.string.cancel), mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.cancel),false, isClicked -> {
                if(isClicked)
                {
                    showDialog(reason);
                }
            });
        }else
        {
            showDialog(reason);
        }

    }

    private void showDialog(ArrayList<CancelReasonPojo.CancelReasonData> reason) {

        indialog = new Dialog(mContext);
        indialog.setCanceledOnTouchOutside(true);
        indialog.setCancelable(true);
        indialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        indialog.setContentView(R.layout.cancel_dialog);
        indialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        indialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView listcancel = indialog.findViewById(R.id.listcancel);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        TextView cancelDialogReson = indialog.findViewById(R.id.cancelDialogReson);
        ImageView cancelDialog = indialog.findViewById(R.id.cancelDialog);
        TextView submitcancel = indialog.findViewById(R.id.submitcancel);
        cancelDialogReson.setTypeface(appTypeface.getHind_medium());
        submitcancel.setTypeface(appTypeface.getHind_semiBold());
        cancelDialog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                indialog.dismiss();
            }
        });
        final CancelAdapter cancelAdapter = new CancelAdapter(mContext, reason, CancelBookingImpl.this);
        listcancel.setLayoutManager(linearLayoutManager);
        listcancel.setAdapter(cancelAdapter);

        submitcancel.setOnClickListener(v -> {
            if (resId!= -1) {
                if(alertProgress.isNetworkAvailable(mContext))
                {

                    cancelAppService(resId);
                    indialog.dismiss();
                }else
                    alertProgress.showNetworkAlert(mContext);

            } else {
                Toast.makeText(mContext, "Please provide valid reason", Toast.LENGTH_SHORT).show();
            }
        });

        indialog.show();
    }

    private void cancelAppService(int resId)
    {

        pDialog.show();
        Observable<Response<ResponseBody>> observable = lspServices.cancelBooking(manager.getAUTH()
                ,Constants.selLang,bid,resId);
        Log.d("SHIJEN", "cancelAppService: cancelApiService auth:"+manager.getAUTH()+ " bid: "+bid +"resId:"+ resId);

        observable.subscribeOn(Schedulers.io())
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
                        Log.d("TAG", "onNextintCode: "+code);

                        try
                        {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d("TAG", "onNextintResponse: "+responseBodyResponse.body().string());
                                    JSONObject jsonObject = new JSONObject(response);
                                    cancelResponse(jsonObject.getString("message"));

                                    if(!"".equals(reminderId) && reminderId!=null)
                                        removeReminder(bid,reminderId);

                                    break;
                                case Constants.SESSION_LOGOUT:
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    break;
                                default:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response,ErrorHandel.class);
                                    alertProgress.alertinfo(mContext,errorHandel.getMessage());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void removeReminder(long bid, String reminderId) {

        CalendarEventHelper calendarEventHelper = new CalendarEventHelper(mContext);
        calendarEventHelper.deleteEvent(Long.parseLong(reminderId));
       /* Observable<Response<ResponseBody>> observable = lspServices.reminderEvent(manager.getAUTH(),
                Constants.selLang,bid,0);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {
                        Log.d("TAG", "onNext: "+responseBodyResponse.code());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
    }

    private void cancelResponse(String message) {


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.cancel));
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton(mContext.getString(R.string.ok), (dialog, which) -> {
            Intent intent = new Intent(mContext, MainActivity.class);
           /* intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
            Constants.isJobDetailsOpen = false;
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
            dialog.dismiss();
        });
        builder.show();
    }
}
