package com.wadaro.promotor.ui.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.util.DownloadPdfTask;
import com.wadaro.promotor.util.MyCurrency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class DetailProductActivity extends MyActivity {

    private ImageView imvw_product_00;
    private TextView txvw_name_00,txvw_price_00,txvw_doc_00,txvw_photo_00;
    private LinearLayout lnly_desc_00;

    private FileAdapter mFileAdapter;
    private ArrayList<Bundle> fileBundles = new ArrayList<>();

    private PhotoAdapter mPhotoAdapter;
    private ArrayList<Bundle> photoBundles = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.product_activity_detail;
    }

    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Detail Produk");

        imvw_product_00 = findViewById(R.id.imvw_product_00);
        txvw_name_00    = findViewById(R.id.txvw_name_00);
        txvw_price_00   = findViewById(R.id.txvw_price_00);
        lnly_desc_00    = findViewById(R.id.lnly_desc_00);
        txvw_doc_00     = findViewById(R.id.txvw_doc_00);
        txvw_photo_00     = findViewById(R.id.txvw_photo_00);

        RecyclerView rcvw_file_00 = findViewById(R.id.rcvw_file_00);
        rcvw_file_00.setLayoutManager(new GridLayoutManager(mActivity, 4));
        mFileAdapter = new FileAdapter(fileBundles);
        rcvw_file_00.setAdapter(mFileAdapter);
        txvw_doc_00.setVisibility(View.GONE);

        RecyclerView rcvw_photo_00 = findViewById(R.id.rcvw_photo_00);
        rcvw_photo_00.setLayoutManager(new GridLayoutManager(mActivity, 3));
        mPhotoAdapter = new PhotoAdapter(mActivity, photoBundles);
        rcvw_photo_00.setAdapter(mPhotoAdapter);
        txvw_photo_00.setVisibility(View.GONE);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        Bundle bundle = getIntent().getBundleExtra("data");
        if (bundle == null){
            Utility.showToastError(mActivity, "Produk tidak tersedia");
            return;
        }
        fileBundles.clear();
        photoBundles.clear();

        String id = bundle.getString("product_id");
        String image = bundle.getString("image");
        PostManager post = new PostManager(mActivity, Config.GET_DETAIL_INFO_PRODUCT.replace("#productId",id));
        Glide.with(mActivity).load(image).into(imvw_product_00);

        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    txvw_name_00.setText(data.getString("product_name"));
                    txvw_price_00.setText(MyCurrency.toCurrnecy("Rp",data.getString("product_price")));
                    JSONArray products = data.getJSONArray("product_files");
                    for (int i=0; i<products.length(); i++){
                        JSONObject jo = products.getJSONObject(i);
                        String type = jo.getString("product_filetype");
                        if (type.equalsIgnoreCase("BROCHURE")){
                            Bundle bd = new Bundle();
                            bd.putString("file",jo.getString("product_filename"));
                            fileBundles.add(bd);
                        }
                        else if (type.equalsIgnoreCase("PHOTO")){
                            Bundle bd = new Bundle();
                            bd.putString("photo",jo.getString("product_filename"));
                            photoBundles.add(bd);
                        }
                        if (i ==0 ){
                            if (!jo.isNull("product_description") && !jo.getString("product_description").equals("null") && !jo.getString("product_description").isEmpty()){
                                buildDesc(jo.getString("product_description"));
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (fileBundles.size() > 0){
                txvw_doc_00.setVisibility(View.VISIBLE);
            }
            if (photoBundles.size() > 0){
                txvw_photo_00.setVisibility(View.VISIBLE);

            }
            mFileAdapter.notifyDataSetChanged();
            mPhotoAdapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());

        mPhotoAdapter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, PrviewActivity.class);
            intent.putExtra("title","Preview");
            intent.putExtra("url",data.getString("photo"));
            startActivity(intent);
        });

        mFileAdapter.setOnSelectedListener(data -> {
            new DownloadPdfTask(mActivity, Objects.requireNonNull(data.getString("file")));
        });

    }

    private void buildDesc(String description){
        TextView txvw = new TextView(mActivity, null);
        if(description.matches("(<)(script[^>]*>[^<]*(?:<(?!\\/script>)[^<]*)*<\\/script>|\\/?\\b[^<>]+>|!(?:--\\s*(?:(?:\\[if\\s*!IE]>\\s*-->)?[^-]*(?:-(?!->)-*[^-]*)*)--|\\[CDATA[^\\]]*(?:](?!]>)[^\\]]*)*]])>)|(e)")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txvw.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
            }
            else {
                txvw.setText(Html.fromHtml(description));
            }
        }
        else {
            txvw.setText(description);
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = Utility.dpToPx(mActivity, 10);
        lnly_desc_00.addView(txvw, lp);
    }
}
