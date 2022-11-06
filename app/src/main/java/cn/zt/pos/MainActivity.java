package cn.zt.pos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.szzt.sdk.device.pinpad.PinPad;

import cn.zt.billpay.BillPayActivity;
import cn.zt.pos.activityDailog.SalesService;
import cn.zt.pos.cashinput.CashInputActivity;
import cn.zt.pos.login.LoginActivity;
import cn.zt.pos.utils.KeyImport;
import cn.zt.pos.utils.PrefUtil;
import cn.zt.pos.utils.UtilsLocal;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private PinPad mPinpad;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        setTheme(R.style.AppThemeMainNIC);
    super.onCreate(savedInstanceState);
        PrefUtil.getSharedPreferences(MainActivity.this);
        if (PrefUtil.getLauncherLog().equals("0")){
            startActivity(new Intent(this,LoginActivity.class));finish();
        }
        if (PrefUtil.getLauncherLog().equals("1")){
            boolean isConnected = mainApplication.isDeviceManagerConnetcted();
            if (isConnected){
                mPinpad = mainApplication.getPinPad();
                PinpadOpen();
            }

        }


        setContentView(R.layout.new_activity_main);
        findViewById(R.id.view_sale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Intent intent = new Intent();intent.setClass(getApplicationContext(), LoginActivity.class);startActivity(intent);}});
        findViewById(R.id.view_sale).setOnClickListener(this);
        findViewById(R.id.view_refund).setOnClickListener(this);
        findViewById(R.id.billPayment).setOnClickListener(this);
        //18ABAB4CEB24F2349D2A5BA4A2EFF3F6 ch 51768F
        boolean isConnected = mainApplication.isDeviceManagerConnetcted();
        if (isConnected){
            mPinpad = mainApplication.getPinPad();
            PinpadOpen();
        }

    }
    private void setPinConfig(){
            if (PrefUtil.getLauncherLog().equals("1")){
                PrefUtil.setLauncherLog("2");
                KeyImport.PinpadUpdateMasterKey(mPinpad,MainActivity.this,PrefUtil.getTMK());
                KeyImport.PinpadUpdateUserKey(mPinpad,MainActivity.this,PrefUtil.getEmvPayload(),PrefUtil.getCheckVal().replace(" ","").trim());
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
//                        messageManager.AppendInfoMessage("keyCode:" + keyCode);

                    }
                });
            }
        });
        if (ret >= 0) {

            Toast.makeText(MainActivity.this,getString(R.string.msg_pinpad_open_succ),Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(MainActivity.this,getString(R.string.msg_pinpad_open_error),Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view_sale:
//                KeyImport.PinpadUpdateMasterKey(mPinpad,MainActivity.this,PrefUtil.getTMK());
//                KeyImport.PinpadUpdateUserKey(mPinpad,MainActivity.this,"18ABAB4CEB24F2349D2A5BA4A2EFF3F6","7351B3B1");//51768F
                Log.d("dinesh", "onClick: Master : "+PrefUtil.getTMK()+"  checkTMK: "+PrefUtil.getCheckVal()+" TWK :"+PrefUtil.getEmvPayload());
                setPinConfig();
                startActivity(new Intent(MainActivity.this, CashInputActivity.class));
                break;
            case R.id.view_refund:
                setPinConfig();
                startActivity(new Intent(MainActivity.this, NewMainActivity.class));
                break;
            case R.id.billPayment:
                startActivity(new Intent(MainActivity.this, BillPayActivity.class));

                break;
        }
    }

    @Override



    protected void onStart() {
        super.onStart();
        Intent i = new Intent(MainActivity.this, SalesService.class);
        stopService(i);
        startService(i);
    }
}
