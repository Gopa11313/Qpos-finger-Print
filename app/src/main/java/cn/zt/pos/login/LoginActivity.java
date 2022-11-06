package cn.zt.pos.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.szzt.sdk.device.Constants;
import com.szzt.sdk.device.barcode.CameraScan;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cn.zt.pos.MainActivity;
import cn.zt.pos.MainApplication;
import cn.zt.pos.R;
import cn.zt.pos.db.TellersDbHelper;
import cn.zt.pos.dlg.ProcessDlg;
import cn.zt.pos.login.loginapi.SoapLoginAPI;
import cn.zt.pos.login.loginapi.SoapLogonAPI;
import cn.zt.pos.utils.LoginInterface;
import cn.zt.pos.utils.PrefUtil;
import cn.zt.pos.utils.TMKEngine;
import cn.zt.pos.utils.UtilsLocal;
import cn.zt.upi.UtilsQRCode;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity implements View.OnClickListener {
    private CameraScan mScan;
    private Handler mHandler=new Handler(Looper.getMainLooper());
    public MainApplication  mainApplication;
    private String termIdMerchant="";
    private EditText termId,pwdEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        findViewById(R.id.scanQr).setOnClickListener(this);
        findViewById(R.id.tellerSignBtn).setOnClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mainApplication = (MainApplication) getApplication();
                boolean isConnected = mainApplication.isDeviceManagerConnetcted();
                mScan = mainApplication.getCameraScanImpl();
            }
        },100);
        termId=findViewById(R.id.tellerNoEdit);
        pwdEdit=findViewById(R.id.pwdEdit);
        PrefUtil.getSharedPreferences(LoginActivity.this);

        PrefUtil.setIP("10.30.3.40");
        PrefUtil.setPort(7788);
//        AppCache.setApplicationMode(AppCache.MODE_NORMAL);
        new TellersDbHelper(LoginActivity.this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scanQr:
                Scan();
                break;
            case R.id.tellerSignBtn:
                findViewById(R.id.tellerSignBtn).setEnabled(false);
                ProcessDlg.ShowProcess(LoginActivity.this, getResources().getString(R.string.btnlogin), "Loging..");
//                new UtilsKeyImport().importKeyTMK(LoginActivity.this,mKey);
                termIdMerchant=termId.getText().toString();

                new SoapLoginAPI(termIdMerchant,pwdEdit.getText().toString(), LoginActivity.this, new LoginInterface() {
                    @Override
                    public void loginSuccess(String s,String qr) {
                        PrefUtil.setPayload(qr);
                        PrefUtil.putTerminalPayload(pwdEdit.getText().toString().trim());
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            PrefUtil.setAppId(jsonObject.getString("app_id"));
                            HashMap<String,String> stringHashMap= UtilsQRCode.getTerminalId(qr);
                            if (PrefUtil.getTerminalNo() == null)
                                PrefUtil.putTerminalNo(UtilsQRCode.getTerminalId(stringHashMap.get("62")).get("07"));
                            if (PrefUtil.getMerchantNo() == null)
                                PrefUtil.putMerchantNo(jsonObject.getString("merch_id"));

                            JSONObject object=jsonObject.getJSONObject("enc_obj");
                            String enc=object.getString("enc_value"),
                                    hm=object.getString("hmac_value");
                            PrefUtil.putMerchantName(jsonObject.getString("merch_name"));

                            try {
                                new TMKEngine(PrefUtil.getTerminalNo(), pwdEdit.getText().toString(), enc, hm, new TMKEngine.TMKEngineCallback() {
                                    @Override
                                    public void onSuccess(String json) {
                                        Log.d("dinesdr", "onSuccess: "+json.toString());
                                        try {
                                            String str= UtilsLocal.convertHexToStringValue(json.toString());
                                            Log.d("dinesdr", "onSuccess: "+str);

                                            JSONObject tmkObject = new JSONObject(str);
                                            PrefUtil.setTMK(tmkObject.getString("PosKey"));
                                            PrefUtil.setCheckVal(tmkObject.getString("CheckValue"));

                                        } catch (Exception e) {
                                           e.printStackTrace();

                                        }

                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        findViewById(R.id.tellerSignBtn).setEnabled(true);


                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                            new SoapLogonAPI(PrefUtil.getTerminalNo(), PrefUtil.getMerchantNo(), LoginActivity.this, new LoginInterface() {
                                @Override
                                public void loginSuccess(String s,String s1) {
                                    Log.d(TAG, "loginSuccess: " + s);
                                    ProcessDlg.CloseDlg();
//                                    PrefUtil.setTMK(s);
                                    PrefUtil.setLauncherLog("1");
                                    PrefUtil.setTxnType("login");
                                    PrefUtil.setEmvPayload(s);
                                    PrefUtil.setOperatID("01");
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    finish();

                                }

                                @Override
                                public void loginFailed() {
                                    Log.d(TAG, "loginFailed: " );
                                    ProcessDlg.ShowBtnOK(LoginActivity.this, getResources().getString(R.string.btnlogin), getResources().getString(R.string.loginerror));
                                    findViewById(R.id.tellerSignBtn).setEnabled(true);

                                }

                            }).execute();


                        }catch (Exception e){
                            e.printStackTrace();
                        }




                        }



                    @Override
                    public void loginFailed() {
                        Log.d(TAG, "loginFailed: " );
                        ProcessDlg.ShowBtnOK(LoginActivity.this, getResources().getString(R.string.btnlogin), getResources().getString(R.string.loginerror));
                        findViewById(R.id.tellerSignBtn).setEnabled(true);
                    }

                }).execute();
                break;
        }
    }


    protected void Scan() {
        Bundle bundle=new Bundle();
        bundle.putInt(CameraScan.BARCODE_CAMERA_TYPE, CameraScan.CarmeraType.TYPE_BACK_FACING);
        bundle.putBoolean(CameraScan.BARCODE_BEEP, true);
        mScan.setConfig(bundle);
        mScan.scan(30000, new CameraScan.CameraListener() {
            @Override
            public void onNotify(final int ret, final byte[] data) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (ret >= 0) {
                            EditText tellerNoEdit = findViewById(R.id.tellerNoEdit);
                            tellerNoEdit.setText(new String(data));
                           /* messageManager
                                    .AppendInfoMessage(getString(R.string.info_scan_success)+"\n"+new String(data));*/
                        }
                        else if(ret== Constants.Error.TIMEOUT){

                            Toast.makeText(LoginActivity.this,getString(R.string.info_scan_timeout),Toast.LENGTH_LONG).show();
                        }
                        else if(ret== Constants.Error.DEVICE_FORCE_CANCLE){

                            Toast.makeText(LoginActivity.this,getString(R.string.info_scan_cancle),Toast.LENGTH_LONG).show();

                        }
                        else if(ret== Constants.Error.DEVICE_USED){


                            Toast.makeText(LoginActivity.this,getString(R.string.info_scan_used),Toast.LENGTH_LONG).show();

                        }
                        else {


                            Toast.makeText(LoginActivity.this,getString(R.string.info_scan_failed),Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
    }
}
