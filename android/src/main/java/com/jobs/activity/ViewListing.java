package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ViewListing extends Activity {
    private TextView title, currentBid, ownerReputation, jobLocation, ownerName, timeCreated, tag;
    private Button makeBid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_listing);
    }

    public void onStart() {
        super.onStart();

        title = (TextView) findViewById(R.id.view_listing_title);
        currentBid = (TextView) findViewById(R.id.view_listing_current_bid);
        ownerReputation = (TextView) findViewById(R.id.view_listing_owner_reputation);
        jobLocation = (TextView) findViewById(R.id.view_listing_job_location);
        ownerName = (TextView) findViewById(R.id.view_listing_owner_name);
        timeCreated = (TextView) findViewById(R.id.view_listing_time_created);
        tag = (TextView) findViewById(R.id.view_listing_tag);
        makeBid = (Button) findViewById(R.id.view_listing_make_bid);

        Bundle b = getIntent().getExtras();
        final String listingID = b.getString("listing_id");
        System.out.println(listingID);

        new AsyncTask<String, Void, String>() {
            private JSONObject response;

            protected String doInBackground(String... urls) {
                response = Listing.get(listingID);
                return null;
            }

            protected void onPostExecute(String result) {
                if (response.has("error")){
                    alertNoSuchListing();
                    return;
                }

                NumberFormat money = new DecimalFormat("#0.00");

                try {
                    title.setText(response.getString("job_title"));
                    currentBid.setText("Current Bid: $" + money.format(response.getDouble("current_bid")));
                    ownerReputation.setText("Owner Reputation: " + response.getString("owner_reputation"));
                    jobLocation.setText("Job Location: " + response.getString("job_location"));
                    ownerName.setText("Owner: " + response.getString("owner_name"));
                    timeCreated.setText("Date Created: " + response.getString("time_created"));
                    tag.setText("Tag: " + response.getString("tag"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

        makeBid.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // TODO: Still need to implement this on the server as well!!!
            }
        });
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
}
