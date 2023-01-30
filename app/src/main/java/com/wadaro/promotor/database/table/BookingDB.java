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

public class BookingDB extends MasterDB {

    public static final String TAG          = "BookingDB";
    public static final String TABLE_NAME   = "BookingDB";

    public static final String ID      = "id";
    public static final String DATA      = "data";
    public static final String IMAGES      = "images";

    public String id     = "";
    public String data   = "";
    public String images   = "";

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID    + " text NULL," +
                " " + DATA    + " text NULL," +
                " " + IMAGES    + " text NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected BookingDB build(Cursor res) {
        BookingDB boking = new BookingDB();
        boking.id    = res.getString(res.getColumnIndex(ID));
        boking.data    = res.getString(res.getColumnIndex(DATA));
        boking.images    = res.getString(res.getColumnIndex(IMAGES));
        return boking;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.id    = res.getString(res.getColumnIndex(ID));
        this.data    = res.getString(res.getColumnIndex(DATA));
        this.images    = res.getString(res.getColumnIndex(IMAGES));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(DATA, data);
        contentValues.put(IMAGES, images);
        return contentValues;
    }

    @Override
    public boolean insert(Context context) {
        return super.insert(context);
    }

    @Override
    public void delete(Context context, String id) {
        super.delete(context,ID+"="+id);
    }

    public ArrayList<BookingDB> getData(Context context){
        ArrayList<BookingDB> data = new ArrayList<>();

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
        String query = "select *  from " + TABLE_NAME +" WHERE "+ID+"='"+id+"'";
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
