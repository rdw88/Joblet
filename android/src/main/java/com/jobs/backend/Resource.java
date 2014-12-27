package com.jobs.backend;

import android.content.Context;

import com.jobs.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

public class Resource {
    public static final ArrayList<String> LOCATIONS = new ArrayList<String>();
    public static final ArrayList<String> TAGS = new ArrayList<String>();

    public static final int[] PAGE_ORDER = {R.string.title_create_listing, R.string.app_name, R.string.title_browse_listings};

    public static void initLocations(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.locations);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }

            reader.close();
            writer.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String locationsJSON = writer.toString();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(locationsJSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                LOCATIONS.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void initTags(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.tags);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }

            reader.close();
            writer.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String locationsJSON = writer.toString();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(locationsJSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                TAGS.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
