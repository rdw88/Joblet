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
import com.jobs.backend.Bid;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class ViewBid extends Activity {
    private TextView email, bid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_bid);
        email = (TextView) findViewById(R.id.bidder_email);
        bid = (TextView) findViewById(R.id.bid_amount);
        Button accept = (Button) findViewById(R.id.accept);
        Button decline = (Button) findViewById(R.id.decline);

        accept.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                new AsyncTask<String, Void, String>() {
                    protected String doInBackground(String... params) {
                        Bid.accept(getIntent().getExtras().getString("bid_id"));
                        return null;
                    }

                    protected void onPostExecute(String str) {
                        finish();
                    }
                }.execute();
            }
        });

        decline.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                new AsyncTask<String, Void, String>() {
                    protected String doInBackground(String... params) {
                        Bid.decline(getIntent().getExtras().getString("bid_id"));
                        return null;
                    }

                    protected void onPostExecute(String str) {
                        finish();
                    }
                }.execute();
            }
        });
    }

    public void onStart() {
        super.onStart();

        new AsyncTask<String, Void, String>() {
            private JSONObject obj;

            protected String doInBackground(String... params) {
                obj = Bid.get(getIntent().getExtras().getString("bid_id"));
                return null;
            }

            protected void onPostExecute(String str) {
                try {
                    double amount = obj.getDouble("amount");
                    DecimalFormat format = new DecimalFormat("#.##");
                    email.setText(obj.getString("email"));
                    bid.setText("$" + format.format(amount));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
