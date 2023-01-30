package com.wadaro.promotor.ui.draft;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.Global;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.BookingDB;
import com.wadaro.promotor.database.table.JpDB;
import com.wadaro.promotor.database.table.SalesOrder2DB;
import com.wadaro.promotor.database.table.SalesOrderDB;
import com.wadaro.promotor.module.FailedWindow;
import com.wadaro.promotor.module.FileProcessing;
import com.wadaro.promotor.module.SuccessWindow;
import com.wadaro.promotor.ui.demo.process.SalesOrder2Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DraftSurveyActivity extends MyActivity {

    private DraftAdapter mAdapter;
    private ArrayList<Bundle> allData = new ArrayList<>();
    private ArrayList<SalesOrderDB> salesOrderDBS = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.draft_activity_activitydraft;
    }

    @Override
    protected void initLayout() {

        RecyclerView rcvw_data_00 = findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));

        mAdapter = new DraftAdapter(mActivity, allData);
        rcvw_data_00.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        allData.clear();
        SalesOrderDB db = new SalesOrderDB();
        for (SalesOrderDB mDb : db.getData(mActivity)) {
            salesOrderDBS.add(mDb);
            try {
                JSONObject data = new JSONObject(mDb.data);
                Bundle bundle = new Bundle();
                bundle.putString("order", mDb.bookingId);
                bundle.putString("so", mDb.id);
                bundle.putString("data", mDb.data);
                bundle.putString("note", data.getString("sales_note"));
                bundle.putString("delivery_date", data.getString("delivery_date"));
                allData.add(bundle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initListener() {
        mAdapter.setOnSelectedListener(data -> {
            String deliveryDate = "";
            String salesNote = "";
            try {
                JSONObject soObj = new JSONObject(data.getString("data"));
                deliveryDate = soObj.getString("delivery_date");
                salesNote = soObj.getString("sales_note");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(mActivity, SalesOrder2Activity.class);
            intent.putExtra("booking_id", data.getString("order"));
            intent.putExtra("sales_order", data.getString("so"));
            intent.putExtra("delivery_date", deliveryDate);
            intent.putExtra("note", salesNote);
            intent.putExtra("offline", true);
            intent.putExtra("offline_so", data.getString("so"));
            startActivity(intent);
        });

        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.mrly_action_00).setOnClickListener(v -> {
            Log.d(TAG, "allData " + allData.size());
//            if (allData.size() == 0){
//                Utility.showToastError(mActivity,"Data tidak ditemukan");
//                return;
//            }
//            loadingWindow = new LoadingWindow(mActivity);
//            loadingWindow.show();

            uploadData();

        });
    }

    private void uploadData() {
        if (allData.size() > 0) {
            Bundle soBundle = allData.get(0);

            String bookingId = soBundle.getString("order");
            JSONObject booking = getBooking(soBundle.getString("order"));
            if (!booking.isNull("booking_demo")) {
                bookingId = "";
            }

            JSONArray customers = getCustomerLead(soBundle.getString("order"));
            JSONObject salesOrder = getSalesOrder(soBundle.getString("data"));

            JSONObject dataSend = new JSONObject();
            try {
                dataSend.put("booking_id", bookingId);
                dataSend.put("booking", booking);
                dataSend.put("customer_lead", customers);
                dataSend.put("sales_order", salesOrder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            upload(dataSend, soBundle.getString("order"));
        } else {
            SuccessWindow successWindow = new SuccessWindow(mActivity);
            successWindow.setDescription("Data berhasil disimpan");
            successWindow.show();
            successWindow.setOnFinishListener(() -> {
                SalesOrderDB salesOrderDB = new SalesOrderDB();
                salesOrderDB.clearData(mActivity);

                SalesOrder2DB salesOrder2DB = new SalesOrder2DB();
                salesOrder2DB.clearData(mActivity);

                JpDB jpDB = new JpDB();
                jpDB.clearData(mActivity);

                MyPreference.delete(mActivity, Global.PREF_DATA_PROCESS_DEMO);
                mActivity.finish();

                FileProcessing.createFolder(mActivity, "/Wadaro/promotor/");
                FileProcessing.clearImage(mActivity, "/Wadaro/promotor/draft/");

                BookingDB bookingDB = new BookingDB();
                bookingDB.clearData(mActivity);
            });
        }
    }

    private void upload(JSONObject dataSend, String bookingID) {

        PostManager post = new PostManager(mActivity, Config.UPLOAD_OFFLINE);
        post.setData(dataSend);
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK) {
                SalesOrderDB salesOrderDB = new SalesOrderDB();
                salesOrderDB.delete(mActivity, bookingID);

                SalesOrder2DB salesOrder2DB = new SalesOrder2DB();
                salesOrder2DB.delete(mActivity, bookingID);

                JpDB jpDB = new JpDB();
                jpDB.deleteByBooking(mActivity, bookingID);

                BookingDB bookingDB = new BookingDB();
                bookingDB.delete(mActivity, bookingID);

                allData.remove(0);
                mAdapter.notifyDataSetChanged();
                uploadData();
            } else {
                FailedWindow failedWindow = new FailedWindow(mActivity);
                failedWindow.show();
                failedWindow.setDescription("Gagal mengupload data " + bookingID + ". Silahkan coba lagi!");
                failedWindow.setOnFinishListener(() -> mActivity.finish());
            }
        });

    }

    private JSONObject getBooking(String bookingID) {
        JSONObject jo = new JSONObject();
        BookingDB bookingDB = new BookingDB();
        bookingDB.getData(mActivity, bookingID);
        if (!bookingDB.data.isEmpty()) {
            try {
                jo = new JSONObject(bookingDB.data);
                jo.remove("booking_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jo;
    }

    private JSONArray getCustomerLead(String bookingId) {
        JSONArray ja = new JSONArray();
        JpDB db = new JpDB();

        ArrayList<JpDB> jpDBS = db.getData(mActivity, bookingId);
        for (JpDB jp : jpDBS) {
            try {
                JSONObject jo = new JSONObject(jp.data);
                jo.put("products", getProduct(jp.id + ""));
                if (jp.id > 1000) {
                    jo.put("consumen_id", jp.id);
                } else {
                    jo.put("consumen_id", "");
                }
                ja.put(jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        createRealCustomerLead(bookingId, ja);
        return ja;
    }

    private JSONArray getProduct(String customerID) {
        JSONArray ja = new JSONArray();
        SalesOrder2DB db = new SalesOrder2DB();
        ArrayList<SalesOrder2DB> data = db.getDataByCustomer(mActivity, customerID);
        for (SalesOrder2DB dataDB : data) {
            try {
                JSONObject jo = new JSONObject(dataDB.data);
                JSONObject jo1 = new JSONObject();
                jo1.put("product_id", jo.getString("product_id"));
                jo1.put("qty", jo.getInt("qty"));
                ja.put(jo1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ja;
    }


    private void createRealCustomerLead(String bookingId, JSONArray ja) {
        SalesOrder2DB db = new SalesOrder2DB();
        ArrayList<SalesOrder2DB> data = db.getRealCustomer(mActivity, bookingId);

        for (SalesOrder2DB jp : data) {
            try {
                JSONObject jo = new JSONObject(jp.data);
                JSONObject joNew = new JSONObject();
                joNew.put("name", jo.getString("consumen_name"));
                joNew.put("ktp", jo.getString("consumen_ktp"));
                joNew.put("consumen_id", jp.customerId);
                joNew.put("address", "");
                joNew.put("products", getProduct(jp.customerId + ""));
                ja.put(joNew);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private JSONObject getSalesOrder(String data) {
        JSONObject salesOrder = new JSONObject();
        try {
            salesOrder = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return salesOrder;
    }
}
