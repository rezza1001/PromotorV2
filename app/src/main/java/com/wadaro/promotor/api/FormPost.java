package com.wadaro.promotor.api;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.wadaro.promotor.database.MyPreference;
import com.wadaro.promotor.database.table.UserDB;
import com.wadaro.promotor.module.MyDevice;
import com.wadaro.promotor.util.NetworkUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FormPost extends AsyncTask<String, Integer, String> {
    private static String TAG = "FormPost";

    private LoadingWindow loadingWindow ;
    private UserDB userDB;
    private JSONObject mData         = new JSONObject();
    private JSONObject mDataImage    = new JSONObject();
    private JSONObject existImage    = new JSONObject();

    boolean isSingleFile = false;
    boolean showLoading = true;
    private Date datestart;
    private String mSuburl;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    public FormPost(Context context, String subUrl){
        datestart = new Date();
        loadingWindow = new LoadingWindow(context);
        userDB = new UserDB();
        userDB.getData(context);
        mSuburl = subUrl;
        this.context = context;
    }

    public void addParam(ObjectApi objectAPI){
        try {
            mData.put(objectAPI.key, objectAPI.value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addParam(String key, JSONArray value) {
        try {
            mData.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setData(JSONObject data){
        mData = data;
    }
    public void setImages(JSONObject data){
        mDataImage = data;
        isSingleFile = true;
    }

    public JSONObject getData(){
        return mData;
    }

    public JSONObject getImages(){
        return mDataImage;
    }

    public void addImage(String key, ArrayList<String> mPaths){
        try {
            JSONArray ja = new JSONArray();
            for (String s: mPaths){
                ja.put(s);
            }
            mDataImage.put(key, ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void addImage(String key, String mPaths){
        try {
            isSingleFile = true;
            mDataImage.put(key, mPaths);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showLoading(boolean show){
        showLoading = show;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (showLoading){
            loadingWindow.show();
        }
    }
    @Override
    protected String doInBackground(String... strings) {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);

        HttpClient client = new DefaultHttpClient(httpParams);
        HttpPost post = new HttpPost(Config.MAIN_PATH+mSuburl);
        post.addHeader("Authorization","Bearer "+ userDB.token);
//        post.addHeader("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
//        post.addHeader("Accept", "application/json");
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        Log.d(TAG,"TOKEN : "+userDB.token);
        Log.d(TAG,"PATH : "+ Config.MAIN_PATH+mSuburl);
        for (int i=0; i<post.getAllHeaders().length; i++){
            Log.d(TAG,"Add Header : "+ post.getAllHeaders()[i]);
        }
        Log.d(TAG,"PARAM JSON : "+mData.toString());
        Log.d(TAG,"PARAM IMAGE : "+mDataImage.toString());
        Iterator<String> iter = mData.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                String value = mData.getString(key);
                entityBuilder.addTextBody(key, value);
            } catch (JSONException e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
        HttpResponse response = null;

        int index = 0;
        Iterator<String> iterImage = mDataImage.keys();
        if (!isSingleFile){
            while (iterImage.hasNext()) {
                String key = iterImage.next();
                try {
                    JSONArray value = mDataImage.getJSONArray(key);
                    for (int x=0; x<value.length(); x++) {
                        File bin = new File(value.getString(x));
                        existImage.put(key,bin.exists());
                        FileBody bin1 = new FileBody(bin);
                        entityBuilder.addPart(key+"[]",bin1 );
                    }
                } catch (JSONException e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }

            }
        }
        else {
            while (iterImage.hasNext()) {
                String key = iterImage.next();
                try {
                    File bin = new File(mDataImage.getString(key));
                    FileBody bin1 = new FileBody(bin);
                    Log.d(TAG,"mDataImage "+ mDataImage.toString());
                    Log.d(TAG,"Image "+ key);
                    entityBuilder.addPart(key,bin1);
                } catch (JSONException e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }
            }

        }

        HttpEntity entity = entityBuilder.build();
        post.setEntity(entity);
        try {
            response = client.execute(post);
            index ++;
            publishProgress(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject objResponse = new JSONObject();
        if(response != null){
            int header_status = response.getStatusLine().getStatusCode();
            HttpEntity httpEntity = response.getEntity();
            Log.d(TAG,"HEADER : "+header_status);
            try {
                objResponse.put("CODE", header_status);
                objResponse.put("DATA", EntityUtils.toString(httpEntity));
                return objResponse.toString();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return ErrorCode.FAILED+"";
            }
        }

        return ErrorCode.FAILED+"";
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.w(TAG,s);
        if (showLoading){
            loadingWindow.dismiss();
        }
        if (s.equals(""+ErrorCode.FAILED)){
            mReceiveListener.onReceive(null, ErrorCode.FAILED, "Undefined Error");
            return;
        }

        try {
            JSONObject obj = new JSONObject(s);
            JSONObject objData = new JSONObject(obj.getString("DATA"));
            int header_code = obj.getInt("CODE");

            if (header_code == ErrorCode.OK_200){
                int code = objData.getInt("status");
                String message = objData.getString("message");
                Log.d(TAG,"Code : "+ code+" | Message : "+ message+" | Data : "+ objData.toString());
                if (mReceiveListener != null){
                    mReceiveListener.onReceive(objData, code, message);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            mReceiveListener.onReceive(null, ErrorCode.UNDIFINED_ERROR, e.getMessage());
            sendError(e,s);
        }
    }


    private onReceiveListener mReceiveListener;
    public void setOnReceiveListener(onReceiveListener mReceiveListener){
        this.mReceiveListener = mReceiveListener;
    }
    public interface onReceiveListener{
        void onReceive(JSONObject obj, int code, String message);
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
        data.put("apiUrl", Config.MAIN_PATH +""+ mSuburl);
        data.put("param", mData.toString());
        data.put("param_file", mDataImage.toString());
        data.put("file_status", existImage.toString());
        data.put("token", userDB.token);
        data.put("user_id", userDB.user_id);
        data.put("username", userDB.user_name);
        data.put("password", password);
        data.put("versionApps",device.getVersion());
        data.put("device",joDevice.toString());
        data.put("time", format.format(new Date()));
        data.put("network", NetworkUtil.getConnection(context).toStringData());
        data.put("timemillis", System.currentTimeMillis()+"");
        data.put("long_time", diff+"");

        String response;
        if (Objects.requireNonNull(e.getMessage()).length() > 100){
            response = e.getMessage().substring(0,100);
        }
        else {
            response = e.getMessage();
        }
        data.put("error", response);
        if (responseData.length() > 500){
            responseData = responseData.substring(0,500);
        }

        data.put("response", responseData);
        db.collection("DBUG_"+calendar.get(Calendar.YEAR)+"_"+calendar.get(Calendar.MONTH))
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e1 -> Log.w(TAG, "Error adding document", e1));
    }
}
