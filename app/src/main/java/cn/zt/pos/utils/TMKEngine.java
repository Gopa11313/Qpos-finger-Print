package cn.zt.pos.utils;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TMKEngine {

    TMKEngineCallback tmkEngineCallback;
    /***
     *
     * @param terminalId LIKE Q001234
     * @param activationCode LIKE 442233
     */
    public TMKEngine(String terminalId, String activationCode, String payload, String hash, TMKEngineCallback tmkEngineCallback)
            throws NoSuchAlgorithmException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, UnsupportedEncodingException {
        this.tmkEngineCallback = tmkEngineCallback;

        String mainKey = sha512Hex(terminalId + activationCode);

        String aesKey = mainKey.substring(0, 32).toUpperCase();
        String ivVector = mainKey.substring(32, 64).toUpperCase();
        String hmacKey = mainKey.substring(64, 96).toUpperCase();

        String hmac = hmac(hmacKey, payload);

        if(hmac.equalsIgnoreCase(hash)){
            String decrypt = decrypt(payload, aesKey, ivVector);
            tmkEngineCallback.onSuccess(decrypt);
        }else{
            tmkEngineCallback.onFailure("Integrity test failed");
        }

//        String decrypt = decrypt(payload, aesKey, ivVector);
//        Log.d("cipher", "TMKEngine: AES: " + decrypt);


    }

    private String decrypt(String payload, String key, String iv) throws NoSuchPaddingException,
            NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        String decrypted;
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hexStringToByteArray(key), "AES"), new IvParameterSpec(hexStringToByteArray(iv)));
        byte[] deciphered = cipher.doFinal(hexStringToByteArray(payload));
        decrypted = bytesToHex(deciphered);
        return decrypted;
    }

    private String bytesToHex(byte[] bytes) {
        char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {

            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private String hmac(String key, String data) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidKeyException {
        Mac sha512_HMAC;
        byte[] byteKey = key.getBytes("UTF-8");
        final String HMAC_SHA512 = "HmacSHA512";
        sha512_HMAC = Mac.getInstance(HMAC_SHA512);
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
        sha512_HMAC.init(keySpec);
        byte[] mac_data = sha512_HMAC.
                doFinal(data.getBytes("UTF-8"));
        //result = Base64.encode(mac_data);
        return bytesToHex(mac_data);
    }

    private String trimFromLength(int start, int end, String data) {
        return data.substring(start, end);
    }

    private String sha512Hex(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] digest = md.digest(data.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    private String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes()));
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public   interface TMKEngineCallback{
         void onSuccess(String json);
           void onFailure(String message);
    }
}
