package com.jobs.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.Address;
import com.jobs.backend.Listing;
import com.jobs.backend.Profile;
import com.jobs.backend.Resource;
import com.jobs.utility.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class EditProfile extends Fragment {
    private TextView text_firstname, text_last_name, text_bio, text_email, text_password, text_city,
            text_profilepic;
    private EditText firstName, lastName, bio, email, password, password2, city;
    private Typeface robotoRegular, robotoBlack, robotoMedium, robotoThin;
    private JSONObject data;
    private ImageView profilePicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile, container, false);
        robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        robotoBlack = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Black.ttf");
        robotoThin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");

        profilePicture = (ImageView) view.findViewById(R.id.profile_picture);

        text_firstname = (TextView) view.findViewById(R.id.text_first_name);
        text_last_name = (TextView) view.findViewById(R.id.text_last_name);
        text_bio = (TextView) view.findViewById(R.id.text_bio);
        text_email = (TextView) view.findViewById(R.id.text_email);
        text_password = (TextView) view.findViewById(R.id.text_password);
        text_city = (TextView) view.findViewById(R.id.text_city);
        text_profilepic = (TextView) view.findViewById(R.id.text_profilepic);
        firstName = (EditText) view.findViewById(R.id.first_name);
        lastName = (EditText) view.findViewById(R.id.last_name);
        bio = (EditText) view.findViewById(R.id.bio);
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        password2 = (EditText) view.findViewById(R.id.password2);
        city = (EditText) view.findViewById(R.id.city);

        text_firstname.setTypeface(robotoRegular);
        text_last_name.setTypeface(robotoRegular);
        text_bio.setTypeface(robotoRegular);
        text_email.setTypeface(robotoRegular);
        text_password.setTypeface(robotoRegular);
        text_city.setTypeface(robotoRegular);
        text_profilepic.setTypeface(robotoRegular);
        firstName.setTypeface(robotoRegular);
        lastName.setTypeface(robotoRegular);
        bio.setTypeface(robotoRegular);
        email.setTypeface(robotoRegular);
        password.setTypeface(robotoRegular);
        password2.setTypeface(robotoRegular);
        city.setTypeface(robotoRegular);

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

        data = ((Global) getActivity().getApplicationContext()).getUserData();

        try {
            //name.setText(data.getString("first_name") + " " + data.getString("last_name"));
            firstName.setText(data.getString("first_name"));
            lastName.setText(data.getString("last_name"));
            city.setText(data.getString("city_code"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
