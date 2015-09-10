package com.jobs.utility;


import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.lang.Integer;

/*
Class that can be accessed anywhere for global needs.
 */
public class Global extends Application {
    private JSONObject userData;
    private JSONArray notificationData;
    public GoogleApiClient googleClient;

    private double currentLongitude, currentLatitude;

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

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }
}
