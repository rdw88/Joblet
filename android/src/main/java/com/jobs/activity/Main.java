package com.jobs.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;


import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.Resource;
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
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        data = getIntent().getExtras().getString("data");
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            public void onPageSelected(int page) {
                getActionBar().setTitle(Resource.PAGE_ORDER[page]);
                final InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pager.getWindowToken(), 0);

            }

            public void onPageScrollStateChanged(int page) {
            }
            public void onPageScrolled(int page, float f, int i) {
            }

        });

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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
