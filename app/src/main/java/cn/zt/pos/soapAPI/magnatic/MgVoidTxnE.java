package cn.zt.pos.soapAPI.magnatic;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.zt.pos.db.TellersDbHelper;
import cn.zt.pos.db.obj.Tracemodule;
import cn.zt.pos.inf.ResponseInterface;
import cn.zt.pos.utils.PrefUtil;
import cn.zt.pos.utils.SOAPURL;
import cn.zt.pos.utils.UtilsLocal;


/**
 * Created by deadlydragger on 12/31/18.
 */

public class MgVoidTxnE extends AsyncTask<String, String, SoapObject> {
    private String de38, track2, txnAmt, pBlock;
    private ResponseInterface responseInterface;
    private String TAG="dinesh";
    private Context context;
    private TellersDbHelper tellersDbHelper;
    private String stan, crrn,oCrrn;
    public MgVoidTxnE(Context context, String de38, String oCrrn, String track2, String txnAmt, String pBlock, ResponseInterface responseInterface) {
        this.context=context;
        this.de38 = de38;
        this.oCrrn = oCrrn;
        this.track2 = track2;
        this.txnAmt = txnAmt;
        this.pBlock = pBlock;
        this.responseInterface = responseInterface;
        tellersDbHelper = new TellersDbHelper(context);
        stan = UtilsLocal.GetStan();
        crrn = UtilsLocal.GetCrrn();
    }

    @Override
    protected SoapObject doInBackground(String... strings) {
        try {
            return getWS(tellersDbHelper, stan, crrn,de38, oCrrn, track2, txnAmt, pBlock);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(SoapObject s) {
        super.onPostExecute(s);
        try {
            Log.d("dinesh", s.toString()+" "+ s.getProperty(0).toString());

            JSONObject object = new JSONObject( s.getProperty(0).toString());
            if (object.getString("status").equals("00")){
//                tellersDbHelper.deletTrace(crrn);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new MgCheckTxnStatusAPI(context,txnAmt,stan,crrn,responseInterface).execute();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, UtilsLocal.TIME);
            }else {
//                tellersDbHelper.deletTrace(crrn);
                responseInterface.onfail();

            }
        }catch (Exception e){
            e.printStackTrace();
            responseInterface.onfail();

        }
    }


    private static SoapObject getWS(TellersDbHelper tellersDbHelper, String stan, String crrn, String de38, String refNo, String track2, String txnAmt, String pBlock) throws IOException, XmlPullParserException {
        final String SOAP_ACTION = "http://tempuri.org/ISoftNacService/VoidTxnM";
        final String OPERATION_NAME = "VoidTxnM";
        final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        final String SOAP_ADDRESS = SOAPURL.URL;
        String stanT=UtilsLocal.GetStan();
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);
        request.addProperty("merchID", PrefUtil.getMerchantNo());
        request.addProperty("termID", PrefUtil.getTerminalNo());
        request.addProperty("stan", stan);
        request.addProperty("crrn", crrn);
        request.addProperty("authCode", de38);
        request.addProperty("txnDate", dateConvert());
        request.addProperty("oStan", stanT);
        request.addProperty("oCrrn", refNo);
        request.addProperty("track2", track2);
        request.addProperty("txnAmt", txnAmt);
        if (pBlock==null||pBlock.isEmpty()){
            request.addProperty("pBlock", "NP");
        }else {
            request.addProperty("pBlock", pBlock);
        }
        request.addProperty("bin",PrefUtil.getCardNo());
       /* if (PrefUtil.getSCode().equalsIgnoreCase("07")){
            request.addProperty("nfc","1");
//            string="1";
        }else {
            request.addProperty("nfc","0");
//            string="0";
        }*/
        Log.d("dinesh", "getWS: " + request.toString());
        tellersDbHelper.insertTrace(new Tracemodule(stan, crrn, "voidTxnE", PrefUtil.getCardNo(), txnAmt, "0"));
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        httpTransport.call(SOAP_ACTION, envelope);
        return (SoapObject) envelope.bodyIn;

    }
    private  static String dateConvert(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
        //String date=sdf.format(new java.util.Date());
        String daten = ""+new SimpleDateFormat("yyyy").format(new Date());//获取系统年份

        String date = null;
        try {
            date = sdf.format((new SimpleDateFormat("yyyyMMdd")).parse(daten + UtilsLocal.Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
