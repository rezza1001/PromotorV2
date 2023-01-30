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


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.AdapterView> {

    private ArrayList<Bundle> mList;
    private Context mContext;

    public OrderAdapter(Context context, ArrayList<Bundle> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_adapter_main, parent, false);
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
        holder.txvw_price_00.setText(MyCurrency.toCurrnecy(data.getString("subtotal")));
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
        private TextView txvw_consumen_00,txvw_product_00,txvw_qty_00,txvw_price_00;
        private LinearLayout lnly_body_00;

        AdapterView(View itemView) {
            super(itemView);
            lnly_body_00        = itemView.findViewById(R.id.lnly_body_00);
            txvw_consumen_00    = itemView.findViewById(R.id.txvw_consumen_00);
            txvw_product_00     = itemView.findViewById(R.id.txvw_product_00);
            txvw_qty_00         = itemView.findViewById(R.id.txvw_qty_00);
            txvw_price_00       = itemView.findViewById(R.id.txvw_price_00);

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
