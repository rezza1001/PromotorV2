package com.wadaro.promotor.ui.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyLayout;

public class InfoView extends MyLayout {

    private TextView txvw_key_00,txvw_value_00;

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.order_view_info;
    }

    @Override
    protected void initLayout() {
        txvw_key_00 = findViewById(R.id.txvw_key_00);
        txvw_value_00 = findViewById(R.id.txvw_value_00);
    }

    @Override
    protected void initListener() {

    }

    public void create(String key, String value){
        txvw_key_00.setText(key);
        txvw_value_00.setText(value);
    }
}
