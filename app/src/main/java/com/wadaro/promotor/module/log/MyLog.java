package com.wadaro.promotor.module.log;

import android.util.Log;

/**
 * Created by pho0890910 on 8/4/2018.
 */
public class MyLog {

    public static final String C_TAB_LOG = "ERP";
    public static void info( String p_strMsg )
    {
        String l_strMsg = "** Info ** " + p_strMsg;
        Log.i(C_TAB_LOG, l_strMsg);
    }

    public static void error( String p_strMsg, Throwable tr )
    {
        String l_strMsg = "** error ** " + p_strMsg;
        Log.e(C_TAB_LOG, l_strMsg, tr);
    }


}
