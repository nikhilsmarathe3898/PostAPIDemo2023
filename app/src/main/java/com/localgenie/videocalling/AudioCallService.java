package com.localgenie.videocalling;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.chatting.ChattingActivity;
import com.localgenie.networking.ChatApiService;
import com.localgenie.networking.ServiceFactory;
import com.localgenie.utilities.LSPApplication;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;
import com.pojo.callpojo.CallActions;
import com.utility.OnMyService;
import com.webRtc.utils.AppRTCAudioManager;
import com.webRtc.utils.AppRTCClient;

import com.webRtc.utils.DirectRTCClient;
import com.webRtc.utils.PeerConnectionClient;

import com.webRtc.utils.TextDrawable;
import com.webRtc.utils.WebSocketRTCClient;
/*import com.howdoo.dubly.R;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;*/

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;

import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.DaggerService;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;



import static com.webRtc.utils.Constants.EXTRA_AECDUMP_ENABLED;
import static com.webRtc.utils.Constants.EXTRA_AUDIOCODEC;
import static com.webRtc.utils.Constants.EXTRA_AUDIO_BITRATE;
import static com.webRtc.utils.Constants.EXTRA_DATA_CHANNEL_ENABLED;
import static com.webRtc.utils.Constants.EXTRA_DISABLE_BUILT_IN_AEC;
import static com.webRtc.utils.Constants.EXTRA_DISABLE_BUILT_IN_AGC;
import static com.webRtc.utils.Constants.EXTRA_DISABLE_BUILT_IN_NS;
import static com.webRtc.utils.Constants.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF;
import static com.webRtc.utils.Constants.EXTRA_ENABLE_RTCEVENTLOG;
import static com.webRtc.utils.Constants.EXTRA_FLEXFEC_ENABLED;
import static com.webRtc.utils.Constants.EXTRA_HWCODEC_ENABLED;
import static com.webRtc.utils.Constants.EXTRA_ID;
import static com.webRtc.utils.Constants.EXTRA_LOOPBACK;
import static com.webRtc.utils.Constants.EXTRA_MAX_RETRANSMITS;
import static com.webRtc.utils.Constants.EXTRA_MAX_RETRANSMITS_MS;
import static com.webRtc.utils.Constants.EXTRA_NEGOTIATED;
import static com.webRtc.utils.Constants.EXTRA_NOAUDIOPROCESSING_ENABLED;
import static com.webRtc.utils.Constants.EXTRA_OPENSLES_ENABLED;
import static com.webRtc.utils.Constants.EXTRA_ORDERED;
import static com.webRtc.utils.Constants.EXTRA_PROTOCOL;
import static com.webRtc.utils.Constants.EXTRA_ROOMID;
import static com.webRtc.utils.Constants.EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED;
import static com.webRtc.utils.Constants.EXTRA_TRACING;
import static com.webRtc.utils.Constants.EXTRA_URLPARAMETERS;
import static com.webRtc.utils.Constants.EXTRA_VIDEOCODEC;
import static com.webRtc.utils.Constants.EXTRA_VIDEO_BITRATE;
import static com.webRtc.utils.Constants.EXTRA_VIDEO_CALL;
import static com.webRtc.utils.Constants.EXTRA_VIDEO_FPS;



/**
 * Created by moda on 16/08/17.
 */

