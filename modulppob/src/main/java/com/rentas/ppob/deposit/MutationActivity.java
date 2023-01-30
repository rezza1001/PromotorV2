package com.rentas.ppob.deposit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MutationActivity extends MyActivity {

    private HeaderView header_view;
    ArrayList<Bundle> mutation = new ArrayList<>();
    private MutationAdapter adapter;
    private TextView txvw_date;
    private final Calendar calStart = Calendar.getInstance();
    private final Calendar calEnd = Calendar.getInstance();

    @Override
    protected int setLayout() {
        return R.layout.deposit_activity_mutation;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        txvw_date   = findViewById(R.id.txvw_date);
        header_view.create("Mutasi Saldo");
        header_view.disableLine();

        RecyclerView rcvw_data = findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new MutationAdapter(mActivity, mutation);
        rcvw_data.setAdapter(adapter);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        calStart.add(Calendar.DAY_OF_MONTH,-7);
        calEnd.add(Calendar.DAY_OF_MONTH,7);
        String sDate ="Period "+Utility.getDateString(calStart.getTime(),"dd/MM/yyyy") +" - "+Utility.getDateString(calEnd.getTime(),"dd/MM/yyyy");
        txvw_date.setText(sDate);
        refresh();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.rvly_date).setOnClickListener(v -> openDate());
        header_view.setOnBackListener(this::onBackPressed);
    }

    private void openDate(){
        Pair<Long, Long> select = new Pair<>(calStart.getTimeInMillis(), calEnd.getTimeInMillis());

        MaterialDatePicker.todayInUtcMilliseconds();
       MaterialDatePicker.Builder<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker();
        datePicker.setTitleText("Pilih Rentang Tanggal");
        datePicker.setSelection(select);
        MaterialDatePicker<Pair<Long, Long>> picker = datePicker.build();
        picker.show(getSupportFragmentManager(),picker.toString());
        picker.addOnPositiveButtonClickListener(selection -> {
            calStart.setTimeInMillis(selection.first);
            calEnd.setTimeInMillis(selection.second);

            String sDate ="Period "+Utility.getDateString(calStart.getTime(),"dd/MM/yyyy") +" - "+Utility.getDateString(calEnd.getTime(),"dd/MM/yyyy");
            txvw_date.setText(sDate);

            refresh();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refresh(){
        mutation.clear();
        PostManager post = new PostManager(mActivity, ConfigAPI.POST_BALANCE_MUTATION);
        post.addParam("user_type","agent");
        post.addParam("user_id",mAgentId);
        post.addParam("from",Utility.getDateString(calStart.getTime(),"yyyy-MM-dd"));
        post.addParam("to",Utility.getDateString(calEnd.getTime(),"yyyy-MM-dd"));
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            mutation.clear();
            if (code == ErrorCode.OK){
                try {
                    DateFormat formatI = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id"));
                    DateFormat formatD = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", new Locale("id"));

                    JSONArray ja = obj.getJSONArray("data");
                    for (int i =0; i<ja.length(); i++){
                        JSONObject jo = ja.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("description",jo.getString("description"));
                        bundle.putString("last_balance", MyCurrency.toCurrnecy("Saldo Setelah Rp",jo.getLong("curr_balance")));
                        bundle.putString("current_balance", MyCurrency.toCurrnecy("Saldo Sebelum Rp",jo.getLong("last_balance")));

                        if (!jo.getString("credit").equals("0")){
                            bundle.putString("nominal", MyCurrency.toCurrnecy("+ Rp",jo.getLong("credit")));
                            bundle.putInt("type",1);
                        }
                        else {
                            bundle.putString("nominal", MyCurrency.toCurrnecy("- Rp",jo.getLong("debit")));
                            bundle.putInt("type",2);
                        }

                        Date date = formatI.parse(Utility.getDateString(jo.getString("created_at")));
                        if (date != null){
                            bundle.putString("date", formatD.format(date));
                        }
                        mutation.add(bundle);
                    }

                } catch (JSONException | ParseException e) {
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
