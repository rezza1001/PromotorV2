package com.rentas.ppob.master;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class MyPreference {

    private static final String TAG = "MyPreference";


    public static  void save(Context context, String key, String value){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();

        Log.d(TAG, "SAVE "+ key+" = "+ value);
    }
    public static  void save(Context context, String key, int value){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();

        Log.d(TAG, "SAVE "+ key+" = "+ value);
    }
    public static  void save(Context context, String key, long value){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.apply();

        Log.d(TAG, "SAVE "+ key+" = "+ value);
    }

    public static int getInt(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt(key,0);
    }
    public static long getLong(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getLong(key,0);
    }

    public static String getString(Context context, String key){
           SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String data = sharedPref.getString(key,"");
        Log.d(TAG, "GET "+ key+" = "+ data);
        return data;
    }

    public static void update(Context context, String key, String value){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putString(key, value).apply();
    }

    public static void update(Context context, String key, int value){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putInt(key, value).apply();
    }

    public static void delete(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().remove(key).apply();
    }

    public static void clear(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean status = sharedPref.edit().clear().commit();
        Log.d(TAG,"clear "+ status);
    }
}
