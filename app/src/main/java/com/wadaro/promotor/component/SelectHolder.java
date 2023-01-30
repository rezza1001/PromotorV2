package com.wadaro.promotor.component;

import org.json.JSONException;
import org.json.JSONObject;

public class SelectHolder {
    public String id = "";
    public String value = "";
    public String data = "";
    public boolean checked = false;

    public SelectHolder(String pId, String pValue, boolean checked){
        this.id         = pId;
        this.value      = pValue;
        this.checked    = checked;
    }
    public SelectHolder(String pId, String pValue){
        this.id         = pId;
        this.value      = pValue;
    }

    public SelectHolder(){
    }

    public SelectHolder(String value){
        try {
            JSONObject obj = new JSONObject(value);
            this.id      = obj.getString("id");
            this.value   = obj.getString("value");
            this.data   = obj.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toString(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("value", value);
            obj.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
