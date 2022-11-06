package cn.zt.upi;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import cn.zt.pos.R;
import cn.zt.pos.activityDailog.IsDailogShown;
import cn.zt.pos.event.NotificationEvent;
import cn.zt.pos.utils.PrefUtil;
import io.socket.client.Socket;

/**
 * Created by deadlydragger on 1/14/19.
 */

public class GenerateUPIQRActivity extends Activity implements View.OnClickListener {
    private Socket socket;
    private BroadcastReceiver br;
    IntentFilter intentFilter;
    private Map<String, String> map;
    private boolean isActivityOpen = true;
    Dialog dialog;
    IsDailogShown isDailogShown;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefUtil.getSharedPreferences(this);
        mediaPlayer = MediaPlayer.create(GenerateUPIQRActivity.this, R.raw.beep);
            setContentView(R.layout.qr_upi_main);
        TextView merchhantName = (TextView)findViewById(R.id.merch_name);
        merchhantName.setText(PrefUtil.getMerchantName());
            setTitle("QR Sale");


        ImageView iv_qr_code = (ImageView) findViewById(R.id.iv_qr_code);
        TextView edt_amount = (TextView) findViewById(R.id.edt_amount);
        ImageView titleBackImage = (ImageView) findViewById(R.id.titleBackImage);
        titleBackImage.setOnClickListener(this);
          PrefUtil.getSharedPreferences(this);
        String decimal_string=PrefUtil.getAmount();
        edt_amount.setText(PrefUtil.getAmount());
//        map = UtilsQRCode.getUPQRCPreference(this);
        UtilsQRCode.generateQR(iv_qr_code, UtilsQRCode.updateTagLenghtValue(decimal_string, map));

    }
    public String decimalFormate(String value){
        String decimal="";
        try {
            decimal= String.format("%.2f", Double.parseDouble(value));

        }catch (Exception e){
            e.printStackTrace();
        }
        return decimal;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationEvent(NotificationEvent event){
        mediaPlayer.start();

        PrefUtil.setIsDailogShown(true);
        showDailog();

    System.out.print("Validating");
    }

    private void setTitle(String title) {
        TextView titleNameText = (TextView) findViewById(R.id.titleNameText);
        titleNameText.setText(title);

    }

    public void setInterface(IsDailogShown isDailogShown) {
        this.isDailogShown = isDailogShown;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBackImage:
                onBackPressed();
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityOpen = false;
        Log.d("onDestroy", "");
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityOpen = false;
        Log.d("onPause", "");
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityOpen = false;
        Log.d("onStop", "");
        if (dialog != null) {
            dialog.dismiss();
        }
        EventBus.getDefault().unregister(this);
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            try{
                if (intent != null) {
//                    isDailogShown.IsDailogShown(true);
                    PrefUtil.setIsDailogShown(true);
                    showDailog();
                    new CountDownTimer(60000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            dialog.hide();
                        }

                    }.start();

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    private void showDailog() {

         dialog = new Dialog(GenerateUPIQRActivity.this); // Context, this, etc.
      /*  if (AppCache.getApplicationMode() == AppCache.MODE_CHINESE_EMBASSY){
            dialog.setContentView(R.layout.dialog_validating_chinese);
        }else{*/
            dialog.setContentView(R.layout.dailog_validating);
//        }
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

         /*   final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(GenerateUPIQRActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dailog_validating, null);
        builder.setView(v);
        builder.setCancelable(false);
        dialog = builder.create();

      /*  if (isActivityOpen){
            dialog.show();
        }else{

        }*/

   /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        float density = getResources().getDisplayMetrics().density;
        lp.width = (int) (320 * density);
        lp.height = (int) (420 * density);
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));*/
    }
}
