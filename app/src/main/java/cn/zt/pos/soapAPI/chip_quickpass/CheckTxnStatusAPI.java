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
import java.util.Calendar;
import java.util.HashMap;

import cn.zt.pos.db.TellersDbHelper;
import cn.zt.pos.db.obj.PreAuthModel;
import cn.zt.pos.inf.ResponseInterface;
import cn.zt.pos.utils.PrefUtil;
import cn.zt.pos.utils.SOAPURL;
import cn.zt.pos.utils.UtilsLocal;


/**
 * Created by deadlydragger on 12/25/18.
 */

public class CheckTxnStatusAPI extends AsyncTask<String, String, SoapObject> {
    String stan, crrn, txnAmt;
    Context context;
    //    ProgressDialog progressDialog;
    ResponseInterface responseInterface;
    int count = 0;
    TellersDbHelper tellersDbHelper;
    static long timer = 0;
    String TAG = "dinesh";

    public CheckTxnStatusAPI(Context context, String txnAmt, String stan, String crrn, ResponseInterface responseInterface) {
        this.context = context;
        this.txnAmt = txnAmt;
        this.stan = stan;
        this.crrn = crrn;
        this.responseInterface = responseInterface;
        tellersDbHelper = new TellersDbHelper(context);
        PrefUtil.getSharedPreferences(context);

//        Log.d(TAG, "current time: " + System.currentTimeMillis());
//        Log.d(TAG, "status time: " + ((timer + (45 * 1000)) <  System.currentTimeMillis()));

    }

