package com.jobs.activity;

import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.fragment.CheckListings;
import com.jobs.fragment.CreateListing;
import com.jobs.fragment.LandingPage;

public class Main extends FragmentActivity  {
    private static final int MAIN_PAGE = 1;
    private MainPagerAdapter adapter;
    private ViewPager pager;
    private String data;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = getIntent().getExtras().getString("data");
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(MAIN_PAGE);


    }


    public class MainPagerAdapter extends FragmentPagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int i) {
            Fragment fragment = null;

            if (i == 0) {
                fragment = new CreateListing();

            } else if (i == 1) {
                fragment = new LandingPage();

            } else if (i == 2) {
                fragment = new CheckListings();

            }

            Bundle args = new Bundle();
            args.putString("data", data);
            fragment.setArguments(args);

            return fragment;
        }

        public int getCount() {
            return 3;
        }
    }

}
