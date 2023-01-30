package com.wadaro.promotor.module;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class ImageResizer {

    private Context mContext;

    public ImageResizer(Context context){
        mContext = context;
    }

    public void compress(String imagelocation,String newpath, String newName){
        File sd     = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            sd = mContext.getExternalFilesDir("");
        }
        File image  = new File(sd+imagelocation);
        try {
            long size = getImageSize(sd+imagelocation);
            if (size >= 1000){
                double mbSize = (size/1000.0);
                Log.d("ImageResizer",imagelocation +" : "+mbSize+" MB");
            }
            else {
                Log.d("ImageResizer",imagelocation +" : "+(size)+" Kb");
            }

            int quality = 100;
            if (size >= 1000 && size <= 2000){
                quality = 97;
            }
            else if (size > 2000 && size < 5000){
                quality = 95;
            }
            else if (size >= 5000){
                quality = 90;
            }

            Log.d("ImageResizer","Process With quality : "+quality);
            new Compressor(mContext)
                  .setQuality(quality)
                  .setDestinationDirectoryPath(sd+newpath)
                  .compressToFile(image,newName);
            File finalSd = sd;
            new Handler().postDelayed(() -> {
                long newsize = getImageSize(finalSd +newpath+newName);
                if (newsize >= 1000){
                    double mbSize = (newsize/1000.0);
                    Log.d("ImageResizer","[NEW] "+ finalSd +newpath+newName +" : "+mbSize+" MB");
                }
                else {
                    Log.d("ImageResizer","[NEW] "+ finalSd +newpath+newName +" : "+(newsize)+" Kb");
                }
                if (mOnFinishListener != null){
                    mOnFinishListener.onFinish(finalSd +newpath, newName);
                }
            },1000);


        } catch (IOException e) {
            e.printStackTrace();
            Utility.showToastError(mContext, e.getMessage());
        }
    }

    private Long getImageSize(String path){
        File image = new File(path);
        return image.length() / 1024;
    }

    private OnFinishListener mOnFinishListener;
    public void setOnFinishListener(OnFinishListener pOnFinishListener){
        mOnFinishListener = pOnFinishListener;
    }
    public interface OnFinishListener{
        void onFinish(String path, String name);
    }
}
