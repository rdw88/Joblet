package com.jobs.fragment;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.jobs.R;
import com.jobs.activity.TagSelector;
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

public class CheckListings extends Fragment {
    private ListView listings;
    private String profileData;
    private String[] searchedTags;
    private ListingAdapter adapter;
    private boolean filtered;

    private final ArrayList<Item> elements = new ArrayList<>();

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            searchedTags = savedInstanceState.getStringArray("elements");
        }
    }

    public void onStart() {
        super.onStart();

        if (searchedTags == null) {
            try {
                JSONObject obj = new JSONObject(profileData);
                String tags = obj.getString("tags");
                String[] tagsArray = tags.split(",");
                searchedTags = new String[tagsArray.length];

                for (int i = 0; i < searchedTags.length; i++) {
                    searchedTags[i] = tagsArray[i];
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Button filter = (Button) getActivity().findViewById(R.id.checkListings_filter);
        filter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TagSelector.class);
                intent.putExtra("array", searchedTags);
                startActivityForResult(intent, 0xf2);
            }
        });

        fill();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("elements", searchedTags);
    }

    public void onStop() {
        super.onStop();
        elements.removeAll(elements);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileData = getArguments().getString("data");
        return inflater.inflate(R.layout.check_listings, container, false);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0xf2:
                if (resultCode == Activity.RESULT_OK) {
                    searchedTags = data.getExtras().getStringArray("array");
                    filtered = true;
                }

                break;
        }
    }

    private void fill() {
        if (searchedTags.length == 0) {
            adapter.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        new AsyncTask<String, Void, String>() {
            private JSONArray response;

            protected String doInBackground(String... urls) {
                HashMap<String, String> map = new HashMap<String, String>();
                String tags = "";
                for (int i = 0; i < searchedTags.length - 1; i++) {
                    tags += searchedTags[i] + ",";
                }

                tags += searchedTags[searchedTags.length - 1];
                map.put("tags", tags);

                response = Listing.search(map);
                return null;
            }

            protected void onPostExecute(String result) {
                try {
                    if (response.length() != 0) {
                        if (response.get(0) == Error.ERROR_SERVER_COMMUNICATION) {
                            alertErrorServer();
                        }
                        elements.removeAll(elements);

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = (JSONObject) response.get(i);
                            Item item = new Item(obj.getString("job_title"), obj.getString("tag"), obj.getDouble("current_bid"), obj.getDouble("owner_reputation"), obj.getString("listing_id"));
                            elements.add(item);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listings = (ListView) getActivity().findViewById(R.id.listing_list);
                listings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String listingID = elements.get(position).listingID;
                        Intent intent = new Intent(getActivity(), ViewListing.class);
                        intent.putExtra("listing_id", listingID);
                        startActivity(intent);
                    }
                });
                adapter = new ListingAdapter(getActivity(), elements);
                listings.setAdapter(adapter);
            }
        }.execute();
    }

    private class ListingAdapter extends ArrayAdapter<Item> {
        private ArrayList<Item> items;

        public ListingAdapter(Context context, ArrayList<Item> items) {
            super(context, R.layout.list_item, items);
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

        public int getCount() {
            return items.size();
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
