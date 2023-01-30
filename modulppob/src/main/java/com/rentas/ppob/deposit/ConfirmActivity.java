package com.rentas.ppob.deposit;

import android.content.Intent;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.component.KeyValView;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;
import com.rentas.ppob.settings.pin.LoginDialog;

public class ConfirmActivity extends MyActivity {

    private HeaderView header_view;
    private KeyValView txvw_currentCommission,txvw_ussageCommission,txvw_lastCommission,
            txvw_nomianl,txvw_currentBalance,txvw_afterBalance;

    private long currentCommission =0;
    private long topup =0;

    @Override
    protected int setLayout() {
        return R.layout.deposit_activity_confirm;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("Konfirmasi Topup");

        txvw_currentCommission = findViewById(R.id.txvw_currentCommission);
        txvw_ussageCommission = findViewById(R.id.txvw_ussageCommission);
        txvw_ussageCommission.setValueColor(getResources().getColor(R.color.font_orange));
        txvw_lastCommission = findViewById(R.id.txvw_lastCommission);
        txvw_nomianl = findViewById(R.id.txvw_nomianl);
        txvw_nomianl.setValueColor(getResources().getColor(R.color.green));
        txvw_currentBalance = findViewById(R.id.txvw_currentBalance);
        txvw_afterBalance = findViewById(R.id.txvw_afterBalance);
    }

    @Override
    protected void initData() {
        currentCommission = getIntent().getLongExtra("commission",0);
        topup = getIntent().getLongExtra("nominal",0);
        txvw_currentCommission.create("Saldo Komisi", MyCurrency.toCurrnecy(currentCommission));
        txvw_ussageCommission.create("Komisi digunakan", MyCurrency.toCurrnecy("-",topup));
        long lastCommission = currentCommission - topup;
        txvw_lastCommission.create("Sisa Komisi", MyCurrency.toCurrnecy(lastCommission));

        txvw_nomianl.create("Nominal Topup", MyCurrency.toCurrnecy("+ Rp",topup));
        long balance = MainData.getBalance(mActivity);
        long afterBalance = balance + topup;
        txvw_currentBalance.create("Saldo Awal", MyCurrency.toCurrnecy("Rp",balance));
        txvw_afterBalance.createBig("Saldo Akhir", MyCurrency.toCurrnecy("Rp",afterBalance));
    }

    @Override
    protected void initListener() {
        header_view.setOnBackListener(this::onBackPressed);
        findViewById(R.id.lnly_action).setOnClickListener(v -> {
            LoginDialog dialog = new LoginDialog(mActivity);
            dialog.show();
            dialog.setOnPinValidListener(new LoginDialog.OnPinValidListener() {
                @Override
                public void onValid(String pin) {
                    requestTopUp();
                }

                @Override
                public void onBack() {

                }
            });
        });
    }

    private void requestTopUp(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_DEPOSIT);
        post.addParam("agent_code", MainData.getAgentCode(mActivity));
        post.addParam("total", topup);
        post.addParam("description", "Topup Deposit By "+ MainData.getAgentID(mActivity));
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                mActivity.sendBroadcast(new Intent("FINISH"));
                DynamicDialog successDialog = new DynamicDialog(mActivity);
                successDialog.showSuccess("Sukses","Topup Anda berhasil");
                successDialog.setOnCloseLister(action -> mActivity.finish());
            }
            else {
                Utility.showFailedDialog(mActivity, message);
            }
        });

    }
}
