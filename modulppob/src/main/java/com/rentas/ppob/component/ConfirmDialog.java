package com.rentas.ppob.component;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.rentas.ppob.R;
import com.rentas.ppob.master.MyDialog;

/**
 * Created by Mochamad Rezza Gumilang on 04/Oct/2021.
 * Class Info :
 */

public class ConfirmDialog extends MyDialog {

    private CardView card_body_00;
    private TextView txvw_title_00,txvw_desc_00, txvw_no,txvw_yes;

    public enum ACTION {YES,NO};

    public ConfirmDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.component_dialog_confirm;
    }

    @Override
    protected void initLayout(View view) {
        card_body_00 = view.findViewById(R.id.card_body_00);
        txvw_title_00 = view.findViewById(R.id.txvw_title_00);
        txvw_desc_00 = view.findViewById(R.id.txvw_desc_00);
        txvw_no = view.findViewById(R.id.txvw_no);
        txvw_yes = view.findViewById(R.id.txvw_yes);
        card_body_00.setVisibility(View.INVISIBLE);

        view.findViewById(R.id.mrly_no).setOnClickListener(v -> {
            if (onCloseLister != null){
                onCloseLister.onClose(ACTION.NO);
            }
            dismiss();
        });
        view.findViewById(R.id.mrly_yes).setOnClickListener(v -> {
            if (onCloseLister != null){
                onCloseLister.onClose(ACTION.YES);
            }
            dismiss();
        });
    }

    @Override
    public void show() {
        if(mActivity.isFinishing()) {
          return;
        }
        super.show();
        card_body_00.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.zoom_in));
        card_body_00.setVisibility(View.VISIBLE);
    }


    public void showInfo(String title, String description){
        txvw_title_00.setText(title);
        txvw_desc_00.setText(description);
        show();
    }

    public void setAction(String yes, String no){
        txvw_no.setText(no);
        txvw_yes.setText(yes);
    }


    private OnCloseLister onCloseLister;
    public void setOnCloseLister(OnCloseLister onCloseLister){
        this.onCloseLister = onCloseLister;
    }
    public interface OnCloseLister{
        void onClose(ACTION action);
    }

}
