package cn.zt.billpay;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.zt.pos.R;

public class BillPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppThemeMainNIC);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
    }
}
