package com.rentas.ppob.settings.pin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;
import com.rentas.ppob.libs.Utility;

import java.util.ArrayList;

public class KeyPadAdapter extends RecyclerView.Adapter<KeyPadAdapter.AdapterView>{

    private ArrayList<Bundle> list;
    private Context context;

    KeyPadAdapter(Context context, ArrayList<Bundle> pList){
        list = pList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_pin_adapter_keypad, parent, false);
        return new AdapterView(itemView);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull AdapterView view, int i) {
        Bundle menu = list.get(i);
        String keypad = menu.getString("value");
        int number = menu.getInt("number");
        view.txvw_key.setText(keypad);
        if (number < 0){
            Typeface font =  ResourcesCompat.getFont(context,R.font.roboto_medium);
            view.txvw_key.setTypeface(font);
            view.txvw_key.setTextSize(16);
        }
        else {
            Typeface font =  ResourcesCompat.getFont(context,R.font.roboto_bold);
            view.txvw_key.setTypeface(font);
            view.txvw_key.setTextSize(20);
        }

        view.txvw_key.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                if (onSelectedListener != null){
                    onSelectedListener.onSelected(keypad,number);
                }
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class AdapterView extends RecyclerView.ViewHolder{
        CardView card_body;
        TextView txvw_key;

        AdapterView(@NonNull View itemView) {
            super(itemView);
            card_body  = itemView.findViewById(R.id.card_body);
            txvw_key    = itemView.findViewById(R.id.txvw_key);


            card_body.setBackground(Utility.getShapeLine((Activity) context,1,6,Color.parseColor("#0b599a"),Color.WHITE));
        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(String value, int number);
    }
}
