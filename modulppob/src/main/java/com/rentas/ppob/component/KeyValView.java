package com.rentas.ppob.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.rentas.ppob.R;
import com.rentas.ppob.master.MyView;

public class KeyValView extends MyView {

    private TextView txvw_key,txvw_value;

    public KeyValView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.component_view_keyvalue;
    }

    @Override
    protected void initLayout() {
        txvw_key = findViewById(R.id.txvw_key);
        txvw_value = findViewById(R.id.txvw_value);
    }

    @Override
    protected void initListener() {
    }


    public void create(String title, String value){
        txvw_key.setText(title);
        txvw_value.setText(value);
    }

    public void createBig(String title, String value){
        Typeface font =  ResourcesCompat.getFont(mActivity,R.font.roboto_bold);
        txvw_key.setTextSize(16);
        txvw_value.setTextSize(16);
        txvw_key.setTextColor(Color.parseColor("#062C56"));
        txvw_value.setTextColor(Color.parseColor("#E3793B"));
        txvw_key.setTypeface(font);
        txvw_value.setTypeface(font);

        txvw_key.setText(title);
        txvw_value.setText(value);
    }

    public void setValueColor(int color){
        txvw_value.setTextColor(color);
    }

}
