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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
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
import com.jobs.backend.Resource;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreateAccount extends FragmentActivity {
    private static final String FRAGMENT_INDEX_KEY = "fragment_index";
    private static final int ACTION_TAKE_PICTURE = 1;
    private static final int ACTION_SELECT_PICTURE = 2;
    private static final int NUM_PAGES = 1;
    private Typeface robotoRegular, robotoMedium;

    private static final Fragment[] FRAGMENT_ORDER = {new EmailFragment(), new PasswordFragment(), new NameFragment(), new CityFragment(), new AgeFragment(), new BioFragment(),
                                                            new ProfilePictureFragment(), new TagsFragment()};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
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

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.create_account_email, container, false);
            Button next = (Button) v.findViewById(R.id.next);
            email = (EditText) v.findViewById(R.id.email);

            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.nextFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return v;
        }

        private HashMap<String, String> updateAccount() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
            map.put("email", email.getText().toString());
            return map;
        }
    }

    @SuppressWarnings("unchecked")
    public static class PasswordFragment extends Fragment {
        private EditText password;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_password, container, false);
            Button next = (Button) v.findViewById(R.id.next);
            Button previous = (Button) v.findViewById(R.id.previous);
            password = (EditText) v.findViewById(R.id.password);
            final EditText confirm = (EditText) v.findViewById(R.id.password2);

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
                        CreateAccount.alertPasswordMismatch(getActivity());
                }
            });

            return v;
        }

        private HashMap<String, String> updateAccount() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
            map.put("password", password.getText().toString());
            return map;
        }
    }

    @SuppressWarnings("unchecked")
    public static class NameFragment extends Fragment {
        private EditText firstName, lastName;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_name, container, false);
            Button next = (Button) v.findViewById(R.id.next);
            Button previous = (Button) v.findViewById(R.id.previous);
            firstName = (EditText) v.findViewById(R.id.first_name);
            lastName = (EditText) v.findViewById(R.id.last_name);

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
    }

    @SuppressWarnings("unchecked")
    public static class CityFragment extends Fragment {
        private EditText city;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_city, container, false);
            Button next = (Button) v.findViewById(R.id.next);
            Button previous = (Button) v.findViewById(R.id.previous);
            city = (EditText) v.findViewById(R.id.city);

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
            map.put("city", city.getText().toString());
            return map;
        }
    }

    @SuppressWarnings("unchecked")
    public static class AgeFragment extends Fragment {
        private EditText age;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_age, container, false);
            Button next = (Button) v.findViewById(R.id.next);
            Button previous = (Button) v.findViewById(R.id.previous);
            age = (EditText) v.findViewById(R.id.age);

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
    }

    @SuppressWarnings("unchecked")
    public static class BioFragment extends Fragment {
        private EditText bio;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_bio, container, false);
            Button next = (Button) v.findViewById(R.id.next);
            Button previous = (Button) v.findViewById(R.id.previous);
            bio = (EditText) v.findViewById(R.id.bio);

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
            Button next = (Button) v.findViewById(R.id.next);
            Button previous = (Button) v.findViewById(R.id.previous);
            ImageButton gallery = (ImageButton) v.findViewById(R.id.gallery);
            ImageButton camera = (ImageButton) v.findViewById(R.id.camera);
            preview = (ImageView) v.findViewById(R.id.preview);

            if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                camera.setVisibility(View.GONE);
            }

            gallery.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, CreateAccount.ACTION_SELECT_PICTURE);
                }
            });

            camera.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + "_";
                    File imageFile = null;

                    try {
                        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
                        profilePicturePath = imageFile.getAbsolutePath();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                        if (imageFile != null) {
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                            startActivityForResult(takePicture, CreateAccount.ACTION_TAKE_PICTURE);
                        }
                    }
                }
            });

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

    @SuppressWarnings("unchecked")
    public static class TagsFragment extends Fragment {
        private static final int MAX_TAGS = 3;
        private TextView[] tagViews = new TextView[MAX_TAGS];
        private int[] tagIDs = {R.id.tag1, R.id.tag2, R.id.tag3};
        private static final ArrayList<String> SELECTED_TAGS = new ArrayList<>();

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.create_account_tags, container, false);
            Button next = (Button) v.findViewById(R.id.next);
            Button previous = (Button) v.findViewById(R.id.previous);
            ImageButton selectTags = (ImageButton) v.findViewById(R.id.selectTags);

            for (int i = 0; i < tagViews.length; i++) {
                tagViews[i] = (TextView) v.findViewById(tagIDs[i]);
            }

            selectTags.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final View view = getActivity().getLayoutInflater().inflate(R.layout.tag_selector, null);
                    ListView listView = (ListView) view.findViewById(R.id.tag_list);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            CheckBox box = (CheckBox) view.findViewById(R.id.tag_checkbox);
                            box.setChecked(!box.isChecked());

                            if (box.isChecked())
                                SELECTED_TAGS.add(Resource.TAGS.get(position));
                            else
                                SELECTED_TAGS.remove(Resource.TAGS.get(position));
                        }
                    });

                    TagSelectorAdapter adapter = new TagSelectorAdapter(getActivity(), Resource.TAGS);
                    listView.setAdapter(adapter);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setPositiveButton(R.string.done, new AlertDialog.OnClickListener(){
                        public void onClick(DialogInterface di, int k) {
                            if (SELECTED_TAGS.size() > MAX_TAGS) {
                                CreateAccount.alertTooManyTags(getActivity());
                                return;
                            }

                            for (int i = 0; i < tagViews.length; i++) {
                                if (i >= SELECTED_TAGS.size()) {
                                    tagViews[i].setText("");
                                    tagViews[i].setVisibility(View.GONE);
                                    continue;
                                }

                                tagViews[i].setVisibility(View.VISIBLE);
                                tagViews[i].setText(SELECTED_TAGS.get(i));
                            }
                        }
                    });

                    builder.setTitle(R.string.ad_filter_title);
                    builder.setView(view);
                    builder.show();
                }
            });

            previous.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    CreateAccount.previousFragment(getActivity(), updateAccount(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            next.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    if (tagViews[0].getText().toString() == null || tagViews[0].getText().toString().equals("")) {
                        CreateAccount.alertNeedsTags(getActivity());
                        return;
                    }

                    CreateAccount.createAccount(updateAccount());
                }
            });

            return v;
        }

        private class TagSelectorAdapter extends ArrayAdapter<String> {
            private ArrayList<String> items;
            private Activity parent;

            public TagSelectorAdapter(Activity context, ArrayList<String> items) {
                super(context, R.layout.tag_list_item, items);
                this.items = items;
                this.parent = context;
            }

            public View getView(final int position, View currentView, ViewGroup parent) {
                LayoutInflater inflater = this.parent.getLayoutInflater();
                View row = inflater.inflate(R.layout.tag_list_item, parent, false);

                TextView tag = (TextView) row.findViewById(R.id.tag_name);
                tag.setText(items.get(position));

                final CheckBox box = (CheckBox) row.findViewById(R.id.tag_checkbox);
                if (SELECTED_TAGS.contains(items.get(position))) {
                    box.setChecked(true);
                }

                box.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        if (box.isChecked()) {
                            SELECTED_TAGS.add(items.get(position));
                        } else {
                            SELECTED_TAGS.remove(items.get(position));
                        }
                    }
                });

                return row;
            }
        }

        private HashMap<String, String> updateAccount() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("account");
            JSONArray array = new JSONArray();

            for (int i = 0; i < tagViews.length; i++) {
                String text = tagViews[i].getText().toString();

                if (text != null && !text.equals("")) {
                    array.put(text);
                }
            }

            map.put("tags", array.toString());
            return map;
        }
    }



    private static void createAccount(HashMap<String, String> account) {

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
