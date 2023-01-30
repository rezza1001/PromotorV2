package com.wadaro.promotor.ui.commission;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;

import java.util.ArrayList;


public class CommissionAdapter extends RecyclerView.Adapter<CommissionAdapter.AdapterView> {

    private ArrayList<CommissionHolder> mList;
    public CommissionAdapter(ArrayList<CommissionHolder> data){
        mList = data;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.commission_adapter_main, parent, false);
        return new AdapterView(itemView);
    }

    public CommissionHolder getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final CommissionHolder data = mList.get(position);
        holder.txvw_qty_00.setText(data.qtyUnit+"");
        holder.txvw_commission_00.setText(data.commission+"");
        holder.txvw_date_00.setText(data.getDate());
        if (position % 2 == 0){
            holder.lnly_body_00.setBackgroundColor(Color.WHITE);
        }
        else {
            holder.lnly_body_00.setBackgroundColor(Color.parseColor("#33228cc9"));
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class AdapterView extends RecyclerView.ViewHolder{
        private final TextView txvw_date_00, txvw_qty_00, txvw_commission_00;
        private final LinearLayout lnly_body_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_date_00    = itemView.findViewById(R.id.txvw_date_00);
            txvw_qty_00     = itemView.findViewById(R.id.txvw_qty_00);
            lnly_body_00    = itemView.findViewById(R.id.lnly_body_00);
            txvw_commission_00 = itemView.findViewById(R.id.txvw_commission_00);

        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(CommissionHolder data);
    }
}
