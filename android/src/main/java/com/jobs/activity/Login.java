package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.jobs.R;

import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.jobs.backend.*;
import com.jobs.backend.Error;

import org.json.JSONException;
import org.json.JSONObject;


public class Login extends Activity {
    private EditText email;
    private EditText password;

    private JSONObject obj;
    private ProgressDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
    }

    protected void onStart() {
        super.onStart();

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/verdana.ttf");
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
                            getProfileInfo();
                        }
                    }
                }.execute();
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
}
