package com.rentas.ppob.data;

import android.content.Context;

import com.rentas.ppob.master.MyPreference;

public class MainData{

    public static String AGENT_ID = "agent_id";
    public static String ACCESS_TOKEN = "access_token";
    public static String PIN = "pin";
    public static String FCM_TOKEN = "fcm_token";

    public static String PARTNER = "partnerId";
    public static String PACKAGE = "packageId";
    public static String AGENT_CODE = "agent_code";
    public static String NAME = "name";
    public static String BALANCE = "balance";

    public static String EMAIL = "email";
    public static String PHONE = "phone";

    public static String AVATAR = "avatar";

    public static void setAgentId(Context context, int agentId){
        MyPreference.save(context,AGENT_ID, agentId);

    }

    public static void setAccessToken(Context context, String accessToken){
        MyPreference.save(context,ACCESS_TOKEN, accessToken);
    }
    public static int getAgentID(Context context){
        return MyPreference.getInt(context,AGENT_ID);
    }

    public static String getAccessToken(Context context){
        return MyPreference.getString(context,ACCESS_TOKEN);
    }

    public static void setPIN(Context context, String pPin) {
        MyPreference.save(context,PIN, pPin);
    }

    public static String getPIN(Context context) {
        return MyPreference.getString(context,PIN);
    }

    public static void initProfile(Context context, String partner, String packageId, String name, long balance){
        MyPreference.save(context,PARTNER, partner);
        MyPreference.save(context,PACKAGE, packageId);
        MyPreference.save(context,NAME, name);
        MyPreference.save(context,BALANCE, balance);
    }

    public static long getBalance(Context context){
        return MyPreference.getLong(context, BALANCE);
    }

    public static int getPartnerId(Context context){
        String sPartner = MyPreference.getString(context, PARTNER);
        return Integer.parseInt(sPartner);
    }

    public static String getAgentCode(Context context){
        return MyPreference.getString(context,AGENT_CODE);
    }
    public static String getProfileName(Context context){
        return MyPreference.getString(context,NAME);
    }

    public static void setFcmToken(Context context, String token){
        MyPreference.save(context, FCM_TOKEN,token);
    }

    public static String getFcmToken(Context context) {
        return MyPreference.getString(context,FCM_TOKEN);
    }

    public static void setEmail(Context context, String email){
        MyPreference.save(context, EMAIL, email);
    }

    public static String getEmail(Context context) {
        return MyPreference.getString(context,EMAIL);
    }

    public static void setPhone(Context context, String phone){
        MyPreference.save(context, PHONE, phone);
    }

    public static String getPhone(Context context) {
        return MyPreference.getString(context,PHONE);
    }

    public static void setAgentCode(Context context, String employee){
        MyPreference.save(context, AGENT_CODE, employee);
    }
    public static void setAvatar(Context context, String avatar){
        MyPreference.save(context, AVATAR, avatar);
    }

    public static String getAvatar(Context context){
        return MyPreference.getString(context,AVATAR);
    }

    public static void clear(Context context){
        MyPreference.clear(context);
    }



}
