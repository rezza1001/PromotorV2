package com.wadaro.promotor;

import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.ui.demo.process.SalesOrder2Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestActivity extends MyActivity {
    @Override
    protected int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            exportDB();
        });
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
