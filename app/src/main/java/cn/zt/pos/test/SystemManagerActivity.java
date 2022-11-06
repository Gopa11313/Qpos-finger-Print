package cn.zt.pos.test;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.utils.MessageManager;
import cn.zt.pos.utils.Shower;

import com.szzt.sdk.system.SystemManager;
import com.szzt.sdk.system.SystemManager.DeviceInfo;

public class SystemManagerActivity extends BaseActivity{
    private SystemManager mSystemManager;
    private Button sytem_reboot,system_shutdown,system_uninstall,system_install,
    				system_setTime,system_getDeviceMsg,system_getUnionPayEncryptedSN;
    private Button mClearButton;
    private TextView mTextViewResult;
    MessageManager messageManager;
    private Shower shower;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        shower = new Shower(this) ;
        boolean isConnected = mainApplication.isDeviceManagerConnetcted();
        init(isConnected);
        if (messageManager != null) {
            messageManager.AppendInfoMessage(R.string.button_system_test);
        }
    }
    
    private void init(boolean isConnected) {
            mTextViewResult = (TextView) findViewById(R.id.mag_text);
            mTextViewResult.setMovementMethod(ScrollingMovementMethod.getInstance());

            messageManager = new MessageManager(this, mTextViewResult);

            sytem_reboot = (Button) findViewById(R.id.sytem_reboot);
            sytem_reboot.setOnClickListener(listener);

            mClearButton = (Button) findViewById(R.id.mag_clear);
            mClearButton.setOnClickListener(listener);

            system_shutdown = (Button) findViewById(R.id.system_shutdown);
            system_shutdown.setOnClickListener(listener);
            
            system_uninstall = (Button) findViewById(R.id.system_uninstall);
            system_uninstall.setOnClickListener(listener);
            
            system_install = (Button) findViewById(R.id.system_install);
            system_install.setOnClickListener(listener);
            
            system_setTime = (Button) findViewById(R.id.system_setTime);
            system_setTime.setOnClickListener(listener);
            
            system_getDeviceMsg = (Button) findViewById(R.id.system_getDeviceMsg);
            system_getDeviceMsg.setOnClickListener(listener);
            
            system_getUnionPayEncryptedSN = (Button) findViewById(R.id.system_getUnionPayEncryptedSN);
            system_getUnionPayEncryptedSN.setOnClickListener(listener);
            
	        mTextViewResult.setEnabled(isConnected);
	        sytem_reboot.setEnabled(isConnected);
	        system_shutdown.setEnabled(isConnected);
	        system_uninstall.setEnabled(isConnected);
	        system_install.setEnabled(isConnected);
	        system_setTime.setEnabled(isConnected);
	        system_getDeviceMsg.setEnabled(isConnected);
	        mClearButton.setEnabled(isConnected);
	        system_getUnionPayEncryptedSN.setEnabled(isConnected);
	        if (isConnected) {
	        	mSystemManager = mainApplication.getSystemManager();
	        } else {
	        	mSystemManager = null;
	        }
    }
    
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sytem_reboot: {
                    int nret = mSystemManager.reboot();
                    if (nret < 0) {
                    	messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_reboot_fail), Color.RED);
                    }
                }
                break;
                case R.id.system_shutdown:
                	messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_sleep_start));
                	int ret =mSystemManager.sleep(1000);
                	if (ret >= 0) {
                		messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_sleep_end));
                    }
                	int nret =mSystemManager.shutdown();
                	if (nret < 0) {
                    	messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_shutdown_fail), Color.RED);
                    }
                	break;
                case R.id.system_uninstall:
                	int ret2 =mSystemManager.uninstall("com.szzt.smartboxdemo");
                	if(ret2>=0){
                		messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_uninstall_suc));
                	}else messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_uninstall_fail), Color.RED);
                	break;
                case R.id.system_install:
//                	int ret3 =mSystemManager.install(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartBoxDemo.apk");
                	int ret3 =mSystemManager.replace(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartBoxDemo.apk");
                	if(ret3>=0){
                		messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_install_suc));
                	}else messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_install_fail), Color.RED);
                	break;
                case R.id.system_setTime: 
                	int ret4 =mSystemManager.setTime(System.currentTimeMillis()-1000*60);
                	if(ret4>=0){
                		messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_settime_suc));
                	}else messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_settime_fail), Color.RED);
                	break;
                case R.id.system_getDeviceMsg:
                	getDeviceMsg();
                    break;
                case R.id.system_getUnionPayEncryptedSN:
                	if(mSystemManager.isSupportUinonPaySN()){
                		byte[] sn=mSystemManager.getUnionPayEncryptedSN("123123");
                		if(sn!=null){
                			messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_getuinonpaysn_suc)+""+sn);
                		}else {
                			messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_getuinonpaysn_fail), Color.RED);
						}
                	}else messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_getuinonpaysn_unsuport), Color.RED);
                	break;
                default:
                    break;
            }
        }

		private void getDeviceMsg() {
			DeviceInfo mDeviceInfo=	mSystemManager.getDeviceInfo();
			if(mDeviceInfo!=null){
    			messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_getdevice_msg_suc)
    					+mDeviceInfo.getSn()+""+mDeviceInfo.getType()
    					+mDeviceInfo.getModel()+mDeviceInfo.getHardwareSN());
    		}else {
    			messageManager.AppendInfoMessage(shower.getString(R.string.msg_sytem_getdevice_msg_fail), Color.RED);
			}
			
		}
    };
}
