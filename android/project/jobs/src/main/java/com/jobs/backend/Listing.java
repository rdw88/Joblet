package com.jobs.backend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import org.apache.http.HttpStatus;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import java.io.File;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

public class Listing {

    public static JSONObject create(String title, String startingAmount, String minReputation, String expirationDate,
                                    String address, String city, String state, double latitude, double longitude,
                                    String email, String password, String description) {

        Map<String, String> map = new HashMap<>();
        map.put("request", "create");
        map.put("job_title", title);
        map.put("starting_amount", startingAmount);
        map.put("owner_email", email);
        map.put("password", password);
        map.put("address", address);
        map.put("city", city);
        map.put("state", state);
        map.put("latitude", Double.toString(latitude));
        map.put("longitude", Double.toString(longitude));

        // Optional, may be null
        if (minReputation != null)
            map.put("min_reputation", minReputation);

        if (expirationDate != null)
            map.put("expiration_date", expirationDate);

        if (description != null)
            map.put("job_description", description);

        try {
            return Address.post(map, Address.LISTING);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JSONObject obj = new JSONObject();

            try {
                obj.put("error", Error.ERROR_SERVER_COMMUNICATION);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return obj;
        }
    }

    public static JSONObject get(String listingID) {
        Map<String, String> map = new HashMap<>();
        map.put("request", "get");
        map.put("listing_id", listingID);

        try {
            return new JSONObject(Address.get(map, Address.LISTING));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JSONObject obj = new JSONObject();

            try {
                obj.put("error", Error.ERROR_SERVER_COMMUNICATION);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return obj;
        }
    }

    public static JSONArray search(HashMap<String, String> map) {
        map.put("request", "search");

        try {
            return new JSONArray(Address.get(map, Address.LISTING));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JSONArray obj = new JSONArray();
            obj.put(Error.ERROR_SERVER_COMMUNICATION);
            return obj;
        }
    }

    public static int upload(String image, String listingID, String email, String password) {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost(Address.UPLOAD);
        File file = new File(image);

        try {
            MultipartEntity mpEntity = new MultipartEntity();
            ContentBody b = new FileBody(file);
            mpEntity.addPart("file", b);
            mpEntity.addPart("listing_id", new StringBody(listingID));
            mpEntity.addPart("destination", new StringBody("listing"));
            mpEntity.addPart("email", new StringBody(email));
            mpEntity.addPart("password", new StringBody(password));
            httppost.setEntity(mpEntity);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    resEntity.consumeContent();
                    return Error.ERROR_IMAGE_UPLOAD_FAILED;
                }

                resEntity.consumeContent();
            }

            httpclient.getConnectionManager().shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private static int getImageOrientation(File file) {
        try{
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                default:
                    return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /*
    updateOperation tells the server what we are updating to the listing with the associated
    listingID. The args HashMap are additional arguments that the server may need to process an update.
    The passed HashMap can be null.
     */
    public static JSONObject update(String updateOperation, String listingID, HashMap<String, String> args) {
        HashMap<String, String> map = args == null ? new HashMap<String, String>() : args;
        map.put("request", "update");
        map.put("listing_id", listingID);
        map.put("update_op", updateOperation);

        try{
            return Address.post(map, Address.LISTING);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JSONObject obj = new JSONObject();

            try {
                obj.put("error", Error.ERROR_SERVER_COMMUNICATION);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return obj;
        }
    }

    /*
    Edits the private side of a listing that only the owner of the listing can edit.
     */
    public static JSONObject edit(String email, String password, String listingID, HashMap<String, String> map) {
        map.put("request", "edit");
        map.put("email", email);
        map.put("password", password);
        map.put("listing_id", listingID);

        try{
            return Address.post(map, Address.LISTING);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JSONObject obj = new JSONObject();

            try {
                obj.put("error", Error.ERROR_SERVER_COMMUNICATION);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return obj;
        }
    }

    public static JSONObject delete(String email, String password, String listingID) {
        Map<String, String> map = new HashMap<>();
        map.put("request", "delete");
        map.put("email", email);
        map.put("password", password);
        map.put("listing_id", listingID);

        try{
            return Address.post(map, Address.LISTING);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JSONObject obj = new JSONObject();

            try {
                obj.put("error", Error.ERROR_SERVER_COMMUNICATION);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return obj;
        }
    }

    public static JSONArray getBids(String listingID) {
        Map<String, String> map = new HashMap<>();
        map.put("request", "get_bids");
        map.put("listing_id", listingID);

        try{
            return new JSONArray(Address.get(map, Address.LISTING));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            JSONArray obj = new JSONArray();
            obj.put(Error.ERROR_SERVER_COMMUNICATION);
            return obj;
        }
    }
}
