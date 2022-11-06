package cn.zt.pos.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.util.Arrays;

import cn.zt.pos.MainApplication;
import cn.zt.upi.UtilsQRCode;

public class PrefUtil {
    public static final String Key_Script = "script";
    public static final String Key_SerialNo = "serialNo";
    public static final String Key_BatchNo = "batchNo";
    public static final String Key_MerchantNo = "merchantNo";
    public static final String Key_TerminalNo = "terminalNo";
    public static final String Key_TerminalPay = "terminalPay";


    public static final String Key_MerchantName = "merchantName";
    public static final String Key_IP = "ip";
    public static final String Key_Port = "port";
    public static final String Key_Tpdu = "tpdu";
    public static final String Key_KeyIndex = "keyIndex";
    public static final String Key_Signed = "signed";
    public static final String Key_OperatorNo = "operatorNo";
    public static final String Key_Reversal = "reversal";
    public static final String Key_ReversalNum = "reversalNum";
    public static final String KEY_COMMUN_WAY = "communWay";


    public static final String ISSUPPORTSM = "isSupportSM";
    public static final String ISFIRSTRUN = "isFirstRun";
    public static final String ICPASSWORD = "icPassword";//ic公钥
    public static final String ICPARAMETER = "icParameter";//ic参数
    public static final String ICSMD = "icSMD";//国密ic参数


    public static final String Key_OverTime = "overtime";
    public static final String Key_DoSignRepeatSend = "doSignRepeatSend";
    public static final String Key_ChongZhengRepeatSend = "chongZhengRepeatSend";
    public static final String Key_MaxTradeNum = "maxTradeNum";
    public static final String GPSFLG = "gps";


    //操作员ID
    public static final String Key_Operation = "Operation";


    // offline trade
    private static final String isOffLineTrade = "isOffLineTrade";

    public static String payload;

    public static String getPayload() {
        return UtilsQRCode.updateTagLenghtValue("10", null);
    }
    public static void setPayload(String payload) {
        PrefUtil.payload = payload;
    }

    public static boolean getIsOffLineTrade() {
        return sharedPreferences.getBoolean(isOffLineTrade, true);//false
    }

    public static void setIsOffLineTrade(boolean key1) {
        sharedPreferences.edit().putBoolean(isOffLineTrade, key1).commit();
    }


    //传统类交易
    public static boolean Consume_Control = true;
    public static boolean ConsumeCancel_Control = true;
    public static boolean Retfund_Control = true;
    public static boolean BalanceInquiry_Control = true;
    public static boolean Pre_Control = true;
    public static boolean PreCancel_Control = true;
    public static boolean PreComplete_Control = true;
    public static boolean PreCompleteCancel_Control = true;
    public static boolean UnionPayQRCode_Control = true;
    public static boolean FullMess_isencry = true;

    //交易输密控制
    public static boolean ConsumeCancel_IsPwd = true;
    public static boolean PreCancel_IsPwd = true;
    public static boolean PreCompleteCancel_IsPwd = true;
    public static boolean ConsumeComleteRequest_Ispwd = true;

    //交易刷卡控制
    public static boolean ConsumeCancel_IsSwipCard = true;
    public static boolean PreCompleteCancel_IsSwipCard = true;

    //结算交易控制
    public static boolean Settlement_IsAutoSignout = true;

    //其他交易控制
    public static boolean Director_IsPwd = true;
    public static boolean HandInput_IsPwd = true;
    public static boolean Tips_IsSupport = true;

    public static boolean Title_IsLogo = true;

    public static boolean Print_Control = true;

    //是否电子签名
    public static boolean Electric_Signatur = true;


    //ip1005 key1密文
    public static String key1 = "";
    //ip1005 随机数明文
    public static String Random = "";

    //激活状态标志位
    public static boolean activation = true;

    //签到重发次数
    public static int relogin = 3;
    //冲正重发次数
    public static int rechongzheng = 3;

    public static int tradetimeout = 60;
    /*
    * 打印联数
    * 1.商户存根
    * 2.持卡人存根
    * 3.银行存根
    * */
    public static int Print_Pieces = 1;
    public static boolean Print_Repeat = false;

