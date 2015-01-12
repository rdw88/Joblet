package com.jobs.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;


import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.text.style.TypefaceSpan;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.Resource;
import com.jobs.fragment.CheckListings;
import com.jobs.fragment.CreateListing;
import com.jobs.fragment.LandingPage;
import java.lang.reflect.Field;


public class Main extends FragmentActivity implements ActionBar.TabListener {
    private static final int MAIN_PAGE = 1;
    private MainPagerAdapter adapter;
    private ViewPager pager;
    private TabsPagerAdapter mAdapter;
    private String data;
    private Button logout;
    private String[] tabs = {"Create", "Profile", "Browse"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpannableString s = new SpannableString("My Title");
        s.setSpan(new TypefaceSpan(this, "Roboto-Black.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        pager = (ViewPager) findViewById(R.id.pager);
        final ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);
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

        SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_data", data);
        editor.apply();

        pager.setAdapter(adapter);
        pager.setCurrentItem(MAIN_PAGE);
    }

    public static class TypefaceSpan extends MetricAffectingSpan {
        /** An <code>LruCache</code> for previously loaded typefaces. */
        private static LruCache<String, Typeface> sTypefaceCache =
                new LruCache<String, Typeface>(12);

        private Typeface mTypeface;

        /**
         * Load the {@link Typeface} and apply to a {@link Spannable}.
         */
        public TypefaceSpan(Context context, String typefaceName) {
            mTypeface = sTypefaceCache.get(typefaceName);

            if (mTypeface == null) {
                mTypeface = Typeface.createFromAsset(context.getApplicationContext()
                        .getAssets(), String.format("fonts/%s", typefaceName));

                // Cache the loaded Typeface
                sTypefaceCache.put(typefaceName, mTypeface);
            }
        }

        @Override
        public void updateMeasureState(TextPaint p) {
            p.setTypeface(mTypeface);

            // Note: This flag is required for proper typeface rendering
            p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            tp.setTypeface(mTypeface);

            // Note: This flag is required for proper typeface rendering
            tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
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

    /**
     * Make log out ovoerflow menu item work here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    }
    **/

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
