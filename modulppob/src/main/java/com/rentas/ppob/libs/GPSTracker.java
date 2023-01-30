package com.rentas.ppob.libs;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;
    public static final int REQUEST_SETTING = 10001;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;
    Location location;

    double latitude; // latitude
    double longitude; // longitude
    String mAddress = "Lokasi tidak diketahui";

    private static final String TAG = "GPSTracker";

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    protected LocationManager locationManager;

    public GPSTracker(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    public Location getLocation() {
        Log.d(TAG, "Start getLocation");
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Get Status GPS enable
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Get Network Status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {
//                showAlertSetting();
                forceOnGPS();
                Log.d(TAG, "Disabled isNetworkEnabled & isGPSEnabled");
            } else {
                Log.d(TAG, "isNetworkEnabled & isGPSEnabled is Enabled");
                this.canGetLocation = true;
                Log.d(TAG, "isNetworkEnabled : "+ isNetworkEnabled);

                ActivityCompat.requestPermissions(((Activity)mContext),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                ActivityCompat.requestPermissions(((Activity)mContext),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                    }
                }
                Log.d(TAG, "isGPSEnabled : "+ isGPSEnabled);
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        else {
            latitude = 0.0;
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            location.getLongitude();
        }
        else {
            longitude = 0.0;
        }
        return longitude;
    }

    public boolean isCanGetLocation() {
        return canGetLocation;
    }

    public void showAlertSetting() {

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS Setting");
        alertDialog.setMessage("GPS is not enabled. Do you want to setting?");
        alertDialog.setPositiveButton("Setting", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
            ((Activity)mContext).startActivityForResult(intent,REQUEST_SETTING);
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

    private void forceOnGPS(){
        Log.d(TAG,"forceOnGPS()");
        new GpsUtils(mContext).turnGPSOn(isGPSEnable -> getLocation());
    }



    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public String getAddress() {
        if (location != null ){
            Geocoder gcd = new Geocoder(mContext, new Locale("id","ID"));
            List<Address> address;
            try {
                address = gcd.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                if(address.size()> 0){
                    mAddress = address.get(0).getAddressLine(0);
                }

            }catch (Exception e){
                Log.d(TAG,e.getMessage());
            }
        }
        else {
            mAddress = "Lokasi tidak diketahui";
        }
        return mAddress;
    }

    public String getSpesifictAddress() {
        JSONObject addressdata = new JSONObject();
        if (location != null ){
            Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
            List<Address> address;
            try {
                address = gcd.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                if(address.size()> 0){
                    addressdata.put("Negara",address.get(0).getCountryName());
                    addressdata.put("provinsi",address.get(0).getAdminArea());
                    addressdata.put("kota",address.get(0).getSubAdminArea());
                    addressdata.put("kecamatan",address.get(0).getLocality().replace("Kecamatan ",""));
                    addressdata.put("alamat",address.get(0).getSubLocality());
                    addressdata.put("kodepos",address.get(0).getPostalCode());
                }

            }catch (Exception e){
                Log.d(TAG,e.getMessage());
            }
        }
        return addressdata.toString();
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public String getZip() {
        String code = "";
        if (location != null ){
            Geocoder gcd = new Geocoder(mContext, new Locale("id","ID"));
            List<Address> address;
            try {
                address = gcd.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                Log.d(TAG,"SIZE "+address.size());
                if(address.size()> 0){
                    code = address.get(0).getPostalCode();
                }

            }catch (Exception e){
                Log.d(TAG,e.getMessage());
            }
        }
        return code;
    }
    public String getZipCode() {
        String zip = "0";
        if (location != null ){
            Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
            List<Address> address;
            try {
                address = gcd.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                if(address.size()> 0){
                    zip = address.get(0).getPostalCode();
                }

            }catch (Exception e){
                Log.d(TAG,e.getMessage());
            }
        }
        return zip;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (pListenr != null){
            pListenr.onChange(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (location != null){
            Log.d(TAG,"onStatusChanged : " + location.getLongitude());
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG,"onProviderEnabled : " + provider);
        if (onConnectionUPListener != null){
            onConnectionUPListener.onUp();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (onConnectionUPListener != null){
            onConnectionUPListener.onDown();
        }
    }


    public boolean statusGPS(){
        return isGPSEnabled;
    }

    LocationChangeListenr pListenr;
    public void setLocationChangeListenr(LocationChangeListenr mListenr){
        pListenr = mListenr;
    }
    public interface LocationChangeListenr{
        public void onChange(Location location);
    }

    private OnConnectionUPListener onConnectionUPListener;
    public void setOnConnectionUPListener(OnConnectionUPListener pOnConnectionUPListener){
        onConnectionUPListener = pOnConnectionUPListener;
    }
    public interface OnConnectionUPListener{
        public void onUp();
        public void onDown();
    }
}
