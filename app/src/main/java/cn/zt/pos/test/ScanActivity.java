package cn.zt.pos.test;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.szzt.sdk.device.Constants.Error;
import com.szzt.sdk.device.barcode.CameraScan;
import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.utils.MessageManager;
/**
 * 扫描界面
 *
 */
public class ScanActivity extends BaseActivity {
    private MessageManager messageManager;
    private CameraScan mScan;
    private Button  mClearButton;
    private Button  mFrontButton;
    private Button  mBackButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.activity_scan);
        boolean isConnected = mainApplication.isDeviceManagerConnetcted();
        init(isConnected);
    }

    private void init(boolean isConnected) {
        if (messageManager == null) {
            TextView text = (TextView) findViewById(R.id.scan_text);
            text.setMovementMethod(ScrollingMovementMethod.getInstance());
            messageManager = new MessageManager(this, text);
            mClearButton = (Button) findViewById(R.id.scan_clear);
            mClearButton.setOnClickListener(listener);
            mFrontButton = (Button) findViewById(R.id.scan_front);
            mFrontButton.setOnClickListener(listener);
            mBackButton = (Button) findViewById(R.id.scan_back);
            mBackButton.setOnClickListener(listener);
            mScan = mainApplication.getCameraScanImpl();
        }
       /* mClearButton.setEnabled(isConnected);
        mFrontButton.setEnabled(isConnected);
        mBackButton.setEnabled(isConnected);*/
    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.scan_clear:
                messageManager.clear();
                break;
            //前置摄像头
            case R.id.scan_front: {
            	Scan(CameraScan.CarmeraType.TYPE_FRONT_FACING,true);                
            }
            	break;
            //后置摄像头
            case R.id.scan_back: {
            	Scan(CameraScan.CarmeraType.TYPE_BACK_FACING,true);             
            }
            break;
            default:
                break;
            }
        }
    };
    protected void Scan(int typeFrontFacing, boolean b) {
    	Bundle bundle=new Bundle();
    	bundle.putInt(CameraScan.BARCODE_CAMERA_TYPE,typeFrontFacing);
    	bundle.putBoolean(CameraScan.BARCODE_BEEP, b);
    	mScan.setConfig(bundle);
    	mScan.scan(30000, new CameraScan.CameraListener() {
			@Override
			public void onNotify(final int ret, final byte[] data) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						 if (ret >= 0) {
							 messageManager
				                .AppendInfoMessage(getString(R.string.info_scan_success)+"\n"+new String(data));
				        }
						 else if(ret==Error.TIMEOUT){
							 messageManager
					            .AppendInfoMessage(getString(R.string.info_scan_timeout),Color.RED);
						 }
						 else if(ret==Error.DEVICE_FORCE_CANCLE){
							 messageManager
					            .AppendInfoMessage(getString(R.string.info_scan_cancle),Color.RED);
						 }
						 else if(ret==Error.DEVICE_USED){
							 messageManager
					            .AppendInfoMessage(getString(R.string.info_scan_used),Color.RED);
						 }
						 else {
				            messageManager
				            .AppendInfoMessage(getString(R.string.info_scan_failed), Color.RED);
				        }
					}
				});
			}
		});
	}

	private Handler mHandler=new Handler(Looper.getMainLooper());
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
