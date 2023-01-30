package com.rentas.ppob.settings.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.R;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.master.MyDialog;
import com.rentas.ppob.master.MyPreference;

import java.util.ArrayList;

public class CheckDialog extends MyDialog {

    private LinearLayout lnly_node,lnly_body;
    private TextView txvw_error,txvw_forgot;
    private StringBuffer mPin = new StringBuffer();
    public CheckDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.setting_pin_dialog_check;
    }

    @Override
    protected void initLayout(View view) {
        create(view);
        lnly_node = findViewById(R.id.lnly_node);
        lnly_body = findViewById(R.id.lnly_body);
        lnly_body.setVisibility(View.INVISIBLE);
        txvw_error = findViewById(R.id.txvw_error);
        txvw_error.setVisibility(View.INVISIBLE);
        txvw_forgot = findViewById(R.id.txvw_forgot);
    }

    @Override
    public void show() {
        super.show();
        lnly_body.setVisibility(View.VISIBLE);
        lnly_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));

        txvw_forgot.setOnClickListener(v -> {
            mActivity.startActivity(new Intent(mActivity, ResetPinActivity.class));
            dismiss();
        });

//        String pin = MainData.getPIN(mActivity);
//        if (pin.isEmpty()){
//            DynamicDialog dialog = new DynamicDialog(mActivity);
//            dialog.showError("Pengaturan PIN","Anda belum memilki PIN.\nBuat PIN baru sekarang");
//            dialog.setAction("Buat PIN");
//            dialog.setOnCloseLister(action -> {
//                mActivity.startActivity(new Intent(mActivity, ActivationActivity.class));
//                dialog.dismiss();
//            });
//
//            this.dismiss();
//        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void create(View view){
        RecyclerView rcvw_keypad = view.findViewById(R.id.rcvw_keypad);
        rcvw_keypad.setLayoutManager(new GridLayoutManager(mActivity,3));
        ArrayList<Bundle> listKeyPad = new ArrayList<>();
        KeyPadAdapter adapter = new KeyPadAdapter(mActivity, listKeyPad);
        rcvw_keypad.setAdapter(adapter);
        for (int i=1; i<=9; i++){
            listKeyPad.add(addKey(i+"",i));
        }
        listKeyPad.add(addKey("Batal",-1));
        listKeyPad.add(addKey("0",0));
        listKeyPad.add(addKey("Hapus",-2));
        adapter.notifyDataSetChanged();

        adapter.setOnSelectedListener((value, number) -> {
            if (txvw_error.getVisibility() == View.VISIBLE){
                txvw_error.setVisibility(View.INVISIBLE);
            }
            if (number >= 0 && mPin.length() <6){
                mPin.append(number);
                selectNode(mPin.length());
                if (mPin.length() == 6){
                    checkPin();
                }
            }
            else if (number == -2 && mPin.length() >0){
                removeNode(mPin.length());
                mPin.deleteCharAt(mPin.length()-1);
            }
            else if (number == -1){
                dismiss();
            }
        });
    }

    private Bundle addKey(String value, int num){
        Bundle bundle = new Bundle();
        bundle.putString("value",value);
        bundle.putInt("number",num);
        return bundle;
    }

    private void selectNode(int position){
        for (int i=0; i<lnly_node.getChildCount(); i++){
            ImageView imageView = (ImageView) lnly_node.getChildAt(i);
            if (position == i+1){
                imageView.setImageResource(R.drawable.pin_selected);
            }
        }
    }
    private void removeNode(int position){
        for (int i=0; i<lnly_node.getChildCount(); i++){
            ImageView imageView = (ImageView) lnly_node.getChildAt(i);
            if (position == i+1){
                imageView.setImageResource(R.drawable.pin_unselected);
            }
        }
    }

    private void checkPin(){
        String pin = MainData.getPIN(mActivity);
        if (pin.equals(mPin.toString())){
            if (onPinValidListener != null){
                onPinValidListener.onValid(mPin.toString());
                dismiss();
            }
        }
        else {
            txvw_error.setText("PIN yang Anda masukan salah !!");
            txvw_error.setVisibility(View.VISIBLE);
            txvw_error.clearAnimation();
            txvw_error.startAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.zoom_in));
        }
    }

    private OnPinValidListener onPinValidListener;
    public void setOnPinValidListener(OnPinValidListener onPinValidListener){
        this.onPinValidListener = onPinValidListener;
    }
    public interface OnPinValidListener{
        void onValid(String pin);
    }
}
