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

public class ProductDB extends MasterDB {


    public static final String TAG          = "ProductDB";
    public static final String TABLE_NAME   = "GOODS";


    public static final String ID = "tmpid";
    public static final String PRODUCT_NAME = "product_name";
    public static final String COMPANY      = "company_id";
    public static final String PRICE        = "price";
    public static final String INSTALLMENT = "installment";
    public static final String PHOTO        = "product_photo";

    public String id = "";
    public String productName = "";
    public String company = "";
    public String photo = "";
    public long installment = 0;
    public long price           = 0;

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID + " varchar(30) NULL," +
                " " + PRODUCT_NAME + " varchar(100) NULL," +
                " " + COMPANY + " varchar(200) NULL," +
                " " + PHOTO    + " varchar(200) NULL," +
                " " + PRICE    + " varchar(10) NULL," +
                " " + INSTALLMENT + " varchar(10) NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected ProductDB build(Cursor res) {
        ProductDB visitDB = new ProductDB();
        visitDB.id = res.getString(res.getColumnIndex(ID));
        visitDB.productName = res.getString(res.getColumnIndex(PRODUCT_NAME));
        visitDB.company = res.getString(res.getColumnIndex(COMPANY));
        visitDB.photo = res.getString(res.getColumnIndex(PHOTO));
        visitDB.installment = res.getInt(res.getColumnIndex(INSTALLMENT));
        visitDB.price = res.getLong(res.getColumnIndex(PRICE));
        return visitDB;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.id = res.getString(res.getColumnIndex(ID));
        this.productName = res.getString(res.getColumnIndex(PRODUCT_NAME));
        this.company = res.getString(res.getColumnIndex(COMPANY));
        this.photo = res.getString(res.getColumnIndex(PHOTO));
        this.installment = res.getInt(res.getColumnIndex(INSTALLMENT));
        this.price = res.getLong(res.getColumnIndex(PRICE));
    }

    public ContentValues createContentValues(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(PRODUCT_NAME, productName);
        contentValues.put(COMPANY, company);
        contentValues.put(PHOTO, photo);
        contentValues.put(INSTALLMENT, installment);
        contentValues.put(PRICE, price);
        return contentValues;
    }


    @Override
    public boolean insert(Context context) {
        delete(context, ID +"='"+ id +"'");
        return super.insert(context);
    }

    public void getData(Context context, String tmpID){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +" WHERE "+ ID +"='"+tmpID+"'", null);
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

    public ArrayList<ProductDB> getData(Context context){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +"  ORDER BY "+ ID, null);
        ArrayList<ProductDB> all_notif = new ArrayList<>();
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

    public void insertBulk(Context context, ArrayList<ProductDB> data){
        DatabaseManager pDB = new DatabaseManager(context);
        String sql = "INSERT INTO "+ TABLE_NAME +" VALUES (?,?,?,?,?,?)";
        SQLiteStatement statement = pDB.getWritableDatabase().compileStatement(sql);
        pDB.getWritableDatabase().beginTransaction();
        for (ProductDB item: data) {
            statement.clearBindings();
            statement.bindString(1, item.id);
            statement.bindString(2, item.productName);
            statement.bindString(3, item.company);
            statement.bindString(4, item.photo);
            statement.bindLong(5, item.price);
            statement.bindLong(6, item.installment);
            try {
                statement.execute();
                Log.i(TAG,"INSERTED "+item.productName+" : "+ item.id);
            }catch (SQLiteConstraintException e){
                Log.e(TAG,"ERROR INSERT "+ item.productName+" "+item.id+" >> "+ Objects.requireNonNull(e.getMessage()));
            }
        }
        pDB.getWritableDatabase().setTransactionSuccessful();
        pDB.getWritableDatabase().endTransaction();
        pDB.close();
    }

}
