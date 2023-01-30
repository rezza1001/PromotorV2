package com.wadaro.promotor.ui.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.util.MyCurrency;

import java.util.ArrayList;


public class SoAdapter extends RecyclerView.Adapter<SoAdapter.AdapterView> {

    private ArrayList<Bundle> mList;
    private Context mContext;

    public SoAdapter(Context context, ArrayList<Bundle> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_adapter_so, parent, false);
        return new AdapterView(itemView);
    }

    public Bundle getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final Bundle data = mList.get(position);
        if (position % 2 == 0){
            holder.lnly_body_00.setBackgroundColor(Color.parseColor("#f8f8f8"));
        }
        else {
            holder.lnly_body_00.setBackgroundColor(Color.parseColor("#eff2f1"));
        }
        holder.txvw_so_00.setText(data.getString("sales_id"));
        holder.txvw_date_00.setText(data.getString("sales_date"));
        holder.txvw_status_00.setText(data.getString("sales_status"));
        holder.lnly_body_00.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class AdapterView extends RecyclerView.ViewHolder{
        private TextView txvw_so_00, txvw_date_00, txvw_status_00;
        private LinearLayout lnly_body_00;

        AdapterView(View itemView) {
            super(itemView);
            lnly_body_00        = itemView.findViewById(R.id.lnly_body_00);
            txvw_so_00    = itemView.findViewById(R.id.txvw_so_00);
            txvw_date_00 = itemView.findViewById(R.id.txvw_date_00);
            txvw_status_00 = itemView.findViewById(R.id.txvw_status_00);

        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(Bundle data);
    }
}
