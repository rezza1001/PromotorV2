package com.rentas.ppob.contact;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.component.ChooserDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.component.LoadingDialog;
import com.rentas.ppob.database.table.ContactDB;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;

import java.util.HashMap;
import java.util.Map;

public class AddContactActivity extends MyActivity {

    private HeaderView header_view;
    private EditText edtx_name,edtx_cutomer;
    private TextView txvw_type;

    private static final HashMap<String,String> TYPE_DATA;
    static {
        TYPE_DATA = new HashMap<>();
        TYPE_DATA.put("1","Nomor Telepon");
        TYPE_DATA.put("2","ID Pelanggan (Number)");
        TYPE_DATA.put("3","ID Pelanggan (Text)");
    }

    @Override
    protected int setLayout() {
        return R.layout.contact_activity_add;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("Tambah Kontak");

        RelativeLayout rvly_name = findViewById(R.id.rvly_name);
        rvly_name.setBackground(Utility.getShapeLine(mActivity,1,6, Color.parseColor("#062C56"), Color.WHITE));
        edtx_name = findViewById(R.id.edtx_name);

        RelativeLayout rvly_type = findViewById(R.id.rvly_type);
        rvly_type.setBackground(Utility.getShapeLine(mActivity,1,6, Color.parseColor("#062C56"), Color.WHITE));
        txvw_type = findViewById(R.id.txvw_type);
        txvw_type.setTag("");

        RelativeLayout rvly_customer = findViewById(R.id.rvly_customer);
        rvly_customer.setBackground(Utility.getShapeLine(mActivity,1,6, Color.parseColor("#062C56"), Color.WHITE));
        edtx_cutomer = findViewById(R.id.edtx_cutomer);


    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        txvw_type.setText("Nomor Telepon");
        txvw_type.setTag("1");
        setInputTypeID("1");

        if (getIntent().getStringExtra("customer_id") != null){
            String type = getIntent().getStringExtra("type");
            Log.d("TAGRZ","Tyoe "+type);
            txvw_type.setText(TYPE_DATA.get(type));
            txvw_type.setTag(type);
            setInputTypeID(type);

            edtx_cutomer.setText(getIntent().getStringExtra("customer_id"));
            edtx_cutomer.setEnabled(false);
            edtx_name.setText(getIntent().getStringExtra("name"));


        }
    }

    @Override
    protected void initListener() {
        header_view.setOnBackListener(this::onBackPressed);
        txvw_type.setOnClickListener(v -> showType());
    }


    public void save(View v) {
        String name = edtx_name.getText().toString();
        String customerId = edtx_cutomer.getText().toString();
        String type = txvw_type.getTag().toString();

        if (name.isEmpty()){
            Utility.showToastError(mActivity,"Nama harus diisi!");
            return;
        }

        if (customerId.isEmpty()){
            Utility.showToastError(mActivity,"Nomor / Id Pelanggan harus diisi!");
            return;
        }
        LoadingDialog dialog = new LoadingDialog(mActivity);
        dialog.show();

        ContactFB contactFB = new ContactFB(mActivity);
        contactFB.member_id = mAgentId+"";
        contactFB.customer_id = customerId;
        contactFB.type = type;
        contactFB.name = name;
        contactFB.insert();
        contactFB.setOnInsertListener((status, message) -> {
            if (status == ErrorCode.OK){
                ContactDB contactDB = new ContactDB();
                contactDB.customerID = customerId;
                contactDB.name = name;
                contactDB.type = type;
                contactDB.insert(mActivity);

                dialog.dismiss();
                Utility.showToastSuccess(mActivity,"Kontak tersimpan");
                setResult(RESULT_OK);
                mActivity.finish();
            }
            else {
                Utility.showFailedDialog(mActivity,"Gagal menyimpan kontak");
            }
        });
    }

    private void showType(){
        if (getIntent().getStringExtra("customer_id") != null){
            return;
        }
        ChooserDialog dialog = new ChooserDialog(mActivity);
        for (Map.Entry<String, String> set : TYPE_DATA.entrySet()) {
            dialog.add(set.getKey(),set.getValue());
        }
        dialog.show("Pilih Tipe" );
        dialog.setOnSelectedListener(data -> {
            txvw_type.setText(data.getString("value"));
            txvw_type.setTag(data.getString("key"));
            String key = data.getString("key");
            setInputTypeID(key);
        });
    }

    private void setInputTypeID(String typeID){
        if (typeID.equals("1")){
            edtx_cutomer.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_CLASS_PHONE);
        }
        else if (typeID.equals("2")){
            edtx_cutomer.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        else {
            edtx_cutomer.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
        }

        edtx_cutomer.setText("");
    }


}
