package com.rentas.ppob.settings.pin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.rentas.ppob.R;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyDialog;

public class PinDialog extends MyDialog {

    private LinearLayout lnly_body;
    private TextView txvw_create;

    public PinDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.setting_pin_dialog;
    }

    @Override
    protected void initLayout(View view) {
        lnly_body = findViewById(R.id.lnly_body);
        lnly_body.setBackground(Utility.getRectBackground(Color.WHITE, Utility.dpToPx(mActivity,10)));
        lnly_body.setVisibility(View.INVISIBLE);

        RelativeLayout rvly_icon = findViewById(R.id.rvly_icon);
        rvly_icon.setBackground(Utility.getOvalBackground(Color.parseColor("#F2F2EF")));

        CardView card_create = findViewById(R.id.card_create);
        int primaryC = mActivity.getResources().getColor(R.color.colorPrimary);
        card_create.setBackground(Utility.getShapeLine(mActivity,1,20,primaryC,0));

        TextView txvw_cancel = findViewById(R.id.txvw_cancel);
        txvw_cancel.setOnClickListener(v -> dismiss());

        txvw_create = findViewById(R.id.txvw_create);
    }

    @Override
    public void show() {
        super.show();
        lnly_body.setVisibility(View.VISIBLE);
        lnly_body.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.zoom_in));
        txvw_create.setOnClickListener(v -> {
            mActivity.startActivity(new Intent(mActivity, ActivationActivity.class));
            dismiss();
        });
    }
}
