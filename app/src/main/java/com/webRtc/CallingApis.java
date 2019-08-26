package com.webRtc;

/**
 * Created by moda on 04/05/17.
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.webkit.URLUtil;


import com.localgenie.R;
import com.localgenie.home.MainActivity;
import com.localgenie.utilities.LSPApplication;
import com.localgenie.videocalling.AudioCallService;
import com.localgenie.videocalling.UtilityVideoCall;
import com.localgenie.videocalling.VideoCallService;
import com.pojo.callpojo.NewCallData;
import com.webRtc.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;



public class CallingApis {

    /* Variables for Audio Video User */


    private static final String TAG = "CallingApis";


    public static void OpenIncomingCallScreen(NewCallData newCallData, Context context) {
        try {
            SharedPreferences m_prefs =
                    context.getSharedPreferences("global_settings", Context.MODE_PRIVATE);
            /* Get the mobile number of the user from shared preferences */
            String previousCallId = m_prefs.getString("call_id", "");



            if (!previousCallId.contentEquals(newCallData.getCallId())) {

                UtilityVideoCall.getInstance().setActiveCallId(newCallData.getCallId());
                UtilityVideoCall.getInstance().setActiveCallerId(newCallData.getUserId());
                /*
                 * Type 0
                 */
                Intent incomingScreen = new Intent("com.localgenie.videocalling.IncomingCallScreen");
                incomingScreen.putExtra("callerImage", newCallData.getUserImage());
                incomingScreen.putExtra("callerName", newCallData.getUserName());
                incomingScreen.putExtra("BookingId", newCallData.getBookingId());
                incomingScreen.putExtra("callerId", newCallData.getUserId());
                incomingScreen.putExtra("callId", newCallData.getCallId());
                incomingScreen.putExtra("roomId", newCallData.getRoom());
                if(newCallData.getType().equals("audio")) {
                    incomingScreen.putExtra("callType", "0"); //Audio call
                }  else {
                    incomingScreen.putExtra("callType", "1"); //video call
                }  incomingScreen.putExtra("callerIdentifier", newCallData.getUserName());


//                incomingScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                incomingScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(context, (int) System.currentTimeMillis(), incomingScreen,
                                0);
                pendingIntent.send();

                /* Save this call id in shared preferences */
                SharedPreferences.Editor editor = m_prefs.edit();
                editor.putString("call_id", newCallData.getCallId());
                editor.apply();
            }
//                } else {
//            /* Register an event to Audio Call or Video call to signial that this call is no longer live */
//                    bus.register("Cancel Call");
//                }
            //}
        } catch (Exception e) {
            //   bus.register("Cancel Call"); i am doing
            e.printStackTrace();
        }
    }

    /**
     * Have to open the incoming call screen
     */

