package com.wadaro.promotor.module.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.module.log.MyLog;
import com.wadaro.promotor.util.GlobalHelper;


/**
 * Created by pho0890910 on 8/8/2018.
 */
public class MyDialog {

    public static final int DIALOG_ID_1 = 0x1001;
    public static final int DIALOG_ID_2 = 0x1002;
    public static final int DIALOG_ID_3 = 0x1003;
    public static final int DIALOG_ID_4 = 0x1004;
    public static final int DIALOG_ID_ERROR = 0x1005;
    public static final int DIALOG_ID_FORCE_LOGOUT = 0x1006;
    public static final int DIALOG_ID_CONFIRMATION = 0x1007;
    public static final int DIALOG_ID_RESULT = 0x1008;
    public static final int DIALOG_ID_ALERT = 0x1009;
    public static final int DIALOG_ID_PIN_BLOCKED = 0x1013;

    public static final String DIALOG_TAG_1 = "0x1001";
    public static final String DIALOG_TAG_2 = "0x1002";
    public static final String DIALOG_TAG_3 = "0x1003";
    public static final String DIALOG_TAG_4 = "0x1004";
    public static final String DIALOG_TAG_5 = "0x1005";
    public static final String DIALOG_TAG_6 = "0x1006";

    public static String C_STR_CLOSE = "close";

    static Dialog dialog;

    public static void dialogDismiss()
    {
        if (dialog != null)
        {
            try{
                dialog.cancel();
            } catch (Exception e){
                MyLog.error("Something wrong ",e);
            }

            dialog = null;
        }
    }

    public static void showDialog1Btn(Context context, final int intDialogId,
                                      String title, String content,
                                      final String btnText, final IMyDialog iMyDialog )
    {

        dialogDismiss();
        Button btnMid = null;
        TextView tvTitle = null;
        TextView tvContent = null;

        try{

            dialog = new Dialog(context, android.R.style.Theme_Material_NoActionBar);
//            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.color.dialog_background);
            dialog.setContentView(R.layout.dialog_1btn);

            if( ! GlobalHelper.isEmpty(title)){

                tvTitle = dialog.findViewById(R.id.tv_dialog_title);
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
            }

            tvContent = dialog.findViewById(R.id.tv_dialog_content);
            tvContent.setText(content);

            btnMid = dialog.findViewById(R.id.btn_dialog_mid);
            btnMid.setText(btnText);

            if(C_STR_CLOSE.equalsIgnoreCase(btnText)){
                // close App

            }

            btnMid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(C_STR_CLOSE.equalsIgnoreCase(btnText)){
                        iMyDialog.aDialogMid(intDialogId, C_STR_CLOSE);
                    } else {
                        iMyDialog.aDialogMid(intDialogId, null);
                    }
                }
            });

            dialog.setCancelable(false);
            dialog.show();

        } catch (Exception e){
            MyLog.error("showDialog1Btn. exception. ",e);
        }
    }

    public static void showDialog2Btn( Context context, final int intDialogId, String title,
                                       String content, String btnLeftText, String btnRightText,
                                       final IMyDialog iMyDialog ){

        dialogDismiss();
        Button btnLeft = null;
        Button btnRight = null;
        TextView tvTitle = null;
        TextView tvContent = null;

        try
        {
            dialog = new Dialog(context,android.R.style.Theme_Material_NoActionBar);
//            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.color.dialog_background);
            dialog.setContentView(R.layout.dialog_2btn);

            if( ! GlobalHelper.isEmpty(title)){

                tvTitle = dialog.findViewById(R.id.tv_dialog_title);
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
            }

            tvContent = dialog.findViewById(R.id.tv_dialog_content);
            tvContent.setText(content);

            btnLeft = dialog.findViewById(R.id.btn_dialog_left);
            btnLeft.setText(btnLeftText);
            btnLeft.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    iMyDialog.aDialogLeft(intDialogId, null);
                }
            });

            btnRight = (Button) dialog.findViewById(R.id.btn_dialog_right);
            btnRight.setText(btnRightText);
            btnRight.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    iMyDialog.aDialogRight(intDialogId, null);
                }
            });
            dialog.setCancelable(false);
            dialog.show();

        } catch (Exception e){
            MyLog.error("showDialog2Btn. exception. ",e);
        }

    }

    public static void updateButtonState(int viewId, boolean state) {
        Button btn = (Button)dialog.findViewById(viewId);
        btn.setEnabled(state);
    }

}
