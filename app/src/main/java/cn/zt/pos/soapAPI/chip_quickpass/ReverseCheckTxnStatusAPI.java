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
import java.util.HashMap;

import cn.zt.pos.db.TellersDbHelper;
import cn.zt.pos.inf.ResponseInterface;
import cn.zt.pos.utils.PrefUtil;
import cn.zt.pos.utils.SOAPURL;
import cn.zt.pos.utils.UtilsLocal;


/**
 * Created by deadlydragger on 1/6/19.
 */

public class ReverseCheckTxnStatusAPI extends AsyncTask<String, String, SoapObject> {
    String stan, crrn, txnAmt;
    Context context;
    //    ProgressDialog progressDialog;
    ResponseInterface responseInterface;
    int count = 0;
    TellersDbHelper tellersDbHelper;

    public ReverseCheckTxnStatusAPI(Context context, String txnAmt, String stan, String crrn, ResponseInterface responseInterface) {
        this.context = context;
        this.txnAmt = txnAmt;
        this.stan = stan;
        this.crrn = crrn;
        this.responseInterface = responseInterface;
        tellersDbHelper = new TellersDbHelper(context);
        PrefUtil.getSharedPreferences(context);
    }

    @Override
    protected SoapObject doInBackground(String... strings) {
        try {
            return getWS(txnAmt, stan, crrn);
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
            count=PrefUtil.getCount()+1;
            PrefUtil.setCount(count);
            Log.d("dinesh", s.toString());
            JSONObject object = new JSONObject(s.getProperty(0).toString());
            if (object.getString("respCode").equals("LO") && count < 5) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new ReverseCheckTxnStatusAPI(context, txnAmt, stan, crrn, responseInterface).execute();
                    }
                }, UtilsLocal.TIME);

            } else if (object.getString("respCode").equals("00")||object.getString("respCode").equals("09")||object.getString("respCode").equals("10")||object.getString("respCode").equals("25")||object.getString("respCode").equals("22")||object.getString("respCode").equals("96")||object.getString("respCode").equals("12")) {
                PrefUtil.getSharedPreferences(context);
                tellersDbHelper.deletTrace(PrefUtil.getReferenceNumber());
               PrefUtil.setCrrn(crrn);
//                responseInterface.loginSuccess("","","","","","");

            } else {
//                tellersDbHelper.deletTrace(crrn);
                responseInterface.onfail();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseInterface.onfail();
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private static SoapObject getWS(String txnAmt, String stan, String crrn) throws IOException, XmlPullParserException {
        final String SOAP_ACTION = "http://tempuri.org/ISoftNacService/CheckTxnStatus";

        final String OPERATION_NAME = "CheckTxnStatus";
        final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        final String SOAP_ADDRESS = SOAPURL.URL;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);
        request.addProperty("stan", stan);
        request.addProperty("crrn", crrn);
        request.addProperty("txnAmt", txnAmt);
        Log.d("dinesh", "getWS: " + request);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);


        httpTransport.call(SOAP_ACTION, envelope);

        return (SoapObject) envelope.bodyIn;

    }

    public static HashMap<String, String> getUPIData(String merchantId) {
        HashMap<String, String> tagAndValue = new HashMap<>();
        char[] merchantIds = merchantId.toCharArray();
        for (int i = 0; i < merchantIds.length; ) {
            if (i + 4 < merchantIds.length) {

                String tag = String.valueOf(merchantIds[i]) + String.valueOf(merchantIds[i + 1]);
                int length;
                switch (String.valueOf(merchantIds[i + 2]) + String.valueOf(merchantIds[i + 3])) {
                    case "0A":
                        length = 20;
                        break;
                    case "0B":
                        length = 22;
                        break;
                    case "0C":
                        length = 24;
                        break;
                    case "0D":
                        length = 26;
                        break;
                    case "0E":
                        length = 28;
                        break;
                    case "0F":
                        length = 30;
                        break;
                    default:
                        length = 2 * Integer.parseInt(String.valueOf(merchantIds[i + 2]) + String.valueOf(merchantIds[i + 3]));
                        break;
                }
                StringBuilder values = new StringBuilder();

                for (int j = i + 4; j < i + 4 + length; j++) {
                    values.append(String.valueOf(merchantIds[j]));
                }
                tagAndValue.put(tag, values.toString());

                i += 4 + length;
            }
        }
        return tagAndValue;
    }
}
