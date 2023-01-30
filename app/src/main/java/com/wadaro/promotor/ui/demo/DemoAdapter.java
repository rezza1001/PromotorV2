package com.wadaro.promotor.ui.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;

import java.util.ArrayList;


public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.AdapterView> {
    private static final String TAG = "FIndAdapter";

    private ArrayList<DemoHolder> mList;
    private Context mContext;

    public DemoAdapter(Context context, ArrayList<DemoHolder> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.demo_adapter, parent, false);
        return new AdapterView(itemView);
    }

    public DemoHolder getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final DemoHolder data = mList.get(position);
        holder.txvw_demo_00.setText(data.id);
        holder.txvw_time_00.setText("Demo "+data.demo+" - "+data.time);
        holder.txvw_coordinator_00.setText(data.coordinator);
        holder.txvw_address_00.setText(data.address);
        holder.txvw_note_00.setText(data.note);
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

    class AdapterView extends RecyclerView.ViewHolder{
        private TextView txvw_demo_00, txvw_time_00,txvw_coordinator_00,txvw_address_00,txvw_note_00;
        private LinearLayout lnly_root_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_demo_00       = itemView.findViewById(R.id.txvw_demo_00);
            txvw_time_00  = itemView.findViewById(R.id.txvw_time_00);
            txvw_coordinator_00   = itemView.findViewById(R.id.txvw_coordinator_00);
            txvw_address_00   = itemView.findViewById(R.id.txvw_address_00);
            lnly_root_00   = itemView.findViewById(R.id.lnly_root_00);
            txvw_note_00   = itemView.findViewById(R.id.txvw_note_00);

        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(DemoHolder data);
    }
}
