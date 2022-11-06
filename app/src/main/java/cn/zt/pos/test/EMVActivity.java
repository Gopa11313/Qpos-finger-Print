package cn.zt.pos.test;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.szzt.sdk.device.Constants;
import com.szzt.sdk.device.card.ContactlessCardReader;
import com.szzt.sdk.device.card.SmartCardReader;
import com.szzt.sdk.device.emv.EMV_STATUS;
import com.szzt.sdk.device.emv.EmvInterface;
import com.szzt.sdk.device.pinpad.PinPad;
import com.szzt.sdk.device.pinpad.PinPad.OnSendKeyListerner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.db.TellersDbHelper;
import cn.zt.pos.db.obj.Tracemodule;
import cn.zt.pos.inf.ResponseInterface;
import cn.zt.pos.soapAPI.chip_quickpass.PostEmvTxnPinlessAPI;
import cn.zt.pos.soapAPI.chip_quickpass.PostEmvTxnWithPinAPI;
import cn.zt.pos.soapAPI.chip_quickpass.PreAuthCancelletionEmvAPI;
import cn.zt.pos.soapAPI.chip_quickpass.PreAuthEmv;
import cn.zt.pos.soapAPI.chip_quickpass.PreAuthEmvComp;
import cn.zt.pos.soapAPI.chip_quickpass.ReverseTxnEmvAPI;
import cn.zt.pos.soapAPI.chip_quickpass.VoidTxnE;
import cn.zt.pos.utils.HexDump;
import cn.zt.pos.utils.MessageManager;
import cn.zt.pos.utils.PrefUtil;
import cn.zt.pos.utils.Shower;
import cn.zt.pos.utils.StringUtil;
import cn.zt.pos.utils.StringUtility;
import cn.zt.upi.TxnType;

public class EMVActivity extends BaseActivity {
    private static final int MSG_WAIT_FOR_CARD = 0;
    private MessageManager messageManager;
    private EmvInterface emvInterface;
    private PinPad pinPad;
    private Button mClearButton;
    private Button mEmvStart;
    private Button mEmvClose;
    private Button mEmvElectronic;
    private Button mElectronicCashInquiry;
    private Button mEmvSetParam;
    private HandlerThread mWaitForCardHandlerThread;
    volatile boolean mIsWaitingForCard = false;
    volatile boolean mHasCard = false;
    private MyHandler myHandler;
    private static ArrayList<byte[]> mAIDParams;
    private static ArrayList<byte[]> mCAPKParams;
    private static ArrayList<String> mAIDParamInString;
    private static ArrayList<String> mCAPKParamInString;
    private static String mTermInfo;
    private Shower shower;
    private byte mTransType = EmvInterface.TRANS_GOODS_SERVICE;
    private static final int EMV_WAIT_CARD = 113;
    private static final int EMV_ELECTRO_CASH_WAIT_CARD = 114;
    private static final int EMV_ELECT_TRANS_WAIT_CARD = 115;
    private long amount = 1000000;
    String TAG = "dr";
    String PIN="";
    Context context;


