package com.rentas.ppob.deposit;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.component.KeyValView;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.master.MyActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainDepositActivity extends MyActivity {

    private HeaderView header_view;
    private KeyValView kv_balance,kv_commission;
    private CardView card_empty;

    private NominalAdapter adapter;
    long commission = 0;
    ArrayList<Long> nominal = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.deposit_activity_main;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("Topup Deposit");
        header_view.disableLine();

        kv_balance = findViewById(R.id.kv_balance);
        kv_commission = findViewById(R.id.kv_commission);

        RecyclerView rcvw_nominal = findViewById(R.id.rcvw_nominal);
        rcvw_nominal.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rcvw_nominal.setNestedScrollingEnabled(false);
        adapter = new NominalAdapter(mActivity, nominal);
        rcvw_nominal.setAdapter(adapter);

        card_empty = findViewById(R.id.card_empty);
        card_empty.setVisibility(View.GONE);

        registerReceiver(receiver, new IntentFilter("FINISH"));

    }

    @Override
    protected void initData() {
        kv_balance.create("Saldo PPOB", MyCurrency.toCurrnecy("Rp",MainData.getBalance(mActivity)));
        loadCommission();
    }

    @Override
    protected void initListener() {
        header_view.setOnBackListener(this::onBackPressed);
        adapter.setOnSelectedListener((data, position) -> {
            Intent intent = new Intent(mActivity, ConfirmActivity.class);
            intent.putExtra("nominal", data);
            intent.putExtra("commission", commission);
            startActivity(intent);
        });
    }

    private void loadCommission(){
        PostManager post = new PostManager(mActivity, ConfigAPI.GET_COMMISSION+ MainData.getAgentCode(mActivity));
        post.executeGET();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    commission = data.getLong("curr_commission");
                    kv_commission.create("Saldo Komisi", MyCurrency.toCurrnecy(commission));
                    if (commission < 20000){
                        showInfo();
                        return;
                    }
                    initNominal();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                showInfo();
            }
        });
    }

    private void showInfo(){
        DynamicDialog dialog = new DynamicDialog(mActivity);
        dialog.showInfo("Topup Info", "Anda tidak dapat melakukan top-up karena komisi anda tidak mencukupi.\nUntuk informasi silahkan hubungin Admin");
        dialog.setAction("Mengerti");
        dialog.setOnCloseLister(action -> mActivity.finish());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initNominal(){
        nominal.clear();
        long[] value = {20000,50000,100000,150000,200000,300000,400000,500000,1000000};
        for (long l : value) {
            if (l <= commission) {
                nominal.add(l);
            }
        }
        adapter.notifyDataSetChanged();
        if (nominal.size() == 0){
            card_empty.setVisibility(View.VISIBLE);
        }
        else {
            card_empty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mActivity.finish();
        }
    };
}
