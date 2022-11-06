package cn.zt.pos.cashinput;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.zt.pos.BaseActivity;
import cn.zt.pos.R;
import cn.zt.pos.swapcard.SwapMsgCardActivity;
import cn.zt.pos.utils.PrefUtil;
import cn.zt.upi.GenerateUPIQRActivity;

public class CashInputActivity extends BaseActivity implements View.OnClickListener {

    private TextView inputMoneyYuanText,inputMoneyFenText;

    private long inputMoney = 0;

    private Bundle bundle;
    private ImageView btn_num_clear,btn_brush_cashier;
    private LinearLayout btn_confirm,qr_pay;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cashier_input_money);
        //MyApplication.activityList.add(this);
        initView();


        PrefUtil.getSharedPreferences(CashInputActivity.this);
        if (PrefUtil.getType().equalsIgnoreCase("0")){

        }

    }

    protected void initView() {
        ImageView titleBackImage = (ImageView) findViewById(R.id.titleBackImage);
        titleBackImage.setOnClickListener(this);
        TextView titleNameText = (TextView) findViewById(R.id.titleNameText);
        titleNameText.setText(getResources().getString(R.string.casher));
        inputMoneyYuanText = (TextView) findViewById(R.id.inputMoneyYuanText);
        inputMoneyFenText = (TextView) findViewById(R.id.inputMoneyFenText);
        Button btn_num00 = (Button) findViewById(R.id.btn_num00);
        btn_num00.setOnClickListener(this);
        Button btn_num0 = (Button) findViewById(R.id.btn_num0);
        btn_num0.setOnClickListener(this);
        Button btn_num1 = (Button) findViewById(R.id.btn_num1);
        btn_num1.setOnClickListener(this);
        Button btn_num2 = (Button) findViewById(R.id.btn_num2);
        btn_num2.setOnClickListener(this);
        Button btn_num3 = (Button) findViewById(R.id.btn_num3);
        btn_num3.setOnClickListener(this);
        Button btn_num4 = (Button) findViewById(R.id.btn_num4);
        btn_num4.setOnClickListener(this);
        Button btn_num5 = (Button) findViewById(R.id.btn_num5);
        btn_num5.setOnClickListener(this);
        Button btn_num6 = (Button) findViewById(R.id.btn_num6);
        btn_num6.setOnClickListener(this);
        Button btn_num7 = (Button) findViewById(R.id.btn_num7);
        btn_num7.setOnClickListener(this);
        Button btn_num8 = (Button) findViewById(R.id.btn_num8);
        btn_num8.setOnClickListener(this);
        Button btn_num9 = (Button) findViewById(R.id.btn_num9);
        btn_num9.setOnClickListener(this);
        btn_num_clear = (ImageView) findViewById(R.id.btn_num_clear);
        btn_num_clear.setOnClickListener(this);
//        btn_confirm = (LinearLayout) findViewById(R.id.btn_confirm);
//        btn_confirm.setOnClickListener(this);
        qr_pay=findViewById(R.id.qr_pay);
        qr_pay.setOnClickListener(this);
        findViewById(R.id.btn_card_confirm).setOnClickListener(this);

     /*   if (WeiPassGlobal.getTransactionInfo().getTransType()==TradeInfo.Type_Sale||WeiPassGlobal.getTransactionInfo().getTransType()==TradeInfo.Type_Refund){
            qr_pay.setVisibility(View.VISIBLE);
            View view= (View)findViewId(R.id.view_line);
            view.setVisibility(View.VISIBLE);
        }*/
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.titleBackImage:
//                LedUtils.getInstance().close();
                finish();
                break;
            case R.id.btn_num0:
            case R.id.btn_num1:
            case R.id.btn_num2:
            case R.id.btn_num3:
            case R.id.btn_num4:
            case R.id.btn_num5:
            case R.id.btn_num6:
            case R.id.btn_num7:
            case R.id.btn_num8:
            case R.id.btn_num9:

                if((inputMoney+"").length()<=11) {
                    inputMoney = Long.parseLong(inputMoney + ((Button) v).getText().toString());

                    inputMoneySetText();
                }
                break;

            case R.id.btn_num00:

                if((inputMoney+"").length()<=10) {
                    inputMoney = Long.parseLong(inputMoney + ((Button) v).getText().toString());

                    inputMoneySetText();
                }
                break;

            case R.id.btn_num_clear:
                if(inputMoney>0) {
                    inputMoney = inputMoney / 10;
                    inputMoneySetText();
                }
                break;

            case R.id.btn_card_confirm:
                PrefUtil.getSharedPreferences(CashInputActivity.this);



                PrefUtil.setAmount(decimalFormate(String.valueOf(Double.valueOf(inputMoney) / 100))+"");
                startActivity(getIntent().setClass(CashInputActivity.this, SwapMsgCardActivity.class));
                finish();

                break;
            case R.id.qr_pay:
                PrefUtil.getSharedPreferences(CashInputActivity.this);
                PrefUtil.setAmount(decimalFormate(String.valueOf(Double.valueOf(inputMoney) / 100))+"");
                startActivity(getIntent().setClass(CashInputActivity.this, GenerateUPIQRActivity.class));
                finish();
                break;

            default:
                break;
        }
    }
    private void inputMoneySetText()
    {
        String inputMoneyString = MoneyUtil.fen2yuan(inputMoney);
        inputMoneyYuanText.setText(inputMoneyString.substring(0,inputMoneyString.length()-2));
        inputMoneyFenText.setText(inputMoneyString.substring(inputMoneyString.length()-2));

    }

    public String decimalFormate(String value){
        String decimal="";
        try {
            decimal= String.format("%.2f", Double.parseDouble(value));

        }catch (Exception e){
            e.printStackTrace();
        }
        return decimal;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
