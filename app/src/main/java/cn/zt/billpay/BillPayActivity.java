package cn.zt.billpay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.zt.billpay.phone.PhonePayActivity;
import cn.zt.http.APICall;
import cn.zt.http.APIInterface;
import cn.zt.http.HttpUrl;
import cn.zt.pos.R;
import cn.zt.pos.utils.PrefUtil;

public class BillPayActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppThemeMainNIC);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility);
        findViewById(R.id.titleBackImage).setOnClickListener(this);
        TextView titleNameText = findViewById(R.id.titleNameText);
        titleNameText.setText("Phone Payment");
        findViewById(R.id.phoneBill).setOnClickListener(this);
        TextView balanceWallet = findViewById(R.id.balanceWallet);
        balanceWallet.setText("NPR xx.xx");
        setBalance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBackImage:
                finish();
                break;
            case R.id.phoneBill:
                startActivity(new Intent(BillPayActivity.this, PhonePayActivity.class));
                break;
        }
    }

    private void setBalance() {
        try {

            final FrameLayout progressBar = findViewById(R.id.idLoading);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_id", PrefUtil.getAppId());
            jsonObject.put("term_id", PrefUtil.getPayload());
            jsonObject.put("dev_token", "");
            jsonObject.put("lat", "27.71498390");
            jsonObject.put("lng", "85.31009810");

            new APICall(BillPayActivity.this, progressBar, jsonObject, HttpUrl.BALANCE_API, new APIInterface() {
                @Override
                public void onSuccess(String s) {
                    TextView balanceWallet = (TextView) findViewById(R.id.balanceWallet);
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(s);
                        balanceWallet.setText("NPR " + object.getString("wallet_amount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailed(String s) {
                    progressBar.setVisibility(View.GONE);
                }
            }).execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