    private  TellersDbHelper tellersDbHelper;

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message m) {
            int what = m.what;
            if (what == EmvInterface.EMV_PROCESS_MSG) {
                int status = m.arg1;
                int retcode = m.arg2;
                EmvTest(status, retcode);
            } else if (what == EmvInterface.EMV_CARD_MSG) {
                int removed = m.arg1;
                if (removed == EmvInterface.CARD_REMOVED) {

                }
            } else if (what == MSG_WAIT_FOR_CARD) {
            } else if (what == EMV_WAIT_CARD) {
                messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_emv_wait_card),
                        Color.GREEN);
            } else if (what == EMV_ELECTRO_CASH_WAIT_CARD) {
                messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.info_get_electro_cash),
                        Color.GREEN);
            } else if (what == EMV_ELECT_TRANS_WAIT_CARD) {
                messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.info_emv_elect_trans_wait_card),
                        Color.GREEN);
            }
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    //接触卡 isEmvInit:true 电子现金 ; false:联机交易
    private void smart_emv(boolean isEmvInit) {
        if (!mIsWaitingForCard) {
            mIsWaitingForCard = true;
            int result = smartCardReader.waitForCard(0, Constants.WAIT_INFINITE);
            if (result < 0) {
                messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.info_card_contact) + shower.getString(R.string.msg_card_read_error));
            } else {
                contactlessCardReader.close();
                messageManager.AppendInfoMessageInUiThread("Contact card");
                byte[] data = new byte[256];
                int ret = smartCardReader.powerOn(0, data);
                if (ret > 0) {
                    if (!isEmvInit) {
                        smart();
                    } else {
                        EmvInit(data, EmvInterface.READER_TYPE_CONTACT_CARD);
                    }
                }
            }
            mIsWaitingForCard = false;
        }
    }

    private void smart() {
        emvInterface.setForceOnline(1);
        emvInterface.initialize(myHandler);
        byte[] term = HexDump.hexStringToByteArray("9F660436800080");
        emvInterface.setTerminalParam(term);
        emvInterface.preprocess(1);
        emvInterface.setCardType(EmvInterface.READER_TYPE_CONTACT_CARD);
        emvInterface.setTransAmount(amount);
        emvInterface.setTransType(EmvInterface.TRANS_GOODS_SERVICE);
        emvInterface.process();
    }

    private void EmvInit(byte[] atr, int result) {
        emvInterface.initialize(myHandler);
        emvInterface.preprocess(0);
        emvInterface.setTransAmount(amount);
        emvInterface.setCardType(result);
        emvInterface.setTransType(mTransType);
        emvInterface.process();
    }

    //非接卡 isEmvInit：false电子现金 ；true:联机交易
    volatile boolean mIsWaitingForCard2 = false;

    private void contact_emv(boolean isEmvInit) {
        if (!mIsWaitingForCard2) {
            mIsWaitingForCard2 = true;
            int result = contactlessCardReader.waitForCard(Constants.WAIT_INFINITE);
            if (result < 0) {
                messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.info_card_contactless) + shower.getString(R.string.msg_card_read_error));

            } else {
                smartCardReader.close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        messageManager.AppendInfoMessage("Contactless card");
                    }
                });
                byte[] data = new byte[256];
                int ret = contactlessCardReader.powerOn(data);
                if (ret > 0) {
                    if (!isEmvInit) {
                        emvInterface.setForceOnline(1);
                        emvInterface.initialize(myHandler);
                        byte[] term = HexDump.hexStringToByteArray("9F660436800080");
                        emvInterface.setTerminalParam(term);
                        emvInterface.preprocess(1);
                        emvInterface.setCardType(EmvInterface.READER_TYPE_CONTACTLESS_CARD);
                        emvInterface.setTransAmount(amount);
                        emvInterface.setTransType(EmvInterface.TRANS_GOODS_SERVICE);
                        emvInterface.process();
                    } else {
                        EmvInit(data, EmvInterface.READER_TYPE_CONTACTLESS_CARD);
                    }
                }
            }
            mIsWaitingForCard2 = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_emv);
        context=this;
        shower = new Shower(this);
        boolean isConnected = mainApplication.isDeviceManagerConnetcted();
        init(isConnected);
        loadEmvParam();
