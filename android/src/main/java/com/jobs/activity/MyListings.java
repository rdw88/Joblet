package com.jobs.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.Listing;
import com.jobs.backend.Profile;

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
        ListView view = (ListView) findViewById(R.id.my_listings_list_view);
        ListingAdapter adapter = new ListingAdapter(this, elements);

        for (int i = 0; i < listings.length; i++) {
            elements.add(new Item(listings[i].getString("job_title"), listings[i].getString("tag"), listings[i].getDouble("current_bid"), listings[i].getBoolean("is_active")));
        }

        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyListings.this, ViewListing.class);
                intent.putExtra("listing", listings[position].toString());
                startActivity(intent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ListingAdapter extends ArrayAdapter<Item> {
        public ListingAdapter(Context context, ArrayList<Item> items) {
            super(context, R.layout.my_listings, items);
        }

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
            isActive.setText(elements.get(position).isActive ? "Active" : "Inactive");

            isActive.setTextColor(elements.get(position).isActive ? 0xff00ff00 : 0xffff0000);

            return row;
        }

        public int getCount() {
            return elements.size();
        }
    }

    private class Item {
        public String title;
        public boolean isActive;
        public String tag;
        public double currentBid;

        public Item(String title, String tag, double currentBid, boolean isActive) {
            this.title = title;
            this.tag = tag;
            this.currentBid = currentBid;
            this.isActive = isActive;
        }
     }
}
