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
import android.widget.AbsListView;
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
import java.util.HashMap;


public class MyListings extends Activity {
    private JSONObject data;
    private final ArrayList<Item> elements = new ArrayList<>();
    private Typeface customFont;
    private ListingAdapter adapter;

    private JSONArray allListings;
    private static final int NUM_LISTINGS_SHOWN = 10;
    private int page;
    private boolean loading;

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
    }

    public void onResume() {
        super.onResume();
        page = 0;
    }

    public void onStart() {
        super.onStart();

        new AsyncTask<String, Void, String>() {
            private JSONObject[] response;

            protected String doInBackground(String... urls) {
                try {
                    JSONObject obj = Profile.getProfile(data.getString("profile_id"));
                    allListings = new JSONArray(obj.getString("owned_listings"));
                    int len = allListings.length() > NUM_LISTINGS_SHOWN ? NUM_LISTINGS_SHOWN : allListings.length();

                    response = new JSONObject[len];

                    for (int i = 0; i < len; i++) {
                        response[i] = Listing.get(allListings.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String result) {
                elements.removeAll(elements);



                try {
                    for (int i = 1; i < response.length; i++) {
                        if (response[i].getString("job_title").equals(response[0].getString("job_title"))){
                            System.out.println(i);
                        }
                    }
                    fill(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void fill(final JSONObject[] listings) throws JSONException {
        SwipeMenuListView view = (SwipeMenuListView) findViewById(R.id.my_listings_list_view);
        adapter = new ListingAdapter();

        for (int i = 0; i < listings.length; i++) {
            elements.add(new Item(listings[i].getString("job_title"), listings[i].getString("tag"),
                    listings[i].getDouble("current_bid"), listings[i].getInt("status"), listings[i].getString("listing_id")));

            System.out.println(i + " " + listings[i].getString("job_title"));
        }


        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyListings.this, ViewListing.class);
                intent.putExtra("listing", listings[position].toString());
                startActivity(intent);
            }
        });

        view.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int last = firstVisibleItem + visibleItemCount;

                if (last == totalItemCount && !loading && totalItemCount >= NUM_LISTINGS_SHOWN && totalItemCount != allListings.length()) {
                    try {
                        loadMore();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                if (menu.getViewType() == 2 && index == 1)
                    index = 2;

                switch (index) {
                    case 0:
                        Intent intent = new Intent(MyListings.this, MyListingBids.class);
                        intent.putExtra("listing_id", elements.get(position).listingID);
                        startActivity(intent);
                        break;

                    case 1:
                        if (menu.getViewType() == 0)
                            elements.get(position).status = 1;
                        else if (menu.getViewType() == 1)
                            elements.get(position).status = 0;

                        updateListing(position);

                        adapter.notifyDataSetChanged();

                        break;

                    case 2:
                        elements.get(position).status = 2;
                        updateListing(position);
                        adapter.notifyDataSetChanged();
                        break;
                }

                return false;
            }
        });

        view.setAdapter(adapter);
    }

    private void loadMore() throws JSONException {
        System.out.println("load more");
        loading = true;
        page++;

        new AsyncTask<String, Void, String>() {
            private JSONObject[] response;

            protected String doInBackground(String... urls) {
                try {
                    int len = NUM_LISTINGS_SHOWN;
                    if (len > allListings.length() - NUM_LISTINGS_SHOWN * page) {
                        len = allListings.length() - NUM_LISTINGS_SHOWN * page;
                    }

                    response = new JSONObject[len];

                    int indices = (page + 1) * NUM_LISTINGS_SHOWN;
                    int j = 0;
                    for (int i = page * NUM_LISTINGS_SHOWN; i < indices && j < len; i++) {
                        response[j] = Listing.get(allListings.getString(i));
                        j++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String result) {
                try {
                    for (int i = 0; i < response.length; i++) {
                        Item item = new Item(response[i].getString("job_title"), response[i].getString("tag"), response[i].getDouble("current_bid"), response[i].getInt("status"), response[i].getString("listing_id"));
                        elements.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
                loading = false;
            }
        }.execute();
    }

    private void updateListing(final int position) {
        new AsyncTask<String, Void ,String>(){
            protected String doInBackground(String... params) {
                HashMap<String, String> map = new HashMap<>();
                map.put("status", Integer.toString(elements.get(position).status));
                Listing.update("status", elements.get(position).listingID, map);
                return null;
            }

            protected void onPostExecute(String message) {

            }
        }.execute();
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
            System.out.println(elements.get(position).title);
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
        public String listingID;

        public Item(String title, String tag, double currentBid, int status, String listingID) {
            this.title = title;
            this.tag = tag;
            this.currentBid = currentBid;
            this.status = status;
            this.listingID = listingID;
        }
     }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}