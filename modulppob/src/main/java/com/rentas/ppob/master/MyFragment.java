package com.rentas.ppob.master;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rentas.ppob.data.MainData;

public abstract class MyFragment extends Fragment {

    protected Activity mActivity;
    protected int mAgentId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mAgentId = MainData.getAgentID(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setlayout(), container, false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initListener();
    }

    protected abstract int setlayout();
    protected abstract void initLayout(View view);
    protected abstract void initListener();

    protected void initData(){
    }

}
