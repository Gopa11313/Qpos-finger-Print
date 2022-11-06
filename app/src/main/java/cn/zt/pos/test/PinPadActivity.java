package cn.zt.pos.test;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.szzt.sdk.device.pinpad.PinPad;

import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.utils.HexDump;
import cn.zt.pos.utils.MessageManager;
import cn.zt.pos.utils.Shower;
import cn.zt.pos.utils.StringUtil;
import cn.zt.pos.utils.StringUtility;

public class PinPadActivity extends BaseActivity {
    private MessageManager messageManager;
    private PinPad mPinpad;
    private Button mClearButton;
    private Button mOpenButton;
    private Button mCloseButton;
    private Button mShowTextButton;
    private Button mLoadMasterKeyButton;
    private Button mUpdateWorkKeyButton;
    private Button mEncryptDataButton;
    private Shower shower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pinpad);
        shower = new Shower(this);
        boolean isConnected = mainApplication.isDeviceManagerConnetcted();
        init(isConnected);
    }

    //获取联机pin
    public void cal_pin(View v) {
        PinpadCalculatePin();
    }

    private void init(boolean isConnected) {
        if (messageManager == null) {
            TextView text = findViewById(R.id.pinpad_text);
            text.setMovementMethod(ScrollingMovementMethod.getInstance());
            messageManager = new MessageManager(this, text);
            mClearButton = findViewById(R.id.pinpad_clear);
            mClearButton.setOnClickListener(listener);
            mOpenButton = findViewById(R.id.pinpad_open);
            mOpenButton.setOnClickListener(listener);
            mCloseButton = findViewById(R.id.pinpad_close);
            mCloseButton.setOnClickListener(listener);
            mShowTextButton = findViewById(R.id.pinpad_showText);
            mShowTextButton.setOnClickListener(listener);
            mLoadMasterKeyButton = findViewById(R.id.pinpad_loadMasterKey);
            mLoadMasterKeyButton.setOnClickListener(listener);
            mUpdateWorkKeyButton = findViewById(R.id.pinpad_updateWorkKey);
            mUpdateWorkKeyButton.setOnClickListener(listener);
            mEncryptDataButton = findViewById(R.id.pinpad_encryptData);
            mEncryptDataButton.setOnClickListener(listener);
            mPinpad = mainApplication.getPinPad();

        }
        mClearButton.setEnabled(isConnected);
        mOpenButton.setEnabled(isConnected);
        mCloseButton.setEnabled(isConnected);
        mShowTextButton.setEnabled(isConnected);
        mUpdateWorkKeyButton.setEnabled(isConnected);
        mUpdateWorkKeyButton.setEnabled(isConnected);
        mEncryptDataButton.setEnabled(isConnected);
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pinpad_clear:
                    PinpadCalculateMac();
                    break;
                case R.id.pinpad_open:
                    PinpadOpen();
                    break;
                case R.id.pinpad_close:
                    PinpadClose();
                    break;
                case R.id.pinpad_showText:
                    PinpadShowText();
                    break;
                case R.id.pinpad_loadMasterKey:
                    PinpadUpdateMasterKey();
                    break;
                case R.id.pinpad_updateWorkKey:
                    PinpadUpdateUserKey();
                    break;
                case R.id.pinpad_encryptData:
                    PinpadEncryptData();
                    break;
                default:
                    break;
            }
        }

    };
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message m) {
            int ret = m.what;
            if (ret >= 0) {
                byte[] data = (byte[]) m.obj;
                messageManager
                        .AppendInfoMessage(shower.getString(R.string.msg_pinpad_pin_result)
                                + StringUtility
                                .ByteArrayToString(
                                        data,
                                        ret));
            } else {
                messageManager
                        .AppendInfoMessage(
                                shower.getString(R.string.msg_pinpad_pin_result_error),
                                Color.RED);
            }
        }

        ;
    };

    private void PinpadCalculatePin() {
        messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_pin_prompt), Color.GREEN);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPinpad.showText(0, "金额:123.00".getBytes(), true);
                mPinpad.setPinLimit(new byte[]{0, 6});
                Bundle bundle = new Bundle();
                bundle.putInt(PinPad.PinStyle.CUSTOM_VIEW_TYPE, 4);
                mPinpad.setPinViewStyle(bundle);
