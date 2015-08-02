package com.jobs.utility;


import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

/*
Class that can be accessed anywhere for global needs.
 */
public class Global extends Application {
    private JSONObject userData;
    private JSONArray notificationData;

    public void setUserData(JSONObject userData) {
        this.userData = userData;
    }

    public JSONObject getUserData() {
        return userData;
    }

    public void setNotificationData(JSONArray data) {
        this.notificationData = data;
    }

    public JSONArray getNotificationData() {
        return notificationData;
    }
}
