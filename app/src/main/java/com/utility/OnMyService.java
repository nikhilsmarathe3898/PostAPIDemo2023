package com.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.videocalling.UtilityVideoCall;
import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import dagger.android.DaggerService;

/**
 * Created by Ali on 1/20/2018.
 */

public class OnMyService extends DaggerService
{
    //  SessionManager sharedPrefs;

    @Inject
    SessionManagerImpl sharedPrefs;
    @Inject
    MQTTManager manager;
    @Inject
    public OnMyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //sharedPrefs = SessionManager.getInstance(this);
        Log.e("ClearFromRecentService", "Service Started");
        // intent is null only when the Service crashed previously

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        try {

            /*  To make myself available for receiving the new call
             */
            JSONObject obj = new JSONObject();
            obj.put("status", 1);

            manager.publish(MqttEvents.CallsAvailability.value + "/" + sharedPrefs.getSID(), obj, 0, true);
            UtilityVideoCall.getInstance().setActiveOnACall(false, false);
        }catch (Exception ex)
        {
            ex.printStackTrace();

        }
            super.onDestroy();
        Log.e("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        String cid = sharedPrefs.getSID();
        Log.e("ClearFromRecentService", "END "+cid+" MQTTRESPO "+manager.isMQTTConnected());
        //Code here
        //addAutoStartup();
        if(manager.isMQTTConnected())
        {
            if(!cid.equals(""))
            {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("status", 1);
                    manager.publish(MqttEvents.CallsAvailability.value + "/" + sharedPrefs.getSID(), obj, 0, true);//UserId
                    UtilityVideoCall.getInstance().setActiveOnACall(false, false);
                //    manager.subscribeToTopic(MqttEvents.Calls.value+"/"+sharedPrefs.getSID(),1);
                    //  mqttManager.subscribeToTopic(MqttEvents.Calls.value+"/5c177bf2f56745d4b143e1a6",1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                manager.unSubscribeToTopic(MqttEvents.JobStatus.value + "/" +cid);
                manager.unSubscribeToTopic(MqttEvents.Provider.value + "/" +cid);
                manager.unSubscribeToTopic(MqttEvents.Message.value + "/" + cid);
              //  manager.publish(MqttEvents.CallsAvailability.value+"/"+cid);
                if(!Constants.LiveTrackBookingPid.equals(""))
                    manager.unSubscribeToTopic(MqttEvents.LiveTrack.value + "/" + Constants.LiveTrackBookingPid);
                Constants.LiveTrackBookingPid = "";
                if (manager!=null) {
                    if (manager.isMQTTConnected()) {
                        manager.disconnect(Constants.MQTT_TOPIC + cid);

                    }
                }
            }
        }else{
            if(!cid.equals(""))
            {

                if (!manager.isMQTTConnected())
                    manager.createMQttConnection(cid,true);

                JSONObject obj = new JSONObject();
                try {
                    obj.put("status", 1);
                    manager.publish(MqttEvents.CallsAvailability.value + "/" + sharedPrefs.getSID(), obj, 0, true);//UserId
                    UtilityVideoCall.getInstance().setActiveOnACall(false, false);
                    //    manager.subscribeToTopic(MqttEvents.Calls.value+"/"+sharedPrefs.getSID(),1);
                    //  mqttManager.subscribeToTopic(MqttEvents.Calls.value+"/5c177bf2f56745d4b143e1a6",1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                manager.unSubscribeToTopic(MqttEvents.JobStatus.value + "/" +cid);
                manager.unSubscribeToTopic(MqttEvents.Provider.value + "/" +cid);
                manager.unSubscribeToTopic(MqttEvents.Message.value + "/" + cid);
                //  manager.publish(MqttEvents.CallsAvailability.value+"/"+cid);
                if(!Constants.LiveTrackBookingPid.equals(""))
                    manager.unSubscribeToTopic(MqttEvents.LiveTrack.value + "/" + Constants.LiveTrackBookingPid);
                Constants.LiveTrackBookingPid = "";
                if (manager!=null) {
                    if (manager.isMQTTConnected()) {
                        manager.disconnect(Constants.MQTT_TOPIC + cid);

                    }
                }            }

        }

        if(!sharedPrefs.getGuestLogin())
        {
            sharedPrefs.setAppOpen(false);
        }

        stopSelf();
    }

    private void addAutoStartup() {

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("ClearFromRecentService", "Service Low Memory");

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("ClearFromRecentService", "Service onConfigurationChanged");

    }
}
