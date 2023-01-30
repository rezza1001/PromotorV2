package com.wadaro.promotor.component;

import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyActivity;

import java.util.ArrayList;

public class SelectActivity extends MyActivity {

    private SelectAdapter mAdapter;
    private EditText edtx_search_00;
    private ArrayList<SelectHolder> allData = new ArrayList<>();
    private ArrayList<SelectHolder> filterData = new ArrayList<>();

    @Override
    protected int setLayout() {
        return (R.layout.component_activity_select);
    }

    @Override
    protected void initLayout() {
        RelativeLayout rvly_header_10 = findViewById(R.id.rvly_header_10);
        rvly_header_10.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        RecyclerView rcvw_gov_00 = findViewById(R.id.rcvw_gov_00);
        mAdapter    = new SelectAdapter(mActivity,filterData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rcvw_gov_00.setLayoutManager(mLayoutManager);
        rcvw_gov_00.setItemAnimator(new DefaultItemAnimator());
        rcvw_gov_00.setAdapter(mAdapter);

        edtx_search_00  = findViewById(R.id.edtx_search_00);

        ImageView imvw_back_00 = findViewById(R.id.imvw_back_00);
        imvw_back_00.setColorFilter(Color.WHITE);
    }

    @Override
    protected void initData() {
        allData.clear();
        filterData.clear();

        ArrayList<String> data = getIntent().getStringArrayListExtra("DATA");
        assert data != null;
        for (String sData : data){
            SelectHolder select = new SelectHolder(sData);
            allData.add(select);
        }
        filter("");

    }


    @Override
    protected void initListener() {
        edtx_search_00.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        mAdapter.setOnSelectedListener((pData, position) -> {
            Intent intent = new Intent();
            intent.putExtra("DATA", pData.toString());
            setResult(RESULT_OK, intent);
            finish();
        });

        findViewById(R.id.mrly_back_00).setOnClickListener(v -> onBackPressed());
    }

    private void filter(String data){
        mAdapter.setKey(data);
        if (data.length() >= 1 ){
            filterData.clear();

            for (SelectHolder opt: allData){
                if (opt.value.toUpperCase().contains(data.toUpperCase())){
                    filterData.add(opt);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
        else {
            filterData.clear();
            filterData.addAll(allData);
            mAdapter.notifyDataSetChanged();
        }
    }
}
