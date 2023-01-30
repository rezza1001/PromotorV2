package com.wadaro.promotor.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.wadaro.promotor.R;
import com.wadaro.promotor.component.ActionButton;

import java.util.Calendar;

public class MyTimeSpinner extends Dialog {

    private TimePicker time_spnr_00;
    private LinearLayout lnly_body_00;
    private int mHour = 0;
    private int mMinute = 0;

    public MyTimeSpinner(@NonNull Context context) {
        super(context, R.style.AppTheme_transparent);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.component_timepicker_spiner, null);
        setContentView(view);

        ActionButton act_cancel_00 = view.findViewById(R.id.act_cancel_00);
        act_cancel_00.setText("Cancel");
        act_cancel_00.setColor("2686DC");
        ActionButton act_ok_00 = view.findViewById(R.id.act_ok_00);
        act_ok_00.setText("OK");
        act_ok_00.setColor("2686DC");

        time_spnr_00 = view.findViewById(R.id.time_spnr_00);
        lnly_body_00 = view.findViewById(R.id.lnly_body_00);
        lnly_body_00.setVisibility(View.INVISIBLE);
        time_spnr_00.setIs24HourView(true);

        time_spnr_00.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            mHour = hourOfDay;
            mMinute = minute;
        });

        act_cancel_00.setOnclikLIstener(this::dismiss);

        act_ok_00.setOnclikLIstener(() -> {
            dismiss();
            if (mLIstener != null){
                mLIstener.onSelected(mHour, mMinute);
            }
        });


    }

    @Override
    public void show() {
        super.show();
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.push_up_in);
        lnly_body_00.setAnimation(animation);
        lnly_body_00.setVisibility(View.VISIBLE);

        Calendar calendar = Calendar.getInstance();
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

    }

    public void setValue(int hour, int minute){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            time_spnr_00.setMinute(minute);
            time_spnr_00.setHour(hour);
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    private OnCloseListener mLIstener;
    public void setOnForceDismissListener(OnCloseListener onForceDismissListener){
        mLIstener = onForceDismissListener;
    }
    public interface OnCloseListener{
        void onSelected(int hour, int minute);
    }
}
