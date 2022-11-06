package cn.zt.pos;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.zt.pos.test.EMVActivity;
import cn.zt.pos.test.ICCardActivity;
import cn.zt.pos.test.MagCardActivity;
import cn.zt.pos.test.PinPadActivity;
import cn.zt.pos.test.PrinterActivity;
import cn.zt.pos.test.PsamCardActivity;
import cn.zt.pos.test.RFCardActivity;
import cn.zt.pos.test.ScanActivity;
import cn.zt.pos.test.SystemManagerActivity;
public class NewMainActivity extends BaseActivity {
	private LinearLayout lin_citiao,lin_psam,lin_pinpad,
			lin_printer, lin_emv, lin_camera, lin_exit, lin_all,
			lin_cantact,lin_cantactless,lin_idcard,lin_serial,lin_SystemManager,lin_HWsecurity;
	private ImageView img_touch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_new_main);
		MainApplication mainApplication = (MainApplication) getApplication();
		boolean isConnected = mainApplication.isDeviceManagerConnetcted();
		initView(isConnected);
	}

	private void initView(boolean isConnect) {
		lin_all = findViewById(R.id.lin_all);
		lin_all.setEnabled(isConnect);
		lin_citiao = findViewById(R.id.lin_citiao);
		setBackShapeNo(lin_citiao, "#6495ED");
		lin_citiao.setOnClickListener(mClick);
		lin_psam = findViewById(R.id.lin_psam);
		setBackShapeNo(lin_psam, "#6495ED");
		lin_psam.setOnClickListener(mClick);
		lin_cantact = findViewById(R.id.lin_cantact);
		setBackShapeNo(lin_cantact, "#63B8FF");
		lin_cantact.setOnClickListener(mClick);
		lin_cantactless = findViewById(R.id.lin_cantactless);
		setBackShapeNo(lin_cantactless, "#63B8FF");
		lin_cantactless.setOnClickListener(mClick);
		lin_pinpad = findViewById(R.id.lin_pinpad);
		setBackShapeNo(lin_pinpad, "#6495ED");
		lin_pinpad.setOnClickListener(mClick);
		lin_printer = findViewById(R.id.lin_printer);
		setBackShapeNo(lin_printer, "#6495ED");
		lin_printer.setOnClickListener(mClick);
		lin_emv = findViewById(R.id.lin_emv);
		setBackShapeNo(lin_emv, "#63B8FF");
		lin_emv.setOnClickListener(mClick);

		lin_camera = findViewById(R.id.lin_camera);
		setBackShapeNo(lin_camera, "#63B8FF");
		lin_camera.setOnClickListener(mClick);
		
		lin_SystemManager = findViewById(R.id.lin_SystemManager);
		setBackShapeNo(lin_SystemManager, "#63B8FF");
		lin_SystemManager.setOnClickListener(mClick);

		
		lin_exit = findViewById(R.id.lin_exit);
		setBackShapeNo(lin_exit, "#9370DB");
		lin_exit.setOnClickListener(mClick);
		
		img_touch = findViewById(R.id.img_touch);
		img_touch.setOnClickListener(mfullClick);
	}

	/**
	 * ???????6??????????????
	 */
	private EditText editText;
	private android.view.View.OnClickListener mfullClick = new View.OnClickListener() {
		long[] mHints = new long[6];
		@Override
		public void onClick(View arg0) {
			System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
			mHints[mHints.length - 1] = SystemClock.uptimeMillis();
			if (SystemClock.uptimeMillis() - mHints[0] <= 2000) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						NewMainActivity.this);
				builder.setTitle(getString(R.string.chooice_config_item));
				builder.setItems(R.array.touchConfig, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int which) {
						arg0.dismiss();
						if (which == 0) {
							printerConfig();
						}
					}
				});
				builder.setNegativeButton(getString(R.string.input_loop_index_cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.cancel();
							}
						});
				AlertDialog dialog = builder.create();
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}

		}
	};
	/**
	 * ???????????????????
	 */
	private void printerConfig() {
		editText  = new EditText(NewMainActivity.this);
    	AlertDialog.Builder builder = new AlertDialog.Builder(NewMainActivity.this);
    	builder.setTitle(getString(R.string.input_loop_index));
    	builder.setView(editText);
    	builder.setNegativeButton(getString(R.string.input_loop_index_cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
    	builder.setPositiveButton(getString(R.string.input_loop_index_submit), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				MainApplication.printIndex = Integer.valueOf(editText.getText().toString().trim());
				arg0.dismiss();
			}
		});
    	AlertDialog alertDialog = builder.create();
    	alertDialog.show();
	}
	private OnClickListener mClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.lin_citiao:
				intent = new Intent();
				intent.setClass(getApplicationContext(), MagCardActivity.class);
				break;
			case R.id.lin_psam:
				intent = new Intent();
				intent.setClass(getApplicationContext(), PsamCardActivity.class);
				break;
			case R.id.lin_pinpad:
				intent = new Intent();
				intent.setClass(getApplicationContext(), PinPadActivity.class);
				break;
			case R.id.lin_printer:
				intent = new Intent();
				intent.setClass(getApplicationContext(), PrinterActivity.class);
				break;
			case R.id.lin_emv:
				intent = new Intent();
				intent.setClass(getApplicationContext(), EMVActivity.class);
				break;
			case R.id.lin_camera:
				intent = new Intent();
				intent.setClass(getApplicationContext(), ScanActivity.class);
				break;
			case R.id.lin_cantact:
				intent = new Intent();
				intent.setClass(getApplicationContext(), ICCardActivity.class);
				break;
			case R.id.lin_cantactless:
				intent = new Intent();
				intent.setClass(getApplicationContext(), RFCardActivity.class);
				break;
			case R.id.lin_SystemManager:
				intent = new Intent();
				intent.setClass(getApplicationContext(), SystemManagerActivity.class);
				break;
			case R.id.lin_exit:
				System.exit(0);
				System.gc();
				break;
			}

			if (intent != null) {
				startActivity(intent);
			}
		}
	};
	@SuppressLint("NewApi")
	private static void setBackShapeNo(View view, String color) {
		int roundRadius = 6; 
		int fillColor = Color.parseColor(color); 
		GradientDrawable gd = new GradientDrawable(); 
		gd.setColor(fillColor);
		gd.setCornerRadius(roundRadius);
		view.setBackground(gd);
	}

	@Override
	public void onServiceConnected() {
		initView(true);
		super.onServiceConnected();
	}

	@Override
	public void onServiceDisconnected() {
		initView(false);
		super.onServiceDisconnected();
	}

}
