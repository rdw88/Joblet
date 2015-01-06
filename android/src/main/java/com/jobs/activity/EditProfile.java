package com.jobs.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.Listing;
import com.jobs.backend.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jondar on 1/3/2015.
 */
public class EditProfile extends Activity {
    private EditText firstName, lastName, email, password, password2;
    private AutoCompleteTextView city;
    private TextView textFirstName, textLastName, textEmail, textPassword, textCity, textCurrentTagslabel,
            textCurrentTags;
    private Button buttonGallery, buttonCamera, setTag;
    private Typeface robotoRegular;
    private JSONObject data;

}
