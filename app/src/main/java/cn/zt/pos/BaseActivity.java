package cn.zt.pos;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BaseActivity extends Activity {
    public MainApplication  mainApplication;
    private LocalBroadcastManager mLocalBroadcastManager;
    private ServiceEventBroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainApplication = (MainApplication) getApplication();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mBroadcastReceiver = new ServiceEventBroadcastReceiver();
    }
    public void onServiceConnected() {

    }

    public void onServiceDisconnected() {

    }

    private class ServiceEventBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MainApplication.ACTION_SERVICE_CONNECTED)) {
                BaseActivity.this.onServiceConnected();
            } else if (action
                    .equals(MainApplication.ACTION_SERVICE_DISCONNECTED)) {
                BaseActivity.this.onServiceDisconnected();
            }
        }
    }

    @Override
    protected void onResume() {
        IntentFilter ifIntentFilter = new IntentFilter();
        ifIntentFilter.addAction(MainApplication.ACTION_SERVICE_CONNECTED);
        ifIntentFilter.addAction(MainApplication.ACTION_SERVICE_DISCONNECTED);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
                ifIntentFilter);
        super.onResume();
    }

    @Override
    protected void onStop() {
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }
}
