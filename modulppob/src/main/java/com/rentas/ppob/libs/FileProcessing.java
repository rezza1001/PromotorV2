package com.rentas.ppob.libs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.rentas.ppob.BuildConfig;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Objects;

public class FileProcessing {

    public static final String ROOT  = "DEMOBOOKER";
    public static final int REQUEST_OPEN_CAMERA  = 1001;
    public static final int REQUEST_OPEN_GALLERY = 1002;
    public static final int RESULT_OK = -1;
    private OnSavedListener mOnSavedListener;

    public static File getMainPath(Context context){
        File mediaPath = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            mediaPath = context.getExternalFilesDir("");
        }
        return mediaPath;
    }

    public void processImage(Context mContext, Intent data, String path, String name){
        int request     = data.getIntExtra("REQ",0);
        int result      = data.getIntExtra("RES",0);
        Intent intent   = data.getSelector();
        Log.d("FileProcessing","Start processImage "+request+":"+result);
        if (request == REQUEST_OPEN_CAMERA && result == RESULT_OK){
            assert intent != null;
            Bitmap thumbnail = (Bitmap) Objects.requireNonNull(intent.getExtras()).get("data");
            saveToTmp(mContext, thumbnail, path, name);
        }
        else if (request == REQUEST_OPEN_GALLERY && result == RESULT_OK){
            assert intent != null;
            Uri contentURI = intent.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), contentURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveToTmp(mContext, bitmap, path, name);
        }
    }
    public  void saveToTmp(Context context, Bitmap bitmap, String path, String name){
        Log.d("FileProcessing",path+name );
        String mediaPath = getMainPath(context).getAbsolutePath()+path+name;
        File media = new File(mediaPath);

        if (media.exists()){
            media.delete();
        }

        File file = null,f = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            File root = new File(getMainPath(context).getAbsolutePath(), "/"+ROOT);
            if (!root.exists()){
                if(root.mkdirs()){
                    Log.d("FileProcessing","Create Zonatik Success");
                }
                else {
                    Log.d("FileProcessing","Create Zonatik Failed");
                }
            }

            file = new File(getMainPath(context), path);
            if(!file.exists()) {
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
        Log.d("FileProcessing",f.getAbsolutePath());

        try {
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, ostream);
            ostream.flush();
            ostream.close();
        } catch (Exception e1) {
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

    public static boolean createFolder(Context context, String path){
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File root = new File(getMainPath(context).getAbsolutePath(), "/"+ROOT);
            Log.d("FileProcessing",root.getAbsolutePath());
            if (!root.exists()){
                if(root.mkdirs()){
                    Log.d("FileProcessing","Create root "+ROOT+" Success");
                    return create(context,path);
                }
                else {
                    Log.d("FileProcessing","Create root "+ROOT+" Failed");
                    return false;
                }
            }
            else {
                return create(context,path);
            }
        }
        else {
            Log.d("FileProcessing","MEDIA_MOUNTED NOT ACCESS");
            return false;
        }
    }

    private static boolean create(Context context, String path){
        File file;
        file = new File(getMainPath(context).getAbsolutePath(), path);
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
        File image = new File(getMainPath(context).getAbsolutePath()+path+name);

        Log.d("FileProcessing",sd.getAbsolutePath()+path+name);
        if (!image.exists()){
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }
    public static Bitmap openImage(Context context,String url){
        File sd =getMainPath(context);
        File image = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing",image.getAbsolutePath());
        if (!image.exists()){
            Log.d("FileProcessing","IMAGE NOT FOUND");
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }

    public int folderFilqQTY(Context context, String url){
        File sd = getMainPath(context);
        File file = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing",sd.getAbsolutePath()+url);
        if (!file.exists()){
            file.mkdirs();
        }
        if (file.listFiles() == null){
            return 0;
        }
        return file.listFiles().length;
    }

    public File[] getAllfiles(Context context, String url){
        File sd = getMainPath(context);
        File file = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing","getAllfiles : " +file.listFiles().length);
        if (file.listFiles() == null){
            return new File[0];
        }
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
    public static boolean deleteImage(Context context, String path, String name){
        String mediaPath = getMainPath(context).getAbsolutePath()+path+name;

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
    public static void clearImage(Context context, String path){
        String mediaPath = getMainPath(context)+path;

        File media = new File(mediaPath);
        String[] children = media.list();
        if (children != null){
            for (int i = 0; i < children.length; i++) {
                new File(media, children[i]).delete();
            }
        }
    }

    public static void clearFolder(Context context, String path){
        File sd = getMainPath(context);

        File dir = new File(sd.getAbsolutePath()+path);
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stringToFile(Context context, String data, String name) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        File path = new File(getMainPath(context).getAbsolutePath(), "/"+ROOT+"/dbug/");

        // Make sure the path directory exists.
        if(!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, name);
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void openFile(Activity activity, String path) {
        File file = new File(path);
        Uri uri = getUri(activity,file);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenintent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String fileName = file.getName();
        if (fileName.contains(".pdf")){
            pdfOpenintent.setDataAndType(uri, "application/pdf");
        }
        else if (fileName.contains(".jpg") || fileName.contains(".jpeg")){
            pdfOpenintent.setDataAndType(uri, "image/jpeg");
        }
        else if (fileName.contains(".txt")){
            pdfOpenintent.setDataAndType(uri, "text/plain");
        }
        else if (fileName.contains(".zip")){
            pdfOpenintent.setDataAndType(uri, "application/zip");
        }
        else if (fileName.contains(".rar")){
            pdfOpenintent.setDataAndType(uri, "application/x-rar-compressed");
        }
        else if (fileName.contains(".xls") || fileName.contains(".xlsx")){
            pdfOpenintent.setDataAndType(uri, "application/vnd.ms-excel");
        }
        else if (fileName.contains(".pptx") || fileName.contains(".ppt")){
            pdfOpenintent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        }
        else if (fileName.contains(".doc") || fileName.contains(".docx")){
            pdfOpenintent.setDataAndType(uri, "application/msword");
        }

        try {
            activity.startActivity(pdfOpenintent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri getUri(Context context, File file){
        return FileProvider.getUriForFile(context, context.getPackageName()+".provider", file);
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
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
