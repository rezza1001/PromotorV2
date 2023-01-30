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

public class CategoryDB extends MasterDB {

    public static final String TAG          = "CategoryDB";
    public static final String TABLE_NAME   = "CategoryDB";

    public static final String ID = "category_id";
    public static final String NAME = "category_name";
    public static final String CODE = "category_code";

    public String id = "";
    public String name = "";
    public String code = "";

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID + " varchar(2) NULL," +
                " " + CODE + " varchar(10) NULL," +
                " " + NAME + " varchar(30) NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected CategoryDB build(Cursor res) {
        CategoryDB jp = new CategoryDB();
        jp.id = res.getString(res.getColumnIndex(ID));
        jp.code = res.getString(res.getColumnIndex(CODE));
        jp.name = res.getString(res.getColumnIndex(NAME));
        return jp;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.id = res.getString(res.getColumnIndex(ID));
        this.code = res.getString(res.getColumnIndex(CODE));
        this.name = res.getString(res.getColumnIndex(NAME));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(CODE, code);
        contentValues.put(NAME, name);
        return contentValues;
    }


    public void delete(Context context, String id) {
        super.delete(context, ID +"='"+id+"'");
    }

    @Override
    public boolean insert(Context context) {
        delete(context, id);
        return super.insert(context);
    }

    public void insertBulk(Context context, ArrayList<CategoryDB> data){
        clearData(context);
        DatabaseManager pDB = new DatabaseManager(context);
        String sql = "INSERT INTO "+ TABLE_NAME +" VALUES (?,?,?)";
        SQLiteStatement statement = pDB.getWritableDatabase().compileStatement(sql);
        pDB.getWritableDatabase().beginTransaction();

        for (CategoryDB prod: data) {
            statement.clearBindings();
            statement.bindString(1, prod.id);
            statement.bindString(2, prod.code);
            statement.bindString(3, prod.name);
            try {
                statement.execute();
                Log.d(TAG,"Insert => category "+ prod.id +" | group "+prod.code +" | name "+prod.name);
            }catch (SQLiteConstraintException e){
                Log.e(TAG,"ERROR INSERT "+prod.id +" <> "+ prod.name +" >> "+ Objects.requireNonNull(e.getMessage()));
            }
        }
        pDB.getWritableDatabase().setTransactionSuccessful();
        pDB.getWritableDatabase().endTransaction();
        pDB.close();
    }


    public ArrayList<CategoryDB> getData(Context context){
        ArrayList<CategoryDB> data = new ArrayList<>();

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

    public void getDataByCode(Context context, String pCode){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +" WHERE "+CODE+"='"+pCode+"'", null);
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
