package com.jobs.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;


import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.Profile;
import com.jobs.fragment.CheckListings;
import com.jobs.fragment.EditProfile;
import com.jobs.fragment.LandingPage;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.rey.material.app.ToolbarManager;
import com.rey.material.util.ThemeUtil;
import com.rey.material.widget.TabPageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

//FragmentActivity was what it extended
public class Main extends AppCompatActivity implements ActionBar.TabListener {
    private static final int MAIN_PAGE = 0;
    // TODO: Make gpsOn true whenever the gps is on
    public static boolean isGpsOn = false;
    private MainPagerAdapter adapter;
    private ViewPager pager;
    private TabsPagerAdapter mAdapter;
    private DrawerAdapter mDrawerAdapter;
    private Toolbar mToolbar;
    private Drawer drawer = null;
    private ToolbarManager mToolbarManager;
    private String data, notificationData;
    private Button logout;
    private Fragment toStart;

    private TabPageIndicator tpi;

    /* I'm removing the create tab because that functionality will be accessed by using
     * the drawer I still need to add. */
    private Tab[] mTabs = new Tab[]{Tab.PROFILE, Tab.BROWSE};

    // EXPERIMENT -> Seems to have worked
    class DrawerAdapter extends BaseAdapter implements View.OnClickListener {

        private Tab mSelectedTab;

        public void setSelected(Tab tab){
            if(tab != mSelectedTab){
                mSelectedTab = tab;
                //notifyDataSetInvalidated();
            }
        }

        public Tab getSelectedTab(){
            return mSelectedTab;
        }

        @Override
        public int getCount() {
            return mTabs.length;
        }

        @Override
        public Object getItem(int position) {
            return mTabs[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(v == null) {
                v = LayoutInflater.from(Main.this).inflate(R.layout.row_drawer, null);
                v.setOnClickListener(this);
            }

            v.setTag(position);
            Tab tab = (Tab)getItem(position);
            ((TextView)v).setText(tab.toString());

            if(tab == mSelectedTab) {
                v.setBackgroundColor(ThemeUtil.colorPrimary(Main.this, 0));
                ((TextView)v).setTextColor(0xFFFFFFFF);
            }
            else {
                v.setBackgroundResource(0);
                ((TextView)v).setTextColor(0xFF000000);
            }

            return v;
        }

        @Override
        public void onClick(View v) {
            int position = (Integer)v.getTag();
            pager.setCurrentItem(position);
            //dl_navigator.closeDrawer(fl_drawer);
        }
    }

    // END EXPERIMENT

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        pager = (ViewPager) findViewById(R.id.pager);

        tpi = (TabPageIndicator) findViewById(R.id.main_tpi);
        adapter = new MainPagerAdapter(getSupportFragmentManager(), mTabs);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tpi.setViewPager(pager, MAIN_PAGE);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.softwhite));
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withFullscreen(false)
                .withActionBarDrawerToggle(true)
                .withSelectedItem(-1)
                .withToolbar(mToolbar)
                .withTranslucentStatusBar(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Edit Profile").withIcon(R.drawable.icon_user_32),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Active Jobs").withIcon(R.drawable.icon_briefcase_32),
                        new PrimaryDrawerItem().withName("Active Listings").withIcon(R.drawable.icon_list_32),
                        new PrimaryDrawerItem().withName("My Bids").withIcon(R.drawable.icon_gavel_32),
                        new PrimaryDrawerItem().withName("Watchlist").withIcon(R.drawable.icon_star_32)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int position, long id, IDrawerItem iDrawerItem) {
                        switch(position){
                            case 0:
                                Intent intent1 = new Intent(Main.this, EditProfile.class);
                                startActivity(intent1);
                                break;
                            case 1:
                                //Intent intent = new Intent(Main.this, MyActiveJobs.class);
                                //startActivity(intent);
                                break;
                            case 2:
                                Intent intent2 = new Intent(Main.this, MyListings.class);
                                startActivity(intent2);
                                break;
                            case 3:
                                Intent intent3 = new Intent(Main.this, MyListingBids.class);
                                startActivity(intent3);
                                break;
                            case 4:
                                //Intent intent4 = new Intent(Main.this, Watchlist.class);
                                //startActivity(intent4);
                                break;
                        }
                        return false;
                    }


                })
        .build();
        data = getIntent().getExtras().getString("data");


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
                final InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pager.getWindowToken(), 0);
                //getActionBar().setSelectedNavigationItem(page);
                tpi.setCurrentItem(page);
                tpi.setSelected(true);
            }

            public void onPageScrollStateChanged(int page) {
            }
            public void onPageScrolled(int page, float f, int i) {
            }

        });

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
        pager.setCurrentItem(tab.getPosition());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public class MainPagerAdapter extends FragmentPagerAdapter {
        Fragment[] mFragments;
        Tab[] tabs;
        public MainPagerAdapter(FragmentManager fm, Tab[] tabs) {
            super(fm);
            mFragments = new Fragment[mTabs.length];
            mTabs = tabs;
        }

        public Fragment getItem(int i) {
            if(mFragments[i] == null) {
                switch (mTabs[i]){
                    case PROFILE:
                        mFragments[i] = new LandingPage();
                        break;
                    case BROWSE:
                        mFragments[i] = new CheckListings();
                        break;
                }
            }
            Bundle args = new Bundle();
            args.putString("data", data);
            mFragments[i].setArguments(args);
            return mFragments[i];
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mTabs[position].toString().toUpperCase();
        }

        public int getCount() {
            return mFragments.length;
        }
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.morevert:
                View menuItemView = findViewById(R.id.morevert);
                PopupMenu popupMenu = new PopupMenu(this, menuItemView);
                popupMenu.getMenuInflater()
                        .inflate(R.menu.overflowlist, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.notifications:
                                Intent intent = new Intent(Main.this, Notifications.class);
                                intent.putExtra("notification_data", notificationData);
                                startActivity(intent);
                                return true;

                            case R.id.logout:
                                logout();
                                return true;

                        }
                        return true;
                    }
                });
                popupMenu.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** The enum that will define the individual tabs */
    public enum Tab {
        PROFILE ("Profile"),
        BROWSE ("Browse");
        private final String name;
        Tab(String s){
            name = s;
        }
        public boolean equalsName(String toCompare){
            return (toCompare != null) && name.equals(toCompare);
        }

        public String toString(){
            return name;
        }
    }

    public static boolean isGpsOn(){
        return isGpsOn;
    }
}
