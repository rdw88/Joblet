package com.jobs.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.jobs.R;
import com.jobs.activity.ViewListing;
import com.jobs.backend.*;
import com.jobs.backend.Error;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckListings extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ListView listings;
    private String profileData;
    private ListingAdapter adapter;
    private final ArrayList<Item> elements = new ArrayList<>();
    private final ArrayList<String> filtered = new ArrayList<>();

    private JSONArray allListings;
    private static final int NUM_LISTINGS_SHOWN = 10;
    private int page;
    private boolean loading;

    private GoogleApiClient googleClient;
    private double currentLatitude;
    private double currentLongitude;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("HI");

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

        if (filtered.size() == 0) {
            try {
                JSONObject obj = new JSONObject(profileData);
                String tags = obj.getString("tags");
                String[] tagsArray = tags.split(",");

                for (int i = 0; i < tagsArray.length; i++) {
                    filtered.add(tagsArray[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Button filter = (Button) getActivity().findViewById(R.id.checkListings_filter);
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
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileData = getArguments().getString("data");

        View rootView = inflater.inflate(R.layout.check_listings, container, false);

        return rootView;
    }

    private void fill() {
        if (filtered.size() == 0) {
            adapter.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... urls) {
                HashMap<String, String> map = new HashMap<String, String>();
                String tags = "";
                for (int i = 0; i < filtered.size() - 1; i++) {
                    tags += filtered.get(i) + ",";
                }

                tags += filtered.get(filtered.size() - 1);
                map.put("tags", tags);

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
                            JSONObject obj = (JSONObject) allListings.get(i);
                            Item item = new Item(obj.getString("job_title"), obj.getString("tag"), obj.getDouble("current_bid"),
                                    obj.getDouble("owner_reputation"), obj.getString("listing_id"), obj.getString("thumbnail"),
                                    obj.getDouble("lat"), obj.getDouble("long"));

                            elements.add(item);
                        }
                    } else
                        elements.removeAll(elements);
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

                listings.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                adapter = new ListingAdapter(getActivity(), elements);
                listings.setAdapter(adapter);
            }
        }.execute();
    }

    private void loadMore() throws JSONException {
        loading = true;
        page++;

        int indices = (page + 1) * NUM_LISTINGS_SHOWN;

        for (int i = page * NUM_LISTINGS_SHOWN; i < indices && i < allListings.length(); i++) {
            JSONObject obj = (JSONObject) allListings.get(i);
            Item item = new Item(obj.getString("job_title"), obj.getString("tag"), obj.getDouble("current_bid"),
                    obj.getDouble("owner_reputation"), obj.getString("listing_id"), obj.getString("thumbnail"),
                    obj.getDouble("lat"), obj.getDouble("long"));

            elements.add(item);
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

    private class ListingAdapter extends ArrayAdapter<Item> {
        private ArrayList<Item> items;

        public Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        public Typeface moneyFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Black.ttf");

        public ListingAdapter(Context context, ArrayList<Item> items) {
            super(context, R.layout.check_listing_list_item, items);
            this.items = items;
        }

        @Override
        public View getView(final int position, View currentView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.check_listing_list_item, parent, false);
            NumberFormat format = new DecimalFormat("#0.00");

            final ImageView picture = (ImageView) row.findViewById(R.id.job_frontpicture);
            TextView title = (TextView) row.findViewById(R.id.job_name);
            title.setTypeface(moneyFont);
            TextView currentBid = (TextView) row.findViewById(R.id.list_currentBid);
            currentBid.setTypeface(moneyFont);
            TextView tags = (TextView) row.findViewById(R.id.listItem_text_tags);
            tags.setTypeface(customFont);
            currentBid.setText("$" + format.format(items.get(position).currentBid));
            TextView reputation = (TextView) row.findViewById(R.id.owner_reputation);
            title.setText(items.get(position).title);
            reputation.setText(Double.toString(items.get(position).reputation));
            tags.setText(tags.getText() + items.get(position).tag);
            TextView distance = (TextView) row.findViewById(R.id.listing_distance);

            if (currentLatitude == 0 || currentLongitude == 0)
                return row;

            double distanceAway = Resource.calculateDistanceInMiles(currentLatitude, currentLongitude, items.get(position).latitude, items.get(position).longitude);
            NumberFormat distanceFormat = new DecimalFormat("#0.0");
            distance.setText(distanceFormat.format(distanceAway) + " mi");

            if (items.get(position).thumbnail.equals(""))
                return row;

            new AsyncTask<String, Void, String>() {
                private Bitmap bitmap;

                protected String doInBackground(String... urls) {
                    try {
                        bitmap = Address.fetchPicture(items.get(position).thumbnail);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                protected void onPostExecute(String result) {
                    picture.setImageBitmap(ImageHelper.getCircularBitmapWithWhiteBorder(bitmap, 3));
                }
            }.execute();

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
        public double latitude;
        public double longitude;
        public String listingID;
        public String thumbnail;

        public Item(String title, String tag, double currentBid, double reputation, String listingID,
                    String thumbnail, double latitude, double longitude) {
            this.title = title;
            this.tag = tag;
            this.currentBid = currentBid;
            this.reputation = reputation;
            this.listingID = listingID;
            this.thumbnail = thumbnail;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
