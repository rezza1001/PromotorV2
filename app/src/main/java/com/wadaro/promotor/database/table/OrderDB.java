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

public class OrderDB extends MasterDB {


    public static final String TAG          = "OrderDB";
    public static final String TABLE_NAME   = "ORDERDB";


    public static final String TMP_ID           = "tmpid";
    public static final String CUSTOMER         = "customer";
    public static final String CUSTOMER_NAME         = "customer_name";
    public static final String GOODS            = "goods";
    public static final String GOODS_NAME       = "goods_name";
    public static final String QTY              = "qty";
    public static final String SURVEY              = "SURVEY";

    public String tmpId         = "";
    public String customer      = "";
    public String customerName      = "";
    public String goods         = "";
    public String goodsName     = "";
    public String qty           = "";
    public String survey           = "0";

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + TMP_ID    + " varchar(30) NULL," +
                " " + CUSTOMER + " varchar(20) NULL," +
                " " + CUSTOMER_NAME + " varchar(50) NULL," +
                " " + GOODS    + " varchar(20) NULL," +
                " " + GOODS_NAME    + " varchar(200) NULL," +
                " " + SURVEY    + " varchar(10) NULL," +
                " " + QTY    + " varchar(200) NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected OrderDB build(Cursor res) {
        OrderDB visitDB = new OrderDB();
        visitDB.tmpId = res.getString(res.getColumnIndex(TMP_ID));
        visitDB.customer = res.getString(res.getColumnIndex(CUSTOMER));
        visitDB.customerName = res.getString(res.getColumnIndex(CUSTOMER_NAME));
        visitDB.goods = res.getString(res.getColumnIndex(GOODS));
        visitDB.goodsName = res.getString(res.getColumnIndex(GOODS_NAME));
        visitDB.qty = res.getString(res.getColumnIndex(QTY));
        visitDB.survey = res.getString(res.getColumnIndex(SURVEY));
        return visitDB;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.tmpId = res.getString(res.getColumnIndex(TMP_ID));
        this.customer = res.getString(res.getColumnIndex(CUSTOMER));
        this.customerName = res.getString(res.getColumnIndex(CUSTOMER_NAME));
        this.goods = res.getString(res.getColumnIndex(GOODS));
        this.goodsName = res.getString(res.getColumnIndex(GOODS_NAME));
        this.qty = res.getString(res.getColumnIndex(QTY));
        this.survey = res.getString(res.getColumnIndex(SURVEY));
    }

    public ContentValues createContentValues(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TMP_ID, tmpId);
        contentValues.put(CUSTOMER, customer);
        contentValues.put(CUSTOMER_NAME, customerName);
        contentValues.put(GOODS, goods);
        contentValues.put(GOODS_NAME, goodsName);
        contentValues.put(QTY, qty);
        contentValues.put(SURVEY, survey);
        return contentValues;
    }


    @Override
    public boolean insert(Context context) {
        delete(context, TMP_ID+"='"+tmpId+"'");
        return super.insert(context);
    }

    public void getData(Context context, String tmpID){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +" WHERE "+ TMP_ID+"='"+tmpID+"'", null);
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

    public ArrayList<OrderDB> getOrders(Context context){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +"  ORDER BY "+ TMP_ID , null);
        ArrayList<OrderDB> all_notif = new ArrayList<>();
        try {
            while (res.moveToNext()){
                all_notif.add(build(res));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
        return all_notif;
    }

}
