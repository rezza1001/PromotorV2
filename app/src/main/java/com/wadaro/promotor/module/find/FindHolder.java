package com.wadaro.promotor.module.find;

public class FindHolder {
    String key, value;
    boolean selected;

    public FindHolder(String key, String value){
        this.key = key;
        this.value = value;
        selected = false;
    }
    public FindHolder(String key, String value, boolean selected){
        this.key = key;
        this.value = value;
        selected = selected;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isSelected() {
        return selected;
    }
}
