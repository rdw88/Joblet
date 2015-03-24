package com.jobs.backend;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
import org.json.JSONException;
import org.json.JSONObject;

public class Profile {

	/*
	 * Returns -1 if created successfully, and and error code otherwise.
	 */
	public static JSONObject createProfile(HashMap<String, String> map) {
		map.put("request", "create");
        JSONObject response = null;

		try {
			response = Address.post(map, Address.PROFILE);
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

        int res = Profile.upload(map.get("profile_picture"), map.get("email"), map.get("password"));
        try {
            response.put("error", res);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static JSONObject login(String email, String password) {
        Map<String, String> data = new HashMap<>();
        data.put("request", "login");
        data.put("email", email);
        data.put("password", password);

        try {
            return Address.post(data, Address.PROFILE);
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

	public static JSONObject editProfile(String profileId, String password, Map<String, String> args) {
		args.put("request", "edit");
		args.put("profile_id", profileId);
		args.put("password", password);

		try {
			return Address.post(args, Address.PROFILE);
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

	public static JSONObject getProfile(String profileId) {
		Map<String, String> map = new HashMap<String, String>();
        map.put("request", "get");
		map.put("profile_id", profileId);

		try {
			return new JSONObject(Address.get(map, Address.PROFILE));
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

	public static JSONObject deleteProfile(String profileId, String password) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("request", "delete");
		data.put("profile_id", profileId);
		data.put("password", password);

		try {
			return Address.post(data, Address.PROFILE);
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

    public static String getID(String email) {
        Map<String, String> data = new HashMap<>();
        data.put("request", "getID");
        data.put("email", email);

        try {
            return new JSONObject(Address.get(data, Address.PROFILE)).getString("profile_id");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return Integer.toString(Error.ERROR_SERVER_COMMUNICATION);
        }
    }

    public static JSONObject registerPhoneID(String email, String id) {
        Map<String, String> data = new HashMap<>();
        data.put("request", "device_id");
        data.put("email", email);
        data.put("id", id);

        try{
            return Address.post(data, Address.PROFILE);
        } catch (JSONException | IOException e) {
            JSONObject error = new JSONObject();

            try {
                error.put("error", Integer.toString(Error.ERROR_SERVER_COMMUNICATION));
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return error;
        }
    }

    public static int upload(String image, String email, String password) {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost(Address.UPLOAD);
        File file = new File(image);

        try {
            MultipartEntity mpEntity = new MultipartEntity();
            ContentBody b = new FileBody(file);
            mpEntity.addPart("file", b);
            mpEntity.addPart("destination", new StringBody("profile"));
            mpEntity.addPart("email", new StringBody(email));
            mpEntity.addPart("password", new StringBody(password));
            httppost.setEntity(mpEntity);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resEntity.getContent()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            if (resEntity != null) {
                resEntity.consumeContent();
            }

            httpclient.getConnectionManager().shutdown();
        } catch (IOException e) {
            return Error.ERROR_IMAGE_UPLOAD_FAILED;
        }

        return -1;
    }

    public static boolean confirmEmailCanBeUsed(String email) {
        HashMap<String, String> map = new HashMap<>();
        map.put("request", "confirm_email");
        map.put("email", email);

        try{
            String response = Address.get(map, Address.PROFILE);
            return response.equals("true");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
