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
	public static int createProfile(String firstName, String lastName, String email, String dob, String skills, String cityCode, String password) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("request", "create");
		map.put("first_name", firstName);
		map.put("last_name", lastName);
		map.put("password", password);
		map.put("dob", dob);
		map.put("skills", skills);
		map.put("city_code", cityCode);
        map.put("email", email);

		try {
			return Address.post(map, Address.PROFILE);
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
            return Address.post(data, Address.PROFILE);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return Error.ERROR_SERVER_COMMUNICATION;
        }
    }

	public static int editProfile(String profileId, String password, Map<String, String> args) {
		args.put("request", "edit");
		args.put("profile_id", profileId);
		args.put("password", password);

		try {
			return Address.post(args, Address.PROFILE);
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
			return Address.get(map, Address.PROFILE);
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
			return Address.post(data, Address.PROFILE);
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
            return Address.get(data, Address.PROFILE).getString("profile_id");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return Integer.toString(Error.ERROR_SERVER_COMMUNICATION);
        }
    }


}
