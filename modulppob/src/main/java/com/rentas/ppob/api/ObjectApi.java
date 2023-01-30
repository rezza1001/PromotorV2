package com.rentas.ppob.api;

public class ObjectApi {

    public String key;
    public String value;

    public ObjectApi(String pKey, String pValue){
        key = pKey;
        value = pValue;
    }

    public ObjectApi(String pKey, int pValue){
        key = pKey;
        value = pValue +"";
    }
    public ObjectApi(String pKey, long  pValue){
        key = pKey;
        value = pValue +"";
    }
}
