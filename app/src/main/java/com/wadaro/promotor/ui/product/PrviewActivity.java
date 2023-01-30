package com.wadaro.promotor.ui.product;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wadaro.promotor.R;
import com.wadaro.promotor.api.ImageDownloader;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.module.FileProcessing;
import com.wadaro.promotor.module.Utility;

public class PrviewActivity extends MyActivity {

    private static final String denah_path      = "/Wadaro/download/";
    private ImageView imvw_photo_00;

    @Override
    protected int setLayout() {
        return R.layout.product_activity_preview;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        FileProcessing.init(mActivity);
        imvw_photo_00 = findViewById(R.id.imvw_photo_00);
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText(getIntent().getStringExtra("title"));
    }

    @Override
    protected void initData() {
        Glide.with(mActivity).load(getIntent().getStringExtra("url")).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Utility.showToastError(mActivity, "Gambar gagal dibuka");
                mActivity.finish();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(imvw_photo_00);
    }

    @Override
    protected void initListener() {
        findViewById(R.id.bbtn_download_00).setOnClickListener(v -> {
            ImageDownloader downloader = new ImageDownloader(mActivity,getIntent().getStringExtra("url"),denah_path,""+System.currentTimeMillis()+".jpeg");
            downloader.execute();
            downloader.setOnDownloadListener(new ImageDownloader.OnDownloadListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinih(String path, String id) {
                    Utility.showToastSuccess(mActivity, "Foto disimpan ke "+path+id);
                }
            });
        });

        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }
}
