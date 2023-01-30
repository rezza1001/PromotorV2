package com.rentas.ppob.report.trans;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.component.KeyValView;
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;
import com.rentas.ppob.report.receipt.ReceiptActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class TransDetailActivity extends MyActivity {

    private HeaderView header_view;
    private LinearLayout lnly_body,lnly_refresh;
    private CardView card_status;
    private TextView txvw_status,txvw_action;
    private ImageView imvw_status,imvw_action;

    private String invDetailId = "";

    @Override
    protected int setLayout() {
        return R.layout.report_trans_activity_detail;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("Detail Transaksi");

        lnly_body = findViewById(R.id.lnly_body);
        card_status = findViewById(R.id.card_status);
        txvw_status = findViewById(R.id.txvw_status);
        imvw_status = findViewById(R.id.imvw_status);
        imvw_action = findViewById(R.id.imvw_action);
        txvw_action = findViewById(R.id.txvw_action);
        lnly_refresh = findViewById(R.id.lnly_refresh);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        lnly_body.removeAllViews();
        PostManager post = new PostManager(mActivity, ConfigAPI.GET_INVOICE_DETAIL+mAgentId+"/"+getIntent().getStringExtra("inv_number"));
        post.executeGET();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    if (obj.has("data")){
                        obj = obj.getJSONObject("data");
                    }
                    buildData("Invoice", obj.getString("invoice_number"));
                    buildData("Produk Pembelian", obj.getString("product_name"));
                    buildData("Nomor Pelanggan", obj.isNull("customer_number") ? "-" : obj.getString("customer_number"));
                    buildData("Tanggal Transaksi", Utility.getDateString(obj.getString("created_at")));
                    buildData("Diskon", MyCurrency.toCurrnecy("- Rp",obj.getLong("discount")));
                    if (obj.has("price")){
                        JSONObject price = obj.optJSONObject("price");
                        if (price.has("fee_admin")){
                            long feeAdmin = price.getLong("fee_admin");
    //                        long ownerAdmin = price.getLong("owner_margin");
    //                        long partnerMargin = price.getLong("partner_margin");
    //                        long admin = feeAdmin + ownerAdmin + partnerMargin;
                            buildData("Admin", MyCurrency.toCurrnecy("+ Rp",feeAdmin));
                        }

                    }

                    buildData("Total", MyCurrency.toCurrnecy("Rp",obj.getLong("total")));

                    if (obj.has("invoice_detail_id")){
                        invDetailId = obj.getString("invoice_detail_id");
                    }

                    imvw_action.setImageResource(R.drawable.ic_baseline_refresh_24);
                    txvw_action.setText("Refresh");
                    if (obj.getString("status").equals("pending") || obj.getString("status").equals("process")){
                        txvw_status.setText("Status transaksi anda saat ini sedang diproses oleh supplier. Mohon menunggu");
                        card_status.setCardBackgroundColor(getResources().getColor(R.color.pending));
                        imvw_status.setImageResource(R.drawable.ic_baseline_error_24);
                        imvw_status.setColorFilter(Color.parseColor("#A0735A"));
                        txvw_status.setTextColor(Color.parseColor("#A0735A"));
                    }
                    else if (obj.getString("status").equals("success")) {
                        txvw_status.setText("Status transaksi anda Sukses");
                        card_status.setCardBackgroundColor(Color.parseColor("#ACE9C8"));
                        imvw_status.setImageResource(R.drawable.ic_check);
                        imvw_status.setColorFilter(Color.parseColor("#044B25"));
                        txvw_status.setTextColor(Color.parseColor("#044B25"));

                        imvw_action.setImageResource(R.drawable.ic_baseline_receipt_24);
                        txvw_action.setText("Lihat Struk");
                    }
                    else {
                        txvw_status.setText("Status transaksi anda Gagal");
                        card_status.setCardBackgroundColor(Color.parseColor("#FFB6BB"));
                        imvw_status.setImageResource(R.drawable.ic_baseline_error_24);
                        imvw_status.setColorFilter(Color.parseColor("#B72F38"));
                        txvw_status.setTextColor(Color.parseColor("#B72F38"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            else {
                Utility.showToastError(mActivity, message);
                mActivity.finish();
            }
        });
    }

    @Override
    protected void initListener() {
        header_view.setOnBackListener(this::onBackPressed);
        lnly_refresh.setOnClickListener(v -> {
            if (txvw_action.getText().toString().equals("Refresh")){
                initData();
            }
            else {
                Intent intent = new Intent(mActivity, ReceiptActivity.class);
                intent.putExtra("invoice", invDetailId);
                intent.putExtra("inv_number", getIntent().getStringExtra("inv_number"));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void inComingNotif(String title, String body, String data) {
        if (title.contains("Transaksi Sukses")){
            Utility.showToastSuccess(mActivity,"Transaksi Sukses");
            mActivity.finish();
        }
        else {
            initData();
        }
    }

    private void buildData(String title, String value){
        KeyValView keyVal = new KeyValView(mActivity, null);
        keyVal.create(title, value);
        lnly_body.addView(keyVal);
    }
}
