package cn.zt.pos.test;
import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.utils.MessageManager;
import cn.zt.pos.utils.StringUtility;
import com.szzt.sdk.device.Constants.Error;
import com.szzt.sdk.device.card.ContactlessCardReader;
import com.szzt.sdk.device.card.MagneticStripeCardReader;
import com.szzt.sdk.device.card.SmartCardReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ReadCardActivity extends BaseActivity {
	private MessageManager      mMessageManager;	
    private Button              mReadButton;
    private Button              mClearButton;
    private Button 				mCloseButton;
    private static final int    CARD_SHOW_MESSAGE=11;
    private static final int    CARD_READ_FINISH=13;
    private boolean             IsRead=false;
    private SmartCardReader     mContactCard;
    private ContactlessCardReader mContactlessCard;
    private MagneticStripeCardReader mMagCard;
    private boolean[] mTypes = new boolean[]{ true, false, false, false, false, false, true, true, false };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_card);
		boolean isConnected = mainApplication.isDeviceManagerConnetcted();
		init(isConnected);
	}
	@Override
	protected void onDestroy() {
		if(mMagCard!=null){
			mMagCard.close();
			mMagCard=null;
		}
		if(mContactlessCard!=null){
			mContactlessCard.close();
			mContactlessCard=null;
		}
		if(mContactCard!=null){
			mContactCard.close(0);
			mContactCard=null;
		}
		super.onDestroy();
	}
	private void init(boolean isConnected) {
		TextView mTextView=(TextView) findViewById(R.id.mag_text);
		mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		mMessageManager=new MessageManager(this, mTextView);
		mMessageManager.InfoMessage(getString(R.string.info_card_test)+"\n");
		mClearButton=(Button) findViewById(R.id.mag_clear);
		mClearButton.setOnClickListener(mClick);
		mReadButton=(Button) findViewById(R.id.mag_open);
		mReadButton.setOnClickListener(mClick);
		mReadButton.setEnabled(isConnected);
		mCloseButton = (Button) findViewById(R.id.mag_close);
		mCloseButton.setOnClickListener(mClick);
		mMagCard=mainApplication.getMagneticStripeCardReader();
		mContactCard=mainApplication.getSmartCardReader();
		mContactlessCard=mainApplication.getContactlessCardReader();
	}
    private View.OnClickListener mClick=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.mag_clear:
				mMessageManager.clear();
				break;
			case R.id.mag_open:
				CardRead();
				break;
			case R.id.mag_close:
				boolean ret = false;
				ret = mMagCard.close() >=0;
				ret = mContactlessCard.close()>=0;
				ret = mContactCard.close()>=0;
				IsRead = true;
				if(ret)
					mMessageManager.AppendInfoMessageInUiThread(getString(R.string.info_readcard_stop_test)+getString(R.string.info_success));
				else
					mMessageManager.AppendInfoMessageInUiThread(getString(R.string.info_readcard_stop_test)+getString(R.string.info_failed));
				break;
			default:
				break;
			}
		}
	};
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			Bundle mBundle=(Bundle) msg.obj;  
			switch (msg.what) {		
			case CARD_SHOW_MESSAGE:
				if(!mBundle.getBoolean("isSuccess")){
					mMessageManager.AppendInfoMessageInUiThread(mBundle.getString("showMessage"),Color.RED);
				}
				else{
					mMessageManager.AppendInfoMessageInUiThread(mBundle.getString("showMessage"));
				}
				break;
			case CARD_READ_FINISH:
				IsRead=false;
				mMessageManager.AppendInfoMessageInUiThread("Card Read Finish",Color.RED);
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						CardRead();
					}
				}, 3000);				
				break;
			default:
				break;
			}
		}
	};
 
	protected void CardRead() {
		IsRead = false;
		if(!IsRead){
			IsRead=true;
			if(mTypes[0])SmartOpen(0);
			if(mTypes[1])SmartOpen(1);  
			if(mTypes[2])SmartOpen(2);
			if(mTypes[3])SmartOpen(3);
			if(mTypes[4])SmartOpen(4);
			if(mTypes[5])SmartOpen(5);
			if(mTypes[6])ContactlessOpen(6);
			if(mTypes[7])MagOpen(7);
		}
	}
	/**
	 */
	private void MagOpen(final int index) { 
		if(mMagCard==null){
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {				
				int ret=mMagCard.open();
				if(ret>=0){
					sendMessage(CARD_SHOW_MESSAGE, getString(R.string.info_card_mag)+"-->"+getString(R.string.info_mag_waitforcard), true);
					ret=mMagCard.waitForCard(100000);					
					if(ret==0){
						mainApplication.playWav(R.raw.success_tone,1);
						sendMessage(CARD_SHOW_MESSAGE, getString(R.string.info_card_mag)+"-->"+getString(R.string.info_waitforcard_success), true);
						CardCloseAll(index);
						getTrackData(MagneticStripeCardReader.TRACK_INDEX_1);
						getTrackData(MagneticStripeCardReader.TRACK_INDEX_2);
						getTrackData(MagneticStripeCardReader.TRACK_INDEX_3);
						MagClose();
						sendMessage(CARD_READ_FINISH, null, false);
					}
					else if(ret==Error.TIMEOUT){
						sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_mag)+"-->"+getString(R.string.info_waitforcard_timeout),false);
						MagClose();
						sendMessage(CARD_READ_FINISH, null, false);
					}
				}					
				else
					sendMessage(CARD_SHOW_MESSAGE, getString(R.string.info_card_mag)+"-->"+getString(R.string.info_open_device_failed), false);
				
				
			}
		}).start();
	}
	protected void MagClose() {
		int ret=mMagCard.close();
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_mag)+getString(R.string.info_close_device_success),true);
		}
		else
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_mag)+getString(R.string.info_close_device_failed),false);
	}
	//�ر����еĿ����豸
	protected void CardCloseAll(int index) {
		if(mTypes[0]&&index!=0)SmartClose(0);
		if(mTypes[1]&&index!=1)SmartClose(1);
		if(mTypes[2]&&index!=2)SmartClose(2);
		if(mTypes[3]&&index!=3)SmartClose(3);
		if(mTypes[4]&&index!=4)SmartClose(4);
		if(mTypes[5]&&index!=5)SmartClose(5);
		if(mTypes[6]&&index!=6)ContactlessClose();
		if(mTypes[7]&&index!=7)MagClose();	
	}
	private void SmartClose(int nSlotIndex) {	
		int ret=mContactCard.close(nSlotIndex);
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_contact)+getString(R.string.info_close_device_success),true);
		}
		else
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_contact)+getString(R.string.info_close_device_failed),false);
		
	}
	//��ôŵ�����
	private String getTrackData(int nTrackIndex) {
		sendMessage(CARD_SHOW_MESSAGE,"trackIndex-->"+nTrackIndex,false);
		byte[] ret=mMagCard.getTrackData(nTrackIndex);
		sendMessage(CARD_SHOW_MESSAGE, "trackData-->"+(ret==null?"":new String(ret)), true);
		return ret==null?null:new String(ret).trim();
	}
	//�ǽӿ�
	private void ContactlessOpen(final int index) {
		if(mContactlessCard==null){
			return;
		}
		new Thread(new Runnable() {			
			@Override
			public void run() {
				
				int ret=mContactlessCard.open();
				if(ret>=0){
					sendMessage(CARD_SHOW_MESSAGE, getString(R.string.info_card_contactless)+"-->"+getString(R.string.info_contactless_waitforcard), true);
					ret=mContactlessCard.waitForCard(100000);
					if(ret==0){
						mainApplication.playWav(R.raw.success_tone,1);
						CardCloseAll(index);
						sendMessage(CARD_SHOW_MESSAGE, getString(R.string.info_card_contactless)+"-->"+getString(R.string.info_waitforcard_success), true);
						int nCardType=mContactlessCard.getCardType();
						if (nCardType == 0x0000 || nCardType == 0x0100) {
							sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_cpu), true);
							ContactlessPowerOn();
		                } else if (nCardType == 0x0002 || nCardType == 0x0003) {
		                	sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_mifare), true);
		                	ContactlessVerifyPin("FFFFFFFFFFFF");
		                } else {
		                	ContactlessPowerOn();
		                }
					}
					else if(ret==Error.TIMEOUT){
						sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_contactless)+"-->"+getString(R.string.info_waitforcard_timeout),false);
						ContactlessClose();
						sendMessage(CARD_READ_FINISH, null, false);
					}
				}
				else
					sendMessage(CARD_SHOW_MESSAGE, getString(R.string.info_card_contactless)+"-->"+getString(R.string.info_open_device_failed), false);
				
			}
		}).start();
		
	}
	protected void ContactlessVerifyPin(String str) {
		byte[] mPin=StringUtility.StringToByteArray(str);
		int ret=mContactlessCard.verifyPinMifare(0x00, 0x0A, mPin);
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_contactless_pin_check)+getString(R.string.info_success),true);
			ContactlessWrite("0102030405060708090a0b0c0d0e0f");
		}
		else
		{
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_contactless_pin_check)+getString(R.string.info_failed),false);
			sendMessage(CARD_READ_FINISH, null, false);
			ContactlessClose();
		}
	}
	private void ContactlessWrite(String str) {
		byte[] data=StringUtility.StringToByteArray(str);
		int ret=mContactlessCard.writeMifare(0x00, 0x01, data);
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_contactless_write)+getString(R.string.info_success),true);
			ContactlessRead();
		}
		else
		{
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_contactless_write)+getString(R.string.info_failed),false);
			sendMessage(CARD_READ_FINISH, null, false);
			ContactlessClose();
		}
	}
	private void ContactlessRead() {
		byte[] result=new byte[256];
		int ret=mContactlessCard.readMifare(0x00, 0x01, result);
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_contactless_read)+getString(R.string.info_success),true);
			sendMessage(CARD_SHOW_MESSAGE,"return data-->"+StringUtility.ByteArrayToString(result, ret),true);
			ContactlessClose();
			sendMessage(CARD_READ_FINISH, null, false);
		}
		else
		{
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_contactless_read)+getString(R.string.info_failed),false);
			sendMessage(CARD_READ_FINISH, null, false);
			ContactlessClose();
		}
	}
	protected void ContactlessPowerOn() {
		byte[] atr=new byte[256];
		int ret=mContactlessCard.powerOn(atr);
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_poweron)+getString(R.string.info_success),true);
			sendMessage(CARD_SHOW_MESSAGE,"PowerOn Atr-->"+StringUtility.ByteArrayToString(atr, ret),true);
			ContactLessApdu("00A404000E315041592E5359532E4444463031");
		}
		else
		{
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_poweron)+getString(R.string.info_failed),false);
			sendMessage(CARD_READ_FINISH, null, false);
			ContactlessClose();
		}
	}
	private void ContactLessApdu(String apdu) {
		byte[] mApdu=StringUtility.StringToByteArray(apdu);
		byte[] result=new byte[256];
		int ret=mContactlessCard.transmit( mApdu, result);
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_apdu)+getString(R.string.info_success),true);
			sendMessage(CARD_SHOW_MESSAGE,"Apdu Return-->"+StringUtility.ByteArrayToString(result, ret),true);
			ContactlessPowerOff();
		}
		else
		{
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_apdu)+getString(R.string.info_failed),false);
			sendMessage(CARD_READ_FINISH, null, false);
			ContactlessClose();
		}
	}
	private void ContactlessPowerOff() {
		int ret=mContactlessCard.powerOff();
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_poweroff)+getString(R.string.info_success),true);
		}
		else
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_poweroff)+getString(R.string.info_failed),false);
		ContactlessClose();
		sendMessage(CARD_READ_FINISH, null, false);
		
	}
	private void ContactlessClose() {
		int ret=mContactlessCard.close();
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_contactless)+getString(R.string.info_close_device_success),true);
		}
		else
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_contactless)+getString(R.string.info_close_device_failed),false);
		
	}
	private void SmartOpen(final int nSlotIndex) {
		if(mContactCard==null){
			return;
		}
		int ret=mContactCard.open(nSlotIndex, new SmartCardReader.SCReaderListener() {
			@Override
			public void notify(int nSlotIndex, int nEvent) {				 
				if(nEvent==SmartCardReader.EVENT_SMARTCARD_NOT_READY){
					sendMessage(CARD_SHOW_MESSAGE, getString(R.string.info_contact_card_remove), false);
				}
			}
		});
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_contact_waitforcard), true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					int retCode=mContactCard.waitForCard(nSlotIndex, 100000);
					if(retCode==0){
						mainApplication.playWav(R.raw.success_tone,1);
						CardCloseAll(nSlotIndex);
						sendMessage(CARD_SHOW_MESSAGE, getString(R.string.info_card_contact)+"-->"+nSlotIndex,true);
						sendMessage(CARD_SHOW_MESSAGE, getString(R.string.info_waitforcard_success),true);
						ContactPowerOn(nSlotIndex);
					}
					else if(retCode==Error.TIMEOUT){
						sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_waitforcard_timeout),false);
						SmartClose(nSlotIndex);
						sendMessage(CARD_READ_FINISH, null, false);
					}
				}
			}).start();
		}
		else{			
			mMessageManager.AppendInfoMessage(getString(R.string.info_open_device_failed),Color.RED);
		}
	}
	
	protected void ContactPowerOn(int nSlotIndex) {
		byte[] atr=new byte[256];
		int ret=mContactCard.powerOn(nSlotIndex, atr);
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_poweron)+getString(R.string.info_success),true);
			sendMessage(CARD_SHOW_MESSAGE,"PowerOn Atr-->"+StringUtility.ByteArrayToString(atr, ret),true);
			ContactApdu(nSlotIndex,"00A404000E315041592E5359532E44444630310000");
		}
		else
		{
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_poweron)+getString(R.string.info_failed),false);
			sendMessage(CARD_READ_FINISH, null, false);
			SmartClose(nSlotIndex);
			
		}
	}
	private void ContactApdu(int nSlotIndex,String apdu) {
		byte[] mApdu=StringUtility.StringToByteArray(apdu);
		byte[] result=new byte[256];
		int ret=mContactCard.transmit(nSlotIndex, mApdu, result);
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_apdu)+getString(R.string.info_success),true);
			sendMessage(CARD_SHOW_MESSAGE,"Apdu Return-->"+StringUtility.ByteArrayToString(result, ret),true);
			ContactPowerOff(nSlotIndex);
		}
		else
		{
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_apdu)+getString(R.string.info_failed),false);
			sendMessage(CARD_READ_FINISH, null, false);
			SmartClose(nSlotIndex);
		}
	}
	private void ContactPowerOff(int nSlotIndex) {
		int ret=mContactCard.powerOff(nSlotIndex);
		if(ret>=0){
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_poweroff)+getString(R.string.info_success),true);
		}
		else
			sendMessage(CARD_SHOW_MESSAGE,getString(R.string.info_card_poweroff)+getString(R.string.info_failed),false);
		SmartClose(nSlotIndex);
		sendMessage(CARD_READ_FINISH, null, false);
	}
	private void sendMessage(int mWhat, String mStr,boolean isSuccess) {
		Message msg= new Message();
		msg.what=mWhat;
		Bundle b=new Bundle();
		b.putString("showMessage", mStr);
		b.putBoolean("isSuccess", isSuccess);
		msg.obj=b;
		mHandler.sendMessage(msg);
	}

}
