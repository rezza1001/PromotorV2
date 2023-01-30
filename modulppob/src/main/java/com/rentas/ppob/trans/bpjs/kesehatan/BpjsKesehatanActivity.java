package com.rentas.ppob.trans.bpjs.kesehatan;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.database.table.ProductDB;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;
import com.rentas.ppob.trans.bpjs.NominalAdapter;
import com.rentas.ppob.trans.post.PaymentPostActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BpjsKesehatanActivity extends MyActivity {

    private HeaderView header_view;
    private ImageView imvw_clean;
    private EditText edtx_phone;
    private LinearLayout lnly_error;
    private TextView txvw_error;

    int productId = 0;
    int productCogId = 0;
    long admin = 0;

    private NominalAdapter adapter;
    ArrayList<Bundle> nominal = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.bpjs_activity_main;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("Bpjs Kesehatan");

        imvw_clean = findViewById(R.id.imvw_clean);
        imvw_clean.setColorFilter(Color.parseColor("#9B9B9B"));

        edtx_phone = findViewById(R.id.edtx_phone);
        lnly_error = findViewById(R.id.lnly_error);
        txvw_error = findViewById(R.id.txvw_error);
        RelativeLayout lnly_operator = findViewById(R.id.lnly_operator);

        edtx_phone.setTag("false");
        edtx_phone.setHint("Masukan ID Bpjs");
        lnly_operator.setVisibility(View.INVISIBLE);

        RecyclerView rcvw_nominal = findViewById(R.id.rcvw_nominal);
        rcvw_nominal.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new NominalAdapter(mActivity, nominal);
        rcvw_nominal.setAdapter(adapter);

        registerReceiver(receiver, new IntentFilter("FINISH"));
    }

    @Override
    protected void initData() {
        getProduct();
    }

    @Override
    protected void initListener() {
        header_view.setOnBackListener(this::onBackPressed);
        edtx_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                showNominal();
                if (!s.toString().isEmpty()){
                    imvw_clean.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                }
                else {
                    imvw_clean.setColorFilter(Color.parseColor("#9B9B9B"));
                }
            }
        });

        imvw_clean.setOnClickListener(v -> edtx_phone.setText(""));
        adapter.setOnSelectedListener((data, position) -> checkPrice(data.getInt("qty")));
    }


    private void getProduct(){
        PostManager post = new PostManager(mActivity, ConfigAPI.GET_PRODUCTS);
        post.addParamHeader("category_id", getIntent().getIntExtra("id",1));
        post.executeGET();
        post.setOnReceiveListener((obj, code, message) -> {

            ProductDB productDB = new ProductDB();
            productDB.deleteByCategory(mActivity,getIntent().getStringExtra("code"));

            if (code == ErrorCode.OK){
                try {
                    JSONArray ja = obj.getJSONArray("data");
                    for (int i=0; i<ja.length(); i++){
                        JSONObject jo = ja.getJSONObject(i);
                        productId = jo.getInt("id");
                        admin = jo.getLong("admin");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showError("Gagal","Mohon maaf saat ini produk tidak tersedia");
                dialog.setOnCloseLister(action -> mActivity.finish());
            }

            if (getIntent().getStringExtra("customer_id") != null){
                edtx_phone.setText(getIntent().getStringExtra("customer_id"));
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showNominal(){
        nominal.clear();
        if (edtx_phone.getText().toString().length() < 5){
            adapter.notifyDataSetChanged();
            return;
        }
        for (int i=1; i <= 12; i++){
            Bundle bundle = new Bundle();
            bundle.putInt("qty",i);
            String sName = getMonth(i);
            bundle.putString("name", sName.split("-")[0]);
            bundle.putString("year", sName.split("-")[1]);
            nominal.add(bundle);
        }
        adapter.notifyDataSetChanged();
    }

    private String getMonth(int nominal){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,(nominal-1) );
        DateFormat format = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    private void checkPrice(int qty){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_CHECK_PRICE_POS);
        post.addParam("product_id",productId);
        post.addParam("agent_id",mAgentId);
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    obj = obj.getJSONObject("data");
                    if (obj.isNull("product_cogs_id")){
                        Utility.showFailedDialog(mActivity,"Gagal menampilkan harga. Silahkan hubungi admin");
                        return;
                    }
                    productCogId =  obj.getInt("product_cogs_id");
                    inquiry(qty);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Utility.showFailedDialog(mActivity,"Gagal menampilkan harga\n"+message);
            }
        });
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void inquiry(int qty){
        String number = edtx_phone.getText().toString().trim();
//        number =  String.format("%016d", Long.parseLong(number));
        lnly_error.setVisibility(View.INVISIBLE);
        PostManager post = new PostManager(mActivity, ConfigAPI.INQUIRY_POSTPAID);
        post.addParam("product_cogs_id", productCogId);
        post.addParam("product_id", productId);
        post.addParam("agent_id", MainData.getAgentID(mActivity));
        post.addParam("customer_id", number);
        post.addParam("cut_balance", 0);
        post.addParam("customer_bill_amount", 0);
        post.addParam("qty", qty);
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj;
                    if (obj.has("data")){
                        data = obj.getJSONObject("data");
                    }
                    Intent intent = getIntent();
                    intent.setClass(mActivity, PaymentPostActivity.class);
                    intent.putExtra("category_name","PLN Pascabayar");
                    intent.putExtra("product_id",productId);
                    intent.putExtra("qty",qty);
                    intent.putExtra("inquiry_id",data.getString("inquiry_id"));
                    intent.putExtra("customer_bill_amount",data.getLong("customer_bill_amount"));
                    intent.putExtra("price",data.getLong("cut_balance"));
                    intent.putExtra("admin",data.getLong("fee_admin"));
                    intent.putExtra("data", data.getString("screen"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                lnly_error.setVisibility(View.VISIBLE);
                txvw_error.setText("Terjadi Kesalahan, Pastikan ID pelanggan sudah benar");
            }
        });
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

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("FINISH")){
                mActivity.finish();
            }
        }
    };
}
