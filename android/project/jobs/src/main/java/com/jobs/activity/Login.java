package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
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
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jobs.R;

import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.jobs.backend.*;
import com.jobs.backend.Error;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class Login extends Activity {
    private EditText email;
    private EditText password;
    private CheckBox rememberMe;
    private TextView remembermeText;

    private ProgressDialog dialog;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging gcm;
    private String regid;
    private String senderID = "906497299543"; // Project number

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Resource.initLocations(this);
        Resource.initTags(this);

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        if (prefs.contains("email") && prefs.contains("password")) {
            if (prefs.getBoolean("isLoggedIn", false)) {
                doLogin(prefs.getString("email", null), prefs.getString("password", null));
                return;
            }
        }
    }

    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        if (!prefs.getBoolean("isLoggedIn", false))
            startUI();
    }

    private void startUI() {
        setContentView(R.layout.login);

        Typeface robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface robotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        Typeface robotoThin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        rememberMe = (CheckBox) findViewById(R.id.checkbox_remember_me);

        com.rey.material.widget.Button login = (com.rey.material.widget.Button) findViewById(R.id.login);

        remembermeText = (TextView) findViewById(R.id.remembermeText);
        remembermeText.setTypeface(robotoRegular);
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        if (prefs.contains("email") && prefs.contains("password")) {
            password.setText(prefs.getString("password", null));
            email.setText(prefs.getString("email", null));
            rememberMe.setChecked(true);
        }

        login.setTypeface(robotoMedium);
        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                dialog = ProgressDialog.show(Login.this, getResources().getString(R.string.pb_login_title), getResources().getString(R.string.pb_login_message));
                if (rememberMe.isChecked()) {
                    SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("email", email.getText().toString());
                    editor.putString("password", password.getText().toString());
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                }

                doLogin(email.getText().toString(), password.getText().toString());
            }
        });
        Button forgotPassword = (Button) findViewById(R.id.forgotPass);
        forgotPassword.setTypeface(robotoMedium);

        Button createAccount = (Button) findViewById(R.id.btn_create_account);
        createAccount.setTypeface(robotoMedium);
        createAccount.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CreateAccount.class));
            }
        });
    }

    private void doLogin(final String email, final String password) {
        new AsyncTask<String, Void, String>() {
            private int response;

            protected String doInBackground(String... urls) {
                try {
                    response = Profile.login(email, password).getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {
                if (dialog != null)
                    dialog.dismiss();

                if (response == Error.ERROR_INCORRECT_PASSWORD || response == Error.ERROR_NO_SUCH_PROFILE) {
                    alertIncorrectPassword();
                } else if (response == -1) {
                    if (checkPlayServices()) {
                        gcm = GoogleCloudMessaging.getInstance(Login.this);
                        registerInBackground(email);
                    }

                    getProfileInfo(email, password);
                }
            }
        }.execute();
    }

    private void getProfileInfo(final String email, final String password) {
        new AsyncTask<String, Void, String>() {
            private String response;

            protected String doInBackground(String... urls) {
                String profileID = Profile.getID(email);
                JSONObject obj = Profile.getProfile(profileID);
                try {
                    obj.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                response = obj.toString();
                return null;
            }

            protected void onPostExecute(String result) {
                SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_data", response);
                editor.apply();
                Intent intent = new Intent(Login.this, Main.class);
                intent.putExtra("data", response);
                startActivityForResult(intent, 0xff);
            }
        }.execute();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0xff:
                SharedPreferences.Editor prefs = getPreferences(Context.MODE_PRIVATE).edit();
                prefs.putBoolean("isLoggedIn", false);
                prefs.apply();
                break;
        }
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

    private void registerInBackground(final String email) {
        new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    regid = gcm.register(senderID);
                    Profile.registerPhoneID(email, regid);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String msg) {
            }
        }.execute();
    }
}
