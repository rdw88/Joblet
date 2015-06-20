package com.jobs.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.jobs.R;
import com.jobs.backend.Address;
import com.jobs.backend.Profile;
import com.jobs.backend.Resource;
import com.jobs.fragment.CheckListings;
import com.jobs.fragment.CreateListing;
import com.jobs.fragment.LandingPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;


public class Main extends FragmentActivity implements ActionBar.TabListener {
    private static final int MAIN_PAGE = 1;
    private MainPagerAdapter adapter;
    private ViewPager pager;
    private TabsPagerAdapter mAdapter;
    private String data;
    private String notificationData;
    private Button logout;
    private String[] tabs = {"Create", "Profile", "Browse"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        pager = (ViewPager) findViewById(R.id.pager);
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //changes tab background
        actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.tab_background));


        pager.setAdapter(mAdapter);

        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        data = getIntent().getExtras().getString("data");
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            public void onPageSelected(int page) {
                getActionBar().setTitle(Resource.PAGE_ORDER[page]);
                final InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pager.getWindowToken(), 0);
                getActionBar().setSelectedNavigationItem(page);
            }

            public void onPageScrollStateChanged(int page) {
            }
            public void onPageScrolled(int page, float f, int i) {
            }

        });

        pager.setAdapter(adapter);
        pager.setCurrentItem(MAIN_PAGE);
    }

    public void onStart() {
        super.onStart();

        new AsyncTask<String, Void, String>() {
            private JSONArray array;

            protected String doInBackground(String... params) {
                String email = null;
                String password = null;

                try {
                    JSONObject obj = new JSONObject(data);
                    email = obj.getString("email");
                    password = obj.getString("password");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                array = Profile.getNotifications(email, password);
                return null;
            }

            public void onPostExecute(String res) {
                notificationData = array.toString();
            }
        }.execute();
    }

    private void logout() {
        new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... params) {
                String email = "";
                try {
                    JSONObject d = new JSONObject(data);
                    email = d.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Profile.logout(email);
                return null;
            }

            protected void onPostExecute(String res) {
                setResult(0xff);
                finish();
            }
        }.execute();
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow, menu);
        return super.onCreateOptionsMenu(menu);
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

    public void onBackPressed() {
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.logout:
                logout();
                return true;

            case R.id.notifications:
                Intent intent = new Intent(Main.this, Notifications.class);
                intent.putExtra("notification_data", notificationData);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
