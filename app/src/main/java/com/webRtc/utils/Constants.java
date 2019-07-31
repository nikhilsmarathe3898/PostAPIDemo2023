package com.webRtc.utils;


import com.localgenie.BuildConfig;

public class Constants {


    private static String applicationId = BuildConfig.APPLICATION_ID;
    public static final String EXTRA_ROOMID = applicationId + ".ROOMID";
    public static final String EXTRA_URLPARAMETERS = applicationId + ".URLPARAMETERS";
    public static final String EXTRA_LOOPBACK = applicationId + ".LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = applicationId + ".VIDEO_CALL";
    public static final String EXTRA_CAMERA2 = applicationId + ".CAMERA2";
    public static final String EXTRA_VIDEO_WIDTH = applicationId + ".VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = applicationId + ".VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = applicationId + ".VIDEO_FPS";
    public static final String EXTRA_VIDEO_BITRATE = applicationId + ".VIDEO_BITRATE";
    public static final String EXTRA_VIDEOCODEC = applicationId + ".VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = applicationId + ".HWCODEC";
    public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = applicationId + ".CAPTURETOTEXTURE";
    public static final String EXTRA_FLEXFEC_ENABLED = applicationId + ".FLEXFEC";
    public static final String EXTRA_AUDIO_BITRATE = applicationId + ".AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = applicationId + ".AUDIOCODEC";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED =
            applicationId + ".NOAUDIOPROCESSING";
    public static final String EXTRA_AECDUMP_ENABLED = applicationId + ".AECDUMP";
    public static final String EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED =
            applicationId + ".SAVE_INPUT_AUDIO_TO_FILE";
    public static final String EXTRA_OPENSLES_ENABLED = applicationId + ".OPENSLES";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = applicationId + ".DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = applicationId + ".DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = applicationId + ".DISABLE_BUILT_IN_NS";
    public static final String EXTRA_DISABLE_WEBRTC_AGC_AND_HPF =
            applicationId + ".DISABLE_WEBRTC_GAIN_CONTROL";
    public static final String EXTRA_TRACING = applicationId + ".TRACING";

    public static final String EXTRA_VIDEO_FILE_AS_CAMERA = applicationId + ".VIDEO_FILE_AS_CAMERA";
    public static final String EXTRA_DATA_CHANNEL_ENABLED = applicationId + ".DATA_CHANNEL_ENABLED";
    public static final String EXTRA_ORDERED = applicationId + ".ORDERED";
    public static final String EXTRA_MAX_RETRANSMITS_MS = applicationId + ".MAX_RETRANSMITS_MS";
    public static final String EXTRA_MAX_RETRANSMITS = applicationId + ".MAX_RETRANSMITS";
    public static final String EXTRA_PROTOCOL = applicationId + ".PROTOCOL";
    public static final String EXTRA_NEGOTIATED = applicationId + ".NEGOTIATED";
    public static final String EXTRA_ID = applicationId + ".ID";
    public static final String EXTRA_ENABLE_RTCEVENTLOG = applicationId + ".ENABLE_RTCEVENTLOG";


}
