package com.jobs.fragment;


import android.support.v4.app.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.activity.ViewListing;
import com.jobs.backend.Listing;
import com.jobs.backend.Profile;
import com.jobs.ui.SwipeMenu;
import com.jobs.ui.SwipeMenuListView;
import com.rey.material.widget.Button;
import com.jobs.utility.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyListings extends Fragment {
    private JSONObject data;
    private final ArrayList<FeedItem> elements = new ArrayList<>();
    private Typeface robotoRegular, robotoMedium;
    private ListingAdapter adapter;
    private Button cancel, preview;

    private JSONArray allListings;
    private static final int NUM_LISTINGS_SHOWN = 10;
    private int page;
    private boolean loading;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            data = new JSONObject(getActivity().getIntent().getExtras().getString("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_listings, container, false);
    }

    public void onResume() {
        super.onResume();
        page = 0;
    }

    public void onStart() {
        super.onStart();

        new AsyncTask<String, Void, String>() {
            private List<JSONObject> response = new ArrayList<>();

            protected String doInBackground(String... urls) {
                try {
                    JSONObject obj = Profile.getProfile(data.getString("email"));
                    JSONArray arr = new JSONArray(obj.getString("owned_listings"));
                    allListings = new JSONArray();

                    for (int i = arr.length() - 1; i >= 0; i--) { // sort from most recent to oldest
                        allListings.put(arr.get(i));
                    }

                    int len = allListings.length() > NUM_LISTINGS_SHOWN ? NUM_LISTINGS_SHOWN : allListings.length();

                    for (int i = 0; i < len; i++) {
                        JSONObject list = Listing.get(allListings.getString(i));
                        if (!list.has("error"))
                            response.add(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String result) {
                elements.removeAll(elements);
                JSONObject[] jsonObjArray = (JSONObject[]) response.toArray();

                try {
                    fill(jsonObjArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void fill(final JSONObject[] listings) throws JSONException {
        ListView listView = (ListView) getActivity().findViewById(R.id.my_listing_bids_list_view);
        adapter = new ListingAdapter();
        listView.setAdapter(adapter);

        for (int i = 0; i < listings.length; i++) {
            elements.add(new FeedItem(listings[i].getString("job_title"), listings[i].getString("tag"),
                    listings[i].getDouble("current_bid"), listings[i].getInt("view_number"),
                    listings[i].getString("listing_id"), listings[i].getInt("status"),
                    listings[i].getString("time_left")));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ViewListing.class);

                try {
                    intent.putExtra("listing_id", listings[position].getString("listing_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                return;
            }

        });

        preview.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
                View view = inflater.inflate(R.layout.mylistings_preview, null);
            }
        });
    }


    private void loadMore() throws JSONException {
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
                        FeedItem fI = new FeedItem(response[i].getString("job_title"),
                                response[i].getString("tag"), response[i].getDouble("current_bid"),
                                response[i].getInt("views"), response[i].getString("listing_id"),
                                response[i].getInt("status"), response[i].getString("time_left")
                                );
                        elements.add(fI);
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
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FeedItem {
        public String title;
        public String tag;
        public double currentBid;
        public int views;
        public double latitude;
        public double longitude;
        public String listingID;
        public int status;
        public String timeLeft;
        public FeedItem(String title, String tag, double currentBid, int views, String listingID,
                        int status, String timeLeft) {
            this.title = title;
            this.tag = tag;
            this.currentBid = currentBid;
            this.views = views;
            this.listingID = listingID;
            this.status = status;
            this.timeLeft = timeLeft;
        }
    }

    private class ListingAdapter extends BaseAdapter {
        public View getView(int position, View currentView, ViewGroup parent) {
            View row = null;

            if (currentView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.my_listings_list_item, parent, false);
            } else {
                row = currentView;
            }

            NumberFormat format = new DecimalFormat("#0.00");

            TextView title = (TextView) row.findViewById(R.id.my_listing_job_title);
            title.setTypeface(robotoRegular);
            TextView currentBid = (TextView) row.findViewById(R.id.my_listing_current_bid);
            currentBid.setTypeface(robotoRegular);
            TextView tag = (TextView) row.findViewById(R.id.my_listing_tag);
            tag.setTypeface(robotoRegular);
            TextView isActive = (TextView) row.findViewById(R.id.my_listing_is_active);
            isActive.setTypeface(robotoRegular);
            preview = (Button) row.findViewById(R.id.btn_preview);
            preview.setTypeface(robotoMedium);
            cancel = (Button) row.findViewById(R.id.btn_cancel);

            title.setText(elements.get(position).title);
            currentBid.setText("Current Bid: $" + format.format(elements.get(position).currentBid));

            String text = "Active";
            int color = 0xff00ff00;
            int status = elements.get(position).status;

            if (status == 1) {
                text = "Inactive";
                color = 0xffff0000;
            } else if (status == 2) {
                text = "In Progress";
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

        public FeedItem getItem(int position) {
            return elements.get(position);
        }
    }

    private class Item{
        public String title;
        public int status;
        public double currentBid;
        public String listingID;

        public Item(String title, String tag, double currentBid, int status, String listingID) {
            super();
            this.title = title;
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


