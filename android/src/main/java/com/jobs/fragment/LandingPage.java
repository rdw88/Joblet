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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.jobs.R;
import com.jobs.activity.EditProfile;
import com.jobs.activity.MyListings;
import com.jobs.backend.ImageHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class LandingPage extends Fragment {
    private TextView name, location, positiveReputation, negativeReputation, listings, jobs, userTags, textMyBids, textMyJobs;
    private ImageView profilePicture;
    private Button myListings, editProfile, watchlist, myBids;
    private JSONObject data;




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        //LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = inflater.inflate(R.layout.landing_page, container, false);

        //View view = localInflater.inflate(R.layout.landing_page, container, false);
        Typeface robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface robotoBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Black.ttf");
        Typeface robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");



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

        //ImageView

        profilePicture = (ImageView) view.findViewById(R.id.profile_picture);

        //Buttons
        myListings = (Button) view.findViewById(R.id.my_listings);
        myListings.setTypeface(robotoMedium);

        myBids = (Button) view.findViewById(R.id.my_bids);


        editProfile = (Button) view.findViewById(R.id.edit_profile);

        watchlist = (Button) view.findViewById(R.id.watchlist);
        watchlist.setTypeface(robotoMedium);

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
                Intent intent = new Intent(getActivity(), EditProfile.class);
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
            userTags.setText(data.getString("tags"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
