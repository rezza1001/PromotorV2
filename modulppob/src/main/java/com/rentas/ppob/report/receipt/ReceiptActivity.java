package com.rentas.ppob.report.receipt;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.component.LoadingDialog;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.FileProcessing;
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.libs.PermissionChecker;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.libs.ViewToImage;
import com.rentas.ppob.master.MyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ReceiptActivity extends MyActivity {

    private HeaderView header_view;
    private LinearLayout lnly_body,lnly_receipt;
    private TextView txvw_nameHeader;
    private StringBuilder copyAsText = new StringBuilder();

    @Override
    protected int setLayout() {
        return R.layout.report_receipt_activity_main;
    }

    @Override
    protected void initLayout() {
        header_view = findViewById(R.id.header_view);
        txvw_nameHeader = findViewById(R.id.txvw_nameHeader);
        header_view.create("Struk");
        header_view.setPrimaryColor();

        lnly_body = findViewById(R.id.lnly_body);
        lnly_receipt = findViewById(R.id.lnly_receipt);

    }

    @Override
    protected void initData() {
        copyAsText = new StringBuilder();
        txvw_nameHeader.setText(MainData.getProfileName(mActivity));
        PostManager post = new PostManager(mActivity, ConfigAPI.GET_RECEIPT+getIntent().getStringExtra("invoice"));
        post.executeGET();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    obj = obj.getJSONObject("data");
                    JSONObject csResponse = obj.getJSONObject("customer_response");
                    if (csResponse.has("json")){
                        csResponse = csResponse.getJSONObject("json");
                    }
                    addHeader(csResponse.getString("header"));
                    JSONArray ja = csResponse.getJSONArray("content");
                    buildItem(ja);

                    long price = obj.getLong("setting_customer_price");
                    long charge = 0;
                    long bill = 0;
                    if (!obj.isNull("customer_trx_charge")){
                        charge = obj.getLong("customer_trx_charge");
                        bill = obj.getLong("customer_bill_amount");
                    }
                    if (bill > 0){
                        addItem("Tagihan", MyCurrency.toCurrnecy("Rp",bill), true);
                        addItem("Admin",MyCurrency.toCurrnecy("Rp",charge), true);

                    }
//                    addItem("Diskon","Rp 0", true);
                    addItemBig("Total",MyCurrency.toCurrnecy("Rp",price), true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void buildItem(JSONArray ja ) throws JSONException {
        for (int i=0; i<ja.length(); i++){
            JSONObject jo = ja.getJSONObject(i);
            String type = jo.getString("type");
            JSONObject style = jo.has("styles")? !jo.isNull("styles") ? jo.getJSONObject("styles") : null:null;
            if (type.equals("line")){
                addLineDashGap();
                continue;
            }
            String label = jo.getString("label");
            String value = jo.getString("value");

            if (type.equals("inline")){
                if (style != null){
                    addItemBig(label,value, true);
                }
                else {
                    addItem(label,value, true);
                }
            }
            else {
                if (style != null){
                    addItemBig(label,value, false);
                }
                else {
                    addItem(label,value, false);
                }
            }
        }

    }

    @Override
    protected void initListener() {
        header_view.setOnBackListener(this::onBackPressed);
        findViewById(R.id.lnly_share).setOnClickListener(v -> buildFile());
        findViewById(R.id.lnly_copy).setOnClickListener(v -> Utility.copyToClip(mActivity,copyAsText.toString(),"Berhasil di copy"));
    }

    private void addHeader(String value){
        copyAsText.append(value).append("\n");
        ReceiptItemView itemView = new ReceiptItemView(mActivity, null);
        itemView.createVertical("",value);
        itemView.setFontValue(R.font.roboto_bold ,15);
        lnly_body.addView(itemView);

        RelativeLayout view = new ReceiptItemView(mActivity, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utility.dpToPx(mActivity, 5));
        lnly_body.addView(view,lp);
    }

    private void addItem(String key, String value, boolean horizontal){
        ReceiptItemView itemView = new ReceiptItemView(mActivity, null);
        if (!horizontal){
            itemView.createVertical(key,value);
            copyAsText.append("       ").append(key).append("\n").append("       ").append(value);
        }
        else {
            itemView.createHorizontal(key,value);
            copyAsText.append(key).append(" : ").append(value).append("\n");
        }
        lnly_body.addView(itemView);
    }


    private void addItemBig(String key, String value, boolean horizontal){
        ReceiptItemView itemView = new ReceiptItemView(mActivity, null);
        if (!horizontal){
            itemView.createVertical(key,value);
            copyAsText.append("       ").append(key).append("\n").append("       ").append(value);
        }
        else {
            itemView.createHorizontal(key,value);
            copyAsText.append(key).append(" : ").append(value).append("\n");
        }
        itemView.setFontValue(R.font.roboto_bold,15);
        itemView.setFontKey( R.font.roboto_bold,15);
        lnly_body.addView(itemView);
    }

    private void addLineDashGap(){
        copyAsText.append("------------------------------------").append("\n");
        ImageView imvw = new ImageView(mActivity, null);
        imvw.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        imvw.setImageResource(R.drawable.dotted_line);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utility.dpToPx(mActivity, 10));
        lnly_body.addView(imvw, lp);
    }

    public void buildFile(){
        PermissionChecker checker = new PermissionChecker(mActivity);
        checker.setAccessListener(hasAccess -> {
            if (hasAccess){
                LoadingDialog loadingWindow = new LoadingDialog(mActivity);
                loadingWindow.show();
                new ViewToImage(mActivity,"transaction",lnly_receipt,getIntent().getStringExtra("inv_number"));
                new Handler().postDelayed(() -> {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) lnly_receipt.getLayoutParams();
                    lp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                    lnly_receipt.setLayoutParams(lp);

                    loadingWindow.dismiss();
                    shareMedia("WhatsApp","Bukti Transaksi");

                },1000);
            }
        });
        checker.checkStorage();
    }

    public void shareMedia(String tag, String subject){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        String mediaPath = FileProcessing.getMainPath(mActivity)+"/"+FileProcessing.ROOT+"/transaction/"+getIntent().getStringExtra("inv_number")+".jpeg";
        File media = new File(mediaPath);
        Uri uri = FileProcessing.getUri(mActivity,media);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_TEXT, subject);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        String whatsapp     =  "WhatsApp";
        String telegram     =  "Telegram";

        if (tag.equals(whatsapp)){
            shareIntent.setPackage("com.whatsapp");
            try {
                mActivity.startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mActivity,"This apps have not been installed", Toast.LENGTH_SHORT).show();
            }
        }
        else  if (tag.equals(telegram)){
            shareIntent.setPackage("org.telegram.messenger");
            try {
                mActivity.startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                try {
                    shareIntent.setPackage("org.thunderdog.challegram");
                    mActivity.startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex1) {
                    Toast.makeText(mActivity,"This apps have not been installed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
