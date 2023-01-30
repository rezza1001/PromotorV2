package com.wadaro.promotor.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.wadaro.promotor.database.DatabaseManager;
import com.wadaro.promotor.database.MasterDB;

import java.util.ArrayList;
import java.util.Objects;

public class TempDB extends MasterDB {

    public static final String DETAIL_BOOKING   = "DETAIL_BOOKING";
    public static final String PROCESS_DEMO     = "PROCESS_DEMO";
    public static final String SALES_ORDER      = "SALES_ORDER";

    public static final String TAG          = "TempDB";
    public static final String TABLE_NAME   = "TEMPDATA";


    public static final String ID           = "id";
    public static final String DATA         = "datatmp";
    public static final String KEY_DATA         = "keydata";

    public String id      = "";
    public String data  = "";
    public String keydata  = "";


    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID    + " varchar(30) NULL," +
                " " + DATA + " TEXT NULL," +
                " " + KEY_DATA + " varchar(30) NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected TempDB build(Cursor res) {
        TempDB visitDB = new TempDB();
        visitDB.id = res.getString(res.getColumnIndex(ID));
        visitDB.data = res.getString(res.getColumnIndex(DATA));
        visitDB.keydata = res.getString(res.getColumnIndex(KEY_DATA));
        return visitDB;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.id = res.getString(res.getColumnIndex(ID));
        this.data = res.getString(res.getColumnIndex(DATA));
        this.keydata = res.getString(res.getColumnIndex(KEY_DATA));

        Log.d(TAG, "buildSingle : "+ this.data);
    }

    public ContentValues createContentValues(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(DATA, data);
        contentValues.put(KEY_DATA, keydata);
        return contentValues;
    }


    @Override
    public boolean insert(Context context) {
        delete(context, ID+"='"+id+"' AND "+KEY_DATA+"='"+keydata+"'");
        return super.insert(context);
    }

    public void insertBulk(Context context, ArrayList<TempDB> data){
        Log.d(TAG,"INSERT BULK "+ data.size());
        DatabaseManager pDB = new DatabaseManager(context);
        String sql = "INSERT INTO "+ TABLE_NAME +" VALUES (?,?,?)";
        SQLiteStatement statement = pDB.getWritableDatabase().compileStatement(sql);
        pDB.getWritableDatabase().beginTransaction();
        for (TempDB item: data) {
            statement.clearBindings();
            statement.bindString(1, item.id);
            statement.bindString(2, item.data);
            statement.bindString(3, item.keydata);
            try {
                statement.execute();
                Log.d(TAG,"INSERTED "+ item.data);
            }catch (SQLiteConstraintException e){
                Log.e(TAG,"ERROR INSERT "+ e.getMessage());
            }
        }
        pDB.getWritableDatabase().setTransactionSuccessful();
        pDB.getWritableDatabase().endTransaction();
        pDB.close();
    }

    public void getData(Context context, String id, String keydata){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME +" WHERE "+ ID+"='"+id+"' AND "+KEY_DATA+"='"+keydata+"'";
        Log.d(TAG,query);
        Cursor res = db.rawQuery(query, null);
        try {
            while (res.moveToNext()){
                buildSingle(res);
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
    }


    public ArrayList<TempDB> getAllData(Context context, String id, String keydata){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME +" WHERE "+ ID+"='"+id+"' AND "+KEY_DATA+"='"+keydata+"'";
        Log.d(TAG,query);
        Cursor res = db.rawQuery(query, null);
        ArrayList<TempDB> tempDBS = new ArrayList<>();
        try {
            while (res.moveToNext()){
                tempDBS.add(build(res));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
        return tempDBS;
    }



}
