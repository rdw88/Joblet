package com.jobs.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.jobs.R;
import com.jobs.backend.*;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class TagSelector extends Activity {
    private final ArrayList<String> selected = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_selector);
    }

    public void onStart() {
        super.onStart();

        Button confirm = (Button) findViewById(R.id.createacc_tags_confirm);
        confirm.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                confirm();
            }
        });

        Bundle b = getIntent().getExtras();

        if (b != null) {
            String[] arr = b.getStringArray("array");
            for (int i = 0; i < arr.length; i++) {
                selected.add(arr[i]);
            }
        }

        final ArrayList<String> items = Resource.TAGS;
        ListView listings = (ListView) findViewById(R.id.tag_list);
        ListAdapter adapter = new ListAdapter(TagSelector.this, items);
        listings.setAdapter(adapter);
        listings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox box = (CheckBox) view.findViewById(R.id.tag_checkbox);
                box.setChecked(!box.isChecked());

                if (box.isChecked())
                    selected.add(items.get(position));
                else
                    selected.remove(items.get(position));
            }
        });
    }

    private void confirm() {
        String[] s = new String[selected.size()];
        selected.toArray(s);
        Intent result = new Intent();
        result.putExtra("array", s);
        setResult(Activity.RESULT_OK, result);
        finish();
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
            View row = inflater.inflate(R.layout.tag_list_item, parent, false);

            final CheckBox box = (CheckBox) row.findViewById(R.id.tag_checkbox);

            if (selected.contains(items.get(position))) {
                box.setChecked(true);
            }

            TextView text = (TextView) row.findViewById(R.id.tag_name);

            box.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if (box.isChecked())
                        selected.add(items.get(position));
                    else
                        selected.remove(items.get(position));
                }
            });

            text.setText(items.get(position));

            return row;
        }
    }
}
