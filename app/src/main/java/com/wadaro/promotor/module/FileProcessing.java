package com.wadaro.promotor.module;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FileProcessing {

    public static final int REQUEST_OPEN_CAMERA  = 1001;
    public static final int REQUEST_OPEN_GALLERY = 1002;
    public static final int RESULT_OK = -1;
    private OnSavedListener mOnSavedListener;
    public static final String ROOT = "Wadaro";

    public void processImage(Context mContext, Intent data, String path, String name){
        int request     = data.getIntExtra("REQ",0);
        int result      = data.getIntExtra("RES",0);
        Intent intent   = data.getSelector();
        Log.d("FileProcessing","Start processImage "+request+":"+result);
        if (request == REQUEST_OPEN_CAMERA && result == RESULT_OK){
            Bitmap thumbnail = (Bitmap) intent.getExtras().get("data");
            saveToTmp(mContext,thumbnail, path, name);
        }
        else if (request == REQUEST_OPEN_GALLERY && result == RESULT_OK){
            Uri contentURI = intent.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), contentURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveToTmp(mContext,bitmap, path, name);
        }
    }

    public static void init(Context context){
        String[] permission = new String[2];
        permission[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permission[1] = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            permission[2] = Manifest.permission.MANAGE_EXTERNAL_STORAGE;
//
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }

        }

        boolean hasPermission = hasPermission((Activity) context, permission);
        if (hasPermission){
            FileProcessing.createFolder(context,ROOT);
        }
        else {
            Log.e("File Processing","No permission");
        }
    }

    public static boolean hasPermission(Activity activity, String[] permissions){
        if(!hasPermissions(activity, permissions)){
            ActivityCompat.requestPermissions(Objects.requireNonNull(activity), permissions, 32);
            return false;
        }
        else {
            return true;
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (permission == null){
                    continue;
                }
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static File getMainPath(Context context){
        File mediaPath = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            mediaPath = context.getExternalFilesDir("");
        }
        return mediaPath;
    }

    public static File getDownloadDir(Context context){
        return  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    public static File getDcimDir(Context context){
        return  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    }

    public  void saveToTmp(Context context, Bitmap bitmap, String path, String name){
        Log.d("FileProcessing",path+name );
        String mediaPath = Environment.getExternalStorageDirectory()+path+name;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            mediaPath = context.getExternalFilesDir("").getAbsolutePath()+path+name;
        }
        File media = new File(mediaPath);

        if (media.exists()){
            media.delete();
        }

        File file = null,f = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File root = new File(Environment.getExternalStorageDirectory(), "/");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                root = context.getExternalFilesDir("");
            }

            if (!root.exists()){
                if(root.mkdirs()){
                    Log.d("FileProcessing","Create Zonatik Success");
                }
                else {
                    Log.d("FileProcessing","Create Zonatik Failed");
                }
            }

            file = new File(Environment.getExternalStorageDirectory(), path);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file = context.getExternalFilesDir(path);
            }
            if(!file.exists())
            {
                if(file.mkdirs()){
                    Log.d("FileProcessing","Create "+path+" Success");
                }
                else {
                    Log.d("FileProcessing","Create "+path+" Failed");
                }
            }
            f = new File(file.getAbsolutePath()+"/"+name);
        }

        assert file != null;
        Log.d("FileProcessing",file.getAbsolutePath()+"/"+name);

        try {
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, ostream);
            ostream.flush();
            ostream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("NAME", name);
        bundle.putString("PATH", path);
        message.what =1 ;
        message.setData(bundle);
        handler.sendMessageDelayed(message,500);
    }
    public  void saveToTmpReal(Bitmap bitmap, String path, String name){
        Log.d("FileProcessing",path+name );
        String mediaPath = Environment.getExternalStorageDirectory()+path+name;
        File media = new File(mediaPath);

        if (media.exists()){
            media.delete();
        }

        File file = null,f = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File root = new File(Environment.getExternalStorageDirectory(), "/");
            if (!root.exists()){
                if(root.mkdirs()){
                    Log.d("FileProcessing","Create Zonatik Success");
                }
                else {
                    Log.d("FileProcessing","Create Zonatik Failed");
                }
            }

            file = new File(Environment.getExternalStorageDirectory(), path);
            if(!file.exists())
            {
                if(file.mkdirs()){
                    Log.d("FileProcessing","Create "+path+" Success");
                }
                else {
                    Log.d("FileProcessing","Create "+path+" Failed");
                }
            }
            f = new File(file.getAbsolutePath()+"/"+name);
        }

        assert file != null;
        Log.d("FileProcessing",file.getAbsolutePath()+"/"+name);

        try {
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.flush();
            ostream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("NAME", name);
        bundle.putString("PATH", path);
        message.what =1 ;
        message.setData(bundle);
        handler.sendMessageDelayed(message,500);
    }

    public static boolean createFolder(Context context,String path){

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File root = new File(Environment.getExternalStorageDirectory(), "/"+path);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                root = context.getExternalFilesDir("/"+path);
            }
            if (!root.exists()){
                if(root.mkdirs()){
                    Log.d("FileProcessing","Create root "+path+" Success");
                  return create(context,path);
                }
                else {
                    Log.d("FileProcessing","Create root "+path+" Failed");
                    return false;
                }
            }
            else {
                return create(context, path);
            }
        }
        else {
            Log.d("FileProcessing","MEDIA_MOUNTED NOT ACCESS");
            return false;
        }
    }

    private static boolean create(Context context,String path){
        File file;
        file = new File(Environment.getExternalStorageDirectory(), path);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            file = context.getExternalFilesDir(path);
        }
        if(!file.exists()) {
            if(file.mkdirs()){
                Log.d("FileProcessing","createFolder "+path+" Success");
                return true;
            }
            else {
                Log.d("FileProcessing","createFolder "+path+" Failed");
                return false;
            }
        }
        else {
            Log.d("FileProcessing","Folder exist "+path+"");
            return true;
        }
    }

    public static Bitmap openImage(Context context, String path, String name){
        File sd = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            sd = context.getExternalFilesDir(path);
        }
        File image = new File(sd.getAbsolutePath()+path+name);
        Log.d("FileProcessing",sd.getAbsolutePath()+path+name);
        if (!image.exists()){
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }
    public static Bitmap openImage(Context context, String url){
        File sd = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            sd = context.getExternalFilesDir("");
        }
        File image = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing",sd.getAbsolutePath()+url);
        if (!image.exists()){
            Log.d("FileProcessing","IMAGE NOT FOUND");
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }

    public int folderFilqQTY(Context context, String url){
        File sd = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            sd = context.getExternalFilesDir("");
        }
        File file = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing",sd.getAbsolutePath()+url);
        if (!file.exists()){
            file.mkdirs();
        }

        return file.listFiles().length;
    }

    public File[] getAllfiles(String url){
        File sd = Environment.getExternalStorageDirectory();
        File file = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing","getAllfiles : " +file.listFiles().length);
        return file.listFiles();
    }

    public static Bitmap openImageWthPath(String url){
        File image = new File(url);
        Log.d("FileProcessing",url);
        if (!image.exists()){
            Log.d("FileProcessing","IMAGE NOT FOUND");
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }

    public static Uri getUriFormFile(Context context,File file){
        return FileProvider.getUriForFile(context, context.getPackageName()+".provider", file);
    }

    public static Bitmap openImageReal(String url){
        File image = new File(url);
        Log.d("FileProcessing",url);
        if (!image.exists()){
            Log.d("FileProcessing","IMAGE NOT FOUND");
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
    }
    public static boolean deleteImage(String path, String name){
        String mediaPath = Environment.getExternalStorageDirectory()+path+name;
        File media = new File(mediaPath);
        Log.d("FileProcessing","Deleted : "+ media.getPath()+" -> "+media.exists());
        if (media.exists()){
           return media.delete();
        }
        else {
            return false;
        }
    }
    public static boolean deleteImage(String mediaPath){
        File media = new File(mediaPath);
        Log.d("FileProcessing","Deleted : "+ media.getPath()+" -> "+media.exists());
        if (media.exists()){
            return media.delete();
        }
        else {
            return false;
        }
    }
    public static void clearImage(Context context,String path){
        String mediaPath = Environment.getExternalStorageDirectory()+path;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            mediaPath = context.getExternalFilesDir("").getAbsolutePath()+path;
        }
        File media = new File(mediaPath);
        String[] children = media.list();
        if (children != null){
            for (int i = 0; i < children.length; i++) {
                new File(media, children[i]).delete();
            }
        }
    }

    public static void clearFolder(String path){
        File sd = Environment.getExternalStorageDirectory();

        File dir = new File(sd.getAbsolutePath()+path);
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(msg -> {
        switch (msg.what){
            case 1:
                Log.d("FileProcessing","RESULT "+ msg.getData().getString("NAME")+""+msg.getData().getString("PATH") );
            if (mOnSavedListener != null){
                mOnSavedListener.onSave(msg.getData().getString("PATH"),msg.getData().getString("NAME") );
            }
        }
        return false;
    } );


    public void setOnSavedListener(OnSavedListener onSavedListener){
        mOnSavedListener = onSavedListener;
    }
    public interface OnSavedListener{
        void onSave(String path, String name);
    }
}
