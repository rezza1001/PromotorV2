package com.rentas.ppob.trans.pln.post;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.rentas.ppob.trans.post.PaymentPostActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainPlnPosActivity extends MyActivity {

    private HeaderView header_view;
    private ImageView imvw_clean;
    private EditText edtx_phone;
    private LinearLayout lnly_error;
    private TextView txvw_error;


    private int productId = 0;
    private int productCogId = 0;

    @Override
    protected int setLayout() {
        return R.layout.trans_plnpos_activity_main;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("PLN Pascabayar");

        imvw_clean = findViewById(R.id.imvw_clean);
        imvw_clean.setColorFilter(Color.parseColor("#9B9B9B"));

        edtx_phone = findViewById(R.id.edtx_phone);
        lnly_error = findViewById(R.id.lnly_error);
        txvw_error = findViewById(R.id.txvw_error);

        edtx_phone.setTag("false");
        edtx_phone.setHint("Nomor PLN");


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
                if (!s.toString().isEmpty()){
                    imvw_clean.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                }
                else {
                    imvw_clean.setColorFilter(Color.parseColor("#9B9B9B"));
                }
            }
        });
        imvw_clean.setOnClickListener(v -> edtx_phone.setText(""));

        findViewById(R.id.lnly_action).setOnClickListener(v -> checkPrice());
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

    private void checkPrice(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_CHECK_PRICE_POS);
        post.addParam("product_id",productId);
        post.addParam("agent_id",mAgentId);
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    obj = obj.getJSONObject("data");
                    productCogId =  obj.getInt("product_cogs_id");
                    inquiry();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Utility.showFailedDialog(mActivity,"Gagal menampilkan harga\n"+message);
            }
        });
    }



    @SuppressLint("SetTextI18n")
    private void inquiry(){
        lnly_error.setVisibility(View.INVISIBLE);
        PostManager post = new PostManager(mActivity, ConfigAPI.INQUIRY_POSTPAID);
        post.addParam("product_cogs_id", productCogId);
        post.addParam("product_id", productId);
        post.addParam("agent_id", MainData.getAgentID(mActivity));
        post.addParam("customer_id", edtx_phone.getText().toString().trim());
        post.addParam("cut_balance", 0);
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
                    intent.putExtra("inquiry_id",data.getString("inquiry_id"));
                    intent.putExtra("customer_bill_amount",data.getLong("customer_bill_amount"));
                    intent.putExtra("price",data.getLong("cut_balance"));
                    intent.putExtra("admin",data.getLong("fee_admin"));
                    intent.putExtra("data", data.getString("screen"));
                    Log.d("TAGRZ","ADMIN "+data.getLong("fee_admin"));
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                lnly_error.setVisibility(View.VISIBLE);
                txvw_error.setText("Terjadi Kesalahan, Pastikan ID pelanggan sudah benar ("+message+")");
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
