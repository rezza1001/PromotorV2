package com.wadaro.promotor.ui.report;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyLayout;

public class Mymarker extends MyLayout {

    private ImageView imvw_icon_00;
    private TextView txvw_number_00;
    public Mymarker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.booking_view_mymarker;
    }

    @Override
    protected void initLayout() {
        imvw_icon_00 = findViewById(R.id.imvw_icon_00);
        txvw_number_00 = findViewById(R.id.txvw_number_00);
    }

    @Override
    protected void initListener() {

    }

    public void create(int resource, int color, int number, int numberColor){
//        imvw_icon_00.setImageResource(resource);
        imvw_icon_00.setColorFilter(color);
        txvw_number_00.setText(number+"");
        txvw_number_00.setTextColor(numberColor);
        findViewById(R.id.rvly_body_00).setOnClickListener(v -> {
            Log.d("TAGRZ","Click Here "+number);
        });
    }
}
