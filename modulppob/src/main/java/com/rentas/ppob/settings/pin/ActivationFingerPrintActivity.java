package com.rentas.ppob.settings.pin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.ApplicationPpob;
import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.data.FingerprintFB;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.MyDevice;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;

import java.util.ArrayList;

public class ActivationFingerPrintActivity extends MyActivity {

    private TextView txvw_error,txvw_forgot;
    LinearLayout lnly_node;
    private MyDevice device;

    private StringBuffer mPin = new StringBuffer();

    @Override
    protected int setLayout() {
        return R.layout.setting_pin_activity_activation_finger;
    }

    @Override
    protected void initLayout() {
        txvw_error = findViewById(R.id.txvw_error);
        txvw_error.setVisibility(View.INVISIBLE);
        txvw_forgot = findViewById(R.id.txvw_forgot);
        lnly_node = findViewById(R.id.lnly_node);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        device = new MyDevice(mActivity);
        RecyclerView rcvw_keypad = findViewById(R.id.rcvw_keypad);
        rcvw_keypad.setLayoutManager(new GridLayoutManager(mActivity,3));
        ArrayList<Bundle> listKeyPad = new ArrayList<>();
        KeyPadAdapter adapter = new KeyPadAdapter(mActivity, listKeyPad);
        rcvw_keypad.setAdapter(adapter);
        for (int i=1; i<=9; i++){
            listKeyPad.add(addKey(i+"",i));
        }
        listKeyPad.add(addKey("Batal",-1));
        listKeyPad.add(addKey("0",0));
        listKeyPad.add(addKey("Hapus",-2));
        adapter.notifyDataSetChanged();

        adapter.setOnSelectedListener((value, number) -> {
            if (txvw_error.getVisibility() == View.VISIBLE){
                txvw_error.setVisibility(View.INVISIBLE);
            }
            if (number >= 0 && mPin.length() <6){
                mPin.append(number);
                selectNode(mPin.length());
                if (mPin.length() == 6){
                    checkPin();
                }
            }
            else if (number == -2 && mPin.length() >0){
                removeNode(mPin.length());
                mPin.deleteCharAt(mPin.length()-1);
            }
            else if (number == -1){
               mActivity.finish();
            }
        });
    }

    @Override
    protected void initListener() {
        findViewById(R.id.mrly_back).setOnClickListener(v -> onBackPressed());

        txvw_forgot.setOnClickListener(v -> {
            mActivity.startActivity(new Intent(mActivity, ResetPinActivity.class));
            mActivity.finish();
        });
    }

    private void selectNode(int position){
        for (int i=0; i<lnly_node.getChildCount(); i++){
            ImageView imageView = (ImageView) lnly_node.getChildAt(i);
            if (position == i+1){
                imageView.setImageResource(R.drawable.pin_selected);
            }
        }
    }
    private void removeNode(int position){
        for (int i=0; i<lnly_node.getChildCount(); i++){
            ImageView imageView = (ImageView) lnly_node.getChildAt(i);
            if (position == i+1){
                imageView.setImageResource(R.drawable.pin_unselected);
            }
        }
    }
    private void clearPin(){
        mPin = new StringBuffer();
        for (int i=0; i<lnly_node.getChildCount(); i++){
            ImageView imageView = (ImageView) lnly_node.getChildAt(i);
            imageView.setImageResource(R.drawable.pin_unselected);
        }
    }

    private Bundle addKey(String value, int num){
        Bundle bundle = new Bundle();
        bundle.putString("value",value);
        bundle.putInt("number",num);
        return bundle;
    }

    @SuppressLint("SetTextI18n")
    private void checkPin(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POTS_LOGIN);
        post.addParam("agent_code", MainData.getAgentCode(mActivity));
        post.addParam("pin", mPin.toString());
        post.addParam("device_id", device.getDeviceID());
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                save();
            }
            else {
                txvw_error.setText("PIN yang Anda masukan salah !!");
                txvw_error.setVisibility(View.VISIBLE);
                txvw_error.clearAnimation();
                txvw_error.startAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.zoom_in));
                clearPin();
            }
        });
    }
    private void save(){
        FingerprintFB fingerprintFB = new FingerprintFB(mActivity);
        fingerprintFB.isActive = true;
        fingerprintFB.agent_code = MainData.getAgentCode(mActivity);
        fingerprintFB.insert();

        ApplicationPpob.fingerPrintActive = true;

        Utility.showToastSuccess(mActivity,"Fingerprint telah aktive");
        new Handler().postDelayed(() -> mActivity.finish(),500);
    }
}
