package com.rentas.ppob.home;

import com.rentas.ppob.data.CategoryData;

import java.util.HashMap;

public class MenuHolder {

    private static final HashMap<String,String> mapIcon = new HashMap<>();
    static {
        mapIcon.put(CategoryData.PAKET_DATA,"ic_paketdata");
        mapIcon.put(CategoryData.PLN_TOKEN,"ic_plntoken");
        mapIcon.put(CategoryData.PLN,"ic_pln");
        mapIcon.put(CategoryData.BPJS_KESEHATAN,"ic_bpjs");
        mapIcon.put(CategoryData.BPJS_KETENAGAKERJAAN,"ic_bpjs");
        mapIcon.put(CategoryData.PULSA,"ic_pulsa");
        mapIcon.put(CategoryData.MULTIFINANCE,"ic_multifinance");
    }

    public  int id = 0;
    public  String name;
    public String icon;
    public  String code;
    boolean isSelected = false;

    public MenuHolder(String code, String name, String icon){
        this.name = name;
        this.icon = icon;
        this.code = code;
    }


    public MenuHolder(int id,String code, String name,String icon){
        this.name = name;
        this.id = id;
        this.code = code;
        this.icon = getIconName(code);
    }

    public MenuHolder(int id, String name,String icon){
        this.name = name;
        this.icon = icon;
        this.id = id;
    }

    public String getIconName(String code){
       if (mapIcon.get(code) == null){
           return "ic_pulsa";
       }
       return mapIcon.get(code);
    }




}
