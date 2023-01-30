package com.wadaro.promotor.component;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.wadaro.promotor.R;

public class MyDateSpinner extends Dialog {

    private DatePicker dspn_date_00;
    private LinearLayout lnly_body_00;
    public MyDateSpinner(@NonNull Context context) {
        super(context, R.style.AppTheme_transparent);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.component_datepicker_spiner, null);
        setContentView(view);

        ActionButton act_cancel_00 = view.findViewById(R.id.act_cancel_00);
        act_cancel_00.setText("Cancel");
        act_cancel_00.setColor("2686DC");
        ActionButton act_ok_00 = view.findViewById(R.id.act_ok_00);
        act_ok_00.setText("OK");
        act_ok_00.setColor("2686DC");


        dspn_date_00 = view.findViewById(R.id.dspn_date_00);
        lnly_body_00 = view.findViewById(R.id.lnly_body_00);
        lnly_body_00.setVisibility(View.INVISIBLE);
        act_cancel_00.setOnclikLIstener(this::dismiss);

        act_ok_00.setOnclikLIstener(() -> {
            dismiss();
            if (mLIstener != null){
                mLIstener.onSelected(dspn_date_00.getYear(),dspn_date_00.getMonth()+1, dspn_date_00.getDayOfMonth());
            }
        });


    }

    @Override
    public void show() {
        super.show();
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.push_up_in);
        lnly_body_00.setAnimation(animation);
        lnly_body_00.setVisibility(View.VISIBLE);

    }

    public void setValue(int yaer, int month, int date){
        dspn_date_00.init(yaer,month,date,null);
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
        void onSelected(int year, int month, int date);
    }
}
