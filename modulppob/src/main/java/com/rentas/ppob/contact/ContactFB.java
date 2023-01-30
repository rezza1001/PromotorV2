package com.rentas.ppob.contact;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.libs.Utility;

import java.util.ArrayList;
import java.util.Objects;

public class ContactFB {

    private static final String FB_NAME = "CONTACTS";

    private FirebaseFirestore database;
    private Context context;

    public String member_id = "";
    public String name = "";
    public String customer_id = "";
    public String type = "";

    public ContactFB(Context context){
        this.context = context;
        database = FirebaseFirestore.getInstance();
    }


    public void insert(){
        delete();
        setDeleteListener(status -> database.collection(FB_NAME).add(this)
                .addOnSuccessListener(documentReference ->{
                    if (mOnSaveListener != null){
                        mOnSaveListener.onSave(ErrorCode.OK,"Success");
                    }
                })
                .addOnFailureListener(e -> {
                    if (mOnSaveListener != null){
                        mOnSaveListener.onSave(ErrorCode.FAILED,"Failed");
                    }
                    Toast.makeText(context,"Failed save to firebase database", Toast.LENGTH_LONG).show();
                    Log.e("FireDatabase", Objects.requireNonNull(e.getMessage()));
                }));
    }

    public void delete(){
        database.collection(FB_NAME)
                .whereEqualTo("customer_id", customer_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int x = Objects.requireNonNull(task.getResult()).size();
                        if (x == 0){
                            if (pDeleteListener != null){
                                pDeleteListener.onDelete(ErrorCode.OK);
                            }
                            return;
                        }
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            document.getReference().delete().addOnSuccessListener(aVoid -> {
                                if (pDeleteListener != null){
                                    pDeleteListener.onDelete(ErrorCode.OK);
                                }
                            });
                        }
                    }
                    else {
                        if (pDeleteListener != null){
                            pDeleteListener.onDelete(ErrorCode.OK);
                        }
                    }
                });
    }

    public void get(String memberid){
        ArrayList<ContactFB>  customerFBS = new ArrayList<>();
        database.collection(FB_NAME)
                .whereEqualTo("member_id", memberid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ContactFB db = new ContactFB(context);
                                db.customer_id =  (String) document.getData().get("customer_id");
                                db.name =  (String) document.getData().get("name");
                                db.type =  (String) document.getData().get("type");
                                db.member_id =  (String) document.getData().get("member_id");
                                customerFBS.add(db);
                            }
                        }
                        else {
                            Utility.showToastError(context,"Failed load contact");
                        }

                    }
                    if (mReadListener != null){
                        mReadListener.onRead(customerFBS);
                    }
                });
    }


    private InsertListener mOnSaveListener;
    public void setOnInsertListener(InsertListener pOnSaveListener){
        mOnSaveListener = pOnSaveListener;
    }
    public interface InsertListener{
        void onSave(int status, String message);
    }


    private DeleteListener pDeleteListener;
    public void setDeleteListener(DeleteListener onDelteListener){
        pDeleteListener = onDelteListener;
    }
    public interface DeleteListener{
        void onDelete(int status);
    }

    private OnReadListener mReadListener;
    public void setOnReadListener(OnReadListener pReadListener){
        mReadListener = pReadListener;
    }
    public interface OnReadListener{
        void onRead(ArrayList<ContactFB> data);
    }
}
