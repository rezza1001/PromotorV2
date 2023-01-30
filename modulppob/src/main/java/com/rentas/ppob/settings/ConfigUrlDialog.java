package com.rentas.ppob.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyDialog;

public class ConfigUrlDialog extends MyDialog {

    private CardView card_create;
    private RelativeLayout rvly_Pin,rvly_url;
    private EditText edtx_pin,edtx_url;
    private TextView txvw_title;

    public ConfigUrlDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.setting_url_dialog;
    }

    @Override
    protected void initLayout(View view) {
        card_create = view.findViewById(R.id.card_create);
        rvly_Pin = view.findViewById(R.id.rvly_Pin);
        edtx_pin = view.findViewById(R.id.edtx_pin);
        txvw_title = view.findViewById(R.id.txvw_title);
        rvly_url = view.findViewById(R.id.rvly_url);
        edtx_url = view.findViewById(R.id.edtx_url);
        card_create.setVisibility(View.INVISIBLE);
        rvly_url.setVisibility(View.GONE);

        view.findViewById(R.id.txvw_create).setOnClickListener(v -> configAPI());
        view.findViewById(R.id.rvly_body).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.lnly_body).setOnClickListener(v -> {

        });

        initUI();
    }

    private void initUI(){
        edtx_url.setText(ConfigAPI.getMainUrl(mActivity));
        edtx_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                Log.d("TAGRZ","value "+value);
                if (value.length() == 8){

                    if (value.equals("11121314")){
                        card_create.setVisibility(View.VISIBLE);
                        rvly_url.setVisibility(View.VISIBLE);
                        rvly_Pin.setVisibility(View.GONE);
                        txvw_title.setText("Configuration main URL API");
                    }
                    new Handler().postDelayed(() -> {
                        edtx_pin.removeTextChangedListener(this);
                        edtx_pin.setText(null);
                        edtx_pin.addTextChangedListener(this);
                    },1000);
                }
            }
        });
    }

    private void configAPI(){
        String url = edtx_url.getText().toString().trim();
        if (url.isEmpty()){
            Utility.showToastError(mActivity,"Empty URL");
            return;
        }
        if (!url.startsWith("http")){
            Utility.showToastError(mActivity,"Invalid URL, Start with http / https");
            return;
        }

        Utility.showToastSuccess(mActivity,"Url has been changed");
        ConfigAPI.setMainURL(mActivity, url);
        if (onConfigChangeListener != null){
            onConfigChangeListener.onChange();
        }
        dismiss();
    }

    private OnConfigChangeListener onConfigChangeListener;
    public void setOnConfigChangeListener(OnConfigChangeListener onConfigChangeListener){
        this.onConfigChangeListener = onConfigChangeListener;
    }
    public interface OnConfigChangeListener{
        void onChange();
    }

}
