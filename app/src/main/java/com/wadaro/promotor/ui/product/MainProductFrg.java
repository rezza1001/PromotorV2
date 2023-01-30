package com.wadaro.promotor.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainProductFrg extends MyFragment {

    private ProductAdapter mAdapter;
    private ArrayList<Bundle> allData = new ArrayList<>();

    public static MainProductFrg newInstance() {
        Bundle args = new Bundle();
        MainProductFrg fragment = new MainProductFrg();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setlayout() {
        return R.layout.product_frgment_main;
    }

    @Override
    protected void initLayout(View view) {
        RecyclerView rcvw_data_00 = view.findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new GridLayoutManager(mActivity,2));

        mAdapter = new ProductAdapter(mActivity, allData);
        rcvw_data_00.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, DetailProductActivity.class);
            intent.putExtra("data", data);
            startActivity(intent);
        });
    }

    @Override
    protected void initData() {
        allData.clear();
        PostManager post = new PostManager(mActivity, Config.GET_INFO_PRODUCT);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONArray data = obj.getJSONArray("data");
                    for (int i=0; i<data.length(); i++){
                        JSONObject jo = data.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("product_name", jo.getString("product_name"));
                        bundle.putString("product_id", jo.getString("product_id"));
                        bundle.putString("price", jo.getString("price"));
                        bundle.putString("image", jo.getString("product_photo"));
                        allData.add(bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mAdapter.notifyDataSetChanged();
        });
    }
}
