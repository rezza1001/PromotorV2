package com.wadaro.promotor.base;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wadaro.promotor.R;
import com.wadaro.promotor.component.WarningWindow;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.UserDB;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.util.NetworkUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public abstract class MyActivity extends AppCompatActivity {

    protected String TAG = "MyActivity";
    protected Activity mActivity;
    private RelativeLayout loadingview;
    protected UserDB mUser;
    protected boolean offlineMode = false;
    private int clickLogo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        mActivity = this;
        TAG = mActivity.getClass().getName();
        mUser = new UserDB();
        mUser.getData(mActivity);

        checkConnection();

        // DEFAULT
        createLoading();
        initLayout();
        initData();
        initListener();

    }

    protected abstract int setLayout();
    protected abstract void initLayout();
    protected abstract void initData();
    protected abstract void initListener();

    protected void setCustomColorNavbar(int color){
        getWindow().setStatusBarColor(color);
    }

    protected void checkConnection(){
//        if (NetworkUtil.getConnectivityStatus(mActivity) && NetworkUtil.isNetworkAvailable(mActivity)){
//            MyPreference.save(mActivity,Global.PREF_OFFLINE_MODE,0);
//        }
//        else {
//            MyPreference.save(mActivity,Global.PREF_OFFLINE_MODE,1);
//        }
        if (MyPreference.getInt(mActivity, Global.PREF_OFFLINE_MODE) == 1){
            setImageNetwork(Color.GRAY,"Offline");
            offlineMode = true;
        }
        else {
            if (NetworkUtil.getConnectivityStatus(mActivity) && NetworkUtil.isNetworkAvailable(mActivity)){
                if (NetworkUtil.getConnection(mActivity).type < 8){
                    WarningWindow warningWindow = new WarningWindow(mActivity);
                    warningWindow.show("Perhatian", "Koneksi anda tidak bagus, Apakah anda akan menggunakan mode Offline?");
                    warningWindow.setOnSelectedListener(status -> {
                        if (status == 2){
                            offlineMode = true;
                            setImageNetwork(Color.GRAY,"Offline");
                            MyPreference.save(mActivity,Global.PREF_OFFLINE_MODE,1);
                        }
                        else {
                            offlineMode = false;
                            MyPreference.save(mActivity,Global.PREF_OFFLINE_MODE,0);
                            setImageNetwork(getResources().getColor(R.color.but_cancel),"Offline");
                        }
                    });
                }
                else {
                    offlineMode = false;
                    MyPreference.save(mActivity,Global.PREF_OFFLINE_MODE,0);
                    setImageNetwork(getResources().getColor(R.color.colorPrimary),"Online");
                }

            }
            else {
                offlineMode = true;
                setImageNetwork(Color.GRAY,"Offline");
                MyPreference.save(mActivity,Global.PREF_OFFLINE_MODE,1);
            }

        }
    }

    private void setImageNetwork(int color, String text){
        ImageView imvw_network_00 = findViewById(R.id.imvw_network_00);
        TextView txvw_onlineStatus = findViewById(R.id.txvw_onlineStatus);
        if (imvw_network_00 != null){
            imvw_network_00.setColorFilter(color);
            txvw_onlineStatus.setText(text);
            txvw_onlineStatus.setTextColor(color);

            imvw_network_00.setOnClickListener(v -> {
                WarningWindow warningWindow = new WarningWindow(mActivity);
                if (MyPreference.getInt(mActivity, Global.PREF_OFFLINE_MODE) == 0){
                    warningWindow.show("Perhatian", "Anda dalam mode online. Anda yakin untuk masuk mode offline?");
                    warningWindow.setOnSelectedListener(status -> {
                        if (status == 2){
                            setImageNetwork(Color.GRAY,"Offline");
                            MyPreference.save(mActivity,Global.PREF_OFFLINE_MODE,1);
                            offlineMode = true;
                        }
                        else {
                            offlineMode = false;
                            MyPreference.save(mActivity,Global.PREF_OFFLINE_MODE,0);
                            setImageNetwork(getResources().getColor(R.color.colorPrimary),"Online");
                        }
                    });
                }
                else {
                    warningWindow.show("Perhatian", "Anda dalam mode offline. Anda akan masuk mode online");
                    warningWindow.setOnSelectedListener(status -> {
                        if (status == 1){
                            offlineMode = true;
                            setImageNetwork(Color.GRAY,"Offline");
                            MyPreference.save(mActivity,Global.PREF_OFFLINE_MODE,1);
                        }
                        else {
                            offlineMode = false;
                            MyPreference.save(mActivity,Global.PREF_OFFLINE_MODE,0);
                            setImageNetwork(getResources().getColor(R.color.colorPrimary),"Online");
                        }
                    });
                }
            });
        }


        ImageView imvw_logo = findViewById(R.id.imvw_logo);
        if (imvw_logo != null){
            imvw_logo.setOnClickListener(v -> {
                clickLogo ++;
                if (clickLogo == 6){
                    exportDB();
                }
                new Handler().postDelayed(() -> {
                    clickLogo = 0;
                },4000);
            });
        }

    }

    private void createLoading(){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        loadingview = new RelativeLayout(mActivity, null);
        ViewGroup.LayoutParams lp = viewGroup.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        loadingview.setVisibility(View.GONE);
        viewGroup.addView(loadingview,lp);

        ProgressBar progressBar = new ProgressBar(mActivity, null,android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(true);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utility.getPixelValue(mActivity,25));
        lp2.topMargin = Utility.getPixelValue(mActivity, -12);
        loadingview.addView(progressBar, lp2);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#1c94fc"), PorterDuff.Mode.SRC_IN);
        setLoadingBarColor(2,0);
    }

    protected void showLoadingBar(int margintop, int background){
        margintop = margintop - 12;
        loadingview.setBackgroundColor(background);
        loadingview.setVisibility(View.VISIBLE);
        loadingview.setOnClickListener(null);

        ProgressBar progressBar = (ProgressBar) loadingview.getChildAt(0);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
        lp.topMargin = Utility.getPixelValue(mActivity, margintop);
    }

    protected void setLoadingBarColor(int margintop, int background){
        margintop = margintop - 12;
        loadingview.setBackgroundColor(background);
        loadingview.setOnClickListener(null);

        ProgressBar progressBar = (ProgressBar) loadingview.getChildAt(0);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
        lp.topMargin = Utility.getPixelValue(mActivity, margintop);
    }

    protected RelativeLayout getLoadingBar(){
        return loadingview;
    }

    protected void hideLoadingBar(){
        loadingview.setVisibility(View.GONE);
    }

    private void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+getPackageName()+"/databases/WADAROPROMOTOR.db";
        String backupDBPath = "WADAROPROMOTOR.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}