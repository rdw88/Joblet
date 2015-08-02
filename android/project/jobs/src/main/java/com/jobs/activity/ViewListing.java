package com.jobs.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
    private TextView title, currentBid, ownerReputation, jobLocation, ownerName, timeCreated, tag,
            textCurrentBid, textOwnerReputation, textJobLocation, textOwnerName, textTimeCreated, textTag, textDescription,
            constantTextDescription, constantTextListEndsOn, textListEndsOn, constantTimeLeft, textLocation, textTimeLeft,
            textBidTimeLeft;
    private Button makeBid, addToWatchlist;
    private ImageView picture;
    private Typeface robotoRegular, robotoBlack, robotoMedium, robotoThin;
    private ActionBar actionBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_listing);
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void onStart() {
        super.onStart();


        robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        robotoBlack = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");
        robotoThin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        title = (TextView) findViewById(R.id.view_listing_title);
        title.setTypeface(robotoRegular);
        currentBid = (TextView) findViewById(R.id.view_listing_current_bid);
        currentBid.setTypeface(robotoRegular);
        textOwnerReputation = (TextView) findViewById(R.id.text_ownerreputation);
        textOwnerReputation.setTypeface(robotoThin);
        ownerReputation = (TextView) findViewById(R.id.view_listing_owner_reputation);
        ownerReputation.setTypeface(robotoBlack);
        textJobLocation = (TextView) findViewById(R.id.text_location);
        textJobLocation.setTypeface(robotoMedium);
        jobLocation = (TextView) findViewById(R.id.view_listing_job_location);
        jobLocation.setTypeface(robotoBlack);
        textOwnerName = (TextView) findViewById(R.id.text_ownername);
        textOwnerName.setTypeface(robotoThin);
        ownerName = (TextView) findViewById(R.id.view_listing_owner_name);
        ownerName.setTypeface(robotoBlack);
        tag = (TextView) findViewById(R.id.view_listing_tag);
        tag.setTypeface(robotoBlack);
        makeBid = (Button) findViewById(R.id.view_listing_make_bid);
        makeBid.setTypeface(robotoMedium);
        picture = (ImageView) findViewById(R.id.picture_swiper);
        addToWatchlist = (Button) findViewById(R.id.view_listing_add_watchlist);
        addToWatchlist.setTypeface(robotoMedium);
        textLocation = (TextView) findViewById(R.id.text_location);
        textLocation.setTypeface(robotoThin);
        textTimeLeft = (TextView) findViewById(R.id.text_timeleft);
        textTimeLeft.setTypeface(robotoThin);
        textBidTimeLeft = (TextView) findViewById(R.id.view_listing_timeleft);
        textBidTimeLeft.setTypeface(robotoBlack);
        textDescription = (TextView) findViewById(R.id.listing_description);
        textDescription.setTypeface(robotoRegular);


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

        Bundle b = getIntent().getExtras();
        if (b.containsKey("listing_id"))
            fillFromServer();
        else {
            JSONObject data = null;
            try {
                data = new JSONObject(b.getString("listing"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            fill(data);
        }
    }

    private void fill(final JSONObject data) {
        new AsyncTask<String, Void, String>() {
            private Bitmap bitmap;

            protected String doInBackground(String... args) {
                try {
                    String pictureURL = data.getString("thumbnail");

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

            protected void onPostExecute(String res) {
                NumberFormat money = new DecimalFormat("#0.00");
                NumberFormat wholeNumber = new DecimalFormat("#0");

                try {
                    String loc = Resource.formatLocation(data.getString("address"), data.getString("city"), data.getString("state"));
                    title.setText(data.getString("job_title"));
                    double curBid = data.getDouble("current_bid");
                    String displayedBid = null;
                    if(curBid < 10000){
                        displayedBid = money.format(curBid);
                    }
                    else{
                        displayedBid = wholeNumber.format(curBid);
                    }
                    currentBid.setText("$" + displayedBid);
                    ownerReputation.setText(data.getString("owner_reputation"));
                    jobLocation.setText(loc);
                    ownerName.setText(data.getString("owner_name"));
                    /* TODO: Get the time_created in a date form not a String form and calculate how
                     *much time is left and then use that to set the text for timeleft
                     */
                    //timeCreated.setText(data.getString("time_created"));
                    tag.setText(data.getString("tag"));
                    textDescription.setText(data.getString("job_description"));
                    picture.setImageBitmap(bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void fillFromServer() {
        new AsyncTask<String, Void, String>() {
            private JSONObject response;

            protected String doInBackground(String... urls) {
                response = Listing.get(getIntent().getExtras().getString("listing_id"));
                return null;
            }

            protected void onPostExecute(String result) {
                if (response.has("error")){
                    alertNoSuchListing();
                    return;
                }

                fill(response);
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
