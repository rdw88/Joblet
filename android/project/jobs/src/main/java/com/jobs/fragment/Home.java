package com.jobs.fragment;


import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.activity.TabsPagerAdapter;
import com.jobs.utility.Global;
import com.rey.material.util.ThemeUtil;
import com.rey.material.widget.TabPageIndicator;

import org.json.JSONObject;

public class Home extends Fragment implements ActionBar.TabListener {
    private static final int MAIN_PAGE = 0;
    // TODO: Make gpsOn true whenever the gps is on
    public static boolean isGpsOn = false;
    private MainPagerAdapter adapter;
    private ViewPager pager;
    private TabsPagerAdapter mAdapter;

    private JSONObject data;

    private Button logout;

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
                v = LayoutInflater.from(getActivity()).inflate(R.layout.row_drawer, null);
                v.setOnClickListener(this);
            }

            v.setTag(position);
            Tab tab = (Tab)getItem(position);
            ((TextView)v).setText(tab.toString());

            if(tab == mSelectedTab) {
                v.setBackgroundColor(ThemeUtil.colorPrimary(getActivity(), 0));
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    public void onStart() {
        super.onStart();

        data = ((Global) getActivity().getApplicationContext()).getUserData();

        if (pager == null) {
            pager = (ViewPager) getActivity().findViewById(R.id.pager);
            tpi = (TabPageIndicator) getActivity().findViewById(R.id.main_tpi);

            adapter = new MainPagerAdapter(getActivity().getSupportFragmentManager(), mTabs);
            mAdapter = new TabsPagerAdapter(getActivity().getSupportFragmentManager());
            pager.setAdapter(adapter);
            tpi.setViewPager(pager, MAIN_PAGE);

            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageSelected(int page) {
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
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
        }
        
        pager.setCurrentItem(MAIN_PAGE);
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

    public class MainPagerAdapter extends FragmentPagerAdapter {
        Fragment[] mFragments;

        public MainPagerAdapter(FragmentManager fm, Tab[] tabs) {
            super(fm);
            mFragments = new Fragment[mTabs.length];
        }

        public Fragment getItem(int i) {
            switch (mTabs[i]) {
                case PROFILE:
                    mFragments[i] = new LandingPage();
                    break;
                case BROWSE:
                    mFragments[i] = new CheckListings();
                    break;
            }

            return mFragments[i];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs[position].toString().toUpperCase();
        }

        public int getCount() {
            return mFragments.length;
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
