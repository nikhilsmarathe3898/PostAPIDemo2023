package com.localgenie.utilities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.localgenie.Dagger2.AppComponent;
import com.localgenie.Dagger2.DaggerAppComponent;
import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;
import com.pojo.LanguagesList;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import io.reactivex.disposables.CompositeDisposable;

/**
 * <h1>IsForeground</h1>
 * @author ${3Embed}
 * @since on 4/11/2017.
 *
 * Usage:
 *
 * 1. Get the Foreground Singleton, passing a Context or Application object unless you
 * are sure that the Singleton has definitely already been initialised elsewhere.
 *
 * 2.a) Perform a direct, synchronous check: Foreground.isForeground() / .isBackground()
 *
 * or
 *
 * 2.b) Register to be notified (useful in Service or other non-UI components):
 *
 *   Foreground.Listener myListener = new Foreground.Listener(){
 *       public void onBecameForeground(){
 *           // ... whatever you want to do
 *       }
 *       public void onBecameBackground(){
 *           // ... whatever you want to do
 *       }
 *   }
 *
 *   public void on_Create(){
 *      super.on_Create();
 *      Foreground.get(this).addListener(listener);
 *   }
 *
 *   public void onDestroy(){
 *      super.on_Create();
 *      Foreground.get(this).removeListener(listener);
 *   }
 */

public class LSPApplication extends DaggerApplication implements Application.ActivityLifecycleCallbacks {
    public static final String TAG = LSPApplication.class.getName();
    private static final long CHECK_DELAY = 500;
    private static LSPApplication instance;
    private boolean foreground = false, paused = true;
    private List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
    private Runnable check;
    private Handler handler = new Handler();
    private MqttAndroidClient mqttAndroidClient;
    @Inject Gson gson;
    @Inject MQTTManager mqttManager;
    @Inject SessionManagerImpl manager;
    private static Context mContext;
    @Inject CompositeDisposable compositeDisposable;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        Fabric.with(this, new Crashlytics());
        mContext = this;
        instance = this;

