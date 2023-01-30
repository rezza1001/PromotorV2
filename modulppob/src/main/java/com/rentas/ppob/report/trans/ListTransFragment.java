package com.rentas.ppob.report.trans;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyFragment;
import com.rentas.ppob.report.receipt.ReceiptActivity;
import com.rentas.ppob.report.receipt.ReceiptFailedActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ListTransFragment extends MyFragment {

    private RelativeLayout rvly_date;
    private TextView txvw_date;
    ArrayList<Bundle> mListData = new ArrayList<>();
    HistoryAdapter adapter;

    private final Calendar calStart = Calendar.getInstance();
    private final Calendar calEnd = Calendar.getInstance();

    public static ListTransFragment newInstance() {

        Bundle args = new Bundle();

        ListTransFragment fragment = new ListTransFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int setlayout() {
        return R.layout.report_trans_frg_main;
    }

    @Override
    protected void initLayout(View view) {
        HeaderView header_view = view.findViewById(R.id.header_view);
        header_view.create("Histori Transaksi");
        header_view.disableLine();
        header_view.hideBack();

        rvly_date = view.findViewById(R.id.rvly_date);
        txvw_date = view.findViewById(R.id.txvw_date);
        RecyclerView rcvw_data = view.findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new HistoryAdapter(mActivity,mListData );
        rcvw_data.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        rvly_date.setOnClickListener(v -> openDate());

        adapter.setOnSelectedListener((data, position) -> {
            if (data.getString("status").equalsIgnoreCase("success")){
                Intent intent = new Intent(mActivity, ReceiptActivity.class);
                intent.putExtra("invoice", data.getString("invoice_detail_id"));
                intent.putExtra("inv_number", data.getString("invoice"));
                startActivity(intent);
            }
            else if (data.getString("status").equalsIgnoreCase("process")){
                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showInfo("Struk Tidak Tersedia","Struk tidak bisa di tampilkan karena transaksi anda sedang di proses");
            }
            else if (data.getString("status").equalsIgnoreCase("refund")){
                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showInfo("Struk Tidak Tersedia","Struk tidak bisa di tampilkan karena transaksi anda sudah di batalkan");
            }
            else {
                Intent intent = new Intent(mActivity, ReceiptFailedActivity.class);
                intent.putExtra("invoice", data.getString("invoice_detail_id"));
                intent.putExtra("inv_number", data.getString("invoice"));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        calStart.add(Calendar.DAY_OF_MONTH,-30);
        String sDate ="Period "+Utility.getDateString(calStart.getTime(),"dd/MM/yyyy") +" - "+Utility.getDateString(calEnd.getTime(),"dd/MM/yyyy");
        txvw_date.setText(sDate);

        loadData();
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {

    }

    private void openDate(){
        Pair<Long, Long> select = new Pair<>(calStart.getTimeInMillis(), calEnd.getTimeInMillis());

        MaterialDatePicker.todayInUtcMilliseconds();
        MaterialDatePicker.Builder<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker();
        datePicker.setTitleText("Pilih Rentang Tanggal");
        datePicker.setSelection(select);
        MaterialDatePicker<Pair<Long, Long>> picker = datePicker.build();
        assert getFragmentManager() != null;
        picker.show(getFragmentManager(),picker.toString());
        picker.addOnPositiveButtonClickListener(selection -> {
            calStart.setTimeInMillis(selection.first);
            calEnd.setTimeInMillis(selection.second);

            String sDate ="Period "+Utility.getDateString(calStart.getTime(),"dd/MM/yyyy") +" - "+Utility.getDateString(calEnd.getTime(),"dd/MM/yyyy");
            txvw_date.setText(sDate);

            loadData();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadData(){
        mListData.clear();
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_LIST_INVOICE);
        post.addParam("agent_id", mAgentId);
        post.addParam("start", Utility.getDateString(calStart.getTime(),"yyyy-MM-dd"));
        post.addParam("end", Utility.getDateString(calEnd.getTime(),"yyyy-MM-dd"));
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONArray ja = obj.getJSONArray("data");
                    for (int i=0; i< ja.length(); i++){
                        JSONObject jo = ja.getJSONObject(i);

                        Bundle bundle = new Bundle();
                        bundle.putString("invoice",jo.getString("invoice_number"));
                        bundle.putString("customer_id","-");

                        bundle.putString("product", jo.getString("product_name"));
                        if (jo.getString("product_name").toUpperCase().contains("DATA")){
                            bundle.putString("category","Pembelian Paket Data");
                        } else if (jo.getString("product_name").toUpperCase().contains("PLN")) {
                            bundle.putString("category","Pembayaran PLN");
                        } else if (jo.getString("product_name").toUpperCase().contains("BPJS")) {
                            bundle.putString("category","Pembayaran BPJS");
                        }
                        else if (jo.getString("product_name").toUpperCase().contains("MULTI")) {
                            bundle.putString("category","Pembayaran Multifinance");
                        }
                        else if (jo.getString("product_name").toUpperCase().contains("TELKOM")) {
                            bundle.putString("category","Pembayaran Telkom");
                        } else {
                            bundle.putString("category", "Pembelian Pulsa");
                        }
                        bundle.putString("invoice_detail_id", jo.getString("invoice_detail_id"));
                        bundle.putString("price", MyCurrency.toCurrnecy("Rp", jo.getLong("total")));
                        bundle.putString("status", jo.getString("status"));

                        DateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm",new Locale("id"));

                        Date date = Utility.getDate(jo.getString("created_at"));
                        bundle.putString("date", formatOut.format(date));

                        mListData.add(bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Utility.showFailedDialog(mActivity, message);
            }
            adapter.notifyDataSetChanged();
        });
    }
}
