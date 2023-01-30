package com.wadaro.promotor.ui.demo.process;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;

import java.util.ArrayList;
import java.util.Objects;


public class ConsumerAdapter extends RecyclerView.Adapter<ConsumerAdapter.AdapterView> {
    private static final String TAG = "FIndAdapter";

    private ArrayList<Bundle> mList;

    public ConsumerAdapter(ArrayList<Bundle> data){
        mList = data;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.demo_process_adapter_consumer, parent, false);
        return new AdapterView(itemView);
    }

    public Bundle getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final Bundle data = mList.get(position);
        holder.txvw_name_00.setText(data.getString("name"));
        holder.txvw_phone_00.setText(getData(data,"phone"));
        holder.txvw_identity_00.setText(getData(data,"address"));

    }

    private String getData(Bundle bundle, String key){
        String data = "-";
        if (bundle != null){
            if (!Objects.equals(bundle.getString(key), "null") && !Objects.requireNonNull(bundle.getString(key)).isEmpty()){
                data = bundle.getString(key);
            }
        }
        return data;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class AdapterView extends RecyclerView.ViewHolder{
        private TextView txvw_name_00, txvw_phone_00,txvw_identity_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_name_00       = itemView.findViewById(R.id.txvw_name_00);
            txvw_phone_00  = itemView.findViewById(R.id.txvw_phone_00);
            txvw_identity_00   = itemView.findViewById(R.id.txvw_identity_00);

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
