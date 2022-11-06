package cn.zt.http;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import org.json.JSONObject;

import cn.zt.billpay.BillPayActivity;


public class APICall extends AsyncTask<String, String, String> {
    FrameLayout progressBar;
   private BillPayActivity context;
    private JSONObject jsonObject;
    private APIInterface apiInterface;
    private String url;

    public APICall(BillPayActivity context, FrameLayout progressBar, JSONObject jsonObject, String url, APIInterface apiInterface){
        this.context=context;
        this.progressBar=progressBar;
        this.jsonObject=jsonObject;
        this.url=url;
        this.apiInterface=apiInterface;
    }
    @Override
    protected String doInBackground(String... strings) {
        Log.d(HttpUrl.TAG, "doInBackground: "+jsonObject.toString());

        return HttpUrlConnection.sendHTTPData(HttpUrl.BASE_URL+url,jsonObject);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            Log.d(HttpUrl.TAG, "onPostExecute: "+s);
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.getString("status").equals("00")){
                apiInterface.onSuccess(jsonObject.getJSONArray("data").getJSONObject(0).toString());
            }else {
                apiInterface.onFailed(jsonObject.getString("message"));

            }
        }catch (Exception e){
            apiInterface.onFailed("Network Server Error.");
        }
    }
}
