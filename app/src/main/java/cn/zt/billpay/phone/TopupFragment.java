package cn.zt.billpay.phone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import cn.zt.Utils;
import cn.zt.billpay.BillPaymentActivity;
import cn.zt.pos.BuildConfig;
import cn.zt.pos.R;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class TopupFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private LinearLayout amountLayout;
    private EditText phone;
    private String byteCode;
    private String selectedItem = "";
    private static final int PICK_CONTACT = 1;
    private boolean isfine = false;
  private Context context;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       context=getActivity();
        amountLayout = view.findViewById(R.id.amountLayout);
        amountLayout.setVisibility(View.GONE);
        final Spinner spinner = getView().findViewById(R.id.amount);
        final TextView ncell = view.findViewById(R.id.topupNcell);
        final TextView ntcPrepaid = view.findViewById(R.id.topupNtc);
        final TextView smartCell = view.findViewById(R.id.topupSmartcell);
        final TextView ntpostpaid = view.findViewById(R.id.ntcPostPaid);
        final TextView ntcPSTN = view.findViewById(R.id.ntcPSTN);
        final TextView utlTextView = view.findViewById(R.id.utl);
        ImageView contact_picker = view.findViewById(R.id.contact_picker);
        contact_picker.setOnClickListener(this);
        Button confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(this);


        phone = (EditText) getView().findViewById(R.id.phone);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phone.setError(null);
                String phone = s.toString();
                isfine = false;
                if (phone.length() == 10) {
                    String first_three = phone.substring(0, 3);
                    switch (first_three) {
                        case "980":
                        case "981":
                        case "982":
                            isfine = true;
                            byteCode = "201";
                            amountLayout.setVisibility(View.VISIBLE);
                            ncell.setBackgroundResource(R.drawable.round_topuup_selected);
                            ntcPrepaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            smartCell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntpostpaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPSTN.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            utlTextView.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            new Utils().hideSoftkey(getActivity());
                            setNcell(spinner);
                            break;
                        case "974":
                            byteCode = "209";
                            isfine = true;
                            amountLayout.setVisibility(View.VISIBLE);
                            ntcPrepaid.setBackgroundResource(R.drawable.round_topuup_selected);

                            ncell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            smartCell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntpostpaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPSTN.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            utlTextView.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            new Utils().hideSoftkey(getActivity());
                            setNTC(spinner);
                            break;

                        case "984":
                        case "986":
                      /*  case "974":*/
                            byteCode = "203";
                            isfine = true;
                            amountLayout.setVisibility(View.VISIBLE);
                            ntcPrepaid.setBackgroundResource(R.drawable.round_topuup_selected);

                            ncell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            smartCell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntpostpaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPSTN.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            utlTextView.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            new Utils().hideSoftkey(getActivity());
                            setNTC(spinner);
                            break;

                        case "975":
                            isfine = true;
                            byteCode = "210";
                            amountLayout.setVisibility(View.VISIBLE);
                            ntpostpaid.setBackgroundResource(R.drawable.round_topuup_selected);

                            ncell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            smartCell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPrepaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPSTN.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            utlTextView.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            new Utils().hideSoftkey(getActivity());
                            setNTPostpaid(spinner);
                            break;
                        case "985":
                      /*  case "975":*/
                            isfine = true;
                            byteCode = "205";
                            amountLayout.setVisibility(View.VISIBLE);
                            ntpostpaid.setBackgroundResource(R.drawable.round_topuup_selected);

                            ncell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            smartCell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPrepaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPSTN.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            utlTextView.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            new Utils().hideSoftkey(getActivity());
                            setNTPostpaid(spinner);
                            break;
                        case "961":
                        case "960":
                        case "962":
                        case "988":
                            isfine = true;
                            byteCode = "207";
                            amountLayout.setVisibility(View.VISIBLE);
                            smartCell.setBackgroundResource(R.drawable.round_topuup_selected);

                            ncell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntpostpaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPrepaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPSTN.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            utlTextView.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            new Utils().hideSoftkey(getActivity());
                            setSmartcell(spinner);
                            break;
                        case "972":
                            isfine = true;
                            byteCode = "206";
                            amountLayout.setVisibility(View.VISIBLE);
                            utlTextView.setBackgroundResource(R.drawable.round_topuup_selected);

                            ncell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntpostpaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPrepaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPSTN.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            smartCell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            new Utils().hideSoftkey(getActivity());
                            setUTL(spinner);
                            break;
                        default:
                            isfine = false;
                            utlTextView.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ncell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntpostpaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPrepaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            ntcPSTN.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            smartCell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                            new Utils().hideSoftkey(getActivity());
                            break;
                    }
                } else if (phone.length() == 9 && phone.startsWith("0")) {
                    isfine = true;
                    byteCode = "204";
                    amountLayout.setVisibility(View.VISIBLE);
                    smartCell.setBackgroundResource(R.drawable.round_topuup_nonselected);


                    ntcPSTN.setBackgroundResource(R.drawable.round_topuup_selected);
                    ncell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                    ntpostpaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                    ntcPrepaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                    new Utils().hideSoftkey(getActivity());
                    setNTPSTN(spinner);
                } else {
                    isfine = false;
                    utlTextView.setBackgroundResource(R.drawable.round_topuup_nonselected);
                    ncell.setBackgroundResource(R.drawable.round_topuup_nonselected);
                    ntpostpaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                    ntcPrepaid.setBackgroundResource(R.drawable.round_topuup_nonselected);
                    ntcPSTN.setBackgroundResource(R.drawable.round_topuup_nonselected);
                    smartCell.setBackgroundResource(R.drawable.round_topuup_nonselected);
//                    new Utility().hideSoftkey(getActivity());
                }
            }
        });
        spinner.setOnItemSelectedListener(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_topup, container, false);
    }

    private void setNcell(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.ncell_recharge_amount, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void setNTC(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.ntc_prepaid, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setSmartcell(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.smart_cell, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setUTL(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.utl, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setNTPostpaid(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.ntc_postpaid, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setNTPSTN(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.landline, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedItem = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getActivity().managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getActivity().getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex("data1")).replace("", BuildConfig.FLAVOR).replace("+977", BuildConfig.FLAVOR).replace("-", BuildConfig.FLAVOR);

                            phone.setText(cNumber.trim());
                            phone.clearFocus();
                            if (cNumber.trim().length() < 11) {

                                phone.setSelection(cNumber.trim().length());
                            }

                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_picker:
                phone.setText("");
                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS};
                    if (!hasPermissions(getContext(), PERMISSIONS)) {
                        requestPermissions(PERMISSIONS, 11);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, PICK_CONTACT);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                }

                break;
            case R.id.confirm:
                if (phone.getText().toString().length() >= 9 && isfine == true) {
                    Intent intent = new Intent(getContext(), BillPaymentActivity.class);
                    intent.putExtra("money", selectedItem);
                    intent.putExtra("cell_number", phone.getText().toString());
                    intent.putExtra("byt_code", byteCode);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    phone.setError("Enter valid number");
                }

                break;
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (checkSelfPermission(context, permission) != PermissionChecker.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 11:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);

                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getActivity().getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "Go to setting and give camera permission.", Toast.LENGTH_LONG).show();
                }

                break;
        }


    }
}
