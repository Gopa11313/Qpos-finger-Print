package cn.zt.upi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.zt.pos.utils.PrefUtil;


/**
 * Created by deadlydragger on 1/14/19.
 */
public class UtilsQRCode {
    private static HashMap<String, String> map;
    private static String terminalId;
    private static String text = "000201010212153127330524000203441005000000000015204729953035245406150.005802NP5915RAKESH SHRESTHA6009KATHMANDU62600120201809281708350113950520201809281708350113960708100505016304af24";

    public static List<UPIQR> getUPIData(String merchantId) {
        //53 Currency Code
        //54 Amount
        //58 Country Code
        //59 Merchant Name
        //60 Merchant City
        //63 checksum
//        15_31_32890524(Acquirer IIN)_32890524 (Forwarding IIN)_000047797701524 (Merchant ID)
//        52_04_5691(MCC)
//         53_03_524(Currency Code)
//        58_02_NP(Country Code)
//        59_16_Aarav Collection(Merchant Name)
//        60_09_Kathmandu(Merchant City)
//        62_60
//        01_20_00000000000000000000(Bill Number)
//        05_20_00000000000000000000(Reference label)
//        07_08_Q0004895(Terminal ID)
//        63_04_E4A7(Check Sum)


        List<UPIQR> tagAndValue = new ArrayList<>();
        char[] merchantIds = merchantId.toCharArray();
        for (int i = 0; i < merchantIds.length; ) {
            if (i + 4 < merchantIds.length) {

                String tag = (String.valueOf(merchantIds[i]) + String.valueOf(merchantIds[i + 1]));
                int length = Integer.parseInt(String.valueOf(merchantIds[i + 2]) + String.valueOf(merchantIds[i + 3]));
                StringBuilder values = new StringBuilder();

                for (int j = i + 4; j < i + 4 + length; j++) {
                    values.append(String.valueOf(merchantIds[j]));
                }
                // tagAndValue.put(tag, values.toString());
                if (length <= 9) {
                    tagAndValue.add(new UPIQR(tag, "0" + length, values.toString()));

                } else {
                    tagAndValue.add(new UPIQR(tag, "" + length, values.toString()));

                }
                i += 4 + length;
            }
        }
        return tagAndValue;
    }

    public static HashMap<String, String> getUPIDict(String merchantId) {
        HashMap<String, String> tagAndValue = new HashMap<>();
        char[] merchantIds = merchantId.toCharArray();
        for (int i = 0; i < merchantIds.length; ) {
            if (i + 4 < merchantIds.length) {

                String tag = String.valueOf(merchantIds[i]) + String.valueOf(merchantIds[i + 1]);
                int length = Integer.parseInt(String.valueOf(merchantIds[i + 2]) + String.valueOf(merchantIds[i + 3]));
                StringBuilder values = new StringBuilder();

                for (int j = i + 4; j < i + 4 + length; j++) {
                    values.append(String.valueOf(merchantIds[j]));
                }
                tagAndValue.put(tag, values.toString());

                i += 4 + length;
            }
        }
        Log.d("dict", tagAndValue.toString());
        return tagAndValue;
    }

    public static HashMap<String, String> getTerminalId(String merchantId) {
        HashMap<String, String> tagAndValue = new HashMap<>();
        char[] merchantIds = merchantId.toCharArray();
        for (int i = 0; i < merchantIds.length; ) {
            if (i + 4 < merchantIds.length) {

                String tag = String.valueOf(merchantIds[i]) + String.valueOf(merchantIds[i + 1]);
                int length = Integer.parseInt(String.valueOf(merchantIds[i + 2]) + String.valueOf(merchantIds[i + 3]));
                StringBuilder values = new StringBuilder();

                for (int j = i + 4; j < i + 4 + length; j++) {
                    values.append(String.valueOf(merchantIds[j]));
                }
                tagAndValue.put(tag, values.toString());

                i += 4 + length;
            }
        }
        return tagAndValue;
    }

    public static String crc16(byte[] data) {
//        0xFFFF
        int PRESET_VALUE = 0x1D0F;
        // initial value
        int POLYNOMIAL = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)

        // byte[] testBytes = "123456789".getBytes("ASCII");


