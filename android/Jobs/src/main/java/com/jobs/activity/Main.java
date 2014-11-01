package com.jobs.activity;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;
import com.jobs.android.R;

public class Main extends Activity {
    private String data;

	// Provides fragments for each section, need to switch to AndroidStatePagerAdapter if this
	// gets memory intensive
	// SectionsPagerAdapter mSectionsPagerAdapter;

	// The {@link ViewPager} that will host the section contents.
	// ViewPager mViewPager;

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

    protected void onStart() {
        super.onStart();
        data = getIntent().getStringExtra("data");
        TextView view = (TextView) findViewById(R.id.text);
        view.setText(data);
    }
}
