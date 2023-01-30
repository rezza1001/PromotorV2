package com.wadaro.promotor.util;

import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyLayout;
import com.wadaro.promotor.module.Utility;

public class FormEditext extends MyLayout {

    private TextView txvw_key_00,txvw_mandatory_00;
    private EditText edtx_data_00;
    private View view_block_00;

    public FormEditext(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.libs_view_editextform;
    }

    @Override
    protected void initLayout() {
        txvw_key_00 = findViewById(R.id.txvw_key_00);
        txvw_mandatory_00 = findViewById(R.id.txvw_mandatory_00);
        txvw_mandatory_00.setVisibility(GONE);

        edtx_data_00 = findViewById(R.id.edtx_data_00);
        view_block_00 = findViewById(R.id.view_block_00);

        view_block_00.setVisibility(GONE);
    }

    @Override
    protected void initListener() {

    }

    public void setTitle(String title){
        txvw_key_00.setText(title);
    }
    public void setMandatory(){
        txvw_mandatory_00.setVisibility(VISIBLE);
    }

    public void setDisable(){
        txvw_key_00.setTextColor(Color.LTGRAY);
        view_block_00.setVisibility(VISIBLE);
    }

    public void setMultiLine(){
        edtx_data_00.setLines(2);
        int padding_8 = Utility.dpToPx(mActivity, 8);
        edtx_data_00.setPadding(padding_8,3,3,padding_8);
    }

    public void setValue(String value){
        if (value.equals("null")){
            edtx_data_00.setText("");
        }
        else {
            edtx_data_00.setText(value);
        }
    }

    public void withoutSpecialChar(){
        edtx_data_00.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        edtx_data_00.setFilters(new InputFilter[]{
                (src, start, end, dst, dstart, dend) -> {
                    if (src.equals("")) {
                        return src;
                    }
                    if (src.toString().matches("[a-zA-Z 0-9]+")) {
                        return src;
                    }
                    return "";
                }
        });
    }

    public void setInputType(int inputType){
        edtx_data_00.setInputType(inputType);
    }

    public String getValue(){
       return edtx_data_00.getText().toString();
    }
}
