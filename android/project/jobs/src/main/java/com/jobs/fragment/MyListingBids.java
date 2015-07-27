package com.jobs.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.activity.ViewBid;
import com.jobs.backend.Bid;
import com.jobs.backend.Listing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyListingBids extends Fragment {
    private List<Item> elements = new ArrayList<>();
    private static final String[] STATUSES = {"Undetermined", "Accepted", "Declined"};
    private static final int[] STATUS_COLORS = {0xfff99800, 0xff3dd33d, 0xffff0000};
    private BidAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_listing_bids, container, false);
    }

    public void onStart() {
        super.onStart();

        final String listingID = getArguments().getString("listing_id");
        adapter = new BidAdapter(getActivity(), elements);

        new AsyncTask<String, Void, String>() {
            private JSONObject[] bidData;

            protected String doInBackground(String... params) {
                JSONArray obj = Listing.getBids(listingID);
                bidData = new JSONObject[obj.length()];

                try {
                    for (int i = 0; i < bidData.length; i++) {
                        bidData[i] = Bid.get(obj.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String message) {
                elements.removeAll(elements);
                try {
                    for (int i = 0; i < bidData.length; i++) {
                        elements.add(new Item(bidData[i].getString("email"), bidData[i].getDouble("amount"), bidData[i].getInt("status"), bidData[i].getString("bid_id")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.reverse(elements); // sort from most recent to oldest.

                adapter.notifyDataSetChanged();
            }
        }.execute();

        ListView lv = (ListView) getActivity().findViewById(R.id.my_listing_bids_list_view);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = elements.get(position);

                if (item.bidStatus > 0)
                    return;

                Intent intent = new Intent(getActivity(), ViewBid.class);
                intent.putExtra("bid_id", item.bidID);
                intent.putExtra("email", item.email);
                intent.putExtra("amount", item.bidAmount);
                intent.putExtra("position", position);
                startActivityForResult(intent, 0xff);
            }
        });

        lv.setAdapter(adapter);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0xff:
                int action = data.getExtras().getInt("action");
                elements.get(data.getExtras().getInt("position")).bidStatus = action;
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private class BidAdapter extends ArrayAdapter<Item> {
        public BidAdapter(Context context, List<Item> items) {
            super(context, R.layout.my_listing_bids_list_item, items);
        }

        public View getView(int position, View currentView, ViewGroup parent) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.my_listing_bids_list_item, parent, false);

            TextView amount = (TextView) v.findViewById(R.id.bid_amount);
            TextView email = (TextView) v.findViewById(R.id.bidder_email);
            TextView status = (TextView) v.findViewById(R.id.bid_status);

            Item item = elements.get(position);

            amount.setText("$" + new DecimalFormat("#.##").format(item.bidAmount));
            email.setText(item.email);
            status.setText(STATUSES[item.bidStatus]);
            status.setTextColor(STATUS_COLORS[item.bidStatus]);

            return v;
        }
    }

    private class Item {
        public String email;
        public double bidAmount;
        public int bidStatus;
        public String bidID;

        public Item(String email, double bidAmount, int bidStatus, String bidID) {
            this.email = email;
            this.bidAmount = bidAmount;
            this.bidStatus = bidStatus;
            this.bidID = bidID;
        }
    }
}
