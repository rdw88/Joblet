package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.jobs.backend.Listing;
import com.jobs.backend.Profile;
import com.jobs.backend.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Jondar on 1/3/2015.
 */
public class EditProfile extends Activity {
    private EditText firstName, lastName, email, password, passwordRetry, dob;
    private AutoCompleteTextView city;
    private TextView textFirstName, textLastName, textEmail, textPassword, textCity, textBio, textCurrentTagslabel,
            tags, t, bio;
    private Button gallery, camera, addTags, create;
    private Typeface robotoRegular, robotoMedium;
    private JSONObject data;
    private ImageView picture;
    private Calendar date;

    private final ArrayList<String> locations = Resource.LOCATIONS;
    private final ArrayList<String> selectedTags = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_account);

        t = (TextView) findViewById(R.id.text_editprofile);
        t.setTypeface(robotoRegular);
        t = (TextView) findViewById(R.id.tags);
        t.setTypeface(robotoRegular);
    }


    protected void onStart() {
        super.onStart();
        textFirstName = (TextView) findViewById(R.id.text_first_name);
        textFirstName.setTypeface(robotoMedium);
        firstName = (EditText) findViewById(R.id.first_name);
        firstName.setTypeface(robotoRegular);
        textLastName = (TextView) findViewById(R.id.text_last_name);
        textLastName.setTypeface(robotoMedium);
        lastName = (EditText) findViewById(R.id.last_name);
        lastName.setTypeface(robotoRegular);
        textBio = (TextView) findViewById(R.id.text_bio);
        textBio.setTypeface(robotoMedium);
        bio = (EditText) findViewById(R.id.bio);
        bio.setTypeface(robotoRegular);
        textEmail = (TextView) findViewById(R.id.text_email);
        textEmail.setTypeface(robotoMedium);
        email = (EditText) findViewById(R.id.email);
        email.setTypeface(robotoRegular);
        textPassword = (TextView) findViewById(R.id.text_password);
        textPassword.setTypeface(robotoMedium);
        password = (EditText) findViewById(R.id.password);
        password.setTypeface(robotoRegular);
        passwordRetry = (EditText) findViewById(R.id.password2);
        passwordRetry.setTypeface(robotoRegular);
        textCity = (TextView) findViewById(R.id.text_city);
        textCity.setTypeface(robotoMedium);
        city = (AutoCompleteTextView) findViewById(R.id.city);
        city.setTypeface(robotoRegular);

        picture = (ImageView) findViewById(R.id.profile_picture);

        gallery = (Button) findViewById(R.id.button_gallery);
        camera = (Button) findViewById(R.id.button_camera);

        dob = (EditText) findViewById(R.id.dob);
        date = Calendar.getInstance();


        tags = (TextView) findViewById(R.id.tags);
        tags.setTypeface(robotoRegular);

        addTags = (Button) findViewById(R.id.add_tags);
        addTags.setTypeface(robotoRegular);
        create = (Button) findViewById(R.id.button_create);
        create.setTypeface(robotoMedium);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, locations);
        city.setAdapter(adapter);

        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, monthOfYear);
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dob.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                dob.setTypeface(robotoRegular);
            }
        };

        dob.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                new DatePickerDialog(EditProfile.this, dateListener, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        addTags.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                final View view = getLayoutInflater().inflate(R.layout.tag_selector, null);
                ListView listView = (ListView) view.findViewById(R.id.tag_list);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckBox box = (CheckBox) view.findViewById(R.id.tag_checkbox);
                        box.setChecked(!box.isChecked());

                        if (box.isChecked())
                            selectedTags.add(Resource.TAGS.get(position));
                        else
                            selectedTags.remove(Resource.TAGS.get(position));
                    }
                });

                TagSelectorAdapter adapter = new TagSelectorAdapter(EditProfile.this, Resource.TAGS);
                listView.setAdapter(adapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);

                builder.setPositiveButton(R.string.done, new AlertDialog.OnClickListener(){
                    public void onClick(DialogInterface di, int k){
                        if (selectedTags.size() == 0)
                            return;

                        String str = "";

                        for (int i = 0; i < selectedTags.size() - 1; i++) {
                            str += selectedTags.get(i) + ", ";
                        }

                        str += selectedTags.get(selectedTags.size() - 1);
                        tags.setText(str);
                    }
                });

                builder.setTitle(R.string.ad_filter_title);
                builder.setView(view);
                builder.show();
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
                    // put editProfile() in with all of the required info sent to the server
                }
            }
        });
    }
    private class TagSelectorAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public TagSelectorAdapter(Context context, ArrayList<String> items) {
            super(context, R.layout.tag_list_item, items);
            this.items = items;
        }

        public View getView(final int position, View currentView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.tag_list_item, parent, false);

            TextView tag = (TextView) row.findViewById(R.id.tag_name);
            tag.setText(items.get(position));

            final CheckBox box = (CheckBox) row.findViewById(R.id.tag_checkbox);
            if (selectedTags.contains(items.get(position))) {
                box.setChecked(true);
            }

            box.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if (box.isChecked()) {
                        selectedTags.add(items.get(position));
                    } else {
                        selectedTags.remove(items.get(position));
                    }
                }
            });

            return row;
        }
    }

    private void alertPasswordMismatch() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
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

    private void alertNeedsTags() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
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
