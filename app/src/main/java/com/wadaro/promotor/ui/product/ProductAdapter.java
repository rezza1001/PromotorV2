package com.wadaro.promotor.ui.product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wadaro.promotor.R;
import com.wadaro.promotor.util.MyCurrency;

import java.util.ArrayList;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.AdapterView> {
    private static final String TAG = "FIndAdapter";

    private ArrayList<Bundle> mList;
    private Context mContext;

    public ProductAdapter(Context context, ArrayList<Bundle> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_adapter_main, parent, false);
        return new AdapterView(itemView);
    }

    public Bundle getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final Bundle data = mList.get(position);
        holder.txvw_product_00.setText(data.getString("product_name"));
        holder.txvw_price_00.setText(MyCurrency.toCurrnecy("Rp",data.getString("price")));
        Glide.with(mContext).load(data.getString("image")).into(holder.imvw_image_00);

        holder.rvly_body_00.setOnClickListener(v -> {
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
        private ImageView imvw_image_00;
        private TextView txvw_product_00, txvw_price_00;
        private RelativeLayout rvly_body_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_product_00 = itemView.findViewById(R.id.txvw_product_00);
            txvw_price_00 = itemView.findViewById(R.id.txvw_price_00);
            imvw_image_00   = itemView.findViewById(R.id.imvw_image_00);
            rvly_body_00   = itemView.findViewById(R.id.rvly_body_00);

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
