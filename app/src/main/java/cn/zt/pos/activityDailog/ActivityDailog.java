package cn.zt.pos.activityDailog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import cn.zt.pos.MainActivity;
import cn.zt.pos.R;
import cn.zt.pos.utils.HttpUrlConnectionPost;
import cn.zt.pos.utils.PrefUtil;


/**
 * Created by deadlydragger on 9/5/17.
 */

public class ActivityDailog extends Activity {
    String app_id, term_id;
//    https://testportal.qpaysolutions.net/api/
    private final String BASE_API="https://node.qpaysolutions.net/QPay.svc/";
    private final String VALIDATE_TRANSACTION="https://testportal.qpaysolutions.net/api/"+"verifytransaction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DialogTheme);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.test_activity);
        print();

//            custumdialogSucess(PrefUtil.getAmount());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        finishAffinity();
    }


    private void print() {

     /*   WeiPassGlobal.getTransactionInfo().setTransDate(DateUtil.getCurDateStr("MMdd"));
        if(WeiPassGlobal.getTransactionInfo().getTransTime()==null)
        {
            WeiPassGlobal.getTransactionInfo().setTransTime(DateUtil.getCurDateStr("HHmmss"));
        }
        LedUtils.getInstance().success();

        TradeRecordDb tradeRecordDb = new TradeRecordDb(ActivityDailog.this);
        tradeRecordDb.insertInfo(WeiPassGlobal.getTransactionInfo());
        PrefUtil.setPrint_Repeat(true);

        PrintContext.Printer(new OnPrientListener() {
            @Override
            public void OnSuccess() {

            }

            @Override
            public void OnProcess() {

            }

            @Override
            public void OnError(String error) {
                ProcessDlg.ShowBtnOK(ActivityDailog.this, "tips", error, new ProcessDlg.DLGOnClickListener() {
                    @Override
                    public void onClickListener(ProcessDlg builder, View v) {

                    }
                });
            }
        }, new OnPrientResultListener() {
            @Override
            public boolean OnSuccess() {
                return PrintContext.dialog();
            }
        });

        String icDataStr = WeiPassGlobal.getTransactionInfo().getIcData();

        Log.d("dinesh", "tlv data : " + icDataStr);*/

    }

     class verifyTransaction extends AsyncTask<String, String, String> {

        public verifyTransaction() {
        }

        @Override
        protected String doInBackground(String... params) {
            HttpUrlConnectionPost httpUrlConnectionPost = new HttpUrlConnectionPost();
            String confirm_response = "";
            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("appId", PrefUtil.getTerminalNo());
                jsonObject.put("termId", PrefUtil.getMerchantNo());
                jsonObject.put("lat", "00");
                jsonObject.put("lng", "00");
                jsonObject.put("stan", PrefUtil.getStan());
                jsonObject.put("crrn", PrefUtil.getCrrn());
                confirm_response = httpUrlConnectionPost.sendHTTPData(VALIDATE_TRANSACTION, jsonObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return confirm_response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                boolean success = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                if (success == true) {
                    if (jsonObject.getString("status").equals("00")) {
//                        custumdialogSucess(PrefUtil.getAmount());

                    } else if (jsonObject.getString("status").equals("99")) {
//                        custumdialogFailure(PrefUtil.getAmount());
                    }

                } else {
//                    custumdialogFailure(PrefUtil.getAmount());
                }
            } catch (Exception e) {
                e.printStackTrace();
                dialogWarning("Timeout");

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }

    public void dialogWarning(String title_display) {
        final Dialog dialog = new Dialog(ActivityDailog.this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_success);
        dialog.show();

        dialog.findViewById(R.id.proceed_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(ActivityDailog.this, MainActivity.class));
               finishAffinity();

            }
        });

    }

    /*public void custumdialogSucess(final String fund_transfer) {


        final Dialog dialog = new Dialog(ActivityDailog.this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_full_screen_sucess_new);
        dialog.setCancelable(false);
        final TextView balance = (TextView) dialog.findViewById(R.id.balance);
        balance.setText(decimalFormate(fund_transfer));
        dialog.show();


        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // PrefUtil.setIsDailogShown(false);
                dialog.dismiss();
                printCustomerCopy(decimalFormate(fund_transfer));

            }
        });
    }
    public void custumdialogSucessForKfc(final String fund_transfer) {

        final Dialog dialog = new Dialog(ActivityDailog.this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_full_screen_sucess_new_kfc);
        dialog.setCancelable(false);
        final TextView balance = (TextView) dialog.findViewById(R.id.balance);
        balance.setText(fund_transfer);
        dialog.show();


        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  PrefUtil.setIsDailogShown(false);
                dialog.dismiss();
                printCustomerCopy(fund_transfer);
            }
        });
    }
    public void custumdialogSucessForEmbassy(final String fund_transfer) {

        final Dialog dialog = new Dialog(ActivityDailog.this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_full_screen_sucess_new_chinese);
        dialog.setCancelable(false);
        final TextView balance = (TextView) dialog.findViewById(R.id.balance);
        balance.setText(fund_transfer);
        dialog.show();


        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  PrefUtil.setIsDailogShown(false);
                dialog.dismiss();
                printCustomerCopy(fund_transfer);
            }
        });
    }

    public void printCustomerCopy(String fund_transfer) {

        final Dialog dialog = new Dialog(ActivityDailog.this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_full_screen_customer_copy);
        dialog.setCancelable(false);
//        final TextView balance = (TextView) dialog.findViewById(R.id.balance);
//        balance.setText(fund_transfer);
        dialog.show();
        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCache.setReceiptName("Customer");
                print();
                dialog.dismiss();
                Intent i = new Intent(ActivityDailog.this, Main2Activity.class);
                startActivity(i);
                finishAffinity();
            }
        });

        dialog.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent i = new Intent(ActivityDailog.this, Main2Activity.class);
                startActivity(i);
                finishAffinity();
            }
        });


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
    public void custumdialogFailure(String fund_transfer) {
        final Dialog dialog = new Dialog(ActivityDailog.this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_full_screen_failure_new);
        dialog.setCancelable(false);
        final TextView balance = (TextView) dialog.findViewById(R.id.balance);
        balance.setText(fund_transfer);
        dialog.show();


        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent i = new Intent(ActivityDailog.this, Main2Activity.class);
                startActivity(i);
                finishAffinity();
            }
        });

//        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDailog.this);
//        LayoutInflater inflater = getLayoutInflater();
//        View v = inflater.inflate(R.layout.dialog_full_screen_failure_new, null);
//
//        final TextView balance = (TextView) v.findViewById(R.id.balance);
//        balance.setText(fund_transfer);
//        builder.setView(v);
//        builder.setCancelable(false);
//        final Dialog dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        v.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                Intent i = new Intent(ActivityDailog.this, Main2Activity.class);
//                startActivity(i);
//                finish();
//            }
//        });
//        dialog.show();
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        float density = getResources().getDisplayMetrics().density;
//        lp.width = (int) (320 * density);
//        lp.height = (int) (420 * density);
//        lp.gravity = Gravity.CENTER;
//        dialog.getWindow().setAttributes(lp);
    }

    public void vibration() {
        Vibrator v = (Vibrator) ActivityDailog.this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }*/
}
