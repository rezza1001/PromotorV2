package com.rentas.ppob.master;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.rentas.ppob.MainPpobActivity;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.FirebaseMessageService;
import com.rentas.ppob.libs.MyNotification;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.report.receipt.ReceiptActivity;

import org.json.JSONException;
import org.json.JSONObject;


public abstract class MyActivity extends AppCompatActivity {

    protected String TAG = "MyActivity";
    protected Activity mActivity;
    private RelativeLayout loadingview;

    protected int mAgentId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        mActivity = this;
        TAG = mActivity.getClass().getName();
        mAgentId = MainData.getAgentID(mActivity);

        // DEFAULT
        createLoading();
        initLayout();
        initData();
        initListener();


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FirebaseMessageService.MY_ACTION);
        registerReceiver(broadMessageNotif,intentFilter );

    }

    protected abstract int setLayout();
    protected abstract void initLayout();
    protected abstract void initData();
    protected abstract void initListener();

    protected void setCustomColorNavbar(int color){
        getWindow().setStatusBarColor(color);
    }

    private void createLoading(){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        loadingview = new RelativeLayout(mActivity, null);
        ViewGroup.LayoutParams lp = viewGroup.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        loadingview.setVisibility(View.GONE);
        viewGroup.addView(loadingview,lp);

        ProgressBar progressBar = new ProgressBar(mActivity, null,android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(true);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utility.getPixelValue(mActivity,25));
        lp2.topMargin = Utility.getPixelValue(mActivity, -12);
        loadingview.addView(progressBar, lp2);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#1c94fc"), PorterDuff.Mode.SRC_IN);
        setLoadingBarColor(2,0);
    }

    protected void showLoadingBar(int margintop, int background){
        margintop = margintop - 12;
        loadingview.setBackgroundColor(background);
        loadingview.setVisibility(View.VISIBLE);
        loadingview.setOnClickListener(null);

        ProgressBar progressBar = (ProgressBar) loadingview.getChildAt(0);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
        lp.topMargin = Utility.getPixelValue(mActivity, margintop);
    }

    protected void setLoadingBarColor(int margintop, int background){
        margintop = margintop - 12;
        loadingview.setBackgroundColor(background);
        loadingview.setOnClickListener(null);

        ProgressBar progressBar = (ProgressBar) loadingview.getChildAt(0);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
        lp.topMargin = Utility.getPixelValue(mActivity, margintop);
    }



    protected void onCheckDraft(int size){
    }

    protected void inComingNotif(String title, String body, String data){
    }

    protected RelativeLayout getLoadingBar(){
        return loadingview;
    }

    protected void hideLoadingBar(){
        loadingview.setVisibility(View.GONE);
    }

    private void mappingNotif(Intent intent){
        String title = intent.getStringExtra("Title");
        String body = intent.getStringExtra("Message");
        String data = intent.getStringExtra("Data");
        String image = intent.getStringExtra("Image");
        inComingNotif(title,body,data);
        showNotif(title, body, data, image);
        if (title.equalsIgnoreCase("Transaksi Sukses") && mActivity instanceof MainPpobActivity){
            try {
                JSONObject jData = new JSONObject(new JSONObject(data).getString("data"));
                Log.d(TAG,"Data fcm "+jData);
                Intent direct = new Intent(mActivity, ReceiptActivity.class);
                direct.putExtra("invoice", jData.getString("invoice_detail_id"));
                direct.putExtra("inv_number", "inv_"+jData.getString("invoice_detail_id"));
                startActivity(direct);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void showNotif( String title, String body, String data, String imageUrl){

        Intent myIntent = new Intent(this, MainPpobActivity.class);
        myIntent.putExtra("DATA_NOTIF", title);

        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) mActivity.findViewById(android.R.id.content)).getChildAt(0);
        MyNotification notification = new MyNotification();
        Bundle bundle = new Bundle();
        try {
            JSONObject joNotif = new JSONObject(data);
            String type = "Notification";
            bundle.putString("title", title);
            bundle.putString("description", data);
            bundle.putString("type", type);
            if (joNotif.has("activity") && joNotif.has("url")){
                bundle.putString("activity", joNotif.getString("activity"));
                bundle.putString("url", joNotif.getString("url"));
            }
            bundle.putString("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        notification.show(mActivity,viewGroup,title, body,bundle);
    }


    BroadcastReceiver broadMessageNotif = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mappingNotif(intent);
        }
    };

}
