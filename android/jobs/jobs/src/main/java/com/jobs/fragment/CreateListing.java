package com.jobs.fragment;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.jobs.R;
import com.jobs.activity.ListingTags;
import com.jobs.activity.Main;
import com.jobs.backend.*;
import com.jobs.backend.Error;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CreateListing extends Fragment {
    private String userData;
    private EditText jobTitle, startingAmount, minRep, jobLocation, activeTime;
    private TextView tag;

    public void onStart() {
        super.onStart();

        jobTitle = (EditText) getActivity().findViewById(R.id.listing_name);
        startingAmount = (EditText) getActivity().findViewById(R.id.starting_amount);
        minRep = (EditText) getActivity().findViewById(R.id.min_reputation);
<<<<<<< HEAD
        jobLocation = (EditText) getActivity().findViewById(R.id.listing_location);
        //activeTime = (EditText) getActivity().findViewById(R.id.active_time);
        tag = (EditText) getActivity().findViewById(R.id.listing_tags);
        Button create = (Button) getActivity().findViewById(R.id.button_createListing_postListing);
        Button pickDate = (Button) getActivity().findViewById(R.id.button_createListing_pickDate);
        pickDate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
=======
        jobLocation = (EditText) getActivity().findViewById(R.id.job_location);
        activeTime = (EditText) getActivity().findViewById(R.id.active_time);
        tag = (TextView) getActivity().findViewById(R.id.listing_tag);
        Button create = (Button) getActivity().findViewById(R.id.button_createlisting);
        Button setTag = (Button) getActivity().findViewById(R.id.set_tag);

        setTag.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListingTags.class);
                startActivityForResult(intent, 0xf1);
>>>>>>> 1c117e7b7725e889fce580f410254744e8b4c786
            }
        });

        create.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //final ProgressDialog dialog = ProgressDialog.show(getActivity().getApplicationContext(), "Creating Listing", "Creating listing, please wait...", true);
                if (!tag.getText().toString().contains("Tag:")) {
                    alertNeedsTag();
                    return;
                }

                new AsyncTask<String, Void, String>() {
                    private int response;

                    protected String doInBackground(String... urls) {
                        String title = jobTitle.getText().toString();
                        String sa = startingAmount.getText().toString();
                        String mr = minRep.getText().toString();
                        String jl = jobLocation.getText().toString();
                        String at = activeTime.getText().toString();
                        String t = tag.getText().toString().substring(5);
                        String profileID = null;

                        try {
                            JSONObject obj = new JSONObject(userData);
                            profileID = obj.getString("profile_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        response = Listing.create(title, sa, mr, jl, at, profileID, t);
                        return null;
                    }

                    protected void onPostExecute(String result) {
                       // dialog.dismiss();

                        if (response == -1) {
                            alertCreateListingSuccess();
                        } else if (response == Error.ERROR_SERVER_COMMUNICATION) {
                            alertErrorServer();
                        }
                    }
                }.execute();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0xf1:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle b = data.getExtras();
                    String s = b.getString("tag");
                    tag.setText("Tag: " + s);
                }

                break;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userData = getArguments().getString("data");
        return inflater.inflate(R.layout.create_listing, container, false);
    }

    private void alertCreateListingSuccess() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.ad_create_listing_success);
        builder.setTitle(R.string.ad_create_listing_success_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
            }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void alertErrorServer() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void alertNeedsTag() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.ad_error_needs_listing_tag);
        builder.setTitle(R.string.ad_error_needs_listing_tag_title);

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
