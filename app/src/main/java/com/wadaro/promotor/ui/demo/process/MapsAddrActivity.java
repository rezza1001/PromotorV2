package com.wadaro.promotor.ui.demo.process;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wadaro.promotor.R;
import com.wadaro.promotor.module.GPSTracker;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.util.SearchAddressDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsAddrActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView txvw_addres_00;
    private MaterialRippleLayout mrly_next_00;
    private LatLng currentLatLang;
    private SearchAddressDialog searchAddressDialog;

    private EditText edtx_address_00;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_mapsaddr);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frgm_map_00);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        txvw_addres_00 = findViewById(R.id.txvw_addres_00);
        mrly_next_00 = findViewById(R.id.mrly_next_00);

        mrly_next_00.setBackground(Utility.getRectBackground("00993D", Utility.dpToPx(this, 10)));

        mrly_next_00.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("ADDRESS", txvw_addres_00.getText().toString());
            intent.putExtra("LONGITUDE", currentLatLang.longitude + "");
            intent.putExtra("LATITUDE", currentLatLang.latitude + "");
            setResult(RESULT_OK, intent);
            MapsAddrActivity.this.finish();
        });

        searchAddressDialog = new SearchAddressDialog(this);
        findViewById(R.id.imvw_search_00).setOnClickListener(v -> {
            searchAddressDialog.show();
        });
        searchAddressDialog.setOnFinishListener(latLng -> {
            txvw_addres_00.setText(getAddress(latLng));
            moveToCurrentLocation(latLng);
        });

        edtx_address_00 = findViewById(R.id.edtx_address_00);
        findViewById(R.id.imbtn_search_00).setOnClickListener(v -> {
            if (!edtx_address_00.getText().toString().isEmpty()){
                searchLocation(edtx_address_00.getText().toString());
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GPSTracker gpsTracker = new GPSTracker(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.getPosition();
                LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                txvw_addres_00.setText(getAddress(latLng));
                currentLatLang = latLng;
            }
        });

        new Handler().postDelayed(() -> {
            Log.d("MapsAddrAct", gpsTracker.getAddress());
            txvw_addres_00.setText(gpsTracker.getAddress());
            moveToCurrentLocation(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
        },1000);
    }

    private void moveToCurrentLocation(LatLng currentLocation) {
        currentLatLang = currentLocation;
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,10));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);

        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Tekan dan tahan untuk pindah").draggable(true));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }

    public void searchLocation(String location) {
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList != null){
            if (addressList.size() > 0){
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                txvw_addres_00.setText(getAddress(latLng));
                moveToCurrentLocation(latLng);
            }
            else {
                Utility.showToastError(this,"Alamat tidak ditemukan");
            }
        }
        else {
            Utility.showToastError(this,"Alamat tidak ditemukan");
        }

    }

    public String getAddress(LatLng latLng) {
        String mAddress = "-";
        if (latLng != null ){
            Geocoder gcd = new Geocoder(this, new Locale("id","ID"));
            List<Address> address;
            try {
                address = gcd.getFromLocation(latLng.latitude,latLng.longitude,1);
                if(address.size()> 0){
                    mAddress = address.get(0).getAddressLine(0);
                }

            }catch (Exception e){
                Log.d("MapsAddrAct", Objects.requireNonNull(e.getMessage()));
            }
        }
        else {
            mAddress = "Lokasi tidak diketahui";
        }
        return mAddress;
    }

}