package com.jobs.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jobs.R;
import org.json.JSONException;
import org.json.JSONObject;

public class LandingPage extends Fragment {
    private TextView name, location, positiveReputation, negativeReputation, listings, jobs;
    private ImageView profilePicture;
    private JSONObject data;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.landing_page, container, false);
        name = (TextView) view.findViewById(R.id.view_name);
        location = (TextView) view.findViewById(R.id.location);
        positiveReputation = (TextView) view.findViewById(R.id.positive_reputation);
        negativeReputation = (TextView) view.findViewById(R.id.negative_reputation);
        listings = (TextView) view.findViewById(R.id.lists_completed);
        jobs = (TextView) view.findViewById(R.id.jobs_completed);
        profilePicture = (ImageView) view.findViewById(R.id.profile_picture);

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
