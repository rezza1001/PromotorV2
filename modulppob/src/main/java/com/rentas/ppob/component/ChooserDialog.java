package com.rentas.ppob.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;
import com.rentas.ppob.master.MyDialog;

import java.util.ArrayList;

public class ChooserDialog extends MyDialog {

    private TextView txvw_title_00;
    private RecyclerView rcvw_data;
    private ArrayList<Bundle> list;

    public ChooserDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.component_dialog_chooser;
    }

    @Override
    protected void initLayout(View view) {
        txvw_title_00 = view.findViewById(R.id.txvw_title_00);
        rcvw_data = view.findViewById(R.id.rcvw_data);
        list = new ArrayList<>();

        view.findViewById(R.id.rvly_root).setOnClickListener(v -> dismiss());
    }

    public void show(String title, ArrayList<Bundle> data) {
        super.show();
        if (list == null){
            list = data;
        }
        txvw_title_00.setText(title);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));
        ChooserAdapter adapter = new ChooserAdapter(list);
        rcvw_data.setAdapter(adapter);

    }

    public void show(String title) {
        show(title, list);
    }

    public void add(String key, String value){
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        bundle.putString("value", value);
        list.add(bundle);
    }
    public void add(String key, String value, String tag){
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        bundle.putString("value", value);
        bundle.putString("tag", tag);
        list.add(bundle);
    }

    class ChooserAdapter extends RecyclerView.Adapter<ChooserAdapter.AdapterView> {

        ChooserAdapter(ArrayList<Bundle> pList) {
            list = pList;
        }

        @NonNull
        @Override
        public ChooserAdapter.AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_adapter_chooser, parent, false);
            return new ChooserAdapter.AdapterView(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ChooserAdapter.AdapterView view, int i) {
            Bundle data = list.get(i);
            String value = data.getString("value");
            view.txvw_name.setText(value);

            view.rvly_root.setOnClickListener(v -> {
                if (onSelectedListener != null){
                    onSelectedListener.onSelected(data);
                    dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class AdapterView extends RecyclerView.ViewHolder {
            TextView txvw_name;
            RelativeLayout rvly_root;

            AdapterView(@NonNull View itemView) {
                super(itemView);
                rvly_root = itemView.findViewById(R.id.rvly_root);
                txvw_name = itemView.findViewById(R.id.txvw_name);
            }
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(Bundle data);
    }
}
