package com.wadaro.promotor.ui.demo.process;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.balysv.materialripple.MaterialRippleLayout;
import com.wadaro.promotor.R;
import com.wadaro.promotor.module.Utility;

import java.util.Objects;

public class CancelDialog extends Dialog {

    private final ImageView imvw_checked_00;
    public CancelDialog(@NonNull Context context) {
        super(context, R.style.AppTheme_transparent);
        WindowManager.LayoutParams wlmp = Objects.requireNonNull(getWindow()).getAttributes();

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.demo_process_dialog_cancel, null);
        setContentView(view);

        int radius = Utility.dpToPx(context, 5);

        RelativeLayout rvly_body_00 = view.findViewById(R.id.rvly_body_00);
        RelativeLayout rvly_header_00 = view.findViewById(R.id.rvly_header_00);
        MaterialRippleLayout mrly_left_00 = view.findViewById(R.id.mrly_left_00);
        MaterialRippleLayout mrly_right_00 = view.findViewById(R.id.mrly_right_00);

        rvly_body_00.setBackground(Utility.getRectBackground("FFFFFF",radius));
        rvly_header_00.setBackground(Utility.getRectBackground("FFFFFF",radius,radius,0,0));
        mrly_left_00.setBackground(Utility.getRectBackground("6D7E9A",radius));
        mrly_right_00.setBackground(Utility.getRectBackground("FC716A",radius));

        imvw_checked_00 = view.findViewById(R.id.imvw_checked_00);
        imvw_checked_00.setTag(0);
        imvw_checked_00.setOnClickListener(v -> {
            if (imvw_checked_00.getTag().toString().equals("0")){
                imvw_checked_00.setTag(1);
                imvw_checked_00.setImageResource(R.drawable.ic_radio_checked);
            }
            else {
                imvw_checked_00.setTag(0);
                imvw_checked_00.setImageResource(R.drawable.ic_radio_uncheck);
            }
        });

        mrly_left_00.setOnClickListener(v -> dismiss());
        mrly_right_00.setOnClickListener(v -> {
            if (mLIstener != null){
                mLIstener.onClose(Integer.parseInt(imvw_checked_00.getTag().toString()));
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


    private OnCLoseListener mLIstener;
    public void setOnSelectedListener(OnCLoseListener onUpgradeListener){
        mLIstener = onUpgradeListener;
    }
    public interface OnCLoseListener{
        void onClose(int status);
    }
}