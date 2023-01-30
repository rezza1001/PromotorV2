package com.wadaro.promotor.util;

import android.util.Log;

import java.text.DecimalFormat;

public class SpellingNumber {
    private static final String[] tensNames = { "", " ten", " twenty", " thirty", " forty",
            " fifty", " sixty", " seventy", " eighty", " ninety" };

    private static final String[] numNames = { "", " one", " two", " three", " four", " five",
            " six", " seven", " eight", " nine", " ten", " eleven", " twelve", " thirteen",
            " fourteen", " fifteen", " sixteen", " seventeen", " eighteen", " nineteen" };

    private static final String[] tensNamesIndo = { "", " sepuluh", " dua puluh", " tiga puluh", " empat puluh",
            " lima puluh", " enam puluh", " tujuh puluh", " delapan puluh", " sembilan puluh" };

    private static final String[] numNamesIndo = { "", " satu", " dua", " tiga", " empat", " lima",
            " enam", " tujuh", " delapan", " sembilan", " sepuluh", " sebelas", " dua belas", " tiga belas",
            " empan belas", " lima belas", " enam belas", " tujuh belas", " delapan belas", " sembilan belas" };
    private static String convertEnglish(int number)
    {
        String soFar;

        if (number % 100 < 20)
        {
            soFar = numNames[number % 100];
            number /= 100;
        } else
        {
            soFar = numNames[number % 10];
            number /= 10;

            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0)
            return soFar;
        return numNames[number] + " hundred" + soFar;
    }

    private static String convertIndo(int number)
    {
        String soFar;

//            Log.d("TAGRZ","NUMBER : "+number % 100);
        if (number % 100 < 20)
        {
            soFar = numNamesIndo[number % 100];
            number /= 100;
        } else
        {
            soFar = numNamesIndo[number % 10];
            number /= 10;

            soFar = tensNamesIndo[number % 10] + soFar;
            number /= 10;
        }

        if (number == 0){
            return soFar;
        }
        else if (number == 1){
           return  "seratus" + soFar;
        }
        else {
            return numNamesIndo[number] + " ratus" + soFar;
        }
    }

    public static String convert(long number, String language)
    {
        // 0 to 999 999 999 999
        if (number == 0) {
            return "zero";
        }
        if (number == 1){
            return  "Satu";
        }

        String snumber = Long.toString(number);

        // pad with "0"
        String mask = "000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        snumber = df.format(number);
        Log.d("TAGRZ","snumber : "+ snumber);
        // XXXnnnnnnnnn
        int billions = Integer.parseInt(snumber.substring(0, 3));
        // nnnXXXnnnnnn
        int millions = Integer.parseInt(snumber.substring(3, 6));
        // nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(snumber.substring(6, 9));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(snumber.substring(9, 12));

        String tradBillions;
        switch (billions)
        {
            case 0:
                tradBillions = "";
                break;

            default:
                if (language.equals("EN")){
                    tradBillions = convertEnglish(billions) + " billion ";
                }
                else {
                    tradBillions = convertIndo(billions) + " milyar ";
                }

        }
        String result = tradBillions;

        String tradMillions;
        switch (millions)
        {
            case 0:
                tradMillions = "";
                break;
            default:
                if (language.equals("EN")){
                    tradMillions = convertEnglish(millions) + " million ";
                }
                else {
                    tradMillions = convertIndo(millions) + " juta ";
                }
        }
        result = result + tradMillions;

        String tradHundredThousands;
        switch (hundredThousands)
        {
            case 0:
                tradHundredThousands = "";
                break;
            case 1:
                if (language.equals("EN")){
                    tradHundredThousands = "one thousand ";
                }
                else {
                    tradHundredThousands = "seribu ";
                }
                break;
            default:
                if (language.equals("EN")){
                    tradHundredThousands = convertEnglish(hundredThousands) + " thousand ";
                }
                else {
                    tradHundredThousands = convertIndo(hundredThousands) + " ribu ";
                }

        }
        result = result + tradHundredThousands;

        String tradThousand = null;

        if (language.equals("EN")){
            tradThousand = convertEnglish(thousands);
        }
        else {
            if (thousands == 1){
                tradThousand = "seratus ";
            }
            else {
                tradThousand = convertIndo(thousands);
            }
        }

        result = result + tradThousand;

        // remove extra spaces!
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

}
