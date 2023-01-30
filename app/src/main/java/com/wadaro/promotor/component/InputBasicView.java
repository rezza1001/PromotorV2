package com.wadaro.promotor.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyLayout;

public class InputBasicView extends MyLayout {

    private TextView txvw_label_00,txvw_mandatory_00;
    private EditText edtx_value_00;
    public InputBasicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.component_view_inputbasic;
    }

    @Override
    protected void initLayout() {
        txvw_label_00       = findViewById(R.id.txvw_label_00);
        txvw_mandatory_00   = findViewById(R.id.txvw_mandatory_00);
        edtx_value_00   = findViewById(R.id.edtx_value_00);
        txvw_mandatory_00.setVisibility(GONE);

    }

    @Override
    protected void initListener() {

    }

    public void setLabel(String label){
        txvw_label_00.setText(label);
    }

    public void setInputType(int inputType, int inputype2){
        edtx_value_00.setInputType(inputType|inputype2);
    }

    public void setValue(String value){
        edtx_value_00.setText(value);
    }

    public String getValue(){
        return edtx_value_00.getText().toString();
    }

    public void setMandatory(){
        txvw_mandatory_00.setVisibility(VISIBLE);
    }

    public boolean isValid(){
        if (txvw_mandatory_00.getVisibility() == VISIBLE){
            if (edtx_value_00.getText().toString().isEmpty()){
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return true;
        }
    }
}
