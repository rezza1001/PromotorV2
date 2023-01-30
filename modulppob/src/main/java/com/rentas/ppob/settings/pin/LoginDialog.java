package com.rentas.ppob.settings.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.rentas.ppob.ApplicationPpob;
import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.MyDevice;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginDialog extends MyDialog {

    private LinearLayout lnly_node,lnly_body;
    private RelativeLayout rvly_load;
    private TextView txvw_error,txvw_forgot;
    private MaterialRippleLayout mrly_fingerprint;
    private StringBuffer mPin = new StringBuffer();
    private MyDevice device;
    private boolean isLoginFirst = false;

    private CancellationSignal cancellationSignal = null;
    private BiometricPrompt.AuthenticationCallback authenticationCallback;

    public LoginDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.setting_pin_dialog_login;
    }

    @Override
    protected void initLayout(View view) {
        create(view);
        lnly_node = findViewById(R.id.lnly_node);
        lnly_body = findViewById(R.id.lnly_body);
        lnly_body.setVisibility(View.INVISIBLE);
        txvw_error = findViewById(R.id.txvw_error);
        txvw_error.setVisibility(View.INVISIBLE);
        txvw_forgot = findViewById(R.id.txvw_forgot);
        rvly_load = view.findViewById(R.id.rvly_load);

        mrly_fingerprint = view.findViewById(R.id.mrly_fingerprint);
        mrly_fingerprint.setVisibility(View.GONE);
        if (Utility.IsSupportFingerPrint(mActivity)){
            mrly_fingerprint.setVisibility(View.VISIBLE);
        }

        initFingerPrint();

        mrly_fingerprint.setOnClickListener(v -> {
            if (!Utility.checkBiometricSupport(mActivity)){
                return;
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                BiometricPrompt   biometricPrompt = new BiometricPrompt
                    .Builder(mActivity)
                    .setTitle("Fingerprint")
                    .setDescription("Melanjutkan transaksi dengan Sidik jari")
                    .setNegativeButton("Batal", mActivity.getMainExecutor(), (dialogInterface, i) -> Log.d("LoginDialog","Authentication Cancelled")).build();
                biometricPrompt.authenticate(getCancellationSignal(),  mActivity.getMainExecutor(),  authenticationCallback);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        device = new MyDevice(mActivity);
        lnly_body.setVisibility(View.VISIBLE);
        lnly_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));

        txvw_forgot.setOnClickListener(v -> {
            mActivity.startActivity(new Intent(mActivity, ResetPinActivity.class));
            onPinValidListener.onBack();
            dismiss();
        });
    }

    public void setLoginFirst(boolean loginFirst) {
        isLoginFirst = loginFirst;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void create(View view){
        RecyclerView rcvw_keypad = view.findViewById(R.id.rcvw_keypad);
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
                if (onPinValidListener != null){
                    onPinValidListener.onBack();
                    dismiss();
                }
            }
        });
    }

    private Bundle addKey(String value, int num){
        Bundle bundle = new Bundle();
        bundle.putString("value",value);
        bundle.putInt("number",num);
        return bundle;
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

    @SuppressLint("SetTextI18n")
    private void checkPin(){
        if (!isLoginFirst){
            String pin = MainData.getPIN(mActivity);
            if (pin.equals(mPin.toString())){
                if (onPinValidListener != null){
                    onPinValidListener.onValid(mPin.toString());
                    dismiss();
                }
            }
            else {
                txvw_error.setText("PIN yang Anda masukan salah !!");
                txvw_error.setVisibility(View.VISIBLE);
                txvw_error.clearAnimation();
                txvw_error.startAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.zoom_in));
                clearPin();

                if (ApplicationPpob.LastFailedPin == 0){
                    ApplicationPpob.LastFailedPin = System.currentTimeMillis();
                }
                long dif  = System.currentTimeMillis() - ApplicationPpob.LastFailedPin;

                if (dif > 3600000){
                    ApplicationPpob.FailedPin = 0;
                }
                Log.d("TAGRZ","dif "+dif+" | "+ApplicationPpob.LastFailedPin);
                ApplicationPpob.FailedPin = ApplicationPpob.FailedPin +1;
                if (ApplicationPpob.FailedPin == 3){
                    blockUser();
                }
            }
            return;
        }
        rvly_load.setVisibility(View.VISIBLE);
        PostManager post = new PostManager(mActivity, ConfigAPI.POTS_LOGIN);
        post.addParam("agent_code", MainData.getAgentCode(mActivity));
        post.addParam("pin", mPin.toString());
        post.addParam("device_id", device.getDeviceID());
        post.showloading(false);
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            rvly_load.setVisibility(View.GONE);
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    MainData.setAccessToken(mActivity, data.getString("access_token"));
                    MainData.setPIN(mActivity, mPin.toString());
                    if (onPinValidListener != null){
                        onPinValidListener.onValid(mPin.toString());
                        dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                if (ApplicationPpob.LastFailedPin == 0){
                    ApplicationPpob.LastFailedPin = System.currentTimeMillis();
                }
                long dif  = System.currentTimeMillis() - ApplicationPpob.LastFailedPin;

                if (dif > 3600000){
                    ApplicationPpob.FailedPin = 0;
                }

                ApplicationPpob.FailedPin = ApplicationPpob.FailedPin +1;
                Log.d("TAGRZ","dif "+dif+" | "+ApplicationPpob.FailedPin);
                if (ApplicationPpob.FailedPin == 3){
                    blockUser();
                }

                txvw_error.setText(message);
                txvw_error.setVisibility(View.VISIBLE);
                txvw_error.clearAnimation();
                txvw_error.startAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.zoom_in));
                clearPin();


            }
        });
    }

    private void initFingerPrint(){
        if (!ApplicationPpob.fingerPrintActive){
            mrly_fingerprint.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Utility.showFailedDialog(mActivity,"Gagal "+errString);
                }

                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    mPin.append(MainData.getPIN(mActivity));
                    checkPin();
                }
            };
        }
    }
    private CancellationSignal getCancellationSignal() {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(() -> Log.d("LoginDialog","Authentication was Cancelled by the user"));
        return cancellationSignal;
    }

    private void blockUser(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_BLOCK_AGENT);
        post.addParam("agent_code", MainData.getAgentCode(mActivity));
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {


            if (code == ErrorCode.OK){
                MainData.clear(mActivity);
                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showInfo("Akun di Blokir","Akun anda telah di blokir karena salah memasukan pin 3x. Silahkan Hubungi admin untuk mengaktifkan kembali");
                dialog.setOnCloseLister(action -> {
                    mActivity.finish();
                    mActivity.finishAffinity();

                });
            }
            else {
                Utility.showFailedDialog(mActivity,"Failed Block");
            }
        });
    }


    private OnPinValidListener onPinValidListener;
    public void setOnPinValidListener(OnPinValidListener onPinValidListener){
        this.onPinValidListener = onPinValidListener;
    }
    public interface OnPinValidListener{
        void onValid(String pin);
        void onBack();
    }
}