//        onlinePinPad();

         tellersDbHelper = new TellersDbHelper(EMVActivity.this);

    }

    @Override
    public void onResume() {
        messageManager.AppendInfoMessage(shower.getString(R.string.test_emv));
        if (mWaitForCardHandlerThread == null) {
            mWaitForCardHandlerThread = new HandlerThread("wait_for_card");
        }
        mWaitForCardHandlerThread.start();
        myHandler = new MyHandler(mWaitForCardHandlerThread.getLooper());
        super.onResume();
    }

    @Override
    public void onDestroy() {
        emvInterface.processExit();
        myHandler.removeMessages(MSG_WAIT_FOR_CARD);
        myHandler.getLooper().quit();
        myHandler = null;
        mWaitForCardHandlerThread = null;
        mIsWaitingForCard = false;
        mIsWaitingForCard2 = false;
        pinPad.close();
        super.onDestroy();
    }

    private SmartCardReader smartCardReader = null;
    private ContactlessCardReader contactlessCardReader = null;

    private void init(boolean isConnected) {
        if (messageManager == null) {
            TextView text = (TextView) findViewById(R.id.emv_text);
            text.setMovementMethod(ScrollingMovementMethod.getInstance());
            messageManager = new MessageManager(this, text);
            mClearButton = (Button) findViewById(R.id.emv_clear);
            mClearButton.setOnClickListener(listener);
            mEmvStart = (Button) findViewById(R.id.emv_start);
            mEmvStart.setOnClickListener(listener);
            mEmvClose = (Button) findViewById(R.id.emv_stop);
            mEmvClose.setOnClickListener(listener);
            mEmvElectronic = (Button) findViewById(R.id.emv_electronic_cash);
            mEmvElectronic.setOnClickListener(listener);
            mElectronicCashInquiry = (Button) findViewById(R.id.emv_electronic_cash_inquiry);
            mElectronicCashInquiry.setOnClickListener(listener);
            mEmvSetParam = (Button) findViewById(R.id.emv_set_param);
            mEmvSetParam.setOnClickListener(listener);
            emvInterface = mainApplication.getEmvInterface();
            smartCardReader = mainApplication.getSmartCardReader();
            contactlessCardReader = mainApplication.getContactlessCardReader();
            pinPad = mainApplication.getPinPad();
        }
        mClearButton.setEnabled(isConnected);
        mEmvStart.setEnabled(isConnected);
        mEmvClose.setEnabled(isConnected);


    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.emv_clear:
                    messageManager.clear();
                    break;
                //联机交易Online transaction
                case R.id.emv_start:
                    myHandler.sendEmptyMessage(EMV_WAIT_CARD);
                    mTransType = EmvInterface.TRANS_GOODS_SERVICE;
                    openCardRead(true);
                    break;
                //电子现金查询Electronic cash inquiry
                case R.id.emv_electronic_cash_inquiry:
                    myHandler.sendEmptyMessage(EMV_ELECTRO_CASH_WAIT_CARD);
                    mTransType = (byte) 0xF4;
                    openCardRead(true);
                    break;
                //电子现金交易Electronic cash transaction
                case R.id.emv_electronic_cash:
                    myHandler.sendEmptyMessage(EMV_ELECT_TRANS_WAIT_CARD);
                    mTransType = EmvInterface.TRANS_GOODS_SERVICE;
                    openCardRead(true);
                    break;
                //设置参数Setting parameters
                case R.id.emv_set_param:
                    emvSetParam();
                    break;
                //停止测试Stop testing
                case R.id.emv_stop:
                    closeReader();
                    break;
                default:
                    break;
            }
        }
    };

    //非接卡、接触卡Non-contact card, contact card
    private void openCardRead(final boolean isEmvInit) {
        int res = smartCardReader.open(0, new SmartCardReader.SCReaderListener() {

            @Override
            public void notify(int nSlotIndex, int nEvent) {

            }
        });
        if (res >= 0) {
            new Thread() {
                public void run() {
                    smart_emv(isEmvInit);
                }
            }.start();
        }

        int res2 = contactlessCardReader.open();
        if (res2 >= 0) {
            new Thread() {
                public void run() {
                    contact_emv(isEmvInit);
                }
            }.start();
        }
    }

    //设置参数Setting parameters
    private void emvSetParam() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                loadEmvParam();
                setEmvParam();

