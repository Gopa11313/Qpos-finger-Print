package cn.zt.billpay.phone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.zt.billpay.BillPaymentActivity;
import cn.zt.pos.R;

public class RechargeFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    String[] carrierName = {"NTC PIN", "Smartcell PIN", "UTL PIN"};
    int carrierIcon[] = {R.drawable.nct_logo, R.drawable.smart_cell, R.drawable.utl};
    Spinner spinner;
    private String bytecode,amount,carrier;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spinner carrier_spinner = (Spinner) view.findViewById(R.id.carrier_spinner);
        carrier_spinner.setOnItemSelectedListener(this);
        spinner = (Spinner) view.findViewById(R.id.amount);
        Button confirm = (Button) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(this);

        SpineerDapter customAdapter = new SpineerDapter(getActivity(), carrierIcon, carrierName);
        carrier_spinner.setAdapter(customAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recharge_fragment, container, false);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (carrierName[position]) {
            case "NTC PIN":
                setNTC();
                break;

            case "Smartcell PIN":
                setSmartcell();
                break;
            case "UTL PIN":
                setUtl();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setNTC() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.ntc_epin, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                amount=parent.getItemAtPosition(position).toString();
                switch (parent.getItemAtPosition(position).toString()) {
                    case "100.00":
                        bytecode = "612";
                        break;
                    case "200.00":
                        bytecode = "606";
                        break;
                    case "500.00":
                        bytecode = "607";
                        break;
                    case "1000.00":
                        bytecode = "608";
                        break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSmartcell() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.smrt_epin, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                amount=parent.getItemAtPosition(position).toString();
                switch (parent.getItemAtPosition(position).toString()) {


                    case "200.00":
                        bytecode = "604";
                        break;

                    case "500.00":
                        bytecode = "605";
                        break;
                    case "50.00":
                        bytecode = "631";
                        break;
                    case "100.00":
                        bytecode = "603";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUtl() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.utl_epin, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                amount=parent.getItemAtPosition(position).toString();
                switch (parent.getItemAtPosition(position).toString()) {
                    case "100.00":
                        bytecode = "609";
                        break;
                    case "250.00":
                        bytecode = "610";
                        break;
                    case "500.00":
                        bytecode = "611";
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                Intent intent = new Intent(getActivity(), BillPaymentActivity.class);
                intent.putExtra("money", amount);
                intent.putExtra("cell_number", "1");
                intent.putExtra("byt_code", bytecode);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }
}
