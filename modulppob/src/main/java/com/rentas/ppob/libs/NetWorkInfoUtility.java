package com.rentas.ppob.libs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.rentas.ppob.api.ConfigAPI;

import java.io.IOException;
import java.util.Calendar;

public class NetWorkInfoUtility {
    String TAG = "NetWorkInfoUtility";

    private int type = 0;
    private boolean status = false;

    public boolean isWifiEnable() {
        return isWifiEnable;
    }

    public void setIsWifiEnable(boolean isWifiEnable) {
        this.isWifiEnable = isWifiEnable;
    }

    public boolean isMobileNetworkAvailable() {
        return isMobileNetworkAvailable;
    }

    public void setIsMobileNetworkAvailable(boolean isMobileNetworkAvailable) {
        this.isMobileNetworkAvailable = isMobileNetworkAvailable;
    }

    public int getType(){
        return type;
    }

    public boolean getStatus(){
        return status;
    }

    private boolean isWifiEnable = false;
    private boolean isMobileNetworkAvailable = false;

    public boolean isNetWorkAvailableNow(Context context) {
        boolean isNetworkAvailable = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        setIsWifiEnable(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
        setIsMobileNetworkAvailable(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        status = activeNetworkInfo != null;
        if (status){
            type = connectivityManager.getActiveNetworkInfo().getType();
        }

        if (isWifiEnable() || isMobileNetworkAvailable()) {
            /*Sometime wifi is connected but service provider never connected to internet
            so cross check one more time*/
            if (isOnline())
                isNetworkAvailable = true;
        }

        return isNetworkAvailable;
    }

    public boolean isOnline() {
        /*Just to check Time delay*/
        long t = Calendar.getInstance().getTimeInMillis();

        Runtime runtime = Runtime.getRuntime();
        try {
            /*Pinging to Google server*/
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 "+ ConfigAPI.MAIN);
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            long t2 = Calendar.getInstance().getTimeInMillis();
            Log.i(TAG,"Ping Time : "+ (t2 - t) + "");
        }
        return false;
    }
}
