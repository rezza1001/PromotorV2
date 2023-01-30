package com.wadaro.promotor.ui.demo.process;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.database.table.TempDB;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.ui.order.InfoView;
import com.wadaro.promotor.util.MySpinnerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ProcessMainActivity extends MyActivity {


    private LinearLayout lnly_info_00;
    private ConsumerAdapter mConsumerAdapter;
    private ArrayList<Bundle> mConsumers = new ArrayList<>();
    private ImageView imvw_coordinator_00,imvw_location_00;
    private MySpinnerView myspn_coordinator_00,myspn_location_00;

    private String mBookingID = "";
    @Override
    protected int setLayout() {
        return R.layout.demo_process_activity_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Proses Demo");

        lnly_info_00 = findViewById(R.id.lnly_info_00);

        RecyclerView rcvw_data_00 = findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        mConsumerAdapter = new ConsumerAdapter(mConsumers);
        rcvw_data_00.setAdapter(mConsumerAdapter);

        imvw_coordinator_00 = findViewById(R.id.imvw_coordinator_00);
        imvw_location_00    = findViewById(R.id.imvw_location_00);

        myspn_coordinator_00 = findViewById(R.id.myspn_coordinator_00);
        myspn_coordinator_00.setTitle("Sesuai");
        myspn_location_00    = findViewById(R.id.myspn_location_00);
        myspn_location_00.setTitle("Sesuai");
    }

    @Override
    protected void initData() {
        mBookingID = getIntent().getStringExtra("booking_id");

        ArrayList<Bundle> values = new ArrayList<>();
        Bundle b1 = new Bundle();
        b1.putString("key","YES");
        b1.putString("value","Ya");
        Bundle b2 = new Bundle();
        b2.putString("key","NO");
        b2.putString("value","Tidak");
        values.add(b1);
        values.add(b2);
        myspn_coordinator_00.setDataBundle(values);
        myspn_location_00.setDataBundle(values);
        
        request();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_process_00).setOnClickListener(v -> {
            Intent intent = getIntent();
            intent.setClass(mActivity, ProcessMenuActivity.class);
            startActivity(intent);
        });
        myspn_coordinator_00.setOnSelectedListener((key, value) -> saveStatus("coordinator_status", key));
        myspn_location_00.setOnSelectedListener((key, value) -> saveStatus("location_status", key));
    }

    private void request(){
        lnly_info_00.removeAllViews();
        mConsumers.clear();
        if (offlineMode){
            TempDB tempDB = new TempDB();
            tempDB.getData(mActivity,mBookingID,TempDB.PROCESS_DEMO);
            try {
                builData(new JSONObject(tempDB.data));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        String param = "?booking_id="+mBookingID;
        PostManager post = new PostManager(mActivity, Config.DEMO_PROCESS +param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                builData(obj);
            }
        });
    }

    private void builData(JSONObject obj){
        try {
            JSONObject data     = obj.getJSONObject("data");
            JSONObject booking  = data.getJSONObject("booking");
            JSONArray dataJp = data.getJSONArray("data_jp");

            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id"));
            Date demoDate = oldFormat.parse(booking.getString("booking_date"));
            if (demoDate != null){
                buildInfo("Tanggal Demo", dateFormat.format(demoDate));
            }
            buildInfo("Koordinator", booking.getString("coordinator_name"));
            buildInfo("Alamat", booking.getString("coordinator_address"));
            buildInfo("Koordinat", booking.getString("coordinator_location"));
            buildInfo("No. KTP", booking.getString("coordinator_ktp"));
            buildInfo("No. HP", booking.getString("coordinator_phone"));

            Glide.with(mActivity).load(Config.IMAGE_PATH_BOOKER + booking.getString("photo_location")).into(imvw_location_00);
            Glide.with(mActivity).load(Config.IMAGE_PATH_BOOKER + booking.getString("photo_coordinator")).into(imvw_coordinator_00);

            for (int i=0; i<dataJp.length(); i++){
                JSONObject jp = dataJp.getJSONObject(i);
                Bundle bundle = new Bundle();
                bundle.putString("name", jp.getString("name"));
                bundle.putString("phone", jp.getString("phone"));
                bundle.putString("address", jp.getString("address"));
                mConsumers.add(bundle);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        mConsumerAdapter.notifyDataSetChanged();
    }

    private void buildInfo(String key, String value){
        InfoView infoView = new InfoView(mActivity, null);
        if (value.equals("null")){
            infoView.create(key, "-");
        }
        else {
            infoView.create(key, value.trim());
        }
        lnly_info_00.addView(infoView);
    }

    private void saveStatus(String pType, String pStatus){
        String param = "?booking_id="+mBookingID;
        PostManager post = new PostManager(mActivity,Config.DEMO_ITEM_CONFIRMATION+param);
        post.addParam(new ObjectApi("type",pType));
        post.addParam(new ObjectApi("status",pStatus));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                Utility.showToastSuccess(mActivity,"Perubahan Berhasil");
            }
            else {
                Utility.showToastError(mActivity, message);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("FINISH"));
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "FINISH")){
                mActivity.finish();
            }
        }
    };

}
