package com.wadaro.promotor.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.UserDB;
import com.wadaro.promotor.module.MyDevice;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.util.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PostManager  extends AsyncTask<String, String, String> {

    private static final String TAG = "PostManager";
    private static final String SPARATOR = "M0C1-14";
    private String apiUrl           = "";
    private String mainPath           = "";
    private JSONObject mData        = new JSONObject();
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private LoadingWindow loading ;
    private boolean showLoading     = true;
    private UserDB mUser;


    @SuppressLint("StaticFieldLeak")
    private RelativeLayout rvly_loading;

    private Date datestart;

    public PostManager(Context mContext, String apiUrl){
        this.apiUrl = apiUrl;
        datestart = new Date();
        mainPath = Config.MAIN_PATH;
        context = mContext;
        mUser = new UserDB(); mUser.getData(context);
        if (context != null){
            loading = new LoadingWindow(mContext);
        }
        else {
            Log.d(TAG,"Request Canceled !!!");
        }
    }

    public PostManager(Context mContext,String mainUrl, String apiUrl){
        datestart = new Date();
        this.apiUrl = apiUrl;
        mainPath = mainUrl;
        context = mContext;
        mUser = new UserDB(); mUser.getData(context);
        if (context != null){
            loading = new LoadingWindow(mContext);
        }
        else {
            Log.d(TAG,"Request Canceled !!!");
        }
    }


    public void showloading(boolean show){
        showLoading = show;
    }

    public void setLoadingParent(RelativeLayout rvly){
        rvly_loading = rvly;
    }

    public void setData(JSONObject jo){
        mData = jo;
    }

    public JSONObject getData(){
        return mData;
    }


    public void setData(ArrayList<ObjectApi> pHolders){
        for(ObjectApi holder : pHolders){
            try {
                mData.put(holder.key, holder.value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addParam(ObjectApi objectAPI){
        if (context == null){
            Log.d(TAG,"Request Canceled !!!");
            return;
        }
        try {
            mData.put(objectAPI.key, objectAPI.value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addParam(String key, JSONObject data){
        if (context == null){
            Log.d(TAG,"Request Canceled !!!");
            return;
        }
        try {
            mData.put(key, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addTransParam(){
    }

    @Override
    protected void onPreExecute() {

        if (context == null){
            Log.d(TAG,"Request Canceled !!!");
            return;
        }
        if (!mUser.token.isEmpty()){
            Log.d(TAG,"TOKEN : "+ mUser.token);
        }
        Log.d(TAG,"onPreExecute with loading : "+ showLoading);
        if (showLoading && rvly_loading == null){
            loading.show();
        }
        else if (rvly_loading != null){
            rvly_loading.setVisibility(View.VISIBLE);
        }

        super.onPreExecute();
    }

    protected String doInBackground(String... arg0) {
        StringBuilder sbResponse = new StringBuilder();
        try {
            String type = arg0[0];
            URL url = new URL(mainPath +""+ apiUrl); //Enter URL here
            String host = url.getHost();
            InetAddress address = InetAddress.getByName(host);
            String ip = address.getHostAddress();
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(120000);
            httpURLConnection.setReadTimeout(120000);
            httpURLConnection.setRequestMethod(type); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            if (type.equals("POST") || type.equals("PUT")){
                httpURLConnection.setDoOutput(true);
            }
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.setRequestProperty("Accept", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            if (!mUser.token.isEmpty()){
                httpURLConnection.setRequestProperty("Authorization", "Bearer "+ mUser.token); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            }

            try {
                httpURLConnection.connect();
                httpURLConnection.setConnectTimeout(120000);
                Log.d(TAG,type+ " "+url+" "+apiUrl);
                if (type.equals("POST")|| type.equals("PUT")){
                    Log.d(TAG,"HOST : "+host+", InetAddress : "+address+", IP : "+ip);
                    Log.d(TAG,"data "+ " : "+ mData.toString());
                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(mData.toString());
                    wr.flush();
                    wr.close();
                }


                int responseCode = httpURLConnection.getResponseCode();
                Log.d(TAG,"RESPONSE CODE : "+ responseCode);
                sbResponse.append(responseCode).append(SPARATOR);
                BufferedReader in = null;
                if (responseCode == ErrorCode.OK_200){
                    in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                }
                else if (responseCode == ErrorCode.BLOCK){
                    in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                }
                else {
                    in = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                    Log.d(TAG,httpURLConnection.getResponseMessage());
                }


                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                sbResponse.append(response.toString());
                return sbResponse.toString();
            }catch (Exception ex){
                ex.printStackTrace();
                Log.d(TAG,"Connection failed ");
                sbResponse.append(ErrorCode.LOST_CONNECTION).append(SPARATOR);
                sbResponse.append("Koneksi bermasalah");
                return sbResponse.toString();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Error "+e.getMessage());
            Log.e(TAG, " Error Request time out");
            sbResponse.append("Request time out");
            sbResponse.append("Error connection");
            loading.dismiss();
            return sbResponse.toString();
        }

    }

    protected void onPostExecute(String presults) {
        Log.d(TAG,"onPostExecute "+ presults.length());
        if (context == null){
            Log.d(TAG,"Request Canceled !!!");
            return;
        }
        Date date1 = new Date();
        long diff = date1.getTime() - datestart.getTime();
        Log.d(TAG,"TOTAL TIME : "+ diff+" Seconds");

        loading.dismiss();

        if (rvly_loading != null){
            rvly_loading.setVisibility(View.GONE);
        }

        try {
            int code    =  Integer.parseInt(presults.split(SPARATOR)[0]);
            String results = code == ErrorCode.OK ? "{\"message\":\"success\"}":"{\"message\":\"failed\"}";
            if (presults.split(SPARATOR).length > 1){
                results = presults.split(SPARATOR)[1];
            }
            if (code == ErrorCode.UNAUTHORIZED){
                clearData();
            }
            if (results!=null) {
                Log.d(TAG,"TEXT RESPONSE "+this.apiUrl +" | "+ results +" | HEADER CODE : "+ code);
            }
            if (mReceiveListener != null){
                if (results != null){
                    if (results.equals("Request time out")){
                        Toast.makeText(context,"Request time out", Toast.LENGTH_SHORT).show();
                        mReceiveListener.onReceive(null, ErrorCode.TIME_OUT, "Time Out");
                        return;
                    }
                    try {
                        JSONObject jo;
                        if (results.startsWith("[")){
                            jo = new JSONObject();
                            jo.put("data", new JSONArray(results));
                        }
                        else {
                            jo   = new JSONObject(results);
                        }
                        int status      = jo.has("status") ? jo.getInt("status") : ErrorCode.OK;
                        String message  = jo.has("message") ? jo.getString("message") : "Success";
                        mReceiveListener.onReceive(jo, status,message);
                    } catch (JSONException e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                        sendError(e,presults);
                        mReceiveListener.onReceive(null, ErrorCode.CODE_UNPROCESSABLE_ENTITY, "Undefined");
                    }

                }
            }

        }catch (Exception e){
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            sendError(e, presults);
            e.printStackTrace();
            if (mReceiveListener != null){
                mReceiveListener.onReceive(null, ErrorCode.UNDIFINED_ERROR,"Error Connection "+ e.getMessage());
            }
            if (context != null){
                Toast.makeText(context, "Error Connection "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private onReceiveListener mReceiveListener;
    public void setOnReceiveListener(onReceiveListener mReceiveListener){
        this.mReceiveListener = mReceiveListener;
    }
    public interface onReceiveListener{
        void onReceive(JSONObject obj, int code, String message);
    }

    private void clearData(){
        mUser.clearData(context);
        Utility.showToastError(context,"Token Expired. Silahkan Login Ulang");
        new Handler().postDelayed(() -> {
            ((Activity)context).finish();
        },3000);
    }

    public void sendError(Exception e, String responseData){
        Calendar calendar = Calendar.getInstance();

        long diff = new Date().getTime() - datestart.getTime();
        MyDevice device = new MyDevice(context);
        JSONObject joDevice = new JSONObject();
        try {
            joDevice.put("name",device.getDeviceName());
            joDevice.put("version",device.getVersion());
            joDevice.put("os",device.getOs());
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        String password = MyPreference.getString(context,"password");

        DateFormat format = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss", Locale.getDefault());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,String> data = new HashMap<>();
        data.put("apiUrl", mainPath +""+ apiUrl);
        data.put("param", mData.toString());
        data.put("token", mUser.token);
        data.put("user_id", mUser.user_id);
        data.put("username", mUser.user_name);
        data.put("password", password);
        data.put("versionApps",device.getVersion());
        data.put("device",joDevice.toString());
        data.put("time", format.format(new Date()));
        data.put("timemillis", System.currentTimeMillis()+"");
        data.put("network", NetworkUtil.getConnection(context).toStringData());
        data.put("long_time", diff+"");
        data.put("year", calendar.get(Calendar.YEAR)+"");
        data.put("month", calendar.get(Calendar.MONTH)+"");
        data.put("date", calendar.get(Calendar.DATE)+"");
        String response = "-";
        if (e.getMessage() != null){
            if (Objects.requireNonNull(e.getMessage()).length() > 100){
                response = e.getMessage().substring(0,100);
            }
            else {
                response = e.getMessage();
            }
        }
        if (responseData.length() > 500){
            responseData = responseData.substring(0,500);
        }

        data.put("error", response);
        data.put("response", responseData);
        db.collection("DBUG_"+calendar.get(Calendar.YEAR)+"_"+calendar.get(Calendar.MONTH))
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e1 -> Log.w(TAG, "Error adding document", e1));
    }

}