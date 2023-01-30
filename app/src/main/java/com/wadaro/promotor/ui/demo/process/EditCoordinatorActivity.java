package com.wadaro.promotor.ui.demo.process;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.InputType;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.util.FormEditext;
import com.wadaro.promotor.util.KtpValidator;
import com.wadaro.promotor.util.OperatorPrifix;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class EditCoordinatorActivity extends MyActivity {

    private FormEditext edtx_ktp_00,edtx_name_00,edtx_phone_00,edtx_address_00;
    private TextView txvw_longlat_00;
    private static final int REQ_TAGADDRESS = 12;
    private String mId = "";

    @Override
    protected int setLayout() {
        return R.layout.demo_activity_editcoordinator;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Edit Coordinator");

        edtx_ktp_00     = findViewById(R.id.edtx_ktp_00);
        edtx_ktp_00.setTitle("Nomor KTP");
        edtx_ktp_00.setInputType(InputType.TYPE_CLASS_NUMBER);

        edtx_name_00    = findViewById(R.id.edtx_name_00);
        edtx_name_00.setTitle("Nama");
        edtx_name_00.withoutSpecialChar();

        edtx_phone_00    = findViewById(R.id.edtx_phone_00);
        edtx_phone_00.setTitle("Nomor Telepon");
        edtx_phone_00.setInputType(InputType.TYPE_CLASS_PHONE);

        edtx_address_00    = findViewById(R.id.edtx_address_00);
        edtx_address_00.setTitle("Alamat");
        edtx_address_00.setMultiLine();

        txvw_longlat_00    = findViewById(R.id.txvw_longlat_00);

    }

    @Override
    protected void initData() {
        String sData = getIntent().getStringExtra("data");
        try {
            if (sData != null){
                JSONObject objData = new JSONObject(sData);
                String name = objData.getString("name");
                if (name.split(" - ").length > 1){
                    name = name.split(" - ")[1];
                }
                mId = objData.getString("id");
                edtx_ktp_00.setValue(objData.getString("ktp"));
                edtx_name_00.setValue(name);
                edtx_phone_00.setValue(objData.getString("phone"));
                edtx_address_00.setValue(objData.getString("address"));
                txvw_longlat_00.setText(objData.getString("location"));
            }
            else {
                onBackPressed();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initListener() {

        findViewById(R.id.bbtn_tagaddress_00).setOnClickListener(v -> {
            startActivityForResult(new Intent(mActivity, MapsAddrActivity.class), REQ_TAGADDRESS);
        });

        findViewById(R.id.bbtn_save_00).setOnClickListener(v -> save());

        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_TAGADDRESS && resultCode == -1){
            if (data != null){
                edtx_address_00.setValue(Objects.requireNonNull(data.getStringExtra("ADDRESS")));
                txvw_longlat_00.setText(data.getStringExtra("LATITUDE")+","+data.getStringExtra("LONGITUDE"));
            }
        }
    }

    private void save(){
        String name = edtx_name_00.getValue();
        String phone = edtx_phone_00.getValue();
        String address = edtx_address_00.getValue();
        String longlat = txvw_longlat_00.getText().toString();
        String identity = edtx_ktp_00.getValue();

        if (!identity.isEmpty()){
            KtpValidator ktpValidator = new KtpValidator();
            if (!ktpValidator.valid(identity)){
                Utility.showToastError(mActivity,"No. KTP/NIK tidak valid!");
                return;
            }
        }
        else {
            if (phone.isEmpty()){
                Utility.showToastError(mActivity,"Nomor Telepon tidak valid!");
                return;
            }
            OperatorPrifix prifix = new OperatorPrifix();
            prifix.getInfo(phone);
            if (!prifix.isValidated()){
                Utility.showToastError(mActivity,"Nomor Telepon tidak valid!");
                return;
            }
            phone = prifix.getPhoneNumber();
        }

        if (name.isEmpty()){
            Utility.showToastError(mActivity,"Name tidak valid!");
            return;
        }

        if (address.isEmpty()){
            Utility.showToastError(mActivity,"Alamat tidak valid!");
            return;
        }

        PostManager post = new PostManager(mActivity,"coordinator/"+mId);
        post.addParam(new ObjectApi("coordinator_name",name));
        post.addParam(new ObjectApi("coordinator_address",address));
        post.addParam(new ObjectApi("coordinator_location",longlat));
        post.addParam(new ObjectApi("coordinator_phone",phone));
        post.addParam(new ObjectApi("coordinator_ktp",identity));
        post.execute("PUT");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                Utility.showToastSuccess(mActivity, message);
                setResult(RESULT_OK);
                mActivity.finish();
            }
            else {
                Utility.showToastError(mActivity, message);
            }
        });
    }
}
