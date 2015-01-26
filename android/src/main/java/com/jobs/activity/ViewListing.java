package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ViewListing extends Activity {
    private TextView title, currentBid, ownerReputation, jobLocation, ownerName, timeCreated, tag, textCurrentBid, textOwnerReputation, textJobLocation, textOwnerName, textTimeCreated, textTag;
    private Button makeBid, addToWatchlist;
    private ImageView picture;
    private Typeface robotoRegular, robotoBlack, robotoMedium;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_listing);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onStart() {
        super.onStart();

        robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        robotoBlack = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");

        title = (TextView) findViewById(R.id.view_listing_title);
        title.setTypeface(robotoRegular);
        textCurrentBid = (TextView) findViewById(R.id.text_currentbid);
        textCurrentBid.setTypeface(robotoMedium);
        currentBid = (TextView) findViewById(R.id.view_listing_current_bid);
        currentBid.setTypeface(robotoRegular);
        textOwnerReputation = (TextView) findViewById(R.id.text_ownerreputation);
        textOwnerReputation.setTypeface(robotoMedium);
        ownerReputation = (TextView) findViewById(R.id.view_listing_owner_reputation);
        ownerReputation.setTypeface(robotoRegular);
        textJobLocation = (TextView) findViewById(R.id.text_location);
        textJobLocation.setTypeface(robotoMedium);
        jobLocation = (TextView) findViewById(R.id.view_listing_job_location);
        jobLocation.setTypeface(robotoRegular);
        textOwnerName = (TextView) findViewById(R.id.text_ownername);
        textOwnerName.setTypeface(robotoMedium);
        ownerName = (TextView) findViewById(R.id.view_listing_owner_name);
        ownerName.setTypeface(robotoRegular);
        textTimeCreated = (TextView) findViewById(R.id.text_listingtimecreated);
        textTimeCreated.setTypeface(robotoMedium);
        timeCreated = (TextView) findViewById(R.id.view_listing_time_created);
        timeCreated.setTypeface(robotoRegular);
        textTag = (TextView) findViewById(R.id.text_tag);
        textTag.setTypeface(robotoMedium);
        tag = (TextView) findViewById(R.id.view_listing_tag);
        tag.setTypeface(robotoRegular);
        makeBid = (Button) findViewById(R.id.view_listing_make_bid);
        makeBid.setTypeface(robotoMedium);
        picture = (ImageView) findViewById(R.id.picture_swiper);
        addToWatchlist = (Button) findViewById(R.id.view_listing_add_watchlist);
        addToWatchlist.setTypeface(robotoMedium);

        makeBid.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewListing.this);
                View view = getLayoutInflater().inflate(R.layout.make_bid, null);
                final EditText bid = (EditText) view.findViewById(R.id.entered_bid);

                builder.setView(view);
                builder.setTitle("Make A Bid");
                builder.setPositiveButton(R.string.btn_make_bid, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int which) {
                        new AsyncTask<String, Void, String>() {
                            private int response;

                            protected String doInBackground(String... params) {
                                String enteredBid = bid.getText().toString();
                                String listingID = getIntent().getExtras().getString("listing_id");
                                String email = "";

                                try {
                                    SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
                                    JSONObject obj = new JSONObject(prefs.getString("user_data", null));
                                    email = obj.getString("email");
                                    response = Bid.makeBid(listingID, email, enteredBid).getInt("error");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                return null;
                            }

                            protected void onPostExecute(String res) {
                                if (response == -1) {
                                    alertBidRequestSent();
                                }
                            }
                        }.execute();
                    }
                });
                builder.setNegativeButton(R.string.button_cancel, null);

                builder.show();

            }
        });

        fillFromServer();
    }

    private void fill(JSONObject data, Bitmap bitmap) {
        NumberFormat money = new DecimalFormat("#0.00");


        try {
            String loc = Resource.formatLocation(data.getString("address"), data.getString("city"), data.getString("state"));
            title.setText(data.getString("job_title"));
            currentBid.setText("$" + money.format(data.getDouble("current_bid")));
            ownerReputation.setText(data.getString("owner_reputation"));
            jobLocation.setText(loc);
            ownerName.setText(data.getString("owner_name"));
            timeCreated.setText(data.getString("time_created"));
            tag.setText(data.getString("tag"));
            picture.setImageBitmap(bitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillFromServer() {
        new AsyncTask<String, Void, String>() {
            private JSONObject response;
            private Bitmap bitmap;

            protected String doInBackground(String... urls) {
                response = Listing.get(getIntent().getExtras().getString("listing_id"));
                try {
                    String pictureURL = response.getString("thumbnail");

                    try {
                        bitmap = Address.fetchPicture(pictureURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {
                picture.setImageBitmap(ImageHelper.getCircularBitmapWithWhiteBorder(bitmap, 1));
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

    private void alertBidRequestSent() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ad_bid_request_sent);
        builder.setTitle(R.string.ad_bid_request_sent_title);

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
