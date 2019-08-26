package com.localgenie.videocalling;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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
import com.utility.AlertProgress;
import com.utility.OnMyService;
import com.webRtc.utils.AppRTCAudioManager;
import com.webRtc.utils.AppRTCClient;
import com.webRtc.utils.DirectRTCClient;
import com.webRtc.utils.PeerConnectionClient;
import com.webRtc.utils.TextDrawable;
import com.webRtc.utils.WebSocketRTCClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.DaggerService;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static com.webRtc.utils.Constants.EXTRA_AECDUMP_ENABLED;
import static com.webRtc.utils.Constants.EXTRA_AUDIOCODEC;
import static com.webRtc.utils.Constants.EXTRA_AUDIO_BITRATE;
import static com.webRtc.utils.Constants.EXTRA_CAMERA2;
import static com.webRtc.utils.Constants.EXTRA_CAPTURETOTEXTURE_ENABLED;
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
import static com.webRtc.utils.Constants.EXTRA_VIDEO_FILE_AS_CAMERA;
import static com.webRtc.utils.Constants.EXTRA_VIDEO_FPS;
import static com.webRtc.utils.Constants.EXTRA_VIDEO_HEIGHT;
import static com.webRtc.utils.Constants.EXTRA_VIDEO_WIDTH;
import static org.webrtc.EglBase.create;



/**
 * Created by moda on 16/08/17.
 */