//                myHandler.sendEmptyMessage(EMV_WAIT_CARD);
//                mTransType = EmvInterface.TRANS_GOODS_SERVICE;
//                openCardRead(false);

            }
        }).start();
    }

    private void loadEmvParam() {
        if (mAIDParams == null) {
            mAIDParams = new ArrayList<byte[]>();
            mCAPKParams = new ArrayList<byte[]>();
            mAIDParamInString = new ArrayList<String>();
            mCAPKParamInString = new ArrayList<String>();
        }
        if (mAIDParams.size() != 0 || mCAPKParams.size() != 0) {
            return;
        }
        try {
            InputStreamReader inputReader = new InputStreamReader(
                    getResources().openRawResource(R.raw.params));
            BufferedReader bufferedReader = new BufferedReader(inputReader);
            ArrayList<byte[]> activeList = null;
            ArrayList<String> stringList = null;
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null)
                    break;
                if (line.startsWith("AID=")) {
                    activeList = mAIDParams;
                    stringList = mAIDParamInString;
                    continue;
                } else if (line.startsWith("CAPK=")) {
                    activeList = mCAPKParams;
                    stringList = mCAPKParamInString;
                    continue;
                } else if (line.startsWith("TERM=")) {
                    line = bufferedReader.readLine();
                    mTermInfo = line;
                    continue;
                }
                stringList.add(line);
                byte[] data = StringUtil.hexString2bytes(line);
                activeList.add(data);
            }
            bufferedReader.close();
            inputReader.close();
        } catch (Exception e) {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_emv_load_para_error) + e.getMessage());
        }
    }

    private void setEmvParam() {
        int index = 0;
        for (byte[] param : mAIDParams) {
            int nret = emvInterface.updateAidParam(1, param);
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_emv_init_aid) + (index)
                    + "]=" + mAIDParamInString.get(index++) + " = " + nret);
        }
        index = 0;
        for (byte[] param : mCAPKParams) {
            int nret = emvInterface.updateCAPKParam(1, param);
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_emv_init_capk) + (index)
                    + "]=" + mCAPKParamInString.get(index++) + " = " + nret);
        }
        if (mTermInfo != null) {
            int nret = emvInterface.setTerminalParam(StringUtil.hexString2bytes(mTermInfo));
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_emv_init_teminal) + nret);
        }
    }

    private void EmvTest(int status, int retcode) {
        Log.e("EmvTest", "status:" + status + " retcode:" + retcode);
        if (status == EmvInterface.STATUS_CONTINUE) {
            switch (retcode) {
                //请求选择候选应用列表
                case EMV_STATUS.EMV_REQ_SEL_CANDIDATES:
                    //请求再次选择候选列表
                case EMV_STATUS.EMV_REQ_SEL_CANDIDATES_AGAIN:
                    byte[] candidateList = new byte[300];
                    int length = emvInterface.getCardCandidateList(candidateList);
                    if (length <= 0) {
                        emvInterface.process();
                        return;
                    }
                    int nret = emvInterface.setCardCandidateListResult(0);
                    messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_emv_sum_up) + length +
                            shower.getString(R.string.msg_emv_apps) + shower.getString(R.string.msg_emv_chose_app)
                            + nret);
                    emvInterface.process();
                    break;
                //请求持卡人输入联机pin
                case EMV_STATUS.EMV_REQ_ONLINE_PIN:
                    //输入联机pin
                    onlinePinPad();
                    Log.d("pinpad","pinpad");
                    break;
                //请求联机交易
                case EMV_STATUS.EMV_REQ_GO_ONLINE:
                    byte[] data = new byte[256];
                    byte[] recvFiled55 = new byte[256];
                    int index = 0;
                    int[] tag = new int[]{0x82, 0x84, 0x95, 0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x9A, 0x9C, 0x9F02, 0x5F2A, 0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x9F09, 0x9F41};
                    for (int i = 0; i < tag.length; i++) {
                        int len = emvInterface.getTagData(tag[i], data);
                        if (len > 0) {
                            Log.e("tag", tag[i] + "-->" + StringUtility.ByteArrayToString(data, len));
                            int tagIndex;
                            if (tag[i] > 0xFF) {
                                Log.e("status", "status-->" + (tag[i] >> 8) + "-->" + (tag[i] << 8));
                                tagIndex = recvFiled55[index++] = (byte) (tag[i] / 0xff);
                            }
                            recvFiled55[index++] = (byte) (tag[i] & 0xff);
                            recvFiled55[index++] = (byte) len;
                            System.arraycopy(data, 0, recvFiled55, index, len);
                            index += len;
                        }
                    }
                  final   byte[] filed55 = new byte[index];
                    Log.d(TAG, "filed55: " + StringUtility.getStringFormat(filed55));


                    byte[] tagData = new byte[100];

                    int tag5F24Length = emvInterface.getTagData(0x5F24, tagData);
                    String tag5F24 = "0" + tag5F24Length + StringUtility.getStringFormat(tagData, tag5F24Length);
                    Log.d(TAG, "tag5F24: " + tag5F24 + " length :" + tag5F24Length);

                    int tag9F03Length = emvInterface.getTagData(0x9F03, tagData);
                    String tag9F03 = "0" + tag9F03Length + StringUtility.getStringFormat(tagData, tag9F03Length);
                    Log.d(TAG, "tag9F03: " + tag5F24 + " length :" + tag9F03Length);


                    int tag95Length = emvInterface.getTagData(0x95, tagData);
                    String tag95 = "0" + tag95Length + StringUtility.getStringFormat(tagData, tag95Length);

                    int tag9F37Length = emvInterface.getTagData(0x9F37, tagData);
                    String tag9F37 = "0" + tag9F37Length + StringUtility.getStringFormat(tagData, tag9F37Length);

                    int tag5F2ALength = emvInterface.getTagData(0x5F2A, tagData);
                    String tag5F2A = "0" + tag5F2ALength + StringUtility.getStringFormat(tagData, tag5F2ALength);

                    int tag9CLength = emvInterface.getTagData(0x9C, tagData);
                    String tag9C = "0" + tag9CLength + StringUtility.getStringFormat(tagData, tag9CLength);

                    int tag9F10Length = emvInterface.getTagData(0x9F10, tagData);
//                    String tag9F10 = "0" + tag9F10Length + StringUtility.getStringFormat(tagData, tag9F10Length);
                    Log.d(TAG, "tag9F10: "+StringUtility.getStringFormat(tagData, tag9F10Length)+" length :"+tag9F10Length);
                    String tag9F10;
                    if (tag9F10Length >= 8) {

                        tag9F10 = "08" + StringUtility.getStringFormat(tagData, tag9F10Length).replace(" ","").trim().substring(0, 16);
                        Log.d(TAG, "EmvTest: "+tag9F10);

                    } else {
                        tag9F10 = "0" + tag9F10Length + StringUtility.getStringFormat(tagData, tag9F10Length);
                        Log.d(TAG, "EmvTest: "+tag9F10);
                    }
                    int tag9F33Length = emvInterface.getTagData(0x9F33, tagData);
                    String tag9F33 = "0" + tag9F33Length + StringUtility.getStringFormat(tagData, tag9F33Length);

                    int tag9ALength = emvInterface.getTagData(0x9A, tagData);
                    String tag9A = "0" + tag9ALength + StringUtility.getStringFormat(tagData, tag9ALength);

                    int tag9F1ALength = emvInterface.getTagData(0x9F1A, tagData);
                    String tag9F1A = "0" + tag9F1ALength + StringUtility.getStringFormat(tagData, tag9F1ALength);

                    int tag82Length = emvInterface.getTagData(0x82, tagData);
                    String tag82 = "0" + tag82Length + StringUtility.getStringFormat(tagData, tag82Length);

                    int tag9F36Length = emvInterface.getTagData(0x9F36, tagData);
                    String tag9F36 = "0" + tag9F36Length + StringUtility.getStringFormat(tagData, tag9F36Length);

                    int tag9F26Length = emvInterface.getTagData(0x9F26, tagData);
                    String tag9F26 = "0" + tag9F26Length + StringUtility.getStringFormat(tagData, tag9F26Length);

                    int tag9F02Length = emvInterface.getTagData(0x9F02, tagData);
                    String tag9F02 = "0" + tag9F02Length + StringUtility.getStringFormat(tagData, tag9F02Length);

                    int tag9F27Length = emvInterface.getTagData(0x9F27, tagData);
                    String tag9F27 = "0" + tag9F27Length + StringUtility.getStringFormat(tagData, tag9F27Length);
                    Log.d(TAG, "tag9F27: " + StringUtility.getStringFormat(tagData, tag9F27Length) + " length : " + tag9F27Length);


                    int tag9BLength = emvInterface.getTagData(0x9B, tagData);
                    String tag9B = "0" + tag9BLength + StringUtility.getStringFormat(tagData, tag9BLength);

                    int tag57Length = emvInterface.getTagData(0x57, tagData);
                    String tag57 = "13"/* + tag57Length*/ + StringUtility.getStringFormat(tagData, tag57Length);


                    PrefUtil.setCardNo(StringUtility.getStringFormat(tagData, tag57Length).replace(" ","").trim().split("D")[0]);

                    int tag5F34Length = emvInterface.getTagData(0x5F34, tagData);
                    String tag5F34 = "0" + tag5F34Length + StringUtility.getStringFormat(tagData, tag5F34Length);

                    String tlvPost = "9F03" + tag9F03 + "95" + tag95 + "9F37" + tag9F37 + "5F2A" + tag5F2A + "9C" + tag9C + "9F10" + tag9F10 + "9F33" + tag9F33 + "9A" + tag9A + "9F1A" + tag9F1A + "82" + tag82 + "9F36" + tag9F36 + "9F26" + tag9F26 + "9F02" + tag9F02 + "9F27" + tag9F27 + "9B" + tag9B + "57" + tag57 + "5F34" + tag5F34;

                    Log.d(TAG, "tlvPost: " + tlvPost.replace(" ", "").trim());
                    System.arraycopy(recvFiled55, 0, filed55, 0, index);



                    /*emvInterface.setOnlineResult(2, filed55, filed55.length);
                    emvInterface.process();*/
                    Log.d(TAG, "EmvTest: "+PIN.replace(" ", "").trim());
//                    PIN="";


                    if (PIN.equals("error")){
                        Toast.makeText(EMVActivity.this,"PIN Wrong",Toast.LENGTH_LONG).show();
                    }
                  else   if (!PIN.isEmpty()){

                        new PostEmvTxnWithPinAPI(EMVActivity.this, String.valueOf((double) amount / 100),tlvPost.replace(" ", "").trim(), PIN.replace(" ", "").trim(), new ResponseInterface() {
                            @Override
                            public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
//                            field55Data[0] = (byte) transOnlineProcess(field55Data, length, countDownLatch, tag39, tag38, tag55, tag91, tag71, tag72);
                                emvInterface.setOnlineResult(2, StringUtility.StringToByteArray(tag55), StringUtility.StringToByteArray(tag55).length);
                                emvInterface.process();
                            }
                            @Override
                            public void onfail() {
                                TellersDbHelper tellersDbHelper = new TellersDbHelper(EMVActivity.this);
                                if (tellersDbHelper.hasTracedata("sale") > 0) {
                                    Tracemodule tracemodule = tellersDbHelper.getTraceTxn("sale");
                                    new ReverseTxnEmvAPI(EMVActivity.this, tracemodule, new ResponseInterface() {
                                        @Override
                                        public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
                                            Toast.makeText(EMVActivity.this, "Last Txn Reversed", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onfail() {

                                        }
                                    }).execute();

                                }
                            }
                        }).execute();
                    }else {

                        switch (PrefUtil.getType()){
                            case TxnType.Sale:
                                new PostEmvTxnPinlessAPI(EMVActivity.this, String.valueOf((double) amount / 100),tlvPost.replace(" ", "").trim(), "", new ResponseInterface() {
                                    @Override
                                    public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {

                                        emvInterface.setOnlineResult(2, StringUtility.StringToByteArray(tag55), StringUtility.StringToByteArray(tag55).length);
                                        emvInterface.process();

                                    }

                                    @Override
                                    public void onfail() {



                                        if (tellersDbHelper.hasTracedata("sale") > 0) {
                                            Tracemodule tracemodule = tellersDbHelper.getTraceTxn("sale");
                                            new ReverseTxnEmvAPI(EMVActivity.this, tracemodule, new ResponseInterface() {
                                                @Override
                                                public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
                                                    Log.d("dinesh", "loginSuccess: authCom Reversed Success");
                                                    Toast.makeText(EMVActivity.this, "Last Txn Reversed", Toast.LENGTH_SHORT).show();

                                                }

                                                @Override
                                                public void onfail() {

                                                }
                                            }).execute();

                                        }


                                    }
                                }).execute();
                                break;
                            case TxnType.Refund:
                                break;
                        }



                    }



                  switch (PrefUtil.getType()){
                      case TxnType.Refund:{
                          new VoidTxnE(EMVActivity.this, PrefUtil.getDe38(), PrefUtil.getReferenceNumber(), tlvPost, String.valueOf((double) amount / 100), PIN, new ResponseInterface() {
                              @Override
                              public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
                                  emvInterface.setOnlineResult(2, StringUtility.StringToByteArray(tag55), StringUtility.StringToByteArray(tag55).length);
                                  emvInterface.process();
                              }
                              @Override
                              public void onfail() {
                                  if (tellersDbHelper.hasTracedata("voidTxnE") > 0) {
                                      Tracemodule tracemodule = tellersDbHelper.getTraceTxn("voidTxnE");
                                      new ReverseTxnEmvAPI(EMVActivity.this, tracemodule, new ResponseInterface() {
                                          @Override
                                          public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
                                              Log.d(TAG, "loginSuccess: Reversed Success");
                                              Toast.makeText(EMVActivity.this, "Last Txn Reversed", Toast.LENGTH_SHORT).show();

                                          }
                                          @Override
                                          public void onfail() {

                                          }
                                      }).execute();

                                  }


                              }
                          }).execute();
                      }
                      break;
                      case TxnType.Preauth:
                      {
                          new PreAuthEmv(context, tlvPost, String.valueOf((double) amount / 100), PIN, new ResponseInterface() {
                              @Override
                              public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
                                  emvInterface.setOnlineResult(2, StringUtility.StringToByteArray(tag55), StringUtility.StringToByteArray(tag55).length);
                                  emvInterface.process();
                              }

                              @Override
                              public void onfail() {

                                  Tracemodule tracemodule = tellersDbHelper.getTraceTxn("preAuth");
                                  new ReverseTxnEmvAPI(context, tracemodule, new ResponseInterface() {
                                      @Override
                                      public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
                                          Log.d(TAG, "loginSuccess: Reversed Success");
                                          Toast.makeText(context, "Last Txn Reversed", Toast.LENGTH_SHORT).show();
                                      }
                                      @Override
                                      public void onfail() {

                                      }
                                  }).execute();
                              }
                          }).execute();
                      }
                          break;
                      case TxnType.Authcom:{
                          new PreAuthEmvComp(context, PrefUtil.getDe38(), tlvPost, String.valueOf((double) amount / 100), PIN, new ResponseInterface() {
                              @Override
                              public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
                                  emvInterface.setOnlineResult(2, StringUtility.StringToByteArray(tag55), StringUtility.StringToByteArray(tag55).length);
                                  emvInterface.process();
                              }

                              @Override
                              public void onfail() {

                                  if (tellersDbHelper.hasTracedata("authCom") > 0) {
                                      Tracemodule tracemodule = tellersDbHelper.getTraceTxn("authCom");
                                      new ReverseTxnEmvAPI(context, tracemodule, new ResponseInterface() {
                                          @Override
                                          public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
                                              Log.d("dinesh", "loginSuccess: authCom Reversed Success");
                                              Toast.makeText(context, "Last Txn Reversed", Toast.LENGTH_SHORT).show();

                                          }

                                          @Override
                                          public void onfail() {

                                          }
                                      }).execute();

                                  }



                              }
                          }).execute();
                      }

                      break;
                      case TxnType.Authcancel:{
                          new PreAuthCancelletionEmvAPI(context, PrefUtil.getDe38(), tlvPost, String.valueOf((double) amount / 100), PIN, new ResponseInterface() {
                              @Override
                              public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
                                  emvInterface.setOnlineResult(2, StringUtility.StringToByteArray(tag55), StringUtility.StringToByteArray(tag55).length);
                                  emvInterface.process();
                              }

                              @Override
                              public void onfail() {

                                  if (tellersDbHelper.hasTracedata("preAuthCancel") > 0) {
                                      Tracemodule tracemodule = tellersDbHelper.getTraceTxn("preAuthCancel");
                                      new ReverseTxnEmvAPI(context, tracemodule, new ResponseInterface() {
                                          @Override
                                          public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
                                              Log.d("dinesh", "loginSuccess: preAuthCancel Reversed Success");
                                              Toast.makeText(context, "Last Txn Reversed", Toast.LENGTH_SHORT).show();

                                          }

                                          @Override
                                          public void onfail() {

                                          }
                                      }).execute();

                                  }



                              }
                          }).execute();
                      }
                  }




                    break;
                //得到卡号
                case EMV_STATUS.EMV_REQ_CARD_CONFIRM:
                    byte[] tagD = new byte[100];
                    byte[] aid = new byte[100];
                    int tagDataLength = emvInterface.getTagData(0x57, tagD);
                    int aidLength = emvInterface.getTagData(0x9F06, aid);
                    cardNumber = StringUtility.getStringFormat(tagD, tagDataLength);
                    String aids = StringUtility.getStringFormat(aid, aidLength);
                    messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_emv_card_num)
                            + cardNumber);
                    Log.d(TAG, "aid: " + aids);
                    emvInterface.process();
                    break;
                default:
                    emvInterface.process();
                    break;
            }
        }
        //完成状态
        else if (status == EmvInterface.STATUS_COMPLETION) {
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_emv_trade_over));
            //电子现金余额查询
            if ((mTransType & 0xFF) == 0xF4) {
                if (retcode == 100) {
                    byte[] temp = new byte[32];
                    String amount = "1000.00";
                    emvInterface.getICCTagData(0x9F79, temp);
                    int ret = emvInterface.getTagData(0x9F79, temp);
                    if (ret >= 0) {
                        amount = HexDump.decBytesToHex(temp, ret);
                    }
                    messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.info_emv_elect_cash_balance)
                            + String.format("%.2f",
                            Integer.parseInt(amount) * 1.0 / 100));


                }
            } else if ((mTransType & 0xFF) == 0xF0) {

                if (retcode == 100) {
                    byte[] sum = new byte[1];
                    emvInterface.getTransLog(0x101, sum);
                    System.out.println("sum:" + sum[0]);
                    byte[] fmt_dol = new byte[60];
                    byte[] rec_val = new byte[256];
                    for (int i = 1; i <= sum[0]; i++) {
                        int index = 0;
                        int len = emvInterface.getTransLog(0x102, fmt_dol);
                        System.out.println("fmt_dol:" + HexDump.decBytesToHex(fmt_dol));

                        len = emvInterface.getTransLog(i, rec_val);
                        System.out.println("rec_val:" + HexDump.decBytesToHex(rec_val, len));

                        StringBuffer stringBuffer = new StringBuffer();

                        index = 0;
                        stringBuffer.append("9A03");
                        stringBuffer.append(HexDump.toHexString(rec_val, index, 3));
                        index += 3;
                        stringBuffer.append("9F2103");
                        stringBuffer.append(HexDump.toHexString(rec_val, index, 3));
                        index += 3;
                        stringBuffer.append("9F0206");
                        stringBuffer.append(HexDump.toHexString(rec_val, index, 6));
                        index += 6;
                        index += 6;
                        stringBuffer.append("9F1A02");
                        stringBuffer.append(HexDump.toHexString(rec_val, index, 2));
                        index += 2;
                        stringBuffer.append("5F2A02");
                        stringBuffer.append(HexDump.toHexString(rec_val, index, 2));

                        index += 2;
                        stringBuffer.append("9F4E14");
                        stringBuffer.append(HexDump.toHexString(rec_val, index, 20));
                        index += 20;
                        stringBuffer.append("9C01");
                        stringBuffer.append(HexDump.toHexString(rec_val, index, 1));
                        index += 1;
                        stringBuffer.append("9F3602");
                        stringBuffer.append(HexDump.toHexString(rec_val, index, 2));
                        messageManager.AppendInfoMessageInUiThread(stringBuffer.toString());
                    }
                }
            } else if ((mTransType & 0xFF) == 0xF2) {
                if (retcode == 100) {
                    String cardSN = "0" + HexDump.decBytesToHex(getTag(0x5F34));
                    System.out.println("cardSN:" + cardSN);
                    String cardNO = HexDump.decBytesToHex(getTag(0x5A));
                    System.out.println("cardNO:" + cardNO);
                }
            }
            byte[] data = new byte[1024];
            emvInterface.getTransLog(0, data);
        } else if (status == EmvInterface.STATUS_ERROR) {
            messageManager.AppendInfoMessageInUiThread("Ic error");
        }
    }

    //获取交易日志
    private byte[] getTag(int tagInt) {
        byte[] temp = new byte[256];
        byte[] result = null;
        int ret = emvInterface.getTagData(tagInt, temp);
        if (ret <= 0) {
            return null;
        }
        result = new byte[ret];
        System.arraycopy(temp, 0, result, 0, ret);
        return result;
    }

    private String cardNumber = "";

    //密码键盘
    private void onlinePinPad() {
        int ret = pinPad.open(new OnSendKeyListerner() {

            @Override
            public void onSendKey(int keyCode) {
                messageManager.AppendInfoMessageInUiThread("keyCode-->" + keyCode);
            }
        });
        if (ret >= 0) {
//            updateMainKey();

            pinPadOnline();
        }else {
            closePinpad(1);
        }
    }
