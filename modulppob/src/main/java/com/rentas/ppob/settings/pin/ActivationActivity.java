package com.rentas.ppob.settings.pin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.rentas.ppob.OtpActivity;
import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.data.ActivationData;
import com.rentas.ppob.libs.OperatorPrefix;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyActivity;
import com.rentas.ppob.master.MyPreference;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

public class ActivationActivity extends MyActivity {
    private static final int RC_SIGN_IN = 1;

    private HeaderView header_view;
    private EditText edtx_newPin,edtx_confirm,edtx_phone,edtx_name,txvw_email;
    private ImageView imvw_show,imvw_confirm;
    private RelativeLayout rvly_phone;
    private ImageView imvw_phone,imvw_email;

    private GoogleSignInClient mGoogleSignInClient;
    private final HashMap<String, Boolean> pinCombine = new HashMap<>();

    @Override
    protected int setLayout() {
        return R.layout.setting_pin_activity_create;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        header_view.create("Aktivasi PPOB");

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

        txvw_email = findViewById(R.id.txvw_email);
        RelativeLayout rvly_email = findViewById(R.id.rvly_email);
        rvly_email.setBackground(Utility.getShapeLine(mActivity,1,6, Color.parseColor("#062C56"), Color.WHITE));

        rvly_phone = findViewById(R.id.rvly_phone);
        rvly_phone.setBackground(Utility.getShapeLine(mActivity,1,6, Color.parseColor("#062C56"), Color.WHITE));

        edtx_phone = findViewById(R.id.edtx_phone);
        imvw_phone = findViewById(R.id.imvw_phone);
        imvw_phone.setTag(0);

        RelativeLayout rvly_name = findViewById(R.id.rvly_name);
        rvly_name.setBackground(Utility.getShapeLine(mActivity,1,6, Color.parseColor("#062C56"), Color.WHITE));
        edtx_name = findViewById(R.id.edtx_name);

        imvw_email = findViewById(R.id.imvw_email);
    }

    @Override
    protected void initData() {
        edtx_name.setEnabled(false);
        edtx_name.setText(MyPreference.getString(mActivity,"WADARO_NAME"));
        loadPinCombine();

        IntentFilter intentFilter = new IntentFilter("OTP_VALID");
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void initListener() {
        header_view.setOnBackListener(this::onBackPressed);
        imvw_show.setOnClickListener(v -> showPin(imvw_show,edtx_newPin));
        imvw_confirm.setOnClickListener(v -> showPin(imvw_confirm,edtx_confirm));

        imvw_email.setOnClickListener(v -> checkAccountGoogle());

        edtx_phone.addTextChangedListener(onPhoneChange);

        findViewById(R.id.lnly_email).setOnClickListener(v -> send("email"));
        findViewById(R.id.lnly_wa).setOnClickListener(v -> send("phone"));
    }

    private void loadPinCombine(){
        try {
            JSONArray ja = new JSONArray(Utility.getJsonFromAssets(mActivity, "PinCombination.json"));
            for (int i=0; i<ja.length(); i++){
                pinCombine.put(ja.getString(i), true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void send(String otpType){
        String newPin = edtx_newPin.getText().toString();
        String confirm = edtx_confirm.getText().toString();
        String phone = edtx_phone.getText().toString();
        String email = txvw_email.getText().toString();
        String name = edtx_name.getText().toString();

        OperatorPrefix prefix = new OperatorPrefix();
        prefix.getInfo(phone);

        if (newPin.isEmpty()){
            Utility.showToastError(mActivity,"Pin baru harus diisi 6 karakter");
            return;
        }
        if (pinCombine.get(newPin) != null){
            Utility.showToastError(mActivity,"Kombinasi pin terlalu mudah. Silahkan ubah pin");
            return;
        }
        if (name.isEmpty()){
            Utility.showToastError(mActivity,"Nama harus diisi");
            return;
        }
        if (!newPin.equals(confirm)){
            Utility.showToastError(mActivity,"Konformasi PIN tidak benar");
            return;
        }
        if (email.isEmpty()){
            Utility.showToastError(mActivity,"Email tidak valid");
            return;
        }
        if (!prefix.isValidated()){
            Utility.showToastError(mActivity,"Nomot handphone tidak valid");
            return;
        }

        PostManager post = new PostManager(mActivity, ConfigAPI.POST_REGISTER);
        post.addParam("phone",prefix.getPhoneNumber());
        post.addParam("email",email);
        post.addParam("pin",confirm);
        post.addParam("agent_code",getIntent().getStringExtra("employee_id"));
        post.addParam("partner_code",getIntent().getStringExtra("company_id"));
        post.addParam("agent_name",name);
        post.addParam("otp_type",otpType);
        post.executePOST();
        post.setOnReceiveListener((obj, code, message) -> {
            Utility.SendLogLogin(ConfigAPI.POST_REGISTER,post.getData().toString(),obj.toString(),getIntent().getStringExtra("employee_id"));
            if (code == ErrorCode.OK){
                ActivationData activationData = new ActivationData();
                activationData.setName(name);
                activationData.setPhone(prefix.getPhoneNumber());
                activationData.setEmail(email);
                activationData.setPin(confirm);
                activationData.setOtpType(otpType);
                activationData.setAgentCode(getIntent().getStringExtra("employee_id"));
                activationData.setPartnerCode(getIntent().getStringExtra("company_id"));

                Intent intent = new Intent(mActivity, OtpActivity.class);
                intent.putExtra("data", activationData);
                startActivity(intent);
            }
            else {
                DynamicDialog dialog = new DynamicDialog(mActivity);
                dialog.showError("Gagal",message);
            }


        });
    }

    private void checkAccountGoogle(){
        resetAccountGoogle();
        regGoogleAccount();
    }
    private void resetAccountGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.app_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult "+ resultCode);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            txvw_email.setText(account.getEmail());
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void regGoogleAccount(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.app_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null){
            signInToGoogle();
        }
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(this, this::handleSignInResult);

    }

    private void signInToGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private final TextWatcher onPhoneChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() <= 8){
                if (imvw_phone.getTag().toString().equals("1")){
                    imvw_phone.setTag(0);
                    imvw_phone.setColorFilter(getResources().getColor(R.color.font_color));
                    rvly_phone.setBackground(Utility.getShapeLine(mActivity,1,6, Color.parseColor("#062C56"), Color.WHITE));
                }
                return;
            }
            OperatorPrefix prefix = new OperatorPrefix();
            prefix.getInfo(s.toString());
            if (prefix.isValidated()){
                if (imvw_phone.getTag().toString().equals("1")){
                    imvw_phone.setTag(0);
                    imvw_phone.setColorFilter(getResources().getColor(R.color.font_color));
                    rvly_phone.setBackground(Utility.getShapeLine(mActivity,1,6, Color.parseColor("#062C56"), Color.WHITE));
                }
            }
            else {
                if (imvw_phone.getTag().toString().equals("0")){
                    imvw_phone.setTag(1);
                    imvw_phone.setColorFilter(getResources().getColor(R.color.error));
                    rvly_phone.setBackground(Utility.getShapeLine(mActivity,1,6, getResources().getColor(R.color.error), Color.WHITE));
                }

            }
        }
    };

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("OTP_VALID")){
                mActivity.finish();
            }
        }
    };
}
