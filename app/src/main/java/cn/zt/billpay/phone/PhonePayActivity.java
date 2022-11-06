package cn.zt.billpay.phone;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import cn.zt.pos.R;

public class PhonePayActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tablayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppThemeMainNIC);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        findViewById(R.id.titleBackImage).setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.view_pager_phone);
        tablayout = (TabLayout) findViewById(R.id.phone_tab_layout_design);
        tablayout.setupWithViewPager(viewPager);
        phoneViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBackImage:
                finish();
                break;

        }
    }



    private void phoneViewPager(ViewPager viewPager){

        viewPagerAdapter= new ViewPagerAdapter(getSupportFragmentManager(),2);
        viewPagerAdapter.addFragment(new TopupFragment(),"Phone Top Up");
        viewPagerAdapter.addFragment(new RechargeFragment(),"recharge PIN");
        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
