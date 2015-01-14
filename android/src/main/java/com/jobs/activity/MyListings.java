package com.jobs.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.Listing;
import com.jobs.backend.Profile;
import com.jobs.ui.SwipeMenu;
import com.jobs.ui.SwipeMenuCreator;
import com.jobs.ui.SwipeMenuItem;
import com.jobs.ui.SwipeMenuListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;


public class MyListings extends Activity {
    private JSONObject data;
    private final ArrayList<Item> elements = new ArrayList<>();
    private Typeface customFont;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_listings);

        customFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            data = new JSONObject(getIntent().getExtras().getString("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<String, Void, String>() {
            private JSONObject[] response;

            protected String doInBackground(String... urls) {
                try {
                    JSONObject obj = Profile.getProfile(data.getString("profile_id"));
                    JSONArray array = new JSONArray(obj.getString("owned_listings"));
                    response = new JSONObject[array.length()];

                    for (int i = 0; i < array.length(); i++) {
                        response[i] = Listing.get(array.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String result) {
                try {
                    fill(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void fill(final JSONObject[] listings) throws JSONException {
        SwipeMenuListView view = (SwipeMenuListView) findViewById(R.id.my_listings_list_view);
        final ListingAdapter adapter = new ListingAdapter();

        for (int i = 0; i < listings.length; i++) {
            elements.add(new Item(listings[i].getString("job_title"), listings[i].getString("tag"), listings[i].getDouble("current_bid"), listings[i].getInt("status")));
        }


        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyListings.this, ViewListing.class);
                intent.putExtra("listing", listings[position].toString());
                startActivity(intent);
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            public void create(SwipeMenu menu) {
                switch (menu.getViewType()) {
                    case 0:   // IN THE CASE THAT THE LISTING IS *ACTIVE*
                        SwipeMenuItem viewBids = new SwipeMenuItem(getApplicationContext());
                        viewBids.setBackground(new ColorDrawable(Color.rgb(0x4c, 0x9a, 0xde)));
                        viewBids.setWidth(dpToPx(90));
                        viewBids.setTitle("Bids");
                        viewBids.setTitleSize(18);
                        viewBids.setTitleColor(Color.WHITE);
                        menu.addMenuItem(viewBids);

                        SwipeMenuItem setInactive = new SwipeMenuItem(getApplicationContext());
                        setInactive.setBackground(new ColorDrawable(Color.rgb(0xf4, 0xa6, 0)));
                        setInactive.setWidth(dpToPx(90));
                        setInactive.setTitle("Set Inactive");
                        setInactive.setTitleSize(18);
                        setInactive.setTitleColor(Color.WHITE);
                        menu.addMenuItem(setInactive);

                        SwipeMenuItem finish = new SwipeMenuItem(getApplicationContext());
                        finish.setBackground(new ColorDrawable(Color.rgb(0x53, 0xd0, 0x62)));
                        finish.setWidth(dpToPx(90));
                        finish.setTitle("Finish");
                        finish.setTitleSize(18);
                        finish.setTitleColor(Color.WHITE);
                        menu.addMenuItem(finish);
                        break;

                    case 1:   // IN THE CASE THAT THE LISTING IS *INACTIVE*
                        viewBids = new SwipeMenuItem(getApplicationContext());
                        viewBids.setBackground(new ColorDrawable(Color.rgb(0x4c, 0x9a, 0xde)));
                        viewBids.setWidth(dpToPx(90));
                        viewBids.setTitle("Bids");
                        viewBids.setTitleSize(18);
                        viewBids.setTitleColor(Color.WHITE);
                        menu.addMenuItem(viewBids);

                        SwipeMenuItem setActive = new SwipeMenuItem(getApplicationContext());
                        setActive.setBackground(new ColorDrawable(Color.rgb(0xf4, 0xa6, 0)));
                        setActive.setWidth(dpToPx(90));
                        setActive.setTitle("Set Active");
                        setActive.setTitleSize(18);
                        setActive.setTitleColor(Color.WHITE);
                        menu.addMenuItem(setActive);

                        finish = new SwipeMenuItem(getApplicationContext());
                        finish.setBackground(new ColorDrawable(Color.rgb(0x53, 0xd0, 0x62)));
                        finish.setWidth(dpToPx(90));
                        finish.setTitle("Finish");
                        finish.setTitleSize(18);
                        finish.setTitleColor(Color.WHITE);
                        menu.addMenuItem(finish);
                        break;

                    case 2:   // IN THE CASE THAT THE LISTING IS *COMPLETED OR PENDING*
                        viewBids = new SwipeMenuItem(getApplicationContext());
                        viewBids.setBackground(new ColorDrawable(Color.rgb(0x4c, 0x9a, 0xde)));
                        viewBids.setWidth(dpToPx(90));
                        viewBids.setTitle("Bids");
                        viewBids.setTitleSize(18);
                        viewBids.setTitleColor(Color.WHITE);
                        menu.addMenuItem(viewBids);

                        finish = new SwipeMenuItem(getApplicationContext());
                        finish.setBackground(new ColorDrawable(Color.rgb(0x53, 0xd0, 0x62)));
                        finish.setWidth(dpToPx(90));
                        finish.setTitle("Finish");
                        finish.setTitleSize(18);
                        finish.setTitleColor(Color.WHITE);
                        menu.addMenuItem(finish);
                        break;
                }
            }
        };

        view.setMenuCreator(creator);

        view.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (menu.getViewType() == 2 && index == 1)
                    index = 2;

                switch (index) {
                    case 0:
                        // start view bids activity
                        break;

                    case 1:
                        if (menu.getViewType() == 0)
                            elements.get(position).status = 1;
                        else if (menu.getViewType() == 1)
                            elements.get(position).status = 0;

                        adapter.notifyDataSetChanged();
                        break;

                    case 2:
                        // accept current bid
                        break;
                }

                return false;
            }
        });

        view.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ListingAdapter extends BaseAdapter {
        public View getView(int position, View currentView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.my_listings_list_item, parent, false);

            NumberFormat format = new DecimalFormat("#0.00");

            TextView title = (TextView) row.findViewById(R.id.my_listing_job_title);
            title.setTypeface(customFont);
            TextView currentBid = (TextView) row.findViewById(R.id.my_listing_current_bid);
            currentBid.setTypeface(customFont);
            TextView tag = (TextView) row.findViewById(R.id.my_listing_tag);
            tag.setTypeface(customFont);
            TextView isActive = (TextView) row.findViewById(R.id.my_listing_is_active);
            isActive.setTypeface(customFont);

            title.setText(elements.get(position).title);
            currentBid.setText("Current Bid: $" + format.format(elements.get(position).currentBid));
            tag.setText(elements.get(position).tag);

            String text = "Active";
            int color = 0xff00ff00;
            int status = elements.get(position).status;

            if (status == 1) {
                text = "Inactive";
                color = 0xffff0000;
            } else if (status == 2) {
                text = "Job Completion Pending...";
                color = 0xffffac25;
            } else if (status == 3) {
                text = "Completed";
                color = 0xff00ff00;
            }

            isActive.setText(text);
            isActive.setTextColor(color);

            return row;
        }

        public int getViewTypeCount() {
            return 3;
        }

        public int getItemViewType(int position) {
            int status = elements.get(position).status;
            return (status == 0 || status == 1) ? status : 2;
        }

        public int getCount() {
            return elements.size();
        }

        public long getItemId(int i) {
            return i;
        }

        public Item getItem(int position) {
            return elements.get(position);
        }
    }

    private class Item {
        public String title;
        public int status;
        public String tag;
        public double currentBid;

        public Item(String title, String tag, double currentBid, int status) {
            this.title = title;
            this.tag = tag;
            this.currentBid = currentBid;
            this.status = status;
        }
     }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
