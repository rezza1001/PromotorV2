package com.rentas.ppob.trans.tlppostpaid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.libs.OperatorPrefix;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;
import com.rentas.ppob.trans.post.PaymentPostActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class MainTlpPostActivity extends MyActivity {

    private HeaderView header_view;
    private ImageView imvw_clean,imvw_operator;
    private EditText edtx_phone;
    private LinearLayout lnly_error;
    private TextView txvw_error,txvw_check,txvw_product,txvw_admin;
    private CardView card_product;

    private int productId = 0;
    private long admin = 0;

    private final HashMap<String, Bundle> mapProduct = new HashMap<>();


    @Override
    protected int setLayout() {
        return R.layout.trans_tlppost_activity_main;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("Telpeon Postpaid");

        imvw_clean = findViewById(R.id.imvw_clean);
        imvw_clean.setColorFilter(Color.parseColor("#9B9B9B"));

        edtx_phone = findViewById(R.id.edtx_phone);
        lnly_error = findViewById(R.id.lnly_error);
        txvw_error = findViewById(R.id.txvw_error);
        txvw_check = findViewById(R.id.txvw_check);
        imvw_operator = findViewById(R.id.imvw_operator);
        txvw_product = findViewById(R.id.txvw_product);
        txvw_admin = findViewById(R.id.txvw_admin);
        card_product = findViewById(R.id.card_product);

        card_product.setVisibility(View.INVISIBLE);
        edtx_phone.setTag("false");
        edtx_phone.setHint("Nomor Telepon");

        txvw_check.setBackground(Utility.getShapeLine(mActivity,1,4,getResources().getColor(R.color.colorPrimary),0));


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

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                lnly_error.setVisibility(View.INVISIBLE);
                card_product.setVisibility(View.INVISIBLE);
                if  (s.length() < 9){
                    return;
                }
                OperatorPrefix prefix = new OperatorPrefix();
                prefix.getInfo(s.toString());
                if (!prefix.isValidated() ){
                    lnly_error.setVisibility(View.VISIBLE);
                    txvw_error.setText("Nomor telepon tidak dikenali");
                    return;
                }

                if (!s.toString().isEmpty()){
                    imvw_clean.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                }
                else {
                    imvw_clean.setColorFilter(Color.parseColor("#9B9B9B"));
                }

                Bundle bundle = mapProduct.get(prefix.getOperator().toUpperCase());
                if (bundle != null){
                    card_product.setVisibility(View.VISIBLE);
                    txvw_product.setText(bundle.getString("productName"));
                    imvw_operator.setImageResource(prefix.getImage());
                    txvw_admin.setTag(MyCurrency.toCurrnecy("Admin", bundle.getLong("admin")));
                    productId = bundle.getInt("productId");
                    admin = bundle.getLong("admin");
                }
                else {
                    lnly_error.setVisibility(View.VISIBLE);
                    txvw_error.setText("Mohon maaf saat ini produk "+ prefix.getOperator()+" tidak ditemukan");
                }

            }
        });
        imvw_clean.setOnClickListener(v -> edtx_phone.setText(""));

        txvw_check.setOnClickListener(v -> checkPrice());
    }

    private void getProduct(){
        mapProduct.clear();
        PostManager post = new PostManager(mActivity, ConfigAPI.GET_PRODUCTS);
        post.addParamHeader("category_id", getIntent().getIntExtra("id",1));
        post.executeGET();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONArray ja = obj.getJSONArray("data");
                    for (int i=0; i<ja.length(); i++){
                        JSONObject jo = ja.getJSONObject(i);
                        JSONObject group = jo.getJSONObject("group");

                        Bundle bundle = new Bundle();
                        bundle.putLong("admin", jo.getLong("admin"));
                        bundle.putInt("productId", jo.getInt("id"));
                        bundle.putString("productName", jo.getString("name"));
                        bundle.putString("groupName", group.getString("name"));
                        mapProduct.put( jo.getString("name").split(" ")[0], bundle);
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
                    inquiry(obj.getInt("product_cogs_id"));
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
    private void inquiry(int productCogId){
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

    public void openContact(){
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS};
        if(Utility.checkPermission(mActivity, PERMISSIONS)){
           return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mActivity.startActivityForResult(intent, 1);
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
