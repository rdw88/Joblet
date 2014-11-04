package com.jobs.backend;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Listing {

    public static int create(String title, String startingAmount, String minReputation, String jobLocation, String activeTime, String profileID) {
        Map<String, String> map = new HashMap<>();
        map.put("request", "create");
        map.put("job_title", title);
        map.put("starting_amount", startingAmount);
        map.put("min_reputation", minReputation);
        map.put("job_location", jobLocation);
        map.put("active_time", activeTime);
        map.put("profile_id", profileID);

        try {
            return Address.post(map, Address.LISTING);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return Error.ERROR_SERVER_COMMUNICATION;
        }
    }

    public static JSONObject get(String listingID) {
        Map<String, String> map = new HashMap<>();
        map.put("request", "get");
        map.put("listing_id", listingID);

        try {
            return Address.get(map, Address.LISTING);
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

    public static JSONObject search(HashMap<String, String> map) {
        map.put("request", "search");

        try {
            return Address.get(map, Address.LISTING);
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
    updateOperation tells the server what we are updating to the listing with the associated
    listingID. The args HashMap are additional arguments that the server may need to process an update.
    The passed HashMap can be null.
     */
    public static int update(String updateOperation, String listingID, HashMap<String, String> args) {
        HashMap<String, String> map = args == null ? new HashMap<String, String>() : args;
        map.put("request", "update");
        map.put("listing_id", listingID);
        map.put("update_op", updateOperation);

        try{
            return Address.post(map, Address.LISTING);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return Error.ERROR_SERVER_COMMUNICATION;
        }
    }

    /*
    Edits the private side of a listing that only the owner of the listing can edit.
     */
    public static int edit(String email, String password, String listingID, HashMap<String, String> map) {
        map.put("request", "edit");
        map.put("email", email);
        map.put("password", password);
        map.put("listing_id", listingID);

        try{
            return Address.post(map, Address.LISTING);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return Error.ERROR_SERVER_COMMUNICATION;
        }
    }

    public static int delete(String email, String password, String listingID) {
        Map<String, String> map = new HashMap<>();
        map.put("request", "delete");
        map.put("email", email);
        map.put("password", password);
        map.put("listing_id", listingID);

        try{
            return Address.post(map, Address.LISTING);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return Error.ERROR_SERVER_COMMUNICATION;
        }
    }
}
