package cn.zt.pos.test;

import android.graphics.Color;
import android.os.Bundle;
import android.os.HandlerThread;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.szzt.sdk.device.Constants.Error;
import com.szzt.sdk.device.Device;
import com.szzt.sdk.device.card.MagneticStripeCardReader;

import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.utils.MessageManager;
import cn.zt.pos.utils.Shower;

public class MagCardActivity extends BaseActivity{
    private MagneticStripeCardReader mMagneticStripeCardReader;
    private Button mOpenButton;
    private Button mClearButton;
    private Button mCloseButton;
    private TextView mTextViewResult;
    MessageManager messageManager;
    HandlerThread mWaitForCardHandlerThread;
    int mTestDeviceType = Device.TYPE_UNKNOWN;
    private Shower shower;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        shower = new Shower(this) ;
        boolean isConnected = mainApplication.isDeviceManagerConnetcted();
        init(isConnected);
        if (messageManager != null) {
            messageManager
                    .AppendInfoMessage(R.string.button_card_read_test);
        }
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if (mMagneticStripeCardReader != null){
    		mMagneticStripeCardReader.close();
    	}
    	
    }
    
    private void init(boolean isConnected) {
        if (mCloseButton == null) {
            mTextViewResult = (TextView) findViewById(R.id.mag_text);
            mTextViewResult.setMovementMethod(ScrollingMovementMethod.getInstance());
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
                mMagneticStripeCardReader = mainApplication
                        .getMagneticStripeCardReader();
        } else {
            mMagneticStripeCardReader = null;
        }
    }
    
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
    
    void testMagStripeCardReader() {
    	mMagneticStripeCardReader.setCheckLrc(1);
        int result = mMagneticStripeCardReader.waitForCard(100000);
        Log.e("waitForCard", "result:" + result);
        if (result == 0) {
            byte[] data1 = mMagneticStripeCardReader
                    .getTrackData(MagneticStripeCardReader.TRACK_INDEX_1);
            byte[] data2 = mMagneticStripeCardReader
                    .getTrackData(MagneticStripeCardReader.TRACK_INDEX_2);
            byte[] data3 = mMagneticStripeCardReader
                    .getTrackData(MagneticStripeCardReader.TRACK_INDEX_3);
            byte[] checkFlag = mMagneticStripeCardReader.getTrackData(MagneticStripeCardReader.TRACK_CHECK_FLAG);
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
                messageManager.AppendInfoMessageInUiThread(strTrack1 + " FLAG:" + checkFlag[0]);
                messageManager
                        .AppendInfoMessageInUiThread("Data of Track 2 (len "
                                + strTrack2.length() + "):");
                messageManager.AppendInfoMessageInUiThread(strTrack2 + " FLAG:" + checkFlag[1]);
                messageManager
                        .AppendInfoMessageInUiThread("Data of Track 3 (len "
                                + strTrack3.length() + "):");
                messageManager.AppendInfoMessageInUiThread(strTrack3 + " FLAG:" + checkFlag[2]);
            }
        } else if (result == Error.TIMEOUT) {
            messageManager.AppendInfoMessageInUiThread(shower.getString(R.string.msg_card_time_out), Color.RED);
        } else if (result == Error.DEVICE_FORCE_CLOSED) {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_force_close), Color.RED);
        } else {
            messageManager.AppendInfoMessageInUiThread(
                    shower.getString(R.string.msg_card_read_error), Color.RED);
        }

    }
    

    
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mag_open: {
                    int nret = mMagneticStripeCardReader.open();
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
                                testMagStripeCardReader();
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
                    int ret = mMagneticStripeCardReader.close();
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
