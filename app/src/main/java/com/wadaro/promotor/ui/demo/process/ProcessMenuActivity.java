package com.wadaro.promotor.ui.demo.process;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.component.WarningWindow;
import com.wadaro.promotor.database.table.BookingDB;
import com.wadaro.promotor.database.table.JpDB;
import com.wadaro.promotor.database.table.TempDB;
import com.wadaro.promotor.module.FailedWindow;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.ui.demo.process.jp.AddJPActivity;
import com.wadaro.promotor.ui.order.InfoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ProcessMenuActivity extends MyActivity {

    private static final int REQ_ADD_JP = 12;

    private TextView txvw_noso_00;
    private LinearLayout lnly_info_00;
    private ConsumerAdapter mConsumerAdapter;
    private final ArrayList<Bundle> mConsumers = new ArrayList<>();

    private String mBookingID = "";
    @Override
    protected int setLayout() {
        return R.layout.demo_process_activity_menu;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Demo Produk");

        lnly_info_00 = findViewById(R.id.lnly_info_00);
        txvw_noso_00 = findViewById(R.id.txvw_noso_00);

        RecyclerView rcvw_data_00 = findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        mConsumerAdapter = new ConsumerAdapter(mConsumers);
        rcvw_data_00.setAdapter(mConsumerAdapter);

    }

    @Override
    protected void initData() {
        mBookingID = getIntent().getStringExtra("booking_id");
        request();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.txvw_info_00).setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, ProductInfoActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.txvw_addjp_00).setOnClickListener(v -> {
            Intent intent = getIntent();
            intent.setClass(mActivity, AddJPActivity.class);
            startActivityForResult(intent,REQ_ADD_JP);
        });
        findViewById(R.id.txvw_so_00).setOnClickListener(v -> {
            Intent intent = getIntent();
            intent.setClass(mActivity, SalesOrderActivity.class);
            startActivity(intent);
        });
        txvw_noso_00.setOnClickListener(v -> {
            WarningWindow warningWindow = new WarningWindow(mActivity);
            warningWindow.show("Demo Selesai","Demo selesai tanpa sales order ?");
            warningWindow.setOnSelectedListener(status -> {
                if (status == 2){
                    finishDemo();
                }
            });
        });
    }

    private void request(){
        lnly_info_00.removeAllViews();
        mConsumers.clear();
        if (offlineMode){
            txvw_noso_00.setVisibility(View.INVISIBLE);
            TempDB tempDB = new TempDB();
            tempDB.getData(mActivity,mBookingID,TempDB.PROCESS_DEMO);
            try {
                if (!tempDB.data.isEmpty()){
                    builData(new JSONObject(tempDB.data));
                    JpDB jpDB = new JpDB();
                    for (JpDB db: jpDB.getData(mActivity, mBookingID)){
                        JSONObject jp = new JSONObject(db.data);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", jp.getString("name"));
                        bundle.putString("phone", jp.getString("phone"));
                        bundle.putString("address", jp.getString("address"));
                        mConsumers.add(bundle);
                    }
                    mConsumerAdapter.notifyDataSetChanged();
                }
                else {
                    BookingDB bookingDB = new BookingDB();
                    bookingDB.getData(mActivity, mBookingID);
                    JSONObject obj = new JSONObject(bookingDB.data);
                    builData(createObject(obj));

                    JpDB jpDB = new JpDB();
                    for (JpDB db: jpDB.getData(mActivity, mBookingID)){
                        JSONObject jp = new JSONObject(db.data);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", jp.getString("name"));
                        bundle.putString("phone", jp.getString("phone"));
                        bundle.putString("address", jp.getString("address"));
                        mConsumers.add(bundle);
                    }
                    mConsumerAdapter.notifyDataSetChanged();
                }
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

            JSONArray dataJp = data.getJSONArray("data_jp");
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

    private JSONObject createObject(JSONObject pParam) throws JSONException {
        JSONObject obj = new JSONObject("{\"status\":200,\"message\":\"Success Read Data\",\"data\":{\"booking\":{\"booking_id\":\"COSMA/DB/10/20/000506\",\"booking_demo\":\"1\",\"booking_date\":\"2020-10-31 19:06:00\",\"delivery_date\":\"2020-10-31\",\"booker_id\":\"dummy-prom\",\"booker_name\":\"DUMMYPROM\",\"branch_manager_id\":\"02014/0206\",\"branch_manager_name\":\"Absis Hanomo\",\"regional_manager_id\":\"02014/0206\",\"regional_manager_name\":\"Absis Hanomo\",\"branch_id\":\"DUMMY-CB-BGR\",\"company_id\":\"COSMA\",\"branch_name\":\"Cabang Dummy Bogor\",\"company_name\":\"PT. COSMA MITRA SEJAHTERA\",\"coordinator_id\":\"0010418\",\"promotor_id\":\"dummy-prom\",\"promotor_name\":\"DUMMYPROM\",\"updated_by\":\"dummy-Prom\",\"updated_date\":\"2020-10-31 19:06:49\",\"photo_coordinator\":\"PlQlBDEbG3XilLitd2I6YN3g0PJFFW0R9LEKJngy.jpg\",\"photo_location\":\"KtzpGzC27IHOUMO4M1Yj4tUk8U6V7lZBVdvnJkGq.jpg\",\"photo_denah\":null,\"coordinator_ktp\":null,\"coordinator_name\":\"Test Online 2\",\"coordinator_alias_name\":\"Rezza Test\",\"coordinator_phone\":\"087881222\",\"coordinator_address\":\"1600 Amphitheatre Pkwy, Mountain View, CA 94043, Amerika Serikat\",\"coordinator_location\":\"37.4220019,-122.0839854\",\"coordinator_note\":\"Test\",\"coordinator_status\":\"Active\",\"sales_area_id\":\"243\",\"sales_area_name\":\"Ciasihan\",\"booking_status\":\"Booking\"},\"data_jp\":[{\"id\":22412,\"booking_id\":\"COSMA/DB/10/20/000506\",\"name\":\"Test Online 2\",\"phone\":\"087881222\",\"address\":\"1600 Amphitheatre Pkwy, Mountain View, CA 94043, Amerika Serikat\",\"ktp\":null,\"updated_by\":\"dummy-Prom\",\"updated_date\":\"2020-10-31 19:06:49\"}]}}");
        JSONObject data     = obj.getJSONObject("data");
        JSONObject booking  = data.getJSONObject("booking");
        booking.put("booking_date",pParam.getString("booking_date"));
        booking.put("coordinator_name",pParam.getString("coordinator_name"));
        booking.put("coordinator_address",pParam.getString("coordinator_address"));
        booking.put("coordinator_location",pParam.getString("coordinator_location"));
        booking.put("coordinator_ktp",pParam.getString("coordinator_ktp"));
        booking.put("coordinator_phone",pParam.getString("coordinator_phone"));
        data.remove("data_jp");
        return obj;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADD_JP && resultCode == RESULT_OK){
            request();
        }
    }

    private void finishDemo(){
        String param = "?booking_id="+mBookingID;
        PostManager post = new PostManager(mActivity,Config.CREATE_SALES_ORDER_WO +param);
        post.addParam(new ObjectApi("booking_id", mBookingID));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                sendBroadcast(new Intent("FINISH"));
                sendBroadcast(new Intent("REFRESH"));
                Utility.showToastSuccess(mActivity,"Order Selesai");
                new Handler().postDelayed(() -> mActivity.finish(),200);
            }
            else {
                FailedWindow window = new FailedWindow(mActivity);
                window.setDescription(message);
                window.show();
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

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "FINISH")){
                mActivity.finish();
            }
        }
    };
}
