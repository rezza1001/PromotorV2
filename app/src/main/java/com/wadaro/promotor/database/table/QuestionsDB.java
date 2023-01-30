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

public class QuestionsDB extends MasterDB {


    public static final String TAG          = "QuestionsDB";
    public static final String TABLE_NAME   = "QUESTIONS";


    public static final String ID           = "id";
    public static final String GROUP         = "mgroup";
    public static final String QUESTION         = "question";
    public static final String ANSWER            = "answer";
    public static final String SCORING       = "scoring";

    public String id         = "";
    public String group      = "";
    public String question      = "";
    public String answer         = "";
    public int scoring     = 0;

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID    + " varchar(30) NULL," +
                " " + GROUP + " varchar(100) NULL," +
                " " + QUESTION + " varchar(200) NULL," +
                " " + ANSWER    + " varchar(200) NULL," +
                " " + SCORING    + " integer default 0 " +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    protected QuestionsDB build(Cursor res) {
        QuestionsDB visitDB = new QuestionsDB();
        visitDB.id = res.getString(res.getColumnIndex(ID));
        visitDB.group = res.getString(res.getColumnIndex(GROUP));
        visitDB.question = res.getString(res.getColumnIndex(QUESTION));
        visitDB.answer = res.getString(res.getColumnIndex(ANSWER));
        visitDB.scoring = res.getInt(res.getColumnIndex(SCORING));
        Log.d("TAGRZ", visitDB.id+" <> "+ visitDB.question+" <> "+ visitDB.answer);
        return visitDB;
    }

    @Override
    protected void buildSingle(Cursor res) {
        this.id = res.getString(res.getColumnIndex(ID));
        this.group = res.getString(res.getColumnIndex(GROUP));
        this.question = res.getString(res.getColumnIndex(QUESTION));
        this.answer = res.getString(res.getColumnIndex(ANSWER));
        this.scoring = res.getInt(res.getColumnIndex(SCORING));
    }

    public ContentValues createContentValues(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(GROUP, group);
        contentValues.put(QUESTION, question);
        contentValues.put(ANSWER, answer);
        contentValues.put(SCORING, scoring);
        return contentValues;
    }


    @Override
    public boolean insert(Context context) {
        delete(context, ID+"='"+id+"'");
        return super.insert(context);
    }

    public void insertBulk(Context context, ArrayList<QuestionsDB> data){
        DatabaseManager pDB = new DatabaseManager(context);
        String sql = "INSERT INTO "+ TABLE_NAME +" VALUES (?,?,?,?,?)";
        SQLiteStatement statement = pDB.getWritableDatabase().compileStatement(sql);
        pDB.getWritableDatabase().beginTransaction();
        for (QuestionsDB item: data) {
            statement.clearBindings();
            statement.bindString(1, item.id);
            statement.bindString(2, item.group);
            statement.bindString(3, item.question);
            statement.bindString(4, item.answer);
            statement.bindLong(5, item.scoring);
            try {
                statement.execute();
                Log.i(TAG,"INSERTED "+item.question+" : "+ item.answer);
            }catch (SQLiteConstraintException e){
                Log.e(TAG,"ERROR INSERT "+ item.question+" "+item.answer+" >> "+ Objects.requireNonNull(e.getMessage()));
            }
        }
        pDB.getWritableDatabase().setTransactionSuccessful();
        pDB.getWritableDatabase().endTransaction();
        pDB.close();
    }

    public void getData(Context context, String id){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +" WHERE "+ ID+"='"+id+"'", null);
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

    public ArrayList<QuestionsDB> getQuestion(Context context, String question){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select *  from " + TABLE_NAME +"  WHERE  ("+ QUESTION+"='"+question+"' OR '-99'='"+question+"')";
        Log.d(TAG, query);
        Cursor res = db.rawQuery(query , null);
        ArrayList<QuestionsDB> all_notif = new ArrayList<>();
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
        Log.d(TAG, "SIZE : "+all_notif.size());
        return all_notif;
    }

}
