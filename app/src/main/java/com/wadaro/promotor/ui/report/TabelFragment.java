package com.wadaro.promotor.ui.report;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class TabelFragment extends MyFragment {

    private ArrayList<DemoHolder> allData = new ArrayList<>();
    private DemoAdapter mAdapkter;
    private TextView txvw_size_00;
    private String dateTmp = "";

    @Override
    protected int setlayout() {
        return R.layout.report_fragment_tabel;
    }

    @Override
    protected void initLayout(View view) {

        RecyclerView rcvw_data_00 = view.findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapkter = new DemoAdapter(mActivity, allData);
        rcvw_data_00.setAdapter(mAdapkter);
        txvw_size_00 = view.findViewById(R.id.txvw_size_00);

    }

    @Override
    protected void initListener() {
        mAdapkter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, DetailActivity.class);
            intent.putExtra("booking_id", data.id);
            intent.putExtra("sales_id", data.salesId);
            mActivity.startActivity(intent);
        });
    }

    @Override
    protected void initData() {;
    }

    @SuppressLint("SetTextI18n")
    private void loadData(String date){
        if (date != null){
            dateTmp = date;
        }
        JSONArray dataMaps = new JSONArray();

        PostManager post = new PostManager(mActivity,Config.GET_HASIL_DEMO);
        post.addParam(new ObjectApi("booking_date",dateTmp));
        post.addParam(new ObjectApi("booking_demo","Semua"));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            allData.clear();
            if (code == ErrorCode.OK){
                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    DateFormat dfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    JSONArray booking = obj.getJSONArray("data");
                    for (int i=0; i<booking.length(); i++){
                        JSONObject objData = booking.getJSONObject(i);
                        DemoHolder demo = new DemoHolder();
                        demo.id = objData.getString("booking_id");
                        demo.demo = objData.getString("booking_demo");
                        demo.coordinator = objData.getString("coordinator_name");
                        demo.address = objData.getString("coordinator_address");
                        Date bookDate = df.parse(objData.getString("booking_date"));
                        if (bookDate != null){
                            demo.time = dfTime.format(bookDate);
                        }
                        demo.note   = objData.getString("coordinator_note");
                        demo.status = objData.getString("booking_status");
                        if (demo.status.equalsIgnoreCase("Sales Order") && !objData.isNull("sales_status")){
                            demo.status = objData.getString("sales_status");
                        }
                        if (!objData.isNull("sales_id")){
                            demo.salesId = objData.getString("sales_id");
                        }
                        allData.add(demo);

                        if (objData.getString("coordinator_location").split(",").length> 1){
                            String latitude = objData.getString("coordinator_location").split(",")[0];
                            String longitude = objData.getString("coordinator_location").split(",")[1];
                            JSONObject joMap = new JSONObject();
                            joMap.put("latitude",latitude);
                            joMap.put("longitude",longitude);
                            joMap.put("number",i);
                            joMap.put("booking_status",objData.getString("booking_status"));
                            joMap.put("booking_demo",objData.getString("booking_demo"));
                            joMap.put("booking_id",objData.getString("booking_id"));
                            joMap.put("company_name",objData.getString("company_name"));
                            dataMaps.put(joMap);
                        }
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
            mAdapkter.notifyDataSetChanged();
            txvw_size_00.setText("Total ada "+allData.size()+" titik demo");

            if (dataMaps.length() > 0){
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent("LOAD_DATA");
                    intent.putExtra("data", dataMaps.toString());
                    Objects.requireNonNull(getActivity()).sendBroadcast(intent);
                },1000);

            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("REFRESH");
        intentFilter.addAction("FINISH");
        mActivity.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mActivity.unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "REFRESH")){
                loadData(intent.getStringExtra("date"));
            }
        }
    };

}
