package com.rentas.ppob;

import android.content.Intent;

import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.data.FingerprintFB;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.MyDevice;
import com.rentas.ppob.libs.PermissionChecker;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;
import com.rentas.ppob.settings.pin.ActivationActivity;
import com.rentas.ppob.settings.pin.LoginDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class LauncherPPOBActivity extends MyActivity {
    private MyDevice myDevice;

    @Override
    protected int setLayout() {
        return R.layout.activity_launcher;
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void initData() {
        myDevice = new MyDevice(mActivity);
        checkAgentStatus();

    }

    @Override
    protected void initListener() {

    }

    private void checkAgentStatus(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_CHECK_AGENT_STATUS);
        post.addParam("agent_code",getIntent().getStringExtra("employee_id"));
        post.addParam("device_id",myDevice.getDeviceID());
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
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
                        MainData.setAgentId(mActivity, Integer.parseInt(data.getString("agent_id")));
                        MainData.setAgentCode(mActivity,getIntent().getStringExtra("employee_id"));
                        checkFingerPrint();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Intent intent = getIntent();
                intent.setClass(mActivity, ActivationActivity.class);
                startActivity(intent);
                mActivity.finish();
            }
        });
    }

    private void checkFingerPrint(){
        if (Utility.IsSupportFingerPrint(mActivity)){
            FingerprintFB fingerprintFB = new FingerprintFB(mActivity);
            fingerprintFB.get(MainData.getAgentCode(mActivity));
            fingerprintFB.setOnReadListener(data -> {
                ApplicationPpob.fingerPrintActive = data.isActive;
                checkPin();
            });
        }
        else {
            ApplicationPpob.fingerPrintActive = false;
            checkPin();
        }
    }

    private void checkPin(){
        LoginDialog dialog = new LoginDialog(mActivity);
        dialog.show();
        dialog.setLoginFirst(true);
        dialog.setOnPinValidListener(new LoginDialog.OnPinValidListener() {
            @Override
            public void onValid(String pin) {
                startActivity(new Intent(mActivity, MainPpobActivity.class));
                mActivity.finish();
            }

            @Override
            public void onBack() {
                mActivity.finish();
            }
        });
    }

}
