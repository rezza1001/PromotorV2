package com.rentas.ppob.report.receipt;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.rentas.ppob.R;
import com.rentas.ppob.master.MyView;

public class ReceiptItemView extends MyView {

    private RelativeLayout rvly_horizontal;
    private LinearLayout lnly_vertical;
    private TextView txvw_key,txvw_value,txvw_keyV,txvw_valueV;

    public ReceiptItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.report_receipt_view_item;
    }

    @Override
    protected void initLayout() {
        rvly_horizontal = findViewById(R.id.rvly_horizontal);
        rvly_horizontal.setVisibility(GONE);
        txvw_key = findViewById(R.id.txvw_key);
        txvw_value = findViewById(R.id.txvw_value);

        lnly_vertical = findViewById(R.id.lnly_vertical);
        lnly_vertical.setVisibility(GONE);
        txvw_keyV = findViewById(R.id.txvw_keyV);
        txvw_valueV = findViewById(R.id.txvw_valueV);

    }

    @Override
    protected void initListener() {

    }

    public void createHorizontal(String key, String value){
        rvly_horizontal.setVisibility(VISIBLE);
        txvw_key.setText(key);
        txvw_value.setText(value);
    }

    public void createVertical(String key, String value){
        lnly_vertical.setVisibility(VISIBLE);
        txvw_keyV.setText(key);
        if (key.isEmpty()){
            txvw_keyV.setVisibility(GONE);
        }
        txvw_valueV.setText(value);
    }

    public void setFontKey(int font, int size){
        txvw_key.setTextSize(size);
        txvw_keyV.setTextSize(size);
        Typeface fontType =  ResourcesCompat.getFont(mActivity,font);
        txvw_key.setTypeface(fontType);
        txvw_keyV.setTypeface(fontType);
    }

    public void setFontValue(int font, int size){
        txvw_value.setTextSize(size);
        txvw_valueV.setTextSize(size);
        Typeface fontType =  ResourcesCompat.getFont(mActivity,font);
        txvw_value.setTypeface(fontType);
        txvw_valueV.setTypeface(fontType);
    }
}
