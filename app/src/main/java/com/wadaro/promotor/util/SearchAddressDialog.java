package com.wadaro.promotor.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.wadaro.promotor.R;
import com.wadaro.promotor.module.Utility;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SearchAddressDialog extends Dialog {

    EditText edtx_address_00;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    private RelativeLayout rvly_body_00;

    public SearchAddressDialog(@NonNull Context context) {
        super(context, R.style.AppTheme_transparent);
        WindowManager.LayoutParams wlmp = Objects.requireNonNull(getWindow()).getAttributes();

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.util_window_searchaddress, null);
        setContentView(view);

        rvly_body_00 = view.findViewById(R.id.rvly_body_00);

        rvly_body_00.setVisibility(View.INVISIBLE);
        edtx_address_00 = view.findViewById(R.id.edtx_address_00);
        view.findViewById(R.id.imbtn_search_00).setOnClickListener(v -> {
            if (!edtx_address_00.getText().toString().isEmpty()){
                searchLocation(edtx_address_00.getText().toString());
            }
        });
        view.findViewById(R.id.lnly_root_00).setOnClickListener(v -> dismiss());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        }
        else {
            buildGoogleApiClient();
        }
    }

    @Override
    public void show() {
        super.show();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_in);
        rvly_body_00.setAnimation(animation);
        rvly_body_00.setVisibility(View.VISIBLE);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        mLocationRequest = new LocationRequest();
                        mLocationRequest.setInterval(1000);
                        mLocationRequest.setFastestInterval(1000);
                        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, location -> {

                            });
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(connectionResult -> {

                })
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }


    public void searchLocation(String location) {
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(getContext());
        try {
            addressList = geocoder.getFromLocationName(location, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList != null){
            if (addressList.size() > 0){
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                if (mLIstener != null){
                    mLIstener.onFinish(latLng);
                }
                edtx_address_00.setText(null);
                dismiss();
            }
            else {
                Utility.showToastError(getContext(),"Alamat tidak ditemukan");
            }
        }
        else {
            Utility.showToastError(getContext(),"Alamat tidak ditemukan");
        }

    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    private OnFinishListener mLIstener;
    public void setOnFinishListener(OnFinishListener onForceDismissListener){
        mLIstener = onForceDismissListener;
    }
    public interface OnFinishListener{
        void onFinish(LatLng latLng);
    }
}