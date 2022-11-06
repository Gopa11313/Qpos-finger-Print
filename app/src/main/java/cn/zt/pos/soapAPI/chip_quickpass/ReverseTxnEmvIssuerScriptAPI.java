package cn.zt.pos.soapAPI.chip_quickpass;

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
 * Created by deadlydragger on 1/6/19.
 */

public class ReverseTxnEmvIssuerScriptAPI extends AsyncTask<String, String, SoapObject> {
    private ResponseInterface responseInterface;
    private Context context;

    private TellersDbHelper tellersDbHelper;
    //    private String stan, crrn;
    private Tracemodule tracemodule;

    public ReverseTxnEmvIssuerScriptAPI(Context context, Tracemodule tracemodule, ResponseInterface responseInterface) {
        this.context = context;
        this.tracemodule=tracemodule;
        this.responseInterface = responseInterface;
        tellersDbHelper = new TellersDbHelper(context);
      /*  stan = UtilsLocal.GetStan();
        crrn = UtilsLocal.GetCrrn();*/
    }

    @Override
    protected SoapObject doInBackground(String... strings) {
        try {
            return getWS(tellersDbHelper,tracemodule /*,stan, crrn*/);
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
            Log.d("dinesh", s.toString() + " " + s.getProperty(0).toString());
            final JSONObject object = new JSONObject(s.getProperty(0).toString());

            if (object.getString("status").equalsIgnoreCase("00")) {

//                tellersDbHelper.deletTrace(crrn);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new ReverseCheckTxnStatusAPI(context, object.getString("txnAmt"), object.getString("stan"), object.getString("crrn"), responseInterface).execute();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, UtilsLocal.TIME);

            }else if (s.getProperty(0).toString().equalsIgnoreCase("09") ){
                PrefUtil.getSharedPreferences(context);
                tellersDbHelper.deletTrace(PrefUtil.getReferenceNumber());

            }
            else {
                tellersDbHelper.deletTrace(tracemodule.getCrrn());
                responseInterface.onfail();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseInterface.onfail();
        }
    }

    private static SoapObject getWS(TellersDbHelper tellersDbHelper, Tracemodule tracemodule/*, String stan, String crrn*/) throws IOException, XmlPullParserException {
        final String SOAP_ACTION = "http://tempuri.org/ISoftNacService/ReverseTxnEmvIssuerScript";
        final String OPERATION_NAME = "ReverseTxnEmvIssuerScript";
        final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        final String SOAP_ADDRESS = SOAPURL.URL;
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);
        request.addProperty("merchID", PrefUtil.getMerchantNo());
        request.addProperty("termID", PrefUtil.getTerminalNo());
        request.addProperty("stan", UtilsLocal.GetStan());
        request.addProperty("crrn", "");
        request.addProperty("txnAmt", tracemodule.getAmount());
        request.addProperty("oStan", tracemodule.getStan());
        request.addProperty("oCrrn", tracemodule.getCrrn());
        request.addProperty("mcc", "4722");
        request.addProperty("pan", tracemodule.getCard_no());
        request.addProperty("bin",tracemodule.getCard_no().substring(0,6));
        request.addProperty("emvPayload",PrefUtil.getEmv());
        PrefUtil.setRefNumber(tracemodule.getCrrn());
        tellersDbHelper.insertTrace(new Tracemodule(tracemodule.getStan(), tracemodule.getCrrn(), "reverse", tracemodule.getCard_no(), tracemodule.getAmount(), "0"));
        Log.d("dinesh", "getWS: " + request.toString());
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        httpTransport.call(SOAP_ACTION, envelope);
        return (SoapObject) envelope.bodyIn;
    }
}