        LocaleUtil.changeAppLanguage(mContext);
        ListenListener();
        //setLanguageValueTosession();

    }

    public static Context getContext() {
        return mContext;
    }
    /**
     * Its not strictly necessary to use this method - _usually_ invoking
     * get with a Context gives us a path to retrieve the Application and
     * initialise, but sometimes (e.g. in test harness) the ApplicationContext
     * is != the Application, and the docs make no guarantees.
     *
     * @param application Application
     * @return an initialised Foreground instance
     */

    public static LSPApplication init(Application application)
    {
        if (instance == null)
        {
            instance = new LSPApplication();
            application.registerActivityLifecycleCallbacks(instance);   //Registering life cycle for application class.
        }
        return instance;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged");
        LocaleUtil.setLanguage(mContext, newConfig);
    }

    /**
     * <h2>get</h2>
     * <p>
     *     to create the single instance of this class
     * </p>
     * @param application: application reference
     * @return : return the singleton instance of this class
     */
    public static LSPApplication get(Application application)
    {
        if (instance == null)
        {
            init(application);
        }
        return instance;
    }

    /**
     * <h2>get</h2>
     * <p>
     *     to create the single instance of this class
     * </p>
     * @param context: calling activity reference
     * @return : return the singleton instance of this class
     */
    public static LSPApplication get(Context context)
    {
        if (instance == null)
        {
            Context appContext = context.getApplicationContext();
            if (appContext instanceof Application)
            {
                init((Application) appContext);
            }
            throw new IllegalStateException(
                    "Foreground is not initialised and " +
                            "cannot obtain the Application object");
        }
        return instance;
    }

    /**
     * <h2>get</h2>
     * <p>
     *     to create the single instance of this class
     * </p>
     * @return: return the singleton instance of this class
     */
    public static LSPApplication get()
    {
        if (instance == null)
        {
            throw new IllegalStateException(
                    "Foreground is not initialised - invoke " +
                            "at least once with parameterised init/get");
        }
        return instance;
    }


    public static synchronized LSPApplication getInstance() {
        return instance;
    }


    /**
     * <h2>LSPApplication</h2>
     * <p>
     *     this method will return the state of the app
     *     whether the app is in  foreground or not
     * </p>
     * @return boolean:  whether the app is in  foreground or not
     */
    public boolean LSPApplication()
    {
        return foreground;
    }

    /**
     * <h2>isBackground</h2>
     * <p>
     *     this method will return the state of the app
     *     whether the app is in background or not
     * </p>
     * @return boolean:  whether the app is in  background or not
     */
    public boolean isBackground()
    {
        return !foreground;
    }

    /**
     * <h2>addListener</h2>
     * <p>
     *     method to add listener to get callback
     * </p>
     */
    public void addListener(Listener listener)
    {
        listeners.add(listener);
    }

    /**
     * <h2>removeListener</h2>
     * <p>
     *     method to remove listener
     * </p>
     */
    public void removeListener(Listener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (check != null)
        {
            handler.removeCallbacks(check);
        }

        if (wasBackground)
        {
            Log.i(TAG, "went foreground");
            for (Listener l : listeners)
            {
                try
                {
                    l.onBecameForeground();
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Listener threw exception!", e);
                }
            }
        }
        else
        {
            Log.i(TAG, "still foreground");
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        paused = true;

        if (check != null)
        {
            handler.removeCallbacks(check);
        }

        handler.postDelayed(check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused)
                {
                    foreground = false;
                    Log.i(TAG, "went background");
                    for (Listener l : listeners)
                    {
                        try {
                            l.onBecameBackground();
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, "Listener threw exception!", e);
                        }
                    }
                }
                else {
                    Log.i(TAG, "still foreground");
                }
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);
        return appComponent;
    }



    private void setLanguageValueTosession() {
        if (manager.getLanguageSettings() == null)
            manager.setLanguageSettings(new LanguagesList("en","English", 0));
        else
            Utility.changeLanguageConfig(manager.getLanguageSettings().getCode(),this);
    }

    public void ListenListener() {
        addListener(new Listener() {
            @Override
            public void onBecameForeground() {

                if(!mqttManager.isMQTTConnected())
                {
                    if(!manager.getAUTH().equals("") || manager.getAUTH()!=null
                            && !manager.getSID().equals("") || manager.getSID()!=null)
                    {
                        mqttManager.createMQttConnection(manager.getSID(),false);
                    }
                }
            }

            @Override
            public void onBecameBackground()
            {
                compositeDisposable.clear();
            }
        });
    }

    public interface Listener
    {
        void onBecameForeground();
        void onBecameBackground();
    }


    /*private void checkActiveCalls(){



        String callId = getCallId();
        String activeCallerId = getCallerId();


        if (callId != null && activeCallerId!=null ) {


            JSONObject obj = new JSONObject();
            try {
                obj.put("callId", callId);
                obj.put("userId", manager.getSID());
                obj.put("type", 2);


                publish(MqttEvents.Calls.value + "/" + activeCallerId, obj, 0, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        removeCurrentCallDetails();





    }


    @SuppressWarnings("TryWithIdenticalCatches")
    public void publish(String topicName, JSONObject obj, int qos, boolean retained) {


        try {

            if (mqttAndroidClient != null) {

                mqttAndroidClient.publish(topicName, obj.toString().getBytes(), qos, retained);
            }


        } catch (MqttException e) {


        } catch (NullPointerException e) {

        }


    }
    public void saveCurrentCallDetails(String callId,String callerId){

        sharedPref.edit().putString("callId",callId).apply();
        sharedPref.edit().putString("callerId",callerId).apply();


    }


    public void removeCurrentCallDetails(){

        sharedPref.edit().putString("callId",null).apply();
        sharedPref.edit().putString("callerId",null).apply();


    }

    public String getCallId() {
        return  sharedPref.getString("callId",null);
    }

    public String getCallerId() {
        return  sharedPref.getString("callerId",null);
    }
*/
}
