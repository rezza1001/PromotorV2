package com.wadaro.promotor.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by pho0890910 on 2/21/2019.
 */
public class GlobalHelper {

    public static boolean isEmpty(String temp){
        if(temp==null||temp.isEmpty()||temp.trim().equals("")){
            return true;
        }
        return false;
    }

    public static boolean isLessThanMinLength(String temp, int minLength){
        if(temp.length() < minLength) return true;

        return false;
    }

    public static String formatIndonesiaCurrency(double number){
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return nf.format(number);
    }

    public static String formatIndonesiaCurrency(BigDecimal number){
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return nf.format(number.doubleValue());
    }

}
