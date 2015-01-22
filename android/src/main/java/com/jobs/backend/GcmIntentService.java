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

import java.text.DecimalFormat;

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
                String[] params = getParams(extras.toString());
                int code = Integer.parseInt(params[0]);

                if (code == 0)
                    newBidNotification(params[1], Double.parseDouble(params[2]), params[3]);
                else if (code == 1) {
                    newBidResponseNotification(params[1], Integer.parseInt(params[2]), Double.parseDouble(params[3]));
                }
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private String[] getParams(String s) {
        String text = s.substring(s.indexOf("data=") + 5, s.indexOf(","));
        return text.split("&");
    }

    private void newBidNotification(String email, double amount, String bidID) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NOTIFICATION_ID++;

        String message = email + " made a bid of $" + amount + "!";

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
}
