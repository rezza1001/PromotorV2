package com.wadaro.promotor.ui.report;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.ui.demo.process.OrderAdapter;
import com.wadaro.promotor.ui.demo.process.SalesOrder2Activity;
import com.wadaro.promotor.ui.order.InfoView;
import com.wadaro.promotor.util.MyCurrency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DetailActivity extends MyActivity {

    private static final int CHANGE_COORDINATOR = 11;

    private LinearLayout lnly_info_00;
    private Button bbtn_edit_00;
    private HorizontalScrollView hz_view_00;
    private TextView txvw_qty_00,txvw_price_00,txvw_total_00;

    private ArrayList<Bundle> mOrderData = new ArrayList<>();
    private OrderAdapter mOrderAdapter;

    private String mBookingID = "";
    private String salesOrder = "";
    private String salesNote = "";
    private String deliveryDate = "";
    @Override
    protected int setLayout() {
        return R.layout.report_activity_detail;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Penugasan Demo");

        lnly_info_00 = findViewById(R.id.lnly_info_00);
        bbtn_edit_00 = findViewById(R.id.bbtn_edit_00);
        hz_view_00 = findViewById(R.id.hz_view_00);
        txvw_qty_00 = findViewById(R.id.txvw_qty_00);
        txvw_price_00 = findViewById(R.id.txvw_price_00);
        txvw_total_00 = findViewById(R.id.txvw_total_00);

        RecyclerView rcvw_prduct_00 = findViewById(R.id.rcvw_product_00);
        rcvw_prduct_00.setLayoutManager(new LinearLayoutManager(mActivity));
        mOrderAdapter = new OrderAdapter(mActivity, mOrderData);
        mOrderAdapter.hideAction();
        rcvw_prduct_00.setAdapter(mOrderAdapter);
    }

    @Override
    protected void initData() {
        mBookingID = getIntent().getStringExtra("booking_id");
        salesOrder = getIntent().getStringExtra("sales_id");
        if (salesOrder == null){
            bbtn_edit_00.setVisibility(View.GONE);
            hz_view_00.setVisibility(View.GONE);
        }
        else if (salesOrder.isEmpty()){
            bbtn_edit_00.setVisibility(View.GONE);
            hz_view_00.setVisibility(View.GONE);
        }
        request();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        bbtn_edit_00.setOnClickListener(v -> {
            Intent intent = getIntent();
            intent.setClass(mActivity, SalesOrder2Activity.class);
            intent.putExtra("sales_order", salesOrder);
            intent.putExtra("delivery_date", deliveryDate);
            intent.putExtra("note", salesNote);
            startActivity(intent);
        });
    }

    private void request(){
        lnly_info_00.removeAllViews();
        mOrderData.clear();

        String param = "?booking_id="+mBookingID;
        PostManager post = new PostManager(mActivity, Config.GET_DEMO_DATA_DETAIL+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data     = obj.getJSONObject("data");
                    JSONObject booking  = data.getJSONObject("booking");
                    JSONObject booker  = booking.getJSONObject("booker");
                    JSONObject branch   = booking.getJSONObject("branch");
                    JSONObject coordinator = booking.getJSONObject("coordinator");

                    SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id"));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id"));
                    Date demoDate = oldFormat.parse(booking.getString("date"));

                    buildInfo("Nama DB / KACAB", booker.getString("name"));
                    buildInfo("Nama KACAB", branch.getString("manager_name"));
                    buildInfo("Nama KAWIL", branch.getString("regional_manager_name"));
                    buildInfo("Cabang", branch.getString("name"));
                    buildInfo("PT", branch.getString("company"));
                    addLine();
                    if (demoDate != null){
                        buildInfo("Tanggal Demo", dateFormat.format(demoDate));
                    }
                    buildInfo("Koordinator", coordinator.getString("name"));
                    buildInfo("Alamat", coordinator.getString("address"));
                    buildInfo("No. KTP", coordinator.getString("ktp"));
                    buildInfo("No. HP", coordinator.getString("phone"));

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                requestOrder();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void requestOrder(){
        if (salesOrder.isEmpty()){
            return;
        }
        String param = "?booking_id="+mBookingID+"&sales_id="+salesOrder;
        PostManager post = new PostManager(mActivity, Config.SALES_ORDER_DETAIL+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                addLine();
                try {
                    JSONObject  data = obj.getJSONObject("data");
                    JSONArray orders = data.getJSONArray("order_details");
                    JSONObject sales = data.getJSONObject("sales");

                    buildInfo("No. Sales Order", sales.getString("sales_id"));
                    buildInfo("Tanggal Kirim", sales.getString("delivery_date"));
                    buildInfo("Keterangan", sales.getString("sales_note"));
                    salesNote = sales.getString("sales_note");
                    deliveryDate = sales.getString("delivery_date");

                    int qty = 0;
                    long price = 0;
                    long total = 0;

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
                        qty     = qty + jo.getInt("qty");
                        price   = price + jo.getLong("selling_price");
                        total   = total + jo.getLong("subtotal");
                    }
                    txvw_qty_00.setText(""+qty);
                    txvw_price_00.setText(MyCurrency.toCurrnecy(price));
                    txvw_total_00.setText(MyCurrency.toCurrnecy(total));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mOrderAdapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_COORDINATOR && resultCode == RESULT_OK){
            request();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("FINISH"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "FINISH")){
                mActivity.finish();
            }
        }
    };
}
