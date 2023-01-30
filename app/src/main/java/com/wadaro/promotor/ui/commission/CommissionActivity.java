package com.wadaro.promotor.ui.commission;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.MyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mochamad Rezza Gumilang on 15/Feb/2021.
 * Class Info :
 */

public class CommissionActivity extends MyActivity {

    private Spinner spnr_demo_00;
    private final ArrayList<CommissionHolder> arrayList = new ArrayList<>();
    private CommissionAdapter mAdapter ;
    private int sizePerPage = 10;
    private int page = 1;
    boolean isLoading = false;

    @Override
    protected int setLayout() {
        return R.layout.commission_activity_main;
    }

    @Override
    protected void initLayout() {
        spnr_demo_00 = findViewById(R.id.spnr_demo_00);

        RecyclerView rcvw_data_00 = findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new CommissionAdapter(arrayList);
        rcvw_data_00.setAdapter(mAdapter);

        rcvw_data_00.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == arrayList.size() - 1) {
                        isLoading = true;
                        rcvw_data_00.post(() -> rcvw_data_00.scrollToPosition(arrayList.size() - 1));
                        new Handler().postDelayed(() -> load(),500);
                    }
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        initMonth();

    }

    @Override
    protected void initListener() {
        findViewById(R.id.rvly_back_00).setOnClickListener(v -> onBackPressed());
        spnr_demo_00.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrayList.clear();
                load();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initMonth(){
        ArrayList<String> tempDemo = new ArrayList<>();
        tempDemo.add("Januari");
        tempDemo.add("Februari");
        tempDemo.add("Maret");
        tempDemo.add("April");
        tempDemo.add("Mei");
        tempDemo.add("Juni");
        tempDemo.add("Juli");
        tempDemo.add("Agustus");
        tempDemo.add("September");
        tempDemo.add("Oktober");
        tempDemo.add("November");
        tempDemo.add("Desember");

        ArrayAdapter<String> adapterSpinnerDemo = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, tempDemo);
        adapterSpinnerDemo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_demo_00.setAdapter(adapterSpinnerDemo);

        Calendar calendar = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("MMMM", new Locale("id"));
        int position =0;
        for (String month: tempDemo){
            if (format.format(calendar.getTime()).equals(month)){
                spnr_demo_00.setSelection(position);
                break;
            }
            position ++;
        }


    }

    private void load(){
        DateFormat format = new SimpleDateFormat("MMMM", new Locale("id"));
        int month = 1;
        try {
            Date date = format.parse(spnr_demo_00.getSelectedItem().toString());
            Calendar calendar = Calendar.getInstance();
            assert date != null;
            calendar.setTime(date);
            month = calendar.get(Calendar.MONTH)+1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PostManager post = new PostManager(mActivity,"insentif?month="+month+"&page="+page);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject objData = obj.getJSONObject("data");
                    JSONArray data = objData.getJSONArray("data");
                    for (int i=0; i<data.length(); i++){
                        JSONObject jo = data.getJSONObject(i);
                        arrayList.add(new CommissionHolder(jo.getString("delivery_date"), jo.getInt("qty"), jo.getLong("commission")));
                    }

                    if (data.length() >= sizePerPage){
                        isLoading = false;
                        page ++;
                    }
                    else {
                        isLoading = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mAdapter.notifyDataSetChanged();
        });
    }
}