        for (byte b : data) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((PRESET_VALUE >> 15 & 1) == 1);
                PRESET_VALUE <<= 1;
                if (c15 ^ bit) PRESET_VALUE ^= POLYNOMIAL;
            }
        }

        PRESET_VALUE &= 0xffff;
        String n = Integer.toHexString(PRESET_VALUE);
        return n;
    }


    public static List<UPIQR> getUPIData(String merchantId, Context context) {
        //53 Currency Code
        //54 Amount
        //58 Country Code
        //59 Merchant Name
        //60 Merchant City
        //63 checksum
        Map<String, String> map = new HashMap<>();
        List<UPIQR> tagAndValue = new ArrayList<>();
        char[] merchantIds = merchantId.toCharArray();
        for (int i = 0; i < merchantIds.length; ) {
            if (i + 4 < merchantIds.length) {

                String tag = (String.valueOf(merchantIds[i]) + String.valueOf(merchantIds[i + 1]));
                int length = Integer.parseInt(String.valueOf(merchantIds[i + 2]) + String.valueOf(merchantIds[i + 3]));
                StringBuilder values = new StringBuilder();

                for (int j = i + 4; j < i + 4 + length; j++) {
                    values.append(String.valueOf(merchantIds[j]));
                }

                map.put(tag, values.toString());
                System.out.println("tag len and value:: " + tag + " " + values.length() + " " + values.toString());
                if (length <= 9) {
                    tagAndValue.add(new UPIQR(tag, "0" + length, values.toString()));

                } else {
                    tagAndValue.add(new UPIQR(tag, "" + length, values.toString()));

                }
                i += 4 + length;
            }
        }
        savePreference(map, context);
        return tagAndValue;
    }


    public static void savePreference(Map<String, String> map, Context context) {
        SharedPreferences pSharedPref = context.getSharedPreferences("upiqrc", Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            JSONObject jsonObject = new JSONObject(map);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
//            editor.remove("upiqrc").commit();
            editor.putString("upiqrc", jsonString);
            editor.commit();
        }
    }

    public static Map<String, String> getUPQRCPreference(Context context) {
        Map<String, String> map = new HashMap<>();
        SharedPreferences pref = context.getSharedPreferences("upiqrc", Context.MODE_PRIVATE);
        try {
            if (pref != null) {
                String jsonString = pref.getString("upiqrc", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    String value = (String) jsonObject.get(key);
                    System.out.println("tag leng and value:: " + key + " " + value.length() + " " + value);

                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void generateQR(ImageView imageView, String text) {
        Bitmap bitmap = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    public static String updateTagLenghtValue(String amount, Map<String, String> map) {
        String checksum = UtilsQRCode.crc16(text.substring(0, text.length() - 4).getBytes());
        Log.d("dinesh", "" + checksum);
        List<UPIQR> upi = UtilsQRCode.getUPIData(text);
        for (int i = 0; i < upi.size(); i++) {
            System.out.println(upi.get(i).getTag() + " " + upi.get(i).getValue());

            if (upi.get(i).getTag().equalsIgnoreCase("54")) {
                upi.get(i).setLength(amount.length());
                upi.get(i).setValue(amount);
            }

            if (upi.get(i).getTag().equalsIgnoreCase("63")) {
                upi.get(i).setLength(checksum.length());
                upi.get(i).setValue(checksum);
            }

            if (upi.get(i).getTag().equalsIgnoreCase("62")) {
                String termId;
              /*  if (AppCache.getApplicationMode() == AppCache.MODE_CHINESE_EMBASSY) {
                    termId = "0120000000000000000000000520000000000000000000000708" + PrefUtil.getTerminalNo();
                } else {*/
                    termId = "0120000000000000000000000520000000000000000000000708" + PrefUtil.getTerminalNo();
//                }

                upi.get(i).setLength(/*map.get("62")*/termId.length());
                upi.get(i).setValue(/*map.get("62")*/termId);
            }

            if (upi.get(i).getTag().equalsIgnoreCase("52")) {

              /*  if (AppCache.getApplicationMode() == AppCache.MODE_CHINESE_EMBASSY) {
                    upi.get(i).setLength(*//*map.get("52")*//*"9222".length());
                    upi.get(i).setValue(*//*map.get("52")*//*"9222");

                } else {*/
                    upi.get(i).setLength(/*map.get("52")*/"7299".length());
                    upi.get(i).setValue(/*map.get("52")*/"7299");


//                }
            }

            if (upi.get(i).getTag().equalsIgnoreCase("53")) {
                upi.get(i).setLength("524".length());
                upi.get(i).setValue(/*map.get("53")*/"524");
            }
            if (upi.get(i).getTag().equalsIgnoreCase("59")) {
                upi.get(i).setLength(/*map.get("59")*/PrefUtil.getMerchantName().length());
                upi.get(i).setValue(/*map.get("59")*/PrefUtil.getMerchantName());
//                System.out.println("Name:: "+map.get("59").length()+" "+map.get("59"));
            }


            if (upi.get(i).getTag().equalsIgnoreCase("60")) {
                upi.get(i).setLength(/*map.get("60")*/"Durbarmarg".length());
                upi.get(i).setValue(/*map.get("60")*/"Durbarmarg");
            }
            if (upi.get(i).getTag().equalsIgnoreCase("15")) {
                String merchId;
             /*   if (AppCache.getApplicationMode() == AppCache.MODE_CHINESE_EMBASSY){
                    merchId = "3187052400520446" + PrefUtil.getMerchantNo();
                }else{*/
                    merchId = "2733052400020344" + PrefUtil.getMerchantNo();

//                }
                upi.get(i).setLength(/*map.get("15")*/merchId.length());
                upi.get(i).setValue(/*map.get("15")*/merchId);
            }
        }
        StringBuilder updateUPI = new StringBuilder();
        for (int j = 0; j < upi.size(); j++) {
            updateUPI.append(upi.get(j).getTag());
            updateUPI.append(upi.get(j).getLength());
            updateUPI.append(upi.get(j).getValue());
        }
        Log.d("upi", updateUPI.toString());
        String tempdata = updateUPI.toString().substring(0, updateUPI.toString().length() - 4);
        checksum = UtilsQRCode.crc16(tempdata.getBytes());
        System.out.println("tempdata" + tempdata + checksum);
      /*  return tempdata + checksum*/return tempdata + (checksum.length() == 4 ? checksum : "0"+checksum);
    }

   /* public static void getTerminal_Id(String id,Context context){
        map=UtilsQRCode.getUPIDict(id);
        terminalId=map.get("07");
        GetBalance.setTerminalId(terminalId);
        Log.d("dinesh","map"+map+"..."+terminalId+"....");
    }*/
}
