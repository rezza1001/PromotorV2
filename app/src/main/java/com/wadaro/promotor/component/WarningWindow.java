package com.wadaro.promotor.component;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.balysv.materialripple.MaterialRippleLayout;
import com.wadaro.promotor.R;
import com.wadaro.promotor.module.Utility;

import java.util.Objects;

public class WarningWindow extends Dialog {

    private TextView txvw_message_00,txvw_title_00;
    public WarningWindow(@NonNull Context context) {
        super(context, R.style.AppTheme_transparent);
        WindowManager.LayoutParams wlmp = Objects.requireNonNull(getWindow()).getAttributes();

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.component_dialog_warning, null);
        setContentView(view);

        int radius = Utility.dpToPx(context, 5);

        RelativeLayout rvly_body_00 = view.findViewById(R.id.rvly_body_00);
        RelativeLayout rvly_header_00 = view.findViewById(R.id.rvly_header_00);
        MaterialRippleLayout mrly_left_00 = view.findViewById(R.id.mrly_left_00);
        MaterialRippleLayout mrly_right_00 = view.findViewById(R.id.mrly_right_00);
        txvw_message_00 = view.findViewById(R.id.txvw_message_00);
        txvw_title_00 = view.findViewById(R.id.txvw_title_00);

        rvly_body_00.setBackground(Utility.getRectBackground("FFFFFF",radius));
        rvly_header_00.setBackground(Utility.getRectBackground("FFFFFF",radius,radius,0,0));
        mrly_left_00.setBackground(Utility.getRectBackground("6D7E9A",radius));
        mrly_right_00.setBackground(Utility.getRectBackground("FC716A",radius));

        mrly_left_00.setOnClickListener(v -> {
            if (mLIstener != null){
                mLIstener.onClose(1);
            }
            dismiss();
        });
        mrly_right_00.setOnClickListener(v -> {
            if (mLIstener != null){
                mLIstener.onClose(2);
            }
            dismiss();
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void show(String title, String message) {
        super.show();
        txvw_title_00.setText(title);
        txvw_message_00.setText(message);
    }

    private OnCLoseListener mLIstener;
    public void setOnSelectedListener(OnCLoseListener onUpgradeListener){
        mLIstener = onUpgradeListener;
    }
    public interface OnCLoseListener{
        void onClose(int status);
    }
}