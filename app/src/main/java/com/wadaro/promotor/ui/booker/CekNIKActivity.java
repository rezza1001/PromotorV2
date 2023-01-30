package com.wadaro.promotor.ui.booker;

import android.widget.TextView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class CekNIKActivity extends MyActivity {



    @Override
    protected int setLayout() {
        return R.layout.activity_cek_nik;
    }

    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Dta Koordinator");

        TextView txvw_ktp_00 = findViewById(R.id.txvw_ktp_00);
        TextView txvw_name_00 = findViewById(R.id.txvw_name_00);
        TextView txvw_nickname_00 = findViewById(R.id.txvw_nickname_00);
        TextView txvw_phone_00 = findViewById(R.id.txvw_phone_00);
        TextView txvw_address_00 = findViewById(R.id.txvw_address_00);
        TextView txvw_note_00 = findViewById(R.id.txvw_note_00);
        TextView txvw_cordinat_00 = findViewById(R.id.txvw_cordinat_00);

        try {
            JSONObject data = new JSONObject(Objects.requireNonNull(getIntent().getStringExtra("DATA")));
            txvw_ktp_00.setText(data.getString("coordinator_ktp"));
            txvw_name_00.setText(data.getString("coordinator_name"));
            txvw_nickname_00.setText(data.getString("coordinator_alias_name"));
            txvw_phone_00.setText(data.getString("coordinator_phone"));
            txvw_address_00.setText(data.getString("coordinator_address"));
            txvw_note_00.setText(data.getString("coordinator_note"));
            txvw_cordinat_00.setText(data.getString("coordinator_location"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        findViewById(R.id.btnSimpan).setOnClickListener(v -> {
            setResult(RESULT_OK, getIntent());
            mActivity.finish();
        });
        findViewById(R.id.btnBatal).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            mActivity.finish();
        });
    }
}
