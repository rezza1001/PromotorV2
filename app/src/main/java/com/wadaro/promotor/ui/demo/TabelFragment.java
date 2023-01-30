package com.wadaro.promotor.ui.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.FormPost;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.Global;
import com.wadaro.promotor.base.MyFragment;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.BookingDB;
import com.wadaro.promotor.database.table.SalesOrderDB;
import com.wadaro.promotor.module.FailedWindow;
import com.wadaro.promotor.module.FileProcessing;
import com.wadaro.promotor.module.SuccessWindow;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.ui.demo.process.DetailDemoActivity;
import com.wadaro.promotor.ui.product.PrviewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class TabelFragment extends MyFragment {

    private ArrayList<DemoHolder> allData = new ArrayList<>();
    private DemoAdapter mAdapkter;
    private TextView txvw_size_00;
    private RoundedImageView imvw_denah_00;
    private static final String photo_path      = "/Wadaro/booking/";

    @Override
    protected int setlayout() {
        return R.layout.fragment_tabel;
    }

    @Override
    protected void initLayout(View view) {

        RecyclerView rcvw_data_00 = view.findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_data_00.setNestedScrollingEnabled(false);

        mAdapkter = new DemoAdapter(mActivity, allData);
        rcvw_data_00.setAdapter(mAdapkter);

        txvw_size_00 = view.findViewById(R.id.txvw_size_00);
        imvw_denah_00 = view.findViewById(R.id.imvw_denah_00);
        imvw_denah_00.setTag("");

        view.findViewById(R.id.bbtn_upload_00).setOnClickListener(v -> performFileSearch());
    }

    @Override
    protected void initListener() {
        mAdapkter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, DetailDemoActivity.class);
            intent.putExtra("booking_id", data.id+"");
            intent.putExtra("offline_booking", false);
            if (!data.offlineId.isEmpty()){
                intent.putExtra("booking_id", data.offlineId+"");
                intent.putExtra("offline_booking", true);
            }
            mActivity.startActivity(intent);
        });

        imvw_denah_00.setOnClickListener(v -> {
            if (!imvw_denah_00.getTag().toString().isEmpty()){
                Intent intent = new Intent(mActivity, PrviewActivity.class);
                intent.putExtra("title","Preview Denah");
                intent.putExtra("url",imvw_denah_00.getTag().toString());
                startActivity(intent);
            }

        });
    }

    @Override
    protected void initData() {
        FileProcessing.createFolder(mActivity,photo_path);
        FileProcessing.clearImage(mActivity,photo_path);
    }

    private void loadData(){
        allData.clear();
        if (MyPreference.getInt(mActivity, Global.PREF_OFFLINE_MODE) == 1){
            String dataAssignment = MyPreference.getString(mActivity,Global.PREF_DATA_PROCESS_DEMO);
            try {
                if (!dataAssignment.isEmpty()){
                    build(new JSONObject(dataAssignment));
                }
                loadOfflineDB();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        PostManager post = new PostManager(mActivity,Config.GET_DEMO_DATA);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                build(obj);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void build(JSONObject obj){
        JSONArray dataMaps = new JSONArray();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            DateFormat dfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
            JSONObject data = obj.getJSONObject("data");
            JSONArray booking = data.getJSONArray("booking");

            for (int i=0; i<booking.length(); i++){
                JSONObject objData = booking.getJSONObject(i);
                DemoHolder demo = new DemoHolder();
                demo.id = objData.getString("booking_id");
                SalesOrderDB salesOrderDB = new SalesOrderDB();
                salesOrderDB.getData(mActivity, demo.id);
                if (!salesOrderDB.bookingId.isEmpty()){
                    continue;
                }

                demo.demo = objData.getString("booking_demo");
                demo.coordinator = objData.getString("coordinator_name");
                demo.address = objData.getString("coordinator_address");
                Date bookDate = df.parse(objData.getString("booking_date"));
                if (bookDate != null){
                    demo.time = dfTime.format(bookDate);
                }
                demo.note = objData.getString("coordinator_note");
                allData.add(demo);

                if (objData.getString("coordinator_location").split(",").length> 1){
                    String latitude = objData.getString("coordinator_location").split(",")[0];
                    String longitude = objData.getString("coordinator_location").split(",")[1];
                    JSONObject joMap = new JSONObject();
                    joMap.put("latitude",latitude);
                    joMap.put("longitude",longitude);
                    joMap.put("number",i);
                    joMap.put("booking_status",objData.getString("booking_status"));
                    joMap.put("booking_demo",objData.getString("booking_demo"));
                    joMap.put("booking_id",objData.getString("booking_id"));
                    joMap.put("company_name",objData.getString("company_name"));
                    dataMaps.put(joMap);
                }
            }
            if (!data.isNull("denah")){
                Glide.with(mActivity).load(Config.IMAGE_PATH_BOOKER+data.getString("denah")).into(imvw_denah_00);
                imvw_denah_00.setTag(Config.IMAGE_PATH_BOOKER+data.getString("denah"));
            }
            mAdapkter.notifyDataSetChanged();
            txvw_size_00.setText("Total ada "+allData.size()+" titik demo");

            if (dataMaps.length() > 0){
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent("LOAD_DATA");
                    intent.putExtra("data", dataMaps.toString());
                    Objects.requireNonNull(getActivity()).sendBroadcast(intent);
                },1000);

            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadOfflineDB(){
        BookingDB db = new BookingDB();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        DateFormat dfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        for (BookingDB bookingDB: db.getData(mActivity)){
            try {
                JSONObject data = new JSONObject(bookingDB.data);
                DemoHolder demo = new DemoHolder();
                demo.id         = bookingDB.id;
                demo.offlineId  = bookingDB.id;
                demo.address    = data.getString("coordinator_address");
                demo.demo       = data.getString("booking_demo");
                demo.note       = data.getString("coordinator_note");
                demo.coordinator = data.getString("coordinator_name");
                Date bookDate   = df.parse(data.getString("booking_date"));
                demo.time       = dfTime.format(bookDate);
                SalesOrderDB soDB = new SalesOrderDB();
                soDB.getData(mActivity, bookingDB.id);
                if (soDB.id.isEmpty()){
                    allData.add(demo);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        mAdapkter.notifyDataSetChanged();
    }

    private static final int READ_REQUEST_CODE = 42;
    private void performFileSearch() {

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(),uri);
                    FileProcessing fp = new FileProcessing();
                    fp.saveToTmp(mActivity,bitmap,photo_path,"denah.jpeg");
                    fp.setOnSavedListener((path, name) -> uploadDenah());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("PARAMETER");
        intentFilter.addAction("REFRESH");
        mActivity.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mActivity.unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadDenah(){
        if (FileProcessing.openImage(mActivity,photo_path,"denah.jpeg") == null){
            Utility.showToastError(mActivity,"Silahkan pilih Gambar Denah!");
            return;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        File sd     = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            sd = mActivity.getExternalFilesDir("");
        }
        FormPost post = new FormPost(mActivity,Config.UPLOAD_DENAH);
        post.addParam(new ObjectApi("booking_date",df.format(new Date())));
        post.addImage("photo_denah",sd.getAbsolutePath()+photo_path+"denah.jpeg");
        post.execute();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                SuccessWindow successWindow = new SuccessWindow(mActivity);
                successWindow.setDescription("Denah berhasil di upload!");
                successWindow.show();
                successWindow.setOnFinishListener(() -> {
                    FileProcessing.clearImage(mActivity,photo_path);
                    try {
                        JSONObject data = obj.getJSONObject("data");
                        Glide.with(mActivity).load(Config.IMAGE_PATH_BOOKER+data.getString("photo_denah")).into(imvw_denah_00);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
            else {
                FailedWindow failedWindow = new FailedWindow(mActivity);
                failedWindow.setDescription(message);
                failedWindow.show();
            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("REFRESH")){
                loadData();
            }
        }
    };

}
