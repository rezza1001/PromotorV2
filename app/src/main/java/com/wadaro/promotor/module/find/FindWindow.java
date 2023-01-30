package com.wadaro.promotor.module.find;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.module.Utility;

import java.util.ArrayList;

public class FindWindow extends Dialog {

    private TextView txvw_title_00;
    private RelativeLayout rvly_body_00;
    private RecyclerView rcvw_find_00;

    public FindWindow(@NonNull Context context) {
        super(context, R.style.AppTheme_transparent);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.libs_find_window, null);
        setContentView(view);

        txvw_title_00 = view.findViewById(R.id.txvw_title_00);
        rvly_body_00 = view.findViewById(R.id.rvly_body_00);

        rvly_body_00.setVisibility(View.INVISIBLE);
        rvly_body_00.setBackground(Utility.getRectBackground("ffffff", Utility.dpToPx(getContext(),5)));

        rcvw_find_00 = view.findViewById(R.id.rcvw_find_00);
        rcvw_find_00.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvw_find_00.setNestedScrollingEnabled(false);
    }

    public void show(ArrayList<FindHolder> data) {
        super.show();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_in);
        rvly_body_00.setAnimation(animation);
        rvly_body_00.setVisibility(View.VISIBLE);

        FindAdapter adapter = new FindAdapter(data);
        rcvw_find_00.setAdapter(adapter);
        adapter.setOnSelectedListener(data1 -> {
            dismiss();
            new Handler().postDelayed(() -> {
                if (mLIstener  != null){
                    mLIstener.onFinish(data1);
                }
            },500);
        });
    }

    @Override
    public void dismiss() {
        rvly_body_00.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_out);
        rvly_body_00.setAnimation(animation);
        new Handler().postDelayed(super::dismiss,500);

    }

    public void setHeaderTitle(String title){
        txvw_title_00.setText(title);
    }


    @Override
    public void onBackPressed() {
        dismiss();
    }

    private OnSelectedListener mLIstener;
    public void setOnSelectedListener(OnSelectedListener onForceDismissListener){
        mLIstener = onForceDismissListener;
    }
    public interface OnSelectedListener{
        void onFinish(FindHolder data);
    }
}
