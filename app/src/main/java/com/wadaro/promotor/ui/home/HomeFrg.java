package com.wadaro.promotor.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.promotor.R;
import com.wadaro.promotor.adapter.HomeMenuGridViewAdapter;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.UserDB;
import com.wadaro.promotor.model.ItemObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFrg extends Fragment implements HomeMenuGridViewAdapter.OnRecyclerViewClickListener{

    private UserDB userDB;

    public HomeFrg() {
        // Required empty public constructor
    }


    public static HomeFrg newInstance() {
        HomeFrg fragment = new HomeFrg();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDB = new UserDB();
        userDB.getData(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_home_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView gridView = view.findViewById(R.id.rvGrid);

        gridView.setHasFixedSize(true);

        //set layout manager and adapter for "GridView"
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        gridView.setLayoutManager(layoutManager);
        HomeMenuGridViewAdapter gridViewAdapter = new HomeMenuGridViewAdapter(getActivity(), getAllItemObject());
        gridViewAdapter.setOnItemClickListener(this);
        gridView.setAdapter(gridViewAdapter);



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    @Override
    public void onItemClick(Integer position) {
        if(position == 0) {
            Objects.requireNonNull(getActivity()).sendBroadcast(new Intent("DEMO_ASSIGNMENT"));
        } else if(position == 1){
            Objects.requireNonNull(getActivity()).sendBroadcast(new Intent("CREATE_DEMO"));
        } else if(position == 2){
            Objects.requireNonNull(getActivity()).sendBroadcast(new Intent("PRODUCT"));
        } else if(position == 3){
            Objects.requireNonNull(getActivity()).sendBroadcast(new Intent("SALES_ORDER"));
        } else if(position == 4){
            Objects.requireNonNull(getActivity()).sendBroadcast(new Intent("REPORT"));
        }else if(position == 5){
            Objects.requireNonNull(getActivity()).sendBroadcast(new Intent("INSENTIF"));
        }else if(position == 6){
            Objects.requireNonNull(getActivity()).sendBroadcast(new Intent("PROFILE"));
        }else if(position == 7){
            Intent intent = new Intent(getContext(), com.rentas.ppob.LauncherPPOBActivity.class);
            intent.putExtra("email",userDB.email);
            intent.putExtra("name",userDB.name);
            intent.putExtra("phone",userDB.phone);
            intent.putExtra("employee_id",userDB.employee_id);
            intent.putExtra("company_id",userDB.company_id);
            intent.putExtra("avatar", Config.IMAGE_PROFILE + userDB.photo);
            MyPreference.save(getContext(),"WADARO_NAME", userDB.name);
            startActivity(intent);
        }
    }

    private List<ItemObject> getAllItemObject(){
        List<ItemObject> items = new ArrayList<>();
        items.add(new ItemObject("Penugasan Demo", "ic_penugasandemo"));
        items.add(new ItemObject("Demo Dadakan", "ic_demodadakan"));
        items.add(new ItemObject("Info Produk", "ic_product"));
        items.add(new ItemObject("Data Sales Order", "ic_shopping_cart"));
        items.add(new ItemObject("Hasil Demo", "ic_rekap"));
        items.add(new ItemObject("Insentif", "ic_calculate"));
        items.add(new ItemObject("Profil", "ic_profile"));
        items.add(new ItemObject("PPOB", "ic_ppob"));

        return items;
    }

}
