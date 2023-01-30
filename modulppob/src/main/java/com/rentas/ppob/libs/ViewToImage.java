package com.rentas.ppob.libs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewToImage {

    private Context mContext;

    public ViewToImage(){}

    public ViewToImage(Context context, View view, String name){
        mContext = context;

        String mediaPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+name+".jpeg";
        File mFile = new File(mediaPath);
        Log.d("ViewToImage",mFile.getPath()+" -> "+mFile.exists());
        if (mFile.exists()){
            mFile.delete();
        }
        saveToFileDwonload(view, name, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private  void saveToFileDwonload(View content,String name, int width, int height){

        Bitmap bitmap = createBitmapFromView(content, width, height);
        File file,f = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file =new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            f = new File(file.getAbsolutePath()+"/"+ name +".jpeg");
        }

        try {
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, ostream);
            ostream.flush();
            ostream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public ViewToImage(Context context, String childpath, View view, String name){
        mContext = context;
        String mediaPath = FileProcessing.getMainPath(context).getAbsolutePath()+"/"+FileProcessing.ROOT+"/"+childpath+"/"+name+".jpeg";
        File mFile = new File(mediaPath);
        Log.d("ViewToImage",mFile.getPath()+" -> "+mFile.exists());
        if (mFile.exists()){
            mFile.delete();
        }
        saveToFile(view,childpath, name, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public ViewToImage(Context context, String childpath, View view, String name, int width, int height){
        mContext = context;
        String mediaPath = FileProcessing.getMainPath(context).getAbsolutePath()+"/"+FileProcessing.ROOT+"/"+childpath+"/"+name+".jpeg";
        File mFile = new File(mediaPath);
        Log.d("ViewToImage",mFile.getPath()+" -> "+mFile.exists());
        if (!mFile.exists()){
            saveToFile(view,childpath, name, width, height);
        }
    }


    private  void saveToFile(View content,String childpath, String name, int width, int height){

        Bitmap bitmap = createBitmapFromView(content, width, height);
        File file,f = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file =new File( FileProcessing.getMainPath(mContext).getAbsolutePath(),FileProcessing.ROOT+"/"+childpath);
            if(!file.exists())
            {
                file.mkdirs();
            }
            f = new File(file.getAbsolutePath()+"/"+ name +".jpeg");
        }

        try {
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, ostream);
            ostream.flush();
            ostream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public Bitmap createBitmapFromView(View v, int width, int height) {

        if (v.getLayoutParams() instanceof LinearLayout.LayoutParams){
            v.setLayoutParams(new LinearLayout.LayoutParams(width,height));
        }
        else  if (v.getLayoutParams() instanceof RelativeLayout.LayoutParams){
            v.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
        }
        else {
            v.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        }

        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
//        c.drawColor(Color.parseColor("#0a84c3"));
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return bitmap;
    }

}
