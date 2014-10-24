package com.jobs.activity;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.jobs.android.R;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	// Provides fragments for each section, need to switch to AndroidStatePagerAdapter if this
	// gets memory intensive
	// SectionsPagerAdapter mSectionsPagerAdapter;

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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the UI from res/layout/activity_main.xml
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}
}
