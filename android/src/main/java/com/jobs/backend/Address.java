package com.jobs.backend;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Address {
	public static final String PROFILE = "http://ryguy.me/profile/";
	public static final String LISTING = "http://ryguy.me/listing/";

	public static String urlEncode(Map<String, String> map) {
		Set<String> keys = map.keySet();
		Iterator<String> it = keys.iterator();
		String encoded = "";

		try {
			while (it.hasNext()) {
				String next = it.next();
				String key = URLEncoder.encode(next, "UTF-8");
				String val = URLEncoder.encode(map.get(next), "UTF-8");
				String encode = key + "=" + val;
				encoded += it.hasNext() ? encode + "&" : encode;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return encoded;
	}

    /*
	 * Sends parameters to server at the specified address. Returns an error code if there was an error on the server.
	 */
    public static int post(Map<String, String> params, String address) throws IOException, JSONException {
        String urlEncoded = Address.urlEncode(params);

        URL url = new URL(address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", "" + Integer.toString(urlEncoded.getBytes("UTF-8").length));
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        JSONObject response = new JSONObject(send(conn, urlEncoded));
        return response.getInt("error");
    }

    public static String get(Map<String, String> params, String address) throws IOException, JSONException {
        URL url = null;

        if (params != null) {
            String urlEncoded = Address.urlEncode(params);
            url = new URL(address + "?" + urlEncoded);
        } else {
            url = new URL(address);
        }

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        return send(conn, null);
    }

    public static String send(HttpURLConnection conn, String data) throws IOException, JSONException {
        if (data != null) {
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(data);
            os.flush();
            os.close();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        String response = "";
        while ((line = reader.readLine()) != null) {
            response += line;
        }

        //response = response.replaceAll("\"", "");

        reader.close();
        conn.disconnect();
        return response;
    }
}
