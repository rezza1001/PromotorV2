package com.wadaro.promotor.ui.demo.process;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
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
import com.wadaro.promotor.base.Global;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.BookingDB;
import com.wadaro.promotor.database.table.JpDB;
import com.wadaro.promotor.database.table.TempDB;
import com.wadaro.promotor.database.table.UserDB;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.ui.booker.CreateDemoActivity;
import com.wadaro.promotor.ui.order.InfoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DetailDemoActivity extends MyActivity {

    private static final int CHANGE_COORDINATOR = 11;

    private LinearLayout lnly_info_00;
    private Button bbtn_cancel_00;
    private ConsumerAdapter mConsumerAdapter;
    private final ArrayList<Bundle> mConsumers = new ArrayList<>();

    private String mBookingID = "";
    private String coordinatorData = "";

    @Override
    protected int setLayout() {
        return R.layout.demo_process_activity_detail;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Penugasan Demo");

        lnly_info_00 = findViewById(R.id.lnly_info_00);
        bbtn_cancel_00 = findViewById(R.id.bbtn_cancel_00);

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
        findViewById(R.id.bbtn_change_00).setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, ChangeCoorActivity.class);
            intent.putExtra("booking_id", mBookingID);
            intent.putExtra("coordinator", coordinatorData);
            startActivityForResult(intent,CHANGE_COORDINATOR);
        });
        bbtn_cancel_00.setOnClickListener(v -> {
            CancelDialog dialog = new CancelDialog(mActivity);
            dialog.show();
            dialog.setOnSelectedListener(this::cancelDemo);
        });
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_process_00).setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, ProcessMainActivity.class);
            if (getIntent().getBooleanExtra("offline_booking", false)){
                intent = new Intent(mActivity, ProcessMenuActivity.class);
            }
            intent.putExtra("booking_id", mBookingID);
            startActivity(intent);
        });
    }

    private void request(){
        lnly_info_00.removeAllViews();
        mConsumers.clear();

        if (offlineMode){
            bbtn_cancel_00.setVisibility(View.INVISIBLE);
            TempDB tempDB = new TempDB();
            tempDB.getData(mActivity,mBookingID,TempDB.DETAIL_BOOKING);
            try {
                if (!tempDB.data.isEmpty()){
                    buildData(new JSONObject(tempDB.data));
                }
                else {
                    BookingDB bookingDB = new BookingDB();
                    bookingDB.getData(mActivity, mBookingID);
                    JSONObject obj = new JSONObject(bookingDB.data);
                    buildData(buildOffline(obj, bookingDB.id));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        String param = "?booking_id="+mBookingID;
        PostManager post = new PostManager(mActivity, Config.GET_DEMO_DATA_DETAIL+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                buildData(obj);
            }
        });
    }

    private void buildData(JSONObject obj){
        try {
            JSONObject data     = obj.getJSONObject("data");
            JSONObject booking  = data.getJSONObject("booking");
            JSONObject booker  = booking.getJSONObject("booker");
            JSONObject branch   = booking.getJSONObject("branch");
            JSONObject coordinator = booking.getJSONObject("coordinator");
            JSONArray dataJp = data.getJSONArray("consumers");

            coordinatorData = coordinator.toString();
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id"));
            Date demoDate = oldFormat.parse(booking.getString("date"));

            buildInfo("Nama DB / KACAB", booker.getString("name"));
            buildInfo("Nama KACAB", branch.getString("manager_name"));
            buildInfo("Nama KAWIL", branch.getString("regional_manager_name"));
            buildInfo("Cabang", branch.getString("name"));
            buildInfo("PT", branch.getString("company"));
            addLine();
            if (demoDate != null){
                buildInfo("Tanggal Demo", dateFormat.format(demoDate));
            }
            buildInfo("Koordinator", coordinator.getString("name"));
            buildInfo("Alamat", coordinator.getString("address"));
            buildInfo("No. KTP", coordinator.getString("ktp"));
            buildInfo("No. HP", coordinator.getString("phone"));

            for (int i=0; i<dataJp.length(); i++){
                JSONObject jp = dataJp.getJSONObject(i);
                Bundle bundle = new Bundle();
                bundle.putString("name", jp.getString("name"));
                bundle.putString("phone", jp.getString("phone"));
                bundle.putString("ktp", jp.getString("ktp"));
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
    private void addLine(){
        View view = new View(mActivity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utility.dpToPx(mActivity, 1));
        lp.topMargin = Utility.dpToPx(mActivity, 5);
        lp.bottomMargin = Utility.dpToPx(mActivity, 5);
        view.setBackgroundColor(Color.LTGRAY);
        lnly_info_00.addView(view,lp);
    }

    private JSONObject buildOffline(JSONObject param, String id) throws JSONException {
        UserDB userDB = new UserDB();
        userDB.getData(mActivity);
        JSONObject dataLogin = new JSONObject(MyPreference.getString(mActivity, Global.DATA_LOGIN));

        JSONObject obj = new JSONObject(" {\"status\":200,\"message\":\"Success Read Data\",\"data\":{\"booking\":{\"id\":\"COSMA\\/DB\\/10\\/20\\/000504\",\"demo\":\"1\",\"date\":\"2020-10-30 16:20:00\",\"status\":\"Booking\",\"booker\":{\"id\":\"dummy-prom\",\"name\":\"DUMMYPROM\"},\"promotor\":{\"id\":\"421\",\"name\":\"dummy-Prom\"},\"branch\":{\"id\":\"DUMMY-CB-BGR\",\"name\":\"Cabang Dummy Bogor\",\"company\":\"PT. COSMA MITRA SEJAHTERA\",\"manager_id\":\"02014\\/0206\",\"manager_name\":\"Absis Hanomo\",\"regional_manager_id\":\"02014\\/0206\",\"regional_manager_name\":\"Absis Hanomo\"},\"coordinator\":{\"id\":\"0010416\",\"name\":\"Test Online 1\",\"alias_name\":\"Online1\",\"ktp\":null,\"phone\":\"08176277833\",\"address\":\"1600 Amphitheatre Pkwy, Mountain View, CA 94043, Amerika Serikat\",\"location\":\"37.4220019,-122.0839854\"}},\"consumers\":[{\"id\":22410,\"booking_id\":\"COSMA\\/DB\\/10\\/20\\/000504\",\"name\":\"Test Online 1\",\"phone\":\"08176277833\",\"address\":\"1600 Amphitheatre Pkwy, Mountain View, CA 94043, Amerika Serikat\",\"ktp\":null,\"updated_by\":\"dummy-Prom\",\"updated_date\":\"2020-10-30 16:21:37\"}]}}");
        JSONObject data     = obj.getJSONObject("data");
        JSONObject booking  = data.getJSONObject("booking");
        Calendar calendar = Calendar.getInstance();
        booking.put("id", "OFFLINE/"+calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DATE)+"/"+ id);

        JSONObject booker  = booking.getJSONObject("booker");
        booker.put("name",userDB.name);

        JSONObject branch   = booking.getJSONObject("branch");
        branch.put("id",userDB.branch_id);
        branch.put("name",dataLogin.getString("branch_name"));
        branch.put("manager_name",dataLogin.getString("branch_manager_name"));
        branch.put("regional_manager_name",dataLogin.getString("regional_manager_name"));
        branch.put("company",dataLogin.getString("company_name"));

        JSONObject coordinator = booking.getJSONObject("coordinator");
        coordinator.put("name",param.getString("coordinator_name"));
        coordinator.put("alias_name",param.getString("coordinator_alias_name"));
        coordinator.put("ktp",param.getString("coordinator_ktp"));
        coordinator.put("phone",param.getString("coordinator_phone"));
        coordinator.put("address",param.getString("coordinator_address"));
        coordinator.put("location",param.getString("coordinator_location"));
        JSONArray dataJp = data.getJSONArray("consumers");
        JSONObject consumer = new JSONObject();
        JpDB jpDB = new JpDB();
        JpDB jpdb = jpDB.getData(mActivity, id).get(0);
        JSONObject jpObj = new JSONObject(jpdb.data);

        consumer.put("id",jpdb.id);
        consumer.put("booking_id",jpdb.bookingID);
        consumer.put("name",jpObj.getString("name"));
        consumer.put("phone",jpObj.getString("phone"));
        consumer.put("address",jpObj.getString("address"));
        consumer.put("ktp",jpObj.getString("ktp"));
        dataJp.put(0,consumer);
        return  obj;
    }

    private void cancelDemo(int pStatus){
        PostManager post = new PostManager(mActivity, Config.DEMO_CANCELLED);
        post.addParam(new ObjectApi("booking_id", mBookingID));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                sendBroadcast(new Intent("FINISH"));
                if (pStatus == 1){
                    startActivity(new Intent(mActivity, CreateDemoActivity.class));
                    mActivity.finish();
                }
                else {
                    new Handler().postDelayed(() -> mActivity.finish(),200);
                }
            }
            else {
                Utility.showToastError(mActivity, message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_COORDINATOR && resultCode == RESULT_OK){
            request();
        }
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
