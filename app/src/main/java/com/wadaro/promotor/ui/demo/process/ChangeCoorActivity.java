package com.wadaro.promotor.ui.demo.process;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.module.FailedWindow;
import com.wadaro.promotor.util.FormEditext;
import com.wadaro.promotor.util.MySpinnerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ChangeCoorActivity extends MyActivity {
    private static final int REQ_ADDRESS = 11;

    private TextView txvw_longlat_00;
    private MySpinnerView myspn_coordinator_00,myspn_address_00;
    private FormEditext edtx_nick_00,edtx_identity_00,edtx_phone_00,edtx_tag_00,edtx_note_00,edtx_name_00;

    @Override
    protected int setLayout() {
        return R.layout.demo_process_activity_change;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Pindah Koordinator");

        myspn_coordinator_00 = findViewById(R.id.myspn_coordinator_00);
        myspn_coordinator_00.setTitle("Pilih Koordinator");

        edtx_name_00 = findViewById(R.id.edtx_name_00);
        edtx_name_00.setTitle("Nama Lengkap Sesuai KTP");
        edtx_name_00.withoutSpecialChar();

        edtx_nick_00 = findViewById(R.id.edtx_nick_00);
        edtx_nick_00.setTitle("Nama Panggilan");
        edtx_nick_00.withoutSpecialChar();
//        edtx_nick_00.setDisable();

        edtx_identity_00 = findViewById(R.id.edtx_identity_00);
        edtx_identity_00.setTitle("NO. KTP / NIK Koordinator");
//        edtx_identity_00.setDisable();

        edtx_phone_00 = findViewById(R.id.edtx_phone_00);
        edtx_phone_00.setTitle("NO. Telepon Koordinator");
//        edtx_phone_00.setDisable();

        edtx_note_00 = findViewById(R.id.edtx_note_00);
        edtx_note_00.setTitle("Patokan Lokasi");

        myspn_address_00 = findViewById(R.id.myspn_address_00);
        myspn_address_00.setTitle("Areal Garapan");

        txvw_longlat_00 = findViewById(R.id.txvw_longlat_00);
        edtx_tag_00 = findViewById(R.id.edtx_tag_00);
        edtx_tag_00.setTitle("Tag Alamat");
//        edtx_tag_00.setDisable();
        edtx_tag_00.setMultiLine();
    }

    @Override
    protected void initData() {

        String param = "?booking_id="+ getIntent().getStringExtra("booking_id");
        PostManager post = new PostManager(mActivity, Config.DEMO_CHANGE_COORDINATOR+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    JSONArray coordinator = data.getJSONArray("coordinator");
                    JSONArray address    = data.getJSONArray("address");

                    buildCoordinator(coordinator);
                    buildAddress(address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initListener() {
        findViewById(R.id.bbtn_tagaddress_00).setOnClickListener(v -> startActivityForResult(new Intent(mActivity, MapsAddrActivity.class), REQ_ADDRESS));
        findViewById(R.id.bbtn_save_00).setOnClickListener(v -> saveData());
        findViewById(R.id.bbtn_cancel_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }

    private void buildCoordinator(JSONArray data) throws JSONException {
        ArrayList<Bundle> coordinators = new ArrayList<>();
        for (int i=0; i<data.length(); i++){
            JSONObject obj = data.getJSONObject(i);
            Bundle bundle = new Bundle();
            bundle.putString("key", obj.getString("id"));
            bundle.putString("value", obj.getString("name"));
            bundle.putString("other", obj.toString());
            coordinators.add(bundle);
        }
        myspn_coordinator_00.setDataBundle(coordinators);
        myspn_coordinator_00.setOnSelectedListener((key, value) -> {
            JSONObject others = myspn_coordinator_00.getOtherData();
            try {
                Log.d("TAGRZ","others "+others);
                edtx_nick_00.setValue(others.getString("name"));
                edtx_identity_00.setValue(others.getString("ktp"));
                edtx_phone_00.setValue(others.getString("phone"));
                edtx_tag_00.setValue(others.getString("address"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        new Handler().postDelayed(() -> myspn_coordinator_00.setSelection(0),600);
    }

    private void buildAddress(JSONArray data) throws JSONException {
        ArrayList<Bundle> coordinators = new ArrayList<>();
        for (int i=0; i<data.length(); i++){
            JSONObject obj = data.getJSONObject(i);
            Bundle bundle = new Bundle();
            bundle.putString("key", obj.getString("id"));
            bundle.putString("value", obj.getString("text"));
            coordinators.add(bundle);
        }
        myspn_address_00.setDataBundle(coordinators);
//        myspn_address_00.setSelection(getIntent().getStringExtra("sales_area"));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADDRESS && resultCode == RESULT_OK){
            if (data != null){
                edtx_tag_00.setValue(Objects.requireNonNull(data.getStringExtra("ADDRESS")));
                txvw_longlat_00.setText(data.getStringExtra("LATITUDE")+","+data.getStringExtra("LONGITUDE"));
            }
        }
    }

    private void saveData(){
        String identity = edtx_identity_00.getValue();
        String name     = edtx_nick_00.getValue();
        String phone    = edtx_phone_00.getValue();
        String address  = edtx_tag_00.getValue();
        String longlat  = txvw_longlat_00.getText().toString();
        String note     = edtx_note_00.getValue();
        String salesArea= myspn_address_00.getKeySelected();

        String param = "?booking_id="+ getIntent().getStringExtra("booking_id");
        PostManager post = new PostManager(mActivity, Config.DEMO_CHANGE_COORDINATOR+param);
        if (!identity.isEmpty()){
            post.addParam(new ObjectApi("coordinator_ktp",identity));
        }
        post.addParam(new ObjectApi("coordinator_name",name));
        post.addParam(new ObjectApi("coordinator_alias_name",name));
        post.addParam(new ObjectApi("coordinator_phone",phone));
        post.addParam(new ObjectApi("coordinator_address",address));
        post.addParam(new ObjectApi("coordinator_location",longlat));
        post.addParam(new ObjectApi("coordinator_note",note));
        post.addParam(new ObjectApi("sales_area_id",salesArea));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                sendBroadcast(new Intent("CHANGE_FINISH"));
                new Handler().postDelayed(() -> {
                    setResult(RESULT_OK);
                    mActivity.finish();
                },100);
            }
            else {
                FailedWindow failedWindow = new FailedWindow(mActivity);
                failedWindow.setDescription(message);
                failedWindow.show();
            }
        });
    }
}
