package com.wadaro.promotor.ui.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainDemoFrg extends MyFragment {

    private TextView txvw_date_00;

    public static MainDemoFrg newInstance() {
        Bundle args = new Bundle();

        MainDemoFrg fragment = new MainDemoFrg();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setlayout() {
        return R.layout.demo_frg_main;
    }

    @Override
    protected void initLayout(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.pager);
        txvw_date_00 = view.findViewById(R.id.txvw_date_00);

        TabelFragment tabelFragment = new TabelFragment();
        MapFragment mapFragment = new MapFragment();

        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addFragment(tabelFragment, "TABEL");
        adapter.addFragment(mapFragment, "MAP");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        DateFormat format = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
        txvw_date_00.setText(format.format(new Date()));
        new Handler().postDelayed(() -> mActivity.sendBroadcast(new Intent("REFRESH")),100);
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.registerReceiver(receiver, new IntentFilter("CHANGE_FINISH"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mActivity.unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "CHANGE_FINISH")){
                initData();
            }
        }
    };
}
