package cn.zt.pos.soapAPI.chip_quickpass;

import android.app.ProgressDialog;
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
 * Created by deadlydragger on 12/25/18.
 */

public class PostEmvTxnWithPinAPI extends AsyncTask<String, String, SoapObject> {
    String txnAmt, emvPayload, pBlock;
    ProgressDialog progressDialog;
    Context context;
    ResponseInterface responseInterface;
    TellersDbHelper tellersDbHelper;
    String stan, crrn;

    public PostEmvTxnWithPinAPI(Context context, String txnAmt, String emvPayload, String pBlock, ResponseInterface responseInterface) {
        this.context = context;
        this.txnAmt = txnAmt;
        this.emvPayload = emvPayload;
        this.pBlock = pBlock;
        this.responseInterface = responseInterface;
        tellersDbHelper = new TellersDbHelper(context);
        stan = UtilsLocal.GetStan();
        crrn = UtilsLocal.GetCrrn();

    }

    @Override
    protected SoapObject doInBackground(String... strings) {
        try {
            PrefUtil.setEmv(emvPayload);
            return getWS(tellersDbHelper, stan, crrn, txnAmt, emvPayload, pBlock);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(SoapObject s) {
        super.onPostExecute(s);
        try {
            Log.d("dinesh", s.toString() + " " + s.getProperty(0).toString());

            JSONObject object = new JSONObject(s.getProperty(0).toString());
            if (object.getString("status").equals("00")) {
//                tellersDbHelper.deletTrace(crrn);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new CheckTxnStatusAPI(context, txnAmt, stan, crrn, responseInterface).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, UtilsLocal.TIME);

            } else {
                responseInterface.onfail();
//                tellersDbHelper.deletTrace(crrn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseInterface.onfail();
           /* PrefUtil.getSharedPreferences(context);
            PrefUtil.setAmt(txnAmt);
            PrefUtil.setPBlock(pBlock);
            PrefUtil.setCard_no(emvPayload);
            PrefUtil.setTxnType("0");*/
        }
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
      /*  progressDialog=new ProgressDialog(context);
        progressDialog.show();*/
    }


    private static SoapObject getWS(TellersDbHelper tellersDbHelper, String stan, String crrn, String txnAmt, String emvPayload, String pBlock) throws IOException, XmlPullParserException {
        final String SOAP_ACTION = "http://tempuri.org/ISoftNacService/PostEmvTxnWithPin";
        final String OPERATION_NAME = "PostEmvTxnWithPin";
        final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        final String SOAP_ADDRESS = SOAPURL.URL;
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);
        request.addProperty("merchID", PrefUtil.getMerchantNo());
        request.addProperty("termID", PrefUtil.getTerminalNo());
        request.addProperty("stan", stan);
        request.addProperty("crrn", crrn);
        request.addProperty("txnAmt", txnAmt);
        request.addProperty("emvPayload", emvPayload);
        request.addProperty("pBlock", pBlock);
        request.addProperty("mcc", "4722");
        String string = "0";
        if (PrefUtil.getSCode().equalsIgnoreCase("07")) {
            request.addProperty("nfc", "1");
            string = "1";
        } else {
            request.addProperty("nfc", "0");
            string = "0";
        }
        request.addProperty("bin", PrefUtil.getCardNo().substring(0,6));
        Log.d("dinesh", "getWS: " + request.toString());
        tellersDbHelper.insertTrace(new Tracemodule(stan, crrn, "sale", /*emvPayload*/PrefUtil.getCardNo(), txnAmt, string));
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        httpTransport.call(SOAP_ACTION, envelope);
        return (SoapObject) envelope.bodyIn;

    }
}
