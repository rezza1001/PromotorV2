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

public class SalesOrder2DB extends MasterDB {

    public static final String TAG          = "SalesOrderDB2";
    public static final String TABLE_NAME   = "SalesOrderDB2";

    public static final String ID           = "id";
    public static final String CUSTOMER_ID           = "customerId";
    public static final String BOOKING_ID   = "bookingid";
    public static final String SALES_ID     = "sales_id";
    public static final String DATA         = "data";
    public static final String DATA_IMAGE   = "image";

    public String id     = "";
    public String bookingId     = "";
    public String customerId     = "";
    public String salesId = "";
    public String data   = "";
    public String dataImage   = "";

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID    + " VARCHAR (50) NULL," +
                " " + CUSTOMER_ID    + " VARCHAR (50) NULL," +
                " " + BOOKING_ID    + " VARCHAR (50) NULL," +
                " " + SALES_ID    + " VARCHAR (50) NULL," +
                " " + DATA_IMAGE    + " VARCHAR (50) NULL," +
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
    protected SalesOrder2DB build(Cursor res) {
        SalesOrder2DB boking = new SalesOrder2DB();
        boking.id    = res.getString(res.getColumnIndex(ID));
        boking.bookingId = res.getString(res.getColumnIndex(BOOKING_ID));
        boking.salesId = res.getString(res.getColumnIndex(SALES_ID));
        boking.dataImage    = res.getString(res.getColumnIndex(DATA_IMAGE));
        boking.data    = res.getString(res.getColumnIndex(DATA));
        boking.customerId    = res.getString(res.getColumnIndex(CUSTOMER_ID));
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
        contentValues.put(SALES_ID, salesId);
        contentValues.put(DATA_IMAGE, dataImage);
        contentValues.put(CUSTOMER_ID, customerId);
        return contentValues;
    }

    @Override
    public boolean insert(Context context) {
        return super.insert(context);
    }


    public void delete(Context context, String booking) {
        super.delete(context, BOOKING_ID+"= '"+booking+"'");
    }

    public ArrayList<SalesOrder2DB> getAllData(Context context, String bookingId){
        ArrayList<SalesOrder2DB> data = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME +" WHERE "+BOOKING_ID+"='"+bookingId+"'";
        Log.d(TAG,query);
        Cursor res = db.rawQuery(query, null);
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

    public ArrayList<SalesOrder2DB> getAllData(Context context){
        ArrayList<SalesOrder2DB> data = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME;
        Log.d(TAG,query);
        Cursor res = db.rawQuery(query, null);
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

    public void getData(Context context, String bookingID){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME +" WHERE "+BOOKING_ID+"='"+bookingID+"'";
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

    public ArrayList<SalesOrder2DB> getDataByCustomer(Context context, String customerID){
        ArrayList<SalesOrder2DB> data = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME+" WHERE "+CUSTOMER_ID+"= '"+customerID+"'" ;
        Log.d(TAG,query);
        Cursor res = db.rawQuery(query, null);
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

    public ArrayList<SalesOrder2DB> getRealData(Context context, String bookingId){
        ArrayList<SalesOrder2DB> data = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select * from "+TABLE_NAME+" where CAST(customerId as INTEGER) > 1000 and "+BOOKING_ID+"='"+bookingId+"'" ;
        Log.d(TAG,query);
        Cursor res = db.rawQuery(query, null);
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

    public ArrayList<SalesOrder2DB> getRealCustomer(Context context, String bookingId){
        ArrayList<SalesOrder2DB> data = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select * from "+TABLE_NAME+" where CAST(customerId as INTEGER) > 1000 and "+BOOKING_ID+"='"+bookingId+"' GROUP BY "+CUSTOMER_ID ;
        Log.d(TAG,query);
        Cursor res = db.rawQuery(query, null);
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
}