public class AudioCallService extends DaggerService implements AppRTCClient.SignalingEvents,
        PeerConnectionClient.PeerConnectionEvents,
        AudioCallEvents,VideoCallContract.View{

  /*  public static final String EXTRA_CALLID = "org.appspot.apprtc.CALLID";
    public static final String EXTRA_ROOMID = "org.appspot.apprtc.ROOMID";
    public static final String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
    public static final String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";

    public static final String EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE";
    public static final String EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC";
    public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "org.appspot.apprtc.CAPTURETOTEXTURE";
    public static final String EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED =
            "org.appspot.apprtc.NOAUDIOPROCESSING";
    public static final String EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP";
    public static final String EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES";
    public static final String EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD";
    public static final String EXTRA_TRACING = "org.appspot.apprtc.TRACING";
    public static final String EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE";
    public static final String EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME";*/
    private static final String TAG = "CallRTCClient";

    // List of mandatory application permissions.
    private static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.MODIFY_AUDIO_SETTINGS", "android.permission.RECORD_AUDIO",
            "android.permission.INTERNET"
    };

    // Peer connection statistics callback period in ms.
    private static final int STAT_CALLBACK_PERIOD = 1000;

    @Nullable
    private PeerConnectionClient peerConnectionClient;
    @Nullable
    private AppRTCClient appRtcClient;
    @Nullable
    private AppRTCClient.SignalingParameters signalingParameters;
    @Nullable
    private AppRTCAudioManager audioManager;
    private AppRTCClient.RoomConnectionParameters roomConnectionParameters;
    @Nullable
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    private EglBase rootEglBase = EglBase.create();

    private static final int MAX_RECONNECT_TIME = 60000;

    private long callStartedTimeMs = 0;


    //  private static Bus bus = AppController.getBus();


    private boolean disconnect = false,cleanUpRequested,timerAlreadyStarted,connected;

    /* Media Player Class to enable ring sound */ MediaPlayer mp,reconnectTonePlayer;

    private CountDownTimer timer,reconnectTimer;

    private CoordinatorLayout root;


    private View audioCallView, callHeaderView;

    private Context mContext;


    private Intent intent;
    private Handler handler;


    /**
     * Previously the fragment content to be accessible in service now
     */
    private long countUp, countUpHeader;

    private Chronometer stopWatchHeader;


    private ImageView mute, speaker;
    private TextView tvStopWatch, tvCallerName,tvReconnecting;
    private boolean isMute = false, isSpeaker = false;

    private ImageView callerImage;


    /**
     * Call control interface for container activity.
     */

    private String imageUrl = "",roomId;
    private ImageView initiateChat;


    private String callDuration, callDurationHeader;


    /**
     * Legacy code although not used as of now
     */
    private boolean isError;
    private boolean iceConnected;
    private boolean commandLineRun;
    private int runTimeMs;
    private WindowManager windowManager;


    private TextView callHeaderTv;
    private ImageView callHeaderIv;

    private WindowManager.LayoutParams params;
   /* PowerManager mgr;
    PowerManager.WakeLock wakeLock;*/


    /****************** i am doing *******************/

    @Inject
    MQTTManager mqttManager;
    @Inject
    SessionManagerImpl manager;

    @Inject
    VideoCallContract.Presenter presenter;
   /* @Inject
    AlertProgress alertProgress;*/

    /************************************************/

    @Override
    public void onCreate() {
        super.onCreate();

        if(!mqttManager.isMQTTConnected())
            mqttManager.createMQttConnection(manager.getSID(),false);
        mContext = this;
        startService(new Intent(this, OnMyService.class));

        audioCallView = LayoutInflater.from(this).inflate(R.layout.audio_call_service, null);
        //  bus.register(this);

       /* mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();*/


        // Set window styles for fullscreen-window size. Needs to be done before
        // adding content.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            audioCallView
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {


            audioCallView
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                    );
        }

        tvStopWatch = (TextView) audioCallView.findViewById(R.id.tvStopWatch);
        //Add the view to the window.
        addMainLayout();


        handler = new Handler(Looper.getMainLooper());


        root =  audioCallView.findViewById(R.id.root);


        iceConnected = false;
        signalingParameters = null;
