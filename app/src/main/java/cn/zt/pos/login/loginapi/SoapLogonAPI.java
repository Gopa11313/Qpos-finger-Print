package cn.zt.pos.login.loginapi;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import cn.zt.pos.login.LoginActivity;
import cn.zt.pos.utils.LoginInterface;
import cn.zt.pos.utils.SOAPURL;


/**
 * Created by deadlydragger on 12/23/18.
 */

public class SoapLogonAPI extends AsyncTask<String, String, SoapObject> {
    String termId, merchId;
    LoginActivity activity;
    LoginInterface loginInterface;
    ProgressDialog progressDialog;


    public SoapLogonAPI(String termId, String merchId, LoginActivity activity, LoginInterface loginInterface) {
        this.termId = termId;
        this.merchId = merchId;
        this.activity = activity;
        this.loginInterface = loginInterface;
    }

    @Override
    protected SoapObject doInBackground(String... strings) {
        try {
            return getWS(termId, merchId);
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
    /*    progressDialog = new ProgressDialog(activity);
        progressDialog.show();*/
    }

    @Override
    protected void onPostExecute(SoapObject s) {
        super.onPostExecute(s);
//        progressDialog.dismiss();
        try {
            Log.d("dinesh",s.toString());

            if (!s.getProperty(0).toString().equalsIgnoreCase("99")) {
                loginInterface.loginSuccess(s.getProperty(0).toString(),"");
            } else {
                loginInterface.loginFailed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            loginInterface.loginFailed();
        }

    }

    private static SoapObject getWS(String termId, String merchantId) throws IOException, XmlPullParserException {
        // Get the SoapResult from the envelope body.
        final String SOAP_ACTION = "http://tempuri.org/ISoftNacService/Logon";

        final String OPERATION_NAME = "Logon";
        final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        final String SOAP_ADDRESS = SOAPURL.URL;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,
                OPERATION_NAME);
        //Use this to add parameters
        request.addProperty("termID", termId);
        request.addProperty("merchID", merchantId);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        Log.d("dinesh", "getWS: "+request.toString());

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        httpTransport.call(SOAP_ACTION, envelope);
        return (SoapObject) envelope.bodyIn;

    }
}