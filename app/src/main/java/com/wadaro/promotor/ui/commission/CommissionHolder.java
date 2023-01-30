package com.wadaro.promotor.ui.commission;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mochamad Rezza Gumilang on 15/Feb/2021.
 * Class Info :
 */

public class CommissionHolder {

    SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));

    protected Date deliveryDate = new Date();
    int qtyUnit ;
    long commission;

    public CommissionHolder(String dateStr, int qtyUnit, long commission){
        this.commission = commission;
        this.qtyUnit = qtyUnit;

        if (dateStr.isEmpty() && !dateStr.equals("null")){
            try {
                deliveryDate = oldFormat.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy", new Locale("id"));
        return format.format(deliveryDate);
    }
}
