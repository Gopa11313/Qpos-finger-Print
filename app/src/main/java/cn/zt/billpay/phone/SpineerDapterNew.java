package cn.zt.billpay.phone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.zt.pos.R;


public class SpineerDapterNew extends BaseAdapter {
    Context context;
    int carrierIcon[];
    String[] carrierName;
    LayoutInflater inflter;

    public SpineerDapterNew(Context applicationContext, int[] flags, String[] countryNames) {
        this.context = applicationContext;
        this.carrierIcon = flags;
        this.carrierName = countryNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return carrierIcon.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_carrier_spineer, null);
        ImageView icon = (ImageView) view.findViewById(R.id.itemIcon);
        TextView names = (TextView) view.findViewById(R.id.itemName);
        icon.setImageResource(carrierIcon[i]);
        names.setText(carrierName[i]);
        return view;
    }
}