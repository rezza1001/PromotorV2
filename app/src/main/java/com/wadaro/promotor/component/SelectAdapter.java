package com.wadaro.promotor.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.wadaro.promotor.R;
import com.wadaro.promotor.module.Utility;

import java.util.ArrayList;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.OptionView>{

    private ArrayList<SelectHolder> list;
    private String key = "";
    private Context mContext;

    public  SelectAdapter(Context pContext, ArrayList<SelectHolder> pList ){
        list     = pList;
        mContext = pContext;
    }

    @NonNull
    @Override
    public OptionView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_select_adapter, parent, false);
        return new OptionView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionView holder, final int position) {
        final SelectHolder option = list.get(position);
        int start   = option.value.toUpperCase().indexOf(key.toUpperCase());
        int end     = start+ (key.length());
        if (start >= 0){
            if (mContext != null && option.value != null){
                holder.txvw_value_00.setText(Utility.BoldText(mContext, option.value,start,end));
            }
        }
        else {
            holder.txvw_value_00.setText(option.value);
        }

        holder.mrly_action_00.setOnClickListener(v -> {
            if (mListener != null){
                mListener.onSelect(option, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setKey(String pKey){
        key = pKey;
    }

    public class OptionView extends RecyclerView.ViewHolder{

        private TextView txvw_value_00;
        private MaterialRippleLayout mrly_action_00;
        public OptionView(View itemView) {
            super(itemView);
            txvw_value_00   =   itemView.findViewById(R.id.txvw_gov_00);
            mrly_action_00  =   itemView.findViewById(R.id.mrly_action_00);
        }
    }

    private OnSelectedListener mListener;
    public void setOnSelectedListener(OnSelectedListener pListener){
        mListener = pListener;
    }
    public interface OnSelectedListener{
         void onSelect(SelectHolder pData, int position);
    }
}
