package com.wadaro.promotor.ui.demo.process;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.database.table.UserDB;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.ui.order.InfoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SummaryOrderActivity extends MyActivity {

    private LinearLayout lnly_info_00;

    private ArrayList<Bundle> mOrderData = new ArrayList<>();
    private OrderAdapter  mOrderAdapter;

    private String mBookingID = "";

    @Override
    protected int setLayout() {
        return R.layout.demo_process_activity_sumary;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Sales Order");

        lnly_info_00 = findViewById(R.id.lnly_info_00);

        RecyclerView rcvw_product_00 = findViewById(R.id.rcvw_product_00);
        rcvw_product_00.setLayoutManager(new LinearLayoutManager(mActivity));
        mOrderAdapter = new OrderAdapter(mActivity, mOrderData);
        mOrderAdapter.hideAction();
        rcvw_product_00.setAdapter(mOrderAdapter);
    }

    @Override
    protected void initData() {
        mBookingID = getIntent().getStringExtra("booking_id");
        request();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_edit_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_finsih_00).setOnClickListener(v -> {
            sendBroadcast(new Intent("FINISH"));
            sendBroadcast(new Intent("REFRESH"));
            Utility.showToastSuccess(mActivity,"Order Selesai");
            new Handler().postDelayed(() -> mActivity.finish(),200);
        });
    }

    private void request(){
        mOrderData.clear();
        lnly_info_00.removeAllViews();
        mBookingID = getIntent().getStringExtra("booking_id");
        String salesOrder = getIntent().getStringExtra("sales_order");
        mUser = new UserDB();
        mUser.getData(mActivity);

        String param = "?booking_id="+mBookingID+"&sales_id="+ salesOrder;
        PostManager post = new PostManager(mActivity, Config.SALES_ORDER_DETAIL+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data     = obj.getJSONObject("data");
                    JSONObject sales    = data.getJSONObject("sales");

                    SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
                    Date delivery = oldFormat.parse(sales.getString("delivery_date"));

                    buildInfo("No. Sales Order", sales.getString("sales_id"));
                    assert delivery != null;
                    buildInfo("Tanggal Kirim", dateFormat.format(delivery));
                    buildInfo("Sales Promotor", mUser.name);
                    buildInfo("Keterangan", sales.getString("sales_note"));

                    JSONArray orders = data.getJSONArray("order_details");
                    for (int i=0; i<orders.length(); i++){
                        JSONObject jo = orders.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("id",jo.getString("id"));
                        bundle.putString("consumen_id",jo.getString("consumen_id"));
                        bundle.putString("consumen_name",jo.getString("consumen_name"));
                        bundle.putString("product_name",jo.getString("product_name"));
                        bundle.putString("product_id",jo.getString("product_id"));
                        bundle.putString("qty",jo.getString("qty"));
                        bundle.putString("price",jo.getString("selling_price"));
                        bundle.putString("subtotal",jo.getString("subtotal"));
                        mOrderData.add(bundle);
                    }

                    request2();
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
            mOrderAdapter.notifyDataSetChanged();
        });
    }

    private void request2(){
        String param = "?booking_id="+mBookingID;
        PostManager post = new PostManager(mActivity, Config.GET_DEMO_DATA_DETAIL+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data     = obj.getJSONObject("data");
                    JSONObject booking  = data.getJSONObject("booking");
                    JSONObject coordinator = booking.getJSONObject("coordinator");
                    addLine();
                    buildInfo("Koordinator", coordinator.getString("name"));
                    buildInfo("Alamat", coordinator.getString("address"));
                    buildInfo("No. KTP", coordinator.getString("ktp"));
                    buildInfo("No. HP", coordinator.getString("phone"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void buildInfo(String key, String value){
        InfoView infoView = new InfoView(mActivity, null);
        if (value.equals("null")){
            infoView.create(key, "-");
        }
        else {
            infoView.create(key, value.trim());
        }
        lnly_info_00.addView(infoView);
    }
    private void addLine(){
        View view = new View(mActivity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utility.dpToPx(mActivity, 1));
        lp.topMargin = Utility.dpToPx(mActivity, 5);
        lp.bottomMargin = Utility.dpToPx(mActivity, 5);
        view.setBackgroundColor(Color.LTGRAY);
        lnly_info_00.addView(view,lp);
    }
}
