package com.rentas.ppob.trans.pulsa;

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
import com.rentas.ppob.database.table.ProductDB;
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.libs.OperatorPrefix;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;
import com.rentas.ppob.trans.prepaid.PaymentPrepaidActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PulsaActivity extends MyActivity {

    private HeaderView header_view;
    private ImageView imvw_clean,imvw_operator;
    private EditText edtx_phone;
    private LinearLayout lnly_error;
    private RelativeLayout lnly_operator;
    private TextView txvw_error;

    private NominalAdapter adapter;
    ArrayList<Bundle> nominal = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.pulsa_activity_main;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("Transaksi Pulsa");

        imvw_clean = findViewById(R.id.imvw_clean);
        imvw_clean.setColorFilter(Color.parseColor("#9B9B9B"));

        edtx_phone = findViewById(R.id.edtx_phone);
        lnly_error = findViewById(R.id.lnly_error);
        txvw_error = findViewById(R.id.txvw_error);
        lnly_operator = findViewById(R.id.lnly_operator);
        imvw_operator = findViewById(R.id.imvw_operator);

        edtx_phone.setTag("false");
        lnly_operator.setVisibility(View.INVISIBLE);

        RecyclerView rcvw_nominal = findViewById(R.id.rcvw_nominal);
        rcvw_nominal.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new NominalAdapter(mActivity, nominal);
        rcvw_nominal.setAdapter(adapter);

        registerReceiver(receiver, new IntentFilter("FINISH"));
    }

    @Override
    protected void initData() {
        initNominal();
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
        adapter.setOnSelectedListener((data, position) -> checkPrice(data));
    }

    @SuppressLint("SetTextI18n")
    private void validateNumber(String phone){
        edtx_phone.setTag("false");
        imvw_operator.setImageResource(0);
        lnly_error.setVisibility(View.INVISIBLE);
        lnly_operator.setVisibility(View.INVISIBLE);
        if  (phone.length() < 9){
            showNominal("");
            return;
        }
        OperatorPrefix prefix = new OperatorPrefix();
        prefix.getInfo(phone);
        if (!prefix.isValidated()){
            lnly_error.setVisibility(View.VISIBLE);
            txvw_error.setText("Nomor telepon tidak dikenali");
            return;
        }
        imvw_operator.setImageResource(prefix.getImage());
        edtx_phone.setTag("true");
        lnly_operator.setVisibility(View.VISIBLE);
        showNominal(prefix.getOperator());
    }

    private void initNominal(){
        PostManager post = new PostManager(mActivity, ConfigAPI.GET_PRODUCTS);
        post.addParamHeader("category_id", getIntent().getIntExtra("id",1));
        post.executeGET();
        post.setOnReceiveListener((obj, code, message) -> {

            ArrayList<ProductDB> allProd = new ArrayList<>();
            ProductDB productDB = new ProductDB();
            productDB.deleteByCategory(mActivity,getIntent().getStringExtra("code"));

            if (code == ErrorCode.OK){
                try {
                    JSONArray ja = obj.getJSONArray("data");
                    for (int i=0; i<ja.length(); i++){
                        JSONObject jo = ja.getJSONObject(i);
                        JSONObject group = jo.getJSONObject("group");
                        JSONObject additional = jo.getJSONObject("additional");

                        ProductDB db = new ProductDB();
                        db.productId    = jo.getString("id");
                        db.productCode  = jo.getString("code");
                        db.name         = jo.getString("name");
                        db.nominal      = jo.getString("denom");
                        db.groupID      = group.getString("id");
                        db.groupName    = group.getString("name");
                        db.description  = additional.getString("description");
                        db.status       = "1";
                        db.category     = getIntent().getStringExtra("code");
                        allProd.add(db);
                    }

                    productDB = new ProductDB();
                    productDB.insertBulk(mActivity, allProd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (getIntent().getStringExtra("customer_id") != null){
                    edtx_phone.setText(getIntent().getStringExtra("customer_id"));
                }
            }
            else {
                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showError("Gagal","Mohon maaf saat ini produk tidak tersedia");
                dialog.setOnCloseLister(action -> mActivity.finish());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showNominal(String operator){
        nominal.clear();
        if (operator.isEmpty()){
            adapter.notifyDataSetChanged();
            return;
        }
        ProductDB db = new ProductDB();
        ArrayList<ProductDB> allProd = db.getAllDataByGroup(mActivity,getIntent().getStringExtra("code"),operator);
        for (ProductDB productDB: allProd){
            Bundle bundle = new Bundle();
            bundle.putLong("nominal", Long.parseLong(productDB.nominal));
            bundle.putString("operator", productDB.groupName);
            bundle.putString("status", productDB.status);
            bundle.putString("id", productDB.productId);
            bundle.putString("code", productDB.productCode);
            bundle.putString("description", productDB.description);
            nominal.add(bundle);
        }
        adapter.notifyDataSetChanged();
    }

    private void checkPrice(Bundle bundle){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_CHECK_PRICE);
        post.addParam("product_id",Integer.parseInt(bundle.getString("id")));
        post.addParam("agent_id",mAgentId);
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    obj = obj.getJSONObject("data");
                    Intent intent = getIntent();
                    intent.setClass(mActivity, PaymentPrepaidActivity.class);
                    intent.putExtra("category_name",getIntent().getStringExtra("name"));
                    intent.putExtra("price",obj.getLong("selling_price"));
                    intent.putExtra("product_cogs_id",obj.getInt("product_cogs_id"));
                    intent.putExtra("product_id",bundle.getString("id"));
                    intent.putExtra("data", buildData(bundle).toString());

                    OperatorPrefix prefix = new OperatorPrefix();
                    prefix.getInfo(edtx_phone.getText().toString());
                    intent.putExtra("customer_id",prefix.getPhoneNumber());

                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else {
                Utility.showFailedDialog(mActivity,"Gagal menampilkan harga\n"+message);
            }
        });
    }

    private JSONArray buildData(Bundle bundle){
        JSONArray ja = new JSONArray();
        long nominal = bundle.getLong("nominal");
        String operator = bundle.getString("operator");
        ja.put(add("Kategori",getIntent().getStringExtra("name")));
        ja.put(add("Operator",operator));
        ja.put(add("Produk",operator+" "+ MyCurrency.toCurrnecy(nominal)));
        OperatorPrefix prefix = new OperatorPrefix();
        prefix.getInfo(edtx_phone.getText().toString());
        ja.put(add("Tujuan",prefix.getPhoneNumber()));
        return ja;
    }

    private JSONObject add(String key, String value){
        JSONObject category = new JSONObject();
        try {
            category.put("key",key);
            category.put("value",value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return category;
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
