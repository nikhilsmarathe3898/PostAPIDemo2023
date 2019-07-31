package com.webRtc.utils;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.localgenie.R;

/**
 * Settings fragment for AppRTC.
 */
public class VideoCallSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.videocall_preferences);
    }
}
