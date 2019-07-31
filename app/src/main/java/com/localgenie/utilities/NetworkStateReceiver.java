package com.localgenie.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.localgenie.networking.ConnectionType;
import com.localgenie.networking.NetworkStateHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * <h2> Network State Changes Broadcast receiver.</h2>
 * @author Pramod
 * @since 16/01/2018.
 */

public class NetworkStateReceiver extends BroadcastReceiver {
    protected Set<NetworkStateReceiverListener> listeners;
    protected Boolean connected;
    private NetworkStateHolder holder;

    public NetworkStateReceiver() {
        listeners = new HashSet<>();
        connected = null;
        holder = new NetworkStateHolder();
    }


    public void onReceive(Context context, Intent intent) {
        if(intent == null || intent.getExtras() == null)
            return;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null) {
            NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
                holder.setConnected(true);

                holder.setConnectionType(isConnectionType(ni.getType()));
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                connected = false;
                holder.setConnected(false);
                holder.setConnectionType(ConnectionType.NOT_CONNECTED);
            }

            notifyStateToAll();
        } else
            Log.e("NetworkState","ConnectivityManager null!");
    }

    /*
    * Getting the connection type.*/
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
     * Get the connection strength - good or not.*/
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


    private void notifyStateToAll() {
        for(NetworkStateReceiverListener listener : listeners)
            notifyState(listener);
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if(connected == null || listener == null)
            return;

        if(connected)
            listener.networkAvailable();
        else
            listener.networkUnavailable();
    }

    public void addListener(NetworkStateReceiverListener l) {
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(NetworkStateReceiverListener l) {
        listeners.remove(l);
    }

    public interface NetworkStateReceiverListener {
        void networkAvailable();
        void networkUnavailable();
    }

}