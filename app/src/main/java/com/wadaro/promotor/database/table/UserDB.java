package com.wadaro.promotor.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wadaro.promotor.database.DatabaseManager;
import com.wadaro.promotor.database.MasterDB;

import java.util.Objects;

public class UserDB extends MasterDB {

    public static final String TAG          = "UserDB";
    public static final String TABLE_NAME   = "USER";

    public static final String USER_ID      = "user_id";
    public static final String USER_NAME      = "user_name";
    public static final String GROUP        = "user_group";
    public static final String COMPANY      = "company_id";
    public static final String BRANCH       = "branch_id";
    public static final String COMPANY_NAME = "company_name";
    public static final String BRANCH_NAME  = "branch_name";
    public static final String BRANCH_TYPE  = "branch_type";
    public static final String ORGANIZATION = "organization_id";
    public static final String NAME = "employee_name";
    public static final String PHONE = "employee_phone";
    public static final String EMAIL = "employee_email";
    public static final String PHOTO = "employee_photo";
    public static final String TOKEN = "token";
    public static final String EMPLOYEE = "employee_id";

    public String user_id     = "";
    public String employee_id  = "";
    public String name  = "";
    public String email  = "";
    public String photo  = "";
    public String phone  = "";
    public String organization_id  = "";
    public String branch_type  = "";
    public String branch_name  = "";
    public String branch_id  = "";
    public String company_id  = "";
    public String company_name  = "";
    public String token  = "";
    public String group  = "";
    public String user_name  = "";


    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + USER_ID    + " varchar(30) NULL," +
                " " + USER_NAME    + " varchar(50) NULL," +
                " " + GROUP    + " varchar(50) NULL," +
                " " + COMPANY    + " varchar(30) NULL," +
                " " + BRANCH    + " varchar(30) NULL," +
                " " + COMPANY_NAME    + " varchar(70) NULL," +
                " " + BRANCH_NAME    + " varchar(70) NULL," +
                " " + BRANCH_TYPE    + " varchar(70) NULL," +
                " " + ORGANIZATION    + " varchar(30) NULL," +
                " " + EMPLOYEE    + " varchar(50) NULL," +
                " " + NAME    + " varchar(50) NULL," +
                " " + PHONE    + " varchar(20) NULL," +
                " " + EMAIL    + " varchar(50) NULL," +
                " " + PHOTO    + " varchar(100) NULL," +
                " " + TOKEN    + " varchar(100) NULL" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected MasterDB build(Cursor res) {
        return null;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.user_id    = res.getString(res.getColumnIndex(USER_ID));
        this.user_name    = res.getString(res.getColumnIndex(USER_NAME));
        this.company_id    = res.getString(res.getColumnIndex(COMPANY));
        this.branch_id    = res.getString(res.getColumnIndex(BRANCH));
        this.company_name    = res.getString(res.getColumnIndex(COMPANY_NAME));
        this.branch_name   = res.getString(res.getColumnIndex(BRANCH_NAME));
        this.branch_type    = res.getString(res.getColumnIndex(BRANCH_TYPE));
        this.organization_id    = res.getString(res.getColumnIndex(ORGANIZATION));
        this.employee_id   = res.getString(res.getColumnIndex(EMPLOYEE));
        this.name    = res.getString(res.getColumnIndex(NAME));
        this.phone    = res.getString(res.getColumnIndex(PHONE));
        this.email    = res.getString(res.getColumnIndex(EMAIL));
        this.photo    = res.getString(res.getColumnIndex(PHOTO));
        this.token    = res.getString(res.getColumnIndex(TOKEN));
        this.group    = res.getString(res.getColumnIndex(GROUP));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_ID, user_id);
        contentValues.put(COMPANY, company_id);
        contentValues.put(BRANCH, branch_id);
        contentValues.put(COMPANY_NAME, company_name);
        contentValues.put(BRANCH_NAME, branch_name);
        contentValues.put(BRANCH_TYPE, branch_type);
        contentValues.put(ORGANIZATION, organization_id);
        contentValues.put(EMPLOYEE, employee_id);
        contentValues.put(NAME, name);
        contentValues.put(PHONE, phone);
        contentValues.put(EMAIL, email);
        contentValues.put(PHOTO, photo);
        contentValues.put(TOKEN, token);
        contentValues.put(GROUP, group);
        contentValues.put(USER_NAME, user_name);
        return contentValues;
    }

    @Override
    public boolean insert(Context context) {
        clearData(context);
        return super.insert(context);
    }

    public void getData(Context context){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME , null);
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
