package cn.zt.testfile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.szzt.sdk.device.Constants;
import com.szzt.sdk.device.Constants.Error;

import com.szzt.sdk.device.Device;
import com.szzt.sdk.device.card.ContactlessCardReader;
import com.szzt.sdk.device.card.MagneticStripeCardReader;
import com.szzt.sdk.device.card.SmartCardReader;


import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.utils.MessageManager;
import cn.zt.pos.utils.Shower;
import cn.zt.pos.utils.StringUtility;

public class CardActivity extends BaseActivity{
    private MagneticStripeCardReader mMagneticStripeCardReader;
    private SmartCardReader mSmartCardReader;
    private ContactlessCardReader mContactlessCardReader;
    private Button mOpenButton;
    private Button mClearButton;
    private Button mCloseButton;
    private TextView mTextViewResult;
    MessageManager messageManager;
    HandlerThread mWaitForCardHandlerThread;
    int mTestDeviceType = Device.TYPE_UNKNOWN;
    private static final int  MSG_WAIT_FOR_CARD  = 0;
    private static final int  MSG_SMARTCARD_REMOVED = 1;
    private Shower shower ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.activity_card);
        shower = new Shower(this) ;
        boolean isConnected = mainApplication.isDeviceManagerConnetcted();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("device-type")) {
            mTestDeviceType = intent
                    .getIntExtra("device-type", mTestDeviceType);
        }
        init(isConnected);
    }

    @Override
    protected void onDestroy() {
        if (mMagneticStripeCardReader != null
                && mMagneticStripeCardReader.getStatus() != Device.STATUS_CLOSED) {
            mMagneticStripeCardReader.close();
        } else if (mSmartCardReader != null
                && mSmartCardReader.getStatus() != Device.STATUS_CLOSED) {
            mSmartCardReader.close();
        } else if (mContactlessCardReader != null
                && mContactlessCardReader.getStatus() != Device.STATUS_CLOSED) {
            mContactlessCardReader.close();
        }

        try {
            myHandler.removeMessages(MSG_SMARTCARD_REMOVED);
            myHandler.removeMessages(MSG_WAIT_FOR_CARD);
            myHandler.getLooper().quit();
            myHandler = null;
            mWaitForCardHandlerThread = null;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (messageManager != null) {
            if (mTestDeviceType == Device.TYPE_MAGSTRIPECARDREADER)
                messageManager
                        .AppendInfoMessage(R.string.test_magstripecardreader);
            else if (mTestDeviceType == Device.TYPE_SMARTCARDREADER)
                messageManager
                        .AppendInfoMessage(R.string.test_smartcardreader);
            else if (mTestDeviceType == Device.TYPE_CONTACTLESSCARDREADER)
                messageManager
                        .AppendInfoMessage(R.string.test_contacltesscardreader);
        }
        if (mWaitForCardHandlerThread == null) {
            mWaitForCardHandlerThread = new HandlerThread("wait_for_card");
        }
        mWaitForCardHandlerThread.start();
        myHandler = new MyHandler(mWaitForCardHandlerThread.getLooper());
        super.onResume();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        init(true);
    }

    @Override
    public void onServiceDisconnected() {
        super.onServiceDisconnected();
        init(false);
    }

    private void init(boolean isConnected) {
        if (mCloseButton == null) {
            mTextViewResult = (TextView) findViewById(R.id.mag_text);
            mTextViewResult.setMovementMethod(ScrollingMovementMethod
                    .getInstance());
            messageManager = new MessageManager(this, mTextViewResult);
            mOpenButton = (Button) findViewById(R.id.mag_open);
            mOpenButton.setOnClickListener(listener);

            mClearButton = (Button) findViewById(R.id.mag_clear);
            mClearButton.setOnClickListener(listener);
            mCloseButton = (Button) findViewById(R.id.mag_close);
            mCloseButton.setOnClickListener(listener);
        }
        mTextViewResult.setEnabled(isConnected);
        mOpenButton.setEnabled(isConnected);
        mClearButton.setEnabled(isConnected);
        mCloseButton.setEnabled(isConnected);
        if (isConnected) {
            if (mTestDeviceType == Device.TYPE_MAGSTRIPECARDREADER)
                mMagneticStripeCardReader = mainApplication
                        .getMagneticStripeCardReader();
            else if (mTestDeviceType == Device.TYPE_SMARTCARDREADER)
                mSmartCardReader = mainApplication.getSmartCardReader();
            else if (mTestDeviceType == Device.TYPE_CONTACTLESSCARDREADER)
                mContactlessCardReader = mainApplication
                        .getContactlessCardReader();
        } else {
            mMagneticStripeCardReader = null;
            mSmartCardReader = null;
            mContactlessCardReader = null;
        }
    }

    void testMagStripeCardReader() {
        int result = mMagneticStripeCardReader.waitForCard(100000);
        if (result == 0) {
            byte[] data1 = mMagneticStripeCardReader
                    .getTrackData(MagneticStripeCardReader.TRACK_INDEX_1);
            byte[] data2 = mMagneticStripeCardReader
                    .getTrackData(MagneticStripeCardReader.TRACK_INDEX_2);
            byte[] data3 = mMagneticStripeCardReader
                    .getTrackData(MagneticStripeCardReader.TRACK_INDEX_3);

//            byte[] data4=mMagneticStripeCardReader.getTrackData(Mags)
            if (data1 == null && data2 == null && data3 == null) {
                messageManager.AppendInfoMessageInUiThread(
                        shower.getString(R.string.msg_card_read_error), Color.RED);
            } else {
                messageManager.AppendInfoMessageInUiThread(
                        shower.getString(R.string.msg_card_read_succ), Color.RED);

                String strTrack1 = trackFormat(data1);
                String strTrack2 = trackFormat(data2);
                String strTrack3 = trackFormat(data3);

                messageManager
                        .AppendInfoMessageInUiThread("Data of Track 1 (len "
                                + strTrack1.length() + "):");
                messageManager.AppendInfoMessageInUiThread(strTrack1);
                messageManager
                        .AppendInfoMessageInUiThread("Data of Track 2 (len "
                                + strTrack2.length() + "):");
                messageManager.AppendInfoMessageInUiThread(strTrack2);
                messageManager
                        .AppendInfoMessageInUiThread("Data of Track 3 (len "
                                + strTrack3.length() + "):");
                messageManager.AppendInfoMessageInUiThread(strTrack3);
                //messageManager.AppendInfoMessageInUiThread("Data of Track 1:"+trackFormat(data1));
                //messageManager.AppendInfoMessageInUiThread("Data of Track 2:"+trackFormat(data2));
                //messageManager.AppendInfoMessageInUiThread("Data of Track 3:"+trackFormat(data3));
            }
//            messageManager.AppendInfoMessageInUiThread(
//                    R.string.info_action_continue_or_close_device, Color.GREEN);
        } else if (result == Error.TIMEOUT) {
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_card_time_out), Color.RED);
        } else if (result == Error.DEVICE_FORCE_CLOSED) {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_close_succ), Color.RED);
        } else {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_read_error), Color.RED);
        }
    }

    void testSmartCardReader() {
        int result = mSmartCardReader.waitForCard(0, Constants.WAIT_INFINITE);
        if (result == 0) {
        	byte[] data=new byte[256];
        	result=mSmartCardReader.powerOn(0, data);
            messageManager.AppendInfoMessageInUiThread("Power On ATR:" + (result>0?StringUtility.ByteArrayToString(data, result):""));

//            messageManager.AppendInfoMessageInUiThread(
//                    R.string.info_action_test_io, Color.GREEN);
            testSmartCardReaderIO();
        } else if (result == Error.TIMEOUT) {
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_card_time_out),
                    Color.RED);
        } else if (result == Error.DEVICE_FORCE_CLOSED) {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_close_succ), Color.RED);
        } else {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_read_error), Color.RED);
        }
    }

    void testContactlessCardReader() {
        int result = mContactlessCardReader.waitForCard(60000);
        if (result == 0) {
            byte[] data=new byte[256];
            result=mContactlessCardReader.powerOn(data);            
            messageManager.AppendInfoMessageInUiThread("Power On ATR:" +( result>0?StringUtility.ByteArrayToString(data, result):""));
            int cardType = mContactlessCardReader.getCardType();

            switch (cardType) {
                case 0x0000:
                    messageManager
                            .AppendInfoMessageInUiThread("Get Card Type: A_CPU");
                    break;
                case 0x0100:
                    messageManager
                            .AppendInfoMessageInUiThread("Get Card Type: B_CPU");
                    break;
                case 0x0001:
                    messageManager
                            .AppendInfoMessageInUiThread("Get Card Type: CLASSIC_MINI");
                    break;
                case 0x0002:
                    messageManager
                            .AppendInfoMessageInUiThread("Get Card Type: CLASSIC_1K");
                    break;
                case 0x0003:
                    messageManager
                            .AppendInfoMessageInUiThread("Get Card Type: CLASSIC_4K");
                    break;
                case 0x0004:
                    messageManager
                            .AppendInfoMessageInUiThread("Get Card Type: UL_64");
                    break;
                case 0x0005:
                    messageManager
                            .AppendInfoMessageInUiThread("Get Card Type: UL_192");
                    break;
                case 0x0006:
                    messageManager
                            .AppendInfoMessageInUiThread("Get Card Type: MP_2K_SL1");
                    break;
                case 0x0007:
                    messageManager
                            .AppendInfoMessageInUiThread("Get Card Type: MP_4K_SL1");
                    break;
                case 0x0008:
                    messageManager
                            .AppendInfoMessageInUiThread("Get Card Type: MP_2K_SL2");
                    break;
                case 0x0009:
                    messageManager
                            .AppendInfoMessageInUiThread("Get Card Type: MP_4K_SL2");
                    break;
//                default:
//                    messageManager.AppendInfoMessageInUiThread(
//                            "Get Card Type: UNKNOW", Color.RED);
                default:
                    messageManager.AppendInfoMessageInUiThread(
                            "Get Card Type: A_CPU", Color.BLACK);
                    break;
            }

            ContactlessCardReader.CardInfo cardInfo = mContactlessCardReader.getCardInfo();
            if (cardInfo != null) {
                messageManager.AppendInfoMessageInUiThread("CardInfo of UID:"
                        + StringUtility.getStringFormat(cardInfo.uid));
            }

            if (cardType == 0x0000 || cardType == 0x0100) {
                testContactlessCardReaderIOApdu();
            } else if (cardType == 0x0002 || cardType == 0x0003) {
                testContactlessCardReaderIO_M1();
            } else {
                testContactlessCardReaderIOApdu();
//                messageManager.AppendInfoMessageInUiThread("读写测试功能未完善",
//                        Color.RED);
            }
        } else if (result == Error.TIMEOUT) {
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_card_time_out),
                    Color.RED);
        } else if (result == Error.DEVICE_FORCE_CLOSED) {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_close_succ), Color.RED);
        } else {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_read_error), Color.RED);
        }
    }

    void testContactlessCardReaderIO_M1() {
        int iSecIdx = 0x02; //扇区02
        int iBlkIdx = 0x02; //块02
        byte[] pin = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };

        int iRet = mContactlessCardReader.verifyPinMifare(iSecIdx, 0x0A, pin);
        if (iRet >= 0) {
            messageManager.AppendInfoMessageInUiThread("Sector " + iSecIdx
                    + shower.getString(R.string.msg_card_verify_pass_succ), Color.GREEN);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            byte[] inData = new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0x03,
                    (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07,
                    (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B,
                    (byte) 0x0C, (byte) 0xA1, (byte) 0xB2, (byte) 0xC3,
                    (byte) 0xD4 };

            iRet = mContactlessCardReader.writeMifare(iSecIdx, iBlkIdx, inData);
            if (iRet >= 0) {
                messageManager.AppendInfoMessageInUiThread("Write [" + iSecIdx
                        + ", " + iBlkIdx + "]:"
                        + StringUtility.ByteArrayToString(inData, 16));
            } else {
                messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_card_write_op_error), Color.RED);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            byte[] outBuf = new byte[16];

            iRet = mContactlessCardReader.readMifare(iSecIdx, iBlkIdx, outBuf);
            if (iRet >= 0) {
                messageManager.AppendInfoMessageInUiThread("Read [" + iSecIdx
                        + ", " + iBlkIdx + "]:"
                        + StringUtility.ByteArrayToString(outBuf, iRet));
            } else {
                messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_card_read_op_error), Color.RED);
            }
        } else {
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_card_verify_pass_error), Color.RED);
        }
    }

    void testContactlessCardReaderIOApdu() {
        // 00 A4 04 00 0E 2PAY.SYS.DDF01
        byte[] apdu = new byte[] { (byte) 0x00, (byte) 0xA4, (byte) 0x04,
                (byte) 0x00, (byte) 0x0E, 0x32, 0x50, 0x41, 0x59, 0x2E, 0x53,
                0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31 };
        byte[] ret = new byte[255];
        int len = mContactlessCardReader.transmit(apdu, ret);
        if (len >= 0) {
            if (len >= 2 && ret[len - 2] == 97) {
                apdu = new byte[] { 0x00, (byte) 0xC0, 0x00, 0x00,
                        (byte) (ret[len - 1] & 0xFF) };
                ret = new byte[255];
                len = mContactlessCardReader.transmit(apdu, ret);
            }
        }
        if (len >= 0) {
            messageManager.AppendInfoMessageInUiThread("APDU:"
                    + StringUtility.ByteArrayToString(ret, len));
        } else {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_exe_apdu_error), Color.RED);
        }
    }

    void testSmartCardReaderIO() {
        // 00 A4 04 00 0E 1PAY.SYS.DDF01
        byte[] apdu = new byte[] { (byte) 0x00, (byte) 0xA4, (byte) 0x04,
                (byte) 0x00, (byte) 0x0E, 0x31, 0x50, 0x41, 0x59, 0x2E, 0x53,
                0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31,0x00,0x00};
        byte[] ret = new byte[255];
        int len = mSmartCardReader.transmit(0, apdu, ret);
        if (len >= 0) {
            if (len >= 2 && ret[len - 2] == 97) {
                apdu = new byte[] { 0x00, (byte) 0xC0, 0x00, 0x00,
                        (byte) (ret[len - 1] & 0xFF) };
                ret = new byte[255];
                len = mSmartCardReader.transmit(0, apdu, ret);
            }
        }
        if (len >= 0) {
            messageManager.AppendInfoMessageInUiThread("APDU:"
                    + StringUtility.ByteArrayToString(ret, len));
        } else {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_exe_apdu_error), Color.RED);
        }
    }

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message m) {
            int what = m.what;
            if (what == MSG_WAIT_FOR_CARD) {
                if (mTestDeviceType == Device.TYPE_MAGSTRIPECARDREADER)
                    testMagStripeCardReader();
                else if (mTestDeviceType == Device.TYPE_SMARTCARDREADER) {
                    testSmartCardReader();
                } else if (mTestDeviceType == Device.TYPE_CONTACTLESSCARDREADER) {
                    testContactlessCardReader();
                }
            } else if (what == MSG_SMARTCARD_REMOVED) {
                messageManager.AppendInfoMessageInUiThread(
                        shower.getString(R.string.msg_card_unplug_card), Color.RED);
            }
        }
    }

    private MyHandler myHandler;

    private String trackFormat(byte[] data) {
        if (data == null) {
            return "no track info";
        } else {
            return new String(trackTrim(data));
        }
    }

    private byte[] trackTrim(byte[] data) {
        if (data == null) {
            return null;
        } else {
            int end = 0;
            for (int i = data.length - 1; i >= 0; i--) {
                if (data[i] != 0) {
                    end = i;
                    break;
                }
            }
            byte[] d = new byte[end + 1];
            System.arraycopy(data, 0, d, 0, end + 1);
            return d;
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mag_open: {
                    int nret = -1;
                    if (mTestDeviceType == Device.TYPE_MAGSTRIPECARDREADER) {
                        nret = mMagneticStripeCardReader
                                .open();

                    } else if (mTestDeviceType == Device.TYPE_SMARTCARDREADER) {
                        nret = mSmartCardReader
                                .open(0,
                                        new SmartCardReader.SCReaderListener(){
                                            @Override
                                            public void notify(
                                                    int nSlotIndex,
                                                    int nEvent) {
                                                if (nEvent == SmartCardReader.EVENT_SMARTCARD_NOT_READY) {
                                                    myHandler
                                                            .sendEmptyMessage(MSG_SMARTCARD_REMOVED);
                                                }
                                            }
                                        });
                    } else if (mTestDeviceType == Device.TYPE_CONTACTLESSCARDREADER) {
                        nret = mContactlessCardReader
                                .open();
                    }
                    if (nret >= 0) {
                        messageManager
                                .AppendInfoMessage(shower.getString(R.string.msg_card_open_success));
                        messageManager
                                .AppendInfoMessage(
                                        shower.getString(R.string.msg_card_wait_for_card),
                                        Color.GREEN);
                        myHandler
                                .sendEmptyMessage(MSG_WAIT_FOR_CARD);
                    } else {
                        messageManager
                                .AppendInfoMessage(shower.getString(R.string.msg_card_open_error));
                    }
                }
                break;
                case R.id.mag_clear:
                    messageManager.clear();
                    break;
                case R.id.mag_close: {
                    int ret = -1;
                    if (mTestDeviceType == Device.TYPE_MAGSTRIPECARDREADER) {
                        ret = mMagneticStripeCardReader
                                .close();
                    } else if (mTestDeviceType == Device.TYPE_SMARTCARDREADER) {
                        ret = mSmartCardReader
                                .close();
                    } else if (mTestDeviceType == Device.TYPE_CONTACTLESSCARDREADER) {
                        ret = mContactlessCardReader
                                .close();
                    }
                    if (ret >= 0) {
                        messageManager
                                .AppendInfoMessage(shower.getString(R.string.msg_card_close_succ));
                    } else {
                        messageManager
                                .AppendInfoMessage(
                                        shower.getString(R.string.msg_card_close_succ),
                                        Color.RED);
                    }
                }

                break;
//            case R.id.mag_read: {
//
//                if (myHandler
//                        .hasMessages(MSG_WAIT_FOR_CARD)) {
//                    messageManager
//                    .AppendInfoMessage(
//                            R.string.info_wait_for_card_already,
//                            Color.RED);
//                } else {
//                    int status = Device.STATUS_IDLE;
//                    if (mTestDeviceType == Device.TYPE_MAGSTRIPECARDREADER) {
//                        status = mMagneticStripeCardReader
//                                .getStatus();
//                    } else if (mTestDeviceType == Device.TYPE_SMARTCARDREADER) {
//                        status = mSmartCardReader
//                                .getStatus();
//                    } else if (mTestDeviceType == Device.TYPE_CONTACTLESSCARDREADER) {
//                        status = mContactlessCardReader
//                                .getStatus();
//                    }
//
//                    if (status == Device.STATUS_IN_ACTION) {
//                        messageManager
//                        .AppendInfoMessage(
//                                R.string.info_wait_for_card_already,
//                                Color.GREEN);
//                    } else {
//                        messageManager
//                        .AppendInfoMessage(
//                                R.string.info_wait_for_card,
//                                Color.RED);
//                        myHandler
//                        .sendEmptyMessage(MSG_WAIT_FOR_CARD);
//                    }
//
//                }
//            }
//            break;
                default:
                    break;
            }
        }
    };
}