//    public static void OpenIncomingCallScreen(String args, Context context) {
    public static void OpenIncomingCallScreen(JSONObject data, Context context) {

        try {
            SharedPreferences m_prefs =
                    context.getSharedPreferences("global_settings", Context.MODE_PRIVATE);
            /* Get the mobile number of the user from shared preferences */

            String previousCallId = m_prefs.getString("call_id", "");

                /*if (!previousCallId.contentEquals(data.getString("callId"))) {*/
            Log.d(TAG, "OpenIncomingCallScreen: "+ previousCallId  + "   "+ data.getString("callId"));
                if(!previousCallId.contentEquals(data.getString("callId"))){

                    Log.d(TAG, "OpenIncomingCallScreen1:"+ previousCallId  + "   "+ data.getString("callId"));

                    /* Save this call id in shared preferences */
                    SharedPreferences.Editor editor = m_prefs.edit();
                    editor.putString("call_id", data.getString("callId"));
                    editor.apply();

                    UtilityVideoCall.getInstance().setActiveCallId(data.getString("callId"));
                    UtilityVideoCall.getInstance().setActiveCallerId(data.getString("userId"));
                    /*
                     * Type 0
                     */
                    Intent incomingScreen = new Intent("com.localgenie.videocalling.IncomingCallScreen");
                    incomingScreen.putExtra("callerImage", data.getString("userImage"));
                    incomingScreen.putExtra("callerName", data.getString("userName"));
                    incomingScreen.putExtra("BookingId", data.getString("bookingId"));
                    incomingScreen.putExtra("callerId", data.getString("userId"));
                    incomingScreen.putExtra("callId", data.getString("callId"));
                    incomingScreen.putExtra("roomId", data.getString("room"));

                    //  incomingScreen.putExtra("callType", data.getString("callType"));
                    if (data.getString("type").equals("audio")) {
                        incomingScreen.putExtra("callType", "0"); //Audio call
                    } else {
                        incomingScreen.putExtra("callType", "1"); //video call
                    }
                    //incomingScreen.putExtra("callerIdentifier", data.getString("callerIdentifier"));
                    // incomingScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    incomingScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), incomingScreen,
                                    0);
                    pendingIntent.send();
                  //  context.startActivity(incomingScreen);


                }

        } catch (JSONException e) {
            Log.d(TAG, "OpenIncomingCallScreen: "+e.getMessage());
            //   bus.register("Cancel Call"); i am doing
            e.printStackTrace();
        } catch (PendingIntent.CanceledException e) {
            //  bus.register("Cancel Call"); i am doing
            e.printStackTrace();
        }
    }

    /* This API is used to start audio or Video call */
    public static void startCall(Context context, String callType,String roomId, String call_id, String callee_caller_id, String callee_caller_name, String callee_caller_image,
                                 boolean isIncomingCall, String callerIdentifier,String bookingId) {




        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);


        String keyprefResolution = context.getString(R.string.pref_resolution_key);
        String keyprefFps = context.getString(R.string.pref_fps_key);
        String keyprefVideoBitrateType = context.getString(R.string.pref_maxvideobitrate_key);
        String keyprefVideoBitrateValue = context.getString(R.string.pref_maxvideobitratevalue_key);
        String keyprefAudioBitrateType = context.getString(R.string.pref_startaudiobitrate_key);
        String keyprefAudioBitrateValue = context.getString(R.string.pref_startaudiobitratevalue_key);
        String keyprefRoomServerUrl = context.getString(R.string.pref_room_server_url_key);


        String roomUrl = context.getString(R.string.pref_room_server_url_default);
               /* sharedPref.getString(keyprefRoomServerUrl,
                context.getString(R.string.pref_room_server_url_default));
*/

        boolean videoCallEnabled = true;
        Intent intent;


        /*
         * CallType----
         *
         * 0-Audio
         * 1-Video
         *
         *
         */


        // Video call enabled flag.
        if (callType.contentEquals("0")) {
            videoCallEnabled = false;

            intent = new Intent(context, AudioCallService.class);

        } else {


            intent = new Intent(context, VideoCallService.class);
        }

        /* -----------------------------------------------------*/
        /*
         * Code to make calls work on the MQtt
         */

        intent.putExtra("isIncomingCall", isIncomingCall);
        intent.putExtra("BookingID", bookingId);

        intent.putExtra("callType", callType);

        intent.putExtra("callerId", callee_caller_id);


        intent.putExtra("callerName", callee_caller_name);
        intent.putExtra("callerImage", callee_caller_image);

        intent.putExtra("callerIdentifier", callerIdentifier);
