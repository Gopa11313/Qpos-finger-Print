package cn.zt.successActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Window;

import com.szzt.sdk.device.aidl.IPrinterListener;
import com.szzt.sdk.device.printer.Printer;

import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.test.PrinterActivity;
import cn.zt.pos.utils.PrefUtil;

public class SuccessActivity extends BaseActivity {
    public static int mPrintStatus = -9999;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_success);
    }

/*
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
    }*/
}
