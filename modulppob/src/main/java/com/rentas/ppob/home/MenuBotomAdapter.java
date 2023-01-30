package com.rentas.ppob.home;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;

import java.util.ArrayList;

public class MenuBotomAdapter extends RecyclerView.Adapter<MenuBotomAdapter.AdapterView>{

    private ArrayList<MenuHolder> list;
    private Context context;

    MenuBotomAdapter(Context context, ArrayList<MenuHolder> pList){
        list = pList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_menu_adapterbottom, parent, false);
        return new AdapterView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterView view, int i) {
        MenuHolder menu = list.get(i);
        view.txvw_menu_00.setText(menu.name);
        Log.d("TAGRZ","menu "+menu.icon);
        int resDef = context.getResources().getIdentifier(menu.icon, "drawable", context.getPackageName());
        view.imvw_icon_00.setImageResource(resDef);
        if (menu.isSelected){
            view.imvw_icon_00.setColorFilter(Color.parseColor("#1b8bf0"));
            view.txvw_menu_00.setTextColor(Color.parseColor("#1b8bf0"));
        }
        else {
            view.imvw_icon_00.setColorFilter(Color.parseColor("#000000"));
            view.txvw_menu_00.setTextColor(Color.parseColor("#de000000"));
        }

        view.mrly_select_00.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(menu, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AdapterView extends RecyclerView.ViewHolder{
        RelativeLayout mrly_select_00;
        ImageView imvw_icon_00;
        TextView txvw_menu_00;

        AdapterView(@NonNull View itemView) {
            super(itemView);
            mrly_select_00  = itemView.findViewById(R.id.mrly_select_00);
            imvw_icon_00    = itemView.findViewById(R.id.imvw_icon_00);
            txvw_menu_00    = itemView.findViewById(R.id.txvw_menu_00);
        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(MenuHolder data, int position);
    }
}
