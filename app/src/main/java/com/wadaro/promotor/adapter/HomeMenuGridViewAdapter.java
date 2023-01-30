package com.wadaro.promotor.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.model.ItemObject;

import java.util.List;

/**
 * Created by pho0890910 on 2/23/2019.
 */
public class HomeMenuGridViewAdapter extends RecyclerView.Adapter<HomeMenuGridViewAdapter.ViewHolder> {

    public interface OnRecyclerViewClickListener {
        void onItemClick(Integer position);
    }

    private List<ItemObject> items;
    private Activity activity;

//    private final OnItemClickListener listener;
    private static OnRecyclerViewClickListener listener;

    public void setOnItemClickListener(OnRecyclerViewClickListener listener){
        this.listener = listener;
    }

    public HomeMenuGridViewAdapter(Activity activity, List<ItemObject> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_with_text_image, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeMenuGridViewAdapter.ViewHolder viewHolder, final int position) {

        int imageResourceId = this.activity.getResources().getIdentifier(items.get(position).getImageResource(),
                "drawable", this.activity.getPackageName());

        viewHolder.imageView.setImageResource(imageResourceId);
        viewHolder.textView.setText(items.get(position).getContent());

        viewHolder.relLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private RelativeLayout relLayout;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.textView);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            relLayout = (RelativeLayout) view.findViewById(R.id.rlGrid);

        }
    }
}
