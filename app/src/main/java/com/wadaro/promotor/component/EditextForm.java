package com.wadaro.promotor.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyLayout;
import com.wadaro.promotor.util.MyCurrency;

import java.util.Objects;

public class EditextForm extends MyLayout {

    private TextInputEditText edtx_value_00;
    private RelativeLayout rvly_readonly_00,rvly_select_00;
    private TextInputLayout txip_00;
    private Bundle bundle;
    private boolean isCurrency = false;
    private String errorMessage = "Required";
    private String mBlockChar = "";
    private String mNominal = "0";

    public EditextForm(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.component_view_editextform;
    }

    @SuppressLint("ResourceType")
    protected void initLayout(){
        txip_00             = findViewById(R.id.txip_00);
        edtx_value_00       = findViewById(R.id.edtx_00);
        rvly_readonly_00    = findViewById(R.id.rvly_readonly_00);
        rvly_select_00      = findViewById(R.id.rvly_select_00);
        rvly_readonly_00.setVisibility(GONE);
        rvly_select_00.setVisibility(GONE);
        rvly_readonly_00.setOnClickListener(v -> {
        });
        rvly_select_00.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected();
            }
        });

        edtx_value_00.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isCurrency){
                    if(!editable.toString().equals(mNominal)){
                        edtx_value_00.removeTextChangedListener(this);
                        String cleanString = editable.toString().replaceAll("[.]", "");
                        edtx_value_00.setTag(cleanString);
                        String formatted = MyCurrency.toCurrnecy(cleanString);
                        mNominal = formatted;
                        edtx_value_00.setText(formatted);
                        edtx_value_00.setSelection(formatted.length());
                        if (formatted.equals("0")){
                            edtx_value_00.setText("");
                        }
                        edtx_value_00.addTextChangedListener(this);
                    }
                }
                if (mOnTextChangeListener != null){
                    mOnTextChangeListener.afterChange(editable.toString());
                }
            }
        });

        edtx_value_00.setOnFocusChangeListener((view, b) -> {
            if (b){
                if (isCurrency){
                    edtx_value_00.setText(getValue());
                }
            }
            else {
                if (isCurrency){
                    edtx_value_00.setText(MyCurrency.toCurrnecy(getValue()));
                }
            }
        });

    }

    @Override
    protected void initListener() {

    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setInputType(int inputType1, int inputType){
        edtx_value_00.setInputType(inputType1 |inputType);
        if (inputType == InputType.TYPE_TEXT_FLAG_MULTI_LINE){
            edtx_value_00.setSingleLine(false);
            edtx_value_00.setMaxLines(2);
        }
    }

    public void setCurrency(boolean currency) {
        isCurrency = currency;
    }

    public void setTitle(String pTitle){
        txip_00.setHint(pTitle);
    }

    public void setTextSize(int size){
        edtx_value_00.setTextSize(size);
    }

    public void setHint(String pHint){
        setTitle(pHint);
    }

    public String getValue(){
        if (isCurrency){
            return Objects.requireNonNull(edtx_value_00.getText()).toString().trim().replace(".","");
        }
        else {
            return Objects.requireNonNull(edtx_value_00.getText()).toString();
        }
    }

    public void setDigit(String digits){
        edtx_value_00.setKeyListener(DigitsKeyListener.getInstance(digits));
    }

    public String getKey(){
        return edtx_value_00.getTag().toString();
    }

    public String getTitle(){
        return Objects.requireNonNull(txip_00.getHint()).toString();
    }

    public void setValue(String value){
        edtx_value_00.setText(value);
        edtx_value_00.setTag(value);
    }
    public void setValue(String key, String value){
        edtx_value_00.setText(value);
        edtx_value_00.setTag(key);
    }

    public void setDisableCharacter(String filter){
        mBlockChar = filter;
        edtx_value_00.setFilters(new InputFilter[] { mBlockfilter });
    }

    private final InputFilter mBlockfilter = (source, start, end, dest, dstart, dend) -> {
        if (source != null && mBlockChar.contains(("" + source))) {
            return "";
        }
        return null;
    };

    public void setMaxLength(int length){
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(length);
        edtx_value_00.setFilters(fArray);
    }

    public void setErrorMessage(String pMessage){
        errorMessage = pMessage;
    }

    public boolean isValid(){
        if (getValue().isEmpty()){
            showError(true);
            edtx_value_00.requestFocus();
            return  false;
        }
        else {
            showError(false);
            txip_00.setErrorEnabled(false);
            return true;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setTypeSelect(){
        rvly_select_00.setVisibility(VISIBLE);
        edtx_value_00.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_drop_down), null);
        edtx_value_00.setEnabled(false);
    }

    public void setReadOnly(boolean readOnly){
        if (readOnly){
            edtx_value_00.setFocusable(false);
            rvly_readonly_00.setVisibility(VISIBLE);
        }
        else {
            rvly_readonly_00.setVisibility(GONE);
        }
    }

    public void showError(boolean ishow){
        txip_00.setError(errorMessage);
        txip_00.setErrorEnabled(ishow);
    }


    private OnTextChangeListener mOnTextChangeListener;

    public void setOnTextChangeListener(OnTextChangeListener pOnTextChangeListener){
        mOnTextChangeListener = pOnTextChangeListener;
    }
    public interface OnTextChangeListener{
        void afterChange(String text);
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected();
    }

}
