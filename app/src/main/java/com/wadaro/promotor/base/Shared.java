package com.wadaro.promotor.base;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import com.wadaro.promotor.model.UserInformation;

/**
 * Created by pho0890910 on 2/20/2019.
 */
public class Shared {

    private static ContextWrapper instance;
    private static SharedPreferences pref;

    private static final String PREF_NAME = "erpwadaro";

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_SESSIONID = "sessionid";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USERLEVEL = "userlevel";

    public static void initialize( Context base )
    {
        instance = new ContextWrapper(base);
        pref = instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static Context getBaseContext()
    {
        return instance.getBaseContext();
    }

    public static boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public static UserInformation getUserInformation(){

        String sessionId = pref.getString(KEY_SESSIONID, null);
        String userName = pref.getString(KEY_USERNAME, null);
        Integer userLvl = pref.getInt(KEY_USERLEVEL, 0);

        UserInformation user = new UserInformation(sessionId, userName, userLvl);

        // return user
        return user;
    }

    public static void clear()
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

}
