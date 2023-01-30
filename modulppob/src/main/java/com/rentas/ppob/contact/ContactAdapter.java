package com.rentas.ppob.contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;
import com.rentas.ppob.libs.Utility;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.AdapterView>{

    private ArrayList<Bundle> list;
    private Context context;

    ContactAdapter(Context context, ArrayList<Bundle> pList){
        list = pList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_adapter_main, parent, false);
        return new AdapterView(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView view, int i) {
        Bundle data = list.get(i);
        String name = data.getString("name").trim();
        String customerId = data.getString("customer_id");

        view.txvw_name.setText(Utility.UpperAfterSpace(name));
        view.txvw_initial.setText(getInitial(name));
        view.txvw_number.setText(customerId);

        view.rvly_more.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onMore(data, i);
            }
        });
        view.rvly_root.setOnClickListener(v -> {
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
        TextView txvw_initial,txvw_name, txvw_number;
        RelativeLayout rvly_more,rvly_root;

        AdapterView(@NonNull View itemView) {
            super(itemView);
            txvw_initial = itemView.findViewById(R.id.txvw_initial);
            txvw_name = itemView.findViewById(R.id.txvw_name);
            txvw_number = itemView.findViewById(R.id.txvw_number);
            rvly_more = itemView.findViewById(R.id.rvly_more);
            rvly_root = itemView.findViewById(R.id.rvly_root);

            txvw_initial.setBackground(Utility.getOvalBackground(context.getResources().getColor(R.color.colorPrimary)));
        }
    }

    private String getInitial(String name){
        name = name.trim();
        name = name.replaceAll("\\s+"," ");
        String[] initArr = name.split(" ");
        if (initArr.length > 1 ){
            return initArr[0].substring(0,1).toUpperCase()+"" +initArr[1].substring(0,1).toUpperCase();
        }
        else if (name.length() > 2){
            return initArr[0].substring(0,1).toUpperCase()+"" +initArr[0].substring(1,2).toUpperCase();
        }
        else {
            return name.toUpperCase();
        }
    }


    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(Bundle data, int position);
        void onMore(Bundle data, int position);
    }
}
