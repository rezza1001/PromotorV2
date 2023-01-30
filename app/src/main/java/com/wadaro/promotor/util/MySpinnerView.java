package com.wadaro.promotor.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MySpinnerView extends MyLayout {

    private TextView txvw_key_00,txvw_mandatory_00;
    private Spinner spnr_data_00;
    private ArrayList<String> values = new ArrayList<>();
    private HashMap<String, String> mapsKeys = new HashMap<>();
    private JSONObject otherData = new JSONObject();
    private boolean activeListenerOnStart = false;

    public MySpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.libs_view_myspiner;
    }

    @Override
    protected void initLayout() {
        txvw_key_00 = findViewById(R.id.txvw_key_00);
        txvw_mandatory_00 = findViewById(R.id.txvw_mandatory_00);
        txvw_mandatory_00.setVisibility(GONE);

        spnr_data_00 = findViewById(R.id.spnr_data_00);

    }

    @Override
    protected void initListener() {
        spnr_data_00.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (onSelectedListener != null && activeListenerOnStart){
                    onSelectedListener.onSelected(getKeySelected(), getValueSelected());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setMandatory(){
        txvw_mandatory_00.setVisibility(VISIBLE);
    }

    public void setTitle(String key) {
        txvw_key_00.setText(key);
    }

    public void setDataBundle(ArrayList<Bundle> data){
        Log.d("MySpinnerView", "data "+ data.size());
        values = new ArrayList<>();
        for (Bundle bundle : data){
            values.add(bundle.getString("value"));
            mapsKeys.put(bundle.getString("value"),bundle.getString("key"));
            if (bundle.getString("other") != null){
                try {
                    otherData.put(Objects.requireNonNull(bundle.getString("key")),new JSONObject(Objects.requireNonNull(bundle.getString("other"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_data_00.setAdapter(adapter);
        setSelection(0);
        new Handler().postDelayed(() -> activeListenerOnStart = true,500);

    }

    public void setData(ArrayList<String> data){
        values = new ArrayList<>();
        for (String bundle : data){
            values.add(bundle);
            mapsKeys.put(bundle,bundle);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_data_00.setAdapter(adapter);
        setSelection(0);
        new Handler().postDelayed(() -> activeListenerOnStart = true,500);
    }

    public String getKeySelected(){
        String data = "";
        try {
            data = mapsKeys.get(spnr_data_00.getSelectedItem().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
    public String getValueSelected(){
        String data = "";
        try {
            data = spnr_data_00.getSelectedItem().toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    public void setSelection(String key){
        int position =0;
        for (String value: values){
            if (value.equals(key)){
                spnr_data_00.setSelection(position);
                break;
            }
            position++;
        }
    }
    public void setSelection(int index){
        spnr_data_00.setSelection(index);
        if (onSelectedListener != null && activeListenerOnStart){
            onSelectedListener.onSelected(getKeySelected(), getValueSelected());
        }
    }

    public JSONObject getOtherData(){
        if (otherData.has(getKeySelected())){
            try {
                return otherData.getJSONObject(getKeySelected());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }


    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(String key, String value);
    }
}
