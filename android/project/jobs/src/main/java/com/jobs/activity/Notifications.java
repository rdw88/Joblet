package com.jobs.activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jobs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Notifications extends Activity {
    private NotificationAdapter adapter;
    private JSONArray notificationData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
    }

    public void onStart() {
        super.onStart();

        Bundle b = getIntent().getExtras();
        if (b.containsKey("notification_data")) {
            try {
                notificationData = new JSONArray(b.getString("notification_data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Item> items = null;

        try {
            if (notificationData != null) {
                items = new ArrayList<>();

                for (int i = 0; i < notificationData.length(); i++) {
                    JSONObject obj = notificationData.getJSONObject(i);
                    String dt = obj.getString("time_created");
                    Item item = new Item(obj.getString("title"), obj.getString("description"), dt);
                    items.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (items != null) {
            ListView notifications = (ListView) findViewById(R.id.notification_list);
            adapter = new NotificationAdapter(this, items);
            notifications.setAdapter(adapter);
        }
    }

    private class NotificationAdapter extends ArrayAdapter<Item> {
        private ArrayList<Item> items;

        public NotificationAdapter(Context context, ArrayList<Item> items) {
            super(context, R.layout.notification_list_item, items);
            this.items = items;
        }

        @Override
        public View getView(final int position, View currentView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.notification_list_item, parent, false);

            TextView title = (TextView) row.findViewById(R.id.notification_title);
            TextView description = (TextView) row.findViewById(R.id.notification_description);
            TextView dateTime = (TextView) row.findViewById(R.id.notification_date_time);

            title.setText(items.get(position).title);
            description.setText(items.get(position).description);
            dateTime.setText(items.get(position).dateTime);

            return row;
        }

        public int getCount() {
            return items.size();
        }
    }

    private class Item {
        public String title;
        public String description;
        public String dateTime;

        public Item(String title, String description, String dateTime) {
            this.title = title;
            this.description = description;
            this.dateTime = dateTime;
        }
    }
}
