package com.rentas.ppob.libs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;

import com.rentas.ppob.component.DynamicDialog;

public class PermissionChecker {
    private Activity mContext;
    public PermissionChecker (Context context){
        mContext = (Activity) context;
    }

    public void checkLocation(){
        String[] PERMISSIONS =  new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        boolean access = Utility.checkPermission(mContext,PERMISSIONS);

        if (!access){
            DynamicDialog dialog = new DynamicDialog(mContext);
            dialog.showInfo("Akes Lokasi & Penyimpanan","System membutuhkan akses lokasi dan Akses Penyimpanan untuk proses tansaksi");
            dialog.setAction("Berikan Akses");
            dialog.setOnCloseLister(action -> {
                ActivityCompat.requestPermissions(mContext, PERMISSIONS, 32);
            });
            if (accessListener != null){
                accessListener.onAccess(false);
            }
        }
        else {
            if (accessListener != null){
                accessListener.onAccess(true);
            }
        }
    }

    public void checkStorage(){
        String[] PERMISSIONS =  new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            PERMISSIONS =  new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        boolean access = Utility.checkPermission(mContext,PERMISSIONS);

        if (!access){
            DynamicDialog dialog = new DynamicDialog(mContext);
            dialog.showInfo("Akes Penyimpanan","System membutuhkan akses penyimpanan untuk membuat struk transaksi anda.");
            String[] finalPERMISSIONS = PERMISSIONS;
            dialog.setAction("Berikan Akses");
            dialog.setOnCloseLister(action -> {
                ActivityCompat.requestPermissions(mContext, finalPERMISSIONS, 32);
            });
            if (accessListener != null){
                accessListener.onAccess(false);
            }
        }
        else {
            if (accessListener != null){
                accessListener.onAccess(true);
            }
        }
    }

    public void fingerPrint(){
        String[] PERMISSIONS = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            PERMISSIONS = new String[]{ Manifest.permission.USE_BIOMETRIC};
            boolean access = Utility.checkPermission(mContext,PERMISSIONS);

            if (!access){
                DynamicDialog dialog = new DynamicDialog(mContext);
                dialog.showInfo("Akes Fingerprint","System membutuhkan akses Fingerprint untuk proses transaksi.");
                String[] finalPERMISSIONS = PERMISSIONS;
                dialog.setAction("Berikan Akses");
                dialog.setOnCloseLister(action -> {
                    ActivityCompat.requestPermissions(mContext, finalPERMISSIONS, 32);
                });
                if (accessListener != null){
                    accessListener.onAccess(false);
                }
            }
            else {
                if (accessListener != null){
                    accessListener.onAccess(true);
                }
            }
            return;

        }
        if (accessListener != null){
            accessListener.onAccess(true);
        }

    }


    private AccessListener accessListener;
    public void setAccessListener(AccessListener accessListener){
        this.accessListener = accessListener;
    }
    public interface AccessListener{
        void onAccess(boolean hasAccess);
    }
}
