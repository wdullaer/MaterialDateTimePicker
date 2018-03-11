package com.wdullaer.datetimepickerexample;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity
{
    ViewPager viewPager;
    PickerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new PickerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        for(int i=0; i < adapter.getCount(); i++) //noinspection ConstantConditions
            tabLayout.getTabAt(i).setText(adapter.getTitle(i));
    }

    private class PickerAdapter extends FragmentPagerAdapter {
        private static final int NUM_PAGES = 2;
        Fragment timePickerFragment;
        Fragment datePickerFragment;

        PickerAdapter(FragmentManager fm) {
            super(fm);
            timePickerFragment = new TimePickerFragment();
            datePickerFragment = new DatePickerFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return timePickerFragment;
                case 1:
                default:
                    return datePickerFragment;
            }
        }

        int getTitle(int position) {
            switch(position) {
                case 0:
                    return R.string.tab_title_time;
                case 1:
                default:
                    return R.string.tab_title_date;
            }
        }
    }
}