//                mPinpad.calculatePinBlock(0, "6225776819866784".getBytes(), 60000, 0, new PinPad.OnInputResultListener() {
                mPinpad.calculatePinBlock(0, "6210947000000062".getBytes(), 10000, 0, new PinPad.OnInputResultListener() {
                    @Override
                    public void onInputResult(int retCode, byte[] data) {
                        Message m = new Message();
                        m.what = retCode;
                        m.obj = data;
                        mHandler.sendMessage(m);
                    }
                });
            }
        }).start();
    }

    //mac计算
    private void PinpadCalculateMac() {
        String srcData = "170C020B0361142D8000000000000000170C020B0361142D8000000000000000" +
                "170C020B0361142D8000000000000000170C020B0361142D8000000000000000" +
                "170C020B0361142D8000000000000000170C020B0361142D8000000000000000" +
                "170C020B0361142D8000000000000000170C020B0361142D8000000000000000";
        byte[] data = new byte[256];
        int ret = mPinpad.calculateMac(1, StringUtil.hexString2bytes(srcData), PinPad.MAC_TYPE_X99, data);
        if (ret >= 0) {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_mac_data) + srcData +
                    shower.getString(R.string.msg_pinpad_mac_result_1)
                    + StringUtility.ByteArrayToString(data, ret));
        } else {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_mac_data) + srcData
                    + shower.getString(R.string.msg_pinpad_mac_result_2), Color.RED);
        }
    }

    //加密
    private void PinpadEncryptData() {
        String srcData = "111111";
        byte[] data = new byte[256];
        int ret = mPinpad.encryptData(2, srcData.getBytes(), data);
        if (ret >= 0) {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_encrypt_data) + srcData +
                    shower.getString(R.string.msg_pinpad_encrypt_result_1)
                    + StringUtility.ByteArrayToString(data, ret));
        } else {
            messageManager.AppendInfoMessage(
                    shower.getString(R.string.msg_pinpad_encrypt_data) + srcData +
                            shower.getString(R.string.msg_pinpad_encrypt_result_1), Color.RED);
        }
    }

    //下载主密钥
    private void PinpadUpdateMasterKey() {
        byte[] newKey = new byte[]{(byte) 0x89, (byte) 0x40, (byte) 0xF7, (byte) 0xB3,
                (byte) 0xEA, (byte) 0xCA, (byte) 0x59, (byte) 0x39,
                (byte) 0x89, (byte) 0x40, (byte) 0x87, (byte) 0xB3,
                (byte) 0xEA, (byte) 0xCA, (byte) 0x89, (byte) 0x39};
        System.out.println("new keyyyy:" + HexDump.decBytesToHex(newKey));
        byte[] mainKey = HexDump.hexStringToByteArray("7EE8E78756FFF9B79FDD12F6778B41D8");

        int ret = mPinpad.updateMasterKey(2, mainKey, null);
        if (ret >= 0) {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_main_key_succ));
        } else {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_main_key_error));
        }
    }

    //下载工作密钥
    private void PinpadUpdateUserKey() {
//    	byte[] keys=new byte[] { (byte) 0x38, (byte) 0x38, (byte) 0x38, (byte) 0x38,
//                (byte) 0x38, (byte) 0x38, (byte) 0x38, (byte) 0x38,
//                (byte) 0x38, (byte) 0x38, (byte) 0x38, (byte) 0x38,
//                (byte) 0x38, (byte) 0x38, (byte) 0x38, (byte) 0x38};

        byte[] keys = HexDump.hexStringToByteArray("89EDEECE534D0BA5378DDDCBCA8AF1E2");
        //下载pin类型密钥
        int ret = mPinpad.updateUserKey(2, PinPad.KeyType.PIN, 0, keys);
        if (ret >= 0) {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_wk_0_succ));
        } else {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_wk_0_error), Color.RED);
        }
        keys = new byte[]{(byte) 0x99, (byte) 0x4D, (byte) 0x4D,
                (byte) 0xC1, (byte) 0x57, (byte) 0xB9, (byte) 0x6C,
                (byte) 0x52, (byte) 0x99, (byte) 0x4D, (byte) 0x4D,
                (byte) 0xC1, (byte) 0x57, (byte) 0xB9, (byte) 0x6C, (byte) 0x52};
        //下载mac类型密钥
        ret = mPinpad.updateUserKey(2, PinPad.KeyType.MAC, 1, keys);
        if (ret >= 0) {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_wk_1_succ));
        } else {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_wk_1_error), Color.RED);
        }
        keys = new byte[]{(byte) 0x38, (byte) 0x38, (byte) 0x38, (byte) 0x38,
                (byte) 0x38, (byte) 0x48, (byte) 0x38, (byte) 0x38,
                (byte) 0x38, (byte) 0x48, (byte) 0x38, (byte) 0x38,
                (byte) 0x38, (byte) 0x48, (byte) 0x38, (byte) 0x38};
        //下载des类型密钥
        ret = mPinpad.updateUserKey(2, PinPad.KeyType.TD, 2, keys);
        if (ret >= 0) {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_wk_2_succ));
        } else {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_wk_2_error), Color.RED);
        }
    }

    private void PinpadClose() {
        int ret = mPinpad.close();
        if (ret >= 0) {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_close_succ));
        } else {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_close_error), Color.RED);
        }
    }

    //设置密码键盘显示的字符串
    private void PinpadShowText() {
        int ret = mPinpad.showText(0, "PINPAD TEST".getBytes(), true);
        if (ret >= 0) {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_show_succ));
        } else {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_show_error), Color.RED);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    //打开密码键盘
    private void PinpadOpen() {
        int ret = mPinpad.open(new PinPad.OnSendKeyListerner() {
            @Override
            public void onSendKey(final int keyCode) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        messageManager.AppendInfoMessage("keyCode:" + keyCode);

                    }
                });
            }
        });
        if (ret >= 0) {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_open_succ));
        } else {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_pinpad_open_error), Color.RED);
        }
    }

}
