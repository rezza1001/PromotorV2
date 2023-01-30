package com.wadaro.promotor.ui.auth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.Global;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.UserDB;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.ui.home.HomePageActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseUi implements View.OnClickListener{

    TextView tvLupaPassword;
    Button btnLogin;
    private EditText edtx_username_00, edtx_password_00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtx_username_00 = findViewById(R.id.edtx_username_00);
        edtx_password_00 = findViewById(R.id.edtx_password_00);
        tvLupaPassword = findViewById(R.id.tvLupaPassword);
        tvLupaPassword.setOnClickListener(this);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

//        edtx_username_00.setText("DUMMYPROM");
//        edtx_password_00.setText("Jakarta2020");
        requestPemission();


        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView txvw_version_00 = findViewById(R.id.txvw_version_00);
            txvw_version_00.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {

        if (isFastDoubleClick()) return;

        if(view.getId() == R.id.tvLupaPassword){
            startActivity(new Intent(getApplicationContext(), ForgotPwdActivity.class));
        } else if(view.getId() == R.id.btnLogin){
            login();
        }
    }

    private void login(){
        if (edtx_username_00.getText().toString().isEmpty()){
            Utility.showToastError(this,"Username Harus diisi!");
            return;
        }
        if (edtx_password_00.getText().toString().isEmpty()){
            Utility.showToastError(this,"Password Harus diisi!");
            return;
        }

        PostManager post = new PostManager(this, Config.API_LOGIN);
        post.addParam(new ObjectApi("user_name",edtx_username_00.getText().toString()));
        post.addParam(new ObjectApi("user_pass",edtx_password_00.getText().toString()));
        post.execute("POST");
        post.showloading(false);
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK_200){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    MyPreference.save(LoginActivity.this, Global.DATA_LOGIN, data.toString());
                    UserDB userDB = new UserDB();
                    userDB.user_id = data.getString("user_id");
                    userDB.group = data.getString("user_group");
                    userDB.company_id = data.getString("company_id");
                    userDB.branch_id = data.getString("branch_id");
                    userDB.employee_id = data.getString("employee_id");
                    userDB.company_name = data.getString("company_name");
                    userDB.branch_name = data.getString("branch_name");
                    userDB.branch_type = data.getString("branch_type");
                    userDB.organization_id = data.getString("organization_id");
                    userDB.name = data.getString("employee_name");
                    userDB.phone = data.getString("employee_phone");
                    userDB.email = data.getString("employee_email");
                    userDB.photo = data.getString("employee_photo");
                    userDB.user_name = data.getString("user_name");
                    userDB.token = obj.getJSONObject("meta").getString("_token");
                    MyPreference.save(LoginActivity.this,"password", edtx_password_00.getText().toString());
                    userDB.insert(LoginActivity.this);
                    startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                    LoginActivity.this.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Utility.showToastError(LoginActivity.this,"Gagal Login");
            }

        });
    }


    private void requestPemission(){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET};


        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }


}
