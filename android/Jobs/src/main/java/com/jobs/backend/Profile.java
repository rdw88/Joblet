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
	 * Returns -1 if created successfully, and and error code otherwise.
	 */
	public static int createProfile(String firstName, String lastName, String email, int age, String skills, String cityCode, String password) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("request", "create");
		map.put("first_name", firstName);
		map.put("last_name", lastName);
		map.put("password", password);
		map.put("age", Integer.toString(age));
		map.put("skills", skills);
		map.put("city_code", cityCode);
        map.put("email", email);

		try {
			return post(map, Address.PROFILE);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return Error.ERROR_SERVER_COMMUNICATION;
		}
	}

    public static int login(String email, String password) {
        Map<String, String> data = new HashMap<>();
        data.put("request", "login");
        data.put("email", email);
        data.put("password", password);

        try {
            return post(data, Address.PROFILE);
        } catch (IOException | JSONException e) {
            return Error.ERROR_SERVER_COMMUNICATION;
        }
    }

	public static int editProfile(String profileId, String password, Map<String, String> args) {
		args.put("request", "edit");
		args.put("profile_id", profileId);
		args.put("password", password);

		try {
			return post(args, Address.PROFILE);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return Error.ERROR_SERVER_COMMUNICATION;
		}
	}

	public static JSONObject getProfile(String profileId) {
		Map<String, String> map = new HashMap<String, String>();
        map.put("request", "profile");
		map.put("profile_id", profileId);

		try {
			return get(map, Address.PROFILE);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			JSONObject error = new JSONObject();

			try {
				error.put("error", Integer.toString(Error.ERROR_SERVER_COMMUNICATION));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			return error;
		}
	}

	public static int deleteProfile(String profileId, String password) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("request", "delete");
		data.put("profile_id", profileId);
		data.put("password", password);

		try {
			return post(data, Address.PROFILE);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return Error.ERROR_SERVER_COMMUNICATION;

		}
	}

    public static String getID(String email) {
        Map<String, String> data = new HashMap<>();
        data.put("request", "get_id");
        data.put("email", email);

        try {
            return get(data, Address.PROFILE).getString("profile_id");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return Integer.toString(Error.ERROR_SERVER_COMMUNICATION);
        }
    }

	/*
	 * Sends parameters to server at the specified address. Returns an error code if there was an error on the server.
	 */
	private static int post(Map<String, String> params, String address) throws IOException, JSONException {
		String urlEncoded = Address.urlEncode(params);

		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", "" + Integer.toString(urlEncoded.getBytes("UTF-8").length));
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		JSONObject response = send(conn, urlEncoded);
		return response.getInt("error");
	}

	private static JSONObject get(Map<String, String> params, String address) throws IOException, JSONException {
		String urlEncoded = Address.urlEncode(params);
		URL url = new URL(address + "?" + urlEncoded);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		return send(conn, null);
	}

	private static JSONObject send(HttpURLConnection conn, String data) throws IOException, JSONException {
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
		JSONObject obj = new JSONObject(response);

		return obj;
	}
}
