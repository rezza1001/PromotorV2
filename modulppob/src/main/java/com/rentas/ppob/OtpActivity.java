package com.rentas.ppob;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.data.ActivationData;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.MyDevice;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;
import com.rentas.ppob.settings.pin.ActivationActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpActivity extends MyActivity {

    private TextView txvw_count,txvw_sendTye,txvw_number;
    private MaterialRippleLayout mrly_resend;
    private TextView txvw_resend,txvw_error;
    private EditText edtx_otp;
    private boolean sending = false;
    private MyDevice device;
    private ActivationData activationData;

    @Override
    protected int setLayout() {
        return R.layout.activity_otp;
    }

    @Override
    protected void initLayout() {
        txvw_count = findViewById(R.id.txvw_count);
        mrly_resend = findViewById(R.id.mrly_resend);
        txvw_error = findViewById(R.id.txvw_error);
        edtx_otp = findViewById(R.id.edtx_otp);
        txvw_sendTye = findViewById(R.id.txvw_sendTye);
        txvw_number = findViewById(R.id.txvw_number);

        txvw_resend = (TextView) mrly_resend.getChildAt(0);
        txvw_error.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void initData() {
        startTimer();
        device = new MyDevice(mActivity);
        activationData = (ActivationData) getIntent().getSerializableExtra("data");
        String typeDesc = "Kode OTP telah dikirm melaui X1";
        typeDesc = typeDesc.replace("X1", activationData.getOtpType().equals("phone") ? "WhatsApp":"Email");
        txvw_sendTye.setText(typeDesc);
        txvw_number.setText(activationData.getOtpType().equals("phone") ? activationData.getPhone() : activationData.getEmail());
    }

    @Override
    protected void initListener() {
        mrly_resend.setOnClickListener(v -> resendOTP());
        findViewById(R.id.mrly_back).setOnClickListener(v -> onBackPressed());
        edtx_otp.addTextChangedListener(textWatcher);

    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == 6){
                edtx_otp.removeTextChangedListener(textWatcher);
                sendOTP();
            }
        }
    };

    private void resendOTP(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_REGISTER);
        post.addParam("phone",activationData.getPhone());
        post.addParam("email",activationData.getEmail());
        post.addParam("pin",activationData.getPin());
        post.addParam("agent_code",activationData.getAgentCode());
        post.addParam("partner_code",activationData.getPartnerCode());
        post.addParam("agent_name",activationData.getName());
        post.addParam("otp_type",activationData.getOtpType());
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            Utility.SendLogLogin("[RESEND] "+ConfigAPI.POST_REGISTER,post.getData().toString(),obj.toString(),activationData.getAgentCode());
            if (code == ErrorCode.OK){
                startTimer();
            }
            else {
                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showError("Gagal",message);
            }
        });
    }

    private void startTimer(){
        if (sending){
            return;
        }
        sending = true;
        txvw_resend.setTextColor(Color.parseColor("#33000000"));
        new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                long ms = millisUntilFinished / 1000;
                runOnUiThread(() -> {
                    int minute = (int) (ms/60);
                    String sMin = "0"+minute;
                    long sec = ms;
                    if (sec > 60){
                        sec = sec -60;
                    }
                    String sSec = sec >= 10 ? sec+"" : "0"+sec;

                    String value = sMin +" : "+ sSec;
                    txvw_count.setText(value);
                });
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                sending= false;
                txvw_count.setText("00:00");
                txvw_resend.setTextColor(Color.parseColor("#26B037"));
            }
        }.start();
    }

    private void sendOTP(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_OTP_VALIDATION);
        post.addParam("agent_code", activationData.getAgentCode());
        post.addParam("otp",Long.parseLong(edtx_otp.getText().toString()));
        post.addParam("pin",activationData.getPin());
        post.addParam("device_id",device.getDeviceID());
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            Utility.SendLogLogin(ConfigAPI.POST_OTP_VALIDATION,post.getData().toString(),obj.toString(),activationData.getAgentCode());
            edtx_otp.addTextChangedListener(textWatcher);
            if (code == ErrorCode.OK){
                ApplicationPpob.FailedPin = 0;
                MainData.setPIN(mActivity, activationData.getPin());
                MainData.setEmail(mActivity, activationData.getEmail());
                MainData.setPhone(mActivity, activationData.getPhone());
                MainData.setAgentCode(mActivity,activationData.getAgentCode());
                login();
            }
            else {
                showError(message);

                if (ApplicationPpob.LastFailedPin == 0){
                    ApplicationPpob.LastFailedPin = System.currentTimeMillis();
                }
                long dif  = System.currentTimeMillis() - ApplicationPpob.LastFailedPin;

                if (dif > 3600000){
                    ApplicationPpob.FailedPin = 0;
                }
                Log.d("TAGRZ","dif "+dif+" | "+ApplicationPpob.LastFailedPin);
                ApplicationPpob.FailedPin = ApplicationPpob.FailedPin +1;
                if (ApplicationPpob.FailedPin == 5){
                    ApplicationPpob.FailedPin = 0;
                    blockUser();
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showError(String message){
        edtx_otp.setText(null);
        txvw_error.setText("Invalid OTP. "+message);
        txvw_error.setVisibility(View.VISIBLE);
        txvw_error.clearAnimation();
        txvw_error.startAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.zoom_in));
        new Handler().postDelayed(() -> txvw_error.setVisibility(View.INVISIBLE),5000);
    }


    private void checkAgentStatus(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_CHECK_AGENT_STATUS);
        post.addParam("agent_code",activationData.getAgentCode());
        post.addParam("device_id",device.getDeviceID());
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            Utility.SendLogLogin(ConfigAPI.POST_CHECK_AGENT_STATUS,post.getData().toString(),obj.toString(),activationData.getAgentCode());
            if (code == ErrorCode.OK){
                try {
                    if (obj.getString("status").equals("failed")){
                        Intent intent = getIntent();
                        intent.setClass(mActivity, ActivationActivity.class);
                        startActivity(intent);
                    }
                    else {
                        JSONObject data = obj.getJSONObject("data");
                        MainData.setPhone(mActivity, data.getString("phone"));
                        MainData.setEmail(mActivity, data.getString("email"));
                        MainData.setAgentId(mActivity, data.getInt("agent_id"));

                        sendBroadcast(new Intent("OTP_VALID"));
                        Utility.showToastSuccess(mActivity,"Aktivasi berhasil");
                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(mActivity, MainPpobActivity.class));
                            mActivity.finish();
                        },200);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                DynamicDialog dialog = new DynamicDialog(mActivity);
                message = "Invalid status agent";
                try {
                    message = obj.getString("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.showError("Gagal",message);
            }
        });
    }

    private void login(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POTS_LOGIN);
        post.addParam("agent_code", activationData.getAgentCode());
        post.addParam("pin", activationData.getPin());
        post.addParam("device_id", device.getDeviceID());
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            Utility.SendLogLogin(ConfigAPI.POTS_LOGIN,post.getData().toString(),obj.toString(),activationData.getAgentCode());
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    MainData.setAccessToken(mActivity, data.getString("access_token"));
                    MainData.setPIN(mActivity, activationData.getPin());
                    checkAgentStatus();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showError("Gagal",message);
            }
        });
    }

    private void blockUser(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_BLOCK_AGENT);
        post.addParam("agent_code", activationData.getAgentCode());
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {

            if (code == ErrorCode.OK){
                MainData.clear(mActivity);
                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showInfo("Akun di Blokir","Akun anda telah di blokir karena salah memasukan OTP 5x. Silahkan Hubungi admin untuk mengaktifkan kembali");
                dialog.setOnCloseLister(action -> {
                    sendBroadcast(new Intent("OTP_VALID"));
                    new Handler().postDelayed(() -> mActivity.finish(),200);
                });
            }
            else {
                Utility.showFailedDialog(mActivity,"Failed Block");
            }
        });
    }
}
