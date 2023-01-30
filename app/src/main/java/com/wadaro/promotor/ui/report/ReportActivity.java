package com.wadaro.promotor.ui.report;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyActivity;

import java.util.Calendar;

public class ReportActivity extends MyActivity {

    private RelativeLayout lnly_date_00;
    private TextView txvw_date_00;

    @Override
    protected int setLayout() {
        return R.layout.report_activity_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Hasil Demo");

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.pager);

        TabelFragment tabelFragment = new TabelFragment();
        MapFragment mapFragment = new MapFragment();

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(tabelFragment, "TABEL");
        adapter.addFragment(mapFragment, "MAP");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        lnly_date_00 = findViewById(R.id.lnly_date_00);
        txvw_date_00 = findViewById(R.id.txvw_date_00);
    }

    @Override
    protected void initData() {
        new Handler().postDelayed(this::request,500);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initListener() {
        Calendar calendar = Calendar.getInstance();
        txvw_date_00.setText(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DATE));

        lnly_date_00.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, (view, year, monthOfYear, dayOfMonth) -> {
                txvw_date_00.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                request();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            datePickerDialog.show();
        });

        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }

    private void request(){
        Intent intent = new Intent("REFRESH");
        intent.putExtra("date",txvw_date_00.getText().toString());
        sendBroadcast(intent);
    }
}
