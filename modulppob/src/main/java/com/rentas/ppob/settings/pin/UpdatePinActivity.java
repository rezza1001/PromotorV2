package com.rentas.ppob.settings.pin;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdatePinActivity extends MyActivity {
    private HeaderView header_view;
    private EditText edtx_newPin,edtx_confirm,edtx_oldPin;
    private ImageView imvw_show,imvw_confirm;
    private TextView txvw_olpPin,txvw_newPin;
    private RelativeLayout rvly_oldPin;

    private boolean isUpdate = false;

    @Override
    protected int setLayout() {
        return R.layout.setting_pin_activity_update;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("Buat PIN");

        RelativeLayout rvly_newPin = findViewById(R.id.rvly_newPin);
        rvly_newPin.setBackground(Utility.getShapeLine(mActivity,1,6, Color.parseColor("#062C56"), Color.WHITE));
        edtx_newPin = findViewById(R.id.edtx_newPin);
        imvw_show = findViewById(R.id.imvw_show);
        imvw_show.setTag(0);

        RelativeLayout rvly_confirm = findViewById(R.id.rvly_confirm);
        rvly_confirm.setBackground(Utility.getShapeLine(mActivity,1,6, Color.parseColor("#062C56"), Color.WHITE));
        edtx_confirm = findViewById(R.id.edtx_confirm);
        imvw_confirm = findViewById(R.id.imvw_confirm);
        imvw_confirm.setTag(0);

        txvw_olpPin = findViewById(R.id.txvw_olpPin);
        rvly_oldPin = findViewById(R.id.rvly_oldPin);
        edtx_oldPin = findViewById(R.id.edtx_oldPin);
        txvw_newPin = findViewById(R.id.txvw_newPin);
        txvw_olpPin.setVisibility(View.GONE);
        rvly_oldPin.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        isUpdate = getIntent().getBooleanExtra("update", false);
        if (isUpdate){
            txvw_olpPin.setVisibility(View.VISIBLE);
            rvly_oldPin.setVisibility(View.VISIBLE);
            txvw_newPin.setText("Pin Baru");
        }
    }

    @Override
    protected void initListener() {
        header_view.setOnBackListener(this::onBackPressed);
        imvw_show.setOnClickListener(v -> showPin(imvw_show,edtx_newPin));
        imvw_confirm.setOnClickListener(v -> showPin(imvw_confirm,edtx_confirm));
    }

    private void showPin(ImageView imageView, EditText editText){
        if (imageView.getTag().toString().equals("0")){
            imageView.setTag(1);
            imageView.setImageResource(R.drawable.ic_eye_on);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        else {
            imageView.setTag(0);
            imageView.setImageResource(R.drawable.ic_eye_off);
            int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
            editText.setInputType(inputType);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        editText.setSelection(editText.getText().toString().length());
    }

    public void save(View v){
        String newPin = edtx_newPin.getText().toString();
        String confirm = edtx_confirm.getText().toString();
        String old = edtx_oldPin.getText().toString();
        if (isUpdate){
            if (old.isEmpty()){
                Utility.showToastError(mActivity,"Pin lama harus diisi");
                return;
            }
            String pin = MainData.getPIN(mActivity);
            if (!pin.equals(old)){
                Utility.showToastError(mActivity,"Pin lama tidak valid");
                return;
            }
        }

        if (newPin.isEmpty()){
            Utility.showToastError(mActivity,"Pin baru harus diisi 6 karakter");
            return;
        }
        if (!newPin.equals(confirm)){
            Utility.showToastError(mActivity,"Konformasi PIN tidak benar");
            return;
        }


        PostManager post = new PostManager(mActivity, ConfigAPI.POST_UPDATE_PIN);
        post.addParam("agent_id",mAgentId);
        post.addParam("pin",confirm);
        post.addParam("repeat_pin",confirm);
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    MainData.setAccessToken(mActivity,data.getString("access_token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainData.setPIN(mActivity, newPin);
                Utility.showToastSuccess(mActivity,"Aktivasi PIN berhasil");
                setResult(RESULT_OK);
                mActivity.finish();
            }
            else {
                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showError("Gagal",message);
            }
        });
    }
}
