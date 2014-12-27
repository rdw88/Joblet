package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ViewListing extends Activity {
    private TextView title, currentBid, ownerReputation, jobLocation, ownerName, timeCreated, tag;
    private Button makeBid;
    private ImageView picture;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_listing);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/verdana.ttf");
    public void onStart() {
        super.onStart();

        title = (TextView) findViewById(R.id.view_listing_title);
        title.setTypeface(customFont);
        currentBid = (TextView) findViewById(R.id.view_listing_current_bid);
        currentBid.setTypeface(customFont);
        ownerReputation = (TextView) findViewById(R.id.view_listing_owner_reputation);
        ownerReputation.setTypeface(customFont);
        jobLocation = (TextView) findViewById(R.id.view_listing_job_location);
        jobLocation.setTypeface(customFont);
        ownerName = (TextView) findViewById(R.id.view_listing_owner_name);
        ownerName.setTypeface(customFont);
        timeCreated = (TextView) findViewById(R.id.view_listing_time_created);
        timeCreated.setTypeface(customFont);
        tag = (TextView) findViewById(R.id.view_listing_tag);
        tag.setTypeface(customFont);
        makeBid = (Button) findViewById(R.id.view_listing_make_bid);
        makeBid.setTypeface(customFont);
        picture = (ImageView) findViewById(R.id.listing_picture);

        Bundle b = getIntent().getExtras();

        if (b.containsKey("listing")) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(b.getString("listing"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            fillFromData(obj);
        } else {
            fillFromServer();
        }

        makeBid.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // TODO: Still need to implement this on the server as well!!!
            }
        });
    }

    private void fill(JSONObject data, Bitmap bitmap) {
        NumberFormat money = new DecimalFormat("#0.00");

        try {
            title.setText(data.getString("job_title"));
            currentBid.setText("Current Bid: $" + money.format(data.getDouble("current_bid")));
            ownerReputation.setText("Owner Reputation: " + data.getString("owner_reputation"));
            jobLocation.setText("Job Location: " + data.getString("job_location"));
            ownerName.setText("Owner: " + data.getString("owner_name"));
            timeCreated.setText("Date Created: " + data.getString("time_created"));
            tag.setText("Tag: " + data.getString("tag"));
            picture.setImageBitmap(bitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillFromData(final JSONObject data) {
        new AsyncTask<String, Void, String>() {
            private Bitmap bitmap;

            protected String doInBackground(String... urls) {
                try {
                    JSONArray arr = new JSONArray(data.getString("job_picture"));
                    String pictureURL = arr.getString(0);

                    try {
                        bitmap = Address.fetchPicture(Address.FILES + pictureURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {
                fill(data, bitmap);
            }
        }.execute();
    }

    private void fillFromServer() {
        new AsyncTask<String, Void, String>() {
            private JSONObject response;
            private Bitmap bitmap;

            protected String doInBackground(String... urls) {
                response = Listing.get(getIntent().getExtras().getString("listing_id"));
                try {
                    JSONArray arr = new JSONArray(response.getString("job_picture"));
                    String pictureURL = arr.getString(0);

                    try {
                        bitmap = Address.fetchPicture(Address.FILES + pictureURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {
                if (response.has("error")){
                    alertNoSuchListing();
                    return;
                }

                fill(response, bitmap);
            }
        }.execute();
    }

    private void alertNoSuchListing() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ad_error_no_such_listing);
        builder.setTitle(R.string.ad_error_no_such_listing_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
            }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
