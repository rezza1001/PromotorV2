package com.wadaro.promotor.ui.demo.process.jp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;

import java.util.ArrayList;


public class ConsumerAdapter extends RecyclerView.Adapter<ConsumerAdapter.AdapterView> {
    private static final String TAG = "FIndAdapter";

    private ArrayList<ConsumerHolder> mList;
    private Context mContext;

    public ConsumerAdapter(Context context, ArrayList<ConsumerHolder> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.demo_process_adapter_addconsumer, parent, false);
        return new AdapterView(itemView);
    }

    public ConsumerHolder getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final ConsumerHolder data = mList.get(position);
        holder.txvw_name_00.setText(data.name);
        holder.txvw_phone_00.setText(data.phone);
        holder.txvw_identity_00.setText(data.address);

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

    class AdapterView extends RecyclerView.ViewHolder{
        private TextView txvw_name_00, txvw_phone_00,txvw_identity_00;
        private ImageView imvw_delete_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_name_00       = itemView.findViewById(R.id.txvw_name_00);
            txvw_phone_00  = itemView.findViewById(R.id.txvw_phone_00);
            txvw_identity_00   = itemView.findViewById(R.id.txvw_identity_00);
            imvw_delete_00   = itemView.findViewById(R.id.imvw_delete_00);

        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onDelete(ConsumerHolder data);
    }
}
