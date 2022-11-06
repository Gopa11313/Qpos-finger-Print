package cn.zt.pos.activityDailog;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Calendar;

import cn.zt.pos.event.NotificationEvent;
import cn.zt.pos.utils.PrefUtil;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
/**
 * Created by QPay on 10/23/2018.
 */

public class SalesService extends Service {
    private Socket socket;
    private BroadcastReceiver br;
    IntentFilter intentFilter;
    String app_id, term_id;
    private IBinder iBinder = new SalesBinder();
    Context context;
    public static Service myService;
    private Handler handler = new Handler();
//    Boolean isDailogShown=false;

    @Override
    public IBinder onBind(Intent intent) {
        try {
            return iBinder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

//    @Override
//    public void IsDailogShown(Boolean isShown) {
//        isDailogShown=isShown;
//    }

    public class SalesBinder extends Binder {

        SalesService getService() {
            try {
                return SalesService.this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    @Override
    public void onCreate() {
        try {
            super.onCreate();
            //service is create
            myService = this;
//            app_id = qpayDatabases.getAppId();
//            term_id = qpayDatabases.getTermId();
            PrefUtil.getSharedPreferences(context);
//            Log.d("term_id", PrefUtil.getTerminalNo() + "...");
            intentFilter = new IntentFilter("ui.wangpos.com.ccbbank.NOTIFICATION");
            listenNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    private void listenNotification() {
        try {
            socket = IO.socket("http://sockettest.qpaysolutions.net/");
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    JsonObject jsonObject = new JsonObject();
                    Log.wtf("dr","wtf");
                   /* if (AppCache.getApplicationMode() == AppCache.MODE_CHINESE_EMBASSY){
                        jsonObject.addProperty("appId", "3A00000008066qp");
                    }else{
                        jsonObject.addProperty("appId", "3A00000024269qp");
                    }*/
                    jsonObject.addProperty("appId", PrefUtil.getAppId());
                    jsonObject.addProperty("id", PrefUtil.getPayload());
//                    jsonObject.addProperty("appId", result.getAppId());
//                    jsonObject.addProperty("id", sharedPrefs.getPayload());
                    jsonObject.addProperty("lat", "00");
                    jsonObject.addProperty("lng", "00");
                    jsonObject.addProperty("socketId", socket.id());
//                    new updateSocketId(socket.id()).execute();
                    Log.d("updateSocketId", "call: "+jsonObject.toString());
                    socket.emit("updateSocketId", jsonObject.toString());

                }

            }).on(PrefUtil.getTerminalNo(), new Emitter.Listener() {

                @Override
                public void call(final Object... args) {


                    String data = args[0].toString();
                    Log.d("service", data);

                    try {
                        if (new JSONObject(data).getString("message").equalsIgnoreCase("validating")) {
                            Log.d("service", "validating dialog" + Calendar.getInstance().getTime().toString());
                            PrefUtil.setIsDailogShown(false);
                            if (PrefUtil.getIsDailogShown(context)) {

                            } else {
                                sendMessageToActivity();
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                String merchantId = jsonObject.optString("merchantId");
                                String token = jsonObject.optString("token");
                                String stan = jsonObject.optString("stan");
                                String crrn = jsonObject.optString("crrn");
                                String status = jsonObject.optString("status");
                                String message = jsonObject.optString("message");
                                String amount = jsonObject.optString("amount");
                                Log.d("success", "call: " + amount);
                                if (stan.length() > 0 && crrn.length() > 0) {
                                    Log.d("service", "activity: " + Calendar.getInstance().getTime().toString());
                                    PrefUtil.setStan(stan);
                                    PrefUtil.setCrrn(crrn);
                                    Log.d("dinesh", "message.." + message + ".." + amount);
                                    PrefUtil.setAmount(decimalFormate(amount));
                                    Intent i = new Intent();
                                    i.setClass(SalesService.this, ActivityDailog.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    System.out.println("Success");
                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                }

            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void sendMessageToActivity() {
//        Intent intent = new Intent("Updates");
//        // You can also include some extra data.
//        LocalBroadcastManager.getInstance(GlobalVariable.getContext()).sendBroadcast(intent);
        EventBus.getDefault().post(new NotificationEvent());
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
}
