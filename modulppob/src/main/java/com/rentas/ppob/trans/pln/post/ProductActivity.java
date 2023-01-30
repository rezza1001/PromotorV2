package com.rentas.ppob.trans.pln.post;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.database.table.ProductDB;
import com.rentas.ppob.master.MyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductActivity extends MyActivity {

    private HeaderView header_view;
    private ProductAdapter mAdapter;
    private final ArrayList<Bundle> products = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.multifinance_activity_product;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("Tagihan PLN");
        RecyclerView rcvw_data = findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        mAdapter = new ProductAdapter(mActivity, products);
        rcvw_data.setAdapter(mAdapter);

        registerReceiver(receiver, new IntentFilter("FINISH"));
    }

    @Override
    protected void initData() {
        initProduct();
    }

    @Override
    protected void initListener() {
        header_view.setOnBackListener(this::onBackPressed);
        mAdapter.setOnSelectedListener((data, position) -> {
            Intent intent = getIntent();
            intent.putExtra("product_id", data.getInt("id"));
            intent.putExtra("admin", data.getLong("admin"));
            intent.putExtra("product_name", data.getString("name"));
            intent.putExtra("description", data.getString("description"));
            intent.setClass(mActivity, PlnActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initProduct(){
        products.clear();
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
                        Bundle bundle = new Bundle();
                        bundle.putInt("id",jo.getInt("id"));
                        bundle.putLong("admin",jo.getLong("admin"));
                        bundle.putString("name",jo.getString("code"));
                        bundle.putString("description",jo.getString("name"));
                        products.add(bundle);
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
            mAdapter.notifyDataSetChanged();
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
