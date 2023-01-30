package com.wadaro.promotor.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wadaro.promotor.R;
import com.wadaro.promotor.adapter.ProfilGridViewAdapter;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.LoadingWindow;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.base.Global;
import com.wadaro.promotor.base.MyFragment;
import com.wadaro.promotor.database.DatabaseManager;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.UserDB;
import com.wadaro.promotor.model.ItemObject;
import com.wadaro.promotor.module.FailedWindow;
import com.wadaro.promotor.module.FileProcessing;
import com.wadaro.promotor.ui.draft.DraftSurveyActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFrg extends MyFragment {

    private static final int REQ_PHOTO = 1;
    private ProfilGridViewAdapter gridViewAdapter;
    private TextView txvw_name_00,txvw_type_00,txvw_nip_00,txvw_phone_00,txvw_email_00;
    private TextView txvw_company_00,txvw_branch_00,txvw_kacab_00,txvw_knwil_00;
    private CircleImageView imvw_profile_00;

    public static ProfileFrg newInstance() {

        Bundle args = new Bundle();

        ProfileFrg fragment = new ProfileFrg();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setlayout() {
        return R.layout.accountl_frg_main;
    }

    @Override
    protected void initLayout(View view) {

        RecyclerView gridView = view.findViewById(R.id.rvGrid);
        gridView.setHasFixedSize(true);
        //set layout manager and adapter for "GridView"
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        gridView.setLayoutManager(layoutManager);
        gridViewAdapter = new ProfilGridViewAdapter(getActivity(), getAllItemObject());
        gridView.setAdapter(gridViewAdapter);

        txvw_name_00 = view.findViewById(R.id.txvw_name_00);
        txvw_type_00 = view.findViewById(R.id.txvw_type_00);
        txvw_nip_00 = view.findViewById(R.id.txvw_nip_00);
        txvw_phone_00 = view.findViewById(R.id.txvw_phone_00);
        txvw_email_00 = view.findViewById(R.id.txvw_email_00);
        txvw_company_00 = view.findViewById(R.id.txvw_company_00);
        txvw_company_00 = view.findViewById(R.id.txvw_company_00);
        txvw_branch_00 = view.findViewById(R.id.txvw_branch_00);
        txvw_kacab_00 = view.findViewById(R.id.txvw_kacab_00);
        txvw_knwil_00 = view.findViewById(R.id.txvw_knwil_00);
        imvw_profile_00 = view.findViewById(R.id.imvw_profile_00);

    }

    @Override
    protected void initData() {
        txvw_name_00.setText(mUser.name);
        txvw_phone_00.setText(mUser.phone);
        txvw_email_00.setText(mUser.email);
        txvw_type_00.setText(mUser.group);
        txvw_nip_00.setText(mUser.user_name);

        txvw_company_00.setText(mUser.company_name);
        txvw_branch_00.setText(mUser.branch_name);
        txvw_kacab_00.setText("-");
        txvw_knwil_00.setText("-");

        FileProcessing.createFolder(mActivity,"Wadaro");
        Glide.with(mActivity).load(Config.IMAGE_PROFILE+mUser.photo).into(imvw_profile_00);
        loadProfile();
    }

    @Override
    protected void initListener() {
        gridViewAdapter.setOnItemClickListener(position -> {
            if (position == 0){
                if (MyPreference.getInt(mActivity, Global.PREF_OFFLINE_MODE) == 1){
                    FailedWindow failedWindow = new FailedWindow(mActivity);
                    failedWindow.setDescription("Anda dalam mode offline");
                    failedWindow.show();
                    return;
                }
                new SynchData(mActivity);
            }
            if (position == 1){
                startActivity(new Intent(mActivity, DraftSurveyActivity.class));
            }
            if (position == 2){
                startActivity(new Intent(mActivity, ChangePasswodActivity.class));
            }
            if (position == 3){
                Intent intent = new Intent(mActivity, ChangePhotoActivity.class);
                startActivityForResult(intent, REQ_PHOTO);
            }
            if (position == 4){
                logout();
            }
        });
    }

    private List<ItemObject> getAllItemObject(){
        List<ItemObject> items = new ArrayList<>();
        items.add(new ItemObject("Download Data Harian", "ic_synchdata"));
        items.add(new ItemObject("Draft Offline", "draft"));
        items.add(new ItemObject("Ganti Password", "ic_password"));
        items.add(new ItemObject("Ganti Foto", "ic_photo"));
        items.add(new ItemObject("Keluar", "ic_keluar"));

        return items;
    }

    private void logout(){
        PostManager post = new PostManager(mActivity,"logout");
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                DatabaseManager databaseManager = new DatabaseManager(mActivity);
                databaseManager.clearAllData(mActivity);
                LoadingWindow loadingWindow = new LoadingWindow(mActivity);
                loadingWindow.show();
                new Handler().postDelayed(() -> {
                    loadingWindow.dismiss();
                    mActivity.finish();
                },1000);
            }
        });
    }

    private void loadProfile(){
        PostManager post = new PostManager(mActivity,"profile/"+mUser.user_id);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    mUser.user_id = data.getString("user_id");
                    mUser.group = data.getString("user_group");
                    mUser.company_id = data.getString("company_id");
                    mUser.branch_id = data.getString("branch_id");
                    mUser.employee_id = data.getString("employee_id");
                    mUser.company_name = data.getString("company_name");
                    mUser.branch_name = data.getString("branch_name");
                    mUser.branch_type = data.getString("branch_type");
                    mUser.organization_id = data.getString("organization_id");
                    mUser.name = data.getString("employee_name");
                    mUser.phone = data.getString("employee_phone");
                    mUser.email = data.getString("employee_email");
                    mUser.photo = data.getString("employee_photo");
                    mUser.user_name = data.getString("user_name");
                    mUser.insert(mActivity);

                    txvw_name_00.setText(mUser.name);
                    txvw_phone_00.setText(mUser.phone);
                    txvw_email_00.setText(mUser.email);
                    txvw_type_00.setText(mUser.group);
                    txvw_nip_00.setText(mUser.user_name);

                    txvw_company_00.setText(mUser.company_name);
                    txvw_branch_00.setText(mUser.branch_name);
                    txvw_kacab_00.setText(data.getString("branch_manager_name"));
                    txvw_knwil_00.setText(data.getString("regional_manager_name"));
                    Glide.with(mActivity).load(Config.IMAGE_PROFILE+mUser.photo).into(imvw_profile_00);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PHOTO && resultCode == -1){
            loadProfile();
        }
    }
}
