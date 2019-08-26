package com.localgenie.videocalling;

/**
 * Created by moda on 04/05/17.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.SlideLayout.ISlideListener;
import com.localgenie.SlideLayout.Renderers.TranslateRenderer;
import com.localgenie.SlideLayout.SlideLayout;
import com.localgenie.SlideLayout.Sliders.Direction;
import com.localgenie.SlideLayout.Sliders.VerticalSlider;
import com.localgenie.home.MainActivity;
import com.localgenie.jobDetailsStatus.JobDetailsActivity;
import com.localgenie.networking.ChatApiService;
import com.localgenie.networking.ServiceFactory;
import com.localgenie.utilities.LSPApplication;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;
import com.pojo.callpojo.CallActions;
import com.utility.OnMyService;
import com.webRtc.CallingApis;
import com.webRtc.utils.TextDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class IncomingCallScreen extends DaggerAppCompatActivity implements android.view.View.OnClickListener, IncomingCallPresenterImpl.IncomingCallView {

    @BindView(R.id.btAcceptCall)
    AppCompatImageView accept;
    @BindView(R.id.tvCallerName)
    TextView tvCallerName;
    @BindView(R.id.tvAudioVideoCall)
    TextView tvAudioVideoCall;
    @BindView(R.id.btRejectCall)
    AppCompatImageView btRejectCall;
    @BindView(R.id.button)
    Button sendMessage;
    @BindView(R.id.userImage)
    ImageView callerImage;
    @BindView(R.id.user_icon)
    ImageView callerImageIcon;

    @Inject
    IncomingCallPresenterImpl.IncomingCallPresenters presenters;
    /* Parameters required to start a call */
    String call_id, roomId;
    @Inject
    MQTTManager mqttManager;
    @Inject
    SessionManagerImpl manager;
    private Ringtone r;
    private String caller_id;
    private String callType, callerName, callerImageUrl, callerIdentifier;
    private String bookingId;
    private CountDownTimer timer;
    private ChatApiService chatApiService;
    private boolean isAttendButtonClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /* SharedPreferences m_prefs =
                getSharedPreferences("global_settings", Context.MODE_PRIVATE);

        String callId = m_prefs.getString("call_id", "");

        if (callId==null || callId.isEmpty() ){
                Intent intent = new Intent(IncomingCallScreen.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                supportFinishAfterTransition();
        } else {*/

            Window window = this.getWindow();

            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                window.getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                window.getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                        );

            }
        /*}*/

       /* *//********For Samsung device to diasble lock**************//*

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "INFO");
        wl.acquire();

        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("name");
        kl.disableKeyguard();
        *//********For Samsung device to diasble lock**************/


        /*getWindow().addFlags(
                LayoutParams.FLAG_FULLSCREEN
                        | LayoutParams.FLAG_KEEP_SCREEN_ON
                        | LayoutParams.FLAG_DISMISS_KEYGUARD
                        | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | LayoutParams.FLAG_TURN_SCREEN_ON);
        *//********For Samsung device to diasble lock**************//*

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "INFO");
        wl.acquire();

        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("name");
        kl.disableKeyguard();
        *//********For Samsung device to diasble lock**************//*
        Window window = this.getWindow();

        window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                LayoutParams.FLAG_TURN_SCREEN_ON | LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_FULLSCREEN);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            window.getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                window.getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                        );
            }

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }*/
        setContentView(R.layout.activity_incoming_call);
        chatApiService = ServiceFactory.createRetrofitChatService(ChatApiService.class);

        startService(new Intent(this, OnMyService.class));

        ButterKnife.bind(this);


        if (!Utility.isAppIsInBackground(this)) {
            timer = new CountDownTimer(15000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    //Perform the click of cancel button here
                    if (!isAttendButtonClicked) {
                        if (!TextUtils.isEmpty(call_id))
                            presenters.endCall(call_id, "request", chatApiService);
                    }
                }
            };
            timer.start();

        }

        SlideLayout slider = (SlideLayout) findViewById(R.id.slider);
        slider.setRenderer(new TranslateRenderer());


        slider.setSlider(new VerticalSlider(Direction.INVERSE));


        slider.setChildId(R.id.fl);
        slider.setThreshold(0.7f);

        slider.addSlideListener(new ISlideListener() {
            @Override
            public void onSlideDone(SlideLayout slider, boolean done) {
                if (done) {
                    // restore start state

                    isAttendButtonClicked = true;
                    /*
                     * MQtt
                     */
                    /*
                     * To tell the caller that i have accepted the call
                     */
                    //TODO answer the call;

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("type", 1);
                        obj.put("callId", call_id);
                        /*
                         * Not useful as of now,but can be useful in future if we add groupcalling
                         */
                        obj.put("userId", manager.getSID());
                        mqttManager.publish(MqttEvents.Calls.value + "/" + caller_id, obj, 0, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    presenters.answerCall(call_id, chatApiService);

                    /*


                     * Last parameter is to specify the call is the incoming call or the outgoing call
                     */
/*
                    Log.d("TAG","onSlideDoneOne: "+caller_id+" c "+callerName
                    +" ca "+callerImageUrl+" cal "+callType+" call "+callerIdentifier);
*/

/*
                    CallingApis.initiateCall(IncomingCallScreen.this,caller_id,callerName,callerImageUrl
                            ,callType,callerIdentifier,roomId,call_id,bookingId+"",true);
*/

                    //  CallingApis.initiateCall(IncomingCallScreen.this, callType, call_id, caller_id, callerName, callerImageUrl, true, callerIdentifier,bookingId);
                    supportFinishAfterTransition();
                    // finish();
                }
            }
        });


        slider.startAnimation(AnimationUtils.loadAnimation(IncomingCallScreen.this, R.anim.call_animation));

        tvCallerName = (TextView)

                findViewById(R.id.tvCallerName);

        tvAudioVideoCall = (TextView)

                findViewById(R.id.tvAudioVideoCall);


        AppCompatImageView btRejectCall = findViewById(R.id.btRejectCall);


        btRejectCall.setOnClickListener(this);


        Button sendMessage = (Button)

                findViewById(R.id.button);

        sendMessage.setOnClickListener(this);
        callerImage = (ImageView)

                findViewById(R.id.userImage);


        callerImageIcon = findViewById(R.id.user_icon);


        /* Get the mobile number of the user from shared preferences */


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            call_id = extras.getString("callId", "");
            roomId = extras.getString("roomId", "");
            caller_id = extras.getString("callerId", "");
            callType = extras.getString("callType", "");
            bookingId = extras.getString("BookingId", "");

            callerName = extras.getString("callerName", "");
            callerImageUrl = extras.getString("callerImage", "");
            callerIdentifier = extras.getString("callerIdentifier", "");
            try {
                mqttManager.subscribeToTopic(MqttEvents.Call.value + "/" + call_id, 0);
                mqttManager.subscribeToTopic(MqttEvents.Calls.value + "/" + call_id, 0);
            } catch (Exception ex) {
                Log.d("Error", "onCreate: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (
                Exception e) {
            e.printStackTrace();
        }

        //TODO check is caller still available then
        presenters.checkIsCallerStillWaiting(call_id, chatApiService,this);
        setupView();

        /* Implement the countdown times */
        timer = new
                CountDownTimer(60000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        /* Perform the click of cancel button here */
                        //  Toast.makeText(IncomingCallScreen.this, "Timeout", Toast.LENGTH_LONG).show();
                        try {

                            JSONObject obj = new JSONObject();
                            obj.put("type", 3);
                            obj.put("callId", call_id);
                            /*
                             * Not useful as of now,but can be useful in future if we add groupcalling
                             */

                            obj.put("userId", manager.getSID());
                            //TODO not answered after 1min
//
                           mqttManager.publish(MqttEvents.Calls.value + "/" + caller_id, obj, 0, false);

                            obj = new JSONObject();

                            obj.put("status", 1);
//
                            mqttManager.publish(MqttEvents.CallsAvailability.value + "/" +manager.getSID(), obj, 0, true);

                            UtilityVideoCall.getInstance().setActiveOnACall(false, true);
                            supportFinishAfterTransition();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
        timer.start();


        //  addNewCall();

        initalizeRxJava();

    }

    private void initalizeRxJava() {

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String objects) {

                Log.d("TAG", "onNextINCommming: " + objects);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(objects);
                    onMessage(jsonObject);
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
        };
        RxCallInfo.getInstance().subscribe(observer);
    }

    /* Setup the view for incoming call screen */
    @SuppressWarnings("TryWithIdenticalCatches")
    private void setupView() {


        Bundle extras = getIntent().getExtras();


        callerName = extras.getString("callerName");
        tvCallerName.setText(callerName);


        callerImageUrl = extras.getString("callerImage");

        if (callerImageUrl == null || callerImageUrl.isEmpty()) {


            callerImage.setImageDrawable(TextDrawable.builder()

                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .fontSize(124 * (int) getResources().getDisplayMetrics().density) /* size in px */
                    .bold()
                    .toUpperCase()
                    .endConfig()


                    .buildRect((extras.getString("callerIdentifier").trim()).charAt(0) + "", Color.parseColor(UtilityVideoCall.getInstance().getColorCode(5))));


            callerImageIcon.setImageDrawable(TextDrawable.builder()


                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .fontSize(24 * (int) getResources().getDisplayMetrics().density) /* size in px */
                    .bold()
                    .toUpperCase()
                    .endConfig()


                    .buildRound((extras.getString("callerIdentifier").trim()).charAt(0) + "", Color.parseColor(UtilityVideoCall.getInstance().getColorCode(5))));


        } else {

            try {

                //TODO createGlideOption not available.
//                    Glide.with(this)
//                            .load(callerImageUrl)
//                            .apply(Utility.createGlideOption(this))
//                            .into(callerImage);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
        //  }


        if (callType.contentEquals("0")) {
            /* It is an audio call */

            tvAudioVideoCall.setText(getResources().getString(R.string.Mqtt_Audio_Call));


            if (ActivityCompat.checkSelfPermission(IncomingCallScreen.this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(IncomingCallScreen.this, new String[]{Manifest.permission.RECORD_AUDIO},
                        0);

            }


        } else {
            /* It is a video call */
            tvAudioVideoCall.setText(getResources().getString(R.string.Mqtt_Video_Call));


            ArrayList<String> arr1 = new ArrayList<>();
            if (ActivityCompat.checkSelfPermission(IncomingCallScreen.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                arr1.add(Manifest.permission.CAMERA);
            }


            if (ActivityCompat.checkSelfPermission(IncomingCallScreen.this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {


                arr1.add(Manifest.permission.RECORD_AUDIO);

            }


            if (arr1.size() > 0) {

                ActivityCompat.requestPermissions(IncomingCallScreen.this, arr1.toArray(new String[arr1.size()]),
                        1);
            }

        }


    }

    @Override
    public void onBackPressed() {
        /* Disable the functionality of back button on the calling screen */
        return;
    }

    @Override
    public void onDestroy() {
        /* Disable the functionality of back button on the calling screen */
        super.onDestroy();
        try {
            if (!isAttendButtonClicked) {
                mqttManager.unSubscribeToTopic(MqttEvents.Call.value + "/" + call_id);
            }
            timer.cancel();
            r.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //   bus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initalizeRxJava();

       /* if (call_id != null && !call_id.trim().isEmpty()) {
            presenters.checkIsCallerStillWaiting(call_id, chatApiService,this);
        } else {
            Intent intent = new Intent(IncomingCallScreen.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.

            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }


            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }


    }


    @Override
    public void onClick(android.view.View view) {

        switch (view.getId()) {
            case R.id.btRejectCall: {
                if (!TextUtils.isEmpty(call_id))
                    presenters.endCall(call_id, "request", chatApiService);
                else
                    rejectCall(false);
                break;
            }
            case R.id.button: {
//                rejectCall(true);
                Toast.makeText(this, getString(R.string.waiting_for_connection), Toast.LENGTH_SHORT).show();
                break;

            }
        }
    }


    public void onMessage(JSONObject object) {

        try {
            Log.d("log1", "15");

            Log.d("log1", object.toString());
            if (object.has("action")) {

                Log.d("log1", "16");
                if (object.getString("action").equals(CallActions.NOT_ANSWER_OR_LEFT.value)) {
                    Log.d("log1", "13");
                    UtilityVideoCall.getInstance().setActiveOnACall(false, true);
                    onSuccessDec();
                } else if (object.getString("action").equals(CallActions.JOIN_ON_CALL.value)) {

                } else if (object.getString("action").equals(CallActions.CALL_ENDED.value)) {
                   /* call_id = "";
                    SharedPreferences m_prefs =
                            getSharedPreferences("global_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = m_prefs.edit();
                    editor.putString("call_id", "");
                    editor.apply();
                    Log.d("log1CutCallId", ""+object.getString("callId"));
                    Log.d("log1", "14");*/
                    UtilityVideoCall.getInstance().setActiveOnACall(false, true);
                    onSuccessDec();
                }
            }
            Log.d("log1", "13");

            try {
                if (object.getString("eventName").equals(MqttEvents.Calls.value + "/" + manager.getSID())) {
                    if (object.getInt("type") == 2) {

                        /*
                         * So many if are used just to avoid any stray messages calling interruption in ongoing or incoming call
                         */
                        if (object.getString("callId").equals(call_id) && object.getString("callerId").equals(caller_id)) {

                            r.stop();
                            supportFinishAfterTransition();
                            //closeApp();
                        }


                        /*
                         * To make myself available for receiving the new call
                         */
                        JSONObject obj = new JSONObject();


                        obj.put("status", 1);
                        mqttManager.publish(MqttEvents.CallsAvailability.value + "/" + manager.getSID(), obj, 0, true);
                        UtilityVideoCall.getInstance().setActiveOnACall(false, true);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            Log.d("TAG", "onMessageTYPEEVENT: "+object.getString("eventName")
//            +" type "+object.getInt("type")+" isCallID "+object.getString("callId").equals(call_id)
//            +" isCallerID "+ object.getString("userId").equals(caller_id));
//
//
//            if (object.getString("eventName").equals(""/*MqttEvents.Calls.value*/)) {
//                if (object.getInt("type") == 2) {
//                    /*
//                     * To make myself available for receiving the new call
//                     */
//                    JSONObject obj = new JSONObject();
//                    obj.put("status", 1);
////                    mqttManager.publish(MqttEvents.CallsAvailability.value + "/" + manager.getSID(), obj, 0, true);
//                    UtilityVideoCall.getInstance().setActiveOnACall(false, true);
//                    /*
//                     * So many if are used just to avoid any stray messages calling interruption in ongoing or incoming call
//                     */
//                    if (object.getString("callId").equals(call_id) && object.getString("userId").equals(caller_id)) {
//                        r.stop();
//                        supportFinishAfterTransition();
//                    }
//                }
//            }

            /*else if (object.getString("eventName").equals(MqttEvents.Signout.value)) {

                Toast.makeText(this, getResources().getString(R.string.logout), Toast.LENGTH_SHORT).show();

                Intent i2 = new Intent(IncomingCallScreen.this, SplashActivity.class);
                i2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i2);
                supportFinishAfterTransition();

            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


   /* private void addNewCall() {

        Map<String, Object> callItem = new HashMap<>();
        String id = Utilities.tsInGmt();


        callItem.put("receiverName", callerName);
        callItem.put("receiverImage", callerImageUrl);
        callItem.put("receiverUid", caller_id);
        callItem.put("callTime", id);
        callItem.put("callInitiated", false);
        callItem.put("callId", String.valueOf(id));
        if (callType.equals("1")) {
            callItem.put("callType", getString(R.string.VideoCall));

        } else {
            callItem.put("callType", getString(R.string.AudioCall));

        }
        callItem.put("receiverIdentifier", callerIdentifier);
        db.addNewCall(AppController.getInstance().getCallsDocId(), callItem);
        Common.callerName = callerName;


    }*/


    @SuppressWarnings("TryWithIdenticalCatches")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == 0) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                /*
                 * Not required essentially
                 */
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

//
//                    try {
//                        JSONObject obj = new JSONObject();
//
//                        obj.put("callId", call_id);
//                        obj.put("userId", AppController.getInstance().getUserId());
//                        obj.put("type", callType);
//
//
//                        AppController.getInstance().publish(MqttEvents.Calls.value + "/" + caller_id, obj, 0, false);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    makeMyselfAvailableForCall();
                    supportFinishAfterTransition();
                }
            }

        } else if (requestCode == 1) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
//                    try {
//                        JSONObject obj = new JSONObject();
//
//                        obj.put("callId", call_id);
//                        obj.put("userId", AppController.getInstance().getUserId());
//                        obj.put("type", callType);
//
//
//                        AppController.getInstance().publish(MqttEvents.Calls.value + "/" + caller_id, obj, 0, false);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    makeMyselfAvailableForCall();
                    supportFinishAfterTransition();
                }
            } else if (grantResults.length == 2 && (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED)) {


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
//                    try {
//                        JSONObject obj = new JSONObject();
//
//                        obj.put("callId", call_id);
//                        obj.put("userId", AppController.getInstance().getUserId());
//                        obj.put("type", callType);
//
//
//                        AppController.getInstance().publish(MqttEvents.Calls.value + "/" + caller_id, obj, 0, false);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    makeMyselfAvailableForCall();
                    supportFinishAfterTransition();

                }

            }
        }
    }


    private void makeMyselfAvailableForCall() {

        try {

            JSONObject obj = new JSONObject();
            obj.put("callId", call_id);
            obj.put("userId", manager.getSID());
            obj.put("type", callType);


            mqttManager.publish(MqttEvents.Calls.value + "/" + caller_id, obj, 0, false);

            obj = new JSONObject();
            obj.put("status", 1);

            mqttManager.publish(MqttEvents.CallsAvailability.value + "/" + manager.getSID(), obj, 0, true);
            UtilityVideoCall.getInstance().setActiveOnACall(false, true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("TryWithIdenticalCatches")
    private void rejectCall(boolean toStartChat) {
        /*
         * Have to send the reject call event and also to make myself availabvle for the next call
         */

        try {
            /*
             * To tell the caller that i have rejected the call
             */

            r.stop();
            JSONObject obj = new JSONObject();
            obj.put("type", 2);
            obj.put("callId", call_id);


            /*
             * Not useful as of now,but can be useful in future if we add groupcalling
             */

            obj.put("userId", manager.getSID());
            mqttManager.publish(MqttEvents.Calls.value + "/" + caller_id, obj, 0, false);

//            /*
//             * To make myself available for receiving the new call
//             */
//

            obj = new JSONObject();
            obj.put("status", 1);

            mqttManager.publish(MqttEvents.CallsAvailability.value + "/" +manager.getSID(), obj, 0, true);
            UtilityVideoCall.getInstance().setActiveOnACall(false, true);

        } catch (JSONException e2) {
            e2.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (toStartChat) {

            /*String docId = AppController.findDocumentIdOfReceiver(caller_id, Utilities.tsInGmt(), tvCallerName.getText().toString(),
                    callerImageUrl, "", false, callerIdentifier, "", false);*/


//            try {
//                Intent intent = new Intent(IncomingCallScreen.this, ChattingActivity.class);
//
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                intent.putExtra("receiverUid", caller_id);
//                intent.putExtra("receiverName", tvCallerName.getText().toString());
//              //  intent.putExtra("documentId", docId);
//
//                intent.putExtra("receiverImage", callerImageUrl);
//                intent.putExtra("colorCode", UtilityVideoCall.getInstance().getColorCode(5));
//
//                startActivity(intent);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        }
        supportFinishAfterTransition();


    }


/* @Override
    public void finish() {
        if(action == 120) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                super.finishAndRemoveTask();
            } else {
                super.finish();
            }
        }
    }*/

    @Override
    public void onSuccessAns() {
        Log.d("TAG", "onSlideDone: " + caller_id + " c " + callerName
                + " ca " + callerImageUrl + " cal " + callType + " call " + callerIdentifier);

     /*   CallingApis.initiateCall(IncomingCallScreen.this,caller_id,callerName,callerImageUrl
                ,callType,callerIdentifier,roomId,call_id,bookingId+"",true);*/// Ankit Did

        CallingApis.initiateCall(IncomingCallScreen.this, caller_id + "", callerName, callerImageUrl
                , callType + "", callerIdentifier, roomId, call_id, bookingId + "", false);

        /*CallingApis.initiateCall(IncomingCallScreen.this,caller_id,callerName,callerImageUrl
                            ,callType,callerIdentifier,roomId,call_id,bookingId+"",true);*/ // Ali did

        supportFinishAfterTransition();
    }

    @Override
    public void onSuccessDec() {

        supportFinishAfterTransition();
    }

    public String randomString() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        sb.append("PnPLabs3Embed");
        return sb.toString();
    }

    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message) {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onShowProgress() {

    }

    @Override
    public void onHideProgress() {

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON |
                    LayoutParams.FLAG_DISMISS_KEYGUARD |
                    LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }
}