public class VideoCallService extends DaggerService implements AppRTCClient.SignalingEvents, PeerConnectionClient.PeerConnectionEvents, VideoCallEvents,
        VideoCallContract.View {

    /* public static final String EXTRA_CALLID = "org.appspot.apprtc.CALLID";
     public static final String EXTRA_ROOMID = "org.appspot.apprtc.ROOMID";
     public static final String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
     public static final String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
     public static final String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
     public static final String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
     public static final String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";
     public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED =
             "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER";
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
            "android.permission.RECORD_AUDIO", "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.INTERNET"
    };
    // Peer connection statistics callback period in ms.
    private static final int STAT_CALLBACK_PERIOD = 1000;
    private static final int MAX_RECONNECT_TIME = 60000;
    private final ProxyVideoSink remoteProxyRenderer = new ProxyVideoSink();
    private final ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
    private final List<VideoSink> remoteSinks = new ArrayList<>();
    // public static Bus bus = AppController.getBus();
    public boolean isVideoAvailable = true, isSwappedFeeds;
    @Inject
    VideoCallContract.Presenter presenter;
    MediaPlayer mp, reconnectTonePlayer;
    CountDownTimer timer, reconnectTimer, disconnectTimer;
    /******************************I am doing*************************/

    @Inject
    AlertProgress alertProgress;
    @Inject
    MQTTManager mqttManager;
    @Inject
    SessionManagerImpl manager;
    String ANDROID_CHANNEL_ID = "com.localgenie.videocalling";
    // String IOS_CHANNEL_ID = "com.chikeandroid.tutsplustalerts.IOS";
    String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";
    @Nullable
    private PeerConnectionClient peerConnectionClient;
    @Nullable
    private AppRTCClient appRtcClient;
    // Controls
    @Nullable
    private AppRTCClient.SignalingParameters signalingParameters;
    @Nullable
    private AppRTCAudioManager audioManager;
    private Toast logToast;
    private AppRTCClient.RoomConnectionParameters roomConnectionParameters;
    @Nullable
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    private EglBase rootEglBase = create();
    private SurfaceViewRenderer localRender;
    private SurfaceViewRenderer remoteRender;
    private RendererCommon.ScalingType scalingType;
    private boolean commandLineRun;
    private int runTimeMs;
    private boolean iceConnected;
    private boolean isError;
    private boolean callControlFragmentVisible = true;
    private long callStartedTimeMs = 0;
    private FrameLayout root;
    private Context mContext;
    private View videoCallView, callHeaderView;
    private WindowManager windowManager;
    private TextView callHeaderTv, tvReconnecting;
    private Intent intent;
    private WindowManager.LayoutParams params;
    private Handler handler;
    // String IOS_CHANNEL_NAME = "IOS CHANNEL";
    /**
     * For the fragment controls which are now moved into the UI
     */


    private ImageView mute, video;
    private TextView tvStopWatch, tvCallerName;
    private boolean isMute = false, isVideoShow = false, timerAlreadyStarted, connected, disconnect, cleanUpRequested;
    private ImageView callerImage;
    /**
     * Call control interface for container activity.
     */

    private String imageUrl = "", roomId, bookingEndtime = "";
    private ImageView initiateChat;
    private boolean isFrontCamera = false;
    private Chronometer stopWatchHeader;
    private long countUp, countUpHeader;
    private RelativeLayout parentRl;
    private SurfaceViewRenderer remoteRenderHeader;

    @SuppressLint("NewApi")
    private static void setNotificationChannel(NotificationManager notificationManager, String id, String name, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(mChannel);

        }
    }

    /******************************End doing*************************/

    @Override
    public void onCreate() {
        super.onCreate();

        if (!mqttManager.isMQTTConnected())
            mqttManager.createMQttConnection(manager.getSID(), false);
        mContext = this;
        startService(new Intent(this, OnMyService.class));

        videoCallView = LayoutInflater.from(this).inflate(R.layout.video_call_service, null);
        //  bus.register(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            videoCallView
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            videoCallView
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);

        }
        // Set window styles for fullscreen-window size. Needs to be done before
        // adding content.

        tvStopWatch = (TextView) videoCallView.findViewById(R.id.tvStopWatch);
        /*
         * For showing own camera view while making a call
         */


        addMainLayout();

        handler = new Handler(Looper.getMainLooper());
        root = (FrameLayout) videoCallView.findViewById(R.id.root);
        // Create UI controls.
        localRender = (SurfaceViewRenderer) videoCallView.findViewById(R.id.local_video_view);
        // localRender.setMirror(true);
        remoteRender = (SurfaceViewRenderer) videoCallView.findViewById(R.id.remote_video_view);
        // remoteRender.setMirror(true);
        /*
         * Mqtt
         */
       // Log.d(TAG, "onCreate: "+intent.getExtras().getBoolean("buttonClck"));

        /*
         * To check if the call screen has been opened for the incoming call or the outgoing call
         *
         */
        /*
         * For the outgoing call have to check for the
         */
        // Swap feeds on pip view click.

        if (localRender != null) {
            localRender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSwappedFeeds(!isSwappedFeeds);
                }
            });
        }

        // Show/hide call control fragment on view click.

        if (remoteRender != null) {
            remoteRender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleCallControlFragmentVisibility();
                }
            });
        }


        remoteSinks.add(remoteProxyRenderer);
        iceConnected = false;
        signalingParameters = null;


        // Create video renderers.
        localRender.init(rootEglBase.getEglBaseContext(), null);
        localRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        remoteRender.init(rootEglBase.getEglBaseContext(), null);
        remoteRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);

        localRender.setZOrderMediaOverlay(true);
        localRender.setEnableHardwareScaler(true /* enabled */);
        remoteRender.setEnableHardwareScaler(false /* enabled */);


        setSwappedFeeds(true /* isSwappedFeeds */);

        // Check for mandatory permissions.
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                logAndToast("Permission " + permission + " is not granted");
                stopSelf();

                return;
            }


        }

        initializeRxJava();
    }

    @Override
    public IBinder onBind(Intent intent) {


        this.intent = intent;
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.intent = intent;

        if (intent != null) {

            if (intent.getExtras() != null && !intent.getExtras().getBoolean("isIncomingCall", true)) {

                Log.d(TAG, "onStartCommand: " + EXTRA_ROOMID);
                try {
                    mqttManager.subscribeToTopic(MqttEvents.Call.value + "/" + intent.getExtras().getString(EXTRA_ROOMID), 0);
                    mqttManager.subscribeToTopic(MqttEvents.Calls.value + "/" + intent.getExtras().getString(EXTRA_ROOMID), 0);
                    mqttManager.subscribeToTopic(MqttEvents.CallsAvailability.value + "/" + intent.getExtras().getString(EXTRA_ROOMID), 0);
                } catch (Exception ex) {
                    Log.d(TAG, "onStartCommand: " + ex.getMessage());
                    ex.printStackTrace();
                }
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
    public void onDestroy() {

       /* ChatApiService chatApiService = ServiceFactory.createRetrofitChatService(ChatApiService.class);
        presenter.endCall(roomId, "join", chatApiService);

        if (!mqttManager.isMQTTConnected())
            mqttManager.createMQttConnection(manager.getSID(), true);*/
        if (!disconnect) {

            onCallHangUp(2, false);

        }
        //disconnect();

        // hideCameraView(false);
        super.onDestroy();
    }

    private void removeViews() {

/*
        if (!mqttManager.isMQTTConnected())
            mqttManager.createMQttConnection(manager.getSID(), true);
*/


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

                    if (videoCallView != null) {


                        /*
                         * For clearing of the full screen UI mode
                         */
                        videoCallView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        try {
                            windowManager.removeView(videoCallView);
                        } catch (IllegalArgumentException ef) {
                        }
                    }


                }
            } else {

                try {
                    if (videoCallView != null) {


                        /*
                         * For clearing of the full screen UI mode
                         */
                        videoCallView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        windowManager.removeView(videoCallView);
                    }
                } catch (IllegalArgumentException e) {


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

                rootEglBase.release();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // OnCallEvents interface implementation.
    @Override
    public void onCallHangUp(int val, boolean received) {


        if (!disconnect) {

            disconnect = true;

            /*
             * Timeout from the sender side
             */

            if (intent != null && intent.getExtras() != null) {
                try {


                    if (!received) {

                        JSONObject obj = new JSONObject();

                        obj.put("callId", roomId);
                        obj.put("userId", manager.getSID());// userId
                        obj.put("type", val);
                        obj.put("userType", 1);
                       // obj.put("action", "4");


                        mqttManager.publish(MqttEvents.Calls.value + "/" + intent.getExtras().getString("callerId"), obj, 0, false);
                        mqttManager.publish(MqttEvents.Call.value + "/" + intent.getExtras().getString("callerId"), obj, 0, false);

                       /* ChatApiService chatApiService = ServiceFactory.createRetrofitChatService(ChatApiService.class);
                        presenter.endCall(roomId, "join", chatApiService);
*/




                        //  AppController.getInstance().removeCurrentCallDetails();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        disconnect();

    }

    @Override
    public void onCameraSwitch() {

        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
        }
    }

    @Override
    public void onMute() {

        isMute = !isMute;

        if (audioManager != null) {
            audioManager.setSpeakerphoneOn(isMute);
            audioManager.setMicrophoneMute(isMute);
        }
    }

    @Override
    public void onVideoShow() {
        video();
    }

    public void video() {


        isVideoAvailable = !isVideoAvailable;
        if (isVideoAvailable) {


            if (isSwappedFeeds) {

                if (remoteRender != null)
                    remoteRender.setBackgroundColor(Color.parseColor("#00000000"));
            } else {

                if (localRender != null)
                    localRender.setBackgroundColor(Color.parseColor("#00000000"));
            }


            if (peerConnectionClient != null) {
                peerConnectionClient.startVideoSource();
            }


            if (intent != null && intent.getExtras() != null) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("callId", intent.getExtras().getString(EXTRA_ROOMID));
                    obj.put("userId", manager.getSID());//userId
                    obj.put("type", 5);
                    obj.put("userType", 1);

                    mqttManager.publish(MqttEvents.Calls.value + "/" + intent.getExtras().getString("callerId"), obj, 0, false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } else {


            if (isSwappedFeeds) {
                if (remoteRender != null)
                    remoteRender.setBackgroundColor(Color.parseColor("#000000"));
            } else {
                if (localRender != null)
                    localRender.setBackgroundColor(Color.parseColor("#000000"));
            }

            if (peerConnectionClient != null) {
                peerConnectionClient.stopVideoSource();
            }


            if (intent != null && intent.getExtras() != null) {
                try {


                    JSONObject obj = new JSONObject();
                    obj.put("callId", intent.getExtras().getString(EXTRA_ROOMID));
                    obj.put("userId", manager.getSID());
                    obj.put("type", 6);
                    obj.put("userType", 1);


                    mqttManager.publish(MqttEvents.Calls.value + "/" + intent.getExtras().getString("callerId"), obj, 0, false);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // Helper functions.
    private void toggleCallControlFragmentVisibility() {


        callControlFragmentVisible = !callControlFragmentVisible;

        try {
            if (callControlFragmentVisible) {
                parentRl.setVisibility(View.VISIBLE);
            } else {
                parentRl.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void startCall() {

        if (appRtcClient != null) {

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

        setSwappedFeeds(false /* isSwappedFeeds */);

        /*
         * To hide the controls
         */

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callControlFragmentVisible = false;
                parentRl.setVisibility(View.GONE);
            }
        }, 2000);


        // Disable statistics callback.
        peerConnectionClient.enableStatsEvents(false, STAT_CALLBACK_PERIOD);
        /* Start the stop watch */

        /*
         * By default video call is put on loudspeaker,unless speaker is moved near the ears.
         */
        if (audioManager != null)
            audioManager.setSpeakerphoneOn(true);


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


                remoteProxyRenderer.setTarget(null);
                localProxyVideoSink.setTarget(null);

                if (appRtcClient != null) {
                    appRtcClient.disconnectFromRoom();
                    appRtcClient = null;
                }
                if (localRender != null) {
                    localRender.release();
                    localRender = null;
                }

                if (remoteRender != null) {
                    remoteRender.release();
                    remoteRender = null;
                }


                if (remoteRenderHeader != null) {
                    remoteRenderHeader.release();
                    remoteRenderHeader = null;
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


        }

        if(Utility.isAppIsInBackgroundOne(this))
        {
            Log.d(TAG, "isAppIsInBackground yes: ");
        }else{
            Log.d(TAG, "isAppIsInBackground no: ");

        }
        /*if(intent.getExtras().getBoolean("buttonClck"))
        {
            ExitActivity.exitApplicationAndRemoveFromRecent(mContext);
        }*/
       /* if(ProcessLifecycleOwner.get().getLifecycle().getCurrentState() == Lifecycle.State.CREATED)
        {
            ExitActivity.exitApplicationAndRemoveFromRecent(mContext);

            Constants.isFromNotification = false;
        }*/
    }

    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and
    // are routed to UI thread.
    private void onConnectedToRoomInternal(final AppRTCClient.SignalingParameters params) {

        signalingParameters = params;

        VideoCapturer videoCapturer = null;
        if (peerConnectionParameters != null && peerConnectionParameters.videoCallEnabled) {
            videoCapturer = createVideoCapturer();
        }

        if (peerConnectionClient != null) {
            peerConnectionClient.createPeerConnection(
                    localProxyVideoSink, remoteSinks, videoCapturer, signalingParameters);


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

    private void setdisconnectTime(String bookingEndtime) {
        long futureduration;
        if (!bookingEndtime.isEmpty()) {
            futureduration = (Long.parseLong(bookingEndtime) * 1000) - ((System.currentTimeMillis()));
            Log.d(TAG, "onTickfutureduration= " + futureduration);
            if (futureduration > 0) {
                if (disconnectTimer != null) {
                    disconnectTimer.cancel();
                    disconnectTimer = null;
                }
                disconnectTimer = new CountDownTimer(futureduration, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "onTick:" + millisUntilFinished / 1000);
                        if ((millisUntilFinished < 121000) &&
                                (millisUntilFinished > 119000)) {

                            Log.d("onTickSystem: ", "" + System.currentTimeMillis());
                            //show notification
                           /* Notification.Builder nb = getAndroidChannelNotification(getString(R.string.telecallalert), getString(R.string.two_min_call_cut));

                            getManager().notify(101, nb.build());*/

                            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                    R.drawable.ic_notification_logo);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(VideoCallService.this, "foreground")
                                    .setContentTitle(getString(R.string.telecallalert))
                                    .setTicker("")
                                    .setContentText(getString(R.string.two_min_call_cut))
                                    .setSmallIcon(R.drawable.ic_notification_logo)
                                    .setLargeIcon(icon)
                                    .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE) //Important for heads-up notification
                                    .setPriority(Notification.PRIORITY_MAX) //Important for heads-up notification;
                                    .setChannelId("CallCut");
//                                    .setContentIntent(pendingIntent)
//                                    .setOngoing(true);
                            Notification notification = notificationBuilder.build();
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            setNotificationChannel(notificationManager, "CallCut", getString(R.string.notificationChannelNameCall), getString(R.string.notificationChannelDescriptionCall));
                            notificationManager.notify(137, notification);
                        }
                    }

                    @Override
                    public void onFinish() {
                        // TODO Auto-generated method stub
                        //cut the call at this time.
                        Log.d(TAG, "onTick: onFinish");
                        Log.d(TAG, "onTick: onFinish" + System.currentTimeMillis());

                      ChatApiService chatApiService = ServiceFactory.createRetrofitChatService(ChatApiService.class);
                        presenter.endCall(roomId, "join", chatApiService);
 /*
                        if (!mqttManager.isMQTTConnected())
                            mqttManager.createMQttConnection(manager.getSID(), true);*/
                        if (!disconnect)
                            onCallHangUp(2, false);
                    }
                }.start();
            }
        }


    }

    private void reportError(final String description) {
        handler.post(new Runnable() {
            @Override
            public void run() {


                Toast.makeText(mContext, description, Toast.LENGTH_SHORT).show();


            }
        });
    }

    private @Nullable
    VideoCapturer createVideoCapturer() {
        final VideoCapturer videoCapturer;
        String videoFileAsCamera = intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);
        if (videoFileAsCamera != null) {
            try {
                videoCapturer = new FileVideoCapturer(videoFileAsCamera);
            } catch (IOException e) {
                reportError("Failed to open video file for emulated camera");
                return null;
            }
        } else if (useCamera2()) {
            if (!captureToTexture()) {
                reportError(getString(R.string.camera2_texture_only_error));
                return null;
            }


            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {

            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            reportError("Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && intent.getBooleanExtra(EXTRA_CAMERA2, true);
    }

    private boolean captureToTexture() {
        return intent.getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false);
    }

    private @Nullable
    VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {

                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else

        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {

                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private void createOffer(/*AppRTCClient.SignalingParameters signalingParameters*/) {
        if (signalingParameters.initiator) {
            //logAndToast("Creating OFFER...");
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createOffer();
        } else {
            if (signalingParameters.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(signalingParameters.offerSdp);
                // logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
            if (signalingParameters.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : signalingParameters.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
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
    public void onChannelClose() {
        Log.d(TAG, "onChannelClose: " + 1);

/*
        handler.post(new Runnable() {
            @Override
            public void run() {
                //   logAndToast("Remote end hung up; dropping PeerConnection");


                disconnect();


            }
        });
*/
    }

    @Override
    public void onChannelError(final String description) {
        Log.d(TAG, "onChannelError: " + 1);

        reportError(description);
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
    public void onIceCandidatesRemoved(IceCandidate[] candidates) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidateRemovals(candidates);
                }
            }
        });
    }

    @Override
    public void onIceConnected() {

    }

    @Override
    public void onIceDisconnected() {
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

    private void initializeReconnectTimer() {


        if (reconnectTimer != null) {
            try {
                reconnectTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        reconnectTimer = new CountDownTimer(MAX_RECONNECT_TIME, STAT_CALLBACK_PERIOD) {
            public void onTick(long millisUntilFinished) {

                if (connected) {
                    reconnectTimer.cancel();

                }
            }

            public void onFinish() {
                reconnectTimer.cancel();
                Toast.makeText(mContext, getResources().getString(R.string.reconnect_failed), Toast.LENGTH_LONG).show();
               /* ChatApiService chatApiService = ServiceFactory.createRetrofitChatService(ChatApiService.class);
                presenter.endCall(roomId, "join", chatApiService);

                if (!mqttManager.isMQTTConnected())
                    mqttManager.createMQttConnection(manager.getSID(), true);*/
                if (!disconnect)
                    onCallHangUp(2, false);
            }
        };
        reconnectTimer.start();

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
    public void onPeerConnectionClosed() {
        Log.d(TAG, "onPeerConnectionClosed: " + 1);

    }

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {
        Log.d(TAG, "onPeerConnectionStatsReady: " + 1);

    }

    @Override
    public void onPeerConnectionError(final String description) {
        reportError(description);
    }

    private void initializeRxJava() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String objects) {
                Log.d("TAG", "onNextVideCall: " + objects);

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

    public void onMessage(JSONObject object) throws JSONException {
        Log.d("onMessage:action2 ", object.toString());
       // Log.d(TAG, "onMessage: "+object.getString("eventName"));

        try {
            Log.d("log7", object.toString());

            if (object.has("action")) {
                Log.d("log6", object.toString());

                if (object.getString("action").equals(CallActions.NOT_ANSWER_OR_LEFT.value)) {
                    Log.d("onMessage:action2 ", object.toString());
                    Log.d("log5", object.toString());

                    if (roomId.equals(intent.getStringExtra(EXTRA_ROOMID))) {
                        Log.d("log4", object.toString());

// tvStopWatch.setText(getString(R.string.connecting));
                        UtilityVideoCall.getInstance().setActiveOnACall(true, true);
// countdowntimer = setReverseTimer(tvStopWatch, 120);
                    } else {
                        Log.d("log3", object.toString());

                        UtilityVideoCall.getInstance().setActiveOnACall(false, true);
                        onRejectSuccess();

                    }

              /*  onRejectSuccess();
                UtilityVideoCall.getInstance().setActiveOnACall(false, true);*/
                } else if (object.getString("action").equals(CallActions.JOIN_ON_CALL.value)) {
                    Log.d("log2", object.toString());

                    if (roomId.equals(intent.getStringExtra(EXTRA_ROOMID))) {
                        bookingEndtime = object.optString("bookingEndtime");
                        setdisconnectTime(bookingEndtime);
                    }
                    tvStopWatch.setText(getString(R.string.connecting));
                    UtilityVideoCall.getInstance().setActiveOnACall(true, true);
                } else if (object.getString("action").equals(CallActions.CALL_ENDED.value)) {
                    Log.d("log1", object.toString());
                    bookingEndtime = "";
                    /*roomId = "";
                    SharedPreferences m_prefs =
                            getSharedPreferences("global_settings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = m_prefs.edit();
                    editor.putString("call_id", "");
                    editor.apply();*/
                    UtilityVideoCall.getInstance().setActiveOnACall(false, true);
                    onRejectSuccess();

                }
            }else{
                if (object.getString("eventName").equals(MqttEvents.CallsAvailability.value)) {

                    if (intent != null) {
                        Bundle extras = intent.getExtras();

                        if (object.getInt("status") == 0) {

                            /*
                             * Receiver is busy
                             */
                            if (root != null) {

                                Toast.makeText(this, extras.getString("callerName") + " " + getString(R.string.busy), Toast.LENGTH_SHORT).show();
                            }


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    stopSelf();
                                }
                            }, 2000);

                            if (extras != null) {

                                Toast.makeText(this, extras.getString("callerName") + " " + getString(R.string.busy), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //  Bundle extras = intent.getExtras();

                            try {
                                /*
                                 * I put myself as busy and make the call request to the receiver
                                 */
                                JSONObject obj = new JSONObject();
                                obj.put("status", 0);


                                mqttManager.publish(MqttEvents.CallsAvailability.value + "/" + manager.getSID(), obj, 0, true);
                                UtilityVideoCall.getInstance().setActiveOnACall(true, true);

                                obj = new JSONObject();
                                obj.put("callerId", manager.getSID());
                                obj.put("callId", extras.getString(EXTRA_ROOMID));
                                obj.put("bookingId", extras.getString("BookingID"));
                                obj.put("callType", "1");
                                obj.put("callerName", manager.getFirstName() + " " + manager.getLastName());
                                obj.put("callerImage", manager.getProfilePicUrl());
                                obj.put("callerIdentifier", extras.getString("callerIdentifier"));

                                obj.put("type", 0);
                                obj.put("userType", 1);

                                /*
                                 * CalleeId although not required but can be used in future on server so using it
                                 *
                                 *
                                 * CalleeId contains the receiverUid,to whom the call has been made
                                 *
                                 * *//*

                                 *//*
                         * Type-0---call initiate request,on receiving the call initiate request,receiver will set his status as busy,so nobody else can call him
                         *
                         * /


                        obj.put("type", 0);
/*
 * Not making any message of call signalling as being persistent intentionally
 */

                                Log.d(TAG, "onMessageMESSAGE: " + obj.toString());
                                mqttManager.publish(MqttEvents.Calls.value + "/" + extras.getString("callerId"), obj, 0, false);

                                UtilityVideoCall.getInstance().setActiveCallId(extras.getString(EXTRA_ROOMID));
                                UtilityVideoCall.getInstance().setActiveCallerId(extras.getString("callerId"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (object.getString("eventName").equals(MqttEvents.Calls.value)) {

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
/*                    if (roomId != null) {


                        if (object.getString("callId").equals(roomId)) {

                            onCallHangUp(2, true);
                        }
                    } else {
                        onCallHangUp(2, true);

                    }*/

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
                        case 5:

                            if (roomId != null) {


                                if (object.getString("callId").equals(roomId)) {

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (remoteRenderHeader != null) {
                                                remoteRenderHeader.setBackgroundColor(Color.parseColor("#00000000"));

                                            }
                                            if (isSwappedFeeds) {
                                                if (localRender != null)
                                                    localRender.setBackgroundColor(Color.parseColor("#00000000"));
                                            } else {
                                                if (remoteRender != null)
                                                    remoteRender.setBackgroundColor(Color.parseColor("#00000000"));


                                            }
                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (remoteRenderHeader != null) {
                                            remoteRenderHeader.setBackgroundColor(Color.parseColor("#00000000"));

                                        }
                                        if (isSwappedFeeds) {
                                            if (localRender != null)
                                                localRender.setBackgroundColor(Color.parseColor("#00000000"));
                                        } else {
                                            if (remoteRender != null)
                                                remoteRender.setBackgroundColor(Color.parseColor("#00000000"));


                                        }
                                    }
                                });

                            }
                            // remoteRender.setBackgroundColor(Color.parseColor("#00000000"));

                            break;
                        case 6:

                            if (roomId != null) {


                                if (object.getString("callId").equals(roomId)) {

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (remoteRenderHeader != null) {
                                                remoteRenderHeader.setBackgroundColor(Color.parseColor("#4e4e4e"));

                                            }

                                            if (isSwappedFeeds) {

                                                if (localRender != null)
                                                    localRender.setBackgroundColor(Color.parseColor("#4e4e4e"));
                                            } else {

                                                if (remoteRender != null)
                                                    remoteRender.setBackgroundColor(Color.parseColor("#4e4e4e"));


                                            }


                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (remoteRenderHeader != null) {
                                            remoteRenderHeader.setBackgroundColor(Color.parseColor("#4e4e4e"));

                                        }

                                        if (isSwappedFeeds) {

                                            if (localRender != null)
                                                localRender.setBackgroundColor(Color.parseColor("#4e4e4e"));
                                        } else {

                                            if (remoteRender != null)
                                                remoteRender.setBackgroundColor(Color.parseColor("#4e4e4e"));


                                        }


                                    }
                                });

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
                            break;

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void startCallProcedure(boolean incomingCall) {


        // Get Intent parameters.
        if (intent != null) {


            Uri roomUri = intent.getData();
            if (roomUri == null) {


                stopSelf();
                return;
            }

            // Get Intent parameters.
            roomId = intent.getStringExtra(EXTRA_ROOMID);
            if (roomId == null || roomId.length() == 0) {

                stopSelf();
                return;
            }


            //  AppController.getInstance().saveCurrentCallDetails(roomId, intent.getStringExtra("callerId"));

            boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
            boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false);

            int videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0);
            int videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0);

            PeerConnectionClient.DataChannelParameters dataChannelParameters = null;
            if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, false)) {
                dataChannelParameters = new PeerConnectionClient.DataChannelParameters(intent.getBooleanExtra(EXTRA_ORDERED, true),
                        intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1),
                        intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(EXTRA_PROTOCOL),
                        intent.getBooleanExtra(EXTRA_NEGOTIATED, false), intent.getIntExtra(EXTRA_ID, -1));
            }


            peerConnectionParameters =
                    new PeerConnectionClient.PeerConnectionParameters(intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), loopback,
                            tracing, videoWidth, videoHeight, intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
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
            windowManager.addView(videoCallView, params);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    private void showCallControlsUi() {


        callHeaderView = LayoutInflater.from(this).inflate(R.layout.video_call_floating_widget, null);
        remoteRenderHeader = (SurfaceViewRenderer) callHeaderView.findViewById(R.id.remoteVideoHeader);

        remoteRenderHeader.init(rootEglBase.getEglBaseContext(), null);

        remoteProxyRenderer.setTarget(remoteRenderHeader);
        callHeaderTv = (TextView) callHeaderView.findViewById(R.id.duration);
        stopWatchHeader = (Chronometer) callHeaderView.findViewById(R.id.chrono);
        parentRl = (RelativeLayout) videoCallView.findViewById(R.id.container_rl);
        parentRl.setVisibility(View.VISIBLE);


        initiateChat = (ImageView) videoCallView.findViewById(R.id.initiateChat);
        // Create UI controls.
        final ImageButton camera = (ImageButton) videoCallView.findViewById(R.id.camera);
        ImageView cancelCall = (ImageButton) videoCallView.findViewById(R.id.cancelCall);
        mute = (ImageButton) videoCallView.findViewById(R.id.mute);
        video = (ImageButton) videoCallView.findViewById(R.id.video);
        camera.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);


        tvCallerName = (TextView) videoCallView.findViewById(R.id.tvCallerName);
        tvReconnecting = (TextView) videoCallView.findViewById(R.id.tvReconnecting);

        callerImage = (ImageView) videoCallView.findViewById(R.id.userImage);


        final String caller_id = intent.getStringExtra("callerId");




        /*
         * If userId doesn't exists in contact
         */
        if (intent != null) {
            Bundle extras = intent.getExtras();
            tvCallerName.setText(extras.getString("callerName"));

            imageUrl = extras.getString("callerImage");

            if (imageUrl == null || imageUrl.isEmpty()) {
                try {
                    String callerIdentifier = extras.getString("callerIdentifier");

                    if (callerIdentifier != null)
                        callerIdentifier = (callerIdentifier.trim()).charAt(0) + "";
                    else
                        callerIdentifier = "+";
                    callerImage.setImageDrawable(TextDrawable.builder()


                            .beginConfig()
                            .textColor(Color.WHITE)
                            .useFont(Typeface.DEFAULT)
                            .fontSize(24 * (int) getResources().getDisplayMetrics().density) /* size in px */
                            .bold()
                            .toUpperCase()
                            .endConfig()


                            .buildRound(callerIdentifier, Color.parseColor(UtilityVideoCall.getInstance().getColorCode(5))));

                } catch (Exception ignored) {

                }
            } else {

                try {


                    Glide.with(this)
                            .load(imageUrl)
                            .apply(Utility.createGlideOption(this))
                            .into(callerImage);


                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


            }
        }
        // }


        // Add buttons click events.
        cancelCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + roomId);
                ChatApiService chatApiService = ServiceFactory.createRetrofitChatService(ChatApiService.class);
                presenter.endCall(roomId, "join", chatApiService);
                onCallHangUp(2, false);

               /* if (intent != null) {
                    ChatApiService chatApiService = ServiceFactory.createRetrofitChatService(ChatApiService.class);
                    presenter.endCall(roomId, "call", chatApiService);

                } else {
                    onCallHangUp(2, false);
                }*/
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCameraSwitch();
                isFrontCamera = !isFrontCamera;

                camera.setSelected(isFrontCamera);


            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMute();
                mute.setSelected(isMute);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onVideoShow();
                isVideoShow = !isVideoShow;
                video.setSelected(isVideoShow);
            }
        });

        final WindowManager.LayoutParams params;// = new WindowManager.LayoutParams();

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


        callHeaderView.setOnTouchListener(new View.OnTouchListener() {
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

                            remoteProxyRenderer.setTarget(isSwappedFeeds ? localRender : remoteRender);


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
                return true;
            }
        });

        /*
         *
         */

        initiateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeView(videoCallView);
                remoteProxyRenderer.setTarget(remoteRenderHeader);

                windowManager.addView(callHeaderView, params);
                /*
                 * To open the chats fragment,we add to the container
                 */
               /* String docId = AppController.findDocumentIdOfReceiver(caller_id, Utilities.tsInGmt(), tvCallerName.getText().toString(),
                        imageUrl, "", false, intent.getExtras().getString("callerIdentifier"), "", false);*/

                UtilityVideoCall.getInstance().setCallMinimized(true);
                UtilityVideoCall.getInstance().setFirstTimeAfterCallMinimized(true);
                if (UtilityVideoCall.getInstance().getActiveActivitiesCount() == 0) {


                    try {
                        Intent intent = new Intent(mContext, ChattingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       /* intent.putExtra("receiverUid", caller_id);
                        intent.putExtra("receiverName", tvCallerName.getText().toString());
                        //  intent.putExtra("documentId", docId);
                        intent.putExtra("receiverImage", imageUrl);
                        intent.putExtra("colorCode", UtilityVideoCall.getInstance().getColorCode(5));
                      */
                        startActivity(intent);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {

                  /*  JSONObject obj = new JSONObject();
                    try {
                        obj.put("eventName", "callMinimized");


                        obj.put("receiverUid", caller_id);
                        obj.put("receiverName", tvCallerName.getText().toString());
                        // obj.put("documentId", docId);
                        obj.put("receiverImage", imageUrl);
                        obj.put("colorCode", UtilityVideoCall.getInstance().getColorCode(5));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    // bus.post(obj);
                }
            }
        });


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

    private void setupCallHeaderDuration() {

        stopWatchHeader.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer arg0) {
                countUpHeader = (SystemClock.elapsedRealtime() - arg0.getBase()) / 1000;


                try {
                    callHeaderTv.setText(String.format(Locale.US, "%02d:%02d:%02d", countUpHeader / 3600, countUpHeader / 60, countUpHeader % 60));


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
        ChatApiService chatApiService = ServiceFactory.createRetrofitChatService(ChatApiService.class);
        presenter.endCall(roomId, "join", chatApiService);
        onCallHangUp(2, false);
    }

    private void setSwappedFeeds(boolean isSwappedFeeds) {

        this.isSwappedFeeds = isSwappedFeeds;
        localProxyVideoSink.setTarget(isSwappedFeeds ? remoteRender : localRender);
        remoteProxyRenderer.setTarget(isSwappedFeeds ? localRender : remoteRender);


        if (remoteRender != null) {
            remoteRender.setMirror(isSwappedFeeds);
        }

        if (localRender != null) {
            localRender.setMirror(!isSwappedFeeds);
        }

        //For the remote header
        if (remoteRenderHeader != null)
            remoteRenderHeader.setMirror(isSwappedFeeds);


    }

    private static class ProxyVideoSink implements VideoSink {
        private VideoSink target;

        @Override
        synchronized public void onFrame(VideoFrame frame) {
            if (target == null) {
                Log.d(TAG, "Dropping frame in proxy because target is null.");
                return;
            }

            target.onFrame(frame);
        }

        synchronized public void setTarget(VideoSink target) {
            this.target = target;
        }
    }



}