package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
<<<<<<< HEAD
=======
import android.content.Intent;
import android.os.AsyncTask;
>>>>>>> f2e65d7c1a4375bf45b44ce27b92ea2d2ea4999d
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.Bid;

import java.text.DecimalFormat;

public class ViewBid extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_bid);
    }

    public void onStart() {
        super.onStart();

        final Bundle b = getIntent().getExtras();

        TextView email = (TextView) findViewById(R.id.bidder_email);
        TextView bid = (TextView) findViewById(R.id.bid_amount);

        DecimalFormat format = new DecimalFormat("#.##");

        email.setText(b.getString("email"));
        bid.setText("$" + format.format(b.getDouble("amount")));

        Button accept = (Button) findViewById(R.id.accept_bid);
        Button decline = (Button) findViewById(R.id.decline_bid);
        Button close = (Button) findViewById(R.id.close);

        accept.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                new AsyncTask<String, Void, String>(){
                    protected String doInBackground(String... args) {
                        Bid.accept(b.getString("bid_id"));
                        return null;
                    }


        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                finish();
                    protected void onPostExecute(String message) {
                        Intent intent = new Intent();
                        intent.putExtra("position", getIntent().getExtras().getInt("position"));
                        intent.putExtra("action", 1);
                        setResult(0xff, intent);
                        finish();
                    }
                }.execute();
            }
        });

        builder.setView(v);
        builder.setPositiveButton(R.string.accept, listener);
        builder.setNegativeButton(R.string.decline, listener);
        builder.setTitle("Respond to Offer");

        AlertDialog dialog = builder.create();
        dialog.show();
        decline.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                new AsyncTask<String, Void, String>(){
                    protected String doInBackground(String... args) {
                        Bid.decline(b.getString("bid_id"));
                        return null;
                    }

                    protected void onPostExecute(String message) {
                        Intent intent = new Intent();
                        intent.putExtra("position", getIntent().getExtras().getInt("position"));
                        intent.putExtra("action", 2);
                        setResult(0xff, intent);
                        finish();
                    }
                }.execute();
            }
        });

        close.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("position", getIntent().getExtras().getInt("position"));
                intent.putExtra("action", 0);
                setResult(0xff, intent);
                finish();
            }
        });
    }
}
