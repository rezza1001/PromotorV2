package com.rentas.ppob.home;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;
import com.rentas.ppob.master.MyView;

import java.util.ArrayList;

public class BottomMenuView extends MyView {

    private boolean click_first=true;
    private int current_menu = 0;
    private ArrayList<MenuHolder> allMenu = new ArrayList<>();
    private MenuBotomAdapter botomAdapter;
    private RecyclerView rcvw_menu_00;

    public BottomMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected int setlayout() {
        return R.layout.home_menu_bottom;
    }

    protected void initLayout(){
        rcvw_menu_00 = findViewById(R.id.rcvw_menu_00);
        rcvw_menu_00.setLayoutManager(new GridLayoutManager(mActivity, 4));
    }

    @Override
    protected void initListener() {
    }

    public void create() {
        allMenu = new ArrayList<>();
        botomAdapter = new MenuBotomAdapter(mActivity, allMenu);
        rcvw_menu_00.setAdapter(botomAdapter);
        rcvw_menu_00.setNestedScrollingEnabled(false);

        allMenu.add(new MenuHolder(1,"Home","ic_home"));
        allMenu.add(new MenuHolder(2,"Transaksi","ic_transaction"));
        allMenu.add(new MenuHolder(3,"Kontak","ic_contact"));
        allMenu.add(new MenuHolder(4,"Pengaturan","ic_settings"));

        botomAdapter.notifyDataSetChanged();
        botomAdapter.setOnSelectedListener((data, position) -> {
            if (!click_first || data.id == current_menu){
                return;
            }
            setSelected(data.id);
            current_menu = data.id;
            click_first = false;
            new Handler().postDelayed(() -> click_first=true, 500);
            if (menuListener != null){
                menuListener.onSelectedMenu(current_menu);
            }
        });
    }

    public void setSelected(int id){
        for (int i=0; i<allMenu.size(); i++){
            allMenu.get(i).isSelected = allMenu.get(i).id == id;
        }
        botomAdapter.notifyDataSetChanged();
    }

    private OnSelectedMenuListener menuListener;
    public void setOnSelectedMenuListener(OnSelectedMenuListener onSelectedMenuListener){
        menuListener = onSelectedMenuListener;
    }
    public interface OnSelectedMenuListener{
        void onSelectedMenu(int position);
    }
}
