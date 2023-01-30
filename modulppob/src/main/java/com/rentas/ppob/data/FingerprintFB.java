package com.rentas.ppob.data;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.contact.ContactFB;
import com.rentas.ppob.libs.MyDevice;
import com.rentas.ppob.libs.Utility;

import java.util.Objects;

public class FingerprintFB {

    private static final String FB_NAME = "FINGER_PRINT";

    private final FirebaseFirestore database;
    private final Context context;
    private final MyDevice device;

    public String agent_code = "";
    public String device_id = "";
    public boolean isActive = false;

    public FingerprintFB(Context context){
        this.context = context;
        device = new MyDevice(context);
        device_id = device.getDeviceID();
        database = FirebaseFirestore.getInstance();
    }


    public void insert(){
        database.collection(FB_NAME).document(agent_code+"_"+device_id).set(this);
    }

    public void delete(){
        Log.d(FB_NAME,"delete "+agent_code+"_"+device_id);
        database.collection(FB_NAME).document(agent_code+"_"+device_id).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (pDeleteListener != null){
                            pDeleteListener.onDelete(ErrorCode.OK);
                        }

                    }
                });
    }


    public void get(String agent_code){
        Log.d(FB_NAME,"get "+agent_code+"_"+device_id);
        database.collection(FB_NAME).document(agent_code+"_"+device_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null){
                            DocumentSnapshot document = task.getResult();

                            this.agent_code =  (String) document.get("agent_code");
                            this.isActive   = Boolean.TRUE.equals(document.getBoolean("isActive"));
                        }
                        else {
                            Utility.showToastError(context,"Failed load");
                        }

                    }
                    if (mReadListener != null){
                        mReadListener.onRead(this);
                    }
                });
    }



    private OnReadListener mReadListener;
    public void setOnReadListener(OnReadListener pReadListener){
        mReadListener = pReadListener;
    }
    public interface OnReadListener{
        void onRead(FingerprintFB data);
    }

    private DeleteListener pDeleteListener;
    public void setDeleteListener(DeleteListener onDelteListener){
        pDeleteListener = onDelteListener;
    }
    public interface DeleteListener{
        void onDelete(int status);
    }
}
