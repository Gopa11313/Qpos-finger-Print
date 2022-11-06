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

import cn.zt.pos.db.TellersDbHelper;
import cn.zt.pos.db.obj.Tracemodule;
import cn.zt.pos.inf.ResponseInterface;
import cn.zt.pos.utils.PrefUtil;
import cn.zt.pos.utils.SOAPURL;
import cn.zt.pos.utils.UtilsLocal;


/**
 * Created by deadlydragger on 1/2/19.
 */

public class MgPreAuthEmv extends AsyncTask<String, String, SoapObject> {
    private String de38, emvPayload, txnAmt, pBlock;
    private ResponseInterface responseInterface;
    private String TAG="dinesh";
    private Context context;
    private TellersDbHelper tellersDbHelper;
    private String stan, crrn;
    public MgPreAuthEmv(Context context, String emvPayload, String txnAmt, String pBlock, ResponseInterface responseInterface) {
        this.context=context;
        this.emvPayload = emvPayload;
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
            return getWS(tellersDbHelper, stan, crrn, emvPayload, txnAmt, pBlock);
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
                PrefUtil.getSharedPreferences(context);
                PrefUtil.setEmvPayload(emvPayload);
                PrefUtil.setTxnType("preauth");


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


    private static SoapObject getWS(TellersDbHelper tellersDbHelper, String stan, String crrn, String track2, String txnAmt, String pBlock) throws IOException, XmlPullParserException {

        final String SOAP_ACTION = "http://tempuri.org/ISoftNacService/PreAuthMag";

        final String OPERATION_NAME = "PreAuthMag";
        final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        final String SOAP_ADDRESS = SOAPURL.URL;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);
        request.addProperty("merchID", PrefUtil.getMerchantNo());
        request.addProperty("termID", PrefUtil.getTerminalNo());
        request.addProperty("stan", stan);
        request.addProperty("crrn", crrn);
        request.addProperty("txnAmt", txnAmt);
        request.addProperty("track2", track2);
        if (pBlock==null||pBlock.isEmpty()){
            request.addProperty("pBlock", "NP");
        }else {
            request.addProperty("pBlock", pBlock);
        }

        request.addProperty("mcc", "4722");
       /* if (PrefUtil.getSCode().equalsIgnoreCase("07")){
            request.addProperty("nfc","1");
//            string="1";
        }else {
            request.addProperty("nfc","0");
//            string="0";
        }*/
        request.addProperty("bin", PrefUtil.getCardNo());
        Log.d("dinesh", "getWS: " + request.toString());
        tellersDbHelper.insertTrace(new Tracemodule(stan, crrn, "preAuth", track2, txnAmt, "0"));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        httpTransport.call(SOAP_ACTION, envelope);
        return (SoapObject) envelope.bodyIn;

    }
}
