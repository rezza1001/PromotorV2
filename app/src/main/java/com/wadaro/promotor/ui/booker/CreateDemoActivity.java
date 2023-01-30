package com.wadaro.promotor.ui.booker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.FormPost;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.Global;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.BookingDB;
import com.wadaro.promotor.database.table.JpDB;
import com.wadaro.promotor.database.table.UserDB;
import com.wadaro.promotor.module.FailedWindow;
import com.wadaro.promotor.module.FileProcessing;
import com.wadaro.promotor.module.ImageResizer;
import com.wadaro.promotor.module.SuccessWindow;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.ui.demo.process.MapsAddrActivity;
import com.wadaro.promotor.ui.demo.process.SalesOrderActivity;
import com.wadaro.promotor.ui.demo.process.jp.ConsumerHolder;
import com.wadaro.promotor.util.FormEditext;
import com.wadaro.promotor.util.KtpValidator;
import com.wadaro.promotor.util.MySpinnerView;
import com.wadaro.promotor.util.MyTimeSpinner;
import com.wadaro.promotor.util.OperatorPrifix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CreateDemoActivity extends MyActivity {

    private static final int REQ_EXISTING_NIK = 11;
    private static final int TAKE_PHOTO_COORDINATOR = 12;
    private static final int TAKE_PHOTO_LOCATION = 13;
    private static final int REQ_TAGADDRESS = 14;

    private ImageButton imbt_calendar_00,imbt_time_00;
    private Button bbtn_tagaddress_00;
    private TextView txvw_longlat_00;
    private TextView txvw_demodate_00;
    private TextView txvw_timedemo_00;
    private TextView txvw_date_00;
    private EditText edtx_nik_00;
    private FormEditext edtx_name_00,edtx_nickname_00,edtx_phone_00,edtx_address_00,edtx_note_00;
    private MySpinnerView spnr_city_00,spnr_book_00,spnr_delivery_00;
    private RoundedImageView imvw_coordinator_00,imvw_location_00;

    private static final String photo_path      = "/Wadaro/prmotor/booking/";
    private static final String draft_path      = "/Wadaro/prmotor/draft/";

    private String mIdentity = "",mNik = "";
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected int setLayout() {
        return R.layout.booker_activity_demo;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        edtx_nik_00         = findViewById(R.id.edtx_nik_00);
        edtx_name_00        = findViewById(R.id.edtx_name_00);
        edtx_nickname_00    = findViewById(R.id.edtx_nickname_00);
        edtx_phone_00       = findViewById(R.id.edtx_phone_00);
        spnr_city_00        = findViewById(R.id.spnr_city_00);
        edtx_address_00     = findViewById(R.id.edtx_address_00);
        txvw_longlat_00     = findViewById(R.id.txvw_longlat_00);
        edtx_note_00        = findViewById(R.id.edtx_note_00);
        spnr_book_00        = findViewById(R.id.spnr_book_00);
        txvw_demodate_00    = findViewById(R.id.txvw_demodate_00);
        imbt_calendar_00    = findViewById(R.id.imbt_calendar_00);
        txvw_timedemo_00    = findViewById(R.id.txvw_timedemo_00);
        imbt_time_00        = findViewById(R.id.imbt_time_00);
        txvw_date_00        = findViewById(R.id.txvw_date_00);
        spnr_delivery_00        = findViewById(R.id.spnr_delivery_00);
        bbtn_tagaddress_00        = findViewById(R.id.bbtn_tagaddress_00);
        imvw_coordinator_00        = findViewById(R.id.imvw_coordinator_00);
        imvw_location_00        = findViewById(R.id.imvw_location_00);

        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Demo Dadakan");

        edtx_name_00.setTitle("Nama Koordinator(Sesuai KTP)");
        edtx_name_00.setMandatory();
        edtx_name_00.withoutSpecialChar();

        edtx_nickname_00.setTitle("Nama Panggilan");
        edtx_nickname_00.withoutSpecialChar();

        edtx_phone_00.setTitle("Nomor Telepon");
        edtx_phone_00.setInputType(InputType.TYPE_CLASS_PHONE);

        edtx_address_00.setTitle("Alamat Koordinator");

        edtx_note_00.setTitle("Patokan Lokasi");

        spnr_city_00.setTitle("Areal Garapan");

        spnr_book_00.setTitle("Jadwal Demo");

        spnr_delivery_00.setTitle("Tanggal Pengiriman");
    }

    @Override
    protected void initData() {
        DateFormat format = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
        txvw_date_00.setText(format.format(new Date()));

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        FileProcessing.createFolder(mActivity,photo_path);
        FileProcessing.createFolder(mActivity,draft_path);
        FileProcessing.clearImage(mActivity,photo_path);


        getData();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_check_00).setOnClickListener(v -> checkNik());

        findViewById(R.id.rvly_coordinator_00).setOnClickListener(v -> openCamera("coordinator", TAKE_PHOTO_COORDINATOR));
        findViewById(R.id.rvly_location_00).setOnClickListener(v -> openCamera("location", TAKE_PHOTO_LOCATION));
        findViewById(R.id.bbtn_cancel_00).setOnClickListener(v -> clearField());

        findViewById(R.id.bbtn_save_00).setOnClickListener(v -> audit());

        bbtn_tagaddress_00.setOnClickListener(v -> startActivityForResult(new Intent(mActivity, MapsAddrActivity.class), REQ_TAGADDRESS));
        imbt_calendar_00.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(mActivity),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        txvw_demodate_00.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        imbt_time_00.setOnClickListener(v -> {
            MyTimeSpinner spinner = new MyTimeSpinner(mActivity);
            spinner.show();
            spinner.setOnForceDismissListener((hour, minute) -> {
                mMinute = minute;
                mHour = hour;
                txvw_timedemo_00.setText(mHour + ":" + mMinute);
            });
            spinner.setValue(mHour, mMinute);
        });

    }

    private void getData(){
        if (offlineMode){
            String data = MyPreference.getString(mActivity, Global.PREF_DATA_BOOKER);
            try {
                JSONObject obj = new JSONObject(data);
                loadData(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        PostManager post = new PostManager(mActivity, Config.GET_SUDDEN_DEMO);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
               loadData(obj);
            }
        });
    }

    private void loadData(JSONObject obj){
        try {
            JSONObject data = obj.getJSONObject("data");
            JSONArray address = data.getJSONArray("address");

            ArrayList<Bundle> addressList = new ArrayList<>();
            Bundle bd = new Bundle();

            bd.putString("key","-1");
            bd.putString("value","Pilih Areal");
            addressList.add(bd);

            for (int i=0; i<address.length(); i++){
                Bundle bundle = new Bundle();
                bundle.putString("key",address.getJSONObject(i).getString("id"));
                bundle.putString("value",address.getJSONObject(i).getString("text"));
                addressList.add(bundle);
            }
            spnr_city_00.setDataBundle(addressList);

            JSONArray demo  = data.getJSONArray("schedule_demo");
            ArrayList<Bundle> demoList = new ArrayList<>();
            for (int i=0; i<demo.length(); i++){
                Bundle bundle = new Bundle();
                bundle.putString("key",demo.getJSONObject(i).getString("id"));
                bundle.putString("value",demo.getJSONObject(i).getString("text"));
                demoList.add(bundle);
            }
            spnr_book_00.setDataBundle(demoList);

            ArrayList<String> planList = new ArrayList<>();
            JSONArray plan  = data.getJSONArray("delivery_plan");
            for (int i=0; i<plan.length(); i++){
                planList.add(plan.getJSONObject(i).getString("start"));
            }
            planList.add("2021-12-14");
            spnr_delivery_00.setData(planList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkNik(){
        if (edtx_nik_00.getText().toString().isEmpty()){
            Utility.showToastError(mActivity,"NIK/No.KTP harus diisi!");
            return;
        }

        PostManager post = new PostManager(mActivity,"coordinator/"+edtx_nik_00.getText().toString()+"/check-nik");
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    Intent intent = new Intent(mActivity, CekNIKActivity.class);
                    intent.putExtra("DATA",data.toString());
                    startActivityForResult(intent, REQ_EXISTING_NIK);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EXISTING_NIK && resultCode == -1){
            clearField();
            try {
                assert data != null;
                JSONObject obj = new JSONObject(Objects.requireNonNull(data.getStringExtra("DATA")));
                edtx_name_00.setValue(obj.getString("coordinator_name"));
                edtx_nickname_00.setValue(obj.getString("coordinator_alias_name"));
                edtx_phone_00.setValue(obj.getString("coordinator_phone"));
                edtx_note_00.setValue(obj.getString("coordinator_note"));
                mIdentity   = obj.getString("coordinator_ktp");
                mNik        = obj.getString("coordinator_id");
                edtx_nik_00.setText(mNik);

                edtx_address_00.setValue(obj.getString("coordinator_address"));
                JSONObject salesArea = obj.getJSONObject("sales_area");
                spnr_city_00.setSelection(salesArea.getString("id"));
                txvw_longlat_00.setText(obj.getString("coordinator_location"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == TAKE_PHOTO_COORDINATOR && resultCode == -1){
            Utility.showToastSuccess(mActivity,"Photo saved to draf");
            Glide.with(mActivity).load(FileProcessing.openImage(mActivity,photo_path,"coordinator.jpg")).into(imvw_coordinator_00);
            ImageResizer resizer = new ImageResizer(mActivity);
            resizer.compress(photo_path+"coordinator.jpg",photo_path,"coordinator_compress.jpg");
        }
        else if (requestCode == TAKE_PHOTO_LOCATION && resultCode == -1){
            Utility.showToastSuccess(mActivity,"Photo saved to draf");
            Glide.with(mActivity).load(FileProcessing.openImage(mActivity,photo_path,"location.jpg")).into(imvw_location_00);
            ImageResizer resizer = new ImageResizer(mActivity);
            resizer.compress(photo_path+"location.jpg",photo_path,"location_compress.jpg");
        }
        else if (requestCode == REQ_TAGADDRESS && resultCode == -1){
            assert data != null;
            edtx_address_00.setValue(data.getStringExtra("ADDRESS"));
            txvw_longlat_00.setText(data.getStringExtra("LATITUDE")+","+data.getStringExtra("LONGITUDE"));
        }
    }

    private void audit(){

        String booking_demo = spnr_book_00.getKeySelected();
        String nik = edtx_nik_00.getText().toString();
        String name = edtx_name_00.getValue();
        String nick = edtx_nickname_00.getValue();
        String phone = edtx_phone_00.getValue();

        String tagAddress = edtx_address_00.getValue();
        String longlat = txvw_longlat_00.getText().toString();
        String note = edtx_note_00.getValue();
        String date = mYear+"-"+(mMonth+1)+"-"+mDay+" "+mHour+":"+mMinute+":00";

        String deliveryDate = spnr_delivery_00.getKeySelected();

        if (phone.isEmpty() && nik.isEmpty()){
            Utility.showToastError(mActivity,"No. KTP / No Telepon harus diisi");
            return;
        }

        if (!nik.isEmpty()){
            KtpValidator ktpValidator = new KtpValidator();
            if (!ktpValidator.valid(nik)){
                Utility.showToastError(mActivity,"No. KTP/NIK tidak valid!");
                return;
            }
        }
        if (!phone.isEmpty()){
            OperatorPrifix prifix = new OperatorPrifix();
            prifix.getInfo(phone);
            if (!prifix.isValidated()){
                Utility.showToastError(mActivity,"No. Telepon tidak valid!");
                return;
            }
            phone = prifix.getPhoneNumber();
        }
        else {
            phone = "-";
        }

        if (name.isEmpty()){
            Utility.showToastError(mActivity,"Nama harus diisi!");
            return;
        }

        String address = spnr_city_00.getKeySelected();
        if (address.isEmpty() || address.equals("-1")) {
            Utility.showToastError(mActivity,"Areal harus diisi!");
            return;
        }
        if (tagAddress.isEmpty() || longlat.isEmpty()){
            Utility.showToastError(mActivity,"Tagging alamat harus diisi!");
            return;
        }

        if (FileProcessing.openImage(mActivity,photo_path+"coordinator_compress.jpg") == null){
            Utility.showToastError(mActivity,"Silahkan masukan foto koordinator!");
            return;
        }
        if (FileProcessing.openImage(mActivity,photo_path+"location_compress.jpg") == null){
            Utility.showToastError(mActivity,"Silahkan masukan foto lokasi!");
            return;
        }

        if (mNik.isEmpty()){
            mNik = nik;
            mIdentity = nik;
        }

        if (nick.isEmpty()){
            nick = "-";
        }
        if (note.isEmpty()){
            note = "-";
        }
        if (offlineMode){
            saveToLocal(mIdentity,name,nick,phone,address,note,date,longlat, booking_demo,tagAddress, deliveryDate);
        }
        else {
            saveToServer(mIdentity,name,nick,phone,address,note,date,longlat, booking_demo,tagAddress, deliveryDate);
        }
    }

    private void saveToServer(String identity, String cName, String cNick, String cPhone,
                              String cAddress, String note, String date, String longlat, String booking, String tagaddress, String sendDate){

        File sd     = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            sd = mActivity.getExternalFilesDir("");
        }

        FormPost post = new FormPost(mActivity,Config.API_TOKEN_SUDDEN_DEMO);
        post.addParam(new ObjectApi("booking_demo", booking));
        post.addParam(new ObjectApi("booking_date", date));
        post.addParam(new ObjectApi("delivery_date",sendDate));
        post.addParam(new ObjectApi("coordinator_ktp",identity));
        post.addParam(new ObjectApi("coordinator_name",cName));
        post.addParam(new ObjectApi("coordinator_alias_name",cNick));
        post.addParam(new ObjectApi("coordinator_phone",cPhone));
        post.addParam(new ObjectApi("coordinator_address",tagaddress));
        post.addParam(new ObjectApi("coordinator_location",longlat));
        post.addParam(new ObjectApi("coordinator_note",note));
        post.addParam(new ObjectApi("sales_area_id",cAddress));
        post.addImage("photo_coordinator",sd.getAbsolutePath()+photo_path+"coordinator_compress.jpg");
        post.addImage("photo_location",sd.getAbsolutePath()+photo_path+"location_compress.jpg");
        post.execute();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    String bookingId = data.getString("booking_id");
                    SuccessWindow successWindow = new SuccessWindow(mActivity);
                    successWindow.setDescription("Data berhasil disimpan!");
                    successWindow.show();
                    successWindow.setOnFinishListener(() -> {
                        Intent intent = new Intent(mActivity, SalesOrderActivity.class);
                        intent.putExtra("booking_id", bookingId);
                        startActivity(intent);
                        mActivity.finish();
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else {
                FailedWindow failedWindow = new FailedWindow(mActivity);
                failedWindow.setDescription(message);
                failedWindow.show();
            }
        });
    }

    private void saveToLocal(String identity, String cName, String cNick, String cPhone, String cAddress, String note, String date, String longlat, String booking, String tagaddress, String sendDate){
        BookingDB bookingDB = new BookingDB();
//        employee_id/tgl/bulan/thn/jammenit
        Calendar calendar = Calendar.getInstance();
        UserDB userDB = new UserDB();
        userDB.getData(mActivity);
        bookingDB.id = userDB.employee_id+"/"+calendar.get(Calendar.DATE)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MINUTE)+"/"+calendar.get(Calendar.SECOND);
        String path_file = draft_path+"photo_"+bookingDB.id+"/";
        FileProcessing.createFolder(mActivity,path_file);
        new Handler().postDelayed(() -> {
            FormPost post = new FormPost(mActivity,"booking");
            post.addParam(new ObjectApi("booking_id",bookingDB.id));
            post.addParam(new ObjectApi("booking_demo",booking));
            post.addParam(new ObjectApi("booking_date",date));
            post.addParam(new ObjectApi("delivery_date",sendDate));
            post.addParam(new ObjectApi("coordinator_ktp",identity));
            post.addParam(new ObjectApi("coordinator_name",cName));
            post.addParam(new ObjectApi("coordinator_alias_name",cNick));
            post.addParam(new ObjectApi("coordinator_phone",cPhone));
            post.addParam(new ObjectApi("coordinator_address",tagaddress));
            post.addParam(new ObjectApi("coordinator_location",longlat));
            post.addParam(new ObjectApi("coordinator_note",note));
            post.addParam(new ObjectApi("sales_area_id",cAddress));
            post.addParam(new ObjectApi("sales_area_id",cAddress));
            post.addParam(new ObjectApi("mode","ofline"));

            File sd     = Environment.getExternalStorageDirectory();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                sd = mActivity.getExternalFilesDir("");
            }
            post.addImage("photo_coordinator",sd.getAbsolutePath()+path_file+"coordinator_compress.jpg");
            post.addImage("photo_location",sd.getAbsolutePath()+path_file+"location_compress.jpg");

            FileProcessing fileProcessing = new FileProcessing();
            fileProcessing.saveToTmp(mActivity,FileProcessing.openImage(mActivity,photo_path+"coordinator_compress.jpg"),path_file,"coordinator_compress.jpg");
            fileProcessing.saveToTmp(mActivity,FileProcessing.openImage(mActivity,photo_path+"location_compress.jpg"),path_file,"location_compress.jpg");

            bookingDB.data = post.getData().toString();
            bookingDB.images = post.getImages().toString();
            bookingDB.insert(mActivity);
            saveJP(bookingDB.id ,cName, cPhone,tagaddress);
            Utility.showToastSuccess(mActivity, "Data disimpan ke draft");

            Intent intent = new Intent(mActivity, SalesOrderActivity.class);
            intent.putExtra("booking_id", bookingDB.id);
            startActivity(intent);
            mActivity.finish();

        },1000);
    }

    private void saveJP(String id, String name,String phone, String address){
        PostManager post = new PostManager(mActivity,Config.CREATE_DEMO_DATA_JP);
        post.addParam(new ObjectApi("name",name));
        post.addParam(new ObjectApi("phone",phone));
        post.addParam(new ObjectApi("address",address));
        post.addParam(new ObjectApi("ktp","-"));
        JpDB jp = new JpDB();
        jp.id           = jp.getNextID(mActivity) + 1;
        jp.bookingID    = id+"";
        jp.data         = post.getData().toString();
        jp.insert(mActivity);

    }

    private void clearField(){
        mNik = "";
        mIdentity = "";
        edtx_nik_00.setText(null);
        edtx_name_00.setValue("");
        edtx_nickname_00.setValue("");
        edtx_address_00.setValue("");
        edtx_note_00.setValue("");
        edtx_phone_00.setValue("");
        FileProcessing.clearImage(mActivity,photo_path);
        Glide.with(mActivity).load(0).into(imvw_coordinator_00);
        Glide.with(mActivity).load(0).into(imvw_location_00);
        imvw_coordinator_00.setImageResource(0);
        imvw_location_00.setImageResource(0);
    }

    private void openCamera(String name, int reqID){
        String[] PERMISSIONS = {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!hasPermissions(mActivity, PERMISSIONS)){
            ActivityCompat.requestPermissions(Objects.requireNonNull(mActivity), PERMISSIONS, 101);
        }
        else {
            String mediaPath = Environment.getExternalStorageDirectory()+photo_path;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                mediaPath = mActivity.getExternalFilesDir("").getAbsolutePath()+photo_path;
            }
            String file =mediaPath+ name+".jpg";
            File newfile = new File(file);
            try {
                newfile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Uri outputFileUri = FileProcessing.getUriFormFile(mActivity, newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            startActivityForResult(cameraIntent, reqID);
        }
    }
    private boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
