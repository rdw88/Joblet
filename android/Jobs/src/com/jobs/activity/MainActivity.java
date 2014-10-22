package com.jobs.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends FragmentActivity implements Actionbar.TabListener  {

    // Provides fragments for each section, need to switch to AndroidStatePagerAdapter if this
    // gets memory intensive
    SectionsPagerAdapter mSectionsPagerAdapter;

    // The {@link ViewPager} that will host the section contents.
    ViewPager mViewPager;

    // Create the activity. Sets up {@link android.app.Actionbar} with pre set tabs. We won't use
    // tab creation because we only have 3 tabs we want the viewer to be able to navigate to
    // these tabs with respect to location are listed here:
    // |Job Listings|Main|Create Listing|
    //
    // <p>A {@link SectionsPagerAdapter} will be instantiated to hold the different pages of
    // fragments that are to be displayed.
    // {@link android.support.v4.view.ViewPager.SimplleOnPageChangeListener} will be configured
    // to recieve callbacks when the user swipes between pages in the ViewPager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the UI from res/layout/activity_main.xml
        setContentView(R.layout.)
    }

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
