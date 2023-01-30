package com.rentas.ppob.api;

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
import com.rentas.ppob.component.LoadingDialog;
import com.rentas.ppob.data.MainData;
import com.rentas.ppob.libs.GPSTracker;
import com.rentas.ppob.libs.MyDevice;
import com.rentas.ppob.libs.NetworkUtil;
import com.rentas.ppob.libs.Utility;
import com.rentas.ppob.master.MyPreference;

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
    private static final String SEPARATOR = "M0C1-14";
    private String apiUrl           = "";
    private String mainPath           = "";
    private JSONObject mData        = new JSONObject();
    private StringBuilder mParamHeader        = new StringBuilder();
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private LoadingDialog loading ;
    private MyDevice myDevice;
    private boolean showLoading     = true;
    GPSTracker gpsTracker;

    @SuppressLint("StaticFieldLeak")
    private RelativeLayout rvly_loading;

    private Date datestart;

    public PostManager(Context mContext, String apiUrl){
        this.apiUrl = apiUrl;
        datestart = new Date();
        mainPath = ConfigAPI.getMainUrl(mContext);

        context = mContext;
        if (context != null){
            loading = new LoadingDialog(mContext);
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

        if (context != null){
            loading = new LoadingDialog(mContext);
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
    public void addParam(String key, String value){
        if (context == null){
            Log.d(TAG,"Request Canceled !!!");
            return;
        }
        try {
            mData.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void addParam(String key, int value){
        if (context == null){
            Log.d(TAG,"Request Canceled !!!");
            return;
        }
        try {
            mData.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void addParam(String key, long value){
        if (context == null){
            Log.d(TAG,"Request Canceled !!!");
            return;
        }
        try {
            mData.put(key, value);
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

    public void addParamHeader(String key, String value){
       if (mParamHeader.toString().isEmpty()){
           mParamHeader.append("?");
       }
       mParamHeader.append(key).append("=").append(value);
    }
    public void addParamHeader(String key, int value){
       if (mParamHeader.toString().isEmpty()){
           mParamHeader.append("?");
       }
       mParamHeader.append(key).append("=").append(value);
    }

    @Override
    protected void onPreExecute() {

        if (context == null){
            Log.d(TAG,"Request Canceled !!!");
            return;
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
            String sUrl = mainPath +""+ apiUrl+mParamHeader.toString();
            String type = arg0[0];
            URL url = new URL(sUrl); //Enter URL here
            Log.d(TAG,"----> "+sUrl+" <-----------");
            String host = url.getHost();
            InetAddress address = InetAddress.getByName(host);
            String ip = address.getHostAddress();
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(ConfigAPI.TIME_OUT);
            httpURLConnection.setReadTimeout(ConfigAPI.TIME_OUT);
            httpURLConnection.setRequestMethod(type); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            if (type.equals("POST") || type.equals("PUT")){
                httpURLConnection.setDoOutput(true);
            }
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.setRequestProperty("Accept", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            String accessToken = MainData.getAccessToken(context);
            if (!accessToken.isEmpty()){
                httpURLConnection.setRequestProperty("Authorization", "Bearer "+ accessToken); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            }

            try {
                httpURLConnection.connect();
                httpURLConnection.setConnectTimeout(ConfigAPI.TIME_OUT);
                Log.d(TAG,type+ " "+url+" "+apiUrl);
                if (type.equals("POST")|| type.equals("PUT")){
                    Log.d(TAG,"HOST : "+host+", InetAddress : "+address+", IP : "+ip);
                    Log.d(TAG,"PARAMETER "+ " : "+ mData.toString());
                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(mData.toString());
                    wr.flush();
                    wr.close();
                }


                int responseCode = httpURLConnection.getResponseCode();
                Log.d(TAG,"RESPONSE CODE : "+ responseCode);
                sbResponse.append(responseCode).append(SEPARATOR);
                BufferedReader in = null;
                if (responseCode == ErrorCode.OK_200 || responseCode == ErrorCode.CREATED){
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
                sbResponse.append(ErrorCode.LOST_CONNECTION).append(SEPARATOR);
                String results = "{\"message\":\"Konesksi Bermaslah\",\"data\":\""+ex.getMessage()+"\"}";
                sbResponse.append(results);
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

    public void executeGET(){
        execute("GET");
    }
    public void executePOST(){
        if (apiUrl.equals(ConfigAPI.POST_INVOICE_POS) || apiUrl.equals(ConfigAPI.POST_INVOICE_PRE) || apiUrl.equals(ConfigAPI.POTS_LOGIN)){
            gpsTracker = new GPSTracker(context);
            myDevice= new MyDevice(context);
            JSONObject jo = new JSONObject();
            try {
                jo.put("device_id",myDevice.getDeviceID());
                jo.put("device_name",myDevice.getDeviceName());
                jo.put("device_os",myDevice.getOs());
                jo.put("device_lac",myDevice.getLAC());
                jo.put("device_ci",myDevice.getCID());
                jo.put("device_mcc",myDevice.getMCC());
                jo.put("device_mnc",myDevice.getMNC());
                jo.put("version_apps",myDevice.getVersionCode());
                if (gpsTracker != null){
                    jo.put("latitude",gpsTracker.getLatitude());
                    jo.put("longitude",gpsTracker.getLongitude());
                }
                mData.put("info",jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        execute("POST");
    }

    protected void onPostExecute(String presults) {
        if (apiUrl.equals(ConfigAPI.POST_INVOICE_POS) || apiUrl.equals(ConfigAPI.POST_INVOICE_PRE)){
            senDbugTrans(presults);
        }
        Log.d(TAG,"onPostExecute "+ presults.length());
        if (context == null){
            Log.d(TAG,"Request Canceled !!!");
            return;
        }
        Date date1 = new Date();
        long diff = date1.getTime() - datestart.getTime();
        Log.d(TAG,"TOTAL TIME : "+ diff+" ms");

        loading.dismiss();

        if (rvly_loading != null){
            rvly_loading.setVisibility(View.GONE);
        }

        try {
            int code    =  Integer.parseInt(presults.split(SEPARATOR)[0]);
            String results = code == ErrorCode.OK ? "{\"message\":\"success\"}":"{\"message\":\"failed\"}";
            if (presults.split(SEPARATOR).length > 1){
                results = presults.split(SEPARATOR)[1];
            }
            if (code == ErrorCode.UNAUTHORIZED && !apiUrl.contains("auth/login")){
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
                        String sStatus      = jo.has("status") ? jo.getString("status") : code == ErrorCode.OK || code == ErrorCode.CREATED ? "success":"error";
                        String message  = jo.has("message") ? jo.getString("message") : "Success";

                        int status = sStatus.equals("success") ? ErrorCode.OK_200 : code;


                        mReceiveListener.onReceive(jo, status,message);
                    } catch (JSONException e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                        sendError(e,presults);
                        mReceiveListener.onReceive(null, ErrorCode.CODE_UNPROCESSABLE_ENTITY, "Undefined");
                    }

                }
            }

            if (code == ErrorCode.LOST_CONNECTION){
                sendError(null,results);
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
//        mUser.clearData(context);
        Utility.showToastError(context,"Token Expired. Silahkan Masuk lagi");
        new Handler().postDelayed(() -> {
            MyPreference.clear(context);
            ((Activity)context).finish();
        },3000);
    }

    public void sendError(Exception e, String responseData){
        Log.d(TAG,"SEND Error ---- >");
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
        data.put("token", MainData.getAccessToken(context));
        data.put("agent_code", MainData.getAgentCode(context));
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
        db.collection("DBUG_PPOB_"+calendar.get(Calendar.YEAR)+"_"+calendar.get(Calendar.MONTH))
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e1 -> Log.w(TAG, "Error adding document", e1));
    }


    public void senDbugTrans(String response){
        Log.d(TAG,"SEND Error ---- >");
        Calendar calendar = Calendar.getInstance();

        long diff = new Date().getTime() - datestart.getTime();


        String password = MyPreference.getString(context,"password");

        DateFormat format = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss", Locale.getDefault());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,String> data = new HashMap<>();
        data.put("apiUrl", mainPath +""+ apiUrl);
        data.put("param", mData.toString());
        data.put("user_code",MainData.getAgentCode(context));
        data.put("user_id", MainData.getAgentID(context)+"");
        data.put("password", password);
        data.put("time", format.format(new Date()));
        data.put("network", NetworkUtil.getConnection(context).toStringData());
        data.put("long_time", diff+"");
        data.put("date", calendar.get(Calendar.DATE)+"");
        data.put("request", mData.toString());
        data.put("response", response);
        db.collection("TRANS_"+calendar.get(Calendar.YEAR)+"_"+calendar.get(Calendar.MONTH))
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e1 -> Log.w(TAG, "Error adding document", e1));
    }

}