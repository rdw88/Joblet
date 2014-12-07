package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import com.jobs.R;
import com.jobs.backend.Profile;
import com.jobs.backend.Error;
import org.w3c.dom.Text;

public class CreateAccount extends Activity {
    private EditText firstName, lastName, email, password, passwordRetry, city;
    private Button addTags, create;
    private TextView tags;
    private DatePicker dob;
    private String[] addedTags;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_account);
    }

    protected void onStart() {
        super.onStart();

        //dob = (DatePicker) findViewById(R.id.dob);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        passwordRetry = (EditText) findViewById(R.id.password2);
<<<<<<< HEAD
        tags = (EditText) findViewById(R.id.profile_tags);
=======
        tags = (TextView) findViewById(R.id.tags);
>>>>>>> 1c117e7b7725e889fce580f410254744e8b4c786
        city = (EditText) findViewById(R.id.city);
        addTags = (Button) findViewById(R.id.button_add_tags);
        create = (Button) findViewById(R.id.button_create);

        addTags.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(CreateAccount.this, TagSelector.class);

                if (addedTags != null) {
                    intent.putExtra("array", addedTags);
                }

                startActivityForResult(intent, 0xf2);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String pw = password.getText().toString();
                String retry = passwordRetry.getText().toString();

                if (tags.getText().toString().equals("")){
                    alertNeedsTags();
                } else if (!pw.equals(retry)) {
                    alertPasswordMismatch();
                } else {
                    createAccount();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0xf2:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle b = data.getExtras();
                    addedTags = b.getStringArray("array");

                    if (addedTags.length != 0) {
                        String str = "";
                        for (int i = 0; i < addedTags.length - 1; i++) {
                            str += addedTags[i] + ", ";
                        }
                        str += addedTags[addedTags.length - 1];
                        tags.setText(str);
                    }
                }

                break;
        }
    }

    private void createAccount() {
        final String fn = firstName.getText().toString();
        final String ln = lastName.getText().toString();
        final String em = email.getText().toString();
        final String pw = password.getText().toString();
        final String db = dob.getMonth() + "-" + dob.getDayOfMonth() + "-" + dob.getYear();
        final String c = city.getText().toString();

        String userTags = tags.getText().toString();
        final String sk = userTags.replaceAll(" ", "");

        new AsyncTask<String, Void, String>() {
            private int response;

            protected String doInBackground(String... urls) {
                response = Profile.createProfile(fn, ln, em, db, sk, c, pw);
                return null;
            }

            protected void onPostExecute(String result) {
                if (response == -1) {
                    alertCreateAccountSuccess();
                } else if (response == Error.ERROR_EMAIL_IN_USE) {
                    alertEmailInUse();
                } else if (response == Error.ERROR_SERVER_COMMUNICATION) {
                    alertErrorServer();
                }
            }
        }.execute();


    }

    private void alertPasswordMismatch() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
        builder.setMessage(R.string.ad_password_mismatch);
        builder.setTitle(R.string.ad_password_mismatch_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                password.setText("");
                passwordRetry.setText("");
                password.setHighlightColor(0xffff0000);
            }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void alertCreateAccountSuccess() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
        builder.setMessage(R.string.ad_create_account_success);
        builder.setTitle(R.string.ad_create_account_success_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                finish();
            }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void alertEmailInUse() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
        builder.setMessage(R.string.ad_email_in_use);
        builder.setTitle(R.string.ad_email_in_use_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                password.setText("");
                passwordRetry.setText("");
                email.setText("");
            }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void alertErrorServer() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
        builder.setMessage(R.string.ad_error_server_comm);
        builder.setTitle(R.string.ad_error_server_comm_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
            }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void alertNeedsTags() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
        builder.setMessage(R.string.ad_error_need_tags);
        builder.setTitle(R.string.ad_error_need_tags_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
            }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
