package com.jobs.fragment;

import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.jobs.R;
import com.jobs.activity.Main;
import com.jobs.backend.Address;
import com.jobs.utility.Global;
import com.rey.material.widget.Button;
import com.rey.material.widget.SnackBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


public class EditProfile extends Fragment {
    private TextView textFirstname, textLastname, textBio, textEmail, textPassword, textCity,
            textProfilepic;
    private EditText firstName, lastName, bio, email, password, password2, city;
    private Typeface robotoRegular, robotoBlack, robotoMedium, robotoThin;
    private JSONObject data;
    private ImageView profilePicture;
    private Button saveChanges;
    private SnackBar snackBar;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        robotoBlack = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Black.ttf");
        robotoThin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile, container, false);


        profilePicture = (ImageView) view.findViewById(R.id.profile_picture);

        textFirstname = (TextView) view.findViewById(R.id.text_first_name);
        textLastname = (TextView) view.findViewById(R.id.text_last_name);
        textBio = (TextView) view.findViewById(R.id.text_bio);
        textEmail = (TextView) view.findViewById(R.id.text_email);
        textPassword = (TextView) view.findViewById(R.id.text_password);
        textCity = (TextView) view.findViewById(R.id.text_city);
        textProfilepic = (TextView) view.findViewById(R.id.text_profilepic);
        firstName = (EditText) view.findViewById(R.id.first_name);
        lastName = (EditText) view.findViewById(R.id.last_name);
        bio = (EditText) view.findViewById(R.id.bio);
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        password2 = (EditText) view.findViewById(R.id.password2);
        city = (EditText) view.findViewById(R.id.city);

        textFirstname.setTypeface(robotoRegular);
        textLastname.setTypeface(robotoRegular);
        textBio.setTypeface(robotoRegular);
        textEmail.setTypeface(robotoRegular);
        textPassword.setTypeface(robotoRegular);
        textCity.setTypeface(robotoRegular);
        textProfilepic.setTypeface(robotoRegular);
        firstName.setTypeface(robotoRegular);
        lastName.setTypeface(robotoRegular);
        bio.setTypeface(robotoRegular);
        email.setTypeface(robotoRegular);
        password.setTypeface(robotoRegular);
        password2.setTypeface(robotoRegular);
        city.setTypeface(robotoRegular);


        snackBar = (SnackBar) view.findViewById(R.id.main_sn);

        saveChanges = (Button) view.findViewById(R.id.button_createListing_postListing);
        saveChanges.setTypeface(robotoMedium);



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

        //TODO: Make upload photo button work

        saveChanges.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean validPass = false;
                String eFirstPass = password.getText().toString();
                String eSecondPass = password2.getText().toString();
                if(eFirstPass.length() > 0 && eSecondPass.length() > 0){
                    if(eFirstPass.equals(eSecondPass)){
                        if(isValidPassword(eFirstPass)){
                            validPass = true;
                        }
                    }
                }
                try {
                    data.put("first_name", firstName.getText().toString());
                    data.put("last_name", lastName.getText().toString());
                    data.put("bio", bio.getText().toString());
                    if(validPass){
                        data.put("password", password.getText().toString());
                    }
                    data.put("city_code", city.getText().toString());
                } catch (JSONException e){
                    System.err.print(e);
                }

                if(snackBar != null && snackBar.getState() == SnackBar.STATE_SHOWN)
                    snackBar.dismiss();
                else{
                    snackBar.applyStyle(R.style.SnackBarSingleLine)
                            .show();
                }


                return;

                //TODO: Make profile picture upload

            }
        });


        return view;
    }

    //TODO: Make an isValidPassword method that makes sure the password is a good password
    private boolean isValidPassword(String pass){
        return true;
    }
}
