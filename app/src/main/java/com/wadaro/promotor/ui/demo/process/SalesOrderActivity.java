package com.wadaro.promotor.ui.demo.process;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.Global;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.BookingDB;
import com.wadaro.promotor.database.table.SalesOrder2DB;
import com.wadaro.promotor.database.table.SalesOrderDB;
import com.wadaro.promotor.database.table.TempDB;
import com.wadaro.promotor.module.FailedWindow;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.ui.order.InfoView;
import com.wadaro.promotor.util.FormEditext;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SalesOrderActivity extends MyActivity {

    private LinearLayout lnly_info_00;
    private FormEditext edtx_note_00;

    private String mBookingID = "";
    private String deliveryDate = "";
    private String sDataCoordinator = "";
    private boolean isClicked = true;

    @Override
    protected int setLayout() {
        return R.layout.demo_process_activity_salesorder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Sales Order");

        lnly_info_00 = findViewById(R.id.lnly_info_00);
        edtx_note_00 = findViewById(R.id.edtx_note_00);
        edtx_note_00.setTitle("Keterangan");
    }

    @Override
    protected void initData() {
        mBookingID = getIntent().getStringExtra("booking_id");
        request();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_edit_00).setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, EditCoordinatorActivity.class);
            intent.putExtra("data",sDataCoordinator);
            startActivityForResult(intent, 11);

        });
        findViewById(R.id.bbtn_save_00).setOnClickListener(v -> {
            if (offlineMode){
                saveLocal();
            }
            else {
                if (isClicked){
                    isClicked = false;
                    new Handler().postDelayed(() -> isClicked = true,500);
                    save();
                }
            }
        });
    }

    private void request(){
        lnly_info_00.removeAllViews();
        if (offlineMode){
            TempDB tempDB = new TempDB();
            tempDB.getData(mActivity,mBookingID,TempDB.SALES_ORDER);
            try {
                if (!tempDB.data.isEmpty()){
                    buildData(new JSONObject(tempDB.data));
                }
                else {
                    BookingDB bookingDB = new BookingDB();
                    bookingDB.getData(mActivity, mBookingID);
                    JSONObject obj = new JSONObject(bookingDB.data);
                    buildData(createData(obj));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        String param = "?booking_id="+mBookingID;
        PostManager post = new PostManager(mActivity, Config.GET_DATA_FOR_CREATE_SALES_ORDER+param);
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
            JSONObject booker  = data.getJSONObject("booker");
            JSONObject branch   = data.getJSONObject("branch");
            JSONObject coordinator = data.getJSONObject("coordinator");
            sDataCoordinator = coordinator.toString();

            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id"));
            Date demoDate = oldFormat.parse(data.getString("date"));

            buildInfo("Nama DB / KACAB", booker.getString("name"));
            buildInfo("Nama KACAB", branch.getString("manager_name"));
            buildInfo("Nama KAWIL", branch.getString("regional_manager_name"));
            buildInfo("Cabang", branch.getString("name"));
            buildInfo("PT", branch.getString("company"));
            addLine();
            if (demoDate != null){
                buildInfo("Tanggal Demo", dateFormat.format(demoDate));
            }
            buildInfo("Jadwal Demo", "demo "+data.getString("demo"));
            buildInfo("Koordinator", coordinator.getString("name"));
            buildInfo("Alamat", coordinator.getString("address"));
            buildInfo("Koordinat", coordinator.getString("location"));
            buildInfo("No. KTP", coordinator.getString("ktp"));
            buildInfo("No. HP", coordinator.getString("phone"));
            buildInfo("Tanggal Pengiriman  ", data.getString("delivery_date"));
            addLine();
            deliveryDate = data.getString("delivery_date");

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
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

    private JSONObject createData(JSONObject pParam){
        try {
            JSONObject obj = new JSONObject("{\"status\":200,\"message\":\"Success Read Data\",\"data\":{\"id\":\"COSMA\\/DB\\/10\\/20\\/000506\",\"demo\":\"1\",\"date\":\"2020-10-31 19:06:00\",\"delivery_date\":\"2020-10-31\",\"status\":\"Booking\",\"booker\":{\"id\":\"dummy-prom\",\"name\":\"DUMMYPROM\"},\"promotor\":{\"id\":\"421\",\"name\":\"dummy-Prom\"},\"branch\":{\"id\":\"DUMMY-CB-BGR\",\"name\":\"Cabang Dummy Bogor\",\"company\":\"PT. COSMA MITRA SEJAHTERA\",\"company_id\":\"COSMA\",\"manager_id\":\"02014\\/0206\",\"manager_name\":\"Absis Hanomo\",\"regional_manager_id\":\"02014\\/0206\",\"regional_manager_name\":\"Absis Hanomo\"},\"coordinator\":{\"id\":\"0010418\",\"name\":\"Test Online 2\",\"alias_name\":\"Rezza Test\",\"ktp\":null,\"phone\":\"087881222\",\"address\":\"1600 Amphitheatre Pkwy, Mountain View, CA 94043, Amerika Serikat\",\"location\":\"37.4220019,-122.0839854\"}}}");

            JSONObject data     = obj.getJSONObject("data");
            data.put("date",pParam.getString("booking_date"));
            data.put("demo",pParam.getString("booking_demo"));
            data.put("delivery_date",pParam.getString("delivery_date"));

            JSONObject booker  = data.getJSONObject("booker");
            JSONObject dataLogin = new JSONObject(MyPreference.getString(mActivity, Global.DATA_LOGIN));
            booker.put("id",dataLogin.getString("employee_id"));
            booker.put("name",dataLogin.getString("user_name"));

            JSONObject branch   = data.getJSONObject("branch");
            branch.put("manager_name",dataLogin.getString("branch_manager_name"));
            branch.put("regional_manager_name",dataLogin.getString("regional_manager_name"));
            branch.put("name",dataLogin.getString("branch_name"));
            branch.put("company",dataLogin.getString("company_name"));
            JSONObject coordinator = data.getJSONObject("coordinator");
            coordinator.put("name",pParam.getString("coordinator_name"));
            coordinator.put("address",pParam.getString("coordinator_address"));
            coordinator.put("location",pParam.getString("coordinator_location"));
            coordinator.put("ktp",pParam.getString("coordinator_ktp"));
            coordinator.put("phone",pParam.getString("coordinator_phone"));
//
//            buildInfo("Jadwal Demo", "demo "+data.getString("demo"));
//            buildInfo("Koordinator", coordinator.getString("name"));
//            buildInfo("Alamat", coordinator.getString("address"));
//            buildInfo("Koordinat", coordinator.getString("location"));
//            buildInfo("No. KTP", coordinator.getString("ktp"));
//            buildInfo("No. HP", coordinator.getString("phone"));
//            buildInfo("Tanggal Pengiriman  ", data.getString("delivery_date"));
//            addLine();
//            deliveryDate = data.getString("delivery_date");

            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private void save(){
        String note  = edtx_note_00.getValue();
        String param = "?booking_id="+mBookingID;
        PostManager post = new PostManager(mActivity, Config.CREATE_SALES_ORDER+param);
        post.addParam(new ObjectApi("delivery_date",deliveryDate));
        post.addParam(new ObjectApi("sales_note",note));
        post.addParam(new ObjectApi("sales_code","K1"));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                Utility.showToastSuccess(mActivity, message);
                try {
                    JSONObject data = obj.getJSONObject("data");
                    Intent intent = getIntent();
                    intent.setClass(mActivity, SalesOrder2Activity.class);
                    intent.putExtra("sales_order", data.getString("sales_id"));
                    intent.putExtra("delivery_date", deliveryDate);
                    intent.putExtra("note", note);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                FailedWindow window = new FailedWindow(mActivity);
                window.setDescription(message);
                window.show();
            }
        });
    }
    private void saveLocal(){
        String note  = edtx_note_00.getValue();
        String param = "?booking_id="+mBookingID;
        PostManager post = new PostManager(mActivity, Config.CREATE_SALES_ORDER+param);
        post.addParam(new ObjectApi("delivery_date",deliveryDate));
        post.addParam(new ObjectApi("sales_note",note));
        post.addParam(new ObjectApi("sales_code","K1"));
        SalesOrderDB orderDB = new SalesOrderDB();
        orderDB.delete(mActivity,mBookingID);
        Calendar calendar = Calendar.getInstance();

        orderDB.id = "COSMA/SO/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR)+"/"+System.currentTimeMillis();
        orderDB.bookingId = mBookingID;
        orderDB.data = post.getData().toString();
        orderDB.insert(mActivity);

        SalesOrder2DB salesOrder2DB = new SalesOrder2DB();
        salesOrder2DB.delete(mActivity, SalesOrder2DB.BOOKING_ID+"='"+mBookingID+"'");

        Intent intent = getIntent();
        intent.setClass(mActivity, SalesOrder2Activity.class);
        intent.putExtra("sales_order", orderDB.id);
        intent.putExtra("delivery_date", deliveryDate);
        intent.putExtra("note", note);
        intent.putExtra("offline", true);
        intent.putExtra("offline_so", "COSMA/SO/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR)+"/???");
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == RESULT_OK){
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "FINISH")){
                mActivity.finish();
            }
        }
    };
}
