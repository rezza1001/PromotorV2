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
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.libs.Utility;

import java.util.ArrayList;

public class NominalAdapter extends RecyclerView.Adapter<NominalAdapter.AdapterView>{

    private ArrayList<Bundle> list;
    private Context context;

    NominalAdapter(Context context, ArrayList<Bundle> pList){
        list = pList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.paketdata_adapter_nominal, parent, false);
        return new AdapterView(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView view, int i) {
        Bundle nominal = list.get(i);
        view.txvw_nominal.setText(nominal.getString("name"));
        view.txvw_description.setText(nominal.getString("description"));

        if (nominal.getString("status").equals("1")){
            view.txvw_status.setText("Tersedia");
            view.card_nominal.setCardBackgroundColor(Color.WHITE);
            view.txvw_buy.setBackground(Utility.getShapeLine((Activity) context,1,5,context.getResources().getColor(R.color.font_orange),0));
            view.txvw_buy.setText("BELI");
            view.txvw_buy.setTextColor(context.getResources().getColor(R.color.font_orange));
        }
        else {
            view.txvw_status.setText("Gangguan");
            view.card_nominal.setCardBackgroundColor(Color.LTGRAY);
            view.txvw_buy.setBackground(Utility.getShapeLine((Activity) context,1,5,context.getResources().getColor(R.color.colorPrimary),0));
            view.txvw_buy.setText("INFO");
            view.txvw_buy.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        view.txvw_buy.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(nominal, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class AdapterView extends RecyclerView.ViewHolder{
        CardView card_nominal;
        TextView txvw_nominal,txvw_status,txvw_buy,txvw_description;

        AdapterView(@NonNull View itemView) {
            super(itemView);
            card_nominal  = itemView.findViewById(R.id.card_nominal);
            txvw_nominal = itemView.findViewById(R.id.txvw_nominal);
            txvw_status = itemView.findViewById(R.id.txvw_status);
            txvw_buy = itemView.findViewById(R.id.txvw_buy);
            txvw_description = itemView.findViewById(R.id.txvw_description);
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