/*

    //下载主密钥
    private void updateMainKey() {
//        byte[] oldKey = HexDump.hexStringToByteArray("701A5B3D7F6E891C56A46A18C1CD5C0A");
        byte[] oldKey = HexDump.hexStringToByteArray("7EE8E78756FFF9B79FDD12F6778B41D851768F");
        byte[] newkey = HexDump.hexStringToByteArray("7EE8E78756FFF9B79FDD12F6778B41D851768F");
//        byte[] newkey = HexDump.hexStringToByteArray("701A5B3D7F6E891C56A46A18C1CD5C0A");
        int ret = pinPad.updateMasterKey(mainKey, oldKey, newkey);
        Log.d("MainKEy", "updateMainKey: " + ret);
        if (ret >= 0)
            updateWorkKey();
        else
            closePinpad(1);
    }
*/

    private void closePinpad(int type) {
        if (type == 1)
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_pinpad_main_key_error));
        else if (type == 2)
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_pinpad_wk_error));
        pinPad.close();
    }
/*
    //下载工作密钥*
    private void updateWorkKey() {
        byte[] pinkey = HexDump.hexStringToByteArray("89EDEECE534D0BA5378DDDCBCA8AF1E2");
//        byte[] pinkey = HexDump.hexStringToByteArray("5A7C9B2D21EDF2907ED9F72A5CFB6EC5");
        int ret = pinPad.updateUserKey(mainKey, 0, mainKey, pinkey);
        if (ret >= 0)
            pinPadOnline();
        else
            closePinpad(2);
    }*/

    //获取联机pin
    private int mainKey = 0;
    private void pinPadOnline() {
        int limit = pinPad.setPinLimit(new byte[]{4, 6, 8});
        messageManager.AppendInfoMessageInUiThread("setLimit:" + limit);
        byte[] arryASCIICardNumber = cardNumber.getBytes();
        int nTimeout_MS = 60000;
        int nFlagSound = 1;
        messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.info_emv_input_pwd));
        pinPad.calculatePinBlock(2, arryASCIICardNumber, nTimeout_MS, nFlagSound, new PinPad.OnInputResultListener() {
            @Override
            public void onInputResult(int ret, byte[] result) {
                if (ret > 0) {
                    messageManager.AppendInfoMessageInUiThread("result-->" + StringUtility.ByteArrayToString(result, ret));
                    emvInterface.setOnlinePinEntered(0, new byte[]{1, 2, 3, 4, 5, 6, 7, 8}, 8);
                    int t = emvInterface.process();
                    messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.info_emv_transformation_ret) + t);
                    PIN=StringUtility.ByteArrayToString(result, ret);
                } else {
                    messageManager.AppendInfoMessageInUiThread("pin" + shower.getString(R.string.btn_pinpad_encrypt) + shower.getString(R.string.info_failed));
                    PIN="error";
                }
            }
        });
    }

    private void closeReader() {
        boolean flag = false;
        flag = contactlessCardReader.close() >= 0;
        flag &= smartCardReader.close() >= 0;
        if (flag) {
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.btn_emv_close) + shower.getString(R.string.info_success));
        } else {
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.btn_emv_close) + shower.getString(R.string.info_failed));
        }
    }
}