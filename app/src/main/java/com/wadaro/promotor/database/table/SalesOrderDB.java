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

public class SalesOrderDB extends MasterDB {

    public static final String TAG          = "SalesOrderDB";
    public static final String TABLE_NAME   = "SalesOrderDB";

    public static final String ID           = "id";
    public static final String BOOKING_ID   = "bookingid";
    public static final String DATA         = "data";

    public String id     = "";
    public String bookingId     = "";
    public String data   = "";

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID    + " VARCHAR (50) NULL," +
                " " + BOOKING_ID    + " VARCHAR (50) NULL," +
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
    protected SalesOrderDB build(Cursor res) {
        SalesOrderDB boking = new SalesOrderDB();
        boking.id    = res.getString(res.getColumnIndex(ID));
        boking.bookingId = res.getString(res.getColumnIndex(BOOKING_ID));
        boking.data    = res.getString(res.getColumnIndex(DATA));
        return boking;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.id    = res.getString(res.getColumnIndex(ID));
        this.bookingId    = res.getString(res.getColumnIndex(BOOKING_ID));
        this.data    = res.getString(res.getColumnIndex(DATA));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(DATA, data);
        contentValues.put(BOOKING_ID, bookingId);
        return contentValues;
    }

    @Override
    public boolean insert(Context context) {
        return super.insert(context);
    }

    @Override
    public void delete(Context context, String bookingId) {
        super.delete(context,BOOKING_ID+"='"+bookingId+"'");
    }

    public ArrayList<SalesOrderDB> getData(Context context){
        ArrayList<SalesOrderDB> data = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME , null);
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

    public void getData(Context context, String id){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME +" WHERE "+BOOKING_ID+"='"+id+"'";
        Cursor res = db.rawQuery(query, null);
        Log.d(TAG,query);
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
}
