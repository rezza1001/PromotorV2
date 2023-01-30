package com.wadaro.promotor.module;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wadaro.promotor.R;

public class SuccessWindow extends Dialog {

    private TextView txvw_desc_00;
    private RelativeLayout rvly_body_00;

    public SuccessWindow(@NonNull Context context) {
        super(context, R.style.AppTheme_transparent);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.libs_window_success, null);
        setContentView(view);

        txvw_desc_00 = view.findViewById(R.id.txvw_desc_00);
        rvly_body_00 = view.findViewById(R.id.rvly_body_00);

        rvly_body_00.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.mrly_action_00).setOnClickListener(v -> dismiss());
        rvly_body_00.setBackground(Utility.getRectBackground("ffffff", Utility.dpToPx(getContext(),10)));
    }

    @Override
    public void show() {
        super.show();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_in);
        rvly_body_00.setAnimation(animation);
        rvly_body_00.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismiss() {
        rvly_body_00.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_out);
        rvly_body_00.setAnimation(animation);

        new Handler().postDelayed(() -> {
            if (mLIstener  != null){
                mLIstener.onFinish();
            }
            super.dismiss();
        },500);
    }

    public void setDescription(String description){
        txvw_desc_00.setText(description);
    }


    @Override
    public void onBackPressed() {
        dismiss();
    }

    private OnFinishListener mLIstener;
    public void setOnFinishListener(OnFinishListener onForceDismissListener){
        mLIstener = onForceDismissListener;
    }
    public interface OnFinishListener{
        void onFinish();
    }
}
