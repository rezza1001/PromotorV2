package com.wadaro.promotor.ui.account;

import android.content.Context;

import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.LoadingWindow;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.Global;
import com.wadaro.promotor.component.WarningWindow;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.BookingDB;
import com.wadaro.promotor.database.table.JpDB;
import com.wadaro.promotor.database.table.ProductDB;
import com.wadaro.promotor.database.table.SalesOrder2DB;
import com.wadaro.promotor.database.table.SalesOrderDB;
import com.wadaro.promotor.database.table.TempDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SynchData {

    private Context mActivity;
    String today;
    private DownloadDialog downloadStatus;

    public SynchData(Context context){
        this.mActivity = context;

        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));
        today = format2.format(new Date());
//        today = "2020-06-30";

        WarningWindow warningWindow = new WarningWindow(context);
        warningWindow.show("Perhatian","Pastikan anda sudah mengupload data sebelumnya, karena data draft akan di hapus dan pastikan anda memiliki jaringan yang bagus untuk download data");
        warningWindow.setOnSelectedListener(status -> {
            if (status == 2){
                downloadStatus = new DownloadDialog(context);

                SalesOrderDB salesOrderDB = new SalesOrderDB();
                salesOrderDB.clearData(mActivity);

                SalesOrder2DB salesOrder2DB = new SalesOrder2DB();
                salesOrder2DB.clearData(mActivity);

                MyPreference.delete(mActivity, Global.PREF_DATA_PROCESS_DEMO);

                JpDB jpDB = new JpDB();
                jpDB.clearData(mActivity);

                getProduct();

                BookingDB bookingDB = new BookingDB();
                bookingDB.clearData(mActivity);
            }
        });
    }


    private void getProduct(){
        downloadStatus.show();
        PostManager post = new PostManager(mActivity, Config.GET_INFO_PRODUCT);
        post.showloading(false);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONArray ja = obj.getJSONArray("data");
                    ArrayList<ProductDB> dbs = new ArrayList<>();
                    for (int i=0; i<ja.length(); i++){
                        JSONObject jo = ja.getJSONObject(i);
                        ProductDB db = new ProductDB();
                        db.id = jo.getString("product_id");
                        db.productName = jo.getString("product_name");
                        db.company = jo.getString("company_id");
                        db.photo = jo.getString("product_photo");
                        db.price = jo.getLong("price");
                        db.installment = jo.getLong("installment");
                        dbs.add(db);
                    }
                    ProductDB db = new ProductDB();
                    db.clearData(mActivity);
                    db.insertBulk(mActivity, dbs);
                } catch (JSONException e) {
                    e.printStackTrace();
                    downloadStatus.setErrorStep(0);
                }
                downloadStatus.setCompleteStep(0);
                getDemo();
            }
            else {
                downloadStatus.dismiss();
            }
        });
    }

    private void getDemo(){
        PostManager post = new PostManager(mActivity, Config.GET_DEMO_DATA);
        post.showloading(false);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                MyPreference.save(mActivity, Global.PREF_DATA_PROCESS_DEMO, obj.toString());
                downloadStatus.setCompleteStep(1);
            }
            getDataBooking();
        });
    }

    private void getDataBooking(){
        PostManager post = new PostManager(mActivity, Config.GET_SUDDEN_DEMO);
        post.showloading(false);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                MyPreference.save(mActivity, Global.PREF_DATA_BOOKER, obj.toString());
                downloadStatus.setCompleteStep(2);
            }
            buildBookingIDs();
        });
    }

    private ArrayList<String> bookIds = new ArrayList<>();
    private void buildBookingIDs(){
        bookIds.clear();
        String dataAssignment = MyPreference.getString(mActivity,Global.PREF_DATA_PROCESS_DEMO);
        if (dataAssignment.isEmpty()){
            downloadStatus.dismiss();
            return;
        }
        try {
            JSONObject obj = new JSONObject(dataAssignment);
            JSONArray booking = obj.getJSONObject("data").getJSONArray("booking");
            for (int i=0; i<booking.length(); i++){
                bookIds.add(booking.getJSONObject(i).getString("booking_id"));
            }
            getDtlBooking();
        } catch (JSONException e) {
            e.printStackTrace();
            downloadStatus.setErrorStep(2);
        }
    }

    private void getDtlBooking(){
        if (bookIds.size() == 0){
            buildProcessDemo();
            return;
        }
        String param = "?booking_id="+bookIds.get(0);
        PostManager post = new PostManager(mActivity, Config.GET_DEMO_DATA_DETAIL+param);
        post.showloading(false);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                if (bookIds.size() > 0){
                    TempDB tempDB = new TempDB();
                    tempDB.id       = bookIds.get(0);
                    tempDB.data     = obj.toString();
                    tempDB.keydata  = TempDB.DETAIL_BOOKING;
                    tempDB.insert(mActivity);
                    bookIds.remove(0);
                    getDtlBooking();
                }
                else {
                    buildProcessDemo();
                }
            }
            else {
                downloadStatus.dismiss();
            }
        });
    }

    private void buildProcessDemo(){
        bookIds.clear();
        String dataAssignment = MyPreference.getString(mActivity,Global.PREF_DATA_PROCESS_DEMO);
        if (dataAssignment.isEmpty()){
            downloadStatus.dismiss();
            return;
        }
        try {
            JSONObject obj = new JSONObject(dataAssignment);
            JSONArray booking = obj.getJSONObject("data").getJSONArray("booking");
            for (int i=0; i<booking.length(); i++){
                bookIds.add(booking.getJSONObject(i).getString("booking_id"));
            }
            getProcessDemo();
        } catch (JSONException e) {
            e.printStackTrace();
            downloadStatus.dismiss();
        }
    }

    private void getProcessDemo(){
        if (bookIds.size() == 0){
            buildSalesOrder();
            return;
        }
        String param = "?booking_id="+bookIds.get(0);
        PostManager post = new PostManager(mActivity, Config.DEMO_PROCESS+param);
        post.showloading(false);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                if (bookIds.size() > 0){
                    TempDB tempDB = new TempDB();
                    tempDB.id       = bookIds.get(0);
                    tempDB.data     = obj.toString();
                    tempDB.keydata  = TempDB.PROCESS_DEMO;
                    tempDB.insert(mActivity);
                    bookIds.remove(0);
                    getProcessDemo();
                }
                else {
                    buildSalesOrder();
                }
            }
            else {
                downloadStatus.dismiss();
            }
        });
    }

    private void buildSalesOrder(){
        bookIds.clear();
        String dataAssignment = MyPreference.getString(mActivity,Global.PREF_DATA_PROCESS_DEMO);
        if (dataAssignment.isEmpty()){
            downloadStatus.dismiss();
            downloadStatus.setCompleteStep(2);
            return;
        }
        try {
            JSONObject obj = new JSONObject(dataAssignment);
            JSONArray booking = obj.getJSONObject("data").getJSONArray("booking");
            for (int i=0; i<booking.length(); i++){
                bookIds.add(booking.getJSONObject(i).getString("booking_id"));
            }
            getSalesOrder();
        } catch (JSONException e) {
            e.printStackTrace();
            downloadStatus.dismiss();
        }
    }

    private void getSalesOrder(){
        if (bookIds.size() == 0){
            downloadStatus.dismiss();
            downloadStatus.setCompleteStep(2);
            return;
        }
        String param = "?booking_id="+bookIds.get(0);
        PostManager post = new PostManager(mActivity, Config.GET_DATA_FOR_CREATE_SALES_ORDER+param);
        post.showloading(false);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                if (bookIds.size() > 0){
                    TempDB tempDB = new TempDB();
                    tempDB.id       = bookIds.get(0);
                    tempDB.data     = obj.toString();
                    tempDB.keydata  = TempDB.SALES_ORDER;
                    tempDB.insert(mActivity);
                    bookIds.remove(0);
                    getSalesOrder();
                }
                else {
                    downloadStatus.dismiss();
                }
            }
            else {
                downloadStatus.dismiss();
            }
        });
    }

}
