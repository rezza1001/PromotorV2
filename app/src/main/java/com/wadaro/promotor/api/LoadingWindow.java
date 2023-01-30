package com.wadaro.promotor.api;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.wadaro.promotor.R;

import java.util.Objects;

public class LoadingWindow extends Dialog {

    public LoadingWindow(@NonNull Context context) {
        super(context, R.style.AppTheme_transparent);
        WindowManager.LayoutParams wlmp = Objects.requireNonNull(getWindow()).getAttributes();

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.api_dialog_loading, null);
        setContentView(view);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mLIstener != null){
            mLIstener.onClose();
        }
    }

    private OnCLoseListener mLIstener;
    public void setOnHomeClickListener(OnCLoseListener onUpgradeListener){
        mLIstener = onUpgradeListener;
    }
    public interface OnCLoseListener{
        void onClose();
    }
}