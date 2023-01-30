package com.rentas.ppob.api;

import android.content.Context;

import com.rentas.ppob.master.MyPreference;

public class ConfigAPI {
//    public static final String MAIN = "http://103.41.206.133:3000/";
    public static final String MAIN = "https://ppob-api.wadaro.id/";
    public static final int TIME_OUT = 30000;

    public static final String GET_CATEGORIES = "categories";
    public static final String GET_PRODUCTS = "products";
    public static final String POST_CHECK_PRICE = "products/check-price";
    public static final String POTS_LOGIN = "auth/login";
    public static final String GET_PROFILE = "profile";
    public static final String GET_AGENT_BY_ID = "agents/";
    public static final String POST_INVOICE_PRE = "invoices/prepaid";
    public static final String GET_LIST_INVOICE = "invoices/trx-list";
    public static final String GET_RECEIPT = "receipts/";
    public static final String POST_UPDATE_FCM = "auth-agent/update-fcm-token";
    public static final String POST_CHECK_PIN = "auth-agent/check-pin";
    public static final String POST_UPDATE_PIN = "auth-agent/update-pin";
    public static final String POST_RESET_PIN = "auth-agent/reset-pin";
    public static final String POST_RESET_DEVICE = "auth-agent/reset-device";
    public static final String POST_BLOCK_AGENT = "auth-agent/block-agent";
    public static final String POST_REGISTER = "auth-agent";
    public static final String GET_INVOICE_DETAIL = "invoice-details/"; // {agentid}/invoice-number
    public static final String POST_CHECK_AGENT_STATUS = "auth-agent/check-agent";
    public static final String POST_OTP_VALIDATION = "auth-agent/agent-otp-validation";
    public static final String POST_CHECK_PRICE_POS = "products/check-price-postpaid";
    public static final String INQUIRY_POSTPAID = "invoices/inquiry";
    public static final String POST_INVOICE_POS = "invoices/postpaid";
    public static final String POST_LIST_INVOICE = "invoices/trx-list";
    public static final String POST_BALANCE_MUTATION = "balance-mutations/list";
    public static final String GET_COMMISSION = "invoice-deposits/curr-commission/"; // {agent_code}
    public static final String POST_DEPOSIT = "invoice-deposits/topup";

    public static String getMainUrl(Context context){
        String url = MyPreference.getString(context, "MAIN_URL");
        if (url.isEmpty()){
            url = MAIN;
        }
        return url;
    }

    public static void setMainURL(Context context, String url){
        MyPreference.save(context, "MAIN_URL", url);
    }

}
