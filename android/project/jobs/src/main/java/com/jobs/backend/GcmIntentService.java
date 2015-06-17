package com.jobs.backend;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jobs.R;
import com.jobs.activity.Login;
import com.jobs.activity.ViewBid;
import com.jobs.activity.ViewListing;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.Iterator;

public class GcmIntentService extends IntentService {
    public static int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Bundle params = getParams(extras.getString("data"));
                int code = Integer.parseInt(params.getString("type"));

                if (code == Address.PUSH_NOTIFICATION_NEW_BID)
                    newBidNotification(params.getString("bidder_email"), Double.parseDouble(params.getString("bid_amount")), params.getString("bid_id"));
                else if (code == Address.PUSH_NOTIFICATION_BID_RESPONSE) {
                    newBidResponseNotification(params.getString("listing_id"), Integer.parseInt(params.getString("status")), Double.parseDouble(params.getString("amount")));
                } else if (code == Address.PUSH_NOTIFICATION_BID_ACCEPTED) {
                    newBidAcceptedNotification(params.getString("job_title"), Double.parseDouble(params.getString("amount")), params.getString("listing_id"));
                }
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private Bundle getParams(String str) {
        String s = null;
        try {
            s = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] data = s.split("&");
        Bundle b = new Bundle();

        for (int i = 0; i < data.length; i++) {
            String[] kv = data[i].split("=", 2);
            b.putString(kv[0], kv[1]);
        }

        return b;
    }

    private void newBidNotification(String email, double amount, String bidID) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NOTIFICATION_ID++;

        DecimalFormat format = new DecimalFormat("#.##");
        String message = email + " made a bid of $" + format.format(amount) + "!";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle("Joblet");
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        mBuilder.setContentText(message);
        mBuilder.setTicker("Someone made a bid!");

        Intent intent = new Intent(this, ViewBid.class);
        intent.putExtra("email", email);
        intent.putExtra("amount", amount);
        intent.putExtra("bid_id", bidID);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pi);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void newBidResponseNotification(final String listingID, final int acceptDecline, final double amount) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NOTIFICATION_ID++;

        new AsyncTask<String, Void, String>() {
            private JSONObject response;

            protected String doInBackground(String... urls) {
                response = Listing.get(listingID);
                return null;
            }

            protected void onPostExecute(String result) {
                String title = "";
                try {
                    title = response.getString("job_title");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DecimalFormat format = new DecimalFormat("#.##");
                String message = "";
                if (acceptDecline == 0) { // Declined
                    message = "Your bid of $" + format.format(amount) + " for " + title + " was declined!";
                } else if (acceptDecline == 1) { // Accepted
                    message = "Your bid of $" + format.format(amount) + " for " + title + " was accepted!";
                }

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GcmIntentService.this);
                mBuilder.setSmallIcon(R.drawable.logo);
                mBuilder.setContentTitle("Joblet");
                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
                mBuilder.setContentText(message);
                mBuilder.setTicker("Bid Response");

                Intent intent = new Intent(GcmIntentService.this, ViewListing.class);
                intent.putExtra("listing", response.toString());
                PendingIntent pi = PendingIntent.getActivity(GcmIntentService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                mBuilder.setContentIntent(pi);

                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }.execute();
    }

    private void newBidAcceptedNotification(String jobTitle, double amount, String listingID) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NOTIFICATION_ID++;

        DecimalFormat format = new DecimalFormat("#.##");
        String message = "Your bid of $" + format.format(amount) + " for " + jobTitle + " was accepted! Click to get in contact.";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle("Joblet");
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        mBuilder.setContentText(message);
        mBuilder.setTicker("Your bid was accepted!");

        /*Intent intent = new Intent(this, ViewBid.class);
        intent.putExtra("email", email);
        intent.putExtra("amount", amount);
        intent.putExtra("bid_id", bidID);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pi);*/

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
