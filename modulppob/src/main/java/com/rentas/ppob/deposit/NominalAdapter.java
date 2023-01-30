package com.rentas.ppob.deposit;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class NominalAdapter extends RecyclerView.Adapter<NominalAdapter.AdapterView>{

    private ArrayList<Long> list;
    private Context context;

    NominalAdapter(Context context, ArrayList<Long> pList){
        list = pList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deposit_adapter_nominal, parent, false);
        return new AdapterView(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView view, int i) {
        long nominal = list.get(i);
        view.txvw_nominal.setText(MyCurrency.toCurrnecy(nominal));


        view.rvly_nominal.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(nominal, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AdapterView extends RecyclerView.ViewHolder{
        RelativeLayout rvly_nominal;
        TextView txvw_nominal;

        AdapterView(@NonNull View itemView) {
            super(itemView);
            rvly_nominal  = itemView.findViewById(R.id.rvly_nominal);
            txvw_nominal = itemView.findViewById(R.id.txvw_nominal);
        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(long data, int position);
    }
}
