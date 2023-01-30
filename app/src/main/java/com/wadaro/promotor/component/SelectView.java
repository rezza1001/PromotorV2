package com.wadaro.promotor.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyLayout;

public class SelectView extends MyLayout {

    private TextView txvw_hint_00,txvw_value_00;
    private MaterialRippleLayout mrly_select_00;
    private SelectHolder data = new SelectHolder();
    private boolean isReadOnly = false;

    public SelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.component_select_view;
    }

    @Override
    protected void initLayout() {
        txvw_hint_00 = findViewById(R.id.txvw_hint_00);
        txvw_value_00 = findViewById(R.id.txvw_value_00);
        mrly_select_00 = findViewById(R.id.mrly_select_00);
    }

    @Override
    protected void initListener() {
        mrly_select_00.setOnClickListener(v -> {
            if (mSelectedListener != null && !isReadOnly){
                mSelectedListener.onSelected(data);
            }
        });
    }

    public void setHint(String hint){
        txvw_hint_00.setText(hint);
    }

    public void hideHint(){
        txvw_hint_00.setVisibility(GONE);
    }


    public void setValue(SelectHolder value){
        if (value.id.isEmpty()){
            data = new SelectHolder();
        }
        else {
            data = value;
            txvw_value_00.setText(value.value);
        }
    }

    public void setReadOnly(boolean readOnly){
        isReadOnly = readOnly;
    }

    public String getHint(){
        return txvw_hint_00.getText().toString();
    }

    public String getKey(){
        return data == null ? "": data.id;
    }
    public String getValue(){
        return data.value;
    }

    public void setEmpty(){
        data = new SelectHolder();
    }

    private SelectedListener mSelectedListener;
    public void setSelectedListener(SelectedListener pSelectedListener){
        mSelectedListener = pSelectedListener;
    }

    public interface SelectedListener{
        void onSelected(SelectHolder data);
    }

}
