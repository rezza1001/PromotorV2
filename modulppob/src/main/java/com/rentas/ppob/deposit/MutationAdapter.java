package com.rentas.ppob.deposit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;
import com.rentas.ppob.libs.MyCurrency;

import java.util.ArrayList;

public class MutationAdapter extends RecyclerView.Adapter<MutationAdapter.AdapterView>{

    private ArrayList<Bundle> list;
    private Context context;

    MutationAdapter(Context context, ArrayList<Bundle> pList){
        list = pList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deposit_adapter_mutation, parent, false);
        return new AdapterView(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView view, int i) {
        Bundle data = list.get(i);
        view.txvw_description.setText(data.getString("description"));
        view.txvw_date.setText(data.getString("date"));
        view.txvw_date.setText(data.getString("date"));
        view.txvw_changeBalance.setText(data.getString("nominal"));
        view.txvw_lastBalance.setText(data.getString("last_balance"));
        view.txvw_currBalance.setText(data.getString("current_balance"));

        if (data.getInt("type") == 1){
            view.txvw_changeBalance.setTextColor(context.getResources().getColor(R.color.green));
        }
        else {
            view.txvw_changeBalance.setTextColor(context.getResources().getColor(R.color.font_orange));
        }
        view.rvly_nominal.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AdapterView extends RecyclerView.ViewHolder{
        RelativeLayout rvly_nominal;
        TextView txvw_description,txvw_date,txvw_changeBalance,txvw_lastBalance,txvw_currBalance;

        AdapterView(@NonNull View itemView) {
            super(itemView);
            rvly_nominal  = itemView.findViewById(R.id.rvly_nominal);
            txvw_description = itemView.findViewById(R.id.txvw_description);
            txvw_date = itemView.findViewById(R.id.txvw_date);
            txvw_changeBalance = itemView.findViewById(R.id.txvw_changeBalance);
            txvw_lastBalance = itemView.findViewById(R.id.txvw_lastBalance);
            txvw_currBalance = itemView.findViewById(R.id.txvw_currBalance);
        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(Bundle data, int position);
    }
}
