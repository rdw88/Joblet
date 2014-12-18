package com.jobs.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.jobs.R;
import com.jobs.activity.ViewListing;
import com.jobs.backend.*;
import com.jobs.backend.Error;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckListings extends Fragment {
    private ListView listings;
    private String profileData;
    private final ArrayList<Item> list = new ArrayList<>();

    public void onStart() {
        super.onStart();

        new AsyncTask<String, Void, String>() {
            private JSONArray response;

            protected String doInBackground(String... urls) {
                HashMap<String, String> map = new HashMap<String, String>();

                try {
                    JSONObject obj = new JSONObject(profileData);
                    map.put("tags", obj.getString("tags"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                response = Listing.search(map);
                return null;
            }

            protected void onPostExecute(String result) {
                try {
                    if (response.length() != 0) {
                        if (response.get(0) == Error.ERROR_SERVER_COMMUNICATION) {
                            alertErrorServer();
                        }

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = (JSONObject) response.get(i);
                            Item item = new Item(obj.getString("job_title"), obj.getString("tag"), obj.getDouble("current_bid"), obj.getDouble("owner_reputation"), obj.getString("listing_id"));
                            list.add(item);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listings = (ListView) getActivity().findViewById(R.id.listing_list);
                listings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String listingID = list.get(position).listingID;
                        Intent intent = new Intent(getActivity(), ViewListing.class);
                        intent.putExtra("listing_id", listingID);
                        startActivity(intent);
                    }
                });
                ListingAdapter adapter = new ListingAdapter(getActivity(), list);
                listings.setAdapter(adapter);
            }
        }.execute();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileData = getArguments().getString("data");
        return inflater.inflate(R.layout.check_listings, container, false);
    }

    private class ListingAdapter extends ArrayAdapter<Item> {
        private Context context;
        private ArrayList<Item> items;

        public ListingAdapter(Context context, ArrayList<Item> items) {
            super(context, R.layout.list_item, items);
            this.context = context;
            this.items = items;
        }

        @Override
        public View getView(int position, View currentView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.list_item, parent, false);

            NumberFormat format = new DecimalFormat("#0.00");

            TextView title = (TextView) row.findViewById(R.id.job_name);
            TextView currentBid = (TextView) row.findViewById(R.id.list_currentBid);
            currentBid.setText("$" + format.format(items.get(position).currentBid));
            TextView reputation = (TextView) row.findViewById(R.id.owner_reputation);
            title.setText(items.get(position).title);
            reputation.setText(Double.toString(items.get(position).reputation));

            return row;
        }
    }

    private void alertErrorServer() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.ad_error_server_comm);
        builder.setTitle(R.string.ad_error_server_comm_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
            }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class Item {
        public String title;
        public String tag;
        public double currentBid;
        public double reputation;
        public String listingID;

        public Item(String title, String tag, double currentBid, double reputation, String listingID) {
            this.title = title;
            this.tag = tag;
            this.currentBid = currentBid;
            this.reputation = reputation;
            this.listingID = listingID;
        }
    }
}
