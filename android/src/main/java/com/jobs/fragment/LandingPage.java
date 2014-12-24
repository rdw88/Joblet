package com.jobs.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Button myListings;
    private JSONObject data;
    TextView t;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.landing_page, container, false);

        name = (TextView) view.findViewById(R.id.user_name);
        location = (TextView) view.findViewById(R.id.location);
        positiveReputation = (TextView) view.findViewById(R.id.positive_reputation);
        negativeReputation = (TextView) view.findViewById(R.id.negative_reputation);
        listings = (TextView) view.findViewById(R.id.lists_completed);
        jobs = (TextView) view.findViewById(R.id.jobs_completed);
        profilePicture = (ImageView) view.findViewById(R.id.profile_picture);
        t = (TextView) view.findViewById(R.id.text_reputation_landingPage);
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/leaguegothic.ttf");
        t.setTypeface(customFont);
        myListings = (Button) view.findViewById(R.id.my_listings);

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
