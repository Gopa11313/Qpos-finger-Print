package cn.zt.pos.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import cn.zt.pos.R;

public class DialogUtils {


    public void dialogSucess(final Activity activity, String msg) {

        final Dialog dialog = new Dialog(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.success_dialog, null);
        final TextView dialog_balance = (TextView) v.findViewById(R.id.dialog_msg);
        dialog_balance.setText(msg);
        dialog.setContentView(v);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        v.findViewById(R.id.proceed_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                    activity.finish();
            }
        });
        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        float density = activity.getResources().getDisplayMetrics().density;
        lp.width = (int) (320 * density);
        lp.height = (int) (250 * density);
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

    }



    public void dailogFailure(final Activity activity, String msg) {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        final Dialog dialog = new Dialog(activity);

        final LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.failure_dialog, null);
        final TextView dialog_balance = v.findViewById(R.id.dialog_msg);
        dialog_balance.setText(msg);
        dialog.setContentView(v);
        dialog.setCancelable(false);
        /*final Dialog*/ /*dialog = builder.create()*/;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        v.findViewById(R.id.proceed_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                activity.finish();
            }
        });

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        float density =activity.getResources().getDisplayMetrics().density;
        lp.width = (int) (300 * density);
        lp.height = (int) (250 * density);
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

    }
}
