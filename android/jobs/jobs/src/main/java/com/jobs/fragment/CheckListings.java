package com.jobs.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.jobs.R;
import com.jobs.backend.*;
import com.jobs.backend.Error;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckListings extends Fragment {
    private ListView listings;
    private String profileData;

    public void onStart() {
        super.onStart();

        final ArrayList<Item> list = new ArrayList<>();

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
                    if (response.get(0) == Error.ERROR_SERVER_COMMUNICATION) {
                        alertErrorServer();
                    }

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = (JSONObject) response.get(i);
                        Item item = new Item(obj.getString("job_title"), obj.getString("tag"), obj.getString("owner_name"), obj.getDouble("owner_reputation"));
                        list.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listings = (ListView) getActivity().findViewById(R.id.listing_list);
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

            //TextView title = (TextView) row.findViewById(R.id.job_name);
            //TextView owner = (TextView) row.findViewById(R.id.user;
            TextView tags = (TextView) row.findViewById(R.id.listing_tags);
            //TextView reputation = (TextView) row.findViewById(R.id.owner_reputation);
            //title.setText(items.get(position).title);
            //owner.setText(items.get(position).owner);
            String ts = items.get(position).tag;
            tags.setText(ts);
            //reputation.setText(Double.toString(items.get(position).reputation));

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
        public String owner;
        public double reputation;

        public Item(String title, String tag, String owner, double reputation) {
            this.title = title;
            this.tag = tag;
            this.owner = owner;
            this.reputation = reputation;
        }
    }
}
