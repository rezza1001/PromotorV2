package com.wadaro.promotor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wadaro.promotor.BuildConfig;
import com.wadaro.promotor.database.table.BookingDB;
import com.wadaro.promotor.database.table.ProductDB;
import com.wadaro.promotor.database.table.JpDB;
import com.wadaro.promotor.database.table.OrderDB;
import com.wadaro.promotor.database.table.QuestionsDB;
import com.wadaro.promotor.database.table.SalesOrder2DB;
import com.wadaro.promotor.database.table.SalesOrderDB;
import com.wadaro.promotor.database.table.TempDB;
import com.wadaro.promotor.database.table.UserDB;

import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {

    public static final String TAG = "RZ Database";

    public static final String DATABASE_NAME = "WADAROPROMOTOR.db";
    private static final int DB_VERSION      = 20;
    private static ArrayList<MasterDB> tables = new ArrayList<>();

    private Context mContext;
    private boolean isDbug = false;

    public DatabaseManager(Context contex) {
        super(contex, DATABASE_NAME , null, DB_VERSION);
        mContext = contex;
//        Log.d(TAG,"ACCESS DB ");

        if (BuildConfig.DEBUG){
            isDbug = true;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"onCreate New Database ");
        addTables();
        for (MasterDB masterDB: tables){
            db.execSQL("DROP TABLE IF EXISTS "+ masterDB.tableName());
            db.execSQL(masterDB.getCreateTable());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,"VERSION  "+ oldVersion + " <> "+ newVersion);
        onCreate(db);
    }
    public boolean insert (String pTable, ContentValues pContent) {
        if (mContext == null){
            Log.d(TAG,"Can't access database Context null");
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        if (isDbug){
            Log.d(TAG,"INSERT "+pTable+ " "+pContent.toString());
        }
        long x = db.insert(pTable, null, pContent);
        if (x == 1){
            return true;
        }
        else {
            return false;
        }
    }

    public int getDbVersion(){
        if (mContext == null){
            Log.d(TAG,"Can't access database Context null");
            return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        return db.getVersion();
    }

    public int update (String pTable, ContentValues pContent, String where) {
        if (mContext == null){
            Log.d(TAG,"Can't access database Context null");
            return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(pTable,pContent,where,null);
    }

    public void updateColumn (String pTable, String query, String pWhere) {
        if (mContext == null){
            Log.d(TAG,"Can't access database Context null");
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        if (isDbug){
            Log.d(TAG,"UPDATE "+pTable+ " SET " +query+ " WHERE "+ pWhere);
        }
        db.execSQL("UPDATE "+pTable+ " SET " +query+ " WHERE "+ pWhere);
    }

    public boolean delete(String pTable, String where) {
        if (mContext == null){
            Log.d(TAG,"Can't access database Context null");
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        if (isDbug){
            Log.d(TAG,"DELETE  FROM "+pTable+ " WHERE "+ where);
        }
        db.execSQL("DELETE  FROM "+pTable+ " WHERE "+ where);
//        db.delete(pTable,where,null);
        return true;
    }


    public boolean delete(String pTable) {
        if (mContext == null){
            Log.d(TAG,"Can't access database Context null");
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        if (isDbug){
            Log.d(TAG,"DELETE  FROM "+pTable);
        }
        db.execSQL("DELETE  FROM "+pTable);
        return true;
    }

    public void clearAllData(Context context){
        addTables();
        for (MasterDB masterDB: tables){
            masterDB.clearData(context);
        }
    }

    public void recreateDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        addTables();
        for (MasterDB masterDB: tables){
            Log.d(TAG,"DROP "+masterDB.tableName());
            db.execSQL("DROP TABLE IF EXISTS " + masterDB.tableName());
            db.execSQL(masterDB.getCreateTable());
        }

    }
    private void addTables(){
        tables.add(new UserDB());
        tables.add(new OrderDB());
        tables.add(new ProductDB());
        tables.add(new QuestionsDB());
        tables.add(new TempDB());
        tables.add(new JpDB());
        tables.add(new SalesOrderDB());
        tables.add(new SalesOrder2DB());
        tables.add(new BookingDB());

    }
}