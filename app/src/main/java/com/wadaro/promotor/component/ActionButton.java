package com.wadaro.promotor.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyLayout;
import com.wadaro.promotor.module.Utility;

public class ActionButton extends MyLayout {

    private MaterialRippleLayout mrly_action_00;
    private TextView txvw_action_00;
    public ActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.component_button;
    }

    @Override
    protected void initLayout() {
        mrly_action_00 = findViewById(R.id.mrly_action_00);
        txvw_action_00 = findViewById(R.id.txvw_action_00);
        mrly_action_00.setBackground(Utility.getRectBackground("CCCCCC",Utility.dpToPx(mActivity,5)));
    }

    @Override
    protected void initListener() {
        mrly_action_00.setOnClickListener(v -> {
            if (mOnClickListener != null){
                mOnClickListener.onClick();
            }
        });

    }

    public void setColor(String code){
        mrly_action_00.setBackground(Utility.getRectBackground(code,Utility.dpToPx(mActivity,5)));
    }

    public void setText(String text){
        txvw_action_00.setText(text);
    }


    private OnClickListener mOnClickListener;
    public void setOnclikLIstener(OnClickListener pOnclikLIstener){
        mOnClickListener = pOnclikLIstener;
    }
    public interface OnClickListener{
        void onClick();
    }
}
