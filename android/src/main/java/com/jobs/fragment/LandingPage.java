package com.jobs.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.jobs.R;
import com.jobs.activity.Main;
import com.jobs.activity.MyListings;

import org.json.JSONException;
import org.json.JSONObject;

public class LandingPage extends Fragment {
    private TextView name, location, positiveReputation, negativeReputation, listings, jobs;
    private ImageView profilePicture;
    private Button myListings, editProfile, watchlist;
    private JSONObject data;
    private TextView t;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View view = localInflater.inflate(R.layout.landing_page, container, false);
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/verdana.ttf");

        //TextViews
        name = (TextView) view.findViewById(R.id.user_name);
        name.setTypeface(customFont);
        location = (TextView) view.findViewById(R.id.location);
        location.setTypeface(customFont);
        positiveReputation = (TextView) view.findViewById(R.id.positive_reputation);
        positiveReputation.setTypeface(customFont);
        negativeReputation = (TextView) view.findViewById(R.id.negative_reputation);
        negativeReputation.setTypeface(customFont);
        listings = (TextView) view.findViewById(R.id.lists_completed);
        listings.setTypeface(customFont);
        jobs = (TextView) view.findViewById(R.id.jobs_completed);
        jobs.setTypeface(customFont);

        //ImageView
        profilePicture = (ImageView) view.findViewById(R.id.profile_picture);

        //Buttons
        myListings = (Button) view.findViewById(R.id.my_listings);
        myListings.setTypeface(customFont);

        editProfile = (Button) view.findViewById(R.id.editProfile);
        editProfile.setTypeface(customFont);

        watchlist = (Button) view.findViewById(R.id.watchlist);
        watchlist.setTypeface(customFont);

        //-onClick-
        myListings.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyListings.class);
                intent.putExtra("data", data.toString());
                startActivity(intent);
            }
        });

        try {
            data = new JSONObject(getArguments().getString("data"));
            name.setText(data.getString("first_name") + " " + data.getString("last_name"));
            location.setText(data.getString("city_code"));
            positiveReputation.setText(data.getString("positive_reputation"));
            negativeReputation.setText(data.getString("negative_reputation"));
            listings.setText(data.getString("listings_completed"));
            jobs.setText(data.getString("jobs_completed"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
