package com.rentas.ppob.component;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rentas.ppob.R;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyView;

public class HeaderView extends MyView {

    private RelativeLayout rvly_mainHeader,rvly_backHeader;
    private ImageView imvw_backHeader;
    private TextView txvw_titleHeader,txvw_desc;
    private View view_line;

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.component_view_header;
    }

    @Override
    protected void initLayout() {
        rvly_mainHeader  = findViewById(R.id.rvly_mainHeader);
        imvw_backHeader  = findViewById(R.id.imvw_backHeader);
        txvw_titleHeader = findViewById(R.id.txvw_titleHeader);
        view_line        = findViewById(R.id.view_line);
        rvly_backHeader        = findViewById(R.id.rvly_backHeader);
        txvw_desc        = findViewById(R.id.txvw_desc);
    }

    @Override
    protected void initListener() {
        rvly_backHeader.setOnClickListener(v -> {
            if (onBackListener != null){
                onBackListener.onBack();
            }
        });
    }

    public void setPrimaryColor(){
        rvly_mainHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        imvw_backHeader.setColorFilter(getResources().getColor(R.color.white));
        txvw_titleHeader.setTextColor(Color.WHITE);
    }

    public void create(String title){
        txvw_titleHeader.setText(title);
    }
    public void create(String title, String desc){
        txvw_titleHeader.setText(title);
        txvw_desc.setText(desc);
        txvw_desc.setVisibility(VISIBLE);
    }

    public void hideBack(){
        rvly_backHeader.setVisibility(GONE);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) txvw_titleHeader.getLayoutParams();
        lp.leftMargin = Utility.dpToPx(mActivity,20);
        txvw_titleHeader.setLayoutParams(lp);
    }

    public void disableLine(){
        view_line.setVisibility(INVISIBLE);
    }


    private OnBackListener onBackListener;
    public void setOnBackListener(OnBackListener onBackListener){
        this.onBackListener = onBackListener;
    }
    public interface OnBackListener{
        void onBack();
    }
}
