package com.rentas.ppob.libs;

import java.text.NumberFormat;
import java.util.Locale;

public class MyCurrency {

    public static String toCurrnecy(String currencyName, String pData){
        boolean isNegative = false;
        try {
            double dData = Double.parseDouble(pData);
            if (dData < 0){
                isNegative = true;
                dData = (dData * -1);
            }
            NumberFormat formatKurensi = NumberFormat.getCurrencyInstance(new Locale("id"));
            String data = formatKurensi.format(dData);
            data = data.substring(1,(data.length()-3));

            if (isNegative){
                return "- "+currencyName+" "+data;
            }
            return currencyName+" "+data;
        }catch (Exception e){
            return "0";
        }

    }

    public static String toCurrnecy(String pData){
        boolean isNegative = false;
        try {
            double dData = Double.parseDouble(pData);
            if (dData < 0){
                isNegative = true;
                dData = (dData * -1);
            }
            NumberFormat formatKurensi = NumberFormat.getCurrencyInstance(new Locale("id"));
            String data = formatKurensi.format(dData);
            data = data.substring(1,(data.length()-3));

            if (isNegative){
                return "- "+data;
            }
            return data;
        }catch (Exception e){
            return "0";
        }

    }

    public static String toCurrnecy(int pData){
        boolean isNegative = false;
        try {
            double dData = Double.parseDouble(pData+"");
            if (dData < 0){
                isNegative = true;
                dData = (dData * -1);
            }
            NumberFormat formatKurensi = NumberFormat.getCurrencyInstance(new Locale("id"));
            String data = formatKurensi.format(dData);
            data = data.substring(1,(data.length()-3));
            if (isNegative){
                return "- "+data;
            }
            return data;
        }catch (Exception e){
            return "0";
        }

    }

    public static String toCurrnecy(String currencyName, int pData){
        boolean isNegative = false;
        try {
            double dData = Double.parseDouble(pData+"");
            if (dData < 0){
                isNegative = true;
                dData = (dData * -1);
            }
            NumberFormat formatKurensi = NumberFormat.getCurrencyInstance(new Locale("id"));
            String data = formatKurensi.format(dData);
            data = data.substring(1,(data.length()-3));
            if (isNegative){
                return "- "+currencyName+" "+data;
            }
            return currencyName+" "+data;
        }catch (Exception e){
            return "0";
        }

    }

    public static String toCurrnecy(String currencyName, long pData){
        boolean isNegative = false;
        try {
            double dData = Double.parseDouble(pData+"");
            if (dData < 0){
                isNegative = true;
                dData = (dData * -1);
            }
            NumberFormat formatKurensi = NumberFormat.getCurrencyInstance(new Locale("id"));
            String data = formatKurensi.format(dData);
            data = data.substring(1,(data.length()-3));
            if (isNegative){
                return "- "+currencyName+" "+data;
            }
            return currencyName+" "+data;
        }catch (Exception e){
            return "0";
        }

    }

    public static String toCurrnecy(Long pData){
        boolean isNegative = false;
        try {
            if (pData < 0){
                isNegative = true;
                pData = (pData * -1);
            }
            NumberFormat formatKurensi = NumberFormat.getCurrencyInstance(new Locale("id"));
            String data = formatKurensi.format(pData);
            data = data.substring(1,(data.length()-3));
            if (isNegative){
                return "- "+data;
            }
            return data;
        }catch (Exception e){
            return "0";
        }
    }


}
