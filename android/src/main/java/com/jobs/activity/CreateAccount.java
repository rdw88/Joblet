package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Parcelable;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.jobs.R;
import com.jobs.backend.Profile;
import com.jobs.backend.Error;
import com.jobs.backend.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CreateAccount extends FragmentActivity {
    private static final int NUM_PAGES = 1;
    private Typeface robotoRegular, robotoMedium;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        setContentView(R.layout.create_account);

        HashMap<String, String> map = new HashMap<>();
        Bundle b = new Bundle();
        b.putSerializable("account", map);
        EmailFragment email = new EmailFragment();
        email.setArguments(b);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, email).commit();
    }

    @SuppressWarnings("unchecked")
    public static class EmailFragment extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.create_account_email, container, false);
            Button next = (Button) v.findViewById(R.id.next);
            Button previous = (Button) v.findViewById(R.id.previous);
            final EditText email = (EditText) v.findViewById(R.id.email);

            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    NameFragment name = new NameFragment();
                    HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
                    map.put("email", email.getText().toString());
                    Bundle b = new Bundle();
                    b.putSerializable("account", map);
                    name.setArguments(b);
                    FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment, name).commit();
                }
            });

            return v;
        }
    }

    @SuppressWarnings("unchecked")
    public static class NameFragment extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_name, container, false);
            Button next = (Button) v.findViewById(R.id.next);
            Button previous = (Button) v.findViewById(R.id.previous);
            final EditText firstName = (EditText) v.findViewById(R.id.first_name);
            final EditText lastName = (EditText) v.findViewById(R.id.last_name);

            previous.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    EmailFragment email = new EmailFragment();
                    HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
                    map.put("first_name", firstName.getText().toString());
                    map.put("last_name", lastName.getText().toString());
                    Bundle b = new Bundle();
                    b.putSerializable("account", map);
                    email.setArguments(b);
                    FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment, email).commit();
                }
            });

            return v;
        }
    }

    private void alertImageUploadFailed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
        builder.setMessage(R.string.ad_create_account_image_upload_failed);
        builder.setTitle(R.string.ad_create_account_image_upload_failed_title);

        builder.setPositiveButton(R.string.button_ok, null);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void alertPasswordMismatch() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
        builder.setMessage(R.string.ad_password_mismatch);
        builder.setTitle(R.string.ad_password_mismatch_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
              //  password.setText("");
               // passwordRetry.setText("");
               // password.setHighlightColor(0xffff0000);
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
                //password.setText("");
                //passwordRetry.setText("");
                //email.setText("");
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
