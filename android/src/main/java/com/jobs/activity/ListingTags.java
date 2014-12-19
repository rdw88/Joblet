package com.jobs.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.jobs.R;
import com.jobs.backend.Address;
import com.jobs.backend.Resource;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class ListingTags extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_tags);
    }

    public void onStart() {
        super.onStart();

        final ArrayList<String> items = Resource.TAGS;
        ListView listings = (ListView) findViewById(R.id.listing_tags_list_view);
        ListAdapter adapter = new ListAdapter(ListingTags.this, items);
        listings.setAdapter(adapter);
        listings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent response = new Intent();
                response.putExtra("tag", items.get(position));
                setResult(Activity.RESULT_OK, response);
                finish();
            }
        });
    }

    private class ListAdapter extends ArrayAdapter<String> {
        private Context context;
        private ArrayList<String> items;

        public ListAdapter(Context context, ArrayList<String> items) {
            super(context, R.layout.list_item, items);
            this.context = context;
            this.items = items;
        }

        @Override
        public View getView(final int position, View currentView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.listing_tags_list_item, parent, false);

            TextView text = (TextView) row.findViewById(R.id.tag);
            text.setText(items.get(position));

            return row;
        }
    }
}
