package com.wadaro.promotor.util;

import java.util.HashMap;

public class KtpValidator {

    private HashMap<Integer, String> MAP_PREFIX = new HashMap<>();

    public KtpValidator(){
        MAP_PREFIX.put(11,"ACEH");
        MAP_PREFIX.put(12,"SUMATERA UTARA");
        MAP_PREFIX.put(13,"SUMATERA BARAT");
        MAP_PREFIX.put(14,"R I A U");
        MAP_PREFIX.put(15,"J A M B I");
        MAP_PREFIX.put(16,"SUMATERA SELATAN");
        MAP_PREFIX.put(17,"BENGKULU");
        MAP_PREFIX.put(18,"LAMPUNG");
        MAP_PREFIX.put(19,"KEPULAUAN BANGKA BELITUNG");
        MAP_PREFIX.put(21,"KEPULAUAN RIAU");
        MAP_PREFIX.put(31,"DKI JAKARTA");
        MAP_PREFIX.put(32,"JAWA BARAT");
        MAP_PREFIX.put(33,"JAWA TENGAH");
        MAP_PREFIX.put(34,"DI YOGYAKARTA");
        MAP_PREFIX.put(35,"JAWA TIMUR");
        MAP_PREFIX.put(36,"B A N T E N");
        MAP_PREFIX.put(51,"B A L I");
        MAP_PREFIX.put(52,"NUSA TENGGARA BARAT");
        MAP_PREFIX.put(53,"NUSA TENGGARA TIMUR");
        MAP_PREFIX.put(61,"KALIMANTAN BARAT");
        MAP_PREFIX.put(62,"KALIMANTAN TENGAH");
        MAP_PREFIX.put(63,"KALIMANTAN SELATAN");
        MAP_PREFIX.put(64,"KALIMANTAN TIMUR");
        MAP_PREFIX.put(71,"SULAWESI UTARA");
        MAP_PREFIX.put(73,"SULAWESI SELATAN");
        MAP_PREFIX.put(74,"SULAWESI TENGGARA");
        MAP_PREFIX.put(75,"GORONTALO");
        MAP_PREFIX.put(76,"SULAWESI BARAT");
        MAP_PREFIX.put(81,"MALUKU");
        MAP_PREFIX.put(82,"MALUKU UTARA");
        MAP_PREFIX.put(94,"PAPUA");
        MAP_PREFIX.put(91,"PAPUA BARAT");               
        MAP_PREFIX.put(72,"SULAWESI TENGAH");
        MAP_PREFIX.put(65,"KALIMANTAN UTARA");
    }

    public boolean valid(String no){
        try {
            no = no.trim();
            if (no.length() != 16){
                return false;
            }
            int province = Integer.parseInt(no.substring(0,2));
            if (!MAP_PREFIX.containsKey(province)){
                return false;
            }
            int month = Integer.parseInt(no.substring(8,10));
            return month <= 12;
        } catch (Exception e) {
            return false;
        }

    }

}



