//        intent.putExtra(Constants.EXTRA_ROOMID, roomId);
        intent.putExtra(Constants.EXTRA_ROOMID, call_id);


        /* -----------------------------------------------------*/


        // Use Camera2 option.
        boolean useCamera2 = sharedPrefGetBoolean(R.string.pref_camera2_key, Constants.EXTRA_CAMERA2,
                R.string.pref_camera2_default, sharedPref, context);

        // Get default codecs.
        String videoCodec = sharedPrefGetString(R.string.pref_videocodec_key,
                Constants.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, sharedPref, context);
        String audioCodec = sharedPrefGetString(R.string.pref_audiocodec_key,
                Constants.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, sharedPref, context);

        // Check HW codec flag.
        boolean hwCodec = sharedPrefGetBoolean(R.string.pref_hwcodec_key,
                Constants.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, sharedPref, context);

        // Check Capture to texture.
        boolean captureToTexture = sharedPrefGetBoolean(R.string.pref_capturetotexture_key,
                Constants.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                sharedPref, context);

        // Check FlexFEC.
        boolean flexfecEnabled = sharedPrefGetBoolean(R.string.pref_flexfec_key,
                Constants.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, sharedPref, context);

        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPrefGetBoolean(R.string.pref_noaudioprocessing_key,
                Constants.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
                sharedPref, context);

        boolean aecDump = sharedPrefGetBoolean(R.string.pref_aecdump_key,
                Constants.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, sharedPref, context);

        boolean saveInputAudioToFile =
                sharedPrefGetBoolean(R.string.pref_enable_save_input_audio_to_file_key,
                        Constants.EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED,
                        R.string.pref_enable_save_input_audio_to_file_default, sharedPref, context);

        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = sharedPrefGetBoolean(R.string.pref_opensles_key,
                Constants.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, sharedPref, context);

        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = sharedPrefGetBoolean(R.string.pref_disable_built_in_aec_key,
                Constants.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                sharedPref, context);

        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = sharedPrefGetBoolean(R.string.pref_disable_built_in_agc_key,
                Constants.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                sharedPref, context);

        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = sharedPrefGetBoolean(R.string.pref_disable_built_in_ns_key,
                Constants.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                sharedPref, context);

        // Check Disable gain control
        boolean disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
                R.string.pref_disable_webrtc_agc_and_hpf_key, Constants.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
                R.string.pref_disable_webrtc_agc_and_hpf_key, sharedPref, context);

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;


        String resolution =
                sharedPref.getString(keyprefResolution, context.getString(R.string.pref_resolution_default));
        String[] dimensions = resolution.split("[ x]+");
        if (dimensions.length == 2) {
            try {
                videoWidth = Integer.parseInt(dimensions[0]);
                videoHeight = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                videoWidth = 0;
                videoHeight = 0;
                Log.e(TAG, "Wrong video resolution setting: " + resolution);
            }

        }

        // Get camera fps from settings.
        int cameraFps = 0;


        String fps = sharedPref.getString(keyprefFps, context.getString(R.string.pref_fps_default));
        String[] fpsValues = fps.split("[ x]+");
        if (fpsValues.length == 2) {
            try {
                cameraFps = Integer.parseInt(fpsValues[0]);
            } catch (NumberFormatException e) {

                Log.e(TAG, "Wrong camera fps setting: " + fps);
            }

        }


        // Get video and audio start bitrate.
        int videoStartBitrate = 0;


        String bitrateTypeDefault = context.getString(R.string.pref_maxvideobitrate_default);
        String bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(
                    keyprefVideoBitrateValue, context.getString(R.string.pref_maxvideobitratevalue_default));
            videoStartBitrate = Integer.parseInt(bitrateValue);

        }

        int audioStartBitrate = 0;


        bitrateTypeDefault = context.getString(R.string.pref_startaudiobitrate_default);
        bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(
                    keyprefAudioBitrateValue, context.getString(R.string.pref_startaudiobitratevalue_default));
            audioStartBitrate = Integer.parseInt(bitrateValue);

        }


        boolean tracing = sharedPrefGetBoolean(R.string.pref_tracing_key, Constants.EXTRA_TRACING,
                R.string.pref_tracing_default, sharedPref, context);

        // Check Enable RtcEventLog.
        boolean rtcEventLogEnabled = sharedPrefGetBoolean(R.string.pref_enable_rtceventlog_key,
                Constants.EXTRA_ENABLE_RTCEVENTLOG, R.string.pref_enable_rtceventlog_default,
                sharedPref, context);

        // Get datachannel options
        boolean dataChannelEnabled = sharedPrefGetBoolean(R.string.pref_enable_datachannel_key,
                Constants.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                sharedPref, context);
        boolean ordered = sharedPrefGetBoolean(R.string.pref_ordered_key, Constants.EXTRA_ORDERED,
                R.string.pref_ordered_default, sharedPref, context);
        boolean negotiated = sharedPrefGetBoolean(R.string.pref_negotiated_key,
                Constants.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, sharedPref, context);
        int maxRetrMs = sharedPrefGetInteger(R.string.pref_max_retransmit_time_ms_key,
                Constants.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                sharedPref, context);
        int maxRetr =
                sharedPrefGetInteger(R.string.pref_max_retransmits_key, Constants.EXTRA_MAX_RETRANSMITS,
                        R.string.pref_max_retransmits_default, sharedPref, context);
        int id = sharedPrefGetInteger(R.string.pref_data_id_key, Constants.EXTRA_ID,
                R.string.pref_data_id_default, sharedPref, context);
        String protocol = sharedPrefGetString(R.string.pref_data_protocol_key,
                Constants.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, sharedPref, context);

        // Start AppRTCMobile activity.

        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);

            intent.setData(uri);

            intent.putExtra(Constants.EXTRA_LOOPBACK, false);
            intent.putExtra(Constants.EXTRA_VIDEO_CALL, videoCallEnabled);

            intent.putExtra(Constants.EXTRA_CAMERA2, useCamera2);
            intent.putExtra(Constants.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(Constants.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(Constants.EXTRA_VIDEO_FPS, cameraFps);

            intent.putExtra(Constants.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(Constants.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(Constants.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(Constants.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(Constants.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
            intent.putExtra(Constants.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            intent.putExtra(Constants.EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(Constants.EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED, saveInputAudioToFile);
            intent.putExtra(Constants.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(Constants.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
            intent.putExtra(Constants.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
            intent.putExtra(Constants.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
            intent.putExtra(Constants.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF);
            intent.putExtra(Constants.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(Constants.EXTRA_AUDIOCODEC, audioCodec);

            intent.putExtra(Constants.EXTRA_TRACING, tracing);
            intent.putExtra(Constants.EXTRA_ENABLE_RTCEVENTLOG, rtcEventLogEnabled);

            intent.putExtra(Constants.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);

            if (dataChannelEnabled) {
                intent.putExtra(Constants.EXTRA_ORDERED, ordered);
                intent.putExtra(Constants.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs);
                intent.putExtra(Constants.EXTRA_MAX_RETRANSMITS, maxRetr);
                intent.putExtra(Constants.EXTRA_PROTOCOL, protocol);
                intent.putExtra(Constants.EXTRA_NEGOTIATED, negotiated);
                intent.putExtra(Constants.EXTRA_ID, id);
            }



            Log.d("log1",context.toString()+" "+LSPApplication.getInstance());

            //context.startService(intent);

            context.startService(intent);

        }
    }

    public static void initiateCall(Context context, String callee_caller_id, String receiverName, String receiverImage, String callType, String receiverIdentifier,String roomId, String callId,String bookingId,boolean isInComing) {

        /* Initiate call init socket API */

        /*
         * MQtt
         */
        // callInit(context, callType, to, call_id);
        /*
         * To identify that the the call screen to be opened is for the outgoing call
         */
        UtilityVideoCall.getInstance().setActiveCallId(callId);

        startCall(context, callType,roomId, callId, callee_caller_id, receiverName, receiverImage, isInComing, receiverIdentifier, bookingId);

        /* Save this call id in shared preferences */
        SharedPreferences m_prefs =
                context.getSharedPreferences("global_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = m_prefs.edit();
        editor.putString("call_id", callId);
        editor.apply();
    }


    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private static String sharedPrefGetString(
            int attributeId, String intentName, int defaultId, SharedPreferences sharedPref, Context context) {
        String defaultValue = context.getString(defaultId);

        String attributeName = context.getString(attributeId);
        return sharedPref.getString(attributeName, defaultValue);


    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private static boolean sharedPrefGetBoolean(
            int attributeId, String intentName, int defaultId, SharedPreferences sharedPref, Context context) {
        boolean defaultValue = Boolean.parseBoolean(context.getString(defaultId));

        String attributeName = context.getString(attributeId);
        return sharedPref.getBoolean(attributeName, defaultValue);

    }


    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private static int sharedPrefGetInteger(
            int attributeId, String intentName, int defaultId, SharedPreferences sharedPref, Context context) {
        String defaultString = context.getString(defaultId);
        int defaultValue = Integer.parseInt(defaultString);

        String attributeName = context.getString(attributeId);
        String value = sharedPref.getString(attributeName, defaultString);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {

            return defaultValue;

        }
    }

    private static boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }
        return false;
    }
}