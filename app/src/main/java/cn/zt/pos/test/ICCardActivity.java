package cn.zt.pos.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.szzt.android.util.HexDump;
import com.szzt.sdk.device.Constants.Error;
import com.szzt.sdk.device.card.SmartCardReader;
import com.szzt.sdk.device.card.SmartCardSlotInfo;

import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.utils.MessageManager;
import cn.zt.pos.utils.Shower;
import cn.zt.pos.utils.StringUtility;

public class ICCardActivity extends BaseActivity{
    private SmartCardReader mSmartCardReader;
    private Button mOpenButton;
    private Button mClearButton;
    private Button mCloseButton;
    private Button mSetVoltButton;
    private TextView mTextViewResult;
    MessageManager messageManager;
    private Shower shower ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ic_card);
        shower = new Shower(this) ;
        boolean isConnected = mainApplication.isDeviceManagerConnetcted();
        init(isConnected);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if (mSmartCardReader != null){
    		mSmartCardReader.close(0);
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

            
            mSetVoltButton = (Button) findViewById(R.id.ic_set_slot);
            mSetVoltButton.setOnClickListener(listener);
            
            mCloseButton = (Button) findViewById(R.id.mag_close);
            mCloseButton.setOnClickListener(listener);
        }
        mTextViewResult.setEnabled(isConnected);
        mOpenButton.setEnabled(isConnected);
        mClearButton.setEnabled(isConnected);
        mCloseButton.setEnabled(isConnected);
        if (isConnected) {
                mSmartCardReader = mainApplication.getSmartCardReader();
        } else {
            mSmartCardReader = null;
        }
    }
    byte mVolt = SmartCardReader.SlotVolt.MODE_3_VOLT;
    void testSmartCardReader() {
        int result = mSmartCardReader.waitForCard(0, 60000);
        if (result == 0) {
        	int type = mSmartCardReader.getCardType(0);
        	System.out.println("type:" + type);
        	if (type == SmartCardReader.CONTACT_CARD_TYPE_A_CPU){
        		byte[] data=new byte[256];
        		result=mSmartCardReader.powerOn(0, data);
                messageManager.AppendInfoMessageInUiThread("Power On ATR:" + (result>0?StringUtility.ByteArrayToString(data, result):""));
                testSmartCardReaderIO();
        	}else if (type == SmartCardReader.CONTACT_CARD_TYPE_4442){
        		byte[] data=new byte[4];
        		result=mSmartCardReader.powerOn(0, data);
                messageManager.AppendInfoMessageInUiThread("Power On ATR:" + (result>0?StringUtility.ByteArrayToString(data, result):""));
                messageManager.AppendInfoMessageInUiThread("type:4442");
                testMemoryCard();
        	}else if (type == SmartCardReader.CONTACT_CARD_TYPE_4428){
        		byte[] data=new byte[4];
        		result=mSmartCardReader.powerOn(0, data);
                messageManager.AppendInfoMessageInUiThread("Power On ATR:" + (result>0?StringUtility.ByteArrayToString(data, result):""));
                messageManager.AppendInfoMessageInUiThread("type:4428");
                testMemoryCard();
        	}else if (type == SmartCardReader.CONTACT_CARD_TYPE_AT24C02){
        		byte[] data=new byte[4];
        		result=mSmartCardReader.powerOn(0, data);
                messageManager.AppendInfoMessageInUiThread("Power On ATR:" + (result>0?StringUtility.ByteArrayToString(data, result):""));
                messageManager.AppendInfoMessageInUiThread("type:AT24C02");
                testMemory24C02();
        	}else if (type == SmartCardReader.CONTACT_CARD_TYPE_AT88SC102){
        		byte[] data=new byte[4];
        		result=mSmartCardReader.powerOn(0, data);
                messageManager.AppendInfoMessageInUiThread("Power On ATR:" + (result>0?StringUtility.ByteArrayToString(data, result):""));
                messageManager.AppendInfoMessageInUiThread("type:AT88SC102");
                testMemory102();
        	}else if (type == SmartCardReader.CONTACT_CARD_TYPE_AT88SC1604){
        		byte[] data=new byte[4];
        		result=mSmartCardReader.powerOn(0, data);
                messageManager.AppendInfoMessageInUiThread("Power On ATR:" + (result>0?StringUtility.ByteArrayToString(data, result):""));
                messageManager.AppendInfoMessageInUiThread("type:AT88SC1604");
                //TODO
        	}else if (type == SmartCardReader.CONTACT_CARD_TYPE_AT88SC1608){
        		byte[] data=new byte[4];
        		result=mSmartCardReader.powerOn(0, data);
                messageManager.AppendInfoMessageInUiThread("Power On ATR:" + (result>0?StringUtility.ByteArrayToString(data, result):""));
                messageManager.AppendInfoMessageInUiThread("type:AT88SC1608");
                testMemory1608();
        	}else{
        		messageManager.AppendInfoMessageInUiThread("Not support");
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
    }
    
    private void testMemory24C02(){
		byte[] data = HexDump.hexStringToByteArray("12345678123456781234567812345678");
		write(SmartCardReader.MemoryCardArea.MAIN, 128, data);
    	read(SmartCardReader.MemoryCardArea.MAIN, 128, new byte[16]);
    }
    
    private void testMemory1608(){
    	byte[] key = HexDump.hexStringToByteArray("501A8D");
    	boolean check = verify(SmartCardReader.MemoryKeyType.USER_WR7, key);
    	if (check){
    		read(SmartCardReader.MemoryCardArea.USER7, 0, new byte[16]);
//        	check = verify(SmartCardReader.MemoryKeyType.USER_WR7, key);
        	if (check){
        		byte[] data = HexDump.hexStringToByteArray("12345678123456781234567812345678");
        		write(SmartCardReader.MemoryCardArea.USER7, 0, data);
        		read(SmartCardReader.MemoryCardArea.USER7, 0, new byte[16]);
        	}
        	
    	}
    }

    private void testMemory102(){
    	byte[] key = HexDump.hexStringToByteArray("FFFF");
    	boolean check = verify(SmartCardReader.MemoryKeyType.MAIN, key);
    	if (check){
    		read(SmartCardReader.MemoryCardArea.MAIN, 32, new byte[16]);
    	}
    }
    
    private void write(int area, int add, byte[] value){
		int ret = mSmartCardReader.write(0, area, add, value);
		if (ret >= 0){
			 messageManager.AppendInfoMessageInUiThread("write:" + HexDump.toHexString(value));
			 messageManager.AppendInfoMessageInUiThread("write memory succ!" );
		}else{
			 messageManager.AppendInfoMessageInUiThread("write memory faild!");
		}
		
    }
    
    private void read(int area, int add, byte[] value){
		int ret = mSmartCardReader.read(0, area, add, value);
		if (ret > 0){
			 messageManager.AppendInfoMessageInUiThread("read memory:"  + HexDump.toHexString(value, 0, ret));
		}else{
			 messageManager.AppendInfoMessageInUiThread("read memory faild!");
		}
		
    }
    
    private boolean verify(int keyType, byte[] key){
		int ret = mSmartCardReader.verify(0, key, keyType);
		if (ret == 0){
			 messageManager.AppendInfoMessageInUiThread("verify succ!");
			 return true;
		}else{
			 messageManager.AppendInfoMessageInUiThread("verify faild!");
			 return false;
		}
    }
    
    void testMemoryCard(){
		byte[] pvalue = new byte[32];
		int ret = mSmartCardReader.read(0, SmartCardReader.MemoryCardArea.MAIN, 0, pvalue);
		if (ret > 0){
			messageManager.AppendInfoMessageInUiThread("read main memory protected:"  + HexDump.toHexString(pvalue));
		}else{
			messageManager.AppendInfoMessageInUiThread("read main memory protected faild!");
		}
		
		byte[] data = new byte[16];
		ret = mSmartCardReader.read(0, SmartCardReader.MemoryCardArea.MAIN, 32, data);
		if (ret > 0){
			messageManager.AppendInfoMessageInUiThread("read main memory:"  + HexDump.toHexString(data, 0, ret));
		}else{
			 messageManager.AppendInfoMessageInUiThread("read main memory faild!");
		}
		byte[] pdata = new byte[8];
		ret = mSmartCardReader.read(0, SmartCardReader.MemoryCardArea.PROTECTED, 0, pdata);
		if (ret > 0){
			 messageManager.AppendInfoMessageInUiThread("read protected memory:"  + HexDump.toHexString(pdata, 0, ret));
		}else{
			 messageManager.AppendInfoMessageInUiThread("read protected memory faild!");
		}
		
		int count = getPassCount();
		if (count >= 0){
			 messageManager.AppendInfoMessageInUiThread("get pin check times:" + count);
		}else{
			 messageManager.AppendInfoMessageInUiThread("get pin check times fail��");
		}
		
		if (count == 1){
			messageManager.AppendInfoMessageInUiThread("the last check pin");
			return;
		}else if (count == 0){
			messageManager.AppendInfoMessageInUiThread("the check pin times is over");
		}
		
		byte[] key = HexDump.hexStringToByteArray("FFFFFF");
		ret = mSmartCardReader.verify(0, key, SmartCardReader.MemoryKeyType.MAIN);
		if (ret == 0){
			 messageManager.AppendInfoMessageInUiThread("verify succ!");
			
			writeData();
			
			writeProtectData();
			
		}else{
			 messageManager.AppendInfoMessageInUiThread("verify faild!");
			return;
		}
		
		ret = mSmartCardReader.read(0, SmartCardReader.MemoryCardArea.MAIN, 32, data);
		if (ret > 0){
			 messageManager.AppendInfoMessageInUiThread("read main memory:" +  HexDump.toHexString(data, 0, ret));
		}else{
			 messageManager.AppendInfoMessageInUiThread("read main memory faild!");
		}
		
		byte[] pass = HexDump.hexStringToByteArray("FFFFFF");
		ret = mSmartCardReader.write(0, SmartCardReader.MemoryCardArea.SECURITY, 1, pass);
		if (ret >= 0){
			messageManager.AppendInfoMessageInUiThread("write pass succ!");
		}else{
			messageManager.AppendInfoMessageInUiThread("write pass faild!");
		}
    }
    
	
	/**
	 * @param
	 * @return
	 */
	private int getPassCount() {
		int ret;
		byte[] count = new byte[1];
		ret = mSmartCardReader.read(0, SmartCardReader.MemoryCardArea.SECURITY, 0, count);
		if (ret > 0){
			System.out.println("read:" + HexDump.toHexString(count));
			if ((count[0] & 0x07) == 0x07){
				return 3;
			}else if ((count[0] & 0x03) == 0x03){
				return 2;
			}else if ((count[0] & 0x01) == 0x01){
				return 1;
			}else{
				return 0;
			}
		}else{
			messageManager.AppendInfoMessageInUiThread("read faild!");
		}
		return -1;
	}

	/**
	 */
	private void writeData() {
		int ret;
		String msg = "hello:" + System.currentTimeMillis();
		ret = mSmartCardReader.write(0, SmartCardReader.MemoryCardArea.MAIN, 32, msg.getBytes());
		if (ret >= 0){
			messageManager.AppendInfoMessageInUiThread("write succ!");
		}else{
			messageManager.AppendInfoMessageInUiThread("write faild!");
		}
	}

	/**
	 */
	private void writeProtectData() {
		int ret;
		byte[] ppp = new byte[1];
		ret = mSmartCardReader.read(0, SmartCardReader.MemoryCardArea.MAIN, 5, ppp);
		if (ret > 0){
			messageManager.AppendInfoMessageInUiThread("�����洢��:"  + HexDump.toHexString(ppp));
		}else{
			messageManager.AppendInfoMessageInUiThread("�����洢��ʧ��!");
		}
		ret = mSmartCardReader.write(0, SmartCardReader.MemoryCardArea.PROTECTED, 5, ppp);
		if (ret >= 0){
			messageManager.AppendInfoMessageInUiThread("д�������ɹ�!");
		}else{
			messageManager.AppendInfoMessageInUiThread("д������ʧ��!");
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
    
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mag_open: {
                	// if don't want to get card remove notice can using mSmartCardReader.open(0, null);
                    int nret = mSmartCardReader.open(0,
                                        new SmartCardReader.SCReaderListener(){
                                            @Override
                                            public void notify(
                                                    int nSlotIndex,
                                                    int nEvent) {
                                                if (nEvent == SmartCardReader.EVENT_SMARTCARD_NOT_READY) {
                                                    messageManager.AppendInfoMessageInUiThread(
                                                            shower.getString(R.string.msg_card_unplug_card), Color.RED);
                                                }else if (nEvent == SmartCardReader.EVENT_SMARTCARD_READY){
                                                	
                                                }
                                            }
                                        });
	                    if (nret >= 0) {
	                        messageManager
	                                .AppendInfoMessage(shower.getString(R.string.msg_card_open_success));
	                        messageManager
	                                .AppendInfoMessage(
	                                        shower.getString(R.string.msg_card_wait_for_card),
	                                        Color.GREEN);
	        				SmartCardSlotInfo cardSlotInfo = new SmartCardSlotInfo();
	        				cardSlotInfo.power = mVolt;
	        				mSmartCardReader.setSlotInfo(0, cardSlotInfo);
	                        new Thread(){
	                        	@Override
	                        	public void run() {
	                        		testSmartCardReader();
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
                    
                case R.id.ic_set_slot:
                	setConfig(ICCardActivity.this);
                	break;
                    
                case R.id.mag_close: {
                    int ret = mSmartCardReader
                                .close();
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
    
	private RadioButton radioMode_1_8, radioMode_3, radioMode_5;
	private void setConfig(final Context context) {
		int txtSize=200;
		LinearLayout config=new LinearLayout(context);
		config.setBackgroundColor(Color.WHITE);
		config.setOrientation(LinearLayout.VERTICAL);
		LinearLayout scanModeLayout =new LinearLayout(context);
		TextView txtMode =new TextView(context);
		txtMode.setText("������ѹ:");
		txtMode.setWidth(txtSize);
		config.addView(txtMode);
		RadioGroup group = new RadioGroup(context);
		
		radioMode_1_8 = new RadioButton(context);
		radioMode_1_8.setText("1.8V");
		radioMode_1_8.setWidth(txtSize);
		group.addView(radioMode_1_8);
		
		radioMode_3 = new RadioButton(context);
		radioMode_3.setText("3V");
		radioMode_3.setWidth(txtSize);
		group.addView(radioMode_3);
		
		radioMode_5 = new RadioButton(context);
		radioMode_5.setText("5V");
		radioMode_5.setWidth(txtSize);
		group.addView(radioMode_5);
		scanModeLayout.addView(group);
		
		config.addView(scanModeLayout);
		
		if (mVolt == SmartCardReader.SlotVolt.MODE_1_8_VOLT){
			radioMode_1_8.setChecked(true);
		}else if (mVolt == SmartCardReader.SlotVolt.MODE_3_VOLT){
			radioMode_3.setChecked(true);
		}else if (mVolt == SmartCardReader.SlotVolt.MODE_5_VOLT){
			radioMode_5.setChecked(true);
		}
		
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setTitle("���ÿ��۲���");
		builder.setView(config);
		builder.setNegativeButton("ȡ��", null);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (radioMode_1_8.isChecked()){
					mVolt = SmartCardReader.SlotVolt.MODE_1_8_VOLT;
				}else if (radioMode_3.isChecked()){
					mVolt = SmartCardReader.SlotVolt.MODE_3_VOLT;
				}else if (radioMode_5.isChecked()){
					mVolt = SmartCardReader.SlotVolt.MODE_5_VOLT;
				}
			}
		});
		builder.show();
	}

}
