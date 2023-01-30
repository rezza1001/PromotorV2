package com.wadaro.promotor.ui.demo.process.jp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.database.table.JpDB;
import com.wadaro.promotor.database.table.TempDB;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.util.FormEditext;
import com.wadaro.promotor.util.OperatorPrifix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddJPActivity extends MyActivity {

    private TextView txvw_date_00,txvw_coordinator_00,txvw_addres_00,txvw_note_00,txvw_ktp_00;
    private FormEditext edtx_name_00,edtx_phone_00, edtx_address_00;
    private ArrayList<ConsumerHolder> consumers = new ArrayList<>();
    private ConsumerAdapter mAdapter;

    private String mBookingID;
    @Override
    protected int setLayout() {
        return R.layout.demo_process_activity_addjp;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Data booking");

        txvw_date_00            = findViewById(R.id.txvw_date_00);
        txvw_coordinator_00     = findViewById(R.id.txvw_coordinator_00);
        txvw_addres_00          = findViewById(R.id.txvw_addres_00);
        txvw_note_00            = findViewById(R.id.txvw_note_00);
        txvw_ktp_00             = findViewById(R.id.txvw_ktp_00);

        edtx_name_00 = findViewById(R.id.edtx_name_00);
        edtx_name_00.setTitle("Nama");
        edtx_name_00.setMandatory();
        edtx_name_00.withoutSpecialChar();

        edtx_phone_00 = findViewById(R.id.edtx_phone_00);
        edtx_phone_00.setTitle("No. Telepon");
        edtx_phone_00.setInputType(InputType.TYPE_CLASS_PHONE);
        edtx_phone_00.setMandatory();

        edtx_address_00     = findViewById(R.id.edtx_address_00);
        edtx_address_00.setTitle("Alamat");
        edtx_address_00.setMandatory();

        RecyclerView rcvw_jp_00 = findViewById(R.id.rcvw_jp_00);
        rcvw_jp_00.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_jp_00.setNestedScrollingEnabled(false);

        mAdapter = new ConsumerAdapter(mActivity,consumers);
        rcvw_jp_00.setAdapter(mAdapter);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        mBookingID = getIntent().getStringExtra("booking_id");
        boolean modeOffline = getIntent().getBooleanExtra("OFFLINE", false);
        if (!modeOffline){
            loadData();
        }
        else {
//            consumers.add(new ConsumerHolder("-99", getIntent().getStringExtra("COORDINATOR_NAME"), getIntent().getStringExtra("COORDINATOR_PHONE"), getIntent().getStringExtra("COORDINATOR_ADDRESS")));
        }

    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle("Anda yakin untuk keluar dari halaman ini?");
            alertDialog.setNegativeButton("Ya", (dialog, which) -> onBackPressed());
            alertDialog.show();
        });
        findViewById(R.id.bbtn_add_00).setOnClickListener(v -> add());
        mAdapter.setOnSelectedListener(data -> {
            for (int i=0; i<consumers.size();i++){
                if (data.phone.equals(consumers.get(i).phone)){
                    if (getIntent().getBooleanExtra("OFFLINE", false)){
                        consumers.remove(i);
                        mAdapter.notifyDataSetChanged();
                    }
                    else {
                        delete(data.id);
                    }
                    break;
                }
            }
        });
        findViewById(R.id.bbtn_save_00).setOnClickListener(v -> onBackPressed());
    }

    private void add(){
        String name     = edtx_name_00.getValue();
        String phone    = edtx_phone_00.getValue();
        String address  = edtx_address_00.getValue();

        if (name.isEmpty()){
            Utility.showToastError(mActivity,"Nama Harus diisi!");
            return;
        }
        if (phone.isEmpty()){
            Utility.showToastError(mActivity,"No Hp. Harus diisi!");
            return;
        }
        OperatorPrifix prifix = new OperatorPrifix();
        prifix.getInfo(phone);
        if (!prifix.isValidated()){
            Utility.showToastError(mActivity,"No. Hp tidak valid!");
            return;
        }
        if (address.isEmpty()){
            Utility.showToastError(mActivity,"Alamat Harus diisi!");
            return;
        }

        if (offlineMode){
            saveToLocal(name, prifix.getPhoneNumber(), address);
        }
        else {
            saveToServer(name, prifix.getPhoneNumber(), address);
        }
    }

    private void saveToLocal(String name,String phone, String address){
        PostManager post = new PostManager(mActivity,Config.CREATE_DEMO_DATA_JP);
        post.addParam(new ObjectApi("name",name));
        post.addParam(new ObjectApi("phone",phone));
        post.addParam(new ObjectApi("address",address));
        post.addParam(new ObjectApi("ktp","-"));
        JpDB jp = new JpDB();
        jp.id           = jp.getNextID(mActivity) + 1;
        jp.bookingID    = mBookingID;
        jp.data         = post.getData().toString();
        jp.insert(mActivity);

        String tmpID = System.currentTimeMillis()+"";
        consumers.add(new ConsumerHolder(tmpID, name, phone, address));
        mAdapter.notifyDataSetChanged();

        edtx_address_00.setValue("");
        edtx_name_00.setValue("");
        edtx_phone_00.setValue("");
    }
    private void saveToServer(String name,String phone, String address){
        PostManager post = new PostManager(mActivity,Config.CREATE_DEMO_DATA_JP);
        post.addParam(new ObjectApi("booking_id",mBookingID));
        post.addParam(new ObjectApi("name",name));
        post.addParam(new ObjectApi("phone",phone));
        post.addParam(new ObjectApi("address",address));
        post.addParam(new ObjectApi("ktp","-"));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                edtx_address_00.setValue("");
                edtx_name_00.setValue("");
                edtx_phone_00.setValue("");
                loadData();
                Utility.showToastSuccess(mActivity, message);
            }
            else {
                Utility.showToastError(mActivity, message);
            }
        });
    }

    private void loadData(){
        consumers.clear();
        if (offlineMode){
            TempDB tempDB = new TempDB();
            tempDB.getData(mActivity,mBookingID,TempDB.PROCESS_DEMO);
            try {
                builData(new JSONObject(tempDB.data));
                JpDB jpDB = new JpDB();
                for (JpDB db: jpDB.getData(mActivity, mBookingID)){
                    JSONObject jp = new JSONObject(db.data);
                    consumers.add(new ConsumerHolder("-99",jp.getString("name"),jp.getString("phone"), jp.getString("address")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mAdapter.notifyDataSetChanged();
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
                txvw_date_00.setText(dateFormat.format(demoDate));
            }
            txvw_coordinator_00.setText(booking.getString("coordinator_name"));
            txvw_addres_00.setText(booking.getString("coordinator_address"));
            txvw_note_00.setText(booking.getString("coordinator_note"));
            txvw_ktp_00.setText(booking.getString("coordinator_ktp"));

            for (int i=0; i<dataJp.length(); i++){
                JSONObject jp = dataJp.getJSONObject(i);
                consumers.add(new ConsumerHolder(jp.getString("id"),jp.getString("name"),jp.getString("phone"), jp.getString("address")));
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();
    }

    private void delete(String id){
        if (id.equals("-99")){
            Utility.showToastError(mActivity,"Tidak dapat dihapus");
            return;
        }
        PostManager post = new PostManager(mActivity,Config.DELETE_DEMO_DATA_JP+id);
        post.execute("DELETE");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                loadData();
            }
            else {
                Utility.showToastError(mActivity , message);
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        mActivity.finish();
    }
}
