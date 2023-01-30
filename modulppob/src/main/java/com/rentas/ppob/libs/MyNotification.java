package com.rentas.ppob.libs;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.rentas.ppob.R;

public class MyNotification {

    public void show(Context context, View view, String title, String body, Bundle data){
        final Snackbar snackbar = Snackbar.make(view, title, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        View snackView =  LayoutInflater.from(context).inflate(R.layout.libs_snack_notification, null);
        TextView txvw_title_00 = snackView.findViewById(R.id.txvw_title_00);
        txvw_title_00.setText(title);
        TextView txvw_body_00 = snackView.findViewById(R.id.txvw_body_00);
        txvw_body_00.setText(body);
        layout.addView(snackView, 0);
        if (layout.getLayoutParams() instanceof  FrameLayout.LayoutParams){
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layout.getLayoutParams();
            lp.gravity = Gravity.TOP;
            layout.setLayoutParams(lp);
        }
        else if (layout.getLayoutParams() instanceof  CoordinatorLayout.LayoutParams){
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) layout.getLayoutParams();
            lp.gravity = Gravity.TOP;
            layout.setLayoutParams(lp);
        }
        else if (layout.getLayoutParams() instanceof  LinearLayout.LayoutParams){
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layout.getLayoutParams();
            lp.gravity = Gravity.TOP;
            layout.setLayoutParams(lp);
        }
        else if (layout.getLayoutParams() instanceof  RelativeLayout.LayoutParams){
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                lp.getRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            layout.setLayoutParams(lp);
        }
        layout.setBackgroundColor(Color.TRANSPARENT);
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
        snackbar.show();

        snackView.setOnClickListener(v -> {
            if (onClickListener != null){
                onClickListener.onClick(data);
            }
        });


    }

    public static void show(Context context, View view, String title, String body){
        final Snackbar snackbar = Snackbar.make(view, title, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        View snackView =  LayoutInflater.from(context).inflate(R.layout.libs_snack_notification, null);
        TextView txvw_title_00 = snackView.findViewById(R.id.txvw_title_00);
        txvw_title_00.setText(title);
        TextView txvw_body_00 = snackView.findViewById(R.id.txvw_body_00);
        txvw_body_00.setText(body);
        layout.addView(snackView, 0);
        if (layout.getLayoutParams() instanceof  FrameLayout.LayoutParams){
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layout.getLayoutParams();
            lp.gravity = Gravity.TOP;
            layout.setLayoutParams(lp);
        }
        else if (layout.getLayoutParams() instanceof  CoordinatorLayout.LayoutParams){
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) layout.getLayoutParams();
            lp.gravity = Gravity.TOP;
            layout.setLayoutParams(lp);
        }
        else if (layout.getLayoutParams() instanceof  LinearLayout.LayoutParams){
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layout.getLayoutParams();
            lp.gravity = Gravity.TOP;
            layout.setLayoutParams(lp);
        }
        else if (layout.getLayoutParams() instanceof  RelativeLayout.LayoutParams){
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                lp.getRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            layout.setLayoutParams(lp);
        }
        layout.setBackgroundColor(Color.TRANSPARENT);
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
        snackbar.show();
    }

    private OnClickListener onClickListener;
    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener{
        void onClick(Bundle data);
    }
}
