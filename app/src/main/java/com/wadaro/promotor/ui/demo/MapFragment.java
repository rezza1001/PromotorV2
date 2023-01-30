package com.wadaro.promotor.ui.demo;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wadaro.promotor.R;
import com.wadaro.promotor.module.GPSTracker;
import com.wadaro.promotor.ui.demo.process.CoordinatorInfoDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener { // implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private GPSTracker gpsTracker;
    int index = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        gpsTracker = new GPSTracker(getActivity());
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(getActivity(), "Membutuhkan Izin Lokasi", Toast.LENGTH_SHORT).show();
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
        } else {
            setUpMapIfNeeded(v);
        }
        return v;
    }

    private void setUpMapIfNeeded(View v) {

        if (mGoogleMap == null) {
            MapView mMapView = v.findViewById(R.id.mvMap);
            mMapView.onCreate(getArguments());
            mMapView.onResume();

            try {
                MapsInitializer.initialize(Objects.requireNonNull(getActivity()));
                mMapView.getMapAsync(gmap -> {
                    mGoogleMap = gmap;
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    //fill markers
                    //add marker listener

                    // latitude and longitude
                    double latitude = gpsTracker.getLatitude();
                    double longitude = gpsTracker.getLongitude();

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitude, longitude)).zoom(17).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));

                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() != null){
            try {
                JSONObject jo = new JSONObject(marker.getTag().toString());
                CoordinatorInfoDialog dialog = new CoordinatorInfoDialog(Objects.requireNonNull(getActivity()));
                dialog.show(jo.getString("booking_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void makersMaker(double longitude, double latitude, int number, int demo, String status, JSONObject data){
        Mymarker mymarker = new Mymarker(getActivity(), null);
        if (demo == 1){
            mymarker.create(R.drawable.ic_marker, Color.BLUE, number, Color.WHITE);
        }
        else if (demo == 2){
            mymarker.create(R.drawable.ic_marker, Color.parseColor("#d44444"), number, Color.WHITE);
        }
        else if (demo == 3){
            mymarker.create(R.drawable.ic_marker, Color.GREEN, number, Color.WHITE);
        }
        else if (demo == 4){
            mymarker.create(R.drawable.ic_marker, Color.YELLOW, number, Color.WHITE);
        }

        Bitmap bitmap = createDrawableFromView(getActivity(), mymarker);
        Marker marker1 = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(longitude, latitude)));
        marker1.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        marker1.setTag(data.toString());
        mGoogleMap.setOnMarkerClickListener(this);

        index++;
        if (index == 1){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(longitude, latitude)).zoom(14).build();
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getActivity()).registerReceiver(receiver,new IntentFilter("LOAD_DATA"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Objects.requireNonNull(getActivity()).unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAGRZ","BroadcastReceiver here. ");
            if (mGoogleMap != null){
                mGoogleMap.clear();
                try {
                    if (intent.getStringExtra("data") == null){
                        return;
                    }
                    JSONArray ja = new JSONArray(intent.getStringExtra("data"));
                    for (int i=0; i<ja.length(); i++){
                        JSONObject jo = ja.getJSONObject(i);
                        double longitude = jo.getDouble("longitude");
                        double latitude = jo.getDouble("latitude");
                        int number = jo.getInt("number")+1;
                        int demo = jo.getInt("booking_demo");
                        makersMaker(latitude, longitude,number,demo, jo.getString("booking_status"), jo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    public Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}
