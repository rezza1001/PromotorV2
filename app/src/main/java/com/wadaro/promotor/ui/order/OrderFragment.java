package com.wadaro.promotor.ui.order;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.ObjectApi;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderFragment extends MyFragment {

    private Spinner spnr_demo_00;
    private RelativeLayout lnly_date_00;
    private TextView txvw_date_00;

    private ArrayList<Bundle> mOrders = new ArrayList<>();
    private SoAdapter mOrderAdapter;

    private HashMap<String,String> MAPS_DEMO = new HashMap<>();

    public static OrderFragment newInstance() {

        Bundle args = new Bundle();

        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setlayout() {
        return R.layout.order_fragmnet_main;
    }

    @Override
    protected void initLayout(View view) {
        spnr_demo_00 = view.findViewById(R.id.spDemo);
        lnly_date_00 = view.findViewById(R.id.lnly_date_00);
        txvw_date_00 = view.findViewById(R.id.txvw_date_00);

        ArrayList<String> tempDemo = new ArrayList<>();
        tempDemo.add("Demo 1");
        tempDemo.add("Demo 2");
        tempDemo.add("Demo 3");
        tempDemo.add("Demo 4");

        MAPS_DEMO.put("Demo 1","1");
        MAPS_DEMO.put("Demo 2","2");
        MAPS_DEMO.put("Demo 3","3");
        MAPS_DEMO.put("Demo 4","4");
        ArrayAdapter<String> adapterSpinnerDemo = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, tempDemo);
        adapterSpinnerDemo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_demo_00.setAdapter(adapterSpinnerDemo);
        spnr_demo_00.setSelection(0);

        RecyclerView rcvw_data_00 = view.findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        mOrderAdapter = new SoAdapter(mActivity, mOrders);
        rcvw_data_00.setAdapter(mOrderAdapter);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initListener() {
        Calendar calendar = Calendar.getInstance();
        txvw_date_00.setText(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DATE));
        lnly_date_00.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, (view, year, monthOfYear, dayOfMonth) -> {
                txvw_date_00.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                request();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            datePickerDialog.show();
        });

        spnr_demo_00.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                request();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mOrderAdapter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, SalesOrderDetail.class);
            intent.putExtra("data", data.getString("data"));
            startActivity(intent);
        });
    }

    private void request(){
        mOrders.clear();
        PostManager post  = new PostManager(mActivity, Config.SALES_ORDER_REPORT);
        post.addParam(new ObjectApi("booking_date",txvw_date_00.getText().toString()));
        post.addParam(new ObjectApi("booking_demo",MAPS_DEMO.get(spnr_demo_00.getSelectedItem())));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONArray data = obj.getJSONArray("data");
                    for (int i=0; i<data.length(); i++){
                        JSONObject jo = data.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("data", jo.toString());
                        bundle.putString("sales_id",jo.getString("sales_id"));
                        bundle.putString("sales_date",jo.getString("sales_date"));
                        bundle.putString("sales_status",jo.getString("sales_status"));
                        mOrders.add(bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mOrderAdapter.notifyDataSetChanged();
        });
    }

}
