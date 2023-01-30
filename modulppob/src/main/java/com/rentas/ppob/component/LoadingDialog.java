package com.rentas.ppob.component;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.rentas.ppob.R;
import com.rentas.ppob.master.MyDialog;

public class LoadingDialog extends MyDialog {
    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.component_loading;
    }

    @Override
    protected void initLayout(View view) {

    }

    @Override
    public void onBackPressed() {

    }
}
