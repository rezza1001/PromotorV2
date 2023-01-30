package com.wadaro.promotor.module.find;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;

import java.util.ArrayList;


public class FindAdapter extends RecyclerView.Adapter<FindAdapter.AdapterView> {
    private static final String TAG = "FIndAdapter";

    private ArrayList<FindHolder> mList;

    FindAdapter(ArrayList<FindHolder> data){
        mList = data;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_find_adapter, parent, false);
        return new AdapterView(itemView);
    }

    public FindHolder getData(int position){
        return mList.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final FindHolder data = mList.get(position);
        holder.txvw_value_00.setText(data.value);
        if (data.isSelected()){
            holder.imvw_checked_00.setImageResource(R.drawable.ic_check);
        }
        else {
            holder.imvw_checked_00.setImageResource(0);
        }

        holder.rvly_root_00.setOnClickListener(v -> {
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
        private TextView txvw_value_00;
        private ImageView imvw_checked_00;
        private RelativeLayout rvly_root_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_value_00   = itemView.findViewById(R.id.txvw_value_00);
            imvw_checked_00 = itemView.findViewById(R.id.imvw_checked_00);
            rvly_root_00 = itemView.findViewById(R.id.rvly_root_00);

        }
    }

    private OnSelectedListener onSelectedListener;
    void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(FindHolder data);
    }
}
