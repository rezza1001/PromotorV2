package com.rentas.ppob.trans.pln.token;

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
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;
import com.rentas.ppob.trans.post.PaymentPostActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainTokenActivity extends MyActivity {

    private HeaderView header_view;
    private ImageView imvw_clean;
    private EditText edtx_phone;
    private LinearLayout lnly_error;
    private RelativeLayout lnly_operator;
    private TextView txvw_error;

    private int productId = 0;
    private int productCogId = 0;
    private long admin =0;

    private NominalAdapter adapter;
    ArrayList<Bundle> nominal = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.pulsa_activity_main;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("PLN Token");

        imvw_clean = findViewById(R.id.imvw_clean);
        imvw_clean.setColorFilter(Color.parseColor("#9B9B9B"));

        edtx_phone = findViewById(R.id.edtx_phone);
        lnly_error = findViewById(R.id.lnly_error);
        txvw_error = findViewById(R.id.txvw_error);
        lnly_operator = findViewById(R.id.lnly_operator);
        ImageView imvw_operator = findViewById(R.id.imvw_operator);
        imvw_operator.setVisibility(View.GONE);

        edtx_phone.setTag("false");
        edtx_phone.setHint("Nomor PLN");
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
                validateNumber(s.toString());

                if (!s.toString().isEmpty()){
                    imvw_clean.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                }
                else {
                    imvw_clean.setColorFilter(Color.parseColor("#9B9B9B"));
                }
            }
        });

        imvw_clean.setOnClickListener(v -> edtx_phone.setText(""));
        adapter.setOnSelectedListener((data, position) -> checkPrice(data.getLong("nominal")));
    }

    @SuppressLint("SetTextI18n")
    private void validateNumber(String phone){
        edtx_phone.setTag("false");
        lnly_error.setVisibility(View.INVISIBLE);
        lnly_operator.setVisibility(View.INVISIBLE);
        if  (phone.length() < 5){
            initNominal("");
            return;
        }

        edtx_phone.setTag("true");
        lnly_operator.setVisibility(View.VISIBLE);
        initNominal(edtx_phone.getText().toString());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initNominal(String input){
        nominal.clear();
        if (input.isEmpty()){
            adapter.notifyDataSetChanged();
            return;
        }
        try {
            JSONArray ja = new JSONArray(Utility.getJsonFromAssets(mActivity, "nominaltoken.json"));
            ArrayList<ProductDB> allProd = new ArrayList<>();
            for (int i=0; i<ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);

                Bundle bundle = new Bundle();
                bundle.putLong("nominal", Long.parseLong(jo.getString("code")));
                bundle.putString("name",  jo.getString("name"));
                nominal.add(bundle);
            }

            ProductDB productDB = new ProductDB();
            productDB.deleteByCategory(mActivity,"pulsa");
            productDB.insertBulk(mActivity, allProd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
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

    private void checkPrice(long bill){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_CHECK_PRICE_POS);
        post.addParam("product_id",productId);
        post.addParam("agent_id",mAgentId);
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    obj = obj.getJSONObject("data");
                    productCogId =  obj.getInt("product_cogs_id");
                    inquiry(bill);
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
    private void inquiry(long bill){
        lnly_error.setVisibility(View.INVISIBLE);
        PostManager post = new PostManager(mActivity, ConfigAPI.INQUIRY_POSTPAID);
        post.addParam("product_cogs_id", productCogId);
        post.addParam("product_id", productId);
        post.addParam("agent_id", MainData.getAgentID(mActivity));
        post.addParam("customer_id", edtx_phone.getText().toString().trim());
        post.addParam("cut_balance", 0);
        post.addParam("customer_bill_amount", bill);
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
                    intent.putExtra("customer_bill_amount",bill);
                    intent.putExtra("price",(data.getLong("cut_balance")));
                    intent.putExtra("admin",data.getLong("fee_admin"));
                    JSONArray screen = data.getJSONArray("screen");
                    JSONObject joBill = new JSONObject();
                    joBill.put("label","Nominal");
                    joBill.put("value",MyCurrency.toCurrnecy(bill));
                    screen.put(joBill);
                    intent.putExtra("data", screen.toString());
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
