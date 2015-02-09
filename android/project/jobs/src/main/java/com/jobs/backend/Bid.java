package com.jobs.backend;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Bid {
    public static JSONObject makeBid(String listingID, String email, String amount) {
        Map<String, String> map = new HashMap<>();
        map.put("request", "make_bid");
        map.put("bidder_email", email);
        map.put("listing_id", listingID);
        map.put("bid_amount", amount);

        try{
            return Address.post(map, Address.BID);
        } catch (JSONException | IOException e) {
            JSONObject obj = new JSONObject();

            try {
                obj.put("error", Error.ERROR_SERVER_COMMUNICATION);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return obj;
        }
    }

    public static JSONObject get(String bidID) {
        Map<String, String> map = new HashMap<>();
        map.put("request", "get");
        map.put("bid_id", bidID);

        try{
            return new JSONObject(Address.get(map, Address.BID));
        } catch (JSONException | IOException e) {
            JSONObject obj = new JSONObject();

            try {
                obj.put("error", Error.ERROR_SERVER_COMMUNICATION);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return obj;
        }
    }

    public static JSONObject accept(String bidID) {
        return respond(bidID, "accept");
    }

    public static JSONObject decline(String bidID) {
        return respond(bidID, "decline");
    }

    private static JSONObject respond(String bidID, String response) {
        Map<String, String> map = new HashMap<>();
        map.put("request", response);
        map.put("bid_id", bidID);

        try{
            return Address.post(map, Address.BID);
        } catch (JSONException | IOException e) {
            JSONObject obj = new JSONObject();

            try {
                obj.put("error", Error.ERROR_SERVER_COMMUNICATION);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return obj;
        }
    }
}
