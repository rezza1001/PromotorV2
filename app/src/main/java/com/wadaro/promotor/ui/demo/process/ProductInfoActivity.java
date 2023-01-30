package com.wadaro.promotor.ui.demo.process;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.ui.product.MainProductFrg;

public class ProductInfoActivity extends MyActivity {
    @Override
    protected int setLayout() {
        return R.layout.demo_process_activity_infoproduct;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Info Produk");

    }

    @Override
    protected void initData() {
        Fragment fragment = MainProductFrg.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_body_00, fragment,"main");
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }
}
