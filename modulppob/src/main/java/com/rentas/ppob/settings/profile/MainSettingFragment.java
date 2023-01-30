package com.rentas.ppob.settings.profile;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rentas.ppob.ApplicationPpob;
import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.ConfirmDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.component.LoadingDialog;
import com.rentas.ppob.data.FingerprintFB;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.database.DatabaseManager;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyFragment;
import com.rentas.ppob.settings.ConfigUrlDialog;
import com.rentas.ppob.settings.pin.ActivationFingerPrintActivity;
import com.rentas.ppob.settings.pin.LoginDialog;
import com.rentas.ppob.settings.pin.UpdatePinActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainSettingFragment extends MyFragment {

    private ItemSettingView stvw_email,stvw_phone,stvw_logout,stvw_pin,stvw_version,stvw_fingerPrint;
    private CircleImageView imvw_profile;

    public static MainSettingFragment newInstance() {
        Bundle args = new Bundle();
        MainSettingFragment fragment = new MainSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int setlayout() {
        return R.layout.setting_profile_frg_main;
    }

    @Override
    protected void initLayout(View view) {
        HeaderView header_view = view.findViewById(R.id.header_view);
        header_view.hideBack();
        header_view.disableLine();
        header_view.setPrimaryColor();
        header_view.create("Pengaturan");

        TextView txvw_name = view.findViewById(R.id.txvw_name);
        txvw_name.setText(MainData.getProfileName(mActivity));

        RelativeLayout rvly_profile = view.findViewById(R.id.rvly_profile);
        rvly_profile.setBackground(Utility.getOvalBackground(Color.WHITE));

        stvw_email = view.findViewById(R.id.stvw_email);
        stvw_email.create(R.drawable.ic_email_24, "Email",MainData.getEmail(mActivity));

        stvw_phone = view.findViewById(R.id.stvw_phone);
        stvw_phone.create(R.drawable.ic_phone, "Nomor Telepon",MainData.getPhone(mActivity));

        stvw_logout = view.findViewById(R.id.stvw_logout);
        stvw_logout.create(R.drawable.ic_logout, "Logout","Keluar dan Reset pengaturan");

        stvw_pin = view.findViewById(R.id.stvw_pin);
        stvw_pin.create(R.drawable.ic_baseline_fiber_pin_24, "PIN Transaksi","Ubah pin transaksi");

        stvw_fingerPrint = view.findViewById(R.id.stvw_fingerPrint);
        stvw_fingerPrint.create(R.drawable.ic_baseline_fingerprint_24, "Fingerprint","Fingerprint OFF");
        if (!Utility.IsSupportFingerPrint(mActivity)){
            stvw_fingerPrint.setVisibility(View.GONE);
        }

        stvw_version = view.findViewById(R.id.stvw_version);
        try {
            PackageInfo pInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
            String version = "ppob v.1.0";
            stvw_version.create(R.drawable.ic_version, "Versi Aplikasi",version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        imvw_profile = view.findViewById(R.id.imvw_profile);
        Glide.with(mActivity).load(MainData.getAvatar(mActivity)).placeholder(R.drawable.image_test_baby).into(imvw_profile);

    }

    @Override
    protected void initListener() {
        stvw_logout.setOnSelectedListener(bundle -> logout(true));

        stvw_version.setOnSelectedListener(bundle -> openDevMode());

        stvw_pin.setOnSelectedListener(bundle -> {
            Intent intent = new Intent(mActivity, UpdatePinActivity.class);
            intent.putExtra("update", true);
            startActivity(intent);
        });
        stvw_fingerPrint.setOnSelectedListener(bundle -> {
            if (ApplicationPpob.fingerPrintActive){
                ConfirmDialog dialog = new ConfirmDialog(mActivity);
                dialog.showInfo("Fingerprint","Anda akan menonaktifkan fingerprint");
                dialog.setAction("YA","Tidak");
                dialog.setOnCloseLister(action -> {
                    if (action == ConfirmDialog.ACTION.YES){
                        deleteFingePrint();
                    }
                });
                return;
            }
            startActivity(new Intent(mActivity, ActivationFingerPrintActivity.class));
        });
    }

    private void deleteFingePrint(){
        LoginDialog dialog = new LoginDialog(mActivity);
        dialog.show();
        dialog.setOnPinValidListener(new LoginDialog.OnPinValidListener() {
            @Override
            public void onValid(String pin) {
                FingerprintFB fingerprintFB = new FingerprintFB(mActivity);
                fingerprintFB.agent_code = MainData.getAgentCode(mActivity);
                fingerprintFB.delete();
                fingerprintFB.setDeleteListener(status -> {
                    ApplicationPpob.fingerPrintActive = false;
                    checkFingerPrint();
                });
            }

            @Override
            public void onBack() {

            }
        });

    }

    int devMode = 0;
    private void openDevMode(){
        devMode ++;
        if (devMode == 6){
            Toast.makeText(mActivity,"Developer Mode", Toast.LENGTH_SHORT).show();
            ConfigUrlDialog urlDialog = new ConfigUrlDialog(mActivity);
            urlDialog.show();
            urlDialog.setOnConfigChangeListener(() -> logout(false));
        }


        new Handler().postDelayed(() -> {
            devMode = 0;
        },4000);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkFingerPrint();
    }

    private void checkFingerPrint(){
        if (ApplicationPpob.fingerPrintActive){
            stvw_fingerPrint.setDescription("Fingerprint ON");
        }
        else {
            stvw_fingerPrint.setDescription("Fingerprint OFF");
        }
    }

    private void logout(boolean clearInternalStorage){

        PostManager postManager = new PostManager(mActivity, ConfigAPI.POST_RESET_DEVICE);
        postManager.addParam("agent_code", MainData.getAgentCode(mActivity));
        postManager.executePOST();
        postManager.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                new Handler().postDelayed(() -> {

                    DatabaseManager databaseManager = new DatabaseManager(mActivity);
                    databaseManager.clearAllData(mActivity);
                    if (clearInternalStorage){
                        MainData.clear(mActivity);
                    }
                    mActivity.finish();
                },500);
            }
            else {
                Utility.showFailedDialog(mActivity, message);
            }
        });


    }

}
