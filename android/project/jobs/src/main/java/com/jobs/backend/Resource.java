package com.jobs.backend;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

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

    private static final int EARTH_RADIUS_MILES = 3959;

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

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

    public static String formatLocation(String address, String city, String state) {
        return address + ", " + city + ", " + state;
    }

    public static double calculateDistanceInMiles(double lat1, double long1, double lat2, double long2) {
        double lat1Radians = Math.toRadians(lat1);
        double lat2Radians = Math.toRadians(lat2);
        double dLong = Math.toRadians(long2 - long1);
        double dLat = Math.toRadians(lat2 - lat1);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1Radians) * Math.cos(lat2Radians) * Math.pow(Math.sin(dLong / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * EARTH_RADIUS_MILES;
    }
}
