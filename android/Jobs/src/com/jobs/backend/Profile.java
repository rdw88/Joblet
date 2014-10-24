package com.jobs.backend;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile {

	/*
	 * Returns NULL if created successfully, and and error code otherwise.
	 */
	public static String createProfile(String firstName, String lastName, int age, String skills, String cityCode, String password) throws IOException, JSONException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("request", "create");
		map.put("first_name", firstName);
		map.put("last_name", lastName);
		map.put("password", password);
		map.put("age", Integer.toString(age));
		map.put("skills", skills);
		map.put("city_code", cityCode);

		String urlEncoded = Address.urlEncode(map);

		URL url = new URL(Address.PROFILE);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", "" + Integer.toString(urlEncoded.getBytes("UTF-8").length));
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		DataOutputStream os = new DataOutputStream(conn.getOutputStream());

		os.writeBytes(urlEncoded);
		os.flush();

		DataInputStream is = new DataInputStream(conn.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		String response = "";
		while ((line = reader.readLine()) != null) {
			response += line;
		}

		reader.close();
		is.close();
		os.close();
		conn.disconnect();

		JSONObject obj = new JSONObject(response);
		return obj.getString("error");
	}
}
