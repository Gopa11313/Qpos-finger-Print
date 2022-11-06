package cn.zt.pos.test;

import com.szzt.sdk.device.Device;
import com.szzt.sdk.device.card.SmartCardReader;

import android.os.Bundle;
import android.os.HandlerThread;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.utils.MessageManager;
import cn.zt.pos.utils.Shower;

public class PsamCardActivity extends BaseActivity {
	private SmartCardReader mSmartCardReader;
	private Button mOpenButton, mOpen2Button;
	private Button mClearButton;
	private Button mCloseButton;
	private TextView mTextViewResult;
	MessageManager messageManager;
	HandlerThread mWaitForCardHandlerThread;
	int mTestDeviceType = Device.TYPE_UNKNOWN;
	private Shower shower;

	private int SlotIndex1 = 1;

	private int SlotIndex2 = 2;

	private int SUCCESS = 1;

	private int FAIL = -1;

	byte apduSend[] = { 0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0xDF, 0x01 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_psamcard);
		shower = new Shower(this);
		boolean isConnected = mainApplication.isDeviceManagerConnetcted();
		init(isConnected);
		if (messageManager != null) {
			messageManager.AppendInfoMessage(R.string.test_psamcardreader);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mSmartCardReader != null) {
			mSmartCardReader.close();
		}
	}

	private void init(boolean isConnected) {
		if (mCloseButton == null) {
			mTextViewResult = (TextView) findViewById(R.id.mag_text);
			mTextViewResult.setMovementMethod(ScrollingMovementMethod.getInstance());

			messageManager = new MessageManager(this, mTextViewResult);

			mOpenButton = (Button) findViewById(R.id.psam1_open);
			mOpenButton.setOnClickListener(listener);

			mOpen2Button = (Button) findViewById(R.id.psam2_open);
			mOpen2Button.setOnClickListener(listener);

			mClearButton = (Button) findViewById(R.id.psam_clear);
			mClearButton.setOnClickListener(listener);

		}
		mTextViewResult.setEnabled(isConnected);
		mOpenButton.setEnabled(isConnected);
		mOpen2Button.setEnabled(isConnected);
		mClearButton.setEnabled(isConnected);
		if (isConnected) {
			mSmartCardReader = mainApplication.getSmartCardReader();
		} else {
			mSmartCardReader = null;
		}
	}

	public byte[] exchangeApdu(int mSlotIndex, byte[] apdu) {
		// TODO Auto-generated method stub
		byte[] apduResp = new byte[512];
		int mRet = mSmartCardReader.transmit(mSlotIndex, apdu, apduResp);
		if (mRet > 0) {
			byte[] apduRespTemp = new byte[mRet];
			System.arraycopy(apduResp, 0, apduRespTemp, 0, mRet);
			return apduRespTemp;
		}
		return null;
	}

	/**
	 * @param buffer
	 * @return
	 */
	public String byte2hex(byte buffer) {
		String h = "";

		String temp = Integer.toHexString(buffer & 0xFF);
		if (temp.length() == 1) {
			temp = "0" + temp;
		}
		h = h + "" + temp;

		return h;
	}

	/**
	 * @param buffer
	 * @return
	 */
	public String bytes2hex(byte[] buffer) {
		String h = "";

		for (int i = 0; i < buffer.length; i++) {
			String temp = Integer.toHexString(buffer[i] & 0xFF);
			if (temp.length() == 1) {
				temp = "0" + temp;
			}
			h = h + "  " + temp;
		}

		return h;
	}


	public int open(int mSlotIndex) {
		if (mSmartCardReader == null) {
			mSmartCardReader = mainApplication.getSmartCardReader();
			if (mSmartCardReader == null) {
				mSmartCardReader = mainApplication.getSmartCardReader();
				return FAIL;
			}
			return FAIL;
		}

		int mRet = mSmartCardReader.open(mSlotIndex, null);

		if (mRet >= 0)
			return SUCCESS;

		return FAIL;
	}

	private void testPsamCardReader(int slotIndex) {
		byte[] apdu = new byte[64];
		int mRet = mSmartCardReader.powerOn(slotIndex, apdu);
		messageManager.AppendInfoMessageInUiThread("POWER ON:"
				+ (mRet > 0 ? SUCCESS : FAIL) + ",slotIndex=" + slotIndex);
		if (mRet >= 0) {
			byte[] response = exchangeApdu(slotIndex, apduSend);
			if (response != null) {
				messageManager.AppendInfoMessageInUiThread("PSAM" + slotIndex
						+ ": apdu response  (len " + response.length + "):");
				messageManager.AppendInfoMessageInUiThread(bytes2hex(response));
			}
			mSmartCardReader.powerOff(slotIndex);
		}
		mSmartCardReader.close(slotIndex);
		messageManager.AppendInfoMessage(shower
				.getString(R.string.btn_psamcard_close));

	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.psam1_open: {
				int nret = open(SlotIndex1);
				if (nret >= 0) {
					messageManager.AppendInfoMessage(shower
							.getString(R.string.msg_card_open_success));

					testPsamCardReader(SlotIndex1);

				} else {
					messageManager.AppendInfoMessage(shower
							.getString(R.string.msg_card_open_error));
				}
			}
				break;
			case R.id.psam2_open: {
				int nret = open(SlotIndex2);
				if (nret >= 0) {
					messageManager.AppendInfoMessage(shower
							.getString(R.string.msg_card_open_success));

					testPsamCardReader(SlotIndex2);

				} else {
					messageManager.AppendInfoMessage(shower
							.getString(R.string.msg_card_open_error));
				}
			}
				break;
				
			case R.id.psam_clear:
				messageManager.clear();
				break;
			default:
				break;
			}
		}
	};
}
