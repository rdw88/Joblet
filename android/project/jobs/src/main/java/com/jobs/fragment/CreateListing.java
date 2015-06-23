package com.jobs.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.activity.ViewListing;
import com.jobs.backend.Error;
import com.jobs.backend.Listing;
import com.jobs.backend.Resource;
import com.nhaarman.listviewanimations.itemmanipulation.TouchEventHandler;
import com.rey.material.widget.RippleManager;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import net.qiujuer.genius.Genius;
import net.qiujuer.genius.animation.TouchEffect;
import net.qiujuer.genius.animation.TouchEffectAnimator;
import net.qiujuer.genius.widget.GeniusButton;
import net.qiujuer.genius.widget.GeniusCheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateListing extends Fragment implements
    TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener
{

    private String userData;
    private EditText jobTitle, startingAmount, minRep, city, address, state, jobDescription;
    private TextView tag, textGallery, textCamera;
    private Button create, listingEndTime, listingEndDate;
    private Button settags, uploadPicture, cameraBtn;
    private String tagSelected, imagePath;
    private ProgressDialog creatingProgress;
    private ImageView expirationtimecancel, expirationdatecancel, tagcancel;
    private TextView t, setdate, settime, tag1, tag2, tag3, createdescription;
    private int setYear, setMonth, setDay;
    private int setHours, setMinutes;
    private boolean dateSet, eDCVisible, eTCVisible, tCVisible;
    private boolean tag1Visible, tag2Visible, tag3Visiblie;
    private ArrayList<String> tagArray;

    public void onStart() {
        super.onStart();
        Typeface robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        Typeface robotoThin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
        dateSet = false;
        eTCVisible = false;
        eDCVisible = false;
        tCVisible = false;
        tag1Visible = false;
        tag2Visible = false;
        tag3Visiblie = false;
        tagArray = new ArrayList();
        createdescription = (TextView) getActivity().findViewById(R.id.pagedescription);
        createdescription.setTypeface(robotoThin);
        jobTitle = (EditText) getActivity().findViewById(R.id.listing_name);
        startingAmount = (EditText) getActivity().findViewById(R.id.starting_amount);
        minRep = (EditText) getActivity().findViewById(R.id.min_reputation);
        address = (EditText) getActivity().findViewById(R.id.listing_location_address);
        city = (EditText) getActivity().findViewById(R.id.listing_location_city);
        state = (EditText) getActivity().findViewById(R.id.listing_location_state);
        //activeTime = (EditText) getActivity().findViewById(R.id.active_time);
        listingEndDate = (Button) getActivity().findViewById(R.id.listing_enddate);
        listingEndDate.setTypeface(robotoMedium);
        listingEndTime = (Button) getActivity().findViewById(R.id.listing_endtime);
        listingEndTime.setTypeface(robotoMedium);
        tag = (TextView) getActivity().findViewById(R.id.text_tag_createListing);
        tag.setTypeface(robotoRegular);
        create = (Button) getActivity().findViewById(R.id.button_createListing_postListing);
        create.setTypeface(robotoMedium);
        uploadPicture = (Button) getActivity().findViewById(R.id.button_createListing_gallery);
        uploadPicture.setTypeface(robotoMedium);
        cameraBtn = (Button) getActivity().findViewById(R.id.button_createListing_camera);
        cameraBtn.setTypeface(robotoMedium);
        jobDescription = (EditText) getActivity().findViewById(R.id.listing_description);
        settime = (TextView) getActivity().findViewById(R.id.settime);
        setdate = (TextView) getActivity().findViewById(R.id.setdate);
        settime.setTypeface(robotoRegular);
        expirationdatecancel = (ImageView) getActivity().findViewById(R.id.expirationdatecancel);
        expirationtimecancel = (ImageView) getActivity().findViewById(R.id.expirationtimecancel);
        tagcancel = (ImageView) getActivity().findViewById(R.id.tagcancel);
        tagcancel.setVisibility(View.INVISIBLE);
        expirationdatecancel.setVisibility(View.INVISIBLE);
        expirationtimecancel.setVisibility(View.INVISIBLE);
        tag1 = (TextView) getActivity().findViewById(R.id.tag1);
        tag1.setTypeface(robotoThin);
        tag1.setVisibility(View.INVISIBLE);
        tag2 = (TextView) getActivity().findViewById(R.id.tag2);
        tag2.setTypeface(robotoThin);
        tag2.setVisibility(View.INVISIBLE);
        tag3 = (TextView) getActivity().findViewById(R.id.tag3);
        tag3.setTypeface(robotoThin);
        tag3.setVisibility(View.INVISIBLE);
        settags = (Button) getActivity().findViewById(R.id.set_tag);
        settags.setTypeface(robotoMedium);
        listingEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                    CreateListing.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
        }});
        listingEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                    CreateListing.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false
              );
                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
          }
      });


        uploadPicture.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0xab);
            }
        });

        Button setTag = (Button) getActivity().findViewById(R.id.set_tag);

        setTag.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                final View view = getActivity().getLayoutInflater().inflate(R.layout.tag_selector, null);
                ListView listView = (ListView) view.findViewById(R.id.tag_list);

                FilterAdapter adapter = new FilterAdapter(getActivity(), Resource.TAGS);
                listView.setAdapter(adapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.ad_select_tag);
                builder.setView(view);
                final AlertDialog dialog = builder.show();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String s = Resource.TAGS.get(position);
                        tagSelected = s;
                        dialog.dismiss();
                        if(tagArray.size() < 3) {
                            tagArray.add(Resource.TAGS.get(position));
                            tagcancel.setVisibility(View.VISIBLE);
                        }
                        for(int i = 0; i < tagArray.size() ; i++){
                            if(tagArray.size() == 3){
                                tag1.setText(tagArray.get(0));
                                tag2.setText(tagArray.get(1));
                                tag3.setText(tagArray.get(2));
                                tCVisible = true;
                                tag1.setVisibility(View.VISIBLE);
                                tag2.setVisibility(View.VISIBLE);
                                tag3.setVisibility(View.VISIBLE);
                            }
                            if(tagArray.size() == 2){
                                tag1.setText(tagArray.get(0));
                                tag2.setText(tagArray.get(1));
                                tCVisible = true;
                                tag1.setVisibility(View.VISIBLE);
                                tag2.setVisibility(View.VISIBLE);
                            }
                            if(tagArray.size() == 1){
                                tag1.setText(tagArray.get(0));
                                tCVisible = true;
                                tag1.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        });

        expirationdatecancel.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent me) {
                if(eDCVisible) {
                    setdate.setText("- - / - - / - -");
                    setYear = -1;
                    setDay = -1;
                    setMonth = -1;
                    expirationdatecancel.setVisibility(View.INVISIBLE);
                    eDCVisible = false;
                    return true;
                }
                else{
                    return false;
                }
            }
        });
        expirationtimecancel.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent me) {
                if(eTCVisible) {
                    settime.setText("- - : - -");
                    setMinutes = -1;
                    setHours = -1;
                    expirationtimecancel.setVisibility(View.INVISIBLE);
                    eTCVisible = false;
                    return true;
                }
                else{
                    return false;
                }
            }
        });
        tagcancel.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent me){
                if(tCVisible){
                    tag1.setVisibility(View.INVISIBLE);
                    tag1.setText("-");
                    tag1Visible = false;
                    tag2.setVisibility(View.INVISIBLE);
                    tag2.setText("-");
                    tag2Visible = false;
                    tag3.setVisibility(View.INVISIBLE);
                    tag3.setText("-");
                    tag3Visiblie = false;
                    tagArray.clear();
                    tCVisible = false;
                    tagcancel.setVisibility(View.INVISIBLE);
                    return true;
                }
                else{
                    return false;
                }
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        setYear = year;
        setMonth = monthOfYear;
        setDay = dayOfMonth;
        Calendar cal = Calendar.getInstance();
        Date curDate = new Date();
        cal.setTime(curDate);
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH);
        int curDay = cal.get(Calendar.DAY_OF_MONTH);
        if((setYear >= curYear) && (setMonth >= curMonth)
                && (setDay >= curDay)) {
            dateSet = true;
            setdate.setText("" + setDay + "/" + setMonth + "/" + setYear);
            expirationdatecancel.setVisibility(View.VISIBLE);
            eDCVisible = true;
        }
        else{
            dateSet = false;
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        setHours = hourOfDay;
        setMinutes = minute;
        boolean isPM = false;
        Calendar cal = Calendar.getInstance();
        Date curDate = new Date();
        cal.setTime(curDate);
        int curHours = cal.get(Calendar.HOUR_OF_DAY);
        int curMinutes = cal.get(Calendar.MINUTE);
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH);
        int curDay = cal.get(Calendar.DAY_OF_MONTH);
        if(setHours >= 12){
            isPM = true;
        }
        String amorpm = null;
        if(isPM){
            amorpm = new String("PM");
            if(setHours != 12){
                setHours = setHours - 12;
            }
        }
        else{
            amorpm = new String("AM");
        }
        if(dateSet) {
            if ((curMonth == setMonth) && (curDay == setDay) && (curYear == setYear)) {
                if (setHours >= curHours && setMinutes >= curMinutes) {
                    expirationtimecancel.setVisibility(View.VISIBLE);
                    eTCVisible = true;
                    settime.setText("" + setHours + ":" + setMinutes + " " + amorpm);
                }
            }
            else {
                settime.setText("" + setHours + ":" + setMinutes + " " + amorpm);
                expirationtimecancel.setVisibility(View.VISIBLE);
                eTCVisible = true;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0xab:
                if (resultCode == Activity.RESULT_OK) {
                    imagePath = Resource.getRealPathFromURI(getActivity(), data.getData());
                }

                break;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userData = getArguments().getString("data");
        View rootView = inflater.inflate(R.layout.create_listing, container, false);

        return rootView;
    }

    private void createListing(final String password) {
        creatingProgress = ProgressDialog.show(getActivity(), "Creating Listing", "Please wait while we create your listing...", true, false);

        new AsyncTask<String, Void, String>() {
            private int response;
            private String listingID;

            protected String doInBackground(String... urls) {
                Geocoder geo = new Geocoder(getActivity());
                String loc = Resource.formatLocation(address.getText().toString(), city.getText().toString(), state.getText().toString());
                double latitude = 0;
                double longitude = 0;

                try {
                    List<Address> addr = geo.getFromLocationName(loc, 1);
                    if (addr == null || addr.isEmpty()) {
                        response = -2;
                        return null;
                    }

                    latitude = addr.get(0).getLatitude();
                    longitude = addr.get(0).getLongitude();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String title = jobTitle.getText().toString();
                String sa = startingAmount.getText().toString();
                String mr = minRep.getText().toString();
                String cityStr = city.getText().toString();
                String addrStr = address.getText().toString();
                String stateStr = state.getText().toString();
                //String at = activeTime.getText().toString();
                String at = "48"; // TODO: NEED TO IMPLEMENT
                String t = tagSelected;
                String profileID = null;
                String description = jobDescription.getText().toString();

                try {
                    JSONObject obj = new JSONObject(userData);
                    profileID = obj.getString("profile_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject obj = Listing.create(title, sa, mr, at, addrStr, cityStr, stateStr, latitude, longitude, profileID, t, password, description);

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
                    creatingProgress.dismiss();
                    alertErrorServer();
                } else if (response == Error.ERROR_INCORRECT_PASSWORD) {
                    creatingProgress.dismiss();
                    alertIncorrectPassword();
                } else if (response == Error.ERROR_LOCATION_NOT_FOUND) {
                    creatingProgress.dismiss();
                    alertLocationNotFound();
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

    private class FilterAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public FilterAdapter(Context context, ArrayList<String> items) {
            super(context, R.layout.listing_tags_list_item, items);
            this.items = items;
        }

        public View getView(final int position, View currentView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.listing_tags_list_item, parent, false);

            TextView tag = (TextView) row.findViewById(R.id.tag);
            tag.setText(items.get(position));
            return row;
        }

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

    private void alertMissingInformation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.ad_missing_information);
        builder.setTitle(R.string.ad_missing_information_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
            }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /** Calculates the center X and center Y coord of a view and
     * returns an array which has the centerx as the first element and
     * centery as the second element.
     * @param v The view whose center coords will be calculated and returned
     * @return An int array in which the first position is the centerx and the
     * second index is the centery
     */
    private int[] calcCenter(View v){
        int cx = (v.getLeft() + v.getRight()) / 2;
        int cy = (v.getTop() + v.getBottom()) / 2;
        int[] toReturn = {cx, cy};
        return toReturn;
    }

    /** Calculate the final radius for a view and returns that as an int.
     * @param v The view whose final radius will be calculated for.
     * @return An int that is the final radius for the view v.
     */
    private int calcFinalRadius(View v){
        return Math.max(v.getWidth(), v.getHeight());
    }

    private void alertLocationNotFound() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.ad_location_not_found);
        builder.setTitle(R.string.ad_location_not_found_title);

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
