package cn.zt.testfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import cn.zt.pos.BaseActivity;
import cn.zt.pos.MainApplication;
import cn.zt.pos.R;
import cn.zt.pos.test.EMVActivity;
import cn.zt.pos.test.PinPadActivity;
import cn.zt.pos.test.PrinterActivity;
import cn.zt.pos.test.ScanActivity;

import com.szzt.sdk.device.Device;

public class MainActivity extends BaseActivity {

	private ListView mListView = null;
	private List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_main);
		MainApplication mainApplication = (MainApplication) getApplication();
		mListView = (ListView) findViewById(R.id.listview);
		mListView.setAdapter(getAdapter());
		boolean isConnected = mainApplication.isDeviceManagerConnetcted();
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onClick(position);
			}
		});
		init(isConnected);
	}

	public SimpleAdapter getAdapter() {
		String[] arrays = getResources().getStringArray(R.array.test);
		String[] from = { "text" };
		int[] to = { R.id.listview_text };
		for (int i = 0; i < arrays.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", arrays[i]);
			data_list.add(map);
		}
		return new SimpleAdapter(this, data_list, R.layout.list_item, from, to);
	}

	@Override
	public void onServiceConnected() {
		init(true);
		super.onServiceConnected();
	}

	@Override
	public void onServiceDisconnected() {
		init(false);
		super.onServiceDisconnected();
	}

	private void init(boolean isConnected) {
		mListView.setEnabled(isConnected);
	}

	public void onClick(int position) {
		Intent intent = null;
		switch (position) {
		case 0:
		case 1:
		case 2: {
			intent = new Intent();
			intent.setClass(getApplicationContext(), CardActivity.class);
			int type = Device.TYPE_UNKNOWN;
			if (position == 0)
				type = Device.TYPE_MAGSTRIPECARDREADER;
			else if (position == 1)
				type = Device.TYPE_CONTACTLESSCARDREADER;
			else if (position == 2)
				type = Device.TYPE_SMARTCARDREADER;
			intent.putExtra("device-type", type);
		}
			break;
		case 3:
			intent = new Intent();
			intent.setClass(getApplicationContext(), PinPadActivity.class);
			break;
		case 4:
			intent = new Intent();
			intent.setClass(getApplicationContext(), PrinterActivity.class);
			break;
		case 5:
			intent = new Intent();
			intent.setClass(getApplicationContext(), EMVActivity.class);
			break;
		case 6:
			intent = new Intent();
			intent.setClass(getApplicationContext(), ScanActivity.class);
			break;
		case 7:
			System.exit(0);
			System.gc();
			break;
		default:
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
