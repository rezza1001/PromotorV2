package com.wadaro.promotor.ui.demo.process;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.component.WarningWindow;
import com.wadaro.promotor.database.table.JpDB;
import com.wadaro.promotor.database.table.ProductDB;
import com.wadaro.promotor.database.table.SalesOrder2DB;
import com.wadaro.promotor.database.table.TempDB;
import com.wadaro.promotor.module.FailedWindow;
import com.wadaro.promotor.module.FileProcessing;
import com.wadaro.promotor.module.ImageResizer;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.ui.demo.process.jp.AddJPActivity;
import com.wadaro.promotor.ui.order.InfoView;
import com.wadaro.promotor.util.FormEditext;
import com.wadaro.promotor.util.KtpValidator;
import com.wadaro.promotor.util.MySpinnerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SalesOrder2Activity extends MyActivity {
    private static final int TAKE_PHOTO_COORDINATOR = 12;
    private static final int REQ_ADD_JP = 13;

    private LinearLayout lnly_info_00,lnly_ktp_00;
    private RelativeLayout rvly_coordinator_00;
    private FormEditext edtx_identity_00,edtx_fullname_00;
    private MySpinnerView spnr_jp_00,spnr_product_00,spnr_qty_00;
    private RoundedImageView imvw_coordinator_00;

    private ArrayList<Bundle> mOrderData = new ArrayList<>();
    private OrderAdapter  mOrderAdapter;

    private String mBookingID = "";
    private String deliveryDate = "";
    private String salesOrder = "";
    private String note = "";

    private static final String photo_path      = "/Wadaro/promotor/";

    @Override
    protected int setLayout() {
        return R.layout.demo_process_activity_salesorder2;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Sales Order");

        lnly_info_00 = findViewById(R.id.lnly_info_00);

        edtx_fullname_00 = findViewById(R.id.edtx_fullname_00);
        edtx_fullname_00.setTitle("Nama Lengkap");

        edtx_identity_00 = findViewById(R.id.edtx_identity_00);
        edtx_identity_00.setTitle("No. KTP");
        edtx_identity_00.setInputType(InputType.TYPE_CLASS_PHONE);

        spnr_jp_00      = findViewById(R.id.spnr_jp_00);
        spnr_jp_00.setTitle("Nama JP/Calon Konsumen");
        spnr_product_00 = findViewById(R.id.spnr_product_00);
        spnr_product_00.setTitle("Nama Barang");
        spnr_qty_00     = findViewById(R.id.spnr_qty_00);
        spnr_qty_00.setTitle("QTY");

        imvw_coordinator_00 = findViewById(R.id.imvw_coordinator_00);
        rvly_coordinator_00 = findViewById(R.id.rvly_coordinator_00);
        lnly_ktp_00 = findViewById(R.id.lnly_ktp_00);

        RecyclerView rcvw_product_00 = findViewById(R.id.rcvw_product_00);
        rcvw_product_00.setLayoutManager(new LinearLayoutManager(mActivity));
        mOrderAdapter = new OrderAdapter(mActivity, mOrderData);
        rcvw_product_00.setAdapter(mOrderAdapter);

    }

    @Override
    protected void initData() {
        mBookingID = getIntent().getStringExtra("booking_id");
        salesOrder = getIntent().getStringExtra("sales_order");
        deliveryDate = getIntent().getStringExtra("delivery_date");
        note = getIntent().getStringExtra("note");
        FileProcessing.createFolder(mActivity,photo_path);
        FileProcessing.clearImage(mActivity,photo_path);

        ArrayList<String> qtys = new ArrayList<>();
        qtys.add("1");
        qtys.add("2");
        spnr_qty_00.setData(qtys);

        if (offlineMode){
            requestFromLocal();
        }
        else {
            requestFromServer();
        }
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_cancel_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_save_00).setOnClickListener(v -> {
            if (offlineMode){
                sendBroadcast(new Intent("FINISH"));
                sendBroadcast(new Intent("REFRESH"));
                Utility.showToastSuccess(mActivity,"Order Selesai");
                new Handler().postDelayed(() -> mActivity.finish(),200);
                return;
            }
            Intent intent = getIntent();
            intent.setClass(mActivity, SummaryOrderActivity.class);
            startActivity(intent);
        });
        rvly_coordinator_00.setOnClickListener(v -> openCamera("ktp", TAKE_PHOTO_COORDINATOR));
        findViewById(R.id.txvw_addjp_00).setOnClickListener(v -> {
            Intent intent = getIntent();
            intent.setClass(mActivity, AddJPActivity.class);
            startActivityForResult(intent,REQ_ADD_JP);
        });
        findViewById(R.id.txvw_addorder_00).setOnClickListener(v -> {
            WarningWindow warning = new WarningWindow(mActivity);
            warning.show("Tambah Order","Anda yakin untuk menambahkan order?");
            warning.setOnSelectedListener(status -> {
                if (status == 2){
                    addSalesOrder();
                }
            });
        });

        spnr_jp_00.setOnSelectedListener((key, value) -> {
            JSONObject others = spnr_jp_00.getOtherData();
            try {
                edtx_fullname_00.setValue(others.getString("consumen_name"));
                if (!others.has("consumen_picture") && !others.has("consumen_ktp") ){
                    edtx_identity_00.setValue("");
                    lnly_ktp_00.setVisibility(View.VISIBLE);
                    edtx_identity_00.setVisibility(View.VISIBLE);
                    return;
                }

                String imageKtp = "";
                if (others.has("consumen_picture")){
                    imageKtp     = others.getString("consumen_picture");
                }

                String ktpNumber    = others.getString("consumen_ktp");
                if (!imageKtp.isEmpty() && !imageKtp.equals("-") && !imageKtp.equals("null")){
                    lnly_ktp_00.setVisibility(View.GONE);
                }
                if (!ktpNumber.isEmpty() && !ktpNumber.equals("null")  && !ktpNumber.equals("-")){
                    edtx_identity_00.setValue(ktpNumber);
                }
                else {
                    edtx_identity_00.setValue("");
                    lnly_ktp_00.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mOrderAdapter.setOnOnDeleteListener(data -> {
            WarningWindow warning = new WarningWindow(mActivity);
            warning.show("Hapus Order","Anda yakin untuk menghapus produk "+data.getString("product_name")+" dari pemesan "+data.getString("consumen_name")+"?");
            warning.setOnSelectedListener(status -> {
                if (status == 2){
                    deleteOrder(data);
                }
            });
        });
    }

    private void requestFromServer(){
        lnly_info_00.removeAllViews();
        mOrderData.clear();
        buildInfo("Sales Order", salesOrder);
        buildInfo("Tanggal Kirim", deliveryDate);
        buildInfo("Keterangan", note);
        addLine();
        String param = "?booking_id="+mBookingID+"&sales_id="+salesOrder;
        PostManager post = new PostManager(mActivity, Config.SALES_ORDER_DETAIL+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    JSONArray consumers = data.getJSONArray("consumers");

                    JSONArray products = data.getJSONArray("products");
                    ArrayList<Bundle> prodBundle = new ArrayList<>();
                    for (int i=0; i<products.length(); i++){
                        JSONObject prod = products.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", prod.getString("product_id"));
                        bundle.putString("value", prod.getString("product_name"));
                        prodBundle.add(bundle);
                    }
                    spnr_product_00.setDataBundle(prodBundle);

                    JSONArray orders = data.getJSONArray("order_details");
                    HashMap<String, String> ktpPicture = new HashMap<>();
                    for (int i=0; i<orders.length(); i++){
                        JSONObject jo = orders.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("id",jo.getString("id"));
                        bundle.putString("consumen_id",jo.getString("consumen_id"));
                        bundle.putString("consumen_name",jo.getString("consumen_name"));
                        bundle.putString("product_name",jo.getString("product_name"));
                        bundle.putString("product_id",jo.getString("product_id"));
                        bundle.putString("qty",jo.getString("qty"));
                        bundle.putString("price",jo.getString("selling_price"));
                        bundle.putString("subtotal",jo.getString("subtotal"));
                        ktpPicture.put(jo.getString("consumen_id"), jo.getString("consumen_picture"));
                        mOrderData.add(bundle);
                    }


                    ArrayList<Bundle> consumerBundle = new ArrayList<>();
                    for (int i=0; i<consumers.length(); i++){
                        JSONObject consume = consumers.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", consume.getString("consumen_id"));
                        bundle.putString("value", consume.getString("consumen_name"));
                        String key = consume.getString("consumen_id")+consume.getString("consumen_name");
                        Log.d("TAGRZ",key +" = "+ktpPicture.get(key));
                        if (ktpPicture.get(key) != null){
                            consume.put("consumen_picture", ktpPicture.get(key));
                        }
                        bundle.putString("other", consume.toString());

                        consumerBundle.add(bundle);
                    }
                    spnr_jp_00.setDataBundle(consumerBundle);
                    if (consumerBundle.size() > 0){
                        new Handler().postDelayed(() -> spnr_jp_00.setSelection(0),600);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mOrderAdapter.notifyDataSetChanged();
        });
    }

    private void requestFromLocal(){
        lnly_info_00.removeAllViews();
        mOrderData.clear();
        if (getIntent().getStringExtra("offline_so") == null){
            Utility.showToastError(mActivity,"Data tidak dapat di proses");
            mActivity.finish();
            return;
        }
        buildInfo("Sales Order", Objects.requireNonNull(getIntent().getStringExtra("offline_so")));
        buildInfo("Tanggal Kirim", deliveryDate);
        buildInfo("Keterangan", note);
        addLine();

        TempDB tempDB = new TempDB();
        tempDB.getData(mActivity,mBookingID,TempDB.PROCESS_DEMO);
        try {
            ArrayList<Bundle> consumerBundle = new ArrayList<>();
            if (!tempDB.data.isEmpty()){
                JSONObject data     = new JSONObject(tempDB.data).getJSONObject("data");
                JSONArray dataJp = data.getJSONArray("data_jp");
                for (int i=0; i<dataJp.length(); i++){
                    Bundle bundle = new Bundle();
                    JSONObject jp = dataJp.getJSONObject(i);
                    bundle.putString("key", jp.getString("id"));
                    bundle.putString("value", jp.getString("name"));
                    bundle.putString("other", jp.toString());
                    consumerBundle.add(bundle);
                }

            }

            JpDB jpDB = new JpDB();
            ArrayList<JpDB> jpDBS = jpDB.getData(mActivity, mBookingID);
            for (JpDB db: jpDBS){
                JSONObject  jp = new JSONObject(db.data);
                Bundle bundle = new Bundle();
                bundle.putString("key", db.id+"");
                bundle.putString("value", jp.getString("name"));
                bundle.putString("other", jp.toString());
                Log.d(TAG,"bundle "+ bundle);
                consumerBundle.add(bundle);
            }
            spnr_jp_00.setDataBundle(consumerBundle);


            ProductDB productDB = new ProductDB();
            ArrayList<Bundle> prodBundle = new ArrayList<>();
            for (ProductDB db: productDB.getData(mActivity)){
                Bundle bundle = new Bundle();
                bundle.putString("key", db.id);
                bundle.putString("value", db.productName);
                JSONObject other = new JSONObject();
                other.put("price", db.price);
                bundle.putString("other", other.toString());
                prodBundle.add(bundle);
            }
            spnr_product_00.setDataBundle(prodBundle);

            SalesOrder2DB salesOrder2DB = new SalesOrder2DB();
            ArrayList<SalesOrder2DB> allOrder = salesOrder2DB.getAllData(mActivity, mBookingID);
            for (SalesOrder2DB order: allOrder){
                JSONObject jo = new JSONObject(order.data);
                Bundle bundle = new Bundle();
                bundle.putString("id",jo.getString("id"));
                bundle.putString("consumen_id",jo.getString("consumen_id"));
                bundle.putString("consumen_name",jo.getString("consumen_name"));
                bundle.putString("product_name",jo.getString("product_name"));
                bundle.putString("product_id",jo.getString("product_id"));
                bundle.putString("qty",jo.getString("qty"));
                bundle.putString("price",jo.getString("price"));
                bundle.putString("subtotal",jo.getString("price"));
                mOrderData.add(bundle);
            }
            mOrderAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void buildInfo(String key, String value){
        InfoView infoView = new InfoView(mActivity, null);
        if (value.equals("null")){
            infoView.create(key, "-");
        }
        else {
            infoView.create(key, value.trim());
        }
        lnly_info_00.addView(infoView);
    }
    private void addLine(){
        View view = new View(mActivity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utility.dpToPx(mActivity, 1));
        lp.topMargin = Utility.dpToPx(mActivity, 5);
        lp.bottomMargin = Utility.dpToPx(mActivity, 5);
        view.setBackgroundColor(Color.LTGRAY);
        lnly_info_00.addView(view,lp);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_COORDINATOR && resultCode == RESULT_OK){
            Utility.showToastSuccess(mActivity,"Photo saved to draf");
            Glide.with(mActivity).load(FileProcessing.openImage(mActivity,photo_path,"ktp.jpg")).into(imvw_coordinator_00);
            ImageResizer resizer = new ImageResizer(mActivity);
            resizer.compress(photo_path+"ktp.jpg",photo_path,"ktp_compress.jpg");
        }
        else if (requestCode == REQ_ADD_JP && resultCode == RESULT_OK) {
            if (offlineMode){
                requestFromLocal();
            }
            else {
                requestFromServer();
            }
        }
    }

    private void addSalesOrder(){
        String identity     = edtx_identity_00.getValue();
        String qty          = spnr_qty_00.getKeySelected();
        String product      = spnr_product_00.getKeySelected();
        String jp           = spnr_jp_00.getKeySelected();
        String fullname     = edtx_fullname_00.getValue();
        File sd     = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            sd = mActivity.getExternalFilesDir("");
        }

        if (offlineMode){
            saveToLocal(identity,qty,product,jp);
            return;
        }

        int totalOrderJp = 0;
        boolean existProduct = false;
        for (Bundle bundle : mOrderData){
            String consument = bundle.getString("consumen_id");
            assert consument != null;
            if (consument.contains(jp)){
                totalOrderJp ++;
                if (Objects.equals(bundle.getString("product_id"), product)){
                    existProduct = true;
                }
            }
        }

        if (fullname.isEmpty()){
            Utility.showToastError(mActivity, "Nama tidak valid");
            return;
        }
        if (!identity.isEmpty()){
            KtpValidator ktpValidator = new KtpValidator();
            if (!ktpValidator.valid(identity)){
                Utility.showToastError(mActivity, "Nomor ktp tidak valid");
                return;
            }
        }

        if (totalOrderJp >= 2){
            showErrorDialog(spnr_jp_00.getValueSelected()+" sudah memesan dua item di sales order ini");
            return;
        }
        if (totalOrderJp + Integer.parseInt(qty) > 2){
            showErrorDialog(spnr_jp_00.getValueSelected()+" hanya bisa order "+(2 - totalOrderJp)+" item lagi untuk sales order ini. Silahkan koreksi form tambah order Anda.");
            return;
        }
        if (existProduct){
            showErrorDialog("Produk "+spnr_product_00.getValueSelected()+" sudah di tambahkan pada order ini");
            return;
        }

        FormPost post = new FormPost(mActivity, Config.SALES_ORDER_ADD_PRODUCT);
        post.addParam(new ObjectApi("sales_id",salesOrder));
        post.addParam(new ObjectApi("consumen_id",jp));
        post.addParam(new ObjectApi("consumen_name",fullname));
        post.addParam(new ObjectApi("product_id",product));
        post.addParam(new ObjectApi("qty",qty));
        post.addParam(new ObjectApi("consumen_ktp",identity));
        if (FileProcessing.openImage(mActivity,photo_path+"ktp_compress.jpg") != null){
            post.addImage("consumen_picture",sd.getAbsolutePath()+photo_path+"ktp_compress.jpg");
        }
        post.execute();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                Utility.showToastSuccess(mActivity, message);
                requestFromServer();
            }
            else {
                showErrorDialog(message);
            }
        });
    }

    private void saveToLocal(String identity, String qty, String product, String jp){

        int totalOrderJp = 0;
        boolean existProduct = false;

        SalesOrder2DB sDB = new SalesOrder2DB();
        ArrayList<SalesOrder2DB> allOrder = sDB.getAllData(mActivity, mBookingID);
        for (SalesOrder2DB order: allOrder){
            try {
                JSONObject jo = new JSONObject(order.data);
                String consument = jo.getString("consumen_id");
                if (consument.equals(jp)){
                    totalOrderJp ++;
                    if (Objects.equals(jo.getString("product_id"), product)){
                        existProduct = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (totalOrderJp >= 2){
            showErrorDialog(spnr_jp_00.getValueSelected()+" sudah memesan dua item di sales order ini");
            return;
        }
        if (totalOrderJp + Integer.parseInt(qty) > 2){
            showErrorDialog(spnr_jp_00.getValueSelected()+" hanya bisa order "+(2 - totalOrderJp)+" item lagi untuk sales order ini. Silahkan koreksi form tambah order Anda.");
            return;
        }
        if (existProduct){
            showErrorDialog("Produk "+spnr_product_00.getValueSelected()+" sudah di tambahkan pada order ini");
            return;
        }

        File sd     = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            sd = mActivity.getExternalFilesDir("");
        }
        FormPost post = new FormPost(mActivity, Config.SALES_ORDER_ADD_PRODUCT);
        post.addParam(new ObjectApi("id",jp+spnr_jp_00.getValueSelected()));
        post.addParam(new ObjectApi("sales_id",salesOrder));
        post.addParam(new ObjectApi("consumen_id",jp));
        post.addParam(new ObjectApi("consumen_name",spnr_jp_00.getValueSelected()));
        post.addParam(new ObjectApi("consumen_ktp",identity));
        post.addParam(new ObjectApi("product_id",product));
        post.addParam(new ObjectApi("product_name",spnr_product_00.getValueSelected()));
        post.addParam(new ObjectApi("qty",qty));
        JSONObject otherProd = spnr_product_00.getOtherData();
        try {
            post.addParam(new ObjectApi("price",otherProd.getString("price")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (FileProcessing.openImage(mActivity,photo_path+"ktp_compress.jpg") != null){
            FileProcessing fs = new FileProcessing();
            String mID = System.currentTimeMillis()+".jpg";
            fs.saveToTmp(mActivity,FileProcessing.openImage(mActivity,photo_path+"ktp_compress.jpg"),photo_path+"draft/",mID);
            post.addImage("consumen_picture",sd.getAbsolutePath()+photo_path+"draft/"+mID);
        }
        SalesOrder2DB salesOrder2DB = new SalesOrder2DB();
        salesOrder2DB.id = jp+spnr_jp_00.getValueSelected();
        salesOrder2DB.customerId = jp;
        salesOrder2DB.bookingId = mBookingID;
        salesOrder2DB.salesId = salesOrder;
        salesOrder2DB.data = post.getData().toString();
        salesOrder2DB.dataImage = post.getImages().toString();
        salesOrder2DB.insert(mActivity);
        Utility.showToastSuccess(mActivity, "Data berhasil disimpan");
        new Handler().postDelayed(() -> {
            requestFromLocal();
            Glide.with(mActivity).load("").into(imvw_coordinator_00);
            FileProcessing.clearImage(mActivity,photo_path);
        },500);
    }

    private void deleteOrder(Bundle bundle){
        if (offlineMode){
            SalesOrder2DB salesOrder2DB = new SalesOrder2DB();
            salesOrder2DB.delete(mActivity, SalesOrder2DB.ID+"='"+bundle.getString("id")+"'");
            Utility.showToastSuccess(mActivity, "Data telah di hapus");
            requestFromLocal();
            return;
        }
        PostManager post = new PostManager(mActivity, Config.SALES_ORDER_DELETE_PRODUCT+bundle.getString("id"));
        post.execute("DELETE");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                Utility.showToastSuccess(mActivity, message);
                requestFromServer();
            }
            else {
                showErrorDialog(message);
            }
        });
    }

    private void showErrorDialog(String message){
        FailedWindow window = new FailedWindow(mActivity);
        window.setDescription(message);
        window.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("FINISH"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "FINISH")){
                mActivity.finish();
            }
        }
    };
}
