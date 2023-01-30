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

public class ProductDB extends MasterDB {

    public static final String TAG          = "ProductDB";
    public static final String TABLE_NAME   = "PRODUCT";

    public static final String CODE         = "code";
    public static final String PRODUCT_ID   = "productId";
    public static final String NOMINAL      = "denom";
    public static final String NAME         = "name";
    public static final String DESCRIPTION  = "description";
    public static final String CATEGORY     = "category";
    public static final String GROUP_ID     = "groupID";
    public static final String GROUP_NAME   = "groupName";
    public static final String STATUS       = "status";



    public String productCode   = "";
    public String nominal       = "";
    public String description   = "";
    public String status        = "";
    public String category      = "";
    public String groupName     = "";
    public String productId     = "";
    public String name          = "";
    public String groupID    = "";



    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + CODE + " varchar(50) NULL," +
                " " + NOMINAL + " varchar(100) NULL," +
                " " + DESCRIPTION    + " text NULL," +
                " " + STATUS    + " varchar(50) NULL," +
                " " + CATEGORY    + " varchar(50) NULL," +
                " " + GROUP_NAME + " varchar(50) NULL," +
                " " + PRODUCT_ID + " varchar(50) NULL," +
                " " + NAME + " text NULL," +
                " " + GROUP_ID + " varchar(20) NULL," +
                "  PRIMARY KEY (" + CODE +") )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CODE, productCode);
        contentValues.put(NOMINAL, nominal);
        contentValues.put(DESCRIPTION, description);
        contentValues.put(STATUS, status);
        contentValues.put(CATEGORY, category);
        contentValues.put(GROUP_NAME, groupName);
        contentValues.put(PRODUCT_ID, productId);
        contentValues.put(NAME, name);
        contentValues.put(GROUP_ID, groupID);
        return contentValues;
    }

    @Override
    public boolean insert(Context context) {
        delete(context, CODE +"='"+ productCode +"'");
        return super.insert(context);
    }


    public ArrayList<ProductDB> getAllData(Context context, String category){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME + " WHERE "
                +"("+CATEGORY+"='"+category+"' OR '"+category+"' = '-99')  AND "+STATUS+"!='0'"+
                " ORDER BY  "+ NOMINAL +" ASC ";
        Cursor res = db.rawQuery(query, null);
        ArrayList<ProductDB> all_notif = new ArrayList<>();
        Log.d(TAG, query);
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

    public ArrayList<ProductDB> getAllData(Context context, String category, String operator){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME + " WHERE "+CATEGORY+"='"+category+"' AND "+ GROUP_NAME +
                "='"+operator+"' AND "+STATUS+" != '0'"+
                " ORDER BY  CAST("+ NOMINAL +" AS INTEGER) ASC  ";
        Log.d(TAG, query);
        Cursor res = db.rawQuery(query, null);
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
    public ArrayList<ProductDB> getAllDataByGroup(Context context, String category, String operator){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME + " WHERE "+CATEGORY+"='"+category+"' AND "+ GROUP_NAME +
                " LIKE '%"+operator+"%' AND "+STATUS+" != '0'"+
                " ORDER BY  CAST("+ NOMINAL +" AS INTEGER) ASC  ";
        Log.d(TAG, query);
        Cursor res = db.rawQuery(query, null);
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

    public void getDataByCode(Context context, String code){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +" WHERE "+ CODE +"='"+code+"'", null);

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

    public void getDataById(Context context, String id){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String sql = "select *  from " + TABLE_NAME +" WHERE "+ PRODUCT_ID +"='"+id+"'";
        Log.d(TAG,sql);
        Cursor res = db.rawQuery(sql, null);
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

    protected ProductDB build(Cursor res){
        ProductDB notif = new ProductDB();
        notif.productCode = res.getString(res.getColumnIndex(CODE));
        notif.category  = res.getString(res.getColumnIndex(CATEGORY));
        notif.nominal = res.getString(res.getColumnIndex(NOMINAL));
        notif.status    = res.getString(res.getColumnIndex(STATUS));
        notif.description    = res.getString(res.getColumnIndex(DESCRIPTION));
        notif.groupName = res.getString(res.getColumnIndex(GROUP_NAME));
        notif.productId = res.getString(res.getColumnIndex(PRODUCT_ID));
        notif.name      = res.getString(res.getColumnIndex(NAME));
        notif.groupID        = res.getString(res.getColumnIndex(GROUP_ID));
        return notif;
    }

    protected void buildSingle(Cursor res){
        this.productCode    = res.getString(res.getColumnIndex(CODE));
        this.category       = res.getString(res.getColumnIndex(CATEGORY));
        this.nominal = res.getString(res.getColumnIndex(NOMINAL));
        this.status         = res.getString(res.getColumnIndex(STATUS));
        this.description    = res.getString(res.getColumnIndex(DESCRIPTION));
        this.groupName = res.getString(res.getColumnIndex(GROUP_NAME));
        this.productId      = res.getString(res.getColumnIndex(PRODUCT_ID));
        this.name           = res.getString(res.getColumnIndex(NAME));
        this.groupID        = res.getString(res.getColumnIndex(GROUP_ID));

        Log.d(TAG,"buildSingle => ID "+ productId +" | code "+productCode+" | name "+this.name);
    }

    public void insertBulk(Context context, ArrayList<ProductDB> data){
        DatabaseManager pDB = new DatabaseManager(context);
        String sql = "INSERT INTO "+ TABLE_NAME +" VALUES (?,?,?,?,?,?,?,?,?)";
        SQLiteStatement statement = pDB.getWritableDatabase().compileStatement(sql);
        pDB.getWritableDatabase().beginTransaction();
        Log.d(TAG,"SIZE DATA "+data.size());
        for (ProductDB prod: data) {
            statement.clearBindings();
            statement.bindString(1, prod.productCode);
            statement.bindString(2, prod.nominal);
            statement.bindString(3, prod.description);
            statement.bindString(4, prod.status);
            statement.bindString(5, prod.category);
            statement.bindString(6, prod.groupName);
            statement.bindString(7, prod.productId);
            statement.bindString(8, prod.name);
            statement.bindString(9, prod.groupID);
            try {
                statement.execute();
                Log.d(TAG,"Insert => category "+ prod.category +" | group "+prod.groupName+" | name "+prod.name);
            }catch (SQLiteConstraintException e){
                Log.e(TAG,"ERROR INSERT "+prod.category+" <> "+ prod.productCode +" >> "+ Objects.requireNonNull(e.getMessage()));
            }
        }
        pDB.getWritableDatabase().setTransactionSuccessful();
        pDB.getWritableDatabase().endTransaction();
        pDB.close();
    }

    public void deleteByCategory(Context context, String categoryID){
        delete(context,CATEGORY+"='"+categoryID+"'");
    }
    public void deleteByGroup(Context context, String categoryID, String group){
        delete(context,CATEGORY+"='"+categoryID+"' AND "+ GROUP_ID +"='"+group+"'");
    }

}

