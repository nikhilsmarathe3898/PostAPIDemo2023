package com.localgenie.networking;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.localgenie.RxObservers.RxNetworkObserver;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
/**
 * <h2>NetworkCheckerService</h2>
 * <P>
 *  Checking for the Internet is there or not.
 *  After each five seconds.
 * </P>
 * @author 3Embed.
 * @version 1.0.
 * */
public class NetworkCheckerService extends Service
{
    //@Inject
    NetworkStateHolder holder;
    //@Inject
    RxNetworkObserver rxNetworkObserver;
    private Timer mTimer = null;
    private ConnectivityManager cm;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e("NET","Checker service started");
         cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        holder = new NetworkStateHolder();
        rxNetworkObserver = new RxNetworkObserver();
        //((LSPApplication)getApplication()).getAppComponent().inject(this);
        int interval = 5;
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CheckForConnection(),0,interval * 1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy()
    {
        if(mTimer!=null)
        {
            mTimer.cancel();
        }
        super.onDestroy();
    }

    /*
     *Checking the internet connection is there or not. */
    private void checkInternetConnection()
    {
        try {
            URL url=new URL("http://www.google.com");
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            urlConnection.setConnectTimeout(2000);
            int status=urlConnection.getResponseCode();
            if(status== HttpURLConnection.HTTP_OK)
            {
                holder.setConnected(true);
                getConnectionType();

            }else
            {
                holder.setConnected(false);
                getConnectionType();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            holder.setConnected(false);
            getConnectionType();
        }
        /*
         *providing the network status. */
        rxNetworkObserver.publishData(holder);
    }

    /*
     *Getting the connection type. */
    private void getConnectionType()
    {
        if(cm!=null)
        {
            NetworkInfo networkInfo= cm.getActiveNetworkInfo();
            if(networkInfo!=null&&networkInfo.isConnectedOrConnecting())
            {
                holder.setConnectionType(isConnectionType(networkInfo.getType()));
                holder.setMessage("Connected");
            }else
            {
                holder.setConnectionType(ConnectionType.NOT_CONNECTED);
                holder.setMessage("Not Connected");
            }
        }else
        {
            holder.setConnectionType(ConnectionType.NOT_CONNECTED);
            holder.setMessage("Not Connected");
        }
    }

    /*
     * Getting the connection type in android.*/
    private ConnectionType isConnectionType(int type)
    {
        if(type== ConnectivityManager.TYPE_WIFI)
        {
            return ConnectionType.WIFI;
        }else
        {
            return ConnectionType.MOBILE;
        }
    }

    /*
     * Get the connection is good or not.*/
    private boolean isConnectionGood(int type,int subType)
    {
        if(type== ConnectivityManager.TYPE_WIFI)
        {
            return true;
        }else if(type== ConnectivityManager.TYPE_MOBILE)
        {
            switch(subType)
            {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        }else{
            return false;
        }
    }

    class CheckForConnection extends TimerTask
    {
        @Override
        public void run()
        {
            checkInternetConnection();
        }
    }
}