/*

        rootEglBase = EglBase.create();
*/
        // Check for mandatory permissions.
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                logAndToast("Permission " + permission + " is not granted");

                Log.d("log1-->","13");
                stopSelf();
                return;
            }
        }


        initializeRxJava();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;

        if (intent != null) {

            if (intent.getExtras() != null && !intent.getExtras().getBoolean("isIncomingCall", true)) {
                mqttManager.subscribeToTopic(MqttEvents.Call.value + "/" + intent.getExtras().getString(EXTRA_ROOMID), 0);


                mp = MediaPlayer.create(this, R.raw.calling);
                mp.setLooping(true);
                mp.start();
                startCallProcedure(false);

            } else {
                tvStopWatch.setText(getString(R.string.connecting));
                startCallProcedure(true);
            }


        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {


        this.intent = intent;
        return null;
    }


    @Override
    public void onDestroy() {

        if (!disconnect) {

            onCallHangUp(2, false);

        }
        disconnect();
        super.onDestroy();
    }

    // OnCallEvents interface implementation.
    @Override
    public void onCallHangUp(int val, boolean received) {

        if (!disconnect) {
            disconnect = true;
            /* Send Reject through call event and call callEnd API */


            /*
             * MQtt
             */
            /*
             * Timeout from the sender side or call canceled from sender side
             */

            if (intent != null) {
                try {
                    if (!received) {

                        JSONObject obj = new JSONObject();


                        obj.put("callId", intent.getExtras().getString(EXTRA_ROOMID));


                        obj.put("userId", manager.getSID());
                        obj.put("type", val);
                        obj.put("userType", 1);

                        /*
                         * 7--timeout
                         * 2--reject
                         */


                        mqttManager.publish(MqttEvents.Calls.value + "/" + intent.getExtras().getString("callerId"), obj, 0, false);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        disconnect();

    }

    @Override
    public void onMute() {
        isMute = !isMute;
        audioManager.setMicrophoneMute(isMute);
    }

    @Override
    public void onSpeaker() {

        isSpeaker = !isSpeaker;
        audioManager.setSpeakerphoneOn(isSpeaker);
    }


    private void startCall() {

        if (appRtcClient != null) {
            callStartedTimeMs = System.currentTimeMillis();


            // Start room connection.
            appRtcClient.connectToRoom(roomConnectionParameters);

            // Create and audio manager that will take care of audio routing,
            // audio modes, audio device enumeration etc.
            audioManager = AppRTCAudioManager.create(getApplicationContext());
            // Store existing audio settings and change audio mode to
            // MODE_IN_COMMUNICATION for best possible VoIP performance.

            audioManager.start(new AppRTCAudioManager.AudioManagerEvents() {
                // This method will be called each time the number of available audio
                // devices has changed.
                @Override
                public void onAudioDeviceChanged(
                        AppRTCAudioManager.AudioDevice audioDevice, Set<AppRTCAudioManager.AudioDevice> availableAudioDevices) {
                    onAudioManagerDevicesChanged(audioDevice, availableAudioDevices);
                }
            });
            /*
             * To set speaker as default for the audio call
             */
            audioManager.setSpeakerphoneOn(false);
            //isSpeaker = false;
        } else {
            Log.e(TAG, "AppRTC client is not allocated for a call.");

        }


    }

    // This method is called when the audio manager reports audio device change,
    // e.g. from wired headset to speakerphone.
    private void onAudioManagerDevicesChanged(
            final AppRTCAudioManager.AudioDevice device, final Set<AppRTCAudioManager.AudioDevice> availableDevices) {
        Log.d(TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
                + "selected: " + device);
        // TODO(henrika): add callback handler.
    }

    // Should be called from UI thread
    private void callConnected() {


        if (peerConnectionClient == null) {
            Log.w(TAG, "Call is connected in closed or error state" + peerConnectionClient + " ");
            return;
        }

        // Disable statistics callback.
        peerConnectionClient.enableStatsEvents(false, STAT_CALLBACK_PERIOD);
        /* Start the stop watch */

        try {
            if (timer != null)
                timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            /* Stop the calling sound */
            if (mp != null) {

                try {
                    if (mp.isPlaying()) {

                        mp.stop();
                        mp.release();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!timerAlreadyStarted) {
            timerAlreadyStarted = true;
            startStopWatch();
        }




    }

    private void onAudioManagerChangedState() {
        // TODO(henrika): disable video if AppRTCAudioManager.AudioDevice.EARPIECE
        // is active.
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private void disconnect() {
        if (!cleanUpRequested) {
            cleanUpRequested = true;
            try {



                /*
                 * To make myself available for receiving the new call
                 */


                JSONObject obj = new JSONObject();
                obj.put("status", 1);

                mqttManager.publish(MqttEvents.CallsAvailability.value + "/" + manager.getSID(), obj, 0, true);
                UtilityVideoCall.getInstance().setActiveOnACall(false, false);


                try {
                    if (mp != null) {

                        try {
                            if (mp.isPlaying()) {

                                mp.stop();
                                mp.release();
                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (reconnectTonePlayer != null) {

                    try {
                        if (reconnectTonePlayer.isPlaying()) {
                            try {
                                reconnectTonePlayer.stop();

                                reconnectTonePlayer.release();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    if (timer != null)
                        timer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (appRtcClient != null) {
                    appRtcClient.disconnectFromRoom();
                    appRtcClient = null;
                }


                if (peerConnectionClient != null) {
                    peerConnectionClient.close();
                    peerConnectionClient = null;
                }
                if (audioManager != null) {
                    audioManager.stop();
                    audioManager = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            removeViews();
            stopSelf();

            Log.d("log1-->","14");
        }


    }


    private void removeViews() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        try {


            if (UtilityVideoCall.getInstance().isCallMinimized()) {


                try {
                    if (callHeaderView != null) {

                        windowManager.removeView(callHeaderView);
                    }


                } catch (IllegalArgumentException e) {

                    e.printStackTrace();

                    if (audioCallView != null) {


                        /*
                         * For clearing of the full screen UI mode
                         */
                        audioCallView
                                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        try {
                            windowManager.removeView(audioCallView);
                        } catch (IllegalArgumentException ef) {
                        }
                    }

                }
            } else {
                try {
                    if (audioCallView != null) {


                        /*
                         * For clearing of the full screen UI mode
                         */
                        audioCallView
                                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                        windowManager.removeView(audioCallView);

                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    if (callHeaderView != null) {
                        try {
                            windowManager.removeView(callHeaderView);

                        } catch (IllegalArgumentException ef) {
                        }
                    }

                }
            }


            try {
                //To handle already released exception

              //  rootEglBase.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void logAndToast(String msg) {


    }


    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and
    // are routed to UI thread.
    private void onConnectedToRoomInternal(final AppRTCClient.SignalingParameters params) {

        signalingParameters = params;

        if (peerConnectionClient != null) {
            peerConnectionClient.createPeerConnection(null,
                    null, null, signalingParameters);


            if (signalingParameters != null && signalingParameters.initiator) {

                // Create offer. Offer SDP will be sent to answering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createOffer();
            } else {
                if (params.offerSdp != null) {
                    peerConnectionClient.setRemoteDescription(params.offerSdp);

                    // Create answer. Answer SDP will be sent to offering client in
                    // PeerConnectionEvents.onLocalDescription event.
                    peerConnectionClient.createAnswer();
                }
                if (params.iceCandidates != null) {
                    // Add remote ICE candidates from room.
                    for (IceCandidate iceCandidate : params.iceCandidates) {
                        peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                    }
                }
            }
        }

    }


    @Override
    public void onConnectedToRoom(final AppRTCClient.SignalingParameters params) {


        handler.post(new Runnable() {

            @Override
            public void run() {
                onConnectedToRoomInternal(params);
            }
        });
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient != null) {
                    peerConnectionClient.setRemoteDescription(sdp);
                    if (signalingParameters != null && !signalingParameters.initiator) {

                        // Create answer. Answer SDP will be sent to offering client in
                        // PeerConnectionEvents.onLocalDescription event.
                        peerConnectionClient.createAnswer();
                    }
                } else {
                    Log.e(TAG, "Received remote SDP for non-initilized peer connection.");

                }


            }
        });

    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient != null) {
                    peerConnectionClient.addRemoteIceCandidate(candidate);
                } else {
                    Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");

                }

            }
        });
    }

    @Override
    public void onChannelClose() {


/*
        handler.post(new Runnable() {
            @Override
            public void run() {
                //  logAndToast("Remote end hung up; dropping PeerConnection");
                disconnect();
            }
        });
*/
    }

    @Override
    public void onChannelError(final String description) {
          reportError(description);
    }
    private void reportError(final String description) {
        handler.post(new Runnable() {
            @Override
            public void run() {


                Toast.makeText(mContext, description, Toast.LENGTH_SHORT).show();


            }
        });
    }

    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    @Override
    public void onLocalDescription(final SessionDescription sdp) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {

                    if (signalingParameters != null && signalingParameters.initiator) {
                        appRtcClient.sendOfferSdp(sdp);
                    } else {
                        appRtcClient.sendAnswerSdp(sdp);
                    }
                }
                if (peerConnectionParameters != null && peerConnectionParameters.videoMaxBitrate > 0) {

                    if (peerConnectionClient != null) {
                        peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
                    }
                }
            }
        });

    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidate(candidate);
                }
            }
        });
    }

    @Override
    public void onIceConnected() {
        /*//  final long delta = System.currentTimeMillis() - callStartedTimeMs;


        handler.post(new Runnable() {
            @Override
            public void run() {
                //    logAndToast("ICE connected, delay=" + delta + "ms");
                iceConnected = true;
                callConnected();
            }
        });*/
    }

    @Override
    public void onIceDisconnected() {

/*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run:");
                if(signalingParameters!=null)
                {
                    createOffer(signalingParameters);
                }
            }
        },3000);
*/

    }

    @Override
    public void onPeerConnectionClosed() {
    }

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {
    }

    @Override
    public void onPeerConnectionError(final String description) {
        reportError(description);
    }


    public void initializeRxJava() {

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String objects) {

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

    private void onMessage(JSONObject object) throws JSONException {
        try {
            if (object.getString("action").equals(CallActions.NOT_ANSWER_OR_LEFT.value)) {
                onRejectSuccess();
                UtilityVideoCall.getInstance().setActiveOnACall(false, true);
            } else if (object.getString("action").equals(CallActions.JOIN_ON_CALL.value)) {
                tvStopWatch.setText(getString(R.string.connecting));
                UtilityVideoCall.getInstance().setActiveOnACall(true, true);
            } else if (object.getString("action").equals(CallActions.CALL_ENDED.value)) {
                onRejectSuccess();
                UtilityVideoCall.getInstance().setActiveOnACall(false, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {


            if (object.getString("eventName").equals(MqttEvents.CallsAvailability.value)) {

                if (intent != null) {


                    Bundle extras = intent.getExtras();

                    if (object.getInt("status") == 0) {
                        /*
                         * Receiver is busy
                         */


                        if (extras != null) {


                            Toast.makeText(this, extras.getString("callerName") + " " + getString(R.string.busy), Toast.LENGTH_SHORT).show();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Log.d("log1-->","15");
                                stopSelf();
                            }
                        }, 2000);


                    } else {
                        /*
                         * I put myself as busy and make the call request to the receiver
                         */


                        try {

                            JSONObject obj = new JSONObject();
                            obj.put("status", 0);


                            mqttManager.publish(MqttEvents.CallsAvailability.value + "/" + manager.getSID(), obj, 0, true);
                            UtilityVideoCall.getInstance().setActiveOnACall(true, true);

                            obj = new JSONObject();
                            obj.put("callerId", manager.getSID());
                            obj.put("callId", extras.getString(EXTRA_ROOMID));
                            obj.put("bookingId", extras.getString("BookingID"));
                            obj.put("callType", "0");
                            obj.put("callerName", manager.getFirstName()+" "+manager.getLastName());
                            obj.put("callerImage", manager.getProfilePicUrl());
                            obj.put("callerIdentifier", extras.getString("callerIdentifier"));

                            obj.put("type", 0);
                            /*
                             * CalleeId althought not required but can be used in future on server so using it
                             *
                             *
                             * CalleeId contains the receiverUid,to whom the call has been made
                             *
                             * */



                        /*
                         * Type-0---call initiate request,on receiving the call initiate request,receiver will set his status as busy,so nobody else can call him
                         *
                         * /


                        obj.put("type", 0);
/*
 * Not making any message of call signalling as being persistent intentionally
 */

                            Log.d(TAG, "onMessageAudio: "+obj+" callerId "+extras.getString("callerId"));
                            mqttManager.publish(MqttEvents.Calls.value + "/" + extras.getString("callerId"), obj, 0, false);


                            UtilityVideoCall.getInstance().setActiveCallId(extras.getString(EXTRA_ROOMID));
                            UtilityVideoCall.getInstance().setActiveCallerId(extras.getString("callerId"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }


            } else if (object.getString("eventName").equals(MqttEvents.Calls.value)) {

                if (intent != null) {
                    switch (object.getInt("type")) {

                        case 1:

                            if (roomId != null) {
                                if (object.getString("callId").equals(roomId)) {
                                    tvStopWatch.setText(getString(R.string.connecting));
                                }

                            } else {
                                tvStopWatch.setText(getString(R.string.connecting));
                            }
                            break;
                        case 2:

                            if (roomId != null) {
                                if (object.getString("callId").equals(roomId)) {
                                    onCallHangUp(2, true);
                                }

                            } else {
                                onCallHangUp(2, true);
                            }
                            break;
                        case 3:

                            if (roomId != null) {
                                if (object.getString("callId").equals(roomId)) {
                                    if (intent.getExtras() != null) {

                                        Toast.makeText(this, getString(R.string.NoAnswer) + " " + intent.getExtras().getString("callerName"), Toast.LENGTH_SHORT).show();
                                    }

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {


                                            stopSelf();
                                        }
                                    }, 2000);
                                }

                            } else {
                                if (intent.getExtras() != null) {

                                    Toast.makeText(this, getString(R.string.NoAnswer) + " " + intent.getExtras().getString("callerName"), Toast.LENGTH_SHORT).show();
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {


                                        stopSelf();
                                    }
                                }, 2000);
                            }
                            break;


                        case 7:

                            if (roomId != null) {
                                if (object.getString("callId").equals(roomId)) {
                                    onCallHangUp(7, true);

                                }

                            } else {
                                onCallHangUp(7, true);
                            }
                    }
                }
            } else if (object.getString("eventName").equals("turnOnScreen")) {

                turnOnScreen();


            } else if (object.getString("eventName").equals("turnOffScreen")) {


                turnOffScreen();

            } else if (object.getString("eventName").equals("appCrashed")) {


               // destroyOnAppCrashed(true);
                disconnect();
                System.exit(1);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }




    }


    private void startCallProcedure(boolean incomingCall) {

        // Get Intent parameters.


        if (intent != null) {


            final Intent intent = this.intent;


            Uri roomUri = intent.getData();
            if (roomUri == null) {
                //  logAndToast(getString(R.string.missing_url));



                Log.d("log1-->","11");

                stopSelf();
                return;
            }

            roomId = intent.getStringExtra(EXTRA_ROOMID);
            if (roomId == null || roomId.length() == 0) {
                //  logAndToast(getString(R.string.missing_url));
                Log.d("log1-->","12");

                stopSelf();
                return;
            }

            boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
            boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false);
            PeerConnectionClient.DataChannelParameters dataChannelParameters = null;
            if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, false)) {
                dataChannelParameters = new PeerConnectionClient.DataChannelParameters(intent.getBooleanExtra(EXTRA_ORDERED, true),
                        intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1),
                        intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(EXTRA_PROTOCOL),
                        intent.getBooleanExtra(EXTRA_NEGOTIATED, false), intent.getIntExtra(EXTRA_ID, -1));
            }

            peerConnectionParameters = new PeerConnectionClient.PeerConnectionParameters(intent.getBooleanExtra(EXTRA_VIDEO_CALL, false), loopback,
                    tracing, 0, 0, intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
                    intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0), intent.getStringExtra(EXTRA_VIDEOCODEC),
                    intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true),
                    intent.getBooleanExtra(EXTRA_FLEXFEC_ENABLED, false),
                    intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0), intent.getStringExtra(EXTRA_AUDIOCODEC),
                    intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false),
                    intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false),
                    intent.getBooleanExtra(EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED, false),
                    intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false),
                    intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false),
                    intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false),
                    intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false),
                    intent.getBooleanExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false),
                    intent.getBooleanExtra(EXTRA_ENABLE_RTCEVENTLOG, false), dataChannelParameters);

            // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the
            // standard WebSocketRTCClient.
            if (loopback || !DirectRTCClient.IP_PATTERN.matcher(roomId).matches()) {
                appRtcClient = new WebSocketRTCClient(this);
            } else {
                Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.");
                appRtcClient = new DirectRTCClient(this);
            }
          /*  // Create connection client and connection parameters.
            appRtcClient = new WebSocketRTCClient(this, new LooperExecutor());*/




            // Create connection parameters.
            String urlParameters = intent.getStringExtra(EXTRA_URLPARAMETERS);
            roomConnectionParameters =
                    new AppRTCClient.RoomConnectionParameters(roomUri.toString(), roomId, loopback, urlParameters);


            // Create peer connection client.
            peerConnectionClient = new PeerConnectionClient(
                    getApplicationContext(), rootEglBase, peerConnectionParameters, this);
            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
            if (loopback) {
                options.networkIgnoreMask = 0;
            }
            peerConnectionClient.createPeerConnectionFactory(options);
            handler = new Handler(Looper.getMainLooper());


            showCallControlsUi();
            startCall();

            /* Implement the count down timer */

            if (!incomingCall) {
                timer = new CountDownTimer(60000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        /* Perform the click of cancel button here */
                        Toast.makeText(mContext, getResources().getString(R.string.Timeout), Toast.LENGTH_LONG).show();
                        onCallHangUp(7, false);
                    }
                };
                timer.start();
            }


        }



    }


    public void startStopWatch() {

        setCallDuration();

        if (initiateChat != null)

            initiateChat.setVisibility(View.VISIBLE);


    }


    private void setCallDuration() {

        try {
            CountDownTimer cTimer = new CountDownTimer(3600000, 1000) {

                public void onTick(long millisUntilFinished) {
                    long milliSec = (3600000 - millisUntilFinished) / 1000;

                    long sec = milliSec % 60;
                    long min = milliSec / 60;
                    if (min < 10) {
                        if (sec < 10) {
                            tvStopWatch.setText("00:0" + min + ":0" + sec);
                            callHeaderTv.setText("00:0" + min + ":0" + sec);
                        } else {
                            tvStopWatch.setText("00:0" + min + ":" + sec);

                            callHeaderTv.setText("00:0" + min + ":" + sec);
                        }

                    } else {
                        if (sec < 10) {
                            tvStopWatch.setText("00:" + min + ":0" + sec);
                            callHeaderTv.setText("00:" + min + ":0" + sec);


                        } else {
                            tvStopWatch.setText("00:" + min + ":" + sec);

                            callHeaderTv.setText("00:" + min + ":" + sec);


                        }

                    }


                }

                @Override
                public void onFinish() {

                }
            };

            cTimer.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("TryWithIdenticalCatches")

    private void showCallControlsUi() {
        /*
         * Instead of adding the fragment in the container,we now just update the visibility of the view
         *
         */

        callHeaderView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        callHeaderIv = (ImageView) callHeaderView.findViewById(R.id.image_iv);
        callHeaderTv = (TextView) callHeaderView.findViewById(R.id.duration);
        stopWatchHeader = (Chronometer) callHeaderView.findViewById(R.id.chrono);


        callerImage = (ImageView) audioCallView.findViewById(R.id.thumbnail);


        /*
         * For initiating of the chat
         */

        initiateChat = (ImageView) audioCallView.findViewById(R.id.initiateChat);


        // Create UI controls.
        ImageView cancelButton = (ImageView) audioCallView.findViewById(R.id.diconnect_btn);
        mute = (ImageView) audioCallView.findViewById(R.id.mute);
        speaker = (ImageView) audioCallView.findViewById(R.id.speaker);

//        mute.getDrawable().clearColorFilter();
//        speaker.getDrawable().clearColorFilter();
//        speaker.getDrawable().setColorFilter(0xFF000000, PorterDuff.Mode.SRC_ATOP);
//        mute.getDrawable().setColorFilter(0xFF000000, PorterDuff.Mode.SRC_ATOP);

        tvCallerName = (TextView) audioCallView.findViewById(R.id.tvCallerName);
        tvReconnecting = (TextView) audioCallView.findViewById(R.id.tvReconnecting);


        final String caller_id = intent.getStringExtra("callerId");


        // Map<String, Object> contactInfo = AppController.getInstance().getDbController().getContactInfoFromUid(UtilityVideoCall.getInstance().getContactsDocId(), caller_id);

//        if (contactInfo != null) {
//
//
//            tvCallerName.setText((String) contactInfo.get("contactName"));
//
//
//            imageUrl = (String) contactInfo.get("contactPicUrl");
//
//            if (imageUrl == null || imageUrl.isEmpty()) {
//
//
//                callerImage.setImageDrawable(TextDrawable.builder()
//
//
//                        .beginConfig()
//                        .textColor(Color.WHITE)
//                        .useFont(Typeface.DEFAULT)
//                        .fontSize(124 * (int) getResources().getDisplayMetrics().density) /* size in px */
//                        .bold()
//                        .toUpperCase()
//                        .endConfig()
//
//                        .buildRect(((String) contactInfo.get("contactName")).trim().charAt(0) + "", Color.parseColor("#FFCCBC")));
//
//                      //  .buildRect(((String) contactInfo.get("contactName")).trim().charAt(0) + "", Color.parseColor(AppController.getInstance().getColorCode(5))));
//
//                /*
//                 * For header when call is minimized
//                 */
//                callHeaderIv.setImageDrawable(TextDrawable.builder()
//
//
//                        .beginConfig()
//                        .textColor(Color.WHITE)
//                        .useFont(Typeface.DEFAULT)
//                        .fontSize(36 * (int) getResources().getDisplayMetrics().density) /* size in px */
//                        .bold()
//                        .toUpperCase()
//                        .endConfig()
//
//                        .buildRect(((String) contactInfo.get("contactName")).trim().charAt(0) + "", Color.parseColor("#FFCCBC")));
//
//                       // .buildRect(((String) contactInfo.get("contactName")).trim().charAt(0) + "", Color.parseColor(AppController.getInstance().getColorCode(5))));
//
//
//            } else {
//
//                Glide.with(this)
//                        .load(imageUrl)
//                        //.apply(Utility.createGlideOption(this))
//                        .into(callerImage);
//                try {
//
//                     /* For header when call is minimized
//                     */
//
//                    Glide.with(this)
//                            .load(imageUrl)
//                            .apply(Utility.createGlideOption(this))
//                            .into(callHeaderIv);
//
//
//
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        } else {


        /*
         * If userId doesn't exists in contact
         */


        if (intent != null) {
            Bundle extras = intent.getExtras();
            tvCallerName.setText(extras.getString("callerIdentifier"));

            imageUrl = extras.getString("callerImage");

            if (imageUrl == null || imageUrl.isEmpty()) {

                try {

                    callerImage.setImageDrawable(TextDrawable.builder()

                            .beginConfig()
                            .textColor(Color.WHITE)
                            .useFont(Typeface.DEFAULT)
                            .fontSize(124 * (int) getResources().getDisplayMetrics().density) /* size in px */
                            .bold()
                            .toUpperCase()
                            .endConfig()


                            .buildRect((extras.getString("callerIdentifier").trim()).charAt(0) + "", Color.parseColor(UtilityVideoCall.getInstance().getColorCode(5))));

                    /*
                     * For header when call is minimized
                     */
                    callHeaderIv.setImageDrawable(TextDrawable.builder()


                            .beginConfig()
                            .textColor(Color.WHITE)
                            .useFont(Typeface.DEFAULT)
                            .fontSize(36 * (int) getResources().getDisplayMetrics().density) /* size in px */
                            .bold()
                            .toUpperCase()
                            .endConfig()


                            .buildRect((extras.getString("callerIdentifier").trim()).charAt(0) + "", Color.parseColor(UtilityVideoCall.getInstance().getColorCode(5))));

                } catch (Exception e) {

                }
            } else {

                try {

                    Glide.with(this)
                            .load(imageUrl)
                            //.apply(Utility.createGlideOption(this))
                            .into(callerImage);


                    /*
                     * For header when call is minimized
                     */

                    Glide.with(this)
                            .load(imageUrl)
                            .apply(Utility.createGlideOption(this))
                            .into(callHeaderIv);



                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        }

        // }
        // Add buttons click events.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallHangUp(2, false);
            }
        });


        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                onMute();
                mute.setSelected(isMute);


                // throw new NullPointerException();
            }
        });

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSpeaker();
                speaker.setSelected(isSpeaker);

            }
        });


        final WindowManager.LayoutParams params;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);


        }


        // final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;


        callHeaderView.setOnTouchListener(new View.OnTouchListener()

        {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {


                    case MotionEvent.ACTION_DOWN:


                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.


                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            //Open the chat conversation click.


                            //close the service and remove the chat heads


                            windowManager.removeView(callHeaderView);
                            addMainLayout();
                            UtilityVideoCall.getInstance().setCallMinimized(false);
                        }
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.


                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        if ((int) (event.getRawX() - initialTouchX) == 0 && (int) (event.getRawY() - initialTouchY) == 0) {
                            lastAction = 0;
                        } else {


                            lastAction = event.getAction();
                        }

                        try {
                            //Update the layout with new X & Y coordinate
                            windowManager.updateViewLayout(callHeaderView, params);
                        } catch (NullPointerException e) {


                            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                            windowManager.updateViewLayout(callHeaderView, params);
                        }
                        return true;
                }
                return false;
            }
        });
        /*
         *To initiate chat while on the call
         *
         */
        initiateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                windowManager.removeView(audioCallView);
                windowManager.addView(callHeaderView, params);


                /*
                 * To open the chats fragment,we add to the container
                 */
                /*String docId = AppController.findDocumentIdOfReceiver(caller_id, Utilities.tsInGmt(),
                        tvCallerName.getText().toString(), imageUrl, "", false,
                        intent.getExtras().getString("callerIdentifier"), "", false);*/


                UtilityVideoCall.getInstance().setCallMinimized(true);
                UtilityVideoCall.getInstance().setFirstTimeAfterCallMinimized(true);

                if (UtilityVideoCall.getInstance().getActiveActivitiesCount() == 0) {


                    try {
                        Intent intent = new Intent(mContext, ChattingActivity.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        /*
                        intent.putExtra("receiverUid", caller_id);
                        intent.putExtra("receiverName", tvCallerName.getText().toString());
                    //    intent.putExtra("documentId", docId);
                        intent.putExtra("receiverImage", imageUrl);
                        intent.putExtra("colorCode", UtilityVideoCall.getInstance().getColorCode(5));*/


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("eventName", "callMinimized");


                        obj.put("receiverUid", caller_id);
                        obj.put("receiverName", tvCallerName.getText().toString());
                        // obj.put("documentId", docId);
                        obj.put("receiverImage", imageUrl);
                        obj.put("colorCode", UtilityVideoCall.getInstance().getColorCode(5));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //  bus.post(obj);
                }

            }
        });
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    public void turnOnScreen() {

        try {
            if (!UtilityVideoCall.getInstance().isCallMinimized()) {

                final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness = 1;

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    params.type = WindowManager.LayoutParams.TYPE_PHONE;
                } else {
                    params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

                }

                WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


                mWindowManager.updateViewLayout(audioCallView, params);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("TryWithIdenticalCatches")
    public void turnOffScreen() {


        try {

            if (!UtilityVideoCall.getInstance().isCallMinimized()) {
                final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

                params.screenBrightness = 0;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    params.type = WindowManager.LayoutParams.TYPE_PHONE;
                } else {
                    params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

                }
                WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


                mWindowManager.updateViewLayout(audioCallView, params);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void addMainLayout() {

        if (params == null) {
            params = new WindowManager.LayoutParams(


                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

            }


        }
        //Add the view to the window

        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }

        try {
            windowManager.addView(audioCallView, params);
        }catch(NullPointerException e ){e.printStackTrace();}

    }

    private void setupCallHeaderDuration() {

        stopWatchHeader.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer arg0) {
                countUpHeader = (SystemClock.elapsedRealtime() - arg0.getBase()) / 1000;

                callDurationHeader = String.format(Locale.getDefault(), "%02d:%02d:%02d", countUpHeader / 3600, countUpHeader / 60, countUpHeader % 60);


                try {
                    callHeaderTv.setText(callDurationHeader);


                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });


        stopWatchHeader.setBase(SystemClock.elapsedRealtime());
        stopWatchHeader.start();
    }

    @Override
    public void onAnswerSuccess() {

    }

    @Override
    public void onRejectSuccess() {
        onCallHangUp(2,false);
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient != null) {
                    peerConnectionClient.removeRemoteIceCandidates(candidates);

                } else {

                    Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");

                }

            }
        });
    }
    @Override
    public void onConnected() {

        handler.post(new Runnable() {
            @Override
            public void run() {


                tvReconnecting.setVisibility(View.GONE);
                connected = true;

                if (reconnectTonePlayer != null) {

                    try {
                        if (reconnectTonePlayer.isPlaying()) {
                            try {
                                reconnectTonePlayer.stop();

                                reconnectTonePlayer.release();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }


                callConnected();
                stopReconnectTimer();
            }
        });
    }

    @Override
    public void onDisconnected() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                tvReconnecting.setVisibility(View.VISIBLE);
                connected = false;

                if (reconnectTonePlayer != null) {
                    try {
                        if (reconnectTonePlayer.isPlaying()) {
                            try {
                                reconnectTonePlayer.stop();

                                reconnectTonePlayer.release();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }


                reconnectTonePlayer = MediaPlayer.create(mContext, R.raw.end_call);
                reconnectTonePlayer.setLooping(true);

                if (!connected) {

                    try {
                        reconnectTonePlayer.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    initializeReconnectTimer();
                }


            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidateRemovals(candidates);
                }
            }
        });
    }

    private void stopReconnectTimer() {
        if (reconnectTimer != null) {
            try {
                reconnectTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void initializeReconnectTimer() {


        if (reconnectTimer != null) {
            try {
                reconnectTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        reconnectTimer = new CountDownTimer(MAX_RECONNECT_TIME, 1000) {
            public void onTick(long millisUntilFinished) {

                if (connected) {
                    reconnectTimer.cancel();

                }
            }

            public void onFinish() {
                reconnectTimer.cancel();

                Toast.makeText(mContext, getResources().getString(R.string.reconnect_failed), Toast.LENGTH_LONG).show();
                onCallHangUp(2, false);
            }
        };
        reconnectTimer.start();

    }


}
