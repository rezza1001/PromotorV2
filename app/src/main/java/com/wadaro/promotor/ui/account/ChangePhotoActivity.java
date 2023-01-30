package com.wadaro.promotor.ui.account;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.FormPost;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.module.FailedWindow;
import com.wadaro.promotor.module.FileProcessing;
import com.wadaro.promotor.module.ImageResizer;
import com.wadaro.promotor.module.SuccessWindow;
import com.wadaro.promotor.module.Utility;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ChangePhotoActivity extends MyActivity {
    private static final int REQ_FILE   = 1;
    private static final int REQ_CAMERA = 2;
    private ImageView imvw_photo_00;
    private String photo_path = "/Wadaro/profile/";
    @Override
    protected int setLayout() {
        return R.layout.account_activity_chngphoto;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_title_00 = findViewById(R.id.txvw_titile_00);
        txvw_title_00.setText("Ganti Photo");

        imvw_photo_00 = findViewById(R.id.imvw_photo_00);
    }

    @Override
    protected void initData() {
        FileProcessing.createFolder(mActivity,photo_path);
        FileProcessing.clearImage(mActivity,photo_path);
        Glide.with(mActivity).load(Config.IMAGE_PROFILE+mUser.photo).into(imvw_photo_00);
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.rvly_galery_00).setOnClickListener(v -> performFileSearch());
        findViewById(R.id.rvly_camera_00).setOnClickListener(v -> openCamera());
        findViewById(R.id.bbtn_save_00).setOnClickListener(v -> saveFile());
    }

    private void performFileSearch() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,REQ_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == REQ_FILE && resultCode == RESULT_OK){
            if (resultData != null) {
                Uri uri = resultData.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(),uri);
                    FileProcessing fp = new FileProcessing();
                    fp.saveToTmp(mActivity, bitmap,photo_path,"profile.jpg");
                    fp.setOnSavedListener((path, name) -> {
                        Glide.with(mActivity).load(FileProcessing.openImage(mActivity,path+name)).into(imvw_photo_00);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == REQ_CAMERA && resultCode == RESULT_OK){
            ImageResizer resizer = new ImageResizer(mActivity);
            resizer.compress(photo_path+"profiletmp.jpg",photo_path,"profile.jpg");
            resizer.setOnFinishListener((path, name) -> {
                Glide.with(mActivity).load(FileProcessing.openImage(mActivity,photo_path+"profile.jpg")).into(imvw_photo_00);
            });
        }
    }

    private void openCamera(){
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
            String file =mediaPath+ "profiletmp.jpg";
            File newfile = new File(file);
            try {
                newfile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = FileProcessing.getUriFormFile(mActivity, newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            startActivityForResult(cameraIntent, REQ_CAMERA);
        }
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void saveFile(){
        if (FileProcessing.openImage(mActivity,photo_path+"profile.jpg") == null){
            Utility.showToastError(mActivity,"Silahkan pilih photo");
            return;
        }
        File sd         = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            sd = mActivity.getExternalFilesDir("");
        }
        FormPost post   = new FormPost(mActivity,Config.CHANGE_PROFILE_PHOTO.replace("#userId", mUser.user_id));
        post.addImage("photo",sd.getAbsolutePath()+photo_path+"profile.jpg");
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                SuccessWindow successWindow = new SuccessWindow(mActivity);
                successWindow.setDescription(message);
                successWindow.show();
                successWindow.setOnFinishListener(() -> {
                    setResult(RESULT_OK);
                    mActivity.finish();
                });

            }else {
                FailedWindow failedWindow = new FailedWindow(mActivity);
                failedWindow.setDescription(message);
                failedWindow.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        mActivity.finish();
    }
}
