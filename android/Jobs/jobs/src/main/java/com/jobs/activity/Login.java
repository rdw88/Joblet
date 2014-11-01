package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.jobs.R;
import android.widget.Button;
import android.widget.EditText;
import com.jobs.backend.*;
import com.jobs.backend.Error;
import org.json.JSONObject;


public class Login extends Activity {
    private EditText email;
    private EditText password;

    private JSONObject obj;
    private ProgressDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    protected void onStart() {
        super.onStart();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        Button createAccount = (Button) findViewById(R.id.create_acc);
        createAccount.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, CreateAccount.class);
                startActivity(intent);
            }
        });

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                dialog = ProgressDialog.show(Login.this, getResources().getString(R.string.pb_login_title), getResources().getString(R.string.pb_login_message));

                new AsyncTask<String, Void, String>() {
                    private int response;

                    protected String doInBackground(String... urls) {
                        response = Profile.login(email.getText().toString(), password.getText().toString());
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
                dialog.dismiss();
                Intent intent = new Intent(Login.this, Main.class);
                intent.putExtra("data", response);
                startActivity(intent);
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
