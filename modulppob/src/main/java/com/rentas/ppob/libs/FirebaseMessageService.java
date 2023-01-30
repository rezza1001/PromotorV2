package com.rentas.ppob.libs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by rezza on 29/01/18.
 */

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class FirebaseMessageService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    public final static String MY_ACTION = "com.multiaccess.chipsakti.MESSAGE_NOTIF";
    public final static String MESSAGE_COMPLAINT = "MESSAGE_COMPLAINT";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
         Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
         Log.d(TAG, "Notification Message Data: " + remoteMessage.getData().get("data"));
         Log.d(TAG, "Notification Message Title: " + remoteMessage.getNotification().getTitle());
        if (remoteMessage.getNotification().getImageUrl() != null){
            String imageUrl = remoteMessage.getNotification().getImageUrl()+"";
             Log.d(TAG,"Image "+imageUrl);

            broadcastData(new JSONObject(remoteMessage.getData()),
                    remoteMessage.getNotification().getBody(),
                    Objects.requireNonNull(remoteMessage.getNotification().getTitle()), imageUrl);
        }
        else {
            broadcastData(new JSONObject(remoteMessage.getData()),remoteMessage.getNotification().getBody(), Objects.requireNonNull(remoteMessage.getNotification().getTitle()), null);
        }

    }

    private void broadcastData(JSONObject data, String body, String title, String image){
        Intent intent = new Intent();
        intent.setAction(MY_ACTION);
        intent.putExtra("Message", body);
        intent.putExtra("Data", data.toString());
        intent.putExtra("Title", title);
        intent.putExtra("Image", image);
        sendBroadcast(intent);

        if (title.toUpperCase().startsWith("PESAN")){
            intent = new Intent();
            intent.setAction(MESSAGE_COMPLAINT);
            intent.putExtra("Message", body);
            intent.putExtra("Data", data.toString());
            intent.putExtra("Title", title);
            sendBroadcast(intent);

        }
    }
}
