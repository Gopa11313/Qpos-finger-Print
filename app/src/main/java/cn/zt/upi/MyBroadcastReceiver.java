package cn.zt.upi;

/**
 * Created by deadlydragger on 1/14/19.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

import cn.zt.pos.MainActivity;
import cn.zt.pos.R;
/**
 * Created by QPay on 10/21/2018.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       /* qpayDatabases = new QpayMerchantDatabase(GlobalVariable.getContext());
        qpayDatabases.getReadableDatabase();
        app_id = qpayDatabases.getAppId();
        term_id = qpayDatabases.getTermId();
        if (intent.getAction().equals("qrcodeeditor.blackspring.net.qrcodeeditor.NOTIFICATION")) {*/
            addNotification(context, intent);

//        }
    }

    private void addNotification(Context context, Intent intent) {


        Intent i = new Intent(context, MainActivity.class);
        i.setClassName(context.getPackageName(), MainActivity.class.getName());
        i.putExtra("message", intent.getStringExtra("notify"));
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Payment")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentText(intent.getStringExtra("notify"))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;
        noBuilder.setDefaults(defaults);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification

    }
}
