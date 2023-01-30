package com.rentas.ppob.trans.paketdata;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;
import com.rentas.ppob.libs.Utility;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.AdapterView>{

    private ArrayList<Bundle> list;
    private Context context;

    GroupAdapter(Context context, ArrayList<Bundle> pList){
        list = pList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trans_paketdata_adapter_group, parent, false);
        return new AdapterView(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView view, int i) {
        Bundle nominal = list.get(i);
        view.txvw_groupName.setText(nominal.getString("name"));

        if (nominal.getBoolean("open")){
            view.card_body.setCardBackgroundColor(context.getResources().getColor(R.color.profile_back_color));
        }
        else {
            view.card_body.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        }
        view.card_body.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(nominal, nominal.getBoolean("open"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AdapterView extends RecyclerView.ViewHolder{
        CardView card_body;
        TextView txvw_groupName;

        AdapterView(@NonNull View itemView) {
            super(itemView);
            card_body  = itemView.findViewById(R.id.card_body);
            txvw_groupName = itemView.findViewById(R.id.txvw_groupName);
        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(Bundle data, boolean expand);
    }
}
