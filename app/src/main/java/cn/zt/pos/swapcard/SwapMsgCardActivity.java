package cn.zt.pos.swapcard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.szzt.sdk.device.Constants;
import com.szzt.sdk.device.card.ContactlessCardReader;
import com.szzt.sdk.device.card.MagneticStripeCardReader;
import com.szzt.sdk.device.card.SmartCardReader;

import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.inf.ResponseInterface;
import cn.zt.pos.soapAPI.magnatic.MgPostEmvTxnWithPinAPI;
import cn.zt.pos.utils.DialogUtils;
import cn.zt.pos.utils.PrefUtil;
import cn.zt.pos.utils.TxnDetails;

public class SwapMsgCardActivity extends BaseActivity implements View.OnClickListener {

    private MagneticStripeCardReader mMagneticStripeCardReader;
    private SmartCardReader mSmartCardReader;
    private ContactlessCardReader mContactlessCardReader;
    private FrameLayout progressLayout;
    private TextView tv_cardNo;
    private String amountDecimal;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_swap_card);
        PrefUtil.getSharedPreferences(SwapMsgCardActivity.this);
        TextView titleNameText = findViewById(R.id.titleNameText);
        titleNameText.setText("Swap Card");
        TextView tv_amount = findViewById(R.id.tv_amount);
         amountDecimal=PrefUtil.getAmount();
        tv_amount.setText("NPR "+amountDecimal);
        findViewById(R.id.titleBackImage).setOnClickListener(this);
        boolean isConnected = mainApplication.isDeviceManagerConnetcted();
        progressLayout=findViewById(R.id.progressLayout);
        tv_cardNo=findViewById(R.id.tv_cardNo);
        switch (returnType(isConnected)){
            case 0:
                int nret = mMagneticStripeCardReader.open();
                if (nret >= 0) {
                    Toast.makeText(this,"Waiting For Card",Toast.LENGTH_LONG).show();
                    new Thread(){
                        @Override
                        public void run() {
                            magStripeCardReader(handler);
                        };
                    }.start();

                } else {
                    Toast.makeText(this,getString(R.string.msg_card_read_op_error),Toast.LENGTH_LONG).show();

                }

                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.titleBackImage:
                finish();
                break;
        }

    }
    private int returnType(Boolean isConnected){
        if (isConnected) {
            mMagneticStripeCardReader = mainApplication
                    .getMagneticStripeCardReader();
            if (mMagneticStripeCardReader == null) {
            }else {
                return 0;
            }
            mSmartCardReader = mainApplication.getSmartCardReader();
            if (mSmartCardReader == null) {
            }else {
                return 1;
            }
            mContactlessCardReader = mainApplication.getContactlessCardReader();
            if (mContactlessCardReader == null) {
            }else {
                return 2;
            }

        }
        return 3;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMagneticStripeCardReader != null){
            mMagneticStripeCardReader.close();
        }
        if (mSmartCardReader != null){
            mSmartCardReader.close(0);
        }
        if (mContactlessCardReader != null){
            mContactlessCardReader.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMagneticStripeCardReader != null){
            mMagneticStripeCardReader.close();
        }
        if (mSmartCardReader != null){
            mSmartCardReader.close(0);
        }
        if (mContactlessCardReader != null){
            mContactlessCardReader.close();
        }
    }

    /*for mag card*/
    private String trackFormat(byte[] data) {
        if (data == null) {
            return "no track info";
        } else {
            return new String(trackTrim(data));
        }
    }
    private byte[] trackTrim(byte[] data) {
        if (data == null) {
            return null;
        } else {
            int end = 0;
            for (int i = data.length - 1; i >= 0; i--) {
                if (data[i] != 0) {
                    end = i;
                    break;
                }
            }
            byte[] d = new byte[end + 1];
            System.arraycopy(data, 0, d, 0, end + 1);
            return d;
        }
    }
    void magStripeCardReader(Handler handler) {
        mMagneticStripeCardReader.setCheckLrc(1);
        int result = mMagneticStripeCardReader.waitForCard(100000);
        Log.e("waitForCard", "result:" + result);
        if (result == 0) {
            byte[] data1 = mMagneticStripeCardReader
                    .getTrackData(MagneticStripeCardReader.TRACK_INDEX_1);
            byte[] data2 = mMagneticStripeCardReader
                    .getTrackData(MagneticStripeCardReader.TRACK_INDEX_2);
            byte[] data3 = mMagneticStripeCardReader
                    .getTrackData(MagneticStripeCardReader.TRACK_INDEX_3);
            byte[] checkFlag = mMagneticStripeCardReader.getTrackData(MagneticStripeCardReader.TRACK_CHECK_FLAG);
            if (data1 == null && data2 == null && data3 == null) {


                Toast.makeText(this,getString(R.string.msg_card_read_error),Toast.LENGTH_LONG).show();

            } else {

//                Toast.makeText(SwapMsgCardActivity.this,getString(R.string.msg_card_read_succ),Toast.LENGTH_LONG).show();
//                String strTrack1 = trackFormat(data1);
                String strTrack2 = trackFormat(data2);
                handler.obtainMessage(0, strTrack2.split("=")[0].replace(";","")).sendToTarget();

//                String strTrack3 = trackFormat(data3);
                Log.d("magStripeCardReader", "magStripeCardReader: "+strTrack2 + " FLAG:" + checkFlag[1]+" card no : "+strTrack2.split("=")[0]);



                new MgPostEmvTxnWithPinAPI(SwapMsgCardActivity.this, amountDecimal, strTrack2.replace(";","").replace("=","D").replace("?",""), "NP", new ResponseInterface() {
                    @Override
                    public void loginSuccess(String tag39, String tag38, String tag55, String tag91, String tag71, String tag72) {
                        Log.d("MgPostEmvTxnWithPinAPI", "loginSuccess: ");
                        progressLayout.setVisibility(View.GONE);
//                        startActivity(new Intent(SwapMsgCardActivity.this, SuccessActivity.class));
                        new DialogUtils().dialogSucess(SwapMsgCardActivity.this,"TXN Success");

                    }
                    @Override
                    public void onfail() {
//                        progressLayout.setVisibility(View.GONE);
                        Log.d("fail", "onfail: ");

                       new DialogUtils().dailogFailure(SwapMsgCardActivity.this,"TXN Failure");
                    }

                }).execute();



            }
        } else if (result == Constants.Error.TIMEOUT) {
            Toast.makeText(this,getString(R.string.msg_card_time_out),Toast.LENGTH_LONG).show();

        } else if (result == Constants.Error.DEVICE_FORCE_CLOSED) {
            Toast.makeText(this,getString(R.string.msg_card_force_close),Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(this,getString(R.string.msg_card_read_error),Toast.LENGTH_LONG).show();

        }

    }



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TxnDetails.CARD_NO:
                    tv_cardNo.setText(msg.obj.toString());
                    PrefUtil.setCardNo(msg.obj.toString());
                    break;
            }
        }
    };





}
