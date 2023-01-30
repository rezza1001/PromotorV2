package com.wadaro.promotor;

import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import com.wadaro.promotor.base.MyActivity;

public class UpdateAppsActivity extends MyActivity {

    @Override
    protected int setLayout() {
        return R.layout.activity_updateapps;
    }

    @Override
    protected void initLayout() {
        TextView txvw_desc_00 = findViewById(R.id.txvw_desc_00);
        String note = "Aplikasi #A1 telah berganti versi. Untuk menggunakan #A1 silahkan download aplikasi terbaru terlebih dahulu";
        note = note.replaceAll("#A1",getResources().getString(R.string.app_name));
        txvw_desc_00.setText(note);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        findViewById(R.id.mrly_download_00).setOnClickListener(v -> {
            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://erp.wadaro.id/wadaro-erp/download/"));
            startActivity(viewIntent);
            mActivity.finish();
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
