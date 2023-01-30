package com.wadaro.promotor.ui.demo.process;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.util.MyCurrency;

import java.util.ArrayList;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.AdapterView> {

    private ArrayList<Bundle> mList;
    private Context mContext;
    private boolean hideDelete = false;

    public OrderAdapter(Context context, ArrayList<Bundle> data){
        mList = data;
        mContext = context;
    }
    public void hideAction(){
        hideDelete = true;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.demo_process_adapter_order, parent, false);
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
        holder.txvw_consumen_00.setText(data.getString("consumen_name"));
        holder.txvw_product_00.setText(data.getString("product_name"));
        holder.txvw_qty_00.setText(data.getString("qty"));
        holder.txvw_price_00.setText(MyCurrency.toCurrnecy(data.getString("price")));
        holder.txvw_subtotal_00.setText(MyCurrency.toCurrnecy(data.getString("subtotal")));
        if (hideDelete){
            holder.imvw_delete_00.setVisibility(View.GONE);
        }
        holder.imvw_delete_00.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onDelete(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class AdapterView extends RecyclerView.ViewHolder{
        private TextView txvw_consumen_00,txvw_product_00,txvw_qty_00,txvw_price_00,txvw_subtotal_00;
        private ImageView imvw_delete_00;
        private LinearLayout lnly_body_00;

        AdapterView(View itemView) {
            super(itemView);
            lnly_body_00        = itemView.findViewById(R.id.lnly_body_00);
            imvw_delete_00      = itemView.findViewById(R.id.imvw_delete_00);
            txvw_consumen_00    = itemView.findViewById(R.id.txvw_consumen_00);
            txvw_product_00     = itemView.findViewById(R.id.txvw_product_00);
            txvw_qty_00         = itemView.findViewById(R.id.txvw_qty_00);
            txvw_price_00       = itemView.findViewById(R.id.txvw_price_00);
            txvw_subtotal_00       = itemView.findViewById(R.id.txvw_subtotal_00);

        }
    }

    private OnDeleteListener onSelectedListener;
    public void setOnOnDeleteListener(OnDeleteListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnDeleteListener{
        void onDelete(Bundle data);
    }
}
