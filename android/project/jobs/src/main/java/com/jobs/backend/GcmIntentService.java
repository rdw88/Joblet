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
                String title = extras.getString("title");
                String description = extras.getString("description");
                JSONObject data = null;
                if (extras.containsKey("extras")) {
                    try {
                        data = new JSONObject(extras.getString("extras"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                showNotification(title, description, data);
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void showNotification(String title, String description, JSONObject extras) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NOTIFICATION_ID++;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle("Joblet");
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(description));
        mBuilder.setContentText(description);
        mBuilder.setTicker(title);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
