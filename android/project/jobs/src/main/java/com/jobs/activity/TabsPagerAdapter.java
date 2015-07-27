package com.jobs.activity;

import com.jobs.fragment.CheckListings;
import com.jobs.fragment.LandingPage;
import com.jobs.fragment.CreateListing;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Games fragment activity
                return new LandingPage();
            case 1:
                // Movies fragment activity
                return new CheckListings();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}