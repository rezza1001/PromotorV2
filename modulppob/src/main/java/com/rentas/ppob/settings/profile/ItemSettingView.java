package com.rentas.ppob.settings.profile;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rentas.ppob.R;
import com.rentas.ppob.master.MyView;

public class ItemSettingView extends MyView {

    private RelativeLayout rvly_itemSetting;
    private ImageView imvw_settingIcon;
    private TextView txvw_settingText,txvw_settingDesc;

    private Bundle data = new Bundle();

    public ItemSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.setting_view_item;
    }

    @Override
    protected void initLayout() {
        rvly_itemSetting = findViewById(R.id.rvly_itemSetting);
        imvw_settingIcon = findViewById(R.id.imvw_settingIcon);
        txvw_settingText = findViewById(R.id.txvw_settingText);
        txvw_settingDesc = findViewById(R.id.txvw_settingDesc);
    }

    @Override
    protected void initListener() {
        rvly_itemSetting.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data);
            }
        });
    }

    public void create(int res, String title,String desc){
        imvw_settingIcon.setImageResource(res);
        txvw_settingText.setText(title);
        txvw_settingDesc.setText(desc);
    }

    public void setDescription(String description){
        txvw_settingDesc.setText(description);
    }

    public void setData(Bundle bundle){
        this.data = bundle;
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(Bundle bundle);
    }
}
