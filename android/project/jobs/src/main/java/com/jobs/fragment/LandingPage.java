package com.jobs.fragment;

import android.content.Context;
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
import com.jobs.backend.Address;
import com.jobs.backend.ImageHelper;
import com.jobs.backend.Listing;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LandingPage extends Fragment {
    private TextView name, location, positiveReputation, negativeReputation, listings, jobs, userTags, textMyBids,
            textMyJobs, subTextMyJobs, textMyJobsNumber, subTextMyBids, textMyBidsNumber;
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
        Typeface robotoThin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");


        try {
            data = new JSONObject(getArguments().getString("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //TextViews
        name = (TextView) view.findViewById(R.id.user_name);
        name.setTypeface(robotoRegular);
        listings = (TextView) view.findViewById(R.id.lists_completed);
        listings.setTypeface(robotoRegular);
        jobs = (TextView) view.findViewById(R.id.jobs_completed);
        jobs.setTypeface(robotoRegular);

        //ImageView

        profilePicture = (ImageView) view.findViewById(R.id.profile_picture);


        new AsyncTask<String, Void, String>() {
            private Bitmap profileBitmap;

            protected String doInBackground(String... params) {
                try {
                    profileBitmap = Address.fetchPicture(data.getString("profile_picture"), 600, 400);
                } catch (IOException |JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String response) {
                profilePicture.setImageBitmap(profileBitmap);
            }
        }.execute();

        try {
            name.setText(data.getString("first_name") + " " + data.getString("last_name"));
            //location.setText(data.getString("city_code"));
            //positiveReputation.setText(data.getString("positive_reputation"));
            //negativeReputation.setText(data.getString("negative_reputation"));
            listings.setText(data.getString("listings_completed"));
            jobs.setText(data.getString("jobs_completed"));

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
