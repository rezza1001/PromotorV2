package com.wadaro.promotor.base;

import android.content.Context;

import com.wadaro.promotor.model.UserInformation;

/**
 * Created by pho0890910 on 2/20/2019.
 */
public class Global {

    public static UserInformation userInformation = null;

    public static void startAppIniData( Context p_context)
    {
        Shared.initialize(p_context);
    }

    public static void clearGlobalData(){
        userInformation = null;
    }

    public static String PREF_DATA_PROCESS_DEMO = "data assignment";
    public static String PREF_REPORT_SURVEY = "report survey";
    public static String PREF_DATA_RECAP = "report recap";
    public static String PREF_DATA_BOOKER = "data booker";

    public static String PREF_OFFLINE_MODE = "offline mode";
    public static String PREF_SYNCH_DATE = "syncronize date";
    public static String DATA_LOGIN = "login data";

}
