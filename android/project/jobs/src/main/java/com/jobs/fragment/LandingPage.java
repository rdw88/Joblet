package com.jobs.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.jobs.R;
import com.jobs.activity.EditProfile;
import com.jobs.activity.MyListings;
import com.jobs.activity.ViewListing;
import com.jobs.backend.Address;
import com.jobs.backend.ImageHelper;
import com.jobs.backend.Listing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LandingPage extends Fragment {
    private TextView name, location, positiveReputation, negativeReputation, listings, jobs, userTags, textMyBids, textMyJobs;
    private ImageView profilePicture;
    private Button myListings, editProfile, watchlist, myBids;
    private JSONObject data;

    private final Recent[] recentJobs = new Recent[2];
    private final Recent[] recentBids = new Recent[2];
    private final ImageView[] recentJobImages = new ImageView[2];
    private final ImageView[] recentBidImages = new ImageView[2];


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        //LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = inflater.inflate(R.layout.landing_page, container, false);

        //View view = localInflater.inflate(R.layout.landing_page, container, false);
        Typeface robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface robotoBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Black.ttf");
        Typeface robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");

        try {
            data = new JSONObject(getArguments().getString("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //TextViews
        name = (TextView) view.findViewById(R.id.user_name);
        name.setTypeface(robotoRegular);
        userTags = (TextView) view.findViewById(R.id.userTags);
        userTags.setTypeface(robotoRegular);
        location = (TextView) view.findViewById(R.id.location);
        location.setTypeface(robotoMedium);
        positiveReputation = (TextView) view.findViewById(R.id.positive_reputation);
        positiveReputation.setTypeface(robotoRegular);
        negativeReputation = (TextView) view.findViewById(R.id.negative_reputation);
        negativeReputation.setTypeface(robotoRegular);
        listings = (TextView) view.findViewById(R.id.lists_completed);
        listings.setTypeface(robotoRegular);
        jobs = (TextView) view.findViewById(R.id.jobs_completed);
        jobs.setTypeface(robotoRegular);
        textMyBids = (TextView) view.findViewById(R.id.text_mybids);
        textMyBids.setTypeface(robotoMedium);
        textMyJobs = (TextView) view.findViewById(R.id.text_myjobs);
        textMyJobs.setTypeface(robotoMedium);

        recentBidImages[0] = (ImageView) view.findViewById(R.id.mybids_recent1);
        recentBidImages[1] = (ImageView) view.findViewById(R.id.mybids_recent2);
        recentJobImages[0] = (ImageView) view.findViewById(R.id.myjobs_recent1);
        recentJobImages[1] = (ImageView) view.findViewById(R.id.myjobs_recent2);

        //ImageView

        profilePicture = (ImageView) view.findViewById(R.id.profile_picture);


        //Buttons
        myListings = (Button) view.findViewById(R.id.my_listings);
        myListings.setTypeface(robotoMedium);

        myBids = (Button) view.findViewById(R.id.my_bids);


        editProfile = (Button) view.findViewById(R.id.edit_profile);

        watchlist = (Button) view.findViewById(R.id.watchlist);
        watchlist.setTypeface(robotoMedium);

        new AsyncTask<String, Void, String>() {
            private Bitmap profileBitmap;

            protected String doInBackground(String... params) {
                try {
                    profileBitmap = Address.fetchPicture(data.getString("profile_picture"), 600, 400);
                    JSONArray rj = new JSONArray(data.getString("recent_jobs"));
                    JSONArray rb = new JSONArray(data.getString("recent_bids"));

                    for (int i = 0; i < rj.length(); i++) { // rj and rb should always be the same length
                        recentBids[i] = new Recent(rb.getString(i));
                        recentJobs[i] = new Recent(rj.getString(i));
                    }
                } catch (IOException |JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String response) {
                profilePicture.setImageBitmap(profileBitmap);

                for (int i = 0; i < recentBidImages.length; i++) {
                    final int index = i;

                    if (recentBids[i] != null) {
                        recentBidImages[i].setImageBitmap(recentBids[i].icon);
                        recentBidImages[i].setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ViewListing.class);
                                intent.putExtra("listing_id", recentBids[index].listingID);
                                startActivity(intent);
                            }
                        });
                    }

                    if (recentJobs[i] != null) {
                        recentJobImages[i].setImageBitmap(recentJobs[i].icon);
                        recentJobImages[i].setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ViewListing.class);
                                intent.putExtra("listing_id", recentJobs[index].listingID);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        }.execute();

        //-onClick-
        myListings.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyListings.class);
                intent.putExtra("data", data.toString());
                startActivity(intent);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Intent intent = new Intent(getActivity(), EditProfile.class);
                //intent.putExtra("data", data.toString());
                //startActivity(intent);
            }
        });

        try {
            name.setText(data.getString("first_name") + " " + data.getString("last_name"));
            location.setText(data.getString("city_code"));
            positiveReputation.setText(data.getString("positive_reputation"));
            negativeReputation.setText(data.getString("negative_reputation"));
            listings.setText(data.getString("listings_completed"));
            jobs.setText(data.getString("jobs_completed"));

            String str = "";
            JSONArray tags = new JSONArray(data.getString("tags"));
            for (int i = 0; i < tags.length(); i++)
                str += tags.getString(i) + " ";

            userTags.setText(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private class Recent {
        public Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.recent_job_placeholder);
        public String listingID;

        public Recent(String listingID) throws IOException, JSONException {
            String url = Listing.get(listingID).getString("thumbnail");
            this.icon = ImageHelper.getCircularBitmapWithWhiteBorder(Address.fetchPicture(url), 3);
            this.listingID = listingID;
        }
    }
}
