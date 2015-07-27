package com.jobs.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.jobs.R;
import com.jobs.activity.Main;
import com.jobs.activity.ViewListing;
import com.jobs.backend.*;
import com.jobs.backend.Error;
import com.jobs.ui.ListItemCard;
import com.jobs.utility.Global;
import com.rey.material.widget.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckListings extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ListView listings;
    private ListingAdapter adapter;
    private FloatingActionButton filter;
    private JSONObject profileData;
    private final ArrayList<FeedItem> elements = new ArrayList<>();
    private final ArrayList<String> filtered = new ArrayList<>();
    private JSONArray allListings;
    private static final int NUM_LISTINGS_SHOWN = 10;
    private int page;
    private boolean loading;

    private GoogleApiClient googleClient;
    private double currentLatitude;
    private double currentLongitude;

    private Typeface robotoRegular, robotoBold, robotoMedium, robotoThin, robotoBlack;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileData = ((Global) getActivity().getApplicationContext()).getUserData();

        robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        robotoBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Black.ttf");
        robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        robotoThin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
        robotoBlack = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Black.ttf");

        googleClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleClient.connect();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            String[] array = savedInstanceState.getStringArray("elements");
            for (int i = 0; i < array.length; i++) {
                filtered.add(array[i]);
            }
        }
    }

    public void onStart() {
        super.onStart();

        if (listings == null) {
            listings = (ListView) getActivity().findViewById(R.id.listing_list);
            filter = (FloatingActionButton) getActivity().findViewById(R.id.checkListings_filter);
        }

        adapter = new ListingAdapter(getActivity().getApplicationContext(), elements);
        listings.setAdapter(adapter);
        if (filtered.size() == 0) {
            try {
                JSONArray tags = new JSONArray(profileData.getString("tags"));

                try {
                    for (int i = 0; i < tags.length(); i++) {
                        filtered.add(tags.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        filter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                final View view = getActivity().getLayoutInflater().inflate(R.layout.tag_selector, null);
                ListView listView = (ListView) view.findViewById(R.id.tag_list);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckBox box = (CheckBox) view.findViewById(R.id.tag_checkbox);
                        box.setChecked(!box.isChecked());

                        if (box.isChecked())
                            filtered.add(Resource.TAGS.get(position));
                        else
                            filtered.remove(Resource.TAGS.get(position));
                    }
                });

                FilterAdapter adapter = new FilterAdapter(getActivity(), Resource.TAGS);
                listView.setAdapter(adapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.ad_filter_title);
                builder.setPositiveButton(R.string.done, new AlertDialog.OnClickListener(){
                    public void onClick(DialogInterface di, int i){
                        fill();
                        page = 0;
                    }
                });
                builder.setView(view);
                builder.show();
            }
        });

        fill();


    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String[] array = new String[filtered.size()];
        outState.putStringArray("elements", filtered.toArray(array));
    }

    public void onStop() {
        super.onStop();
        elements.removeAll(elements);
        adapter.clear();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.check_listings, container, false);
    }

    private void fill() {
        if (filtered.size() == 0 && adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... urls) {
                HashMap<String, String> map = new HashMap<String, String>();

                JSONArray tags = new JSONArray(filtered);
                map.put("tags", tags.toString());

                allListings = Listing.search(map);
                return null;
            }

            protected void onPostExecute(String result) {
                try {
                    if (allListings.length() != 0) {
                        if (allListings.get(0) == Error.ERROR_SERVER_COMMUNICATION) {
                            alertErrorServer();
                        }
                        elements.removeAll(elements);

                        int len = allListings.length() > NUM_LISTINGS_SHOWN ? NUM_LISTINGS_SHOWN : allListings.length();
                        for (int i = 0; i < len; i++) {
                            JSONObject obj = allListings.getJSONObject(i);
                            FeedItem feedItem = new FeedItem(obj.getString("job_title"), obj.getString("tag"), obj.getDouble("current_bid"),
                                    obj.getDouble("owner_reputation"), obj.getString("listing_id"), obj.getString("thumbnail"),
                                    obj.getDouble("lat"), obj.getDouble("long"));

                            elements.add(feedItem);

                        }
                        adapter.notifyDataSetChanged();
                    } else
                        elements.removeAll(elements);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String listingID = elements.get(position).listingID;
                        Intent intent = new Intent(getActivity(), ViewListing.class);
                        intent.putExtra("listing_id", listingID);
                        startActivity(intent);
                    }
                });
                listings.setOnScrollListener(new AbsListView.OnScrollListener() {

                    public void onScrollStateChanged(AbsListView view, int scrollStat) {
                    }

                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int last = firstVisibleItem + visibleItemCount;
                        if (last == totalItemCount && !loading && totalItemCount >= NUM_LISTINGS_SHOWN && totalItemCount != allListings.length()) {
                            try {
                                loadMore();
                            } catch (Exception e) {

                            }
                        }
                    }
                });
            }
        }.execute();
    }

    private void loadMore() throws JSONException {
        loading = true;
        page++;

        int indices = (page + 1) * NUM_LISTINGS_SHOWN;

        for (int i = page * NUM_LISTINGS_SHOWN; i < indices && i < allListings.length(); i++) {
            JSONObject obj = (JSONObject) allListings.get(i);
            FeedItem feedItem = new FeedItem(obj.getString("job_title"), obj.getString("tag"), obj.getDouble("current_bid"),
                    obj.getDouble("owner_reputation"), obj.getString("listing_id"), obj.getString("thumbnail"),
                    obj.getDouble("lat"), obj.getDouble("long"));

            elements.add(feedItem);
        }

        adapter.notifyDataSetChanged();
        loading = false;
    }

    public void onConnected(Bundle connectionHint) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleClient);

        if (lastLocation != null) {
            currentLatitude = lastLocation.getLatitude();
            currentLongitude = lastLocation.getLongitude();
        }
    }

    public void onConnectionFailed(ConnectionResult b) {
        // TODO: Implement
    }

    public void onConnectionSuspended(int i) {
    }

    private class FilterAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public FilterAdapter(Context context, ArrayList<String> items) {
            super(context, R.layout.tag_list_item, items);
            this.items = items;
        }

        public View getView(final int position, View currentView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.tag_list_item, parent, false);

            TextView tag = (TextView) row.findViewById(R.id.tag_name);
            tag.setText(items.get(position));

            final CheckBox box = (CheckBox) row.findViewById(R.id.tag_checkbox);
            if (filtered.contains(items.get(position))) {
                box.setChecked(true);
            }

            box.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if (box.isChecked()) {
                        filtered.add(items.get(position));
                    } else {
                        filtered.remove(items.get(position));
                    }
                }
            });

            return row;
        }
    }


    private class ListingAdapter extends ArrayAdapter<FeedItem> {
        private ArrayList<FeedItem> feedItems;

        public ListingAdapter(Context context, ArrayList<FeedItem> feedItems) {
            super(context, R.layout.check_listings_list_item, feedItems);
            this.feedItems = feedItems;
        }

        @Override
        public View getView(final int position, View currentView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.check_listings_list_item, parent, false);
            NumberFormat format = new DecimalFormat("#0.00");

            TextView title = (TextView) row.findViewById(R.id.listing_job_title);
            title.setTypeface(robotoMedium);
            TextView currentBid = (TextView) row.findViewById(R.id.listing_current_bid);
            currentBid.setTypeface(robotoBlack);
            TextView tags = (TextView) row.findViewById(R.id.listing_tag);
            tags.setTypeface(robotoBlack);
            currentBid.setText("$" + format.format(feedItems.get(position).currentBid));
            TextView reputation = (TextView) row.findViewById(R.id.listing_ownerreputation);
            title.setText(feedItems.get(position).title);
            reputation.setText(Double.toString(feedItems.get(position).reputation));
            tags.setText(tags.getText() + feedItems.get(position).tag);
            TextView distance = (TextView) row.findViewById(R.id.listing_distanceaway);

            if (currentLatitude != 0 && currentLongitude != 0) {
                double distanceAway = Resource.calculateDistanceInMiles(currentLatitude, currentLongitude, feedItems.get(position).latitude, feedItems.get(position).longitude);
                NumberFormat distanceFormat = new DecimalFormat("#0.0");
                distance.setText(distanceFormat.format(distanceAway) + " mi");
            }

            return row;
        }

        public int getCount() {
            return feedItems.size();
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



    private class FeedItem {
        public String title;
        public String tag;
        public double currentBid;
        public double reputation;
        public double latitude;
        public double longitude;
        public String listingID;
        public FeedItem(String title, String tag, double currentBid, double reputation, String listingID,
                        String thumbnail, double latitude, double longitude) {
            this.title = title;
            this.tag = tag;
            this.currentBid = currentBid;
            this.reputation = reputation;
            this.listingID = listingID;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }


    private double calcDistanceAway(FeedItem fi) {
        double distanceAway =
                Resource.calculateDistanceInMiles(currentLatitude, currentLongitude,
                fi.latitude, fi.longitude);
        return distanceAway;
    }




}
