package com.rentas.ppob.libs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Objects;

public class NetworkUtil {


    public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context == null){
            return true;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void internetSpeed(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                assert cm != null;
                nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                assert nc != null;
                int downSpeed = nc.getLinkDownstreamBandwidthKbps();
                int upSpeed = nc.getLinkUpstreamBandwidthKbps();
                int signal = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    signal = nc.getSignalStrength();
                }

                Log.d("NetworkUtil", "Down :"+ downSpeed+" | UP :"+ upSpeed+" | Signal : "+ signal);
            }
        }

    }

    public static ConnectionHolder getConnection(Context context){
        ConnectionHolder conn = new ConnectionHolder();
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null){
            return conn;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null){
            conn.name = "Network not detected";
            conn.strange = "Undefined";
        }
        else {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                conn.name = "WIFI";
                conn.strange = "Undefined";
                conn.type = 12;
            }
            else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                conn.type = activeNetwork.getSubtype();
                conn.name = "PAKET DATA";
                conn.strange = "0000";
                if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS){
                    conn.strange = "GPRS Under 100 kbps";
                }
                else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA){
                    conn.strange = "CDMA 14 - 64 kbps";
                }
                else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE){
                    conn.strange = "EDGE 50 - 100 kbps";
                } else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0){
                    conn.strange = "EVDO_0 400 - 1000 kbps";
                } else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A){
                    conn.strange = "EVDO_A 600 - 1400 kbps";
                } else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_UMTS){
                    conn.strange = "UMTS 400 - 7000 kbps";
                } else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_HSUPA){
                    conn.strange = "HSUPA 2 - 23 Mbps";
                }
                else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA){
                    conn.strange = "HSDPA 2 - 14 Mbps";
                }
                else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE){
                    conn.strange = "LTE 10 Mbps";
                }
                else {
                    conn.strange = activeNetwork.getSubtype()+"";
                }
            }

        }
        return conn;
    }

    public static class ConnectionHolder {
        public String name = "LOST CONNECTION";
        public String strange = "0";
        public  int type = 0;

        public String toStringData(){
            return "Type "+ name+", Strange Level : "+strange+" = "+ type;
        }
    }
}
