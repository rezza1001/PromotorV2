package com.wadaro.promotor.ui.auth;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.wadaro.promotor.module.dialog.IMyDialog;
import com.wadaro.promotor.module.dialog.MyDialog;

/**
 * Created by pho0890910 on 2/21/2019.
 */
public abstract class BaseUi extends AppCompatActivity implements IMyDialog {

    static long lastClickTime;

    public static boolean isFastDoubleClick()
    {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 400)
        {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void hideSoftKeyboard( Activity activity )
    {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try
        {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e)
        {}
    }

    @Override
    public synchronized void onBackPressed()
    {
        finish();
    }

    @Override
    public void aDialogLeft(int p_intDialogId, Object p_obj) {

    }

    @Override
    public void aDialogRight(int p_intDialogId, Object p_obj) {

    }

    @Override
    public void aDialogMid(int p_intDialogId, Object p_obj) {
        if(p_intDialogId == MyDialog.DIALOG_ID_ALERT){
            MyDialog.dialogDismiss();
        }

        // close App
        if (p_obj != null)
        {
            if (p_obj instanceof String)
            {
                String l_strClose = (String) p_obj;
                if (MyDialog.C_STR_CLOSE.equalsIgnoreCase(l_strClose))
                {
                    MyDialog.dialogDismiss();

                    // todo

//                    closeApp();
                }
            }
        }

    }

}
