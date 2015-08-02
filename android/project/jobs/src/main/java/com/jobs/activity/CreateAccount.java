package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.*;
import com.jobs.backend.Error;

import net.qiujuer.genius.Genius;
import net.qiujuer.genius.widget.GeniusEditText;
import net.qiujuer.genius.widget.attribute.EditTextAttributes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CreateAccount extends FragmentActivity {
    private static final String FRAGMENT_INDEX_KEY = "fragment_index";
    private static final int ACTION_TAKE_PICTURE = 1;
    private static final int ACTION_SELECT_PICTURE = 2;
    private static final int NUM_PAGES = 1;
    private static Typeface robotoRegular, robotoMedium, robotoThin;

    private static final Fragment[] FRAGMENT_ORDER = {new EmailFragment(), new PasswordFragment(), new NameFragment(), new CityFragment(), new AgeFragment(), new BioFragment(),
                                                            new ProfilePictureFragment()};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        robotoThin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        setContentView(R.layout.create_account);

        HashMap<String, String> map = new HashMap<>();
        Bundle b = new Bundle();
        b.putSerializable("account", map);
        b.putInt(FRAGMENT_INDEX_KEY, 0);
        Fragment fragment = FRAGMENT_ORDER[0];
        fragment.setArguments(b);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment).commit();
    }

    private static void nextFragment(Activity parent, HashMap<String, String> args, int currentIndex) {
        if (currentIndex >= FRAGMENT_ORDER.length - 1)
            return;

        Fragment fragment = FRAGMENT_ORDER[currentIndex + 1];
        Bundle bundle = new Bundle();
        bundle.putSerializable("account", args);
        bundle.putInt(FRAGMENT_INDEX_KEY, currentIndex + 1);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = parent.getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment).commit();
    }

    private static void previousFragment(Activity parent, HashMap<String, String> args, int currentIndex) {
        Fragment fragment = FRAGMENT_ORDER[currentIndex - 1];
        Bundle bundle = new Bundle();
        bundle.putSerializable("account", args);
        bundle.putInt(FRAGMENT_INDEX_KEY, currentIndex - 1);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = parent.getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment).commit();
    }

    @SuppressWarnings("unchecked")
    public static class EmailFragment extends Fragment {
        private EditText email;
        private Button next;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.create_account_email, container, false);
            TextView instructionsTitle = (TextView) v.findViewById(R.id.text_emailtitle);
            instructionsTitle.setTypeface(robotoThin);
            TextView instructions = (TextView) v.findViewById(R.id.text_email);
            instructions.setTypeface(robotoMedium);
            next = (Button) v.findViewById(R.id.email_next);
            next.setTypeface(robotoMedium);
            email = (EditText) v.findViewById(R.id.email);
            email.setTypeface(robotoRegular);

            email.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    String input = email.getText().toString();

                    if (input.contains("@") && input.indexOf(".") > input.indexOf("@")) {
                        confirmEmail();
                    }

                    return false;
                }
            });

            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.nextFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return v;
        }

        private void confirmEmail() {
            new AsyncTask<String, Void, String>() {
                private boolean canBeUsed;

                protected String doInBackground(String... params) {
                    canBeUsed = Profile.confirmEmailCanBeUsed(params[0]);
                    return null;
                }

                protected void onPostExecute(String message) {

                    if (canBeUsed) {
                        email.setBackgroundColor(getResources().getColor(R.color.Treetop_Primary));
                        next.setEnabled(true);
                    } else {
                        email.setBackgroundColor(getResources().getColor(R.color.Marsala_Primary));
                        next.setEnabled(false);
                    }
                }
            }.execute(email.getText().toString());
        }

        private HashMap<String, String> updateAccount() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
            map.put("email", email.getText().toString());
            return map;
        }
    }

    @SuppressWarnings("unchecked")
    public static class PasswordFragment extends Fragment {
        private EditText password, confirm;
        private Button next;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_password, container, false);
            TextView instructionsTitle = (TextView) v.findViewById(R.id.text_passwordtitle);
            instructionsTitle.setTypeface(robotoThin);
            TextView instructions = (TextView) v.findViewById(R.id.text_password);
            instructions.setTypeface(robotoRegular);
            next = (Button) v.findViewById(R.id.pass_next);
            next.setTypeface(robotoMedium);
            Button previous = (Button) v.findViewById(R.id.pass_previous);
            previous.setTypeface(robotoMedium);
            password = (EditText) v.findViewById(R.id.password);
            password.setTypeface(robotoRegular);
            confirm = (EditText) v.findViewById(R.id.password2);
            confirm.setTypeface(robotoRegular);

            password.setOnKeyListener(new KeyListener());
            confirm.setOnKeyListener(new KeyListener());

            previous.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.previousFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    String pass = password.getText().toString();
                    String passAgain = confirm.getText().toString();

                    if (pass.equals(passAgain))
                        CreateAccount.nextFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                    else
                        CreateAccount.alertPasswordMismatch(getActivity()); // this probably should never happen, but just to be safe.
                }
            });

            return v;
        }

        private HashMap<String, String> updateAccount() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
            map.put("password", password.getText().toString());
            return map;
        }

        private class KeyListener implements View.OnKeyListener {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                next.setEnabled(false);
                String pass = password.getText().toString();
                String confirmText = confirm.getText().toString();

                if (confirmText.equals(pass) && !pass.equals("")) {
                    next.setEnabled(true);
                }

                return false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static class NameFragment extends Fragment {
        private EditText firstName, lastName;
        private Button next;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_name, container, false);
            TextView instructionsTitle = (TextView) v.findViewById(R.id.text_nametitle);
            instructionsTitle.setTypeface(robotoThin);
            TextView instructions = (TextView) v.findViewById(R.id.text_enteryourname);
            instructions.setTypeface(robotoRegular);
            next = (Button) v.findViewById(R.id.name_next);
            next.setTypeface(robotoMedium);
            Button previous = (Button) v.findViewById(R.id.name_previous);
            previous.setTypeface(robotoMedium);
            firstName = (EditText) v.findViewById(R.id.first_name);
            firstName.setTypeface(robotoRegular);
            lastName = (EditText) v.findViewById(R.id.last_name);
            lastName.setTypeface(robotoRegular);

            firstName.setOnKeyListener(new KeyListener());
            lastName.setOnKeyListener(new KeyListener());

            previous.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.previousFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.nextFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return v;
        }

        private HashMap<String, String> updateAccount() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
            map.put("first_name", firstName.getText().toString());
            map.put("last_name", lastName.getText().toString());
            return map;
        }

        private class KeyListener implements View.OnKeyListener {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (!firstName.getText().toString().equals("") && !lastName.getText().toString().equals(""))
                    next.setEnabled(true);
                else
                    next.setEnabled(false);

                return false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static class CityFragment extends Fragment {
        private EditText city, state;
        private Button next;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_city, container, false);
            TextView instructionsTitle = (TextView) v.findViewById(R.id.text_citytitle);
            instructionsTitle.setTypeface(robotoThin);
            TextView instructions = (TextView) v.findViewById(R.id.text_city);
            instructions.setTypeface(robotoRegular);
            next = (Button) v.findViewById(R.id.location_next);
            next.setTypeface(robotoMedium);
            Button previous = (Button) v.findViewById(R.id.location_previous);
            previous.setTypeface(robotoMedium);
            city = (EditText) v.findViewById(R.id.city);
            city.setTypeface(robotoRegular);
            state = (EditText) v.findViewById(R.id.state);
            state.setTypeface(robotoRegular);

            city.setOnKeyListener(new KeyListener());
            state.setOnKeyListener(new KeyListener());

            previous.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.previousFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.nextFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return v;
        }

        private HashMap<String, String> updateAccount() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
            map.put("city_code", city.getText().toString() + ", " + state.getText().toString());
            return map;
        }

        private class KeyListener implements View.OnKeyListener {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String cityName = city.getText().toString();
                String stateName = state.getText().toString();

                if (!cityName.equals("") && !stateName.equals("")) {
                    try {
                        Geocoder geo = new Geocoder(getActivity());
                        List<Address> addr = geo.getFromLocationName(cityName + ", " + stateName, 1);
                        if (addr == null || addr.isEmpty()) {
                            next.setEnabled(false);
                        } else {
                            next.setEnabled(true);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static class AgeFragment extends Fragment {
        private EditText age;
        private Button next;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_age, container, false);
            TextView instructionsTitle = (TextView) v.findViewById(R.id.text_agetitle);
            instructionsTitle.setTypeface(robotoThin);
            TextView instructions = (TextView) v.findViewById(R.id.text_age);
            instructions.setTypeface(robotoRegular);
            next = (Button) v.findViewById(R.id.age_next);
            next.setTypeface(robotoMedium);
            Button previous = (Button) v.findViewById(R.id.age_previous);
            previous.setTypeface(robotoMedium);
            age = (EditText) v.findViewById(R.id.age);
            age.setTypeface(robotoRegular);
            age.setOnKeyListener(new KeyListener());

            previous.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.previousFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.nextFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return v;
        }

        private HashMap<String, String> updateAccount() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
            map.put("age", age.getText().toString());
            return map;
        }

        private class KeyListener implements View.OnKeyListener {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (!age.getText().toString().equals("") && !next.isEnabled())
                    next.setEnabled(true);
                else
                    next.setEnabled(false);

                return false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static class BioFragment extends Fragment {
        private EditText bio;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_bio, container, false);
            TextView instructionsTitle = (TextView) v.findViewById(R.id.text_biotitle);
            instructionsTitle.setTypeface(robotoThin);
            TextView instructions = (TextView) v.findViewById(R.id.text_bio);
            instructions.setTypeface(robotoRegular);
            Button next = (Button) v.findViewById(R.id.bio_next);
            next.setTypeface(robotoMedium);
            Button previous = (Button) v.findViewById(R.id.bio_previous);
            previous.setTypeface(robotoMedium);
            bio = (EditText) v.findViewById(R.id.bio);
            bio.setTypeface(robotoRegular);

            previous.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.previousFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.nextFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return v;
        }

        private HashMap<String, String> updateAccount() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
            map.put("bio", bio.getText().toString());
            return map;
        }
    }

    @SuppressWarnings("unchecked")
    public static class ProfilePictureFragment extends Fragment {
        private String profilePicturePath;
        private ImageView preview;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_profilepicture, container, false);
            TextView instructions = (TextView) v.findViewById(R.id.text_uploadpicture);
            instructions.setTypeface(robotoRegular);
            Button next = (Button) v.findViewById(R.id.profilepic_next);
            next.setTypeface(robotoMedium);
            Button previous = (Button) v.findViewById(R.id.profilepic_previous);
            previous.setTypeface(robotoMedium);
            preview = (ImageView) v.findViewById(R.id.preview);

            if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            }


            previous.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.previousFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.nextFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return v;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case ACTION_SELECT_PICTURE:
                    if (resultCode == Activity.RESULT_OK) {
                        String path = Resource.getRealPathFromURI(getActivity(), data.getData());
                        preview.setImageBitmap(BitmapFactory.decodeFile(path));
                        profilePicturePath = path;
                    }

                    break;

                case ACTION_TAKE_PICTURE:
                    if (resultCode == Activity.RESULT_OK) {
                        //Bitmap b = BitmapFactory.decodeFile(profilePicturePath);
                        //preview.setImageBitmap(b);
                        setPic();
                    }

                    break;
            }
        }

        private void setPic() {
            int targetW = preview.getWidth();
            int targetH = preview.getHeight();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(profilePicturePath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(profilePicturePath, bmOptions);
            preview.setImageBitmap(bitmap);
        }

        private HashMap<String, String> updateAccount() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
            map.put("profile_picture", profilePicturePath);
            return map;
        }
    }


    private static void createAccount(final Activity context, final HashMap<String, String> account) {
        new AsyncTask<String, Void, String>() {
            private int response;

            protected String doInBackground(String... params) {
                try {
                    response = Profile.createProfile(account).getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(String res) {
                if (response == -1) {
                    CreateAccount.alertCreateAccountSuccess(context);
                } else if (response == com.jobs.backend.Error.ERROR_SERVER_COMMUNICATION) {
                    CreateAccount.alertErrorServer(context);
                } else if (response == com.jobs.backend.Error.ERROR_EMAIL_IN_USE) {
                    CreateAccount.alertEmailInUse(context);
                } else if (response == Error.ERROR_IMAGE_UPLOAD_FAILED) {
                    CreateAccount.alertImageUploadFailed(context);
                }
            }
        }.execute();
    }

    private static void alertImageUploadFailed(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.ad_create_account_image_upload_failed);
        builder.setTitle(R.string.ad_create_account_image_upload_failed_title);

        builder.setPositiveButton(R.string.button_ok, null);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void alertPasswordMismatch(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    private static void alertCreateAccountSuccess(final Activity context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.ad_create_account_success);
        builder.setTitle(R.string.ad_create_account_success_title);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                context.finish();
            }
        };

        builder.setPositiveButton(R.string.button_ok, listener);
        //builder.setNegativeButton(R.string.button_cancel, listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void alertEmailInUse(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    private static void alertErrorServer(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    private static void alertNeedsTags(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    private static void alertTooManyTags(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.ad_error_too_many_tags);
        builder.setTitle(R.string.ad_error_too_many_tags_title);

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
