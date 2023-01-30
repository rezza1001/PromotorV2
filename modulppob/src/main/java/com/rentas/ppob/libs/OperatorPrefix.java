package com.rentas.ppob.libs;

import android.util.Log;

import com.rentas.ppob.R;

import java.util.HashMap;

public class OperatorPrefix {
    private HashMap<String, String> MAP_PREFIX = new HashMap<>();

    public static final String TELKOMSEL = "TELKOMSEL";
    public static final String INDOSAT = "INDOSAT";
    public static final String XL = "XL";
    public static final String THREE = "THREE";
    public static final String SMART = "SMARTFREN";
    public static final String AXIS = "AXIS";
    private String phone_number = "0";
    private boolean isValid = false;
    private String operator = "Tidak ditemukan";

    public void getInfo(String number) {
        Log.d("OperatorPrifix", number);
        OperatorPrefix operatorPrifix = new OperatorPrefix();
        operatorPrifix.MAP_PREFIX = new HashMap<>();
        operatorPrifix.MAP_PREFIX.put("0811", "TELKOMSEL - Halo");
        operatorPrifix.MAP_PREFIX.put("0812", "TELKOMSEL - Simpati");
        operatorPrifix.MAP_PREFIX.put("0813", "TELKOMSEL - Simpati");
        operatorPrifix.MAP_PREFIX.put("0821", "TELKOMSEL - Simpati");
        operatorPrifix.MAP_PREFIX.put("0822", "TELKOMSEL - Simpati");
        operatorPrifix.MAP_PREFIX.put("0823", "TELKOMSEL - Kartu As");
        operatorPrifix.MAP_PREFIX.put("0852", "TELKOMSEL - Kartu As");
        operatorPrifix.MAP_PREFIX.put("0853", "TELKOMSEL - Kartu As");
        operatorPrifix.MAP_PREFIX.put("0851", "TELKOMSEL - Kartu As");

        operatorPrifix.MAP_PREFIX.put("0814", "INDOSAT - M2 Broadband");
        operatorPrifix.MAP_PREFIX.put("0815", "INDOSAT - Mentari");
        operatorPrifix.MAP_PREFIX.put("0816", "INDOSAT - Mentari");
        operatorPrifix.MAP_PREFIX.put("0855", "INDOSAT - Matrix");
        operatorPrifix.MAP_PREFIX.put("0856", "INDOSAT - IM3");
        operatorPrifix.MAP_PREFIX.put("0857", "INDOSAT - IM3");
        operatorPrifix.MAP_PREFIX.put("0858", "INDOSAT - Mentari");

        operatorPrifix.MAP_PREFIX.put("0817", "XL");
        operatorPrifix.MAP_PREFIX.put("0818", "XL");
        operatorPrifix.MAP_PREFIX.put("0819", "XL");
        operatorPrifix.MAP_PREFIX.put("0859", "XL");
        operatorPrifix.MAP_PREFIX.put("0877", "XL");
        operatorPrifix.MAP_PREFIX.put("0878", "XL");

        operatorPrifix.MAP_PREFIX.put("0895", "THREE");
        operatorPrifix.MAP_PREFIX.put("0896", "THREE");
        operatorPrifix.MAP_PREFIX.put("0897", "THREE");
        operatorPrifix.MAP_PREFIX.put("0898", "THREE");
        operatorPrifix.MAP_PREFIX.put("0899", "THREE");

        operatorPrifix.MAP_PREFIX.put("0881", "SMARTFREN");
        operatorPrifix.MAP_PREFIX.put("0882", "SMARTFREN");
        operatorPrifix.MAP_PREFIX.put("0883", "SMARTFREN");
        operatorPrifix.MAP_PREFIX.put("0884", "SMARTFREN");
        operatorPrifix.MAP_PREFIX.put("0885", "SMARTFREN");
        operatorPrifix.MAP_PREFIX.put("0886", "SMARTFREN");
        operatorPrifix.MAP_PREFIX.put("0887", "SMARTFREN");
        operatorPrifix.MAP_PREFIX.put("0888", "SMARTFREN");
        operatorPrifix.MAP_PREFIX.put("0889", "SMARTFREN");

        operatorPrifix.MAP_PREFIX.put("0838", "AXIS");
        operatorPrifix.MAP_PREFIX.put("0831", "AXIS");
        operatorPrifix.MAP_PREFIX.put("0832", "AXIS");
        operatorPrifix.MAP_PREFIX.put("0833", "AXIS");

        try {
            if (number.substring(0, 2).equals("62")) {
                String num = number.substring(2);
                number = "0" + num;
            } else if (number.substring(0, 3).equals("+62")) {
                String num = number.substring(3);
                number = "0" + num;
            }
            else if (number.startsWith("8")){
                number = "0" + number;
            }
            if (number.length() < 4) {
                return;
            }

            Log.d("OperatorPref", "Phone Number : " + number);
            phone_number = number;
            if (operatorPrifix.MAP_PREFIX.get(number.substring(0, 4)) != null) {
                if (phone_number.length() < 9){
                    isValid = false;
                    operator = "Nomor Telepon Tidak Valid";
                }else{
                    isValid = true;
                    operator = operatorPrifix.MAP_PREFIX.get(number.substring(0, 4));
                }
            } else {
                isValid = false;
                operator = "Prefix tidak dikenali";
            }
        } catch (Exception e) {
            isValid = false;
            operator = "Prefix tidak dikenali";
        }


    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public String getPhoneIndonesia() {
        return "+62"+phone_number.substring(1);
    }

    public String getOperator() {
        return operator.split(" ")[0];
    }

    public Boolean isValidated() {
        return isValid;
    }

    public String getResponse(){
        return operator;
    }

    public Boolean getStatus() {
        return isValid;
    }

    public int getImage() {
        if (operator.contains("TELKOMSEL")) {
            return R.drawable.telkomsel_list_medium;
        } else if (operator.contains("INDOSAT")) {
            return R.drawable.indosat_list_medium;
        } else if (operator.contains("XL")) {
            return R.drawable.xl_list_medium;
        } else if (operator.contains("THREE")) {
            return R.drawable.tri_list_medium;
        } else if (operator.contains("SMARTFREN")) {
            return R.drawable.smartfren_list_medium;
        } else if (operator.contains("AXIS")) {
            return R.drawable.axis_list_medium;
        } else {
            return 0;
        }
    }

}