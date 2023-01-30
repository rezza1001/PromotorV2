package com.wadaro.promotor.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wadaro.promotor.database.DatabaseManager;
import com.wadaro.promotor.database.MasterDB;

import java.util.ArrayList;
import java.util.Objects;

public class JpDB extends MasterDB {

    public static final String TAG          = "JpDB";
    public static final String TABLE_NAME   = "JpDB";

    public static final String ID      = "id";
    public static final String DATA      = "data";
    public static final String BOOKING_ID      = "bookingID";
    public static final String REAL_ID      = "real_id";

    public int id               = 0;
    public String realID     = "";
    public String bookingID     = "";
    public String data          = "";
    public String images        = "";

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID    + " INTEGER DEFAULT 0," +
                " " + BOOKING_ID    + " varchar(50) NULL," +
                " " + REAL_ID    + " varchar(50) NULL," +
                " " + DATA    + " text NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected JpDB build(Cursor res) {
        JpDB jp = new JpDB();
        jp.id    = res.getInt(res.getColumnIndex(ID));
        jp.data    = res.getString(res.getColumnIndex(DATA));
        jp.bookingID    = res.getString(res.getColumnIndex(BOOKING_ID));
        jp.realID    = res.getString(res.getColumnIndex(REAL_ID));
        Log.d(TAG,"jp.id "+jp.id);
        Log.d(TAG,"bookingID "+jp.bookingID);
        Log.d(TAG,"data "+jp.data);
        return jp;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.id    = res.getInt(res.getColumnIndex(ID));
        this.data    = res.getString(res.getColumnIndex(DATA));
        this.bookingID    = res.getString(res.getColumnIndex(BOOKING_ID));
        this.realID    = res.getString(res.getColumnIndex(REAL_ID));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(DATA, data);
        contentValues.put(BOOKING_ID, bookingID);
        contentValues.put(REAL_ID, realID);
        return contentValues;
    }


    public void delete(Context context, int id) {
        delete(context, ID+"="+id+"");
    }
    public void deleteByBooking(Context context, String booking) {
        delete(context, BOOKING_ID+"='"+booking+"'");
    }

    @Override
    public boolean insert(Context context) {
        delete(context, ID+"="+id);
        return super.insert(context);
    }

    public ArrayList<JpDB> getData(Context context, String bookingID){
        ArrayList<JpDB> data = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String sql = "select *  from " + TABLE_NAME+" WHERE "+BOOKING_ID+"='"+bookingID+"'";
        Log.d(TAG,sql);
        Cursor res = db.rawQuery(sql, null);
        try {
            while (res.moveToNext()){
                data.add(build(res));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
        Log.d(TAG,"Data size "+ data.size());
        return  data;
    }

    public void getDataById(Context context, String id){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME+" WHERE "+ID+"="+id, null);
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

    public ArrayList<JpDB> getData(Context context){
        ArrayList<JpDB> data = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME, null);
        try {
            while (res.moveToNext()){
                data.add(build(res));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }

        return  data;
    }

    public int getNextID(Context context){
        int max = 0;
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select MAX("+ID+") as IDMAX  from " + TABLE_NAME , null);
        try {
            while (res.moveToNext()){
                max    = res.getInt(res.getColumnIndex("IDMAX"));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }

        return max;
    }
}
