package com.jobs.fragment;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.jobs.R;
import com.jobs.activity.ListingTags;
import com.jobs.activity.Main;
import com.jobs.activity.ViewListing;
import com.jobs.backend.*;
import com.jobs.backend.Error;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class CreateListing extends Fragment {
    private String userData;
    private EditText jobTitle, startingAmount, minRep, jobLocation, activeTime;
    private TextView tag;
    private Button uploadPicture, create;
    private ImageView preview;
    private Bitmap previewBitmap;

    private String tagSelected, imagePath;
    private ProgressDialog creatingProgress;
    TextView t;

    public void onStart() {
        super.onStart();

        jobTitle = (EditText) getActivity().findViewById(R.id.listing_name);
        startingAmount = (EditText) getActivity().findViewById(R.id.starting_amount);
        minRep = (EditText) getActivity().findViewById(R.id.min_reputation);
        jobLocation = (EditText) getActivity().findViewById(R.id.listing_location);
        //activeTime = (EditText) getActivity().findViewById(R.id.active_time);
        tag = (TextView) getActivity().findViewById(R.id.text_tag_createListing);
        Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/verdana.ttf");
        create = (Button) getActivity().findViewById(R.id.button_createListing_postListing);
        create.setTypeface(customFont);
        Button pickDate = (Button) getActivity().findViewById(R.id.button_createListing_pickDate);
        uploadPicture = (Button) getActivity().findViewById(R.id.button_createListing_uploadPicture);
        preview = (ImageView) getActivity().findViewById(R.id.uploaded_picture);

        t = (TextView) getActivity().findViewById(R.id.text_jobName_createListing);
        t.setTypeface(customFont);

        t = (TextView) getActivity().findViewById(R.id.text_tag_createListing);
        t.setTypeface(customFont);

        t = (TextView) getActivity().findViewById(R.id.text_picturesSelected_createListing);
        t.setTypeface(customFont);

        uploadPicture.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0xab);
            }
        });

        pickDate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        Button setTag = (Button) getActivity().findViewById(R.id.set_tag);

        setTag.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListingTags.class);
                startActivityForResult(intent, 0xf1);
            }
        });

        create.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (tagSelected == null) {
                    alertNeedsTag();
                    return;
                }

                final View view = getActivity().getLayoutInflater().inflate(R.layout.confirm_password, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.ad_confirm_password_title);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.confirm, new AlertDialog.OnClickListener(){
                    public void onClick(DialogInterface di, int i){
                        EditText password = (EditText) view.findViewById(R.id.confirmed_password);
                        createListing(password.getText().toString());
                    }
                });
                builder.setView(view);
                builder.show();
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
                    tagSelected = s;
                }

                break;

            case 0xab:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    imagePath = getRealPathFromURI(selectedImage);

                    try {
                        InputStream is = getActivity().getContentResolver().openInputStream(selectedImage);
                        previewBitmap = BitmapFactory.decodeStream(is);
                        preview.setImageBitmap(previewBitmap);
                        preview.setVisibility(ImageView.VISIBLE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userData = getArguments().getString("data");
        return inflater.inflate(R.layout.create_listing, container, false);
    }

    private void createListing(final String password) {
        creatingProgress = ProgressDialog.show(getActivity(), "Creating Listing", "Please wait while we create your listing...", true, false);

        new AsyncTask<String, Void, String>() {
            private int response;
            private String listingID;

            protected String doInBackground(String... urls) {
                String title = jobTitle.getText().toString();
                String sa = startingAmount.getText().toString();
                String mr = minRep.getText().toString();
                String jl = jobLocation.getText().toString();
                //String at = activeTime.getText().toString();
                String at = "48"; // TODO: NEED TO IMPLEMENT
                String t = tagSelected;
                String profileID = null;

                try {
                    JSONObject obj = new JSONObject(userData);
                    profileID = obj.getString("profile_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject obj = Listing.create(title, sa, mr, jl, at, profileID, t, password);

                try {
                    response = obj.getInt("error");

                    if (response == -1)
                        listingID = obj.getString("listing_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String result) {
                if (response == -1) {
                    if (imagePath != null)
                        uploadPictures(listingID, password);
                    else
                        alertCreateListingSuccess(listingID);
                } else if (response == Error.ERROR_SERVER_COMMUNICATION) {
                    alertErrorServer();
                } else if (response == Error.ERROR_INCORRECT_PASSWORD) {
                    alertIncorrectPassword();
                }
            }
        }.execute();
    }

    private void uploadPictures(final String listingID, final String password) {
        new AsyncTask<String, Void, String>() {
            private int response;

            protected String doInBackground(String... urls) {
                String email = null;
                try {
                    JSONObject obj = new JSONObject(userData);
                    email = obj.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                response = Listing.upload(imagePath, listingID, email, password);
                return null;
            }

            protected void onPostExecute(String result) {
                if (response == -1) {
                    alertCreateListingSuccess(listingID);
                }
            }
        }.execute();
    }

    private String getRealPathFromURI(Uri contentUri) {
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private void alertCreateListingSuccess(final String listingID) {
        creatingProgress.dismiss();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.ad_create_listing_success);
        builder.setTitle(R.string.ad_create_listing_success_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                Intent intent = new Intent(getActivity(), ViewListing.class);
                intent.putExtra("listing_id", listingID);
                startActivity(intent);
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

    private void alertIncorrectPassword(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.ad_confirmed_password_incorrect);
        builder.setTitle(R.string.ad_confirmed_password_incorrect_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                create.performClick();
            }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
