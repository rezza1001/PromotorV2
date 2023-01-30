package com.rentas.ppob.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rentas.ppob.ApplicationPpob;
import com.rentas.ppob.R;
import com.rentas.ppob.api.ConfigAPI;
import com.rentas.ppob.api.ErrorCode;
import com.rentas.ppob.api.PostManager;
import com.rentas.ppob.component.DynamicDialog;
import com.rentas.ppob.data.CategoryData;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.database.table.CategoryDB;
import com.rentas.ppob.deposit.MainDepositActivity;
import com.rentas.ppob.deposit.MutationActivity;
import com.rentas.ppob.libs.ItemOffsetDecoration;
import com.rentas.ppob.libs.MyCurrency;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class HomeFragment extends MyFragment {

    private final ArrayList<MenuHolder> prepaidMenu = new ArrayList<>();
    private final ArrayList<MenuHolder> postpaidMenu = new ArrayList<>();
    private MenuAdapter adapterPrepaid;
    private MenuAdapter adapterPostpaid;
    private LinearLayout lnly_deposit,lnly_mutation;
    private LinearLayout lnly_back;
    private TextView txvw_balance;
    private ImageView imvw_api;


    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setlayout() {
        return R.layout.home_fragment_main;
    }

    @Override
    protected void initLayout(View view) {
        lnly_deposit = view.findViewById(R.id.lnly_deposit);
        lnly_mutation = view.findViewById(R.id.lnly_mutation);
        lnly_back = view.findViewById(R.id.lnly_back);
        txvw_balance = view.findViewById(R.id.txvw_balance);
        imvw_api    = view.findViewById(R.id.imvw_api);

        mActivity.registerReceiver(receiver, new IntentFilter(ApplicationPpob.CONNECTION_CHANGE));
        changeNetwork(ApplicationPpob.netWorkInfoUtility.getType(),ApplicationPpob.netWorkInfoUtility.isOnline());

        initPrepaid(view);
        initPostpaid(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        getProfile();
    }

    @Override
    protected void initListener() {
        adapterPrepaid.setOnSelectedListener((data, position) -> CategoryData.toTransaction(mActivity, data));

        adapterPostpaid.setOnSelectedListener((data, position) -> CategoryData.toTransaction(mActivity, data));

        lnly_back.setOnClickListener(v -> mActivity.finish());

        lnly_deposit.setOnClickListener(v -> {
            DynamicDialog dialog = new DynamicDialog(mActivity);
            dialog.showInfo("Info Deposit","Deposit saat ini hanya bisa dilakukan oleh admin. Segera hubungi admin");
//            startActivity(new Intent(mActivity, MainDepositActivity.class))
        });
        lnly_mutation.setOnClickListener(v -> startActivity(new Intent(mActivity, MutationActivity.class)));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initPrepaid(View view){

        RecyclerView rcvw_prepaid = view.findViewById(R.id.rcvw_prepaid);
        rcvw_prepaid.setLayoutManager(new GridLayoutManager(mActivity,3));
        rcvw_prepaid.setNestedScrollingEnabled(false);
        int horizontal = Utility.dpToPx(mActivity, 5);
        int vertical = Utility.dpToPx(mActivity, 10);
        rcvw_prepaid.addItemDecoration(new ItemOffsetDecoration(horizontal,vertical,horizontal,vertical));

        adapterPrepaid = new MenuAdapter(mActivity, prepaidMenu);
        rcvw_prepaid.setAdapter(adapterPrepaid);

        PostManager post = new PostManager(mActivity, ConfigAPI.GET_CATEGORIES);
        post.addParamHeader("package_id","1");
        post.executeGET();
        post.setOnReceiveListener((obj, code, message) -> {
           if (code == ErrorCode.OK){
               ArrayList<CategoryDB> categoryDBS = new ArrayList<>();
               try {
                   JSONArray data = obj.getJSONArray("data");
                   for (int i=0; i<data.length(); i++){
                       String pCode = data.getJSONObject(i).getString("code");
                       int pId      = data.getJSONObject(i).getInt("id");
                       String name  = data.getJSONObject(i).getString("name");

                       CategoryDB db = new CategoryDB();
                       db.id = pId+"";
                       db.code  = pCode;
                       db.name = name;
                       categoryDBS.add(db);

                       if (pCode.equals(CategoryData.PAKET_DATA) || pCode.equals(CategoryData.PULSA)){
                            prepaidMenu.add(new MenuHolder(pId,pCode,name,""));
                       }else {
                           postpaidMenu.add(new MenuHolder(pId,pCode,name,""));
                       }
                   }

                   CategoryDB db = new CategoryDB();
                   db.insertBulk(mActivity, categoryDBS);

                   prepaidMenu.add(new MenuHolder(99,"99","Paket SMS",""));
//                   postpaidMenu.add(new MenuHolder(99,CategoryData.BPJS_KESEHATAN,"BPJS Kesehatan",""));
//                   postpaidMenu.add(new MenuHolder(99,CategoryData.BPJS_KETENAGAKERJAAN,"BPJS Ketengakerjaan",""));
//                   postpaidMenu.add(new MenuHolder(99,CategoryData.MULTIFINANCE,"Multifinance",""));
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
           else {
               Utility.showFailedDialog(mActivity,message);
           }
            adapterPrepaid.notifyDataSetChanged();
            adapterPostpaid.notifyDataSetChanged();

        });
    }

    private void initPostpaid(View view){
        RecyclerView rcvw_postpaid = view.findViewById(R.id.rcvw_postpaid);
        rcvw_postpaid.setLayoutManager(new GridLayoutManager(mActivity,3));
        rcvw_postpaid.setNestedScrollingEnabled(false);
        int horizontal = Utility.dpToPx(mActivity, 5);
        int vertical = Utility.dpToPx(mActivity, 10);
        rcvw_postpaid.addItemDecoration(new ItemOffsetDecoration(horizontal,vertical,horizontal,vertical));
        adapterPostpaid = new MenuAdapter(mActivity, postpaidMenu);
        rcvw_postpaid.setAdapter(adapterPostpaid);
    }


    private void getProfile(){
        PostManager post = new PostManager(mActivity, ConfigAPI.GET_AGENT_BY_ID+mAgentId);
        post.executeGET();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    obj = obj.getJSONObject("data");

                    String partnerId = obj.getString("partner_id");
                    String packageId = obj.getString("package_id");
                    String name = obj.getString("name");
                    long balance = obj.getLong("saldo");

                    MainData.initProfile(mActivity, partnerId, packageId, name, balance);
                    txvw_balance.setText(MyCurrency.toCurrnecy(balance));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("TYPE",0);
            boolean inetStatus = intent.getBooleanExtra("INET_STATUS",false);
            boolean ipStatus = intent.getBooleanExtra("IP_STATUS",false);
            changeNetwork(type, (inetStatus && ipStatus));

        }
    };

    private void changeNetwork(int type, boolean status){
        Log.d("TAGRZ","changeNetwork "+ status);
        if (!status){
            imvw_api.setImageLevel(3);
            return;
        }
        if (type == ConnectivityManager.TYPE_MOBILE){
            imvw_api.setImageLevel(1);
        }
        else {
            imvw_api.setImageLevel(2);
        }
    }
}
