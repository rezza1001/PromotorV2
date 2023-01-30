package com.wadaro.promotor.ui.order;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.module.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SalesOrderDetail extends MyActivity {

    private final ArrayList<Bundle> mOrders = new ArrayList<>();
    private OrderAdapter mOrderAdapter;
    private LinearLayout lnly_data_00;

    @Override
    protected int setLayout() {
        return R.layout.order_activity_salesorder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Sales Order");

        lnly_data_00 = findViewById(R.id.lnly_data_00);

        RecyclerView rcvw_product_00 = findViewById(R.id.rcvw_product_00);
        rcvw_product_00.setLayoutManager(new LinearLayoutManager(mActivity));
        mOrderAdapter = new OrderAdapter(mActivity, mOrders);
        rcvw_product_00.setAdapter(mOrderAdapter);

    }

    @Override
    protected void initData() {
        try {
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id"));

            JSONObject data = new JSONObject(getIntent().getStringExtra("data"));
            Date demoDate = oldFormat.parse(data.getString("booking_date"));
            Date deliveryDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(data.getString("delivery_date"));

            buildInfo("Nama DB / KACAB", data.getString("booker_name"));
            buildInfo("Nama KACAB", data.getString("branch_manager_name"));
            buildInfo("Nama KAWIL", data.getString("regional_manager_name"));
            buildInfo("Cabang", data.getString("branch_name"));
            buildInfo("PT", data.getString("company_name"));
            addLine();
            if (demoDate != null){
                buildInfo("Tanggal Demo", dateFormat.format(demoDate));
            }
            buildInfo("Koordinator", data.getString("coordinator_name"));
            buildInfo("Alamat", data.getString("coordinator_address"));
            buildInfo("No. KTP", data.getString("coordinator_ktp"));
            buildInfo("No. HP", data.getString("coordinator_phone"));
            addLine();
            buildInfo("No. Sales Order", data.getString("sales_id"));
            if (deliveryDate != null){
                buildInfo("Tanggal Kirim", dateFormat.format(deliveryDate));
            }
            buildInfo("Nama Sales Promotor", data.getString("promotor_name"));
            buildInfo("Keterangan", data.getString("sales_note"));

            JSONArray details = data.getJSONArray("details");
            for (int i=0; i<details.length(); i++){
                JSONObject detail = details.getJSONObject(i);
                Bundle bundle = new Bundle();
                bundle.putString("consumen_name", detail.getString("consumen_name"));
                bundle.putString("product_name", detail.getString("product_name"));
                bundle.putString("qty", detail.getString("qty"));
                bundle.putString("subtotal", detail.getString("subtotal"));
                bundle.putString("selling_price", detail.getString("selling_price"));
                mOrders.add(bundle);
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        mOrderAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }

    private void buildInfo(String key, String value){
        InfoView infoView = new InfoView(mActivity, null);
        if (value.equals("null")){
            infoView.create(key, "-");
        }
        else {
            infoView.create(key, value.trim());
        }
        lnly_data_00.addView(infoView);
    }
    private void addLine(){
        View view = new View(mActivity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utility.dpToPx(mActivity, 1));
        lp.topMargin = Utility.dpToPx(mActivity, 5);
        lp.bottomMargin = Utility.dpToPx(mActivity, 5);
        view.setBackgroundColor(Color.LTGRAY);
        lnly_data_00.addView(view,lp);
    }
}
