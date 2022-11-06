package cn.zt.pos.test;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.szzt.sdk.device.Constants.Error;
import com.szzt.sdk.device.card.ContactlessCardReader;
import com.szzt.sdk.device.card.M1KeyType;

import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.utils.MessageManager;
import cn.zt.pos.utils.Shower;
import cn.zt.pos.utils.StringUtility;

public class RFCardActivity extends BaseActivity{
    private ContactlessCardReader mContactlessCardReader;
    private Button mOpenButton;
    private Button mClearButton;
    private Button mCloseButton;
    private TextView mTextViewResult;
    MessageManager messageManager;
    private Shower shower ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        shower = new Shower(this) ;
        boolean isConnected = mainApplication.isDeviceManagerConnetcted();
        init(isConnected);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if (mContactlessCardReader != null){
    		mContactlessCardReader.close();
    	}
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
        	mContactlessCardReader = mainApplication.getContactlessCardReader();
        } else {
            mContactlessCardReader = null;
        }
    }
    
    int testContactlessCardReader() {
        int result = mContactlessCardReader.waitForCard(60000);
        if (result == 0) {
            byte[] data=new byte[256];
            int ret =mContactlessCardReader.powerOn(data);            
            messageManager.AppendInfoMessageInUiThread("Power On ATR:" +( ret>0?StringUtility.ByteArrayToString(data, ret):""));
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
                Log.d("dr", "testContactlessCardReader: "+"CardInfo of UID:"
                        + StringUtility.getStringFormat(cardInfo.uid));
            }

            if (cardType == 0x0000 || cardType == 0x0100) {
                testContactlessCardReaderIOApdu();
            } else if (cardType == 0x0002 || cardType == 0x0003) {
                testContactlessCardReaderIO_M1();
            } else {
                testContactlessCardReaderIOApdu();
            }
        } else if (result == Error.TIMEOUT) {
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_card_time_out),
                    Color.RED);
        } else if (result == Error.DEVICE_FORCE_CLOSED) {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_force_close), Color.RED);
        } else {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_read_error), Color.RED);
        }
        return result;
    }

    void testContactlessCardReaderIO_M1() {
        int iSecIdx = 0x02; //����02
        int iBlkIdx = 0x02; //��02
        byte[] pin = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        int iRet = mContactlessCardReader.verifyPinMifare(iSecIdx, M1KeyType.KEYTYPE_A, pin);
        if (iRet >= 0) {
            messageManager.AppendInfoMessageInUiThread("Sector " + iSecIdx
                    + shower.getString(R.string.msg_card_verify_pass_succ), Color.GREEN);

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
            Log.d("dr", "testContactlessCardReaderIOApdu: "+"APDU:"
                    + StringUtility.ByteArrayToString(ret, len));
        } else {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_exe_apdu_error), Color.RED);
        }
    }
    
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mag_open: {
                    int nret = mContactlessCardReader.open();
                    if (nret >= 0) {
                        messageManager
                                .AppendInfoMessage(shower.getString(R.string.msg_card_open_success));
                        messageManager
                                .AppendInfoMessage(
                                        shower.getString(R.string.msg_card_wait_for_card),
                                        Color.GREEN);
                        new Thread(){
                        	@Override
                        	public void run() {
                        		int ret = testContactlessCardReader();
                        		if (ret == 0){
                            		messageManager.AppendInfoMessageInUiThread("");
                            		mContactlessCardReader.waitRemove();
                            		messageManager.AppendInfoMessageInUiThread("");
                        		}
                        		mContactlessCardReader.close();
                        	};
                        }.start();
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
                    int ret = mContactlessCardReader.close();
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
                default:
                    break;
            }
        }
    };

}
