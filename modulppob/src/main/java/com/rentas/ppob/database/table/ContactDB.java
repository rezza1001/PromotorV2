package com.rentas.ppob.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.rentas.ppob.database.DatabaseManager;
import com.rentas.ppob.database.MasterDB;

import java.util.ArrayList;
import java.util.Objects;

public class ContactDB extends MasterDB {

    public static final String TAG          = "ContactDB";
    public static final String TABLE_NAME   = "ContactDB";

    public static final String CUSTOMER_ID = "cutomer_id";
    public static final String NAME = "name";
    public static final String TYPE = "type_id";

    public String customerID = "";
    public String name = "";
    public String type = "";

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + CUSTOMER_ID + " varchar(30) NULL," +
                " " + NAME + " varchar(30) NULL," +
                " " + TYPE + " varchar(10) NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected ContactDB build(Cursor res) {
        ContactDB jp = new ContactDB();
        jp.customerID = res.getString(res.getColumnIndex(CUSTOMER_ID));
        jp.type = res.getString(res.getColumnIndex(TYPE));
        jp.name = res.getString(res.getColumnIndex(NAME));
        return jp;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.customerID = res.getString(res.getColumnIndex(CUSTOMER_ID));
        this.type = res.getString(res.getColumnIndex(TYPE));
        this.name = res.getString(res.getColumnIndex(NAME));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOMER_ID, customerID);
        contentValues.put(TYPE, type);
        contentValues.put(NAME, name);
        return contentValues;
    }


    public void delete(Context context, String id) {
        super.delete(context, CUSTOMER_ID +"='"+id+"'");
    }

    @Override
    public boolean insert(Context context) {
        delete(context,customerID);
        return super.insert(context);
    }

    public void insertBulk(Context context, ArrayList<ContactDB> data){
        DatabaseManager pDB = new DatabaseManager(context);
        String sql = "INSERT INTO "+ TABLE_NAME +" VALUES (?,?,?)";
        SQLiteStatement statement = pDB.getWritableDatabase().compileStatement(sql);
        pDB.getWritableDatabase().beginTransaction();
        Log.d(TAG,"SIZE DATA "+data.size());
        for (ContactDB prod: data) {
            statement.clearBindings();
            statement.bindString(1, prod.customerID);
            statement.bindString(2, prod.name);
            statement.bindString(3, prod.type);
            try {
                statement.execute();
                Log.d(TAG,"Insert => category "+ prod.customerID +" | group "+prod.type+" | name "+prod.name);
            }catch (SQLiteConstraintException e){
                Log.e(TAG,"ERROR INSERT "+prod.customerID+" <> "+ prod.name +" >> "+ Objects.requireNonNull(e.getMessage()));
            }
        }
        pDB.getWritableDatabase().setTransactionSuccessful();
        pDB.getWritableDatabase().endTransaction();
        pDB.close();
    }


    public ArrayList<ContactDB> getData(Context context){
        ArrayList<ContactDB> data = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +" ORDER BY "+NAME, null);
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
