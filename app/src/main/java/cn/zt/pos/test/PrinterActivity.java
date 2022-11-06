package cn.zt.pos.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.szzt.sdk.device.Device;
import com.szzt.sdk.device.aidl.IPrinterListener;
import com.szzt.sdk.device.printer.Printer;

import cn.zt.pos.BaseActivity;
import cn.zt.pos.MainApplication;
import cn.zt.pos.R;
import cn.zt.pos.utils.MessageManager;
import cn.zt.pos.utils.PrefUtil;
import cn.zt.pos.utils.Shower;

public class PrinterActivity extends BaseActivity {
    private MessageManager messageManager;
    private Printer mPrint;
    private Button mClearButton;
    private Button mOpenButton;
    private Button mLoopButton;
    private Button mPrintDefaultButton;
    private Button mCloseButton;
    private Shower shower;
    private static final String mPrintReceive = "com.szzt.printer.stat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_printer);
        shower = new Shower(this);
        boolean isConnected = mainApplication.isDeviceManagerConnetcted();
        IntentFilter printerFilter = new IntentFilter(mPrintReceive);
        PrinterReceiver printReceiver = new PrinterReceiver();
        registerReceiver(printReceiver, printerFilter);
        init(isConnected);
    }

    public static int mPrintStatus = -9999;

    class PrinterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPrintReceive.equals(intent.getAction())) {
                mPrintStatus = intent.getIntExtra("stat", Printer.STATUS_OK);
                if (mPrintStatus == Printer.STATUS_OK) {
                    if (mIndex > 0) {
                        PrinterDefaultPurchase();
                    }
                }
            }
        }
    }

    private void init(boolean isConnected) {
        if (messageManager == null) {
            TextView text = (TextView) findViewById(R.id.print_text);
            text.setMovementMethod(ScrollingMovementMethod.getInstance());
            messageManager = new MessageManager(this, text);
            mClearButton = (Button) findViewById(R.id.print_clear);
            mClearButton.setOnClickListener(listener);
            mLoopButton = (Button) findViewById(R.id.print_loop);
            mLoopButton.setOnClickListener(listener);
            mOpenButton = (Button) findViewById(R.id.print_open);
            mOpenButton.setOnClickListener(listener);
            mPrintDefaultButton = (Button) findViewById(R.id.print_print);
            mPrintDefaultButton.setOnClickListener(listener);
            mCloseButton = (Button) findViewById(R.id.print_close);
            mCloseButton.setOnClickListener(listener);
            mPrint = mainApplication.getPrinter();
        }
        mClearButton.setEnabled(isConnected);
        mOpenButton.setEnabled(isConnected);
        mPrintDefaultButton.setEnabled(isConnected);
        mCloseButton.setEnabled(isConnected);
        mLoopButton.setEnabled(isConnected);
    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.print_clear:
                    messageManager.clear();
                    break;
                case R.id.print_open: {
                    int ret = mPrint.open();
                    if (ret >= 0) {
                        mPrintStatus = ret = mPrint.getStatus();
                        if (ret == Printer.STATUS_NO_PAPER) {
                            //no paper
                            messageManager
                                    .AppendInfoMessage("\n" + shower.getString(R.string.msg_printer_lake_paper));
                        } else if (ret == Printer.STATUS_OK) {
                            //ok
                            messageManager
                                    .AppendInfoMessage("\n" + shower.getString(R.string.msg_printer_has_paper));
                        } else if (ret == Printer.STATUS_LOWVOL) {
                            //low vol
                            messageManager
                                    .AppendInfoMessage("\n" + shower.getString(R.string.msg_printer_has_paper));
                        } else if (ret == Printer.STATUS_OVERHEAT) {
                            //too heat
                            messageManager
                                    .AppendInfoMessage("\n" + shower.getString(R.string.msg_printer_has_paper));
                        } else {
                            messageManager
                                    .AppendInfoMessage("\n" + shower.getString(R.string.msg_printer_unknow_status));
                        }
                    } else {
                        messageManager
                                .AppendInfoMessage(
                                        shower.getString(R.string.msg_printer_open_error),
                                        Color.RED);
                    }
                }
                break;
                case R.id.print_close: {
                    mIndex = 0;
                    int ret = mPrint.close();
                    if (ret >= 0) {
                        messageManager
                                .AppendInfoMessage(shower.getString(R.string.msg_printer_close_succ));
                    } else {
                        messageManager
                                .AppendInfoMessage(
                                        shower.getString(R.string.msg_printer_clsoe_error),
                                        Color.RED);
                    }
                }
                break;
                case R.id.print_print:
                    if (mPrint.getStatus() == Printer.STATUS_NO_PAPER) {
                        messageManager
                                .AppendInfoMessage(
                                        shower.getString(R.string.msg_printer_lake_paper),
                                        Color.RED);
                        return;
                    }
                    if (mIndex <= 0) {
                        PrinterDefaultPurchase();
                        mIndex = 1;
                    }
                    break;
                case R.id.print_loop:
                    if (mPrint.getStatus() == Printer.STATUS_NO_PAPER) {
                        messageManager
                                .AppendInfoMessage(
                                        shower.getString(R.string.msg_printer_lake_paper),
                                        Color.RED);
                        return;
                    }

                    if (mIndex <= 0) {
                        PrinterDefaultPurchase();
                        if (MainApplication.printIndex == 0) {
                            mIndex = 300;
                        } else {
                            mIndex = MainApplication.printIndex;
                        }

                    } else {
                        messageManager
                                .AppendInfoMessage(
                                        "mIndex=" + mIndex,
                                        Color.RED);
                    }

                    break;
                default:

                    break;
            }
        }
    };
    private int mIndex = 0;

    private void PrinterDefaultPurchase() {
        if (mPrintStatus == Printer.STATUS_OK) {
            Bitmap icon = BitmapFactory.decodeResource(PrinterActivity.this.getResources(),
                    R.drawable.sample);

//            mPrint.addImg(icon);
            mPrint.addStr("QPay Pvt. Ltd.\n", Printer.Font.LARGE, true, Printer.Align.LEFT);
            mPrint.addStr(PrefUtil.getMerchantName() + " COPY (QPOS)\n", Printer.Font.FONT_2, true, Printer.Align.LEFT);
            mPrint.addStr("MERCHANT NO :  " + PrefUtil.getMerchantNo() + "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);

            mPrint.addStr("AMOUNT :  " + PrefUtil.getAmount() + "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("OPERATOR NO : 01\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("ISSUER      :Bank of China\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("ACQUIRER    :QR Payment\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("CARD NO:    :" + PrefUtil.getCardNo() + "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("EXP DATE:    :" + PrefUtil.getExpDate() + "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("TRANS TYPE  :" + PrefUtil.getType() + "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("Auth Code   :" + PrefUtil.getDe38() + "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("Ref No: " + "" + PrefUtil.getReferenceNumber() + "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("AUTH NO:" + "" + "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("DATE : " + PrefUtil.getTxnDate()+ "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("TC : " +  "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("AID : " +  "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("APP LABLE : " + "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE  : " + PrefUtil.getTxnDate()+ "\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("GOODS/SERVICES" + "\n\n\n", Printer.Font.FONT_2, false, Printer.Align.LEFT);
            mPrint.addStr("CARDHOLDER SIGNATURE"+ "\n" , Printer.Font.FONT_2, true, Printer.Align.LEFT);
            mPrint.addStr("---------------------------------------------" +"\n", Printer.Font.FONT_2, true, Printer.Align.CENTER);


            mPrint.start(new IPrinterListener.Stub() {

                @Override
                public void PrinterNotify(final int retCode) throws RemoteException {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (retCode == 0) {
                                mIndex--;
                                if (mIndex > 0) {
                                    PrinterDefaultPurchase();
                                } else {
                                    messageManager.AppendInfoMessage(shower.getString(R.string.msg_printer_print_succ));
                                }
                            } else {
                                int ret = mPrint.getStatus();
                                if (ret == Printer.STATUS_NO_PAPER) {
                                    //no paper
                                    messageManager
                                            .AppendInfoMessage("\n" + shower.getString(R.string.msg_printer_lake_paper));
                                } else if (ret == Printer.STATUS_OK) {
                                    //ok
                                    messageManager
                                            .AppendInfoMessage("\n" + shower.getString(R.string.msg_printer_has_paper));
                                } else if (ret == Printer.STATUS_LOWVOL) {
                                    //low vol
                                    messageManager
                                            .AppendInfoMessage("\n" + shower.getString(R.string.msg_printer_has_paper));
                                } else if (ret == Printer.STATUS_OVERHEAT) {
                                    //too heat
                                    messageManager
                                            .AppendInfoMessage("\n" + shower.getString(R.string.msg_printer_has_paper));
                                } else {
                                    messageManager
                                            .AppendInfoMessage("\n" + shower.getString(R.string.msg_printer_unknow_status));
                                }
                            }
                        }
                    });
                }
            });
        } else {
            messageManager.AppendInfoMessage(shower.getString(R.string.msg_printer_print_error),
                    Color.RED);
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onDestroy() {
        if (mPrint.getStatus() != Device.STATUS_CLOSED) {
            mPrint.close();
        }
        super.onDestroy();
    }

}
