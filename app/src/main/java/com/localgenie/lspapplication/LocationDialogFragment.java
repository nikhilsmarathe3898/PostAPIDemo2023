package com.localgenie.lspapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.localgenie.R;
import com.localgenie.home.ServiceFragContract;
import com.localgenie.utilities.Constants;
import com.mqtt.MQTTManager;

import io.reactivex.disposables.CompositeDisposable;

public class LocationDialogFragment extends DialogFragment {

    String auth;
    private MQTTManager mqttManager;
    private Activity activity;
    private String sid;
    private int loc_interval;
    private String date;
    private CompositeDisposable compositeDisposable;
    private View dialog;
    private ServiceFragContract.ServiceView serviceView;

    public LocationDialogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        compositeDisposable = new CompositeDisposable();
        // Inflate the layout for this fragment
        dialog = inflater.inflate(R.layout.enable_location_dialog, container, false);
        // Enable Location button
        dialog.findViewById(R.id.enableLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Connect to MQTT and get providers list

               /* SharedPreferences sharedPreferences = activity.getSharedPreferences("local_db", Context.MODE_PRIVATE);

                sid = sharedPreferences.getString("sid",null);

                loc_interval = sharedPreferences.getInt("loc_interval",0);

                Log.e("MQTT","LOC_INTERVAL :: "+loc_interval);

                auth = sharedPreferences.getString("auth",null);
*/
               boolean isAllowed = true;
                serviceView.onLocationListener(isAllowed);
                Log.e("MQTT", "isLocationClicked  "+isAllowed);

                dismiss();
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }
    public void initializePresenter(ServiceFragContract.ServiceView serviceView)
    {
        this.serviceView =  serviceView;

    }

    @Override
    public void onResume() {
        Log.e("MQTT","Start onResume");
        if (mqttManager!=null) {
            if (mqttManager.isMQTTConnected()) {
                Log.e("MQTT", "onResume says hi to MQTT");
            }
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mqttManager!=null) {
            if (mqttManager.isMQTTConnected()) {
                mqttManager.disconnect(Constants.MQTT_TOPIC + sid);
            }
        }
        super.onDestroy();
    }
}