    @Override
    protected SoapObject doInBackground(String... strings) {
        try {
            if (timer == 0) {
                timer = System.currentTimeMillis();
                Log.d(TAG, "assigned time: " + timer);
                Log.d(TAG, "total time: " + (timer + (45 * 1000)));

            }

            if ((timer + (40 * 1000)) < System.currentTimeMillis()) {
                Log.d(TAG, "total time: " + (timer + (40 * 1000) + "  Current Time+" + System.currentTimeMillis()));

                return null;
            } else {
                return getWS(txnAmt, stan, crrn);
            }
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
            count = PrefUtil.getCount() + 1;
            PrefUtil.setCount(count);
            Log.d("dinesh", s.toString());
//        progressDialog.dismiss();
            JSONObject object = new JSONObject(s.getProperty(0).toString());
            if (object.getString("respCode").equals("LO")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new CheckTxnStatusAPI(context, txnAmt, stan, crrn, responseInterface).execute();
                    }
                }, UtilsLocal.TIME);

            } else if (object.getString("respCode").equals("00")/*||object.getString("respCode").equals("55")*/) {
                timer = 0;
                PrefUtil.setCrrn(crrn);
//                new TransactionInfo().setReferNo(crrn);

                String emv = object.getString("emvData");//720B86098418000004263DD2759F36020014910A516C8464A7DA8C583030
                String tag72 = "", tag55, tag91 = "", tag38, tag9f36 = "";
                tag38 = object.getString("de38");
                if (!object.getString("emvData").equalsIgnoreCase("EMV Data Not Present")) {
                    tag55 = object.getString("emvData");

                }
                tag55 = emv;
                String remainingLength = "", lengthtag = "";
                if (emv.startsWith("72")) {
                    tag72 = emv.replaceAll("72", "");
                    switch (tag72.substring(0, 2)) {
                        case "0A":
                            tag72 = tag72.substring(2, 22);
                            lengthtag = "720A";
                            break;
                        case "0B":
                            tag72 = tag72.substring(2, 24);
                            lengthtag = "720B";
                            break;
                        case "0C":
                            tag72 = tag72.substring(2, 26);
                            lengthtag = "720C";
                            break;
                        case "0D":
                            tag72 = tag72.substring(2, 28);
                            lengthtag = "720D";
                            break;
                        case "0E":
                            tag72 = tag72.substring(2, 30);
                            lengthtag = "720E";
                            break;
                        case "0F":
                            tag72 = tag72.substring(2, 22);
                            lengthtag = "720F";
                            break;
                    }
                    remainingLength = "";
                    remainingLength = emv.replaceAll(lengthtag + "" + tag72, "");

                } else {
                    remainingLength = emv;
                }

                try {
                    if (remainingLength.startsWith("9F36")) {
                        String tagl = "";
                        remainingLength = remainingLength.replaceAll("9F36", "");
                        switch (remainingLength.substring(0, 2)) {
                            case "01":
                                tag9f36 = remainingLength.substring(2, 4);
                                tagl = "01";
                                break;
                            case "02":
                                tag9f36 = remainingLength.substring(2, 6);
                                tagl = "02";
                                break;
                            case "03":
                                tag9f36 = remainingLength.substring(2, 8);
                                tagl = "03";
                                break;
                            case "04":
                                tag9f36 = remainingLength.substring(2, 10);
                                tagl = "04";
                                break;
                            case "0E":
                                tag9f36 = remainingLength.substring(2, 30);
                                tagl = "0E";
                                break;
                            case "0F":
                                tag9f36 = remainingLength.substring(2, 22);
                                tagl = "0F";
                                break;
                        }
                        remainingLength = remainingLength.replaceAll(tagl + "" + tag9f36, "").trim();

                    }
                    Log.d(TAG, "parseEmv: tag9f36 :" + tag9f36);
                    Log.d(TAG, "parseEmv: remaining length " + remainingLength);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    if (remainingLength.startsWith("91")) {
                        remainingLength = remainingLength.substring(2, remainingLength.length());

                        switch (remainingLength.substring(0, 2)) {
                            case "08":
                                tag91 = remainingLength.substring(2, 18);
                                break;
                            case "0A":
                                tag91 = remainingLength.substring(2, 22);
                                break;
                            case "0B":
                                tag91 = remainingLength.substring(2, 24);
                                break;
                            case "0C":
                                tag91 = remainingLength.substring(2, 26);
                                break;
                            case "0D":
                                tag91 = remainingLength.substring(2, 28);
                                break;
                            case "0E":
                                tag91 = remainingLength.substring(2, 30);
                                break;
                            case "0F":
                                tag91 = remainingLength.substring(2, 22);
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                PrefUtil.setRefNumber(crrn);
                PrefUtil.setD38(tag38);
                PrefUtil.getSharedPreferences(context);
                PrefUtil.setStan(stan);
                Log.d(TAG, "onPostExecute: tag72 " + tag72 + " tag9f36 " + tag9f36 + " tag 91" + tag91);
                Log.d("dinesh", "onPostExecute: " + object.getString("respCode"));
                if (PrefUtil.getType().equalsIgnoreCase("preAuth")) {
                    tellersDbHelper.insertPreauthInfo(new PreAuthModel(tag38, Calendar.getInstance().getTime().toString(), txnAmt, PrefUtil.getCardNo(), PrefUtil.getEmvPayload()));
                }
                if (PrefUtil.getType().equalsIgnoreCase("preAuthComplete") || PrefUtil.getType().equalsIgnoreCase("preAuthCancel")) {
                    tellersDbHelper.deleteAuthComplete(PrefUtil.getDe38());
                    PrefUtil.setTxnType("00");
                    tag72 = "";
                    tag91 = "";
                    tag55 = "";

                }
                PrefUtil.setdisAmount(object.getString("disAmount"));
                PrefUtil.setpaidAmount(object.getString("paidAmount"));
                String amunt = "Txn Amount : " + txnAmt + "\n Discount Amount : " + object.getString("disAmount") + "\n Paid Amount : " + object.getString("paidAmount");
                responseInterface.loginSuccess(object.getString("respCode"), tag38, tag55, tag91, amunt, tag72);// emv.substring(emv.length()-18)


            } else {
                timer = 0;
                responseInterface.onfail();
            }
        } catch (Exception e) {
            timer = 0;
            e.printStackTrace();
            Log.d(TAG, "Prepared to send reversal");
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
