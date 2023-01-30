package com.wadaro.promotor.ui.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wadaro.promotor.R;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.module.FileProcessing;
import com.wadaro.promotor.ui.account.ProfileFrg;
import com.wadaro.promotor.ui.booker.CreateDemoActivity;
import com.wadaro.promotor.ui.demo.MainDemoFrg;
import com.wadaro.promotor.ui.commission.CommissionActivity;
import com.wadaro.promotor.ui.order.OrderFragment;
import com.wadaro.promotor.ui.product.MainProductFrg;
import com.wadaro.promotor.ui.report.ReportActivity;

public class HomePageActivity extends MyActivity {

    private FrameLayout frame_body_00;
    private BottomNavigationView navigationView;
    private MenuItem menuItem;

    @Override
    protected int setLayout() {
        return R.layout.ui_home_activity_main;
    }

    @Override
    protected void initLayout() {
        frame_body_00   = findViewById(R.id.frame_body_00);
        navigationView  = findViewById(R.id.bottom_nav_bar);
        navigationView.inflateMenu(R.menu.menu_demobooker);
        setCheckedBottomMenu(R.id.bottom_home);
        selectedMenu(0);
    }

    void setCheckedBottomMenu(int rIdmenu){
        Menu menu = navigationView.getMenu();
        menuItem = menu.findItem(rIdmenu);
        if(menuItem!=null) menuItem.setChecked(true);

    }

    @Override
    protected void initData() {
        FileProcessing.createFolder(mActivity,"Wadaro");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void initListener() {
        navigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.bottom_home:
                    selectedMenu(0);
                    return true;
                case R.id.bottom_demo:
                    selectedMenu(1);
                    return true;
                case R.id.bottom_produk:
                    selectedMenu(2);
                    return true;
                case R.id.bottom_sales:
                    selectedMenu(3);
                    return true;
                case R.id.bottom_profile:
                    selectedMenu(4);
                    return true;
            }
            return false;
        });
    }

    private void selectedMenu(int menu){
        Fragment fragment ;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (menu){
            default:
                fragment = HomeFrg.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"main");
                break;
            case 1:
                fragment = MainDemoFrg.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"demo");
                break;
            case 2:
                fragment = MainProductFrg.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"profile");
                break;
            case 3:
                fragment = OrderFragment.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"order");
                break;
            case 4:
                fragment = ProfileFrg.newInstance();
                fragmentTransaction.replace(frame_body_00.getId(), fragment,"profile");
                break;
        }
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("DEMO_ASSIGNMENT");
        intentFilter.addAction("CREATE_DEMO");
        intentFilter.addAction("PRODUCT");
        intentFilter.addAction("SALES_ORDER");
        intentFilter.addAction("REPORT");
        intentFilter.addAction("PROFILE");
        intentFilter.addAction("INSENTIF");
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("DEMO_ASSIGNMENT")){
                setCheckedBottomMenu(R.id.bottom_demo);
                selectedMenu(1);
            }
            else if (intent.getAction().equals("CREATE_DEMO")){
                startActivity(new Intent(mActivity, CreateDemoActivity.class));
            }
            else if (intent.getAction().equals("PRODUCT")){
                setCheckedBottomMenu(R.id.bottom_produk);
                selectedMenu(2);
            }
            else if (intent.getAction().equals("REPORT")){
                startActivity(new Intent(mActivity, ReportActivity.class));
            }
            else if (intent.getAction().equals("SALES_ORDER")){
                setCheckedBottomMenu(R.id.bottom_sales);
                selectedMenu(3);
            }
            else if (intent.getAction().equals("PROFILE")){
                setCheckedBottomMenu(R.id.bottom_profile);
                selectedMenu(4);
            }
            else if (intent.getAction().equals("INSENTIF")){
                startActivity(new Intent(mActivity, CommissionActivity.class));
            }

        }
    };
}
