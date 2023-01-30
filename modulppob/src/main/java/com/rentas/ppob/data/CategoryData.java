package com.rentas.ppob.data;

import android.app.Activity;
import android.content.Intent;

import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.home.MenuHolder;
import com.rentas.ppob.trans.bpjs.kesehatan.BpjsKesehatanActivity;
import com.rentas.ppob.trans.bpjs.ketenagakerjaan.BpjsKetenagakerjaanActivity;
import com.rentas.ppob.trans.multifinance.ProductActivity;
import com.rentas.ppob.trans.paketdata.PaketDataActivity;
import com.rentas.ppob.trans.pln.post.MainPlnPosActivity;
import com.rentas.ppob.trans.pln.token.MainTokenActivity;
import com.rentas.ppob.trans.pulsa.PulsaActivity;
import com.rentas.ppob.trans.tlppostpaid.MainTlpPostActivity;

public class CategoryData {

    public static String PULSA = "CT001";
    public static String PAKET_DATA = "CT002";
    public static String PLN = "CT003";
    public static String PLN_TOKEN = "CT004";
    public static String TLP_POSTPAID = "CT005";
    public static String BPJS_KESEHATAN = "CT007";
    public static String BPJS_KETENAGAKERJAAN = "CT008";
    public static String MULTIFINANCE = "CT006";
    public static String SMS_TELP = "CT010";
    public static String TELKOM = "CT011";
    public static String PDAM = "CT012";

    public static void toTransaction(Activity activity, MenuHolder data){
        Intent intent = new Intent();
        intent.putExtra("id", data.id);
        intent.putExtra("name", data.name);
        intent.putExtra("code", data.code);
        if (data.code.equals(PULSA)){
            intent.setClass(activity, PulsaActivity.class);
            activity.startActivity(intent);
        }
        else if (data.code.equals(PAKET_DATA)){
            intent.setClass(activity, PaketDataActivity.class);
            activity.startActivity(intent);
        }
        else if (data.code.equals(PLN)){
            intent.setClass(activity, com.rentas.ppob.trans.pln.post.ProductActivity.class);
            activity.startActivity(intent);
        }
        else if (data.code.equals(PLN_TOKEN)){
            intent.setClass(activity, MainTokenActivity.class);
            activity.startActivity(intent);
        }
        else if (data.code.equals(TLP_POSTPAID)){
            intent.setClass(activity, MainTlpPostActivity.class);
            activity.startActivity(intent);
        }
        else if (data.code.equals(BPJS_KESEHATAN)){
            intent.setClass(activity, BpjsKesehatanActivity.class);
            activity.startActivity(intent);
        }
        else if (data.code.equals(BPJS_KETENAGAKERJAAN)){
            intent.setClass(activity, BpjsKetenagakerjaanActivity.class);
            activity.startActivity(intent);
        }
//        else if (data.code.equals(MULTIFINANCE)){
//            intent.setClass(activity, ProductActivity.class);
//            activity.startActivity(intent);
//        }
        else if (data.code.equals(TELKOM)){
            intent.setClass(activity, com.rentas.ppob.trans.telkom.ProductActivity.class);
            activity.startActivity(intent);
        }
        else if (data.code.equals(PDAM)){
            intent.setClass(activity, com.rentas.ppob.trans.pdam.ProductActivity.class);
            activity.startActivity(intent);
        }
//        else if (data.code.equals(SMS_TELP)){
//            intent.setClass(activity, com.rentas.ppob.trans.smstlp.ProductActivity.class);
//            activity.startActivity(intent);
//        }
        else {
            DynamicDialog dialog = new DynamicDialog(activity);
            dialog.showInfo("Akan Segera Hadir","Fitur ini untuk sementara belum bisa digunakan");
        }

    }
}
