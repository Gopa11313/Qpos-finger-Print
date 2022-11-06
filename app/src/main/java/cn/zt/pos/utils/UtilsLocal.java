package cn.zt.pos.utils;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by deadlydragger on 1/3/19.
 */

public class UtilsLocal {
    public static String GetStan() {
        Random r = new Random();
        int low = 100000;
        int high = 999999;
        int result = r.nextInt(high-low) + low;
        return String.valueOf(result);
    }


    public static String GetCrrn()
    {
        Calendar calendar = Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int dayOfYear=calendar.get(Calendar.DAY_OF_YEAR);
        String crrn= String.valueOf(year%10)+ String.format("%03d", dayOfYear);
        return crrn+ String.format("%02d", calendar.get(Calendar.MONTH)+1)+""+ String.format("%02d", calendar.get(Calendar.HOUR))+""+ String.format("%02d", calendar.get(Calendar.MINUTE))+""+ String.format("%02d", calendar.get(Calendar.SECOND));
    }
    public static int TIME=3000;

    public static String Date(){
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime().toString();
    }

    public static String convertHexToStringValue(String hex) {
        StringBuilder stringbuilder = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            stringbuilder.append((char)decimal);
        }
        return stringbuilder.toString();
    }
}
