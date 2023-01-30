package com.rentas.ppob.report.trans;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;
import com.rentas.ppob.libs.VerticalTextView;

import java.util.ArrayList;
import java.util.Date;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.AdapterView>{

    private ArrayList<Bundle> list;
    private Context context;

    HistoryAdapter(Context context, ArrayList<Bundle> pList){
        list = pList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_trans_adapter_main, parent, false);
        return new AdapterView(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView view, int i) {
        Bundle data = list.get(i);
        view.txvw_category.setText(data.getString("category"));
        view.txvw_customer.setText(data.getString("invoice"));
        view.txvw_product.setText(data.getString("product"));
        view.txvw_date.setText(data.getString("date"));
        view.txvw_price.setText(data.getString("price"));
        view.txvw_status.setText(data.getString("status"));
        String status = data.getString("status");
        switch (status) {
            case "success":
                view.txvw_status.setText("Sukses");
                view.txvw_status.setBackgroundColor(Color.parseColor("#009C49"));
                break;
            case "process":
                view.txvw_status.setText("Proses");
                view.txvw_status.setBackgroundColor(Color.parseColor("#A4A4A4"));
                break;
            case "failed":
                view.txvw_status.setText("Gagal");
                view.txvw_status.setBackgroundColor(Color.parseColor("#B00020"));
                break;
        }

        view.rvly_trans.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data, i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class AdapterView extends RecyclerView.ViewHolder{
        TextView txvw_category,txvw_customer,txvw_product,txvw_date,txvw_price;
        VerticalTextView txvw_status;
        RelativeLayout rvly_trans;

        AdapterView(@NonNull View itemView) {
            super(itemView);
            txvw_category    = itemView.findViewById(R.id.txvw_category);
            txvw_customer    = itemView.findViewById(R.id.txvw_customer);
            txvw_product    = itemView.findViewById(R.id.txvw_product);
            txvw_date    = itemView.findViewById(R.id.txvw_date);
            txvw_price    = itemView.findViewById(R.id.txvw_price);
            txvw_status    = itemView.findViewById(R.id.txvw_status);
            rvly_trans    = itemView.findViewById(R.id.rvly_trans);
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
