package com.rentas.ppob;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.contact.ContactFragment;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.home.BottomMenuView;
import com.rentas.ppob.home.HomeFragment;
import com.rentas.ppob.libs.PermissionChecker;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;
import com.rentas.ppob.report.trans.ListTransFragment;
import com.rentas.ppob.settings.pin.UpdatePinActivity;
import com.rentas.ppob.settings.profile.MainSettingFragment;

public class MainPpobActivity extends MyActivity {

    private BottomMenuView menu_bottom;
    private FrameLayout frame_body_00;

    @Override
    protected int setLayout() {
        return R.layout.activity_main_ppob;
    }

    @Override
    protected void initLayout() {
        frame_body_00 = findViewById(R.id.frame_body_00);
        menu_bottom = findViewById(R.id.menu_bottom);
        menu_bottom.create();

        registerReceiver(receiver, new IntentFilter("LOAD_INVOICE"));
    }

    @Override
    protected void initData() {
        PermissionChecker checker = new PermissionChecker(mActivity);
        checker.checkLocation();

        validAccess();

    }

    @Override
    protected void initListener() {
        menu_bottom.setOnSelectedMenuListener(this::selectedMenu);
    }



    private void validAccess(){
        checkPinStatus();
        selectedMenu(1);
        initFCMToken();
        menu_bottom.setSelected(1);
    }

    private void selectedMenu(int menu){
        Fragment fragment ;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (menu){
            case 2:
                fragment = ListTransFragment.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"main");
                break;
            case 3:
                fragment = ContactFragment.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"main");
                break;
            case 4:
                fragment = MainSettingFragment.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"main");
                break;
            default:
                fragment = HomeFragment.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"main");
                break;
        }
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void initFCMToken(){
        mAgentId = MainData.getAgentID(mActivity);
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }
            String token = task.getResult();
            Log.d(TAG, "FCM TOKEN : "+ token);
            if (!MainData.getFcmToken(mActivity).equals(token)){
                updateFirebaseToken(token);
            }
        });
    }

    private void updateFirebaseToken(String token){
        PostManager post  = new PostManager(mActivity,ConfigAPI.POST_UPDATE_FCM);
        post.addParam("agent_id",mAgentId);
        post.addParam("fcm_token",token);
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                MainData.setFcmToken(mActivity, token);
                Utility.showToastSuccess(mActivity,"Data updated");
            }
            else {
                Utility.showToastError(mActivity,"Data failed to update");
            }
        });
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LOAD_INVOICE")){
                selectedMenu(2);
                menu_bottom.setSelected(2);
            }
        }
    };

    private void checkPinStatus(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_CHECK_PIN);
        post.addParam("agent_id", mAgentId);
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (message.toLowerCase().contains("hasn't set the pin")){
                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showError("Pengaturan PIN","Anda belum memilki PIN.\nBuat PIN baru sekarang");
                dialog.setAction("Buat PIN");
                dialog.setOnCloseLister(action -> startActivity(new Intent(mActivity, UpdatePinActivity.class)));
            }
        });
    }

}