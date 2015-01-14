package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jobs.R;

import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.jobs.backend.*;
import com.jobs.backend.Error;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class Login extends Activity {
    private EditText email;
    private EditText password;

    private ProgressDialog dialog;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging gcm;
    private String regid;
    private String senderID = "906497299543"; // Project number

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        Resource.initLocations(this);
        Resource.initTags(this);
    }

    protected void onStart() {
        super.onStart();

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        final CheckBox rememberMe = (CheckBox) findViewById(R.id.checkbox_remember_me);
        rememberMe.setTypeface(customFont);

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        if (prefs.contains("email") && prefs.contains("password")) {
            email.setText(prefs.getString("email", null));
            password.setText(prefs.getString("password", null));
            rememberMe.setChecked(true);
        }

        Button login = (Button) findViewById(R.id.login);
        login.setTypeface(customFont);
        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                dialog = ProgressDialog.show(Login.this, getResources().getString(R.string.pb_login_title), getResources().getString(R.string.pb_login_message));
                if (rememberMe.isChecked()) {
                    SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("email", email.getText().toString());
                    editor.putString("password", password.getText().toString());
                    editor.apply();
                }

                new AsyncTask<String, Void, String>() {
                    private int response;

                    protected String doInBackground(String... urls) {
                        try {
                            response = Profile.login(email.getText().toString(), password.getText().toString()).getInt("error");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    protected void onPostExecute(String result) {
                        if (response == Error.ERROR_INCORRECT_PASSWORD || response == Error.ERROR_NO_SUCH_PROFILE) {
                            dialog.dismiss();
                            alertIncorrectPassword();
                        } else if (response == -1) {
                            if (checkPlayServices()) {
                                gcm = GoogleCloudMessaging.getInstance(Login.this);
                                regid = getRegistrationId(getApplicationContext());

                                if (regid.isEmpty()) {
                                    registerInBackground(email.getText().toString());
                                }
                            }

                            getProfileInfo();
                        }
                    }
                }.execute();
            }
        });

        Button createAccount = (Button) findViewById(R.id.btn_create_account);
        createAccount.setTypeface(customFont);
        createAccount.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CreateAccount.class));
            }
        });
    }

    private void getProfileInfo() {
        new AsyncTask<String, Void, String>() {
            private String response;

            protected String doInBackground(String... urls) {
                String profileID = Profile.getID(email.getText().toString());
                response = Profile.getProfile(profileID).toString();
                return null;
            }

            protected void onPostExecute(String result) {
                Intent intent = new Intent(Login.this, Main.class);
                intent.putExtra("data", response);
                startActivity(intent);
                dialog.dismiss();
            }
        }.execute();
    }

    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    private void alertIncorrectPassword() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setMessage(R.string.ad_wrong_password);
        builder.setTitle(R.string.ad_wrong_password_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface di, int i) {
                 password.setText("");
             }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }

            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString("registration_id", "");
        if (registrationId.isEmpty()) {
            return "";
        }

        int registeredVersion = prefs.getInt("app_version", Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }

        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(Login.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground(final String email) {
        new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    regid = gcm.register(senderID);

                    try {
                        JSONObject obj = Profile.registerPhoneID(email, regid);
                        if (obj.has("error") && obj.getInt("error") == -1) {
                            storeRegistrationId();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String msg) {
            }
        }.execute();
    }

    private void storeRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences(getApplicationContext());
        int appVersion = getAppVersion(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("registration_id", regid);
        editor.putInt("app_version", appVersion);
        editor.apply();
    }
}
