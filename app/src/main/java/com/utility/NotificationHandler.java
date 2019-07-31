package com.utility;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.localgenie.R;
import com.localgenie.jobDetailsStatus.JobDetailsActivity;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;

import javax.inject.Inject;

import dagger.android.DaggerActivity;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * Created by embed on 22/8/16.
 *
 */
public class NotificationHandler extends DaggerAppCompatActivity
{
    String message,latlong,dialogtitle;
    String spltlatlonf[];
    String TAG = "NotificationHandler";
    Intent intent;
    ProgressDialog pDialog;
    @Inject
    SessionManagerImpl manager;
    @Inject
    LSPServices lspServices;
    private int action;
    private long bid;

    @Inject
    public NotificationHandler() {
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         pDialog = new ProgressDialog(this);
        if(getIntent().getExtras()!=null)
        {
            message = getIntent().getStringExtra("message");
            action = getIntent().getIntExtra("statcode",0);
            if(action==23)
            {

                dialogtitle = "Customer ID: "+manager;
            }
            else {
                bid = getIntent().getLongExtra("bid",0);
                dialogtitle = "Booking ID: "+bid;
            }

            if(action == 11 || action == 12 ||action == 10 || action == 5 || action == 4 || action == 23)
            {
                NotificationUtils.clearNotifications(NotificationHandler.this);
            }
            Log.i(TAG,"Handlermess "+message+" bid "+bid+" action "+action);

        }
        if(action==23)
        {
            new AlertDialog.Builder(this)
                    .setTitle(dialogtitle)
                    .setMessage(""+message)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {

                            Utility.setMAnagerWithBID(NotificationHandler.this,manager);
                            dialog.dismiss();
                        }
                    })

                    .setIcon(R.mipmap.ic_launcher_round)
                    .show();
        }
        else
        {
         new AlertDialog.Builder(this)
                .setTitle(dialogtitle)
                .setMessage(""+message)
                 .setCancelable(false)
                 .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        if(action == 11 || action == 12 ||action == 10 || action == 5 || action == 4)
                        {
                         //   intent = new Intent(NotificationHandler.this,MenuActivity.class);
                          //  Utility.deleteEventFromCalender(bid,manager,NotificationHandler.this);
                            finish();
                            dialog.dismiss();
                        }

                        else
                        {
                            NotificationUtils.clearNotifications(NotificationHandler.this);
                            intent = new Intent(NotificationHandler.this,JobDetailsActivity.class);
                            intent.putExtra("BID", bid);
                            intent.putExtra("STATUS", action);
                            intent.putExtra("ImageUrl","");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            dialog.dismiss();
                        }

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                        dialog.dismiss();
                    }
                })
                .setIcon(R.mipmap.ic_launcher_round)
            .show();
        }

    }

    public void addReminderEventId(int eventId, long bookingId,SessionManagerImpl manager) {
        Log.d(TAG, "addReminderEventId: auth: "+manager.getAUTH()+" Sellang:"+Constants.selLang+" BookingId: "+bookingId+" EventId: "+eventId);
        Observable<Response<ResponseBody>> responseObservable=null;
        if(lspServices!=null){
            responseObservable = lspServices.reminderEvent(manager.getAUTH(),
                    Constants.selLang,bookingId,eventId);
            responseObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                        @Override
                        public void onNext(Response<ResponseBody> responseBodyResponse) {
                            Log.d(TAG, "onNextReminder: "+responseBodyResponse.code());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }else{
            Log.d(TAG, "addReminderEventId: LSPSERVICES  is null ");
            Log.d(TAG, "addReminderEventId: SESSIONMANAGER  is  "+manager);
        }
    }
}
