package com.rentas.ppob.settings.pin;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;

public class ResetPinActivity extends MyActivity {
    private HeaderView header_view;
    private EditText edtx_mail;
    private TextView txvw_alert;

    @Override
    protected int setLayout() {
        return R.layout.setting_pin_activity_reset;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        txvw_alert = findViewById(R.id.txvw_alert);
        header_view.create("Reset PIN");

        RelativeLayout rvly_newPin = findViewById(R.id.rvly_newPin);
        rvly_newPin.setBackground(Utility.getShapeLine(mActivity,1,6, Color.parseColor("#062C56"), Color.WHITE));

        TextView txvw_alert = findViewById(R.id.txvw_alert);
        txvw_alert.setBackground(Utility.getRectBackground(Color.parseColor("#FFB8B8"),Utility.dpToPx(mActivity, 6) ));

        edtx_mail = findViewById(R.id.edtx_mail);

    }

    @Override
    protected void initData() {
        txvw_alert.setVisibility(View.INVISIBLE);
        txvw_alert.setText("");
    }

    @Override
    protected void initListener() {
        header_view.setOnBackListener(this::onBackPressed);
    }

    private void showError(String message){

        if (txvw_alert.getVisibility() == View.INVISIBLE && !txvw_alert.getText().equals(message)){
            txvw_alert.setText(message);
            txvw_alert.setVisibility(View.VISIBLE);
            txvw_alert.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.zoom_in));
            new Handler().postDelayed(() -> {
                txvw_alert.clearAnimation();
                txvw_alert.setText("");
                txvw_alert.setVisibility(View.INVISIBLE);
            },5000);
        }
    }

    public void save(View v){
        String email = edtx_mail.getText().toString().trim();

        if (email.isEmpty()){
            showError("Silahkan masukan Email anda terlebih dahulu");
            return;
        }

        PostManager post = new PostManager(mActivity, ConfigAPI.POST_RESET_PIN);
        post.addParam("email",email);
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                MainData.setPIN(mActivity,"");

                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showSuccess("Reset PIN","Silahkan cek email "+email+" dan lakukan reset pin terlebih dahulu.");
                dialog.setOnCloseLister(action -> mActivity.finish());
            }
            else {
                showError("Email yang anda masukan tidak terdaftar di sistem PPOB. Silahkan cek kembali email anda.\n("+message+")");
            }
        });
    }
}
