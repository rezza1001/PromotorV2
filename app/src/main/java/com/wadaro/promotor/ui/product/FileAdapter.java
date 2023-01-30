package com.wadaro.promotor.ui.product;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;

import java.util.ArrayList;


public class FileAdapter extends RecyclerView.Adapter<FileAdapter.AdapterView> {

    private ArrayList<Bundle> mList;

    public FileAdapter(ArrayList<Bundle> data){
        mList = data;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_adapter_file, parent, false);
        return new AdapterView(itemView);
    }

    public Bundle getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final Bundle data = mList.get(position);
        holder.txvw_name_00.setText("Informasi "+(position + 1));

        holder.lnly_root_00.setOnClickListener(v -> {
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
        private TextView txvw_name_00;
        private LinearLayout lnly_root_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_name_00   = itemView.findViewById(R.id.txvw_name_00);
            lnly_root_00   = itemView.findViewById(R.id.lnly_root_00);

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
