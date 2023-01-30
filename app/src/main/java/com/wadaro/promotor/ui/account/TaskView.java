package com.wadaro.promotor.ui.account;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyLayout;

public class TaskView extends MyLayout {

    private TextView txvw_value_00;
    private ImageView imvw_status_00;
    public TaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.profile_view_task;
    }

    @Override
    protected void initLayout() {
        txvw_value_00 = findViewById(R.id.txvw_value_00);
        imvw_status_00 = findViewById(R.id.imvw_status_00);
    }

    @Override
    protected void initListener() {

    }

    public void create(String value){
        txvw_value_00.setText(value);
        setProcess();
    }

    public void setSuccess(){
        imvw_status_00.setColorFilter(Color.parseColor("#1CA336"));
    }
    public void setFailed(){
        imvw_status_00.setColorFilter(Color.parseColor("#EC3131"));
    }
    public void setProcess(){
        imvw_status_00.setColorFilter(Color.parseColor("#BFBFBF"));
    }
}
