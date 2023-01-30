package com.rentas.ppob.trans.prepaid;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.component.KeyValView;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;
import com.rentas.ppob.report.trans.TransDetailActivity;
import com.rentas.ppob.settings.pin.CheckDialog;
import com.rentas.ppob.settings.pin.LoginDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PaymentPrepaidActivity extends MyActivity {

    private HeaderView header_view;
    private LinearLayout lnly_detailBuy,lnly_detailPay,lnly_error;
    private KeyValView kv_balance,kv_balanceAfter;
    private long balance = 0;
    private long cutBalance = 0;

    @Override
    protected int setLayout() {
        return R.layout.pulsa_activity_paymentprepaid;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("Detail Transaksi");

        lnly_detailBuy = findViewById(R.id.lnly_detailBuy);
        lnly_detailPay = findViewById(R.id.lnly_detailPay);
        kv_balance = findViewById(R.id.kv_balance);
        lnly_error = findViewById(R.id.lnly_error);
        kv_balanceAfter = findViewById(R.id.kv_balanceAfter);

        lnly_error.setVisibility(View.GONE);
        kv_balanceAfter.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        balance = MainData.getBalance(mActivity);

        lnly_detailBuy.removeAllViews();
        lnly_detailPay.removeAllViews();
        kv_balance.createBig("Saldo Utama",MyCurrency.toCurrnecy(balance));
        String infoString = getIntent().getStringExtra("data");
        try {
            JSONArray jaInfo = new JSONArray(infoString);
            for (int i=0; i<jaInfo.length(); i++){
                JSONObject jo = jaInfo.getJSONObject(i);
                addInfo(jo.getString("key"),jo.getString("value"), lnly_detailBuy);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        long price = getIntent().getLongExtra("price",0);
        addInfo("Harga Jual", MyCurrency.toCurrnecy("Rp",price), lnly_detailPay);
        addInfo("Diskon", "0", lnly_detailPay);
        addInfo("Biaya Admin", "Gratis", lnly_detailPay);

        KeyValView kv = new KeyValView(mActivity, null);
        kv.createBig("Total Pembayaran", MyCurrency.toCurrnecy("Rp",(price)));
        lnly_detailPay.addView(kv);

        if (price > balance){
            lnly_error.setVisibility(View.VISIBLE);
            kv_balanceAfter.setVisibility(View.GONE);
        }
        else {
            lnly_error.setVisibility(View.GONE);
            kv_balanceAfter.setVisibility(View.VISIBLE);
            long balanceAfter = balance - price;
            kv_balanceAfter.create("Saldo Setelah Bayar",MyCurrency.toCurrnecy("Rp",balanceAfter));
        }
        cutBalance = price;
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
                    createInvoice();
                }

                @Override
                public void onBack() {

                }
            });
        });
    }

    private void addInfo(String key, String value, LinearLayout lnly){
        KeyValView kv = new KeyValView(mActivity, null);
        kv.create(key, value);
        lnly.addView(kv);
    }

    private void createInvoice(){
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_INVOICE_PRE);
        post.addParam("agent_id", MainData.getAgentID(mActivity));
        post.addParam("partner_id", MainData.getPartnerId(mActivity));
        post.addParam("agent_code", MainData.getAgentCode(mActivity));
        post.addParam("product_cogs_id", getIntent().getIntExtra("product_cogs_id",0));
        post.addParam("product_id", Integer.parseInt(getIntent().getStringExtra("product_id")));
        post.addParam("cut_balance", cutBalance);
        post.addParam("customer_id", getIntent().getStringExtra("customer_id"));
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    obj = obj.getJSONObject("data");
                    Intent intent = new Intent(mActivity, TransDetailActivity.class);
                    intent.putExtra("inv_number", obj.getString("invoice_number"));
                    startActivity(intent);

                    Utility.showToastSuccess(mActivity,"Transaksi Diproses");
                    mActivity.finish();
                    sendBroadcast(new Intent("FINISH"));
//                    sendBroadcast(new Intent("LOAD_INVOICE"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            else {
                Utility.showFailedDialog(mActivity, message);
            }
        });
    }

}