    //签名数据字符串格式
    public static String SignDataStr = "";

    //锁定终端false/解锁状态true
    public static boolean IsLock = false;


    public static final String TPDU = "tpdu";

    public static final boolean getICPARAMETER() {
        return getBoolean(ICPARAMETER, false);
    }

    public static final void setICPARAMETER(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(ICPARAMETER, ipvalue).commit();
    }

    public static final boolean getGPSFLG() {
        return getBoolean(GPSFLG, false);
    }

    public static final void setGPS(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(GPSFLG, ipvalue).commit();
    }

    public static final boolean getICPASSWORD() {
        return getBoolean(ICPASSWORD, false);
    }

    public static final void setICPASSWORD(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(ICPASSWORD, ipvalue).commit();
    }

    public static final boolean getICSMD() {
        return getBoolean(ICSMD, false);
    }

    public static final void setICSMD(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(ISFIRSTRUN, ipvalue).commit();
    }

    //第一次运行
    public static final boolean getISFIRSTRUN() {
        return getBoolean(ISFIRSTRUN, false);
    }

    public static final void setISFIRSTRUN(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(ISFIRSTRUN, ipvalue).commit();
    }

    public static final boolean getISSUPPORTSM() {
        return getBoolean(ISSUPPORTSM, true);
    }

    public static final void setISSUPPORTSM(boolean ipvalue) {
        sharedPreferences.edit().putBoolean(ISSUPPORTSM, ipvalue).commit();
    }

    public static final void setTPDU(String ipvalue) {
        sharedPreferences.edit().putString(TPDU, ipvalue).commit();
    }

    public static final String getTPDU() {
        return getString(TPDU, "");
    }

    public static final String Head = "Head";

    public static final void setHead(String ipvalue) {
        sharedPreferences.edit().putString(Head, ipvalue).commit();
    }

    public static final String getHead() {
        String head = getString(Head, "");
        if (head == null || head.length() <= 0) {
            return getTPDU();
        } else {
            return head;
        }
    }

    //转账汇款临时使用
    public static final String TransterCardNo = "TransterCardNo";

    public static String getKeyCommunWay() {
        return getString(KEY_COMMUN_WAY, "SOCKET");
    }

    public static void setKeyCommunWay(String value) {
        sharedPreferences.edit().putString(KEY_COMMUN_WAY, value).commit();
    }

    public static final void setOperatID(String ipvalue) {
        sharedPreferences.edit().putString(Key_Operation, ipvalue).commit();
    }

    public static final String getOperatID() {
        return getString(Key_Operation, "");
    }

    //传输密钥
    public static final String TmpTLK = "TmpTLK";

    public static final void setTLK(String ipvalue) {
        sharedPreferences.edit().putString(TmpTLK, ipvalue).commit();
    }

    public static final String getTLK() {
        return getString(TmpTLK, "");
    }

    //公钥信息
    public static final String Key_PublicKeyInfo = "PublicKeyInfo";

    public static final void setPublicKeyInfo(String ipvalue) {
        sharedPreferences.edit().putString(Key_PublicKeyInfo, ipvalue).commit();
    }

    public static final String getPublicKeyInfo() {
        return getString(Key_PublicKeyInfo, "");
    }

    public static final void setIP(String ipvalue) {
        sharedPreferences.edit().putString(Key_IP, ipvalue).commit();
    }

    public static final String getIP() {
        return getString(Key_IP, "");
    }

    public static final void setPort(int portvalue) {
        sharedPreferences.edit().putInt(Key_Port, portvalue).commit();
    }

    public static final int getPort() {
        return getInt(Key_Port, 0);
    }

