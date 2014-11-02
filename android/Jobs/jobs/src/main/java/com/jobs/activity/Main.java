package com.jobs.activity;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jobs.R;
import org.json.JSONException;
import org.json.JSONObject;

public class Main extends FragmentActivity {
    private static final int[] PAGE_ORDER = {R.layout.check_listing, R.layout.landing_page, R.layout.create_listing};
    private static final int MAIN_PAGE = 1;

    private String firstName, lastName, skills, city, email;
    private MainPagerAdapter adapter;
    private ViewPager pager;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(MAIN_PAGE);
	}

    protected void onStart() {
        super.onStart();
        String data = getIntent().getStringExtra("data");

        try {
            JSONObject obj = new JSONObject(data);
            firstName = obj.getString("first_name");
            lastName = obj.getString("last_name");
            skills = obj.getString("skills");
            city = obj.getString("city_code");
            email = obj.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MainPagerAdapter extends FragmentPagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int i) {
            Fragment fragment = new FragmentObject();
            Bundle args = new Bundle();
            args.putInt("page", i);
            fragment.setArguments(args);
            return fragment;
        }

        public int getCount() {
            return PAGE_ORDER.length;
        }

        public CharSequence getPageTitle(int position) {
            return "page " + position;
        }
    }

    public static class FragmentObject extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int page = getArguments().getInt("page");
            return inflater.inflate(PAGE_ORDER[page], container, false);
        }
    }
}
