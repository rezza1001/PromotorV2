package com.rentas.ppob;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.rentas.ppob.libs.NetWorkInfoUtility;

public class ApplicationPpob extends Application {

    private static final String TAG = "ApplicationPpob";
    public static final String CONNECTION_CHANGE = "CONNECTION_CHANGE";

    public static boolean fingerPrintActive = false;
    public static int FailedPin = 0;
    public static long LastFailedPin = 0;


    public ApplicationPpob() {
        super();
    }

    public static volatile NetWorkInfoUtility netWorkInfoUtility;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"ApplicationPpob onCreated");
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        requestConnection();
    }

    public void requestConnection(){
        netWorkInfoUtility = new NetWorkInfoUtility();
        netWorkInfoUtility.isNetWorkAvailableNow(getApplicationContext());

        sendStatus(netWorkInfoUtility.getStatus());
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            netWorkInfoUtility.isNetWorkAvailableNow(getApplicationContext());

            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                sendStatus(true);
                Log.d(TAG,"Connection change "+ activeNetworkInfo.getTypeName()+" : "+ activeNetworkInfo.isConnected());
            }
            else {
                Log.e(TAG,"Connection change disconnect ");
                sendStatus(false);
            }
        }
    };

    private void sendStatus(boolean statusNetwork){
        Intent broadCastIntent = new Intent(CONNECTION_CHANGE);
        broadCastIntent.putExtra("TYPE",netWorkInfoUtility.getType());
        broadCastIntent.putExtra("INET_STATUS",statusNetwork);
        broadCastIntent.putExtra("IP_STATUS",netWorkInfoUtility.isOnline());
        sendBroadcast(broadCastIntent);
    }

}