    public static final void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }


    public static final String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public static final void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public static final int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public static final void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).commit();
    }

    public static final long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public static final void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static final boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public static final void putSerialNo(int serialNo) {
        sharedPreferences.edit().putInt(Key_SerialNo, serialNo).commit();
    }

    public static final int getSerialNo() {
        int serNo = sharedPreferences.getInt(Key_SerialNo, 1);
        if (serNo >= 1000000)
            putSerialNo(1);
        if (serNo == 0)
            putSerialNo(1);
        return sharedPreferences.getInt(Key_SerialNo, 111);
    }

    public static final String getSerialNoForHttp() {
        String str = "0000000000000000";
        putSerialNo(getSerialNo() + 1);

        String no = String.format("%016d", getSerialNo());
        Log.i("", "getSerialNoForHttp: " + no);
        return no;
    }

    public static final void putBatchNo(String batchNo) {
        sharedPreferences.edit().putString(Key_BatchNo, batchNo).commit();
    }

    public static final String getBatchNo() {
        return sharedPreferences.getString(Key_BatchNo, "0000001");
    }

    public static final void putMerchantNo(String merchantNo) {
        sharedPreferences.edit().putString(Key_MerchantNo, merchantNo).apply();
    }

    public static final String getMerchantNo() {
        return sharedPreferences.getString(Key_MerchantNo, null);
    }

    public static final   void putTerminalNo(String terminalNo) {
        sharedPreferences.edit().putString(Key_TerminalNo, terminalNo).apply();
    }

    public static  final String getTerminalNo() {
        return sharedPreferences.getString(Key_TerminalNo, null);
    }



    public static void putTerminalPayload(String terminalNo) {
        sharedPreferences.edit().putString(Key_TerminalPay, terminalNo).apply();
    }

    public static String getTerminalPayload() {
        return sharedPreferences.getString(Key_TerminalPay, null);
    }



    public static final void putMerchantName(String merchantName) {
        sharedPreferences.edit().putString(Key_MerchantName, merchantName).apply();
    }

    public static final String getMerchantName() {
        return sharedPreferences.getString(Key_MerchantName, null);
    }

    public static final boolean put(String merchantNo, String terminalNo) {
        return sharedPreferences.edit().putString(Key_MerchantNo, merchantNo)
                .putString(Key_TerminalNo, terminalNo).commit();
    }



    public static final boolean putScript(String str) {
        return sharedPreferences.edit().putString(Key_Script, str).commit();
    }

    public static final String getScript() {
        String str = sharedPreferences.getString(Key_Script, null);
        if (!TextUtils.isEmpty(str)) {
            return str;
        }
        return null;
    }


    public static final boolean putInstiNo(String instiNo) {
        return sharedPreferences.edit().putString("instiNo", instiNo).commit();
    }

    public static final String getInstiNo() {
        return sharedPreferences.getString("instiNo", null);
    }

    private static SharedPreferences sharedPreferences;

    public static final SharedPreferences getSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = MainApplication.getAppContext().getSharedPreferences("qpos.ccbbank_preferences", Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
//            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }
        return sharedPreferences;
    }

    /**
     * 获取指定位权限
     *
     * @param location 从1开始
     * @return 权限标识 0为关 1为开
     */
    public static boolean getAppointLocationManagerLimit(int location) {
        String managerLimitString = sharedPreferences.getString("managerLimitString", "11111111111111111111111111111111111111111111111111");

        return String.valueOf(managerLimitString.charAt(location - 1)).equals("1");
    }

    public static String getManagerLimitString() {
        String managerLimitString = sharedPreferences.getString("managerLimitString", null);
        if (managerLimitString == null) {
            managerLimitString = "11111111111111111111111111111111111111111111111111";
            sharedPreferences.edit().putString("managerLimitString", managerLimitString).apply();
        }

        return managerLimitString;
    }

    public static void setManagerLimitString(String managerLimitString) {
        sharedPreferences.edit().putString("managerLimitString", managerLimitString).apply();
    }

    /**
     * 设置指定位权限
     *
     * @param location 从1开始
     */
    public static void setAppointLocationManagerLimit(int location, boolean limitFlag) {
        String managerLimitString = sharedPreferences.getString("managerLimitString", "11111111111111111111111111111111111111111111111111");

        StringBuilder stringBuilder = new StringBuilder(managerLimitString);
        stringBuilder.replace(location - 1, location, limitFlag ? "1" : "0");

        sharedPreferences.edit().putString("managerLimitString", stringBuilder.toString()).apply();
    }


    public static int getOverTimeInt() {
        return sharedPreferences.getInt(Key_OverTime, 60);
    }

    public static void setOverTimeInt(int overTime) {
        sharedPreferences.edit().putInt(Key_OverTime, overTime).apply();
    }

    public static int getChongZhengRepeatSend() {
        return sharedPreferences.getInt(Key_ChongZhengRepeatSend, 3);
    }

    public static void setChongZhengRepeatSend(int chongZhengRepeatSend) {
        sharedPreferences.edit().putInt(Key_ChongZhengRepeatSend, chongZhengRepeatSend).apply();
    }

    public static Boolean getDoSignRepeatSend() {
        return sharedPreferences.getBoolean(Key_DoSignRepeatSend, true);
    }

    public static void setDoSignRepeatSend(boolean doSignRepeatSend) {
        sharedPreferences.edit().putBoolean(Key_DoSignRepeatSend, doSignRepeatSend).apply();
    }

    public static int getMaxTradeNum() {
        return sharedPreferences.getInt(Key_MaxTradeNum, 9999);
    }

    public static void setMaxTradeNum(int maxTradeNum) {
        sharedPreferences.edit().putInt(Key_MaxTradeNum, maxTradeNum).apply();
    }

    public static String getLastSettle() {
        return sharedPreferences.getString("lastSettle", null);
    }

    public static void setLastSettle(String lastSettle) {
        sharedPreferences.edit().putString("lastSettle", lastSettle).apply();
    }

    public static boolean getConsume_Control() {
        return sharedPreferences.getBoolean("Consume_Control", true);
    }

    public static void setConsume_Control(boolean Consume_Control) {
        sharedPreferences.edit().putBoolean("Consume_Control", Consume_Control).apply();
    }

    public static boolean getConsumeCancel_Control() {
        return sharedPreferences.getBoolean("ConsumeCancel_Control", true);
    }

    public static void setConsumeCancel_Control(boolean ConsumeCancel_Control) {
        sharedPreferences.edit().putBoolean("ConsumeCancel_Control", ConsumeCancel_Control).apply();
    }

    public static boolean getRetfund_Control() {
        return sharedPreferences.getBoolean("Retfund_Control", true);
    }

    public static void setRetfund_Control(boolean Retfund_Control) {
        sharedPreferences.edit().putBoolean("Retfund_Control", Retfund_Control).apply();
    }

    public static boolean getBalanceInquiry_Control() {
        return sharedPreferences.getBoolean("BalanceInquiry_Control", true);
    }

    public static void setBalanceInquiry_Control(boolean BalanceInquiry_Control) {
        sharedPreferences.edit().putBoolean("BalanceInquiry_Control", BalanceInquiry_Control).apply();
    }

    public static boolean getPreCancel_Control() {
        return sharedPreferences.getBoolean("PreCancel_Control", true);
    }

    public static void setPreCancel_Control(boolean PreCancel_Control) {
        sharedPreferences.edit().putBoolean("PreCancel_Control", PreCancel_Control).apply();
    }

    public static boolean getPreComplete_Control() {
        return sharedPreferences.getBoolean("PreComplete_Control", true);
    }

    public static void setPreComplete_Control(boolean PreComplete_Control) {
        sharedPreferences.edit().putBoolean("PreComplete_Control", PreComplete_Control).apply();
    }

    public static boolean getPre_Control() {
        return sharedPreferences.getBoolean("Pre_Control", true);
    }

    public static void setPre_Control(boolean Pre_Control) {
        sharedPreferences.edit().putBoolean("Pre_Control", Pre_Control).apply();
    }

    public static boolean getPreCompleteCancel_Control() {
        return sharedPreferences.getBoolean("PreCompleteCancel_Control", true);
    }

    public static void setPreCompleteCancel_Control(boolean PreCompleteCancel_Control) {
        sharedPreferences.edit().putBoolean("PreCompleteCancel_Control", PreCompleteCancel_Control).apply();
    }

    public static boolean getUnionPayQRCode_Control() {
        return sharedPreferences.getBoolean("UnionPayQRCode_Control", true);
    }

    public static void setUnionPayQRCode_Control(boolean UnionPayQRCode_Control) {
        sharedPreferences.edit().putBoolean("UnionPayQRCode_Control", UnionPayQRCode_Control).commit();
    }

    public static boolean getFullMess_isencry() {
        return sharedPreferences.getBoolean("FullMess_isencry", false);
    }

    public static void setFullMess_isencry(boolean FullMess_isencry) {
        sharedPreferences.edit().putBoolean("FullMess_isencry", FullMess_isencry).commit();
    }


    public static boolean getPreCompleteCancel_IsSwipCard() {
        return sharedPreferences.getBoolean("PreCompleteCancel_IsSwipCard", true);
    }

    public static void setPreCompleteCancel_IsSwipCard(boolean PreCompleteCancel_IsSwipCard) {
        sharedPreferences.edit().putBoolean("PreCompleteCancel_IsSwipCard", PreCompleteCancel_IsSwipCard).commit();
    }

    public static boolean getConsumeCancel_IsSwipCard() {
        return sharedPreferences.getBoolean("ConsumeCancel_IsSwipCard", true);
    }

    public static void setConsumeCancel_IsSwipCard(boolean ConsumeCancel_IsSwipCard) {
        sharedPreferences.edit().putBoolean("ConsumeCancel_IsSwipCard", ConsumeCancel_IsSwipCard).commit();
    }

    public static boolean getSettlement_IsAutoSignout() {
        return sharedPreferences.getBoolean("Settlement_IsAutoSignout", true);
    }

    public static void setSettlement_IsAutoSignout(boolean Settlement_IsAutoSignout) {
        sharedPreferences.edit().putBoolean("Settlement_IsAutoSignout", Settlement_IsAutoSignout).commit();
    }

    public static boolean getDirector_IsPwd() {
        return sharedPreferences.getBoolean("Director_IsPwd", true);
    }

    public static void setDirector_IsPwd(boolean Director_IsPwd) {
        sharedPreferences.edit().putBoolean("Director_IsPwd", Director_IsPwd).commit();
    }

    public static boolean getHandInput_IsPwd() {
        return sharedPreferences.getBoolean("HandInput_IsPwd", true);
    }

    public static void setHandInput_IsPwd(boolean HandInput_IsPwd) {
        sharedPreferences.edit().putBoolean("HandInput_IsPwd", HandInput_IsPwd).commit();
    }

    public static boolean getTips_IsSupport() {
        return sharedPreferences.getBoolean("Tips_IsSupport", true);
    }

    public static void setTips_IsSupport(boolean Tips_IsSupport) {
        sharedPreferences.edit().putBoolean("Tips_IsSupport", Tips_IsSupport).commit();
    }


    public static boolean getConsumeCancel_IsPwd() {
        return sharedPreferences.getBoolean("ConsumeCancel_IsPwd", true);
    }

    public static void setConsumeCancel_IsPwd(boolean ConsumeCancel_IsPwd) {
        sharedPreferences.edit().putBoolean("ConsumeCancel_IsPwd", ConsumeCancel_IsPwd).commit();
    }

    public static boolean getPreCancel_IsPwd() {
        return sharedPreferences.getBoolean("PreCancel_IsPwd", true);
    }

    public static void setPreCancel_IsPwd(boolean PreCancel_IsPwd) {
        sharedPreferences.edit().putBoolean("PreCancel_IsPwd", PreCancel_IsPwd).commit();
    }

    public static boolean getPreCompleteCancel_IsPwd() {
        return sharedPreferences.getBoolean("PreCompleteCancel_IsPwd", true);
    }

    public static void setPreCompleteCancel_IsPwd(boolean PreCompleteCancel_IsPwd) {
        sharedPreferences.edit().putBoolean("PreCompleteCancel_IsPwd", PreCompleteCancel_IsPwd).apply();
    }

    public static boolean getConsumeComleteRequest_Ispwd() {
        return sharedPreferences.getBoolean("ConsumeComleteRequest_Ispwd", true);
    }

    public static void setConsumeComleteRequest_Ispwd(boolean ConsumeComleteRequest_Ispwd) {
        sharedPreferences.edit().putBoolean("ConsumeComleteRequest_Ispwd", ConsumeComleteRequest_Ispwd).apply();
    }

    //Print_Pieces
    public static int getPrint_Pieces() {
        return sharedPreferences.getInt("Print_Pieces", 1);
    }

    public static void setPrint_Pieces(int Print_Pieces) {
        sharedPreferences.edit().putInt("Print_Pieces", Print_Pieces).apply();
    }

    public static boolean getTitle_IsLogo() {
        return sharedPreferences.getBoolean("Title_IsLogo", true);
    }

    public static void setTitle_IsLogo(boolean Title_IsLogo) {
        sharedPreferences.edit().putBoolean("Title_IsLogo", Title_IsLogo).apply();
    }

    //Print_Control

    public static boolean getPrint_Control() {
        return sharedPreferences.getBoolean("Print_Control", true);
    }

    public static void setPrint_Control(boolean Print_Control) {
        sharedPreferences.edit().putBoolean("Print_Control", Print_Control).apply();
    }

    //SignDataStr
    public static String getSignDataStr() {
        return sharedPreferences.getString("SignDataStr", null);
    }

    public static void setSignDataStr(String SignDataStr) {
        sharedPreferences.edit().putString("SignDataStr", SignDataStr).apply();
    }

    //Print_Repeat
    public static boolean getPrint_Repeat() {
        return sharedPreferences.getBoolean("Print_Repeat", false);
    }

    public static void setPrint_Repeat(boolean Print_Repeat) {
        sharedPreferences.edit().putBoolean("Print_Repeat", Print_Repeat).apply();
    }

    //Electric_Signatur
    public static boolean getElectric_Signatur() {
        return sharedPreferences.getBoolean("Electric_Signatur", true);
    }

    public static void setElectric_Signatur(boolean Electric_Signatur) {
        sharedPreferences.edit().putBoolean("Electric_Signatur", Electric_Signatur).apply();
    }

    //key1
    public static String getkey1() {
        return sharedPreferences.getString("key1", "");
    }

    public static void setkey1(String key1) {
        sharedPreferences.edit().putString("key1", key1).apply();
    }

    //Random
    public static String getRandom() {
        return sharedPreferences.getString("Random", "");
    }

    public static void setRandom(String Random) {
        sharedPreferences.edit().putString("Random", Random).apply();
    }

    //activation
    public static boolean getActivation() {
        return sharedPreferences.getBoolean("activation", true);
    }

    public static void setActivation(boolean activation) {
        sharedPreferences.edit().putBoolean("activation", activation).apply();
    }

    // relogin = 3;
    public static int getRelogin() {
        return sharedPreferences.getInt("relogin", 3);
    }

    public static void setRelogin(int relogin) {
        sharedPreferences.edit().putInt("relogin", relogin).apply();
    }

    // rechongzheng
    public static int getRechongzheng() {
        return sharedPreferences.getInt("rechongzheng", 3);
    }

    public static void setRechongzheng(int rechongzheng) {
        sharedPreferences.edit().putInt("rechongzheng", rechongzheng).apply();
    }

    //tradetimeout
    public static int getTradetimeout() {
        return sharedPreferences.getInt("tradetimeout", 60);
    }

    public static void setTradetimeout(int tradetimeout) {
        sharedPreferences.edit().putInt("tradetimeout", tradetimeout).apply();
    }

    //IsLock
    public static boolean getIsLock() {
        return sharedPreferences.getBoolean("IsLock", false);//默认为锁定
    }

    public static void setIsLock(boolean IsLock) {
        sharedPreferences.edit().putBoolean("IsLock", IsLock).apply();
    }

    public static String getCardHolderName() {
        return sharedPreferences.getString("cardHolderName", "");
    }

    public static void setCardHolderName(String cardHolderName) {
        sharedPreferences.edit().putString("cardHolderName", cardHolderName).apply();
    }


    public static String getReferenceNumber() {
        return sharedPreferences.getString("refNumber", "");
    }

    public static void setRefNumber(String refNumber) {
        sharedPreferences.edit().putString("refNumber", refNumber).apply();
    }

    public static String getDe38() {
        return sharedPreferences.getString("de38", "");
    }

    public static void setD38(String de38) {
        sharedPreferences.edit().putString("de38", de38).apply();
    }


    public static String getTxnDate() {
        return sharedPreferences.getString("txnDate", "");
    }

    public static void setTxnDate(String txnDate) {
        sharedPreferences.edit().putString("txnDate", txnDate).apply();
    }

    public static String getExpDate() {
        return sharedPreferences.getString("expDate", "");
    }

    public static void setExpDate(String txnDate) {
        sharedPreferences.edit().putString("expDate", txnDate).apply();
    }



    public static String getType() {
        return sharedPreferences.getString("txnType", "");
    }

    public static void setTxnType(String txnType) {
        sharedPreferences.edit().putString("txnType", txnType).apply();
    }


    public static String getEmvPayload() {
        return sharedPreferences.getString("payLoad", "");
    }

    public static void setEmvPayload(String payLoad) {
        sharedPreferences.edit().putString("payLoad", payLoad).apply();
    }


    public static String getAmt() {
        return sharedPreferences.getString("amt", "");
    }

    public static void setAmt(String payLoad) {
        sharedPreferences.edit().putString("amt", payLoad).apply();
    }

    public static String getSCode() {
        return sharedPreferences.getString("sCode", "");
    }

    public static void setSCode(String payLoad) {
        sharedPreferences.edit().putString("sCode", payLoad).apply();
    }

    public static int getCount() {
        return sharedPreferences.getInt("count", 0);
    }

    public static void setCount(int payLoad) {
        sharedPreferences.edit().putInt("count", payLoad).apply();
    }


    public static String getStan() {
        return sharedPreferences.getString("stan", "0");
    }

    public static void setStan(String stan) {
        sharedPreferences.edit().putString("stan", stan).apply();
    }

    public static String gett91() {
        return sharedPreferences.getString("d91", "0");
    }

    public static void sett91(String stan) {
        sharedPreferences.edit().putString("d91", stan).apply();
    }

    public static String gettBin() {
        return sharedPreferences.getString("bin", "0");
    }

    public static void setBin(String stan) {
        sharedPreferences.edit().putString("bin", stan).apply();
    }


    public static String getEmv() {
        return sharedPreferences.getString("epload", "0");
    }

    public static void setEmv(String stan) {
        sharedPreferences.edit().putString("epload", stan).apply();
    }

    public static void setIsDailogShown(boolean isDailogShown) {
        sharedPreferences.edit().putBoolean("isDailogShown", isDailogShown).apply();
    }

    public static boolean getIsDailogShown(Context context) {
        return sharedPreferences.getBoolean("isDailogShown", false);

    }


    public static void setCrrn(String crnn){
        PrefUtil.setRefNumber(crnn);
        sharedPreferences.edit().putString("crnn", crnn).apply();
    }

    public static String getCrrn(){
        return sharedPreferences.getString("crnn", "");
    }


    public static void setAmount(String amount){
        sharedPreferences.edit().putString("amount", amount).apply();
    }

    public static String getAmount(){
        return sharedPreferences.getString("amount", "");
    }

    public static void setdisAmount(String amount){
        sharedPreferences.edit().putString("disAmount", amount).apply();
    }

    public static String getdisAmount(){
        return sharedPreferences.getString("disAmount", "0");
    }

    public static void setpaidAmount(String amount){
        sharedPreferences.edit().putString("paidAmount", amount).apply();
    }

    public static String getpaidAmount(){
        return sharedPreferences.getString("paidAmount", "0");
    }



    public static void setTMK(String tmk){
        sharedPreferences.edit().putString("tmk", tmk).apply();
    }

    public static String getTMK(){
        return sharedPreferences.getString("tmk", "0");
    }



    public static void setAppId(String tmk){
        sharedPreferences.edit().putString("appId", tmk).apply();
    }

    public static String getAppId(){
        return sharedPreferences.getString("appId", "0");
    }



    public static void setCardNo(String tmk){
        sharedPreferences.edit().putString("card_no", tmk).apply(); }


    public static String getCardNo(){
        return sharedPreferences.getString("card_no", "0");
    }



    public static void setLauncherLog(String tmk){
        sharedPreferences.edit().putString("log_launch", tmk).apply();
    }

    public static String getLauncherLog(){
        return sharedPreferences.getString("log_launch", "0");
    }



    public static void setCheckVal(String tmk){
        sharedPreferences.edit().putString("check_val", tmk).apply();
    }

    public static  String getCheckVal(){
        return sharedPreferences.getString("check_val", "0");
    }

}
