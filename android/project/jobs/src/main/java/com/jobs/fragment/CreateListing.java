package com.jobs.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jobs.R;
import com.jobs.activity.ViewListing;
import com.jobs.backend.*;
import com.jobs.backend.Error;
import com.jobs.utility.CurrencyFormatInputFilter;
import com.jobs.utility.Global;
import com.jobs.utility.Helper;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CreateListing extends Fragment {
    private static final String FRAGMENT_INDEX_KEY = "fragment_index";
    private static final Fragment[] FRAGMENT_ORDER = {new ListingName(), new ListingPrice(), new ListingAddress(), new RecommendedSettings(),
                new OptionalSettings(), new ReviewListing()};

    private static Typeface robotoRegular, robotoMedium, robotoThin;
    private static HashMap<String, String> info;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        robotoThin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_listing_listingname, container, false);

        final EditText name = (EditText) view.findViewById(R.id.listingTitle);
        name.setTypeface(robotoRegular);
        TextView theEssentials = (TextView) view.findViewById(R.id.header);
        theEssentials.setTypeface(robotoRegular);
        TextView jobTitle = (TextView) view.findViewById(R.id.title);
        jobTitle.setTypeface(robotoRegular);
        TextView desc = (TextView) view.findViewById(R.id.description);
        desc.setTypeface(robotoRegular);
        Button next = (Button) view.findViewById(R.id.listingTitleNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(getActivity(), view);
                HashMap<String, String> args = new HashMap<>();
                args.put("listing_name", name.getText().toString());
                CreateListing.nextFragment(getActivity(), args, 0);
            }
        });

        return view;
    }

    private static void nextFragment(FragmentActivity parent, HashMap<String, String> args, int currentIndex) {
        if (currentIndex >= FRAGMENT_ORDER.length - 1)
            return;
        info = args;
        Fragment fragment = FRAGMENT_ORDER[currentIndex + 1];
        Bundle bundle = new Bundle();
        bundle.putSerializable("listing", args);
        bundle.putInt(FRAGMENT_INDEX_KEY, currentIndex + 1);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = parent.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_holder, fragment).commit();
    }

    private static void previousFragment(FragmentActivity parent, HashMap<String, String> args, int currentIndex) {
        Fragment fragment = FRAGMENT_ORDER[currentIndex - 1];
        Bundle bundle = new Bundle();
        bundle.putSerializable("listing", args);
        bundle.putInt(FRAGMENT_INDEX_KEY, currentIndex - 1);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = parent.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_holder, fragment).commit();
    }

    public static class ListingName extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.create_listing_listingname, container, false);

            final EditText name = (EditText) view.findViewById(R.id.listingTitle);
            name.setTypeface(robotoRegular);
            TextView theEssentials = (TextView) view.findViewById(R.id.header);
            theEssentials.setTypeface(robotoRegular);
            TextView instructionsTitle = (TextView) view.findViewById(R.id.title);
            instructionsTitle.setTypeface(robotoRegular);
            TextView instructionsSubTitle = (TextView) view.findViewById(R.id.description);
            instructionsSubTitle.setTypeface(robotoRegular);
            Button next = (Button) view.findViewById(R.id.listingTitleNext);
            next.setTypeface(robotoMedium);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, String> args = new HashMap<>();
                    args.put("listing_name", name.getText().toString());
                    hideKeyboard(getActivity(), view);
                    CreateListing.nextFragment(getActivity(), args, 0);
                }
            });

            return view;
        }
    }

    public static class RecommendedSettings extends Fragment {
        private String description;
        private List<String> picturePaths = new ArrayList<>();

        private ImageView image0, image1;
        private TextView more, viewDescription;
        private EditText enterDescription;
        private RelativeLayout enterDescriptionButtons;

        private Dialog selector;
        private Adapter adapter;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.create_listing_recommended, container, false);
            Button next = (Button) view.findViewById(R.id.listingDescriptionReview);
            Button prev = (Button) view.findViewById(R.id.listingDescriptionPrev);
            next.setTypeface(robotoMedium);
            prev.setTypeface(robotoMedium);

            TextView header = (TextView) view.findViewById(R.id.header);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView promptDescription = (TextView) view.findViewById(R.id.description);
            header.setTypeface(robotoRegular);
            title.setTypeface(robotoRegular);
            promptDescription.setTypeface(robotoRegular);

            ArrayList<Item> items = new ArrayList<>();
            items.add(new Item("Add Description", description == null ? "<No description>" : description));
            items.add(new Item("Add Pictures", "No pictures set."));

            final ListView list = (ListView) view.findViewById(R.id.optionalListingList);
            adapter = new Adapter(getActivity(), items);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                    if (i == 0) {
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setCancelable(true);
                        dialog.negativeAction("Cancel");
                        dialog.negativeActionTextColor(0xfff05d5e);
                        dialog.negativeActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        dialog.setContentView(R.layout.create_listing_enter_listing_description);
                        final EditText entered = (EditText) dialog.findViewById(R.id.description);

                        dialog.positiveAction("Save");
                        dialog.positiveActionTextColor(0xfff05d5e);
                        dialog.positiveActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                description = entered.getText().toString();
                                dialog.dismiss();
                                viewDescription.setText(description);
                                adapter.notifyDataSetChanged();
                            }
                        });

                        if (description != null)
                            entered.setText(description);

                        dialog.setTitle("Add Description");
                        dialog.titleColor(0xff444444);
                        dialog.show();
                    } else if (i == 1) {
                        selector = Helper.galleryOrCameraDialog(RecommendedSettings.this);
                        selector.show();
                    }
                }
            });



            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(getActivity(), view);
                    CreateListing.nextFragment(getActivity(), updateListing(),
                            getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(getActivity(), view);
                    CreateListing.previousFragment(getActivity(), updateListing(),
                            getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return view;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case Helper.RESULT_GALLERY:
                    if (resultCode == Activity.RESULT_OK) {
                        picturePaths.add(Resource.getRealPathFromURI(getActivity(), data.getData()));
                    }

                    break;

                case Helper.RESULT_CAMERA:
                    if (resultCode == Activity.RESULT_OK) {
                        picturePaths.add(Helper.lastCameraPicturePath);
                    }

                    break;
            }

            // TODO: Fix this bug. image1 not getting set, "more" TextView getting ignored.
            if (resultCode == Activity.RESULT_OK) {
                selector.dismiss();

                if (picturePaths.size() == 2) {
                    setPic(picturePaths.get(1), 1);
                } else if (picturePaths.size() == 1){
                    setPic(picturePaths.get(0), 0);
                }

                if (picturePaths.size() > 2)
                    more.setVisibility(View.VISIBLE);
            }
        }

        private void setPic(String path, int image) {
            int targetW = image0.getMeasuredWidth();
            int targetH = image0.getMeasuredHeight();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
            if (image == 0)
                image0.setImageBitmap(bitmap);
            else if (image == 1)
                image1.setImageBitmap(bitmap);
        }

        @SuppressWarnings("unchecked")
        private HashMap<String, String> updateListing() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("listing");
            map.put("listing_description", description);
            map.put("num_pictures", Integer.toString(picturePaths.size()));

            for (int i = 0; i < picturePaths.size(); i++) {
                map.put("picture_" + i, picturePaths.get(i));
            }

            return map;
        }

        private class Adapter extends ArrayAdapter<Item> {
            private ArrayList<Item> items;

            public Adapter(Context context, ArrayList<Item> items) {
                super(context, R.layout.create_listing_optional_listview_item, items);
                this.items = items;
            }

            @Override
            public View getView(final int position, View currentView, ViewGroup parent) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View row = null;

                if (position == 0) {
                    row = inflater.inflate(R.layout.create_listing_add_description_list_item, parent, false);
                    viewDescription = (TextView) row.findViewById(R.id.view_description);

                    if (description != null)
                        viewDescription.setText(description);
                    else
                        viewDescription.setText(items.get(position).description);
                } else if (position == 1) {
                    row = inflater.inflate(R.layout.create_listing_add_pictures_list_item, parent, false);

                    image0 = (ImageView) row.findViewById(R.id.add_pictures_preview_0);
                    image1 = (ImageView) row.findViewById(R.id.add_pictures_preview_1);
                    more = (TextView) row.findViewById(R.id.more);
                    TextView description = (TextView) row.findViewById(R.id.description);
                    description.setText(items.get(position).description);
                }

                TextView title = (TextView) row.findViewById(R.id.title);
                title.setText(items.get(position).title);
                title.setTypeface(robotoRegular);
                //TextView description = (TextView) row.findViewById(R.id.description);
                //description.setText(items.get(position).description);
                //description.setTypeface(robotoRegular);
                return row;
            }

            public int getCount() {
                return items.size();
            }
        }

        private class Item {
            public String title;
            public String description;

            public Item(String title, String description) {
                this.title = title;
                this.description = description;
            }
        }
    }

    public static class OptionalSettings extends Fragment {
        private int minReputation = 90;
        private boolean minRepSet;

        private int year, month, day, hour, minute;
        private String formattedDT;

        private TextView dateTimeDescription, minRepDescription;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.create_listing_optional_settings, container, false);
            Button next = (Button) view.findViewById(R.id.listingDescriptionReview);
            Button prev = (Button) view.findViewById(R.id.listingDescriptionPrev);
            next.setTypeface(robotoMedium);
            prev.setTypeface(robotoMedium);

            TextView header = (TextView) view.findViewById(R.id.header);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView description = (TextView) view.findViewById(R.id.description);
            header.setTypeface(robotoRegular);
            title.setTypeface(robotoRegular);
            description.setTypeface(robotoRegular);

            ArrayList<Item> items = new ArrayList<>();
            items.add(new Item("Add Expiration Date/Time", formattedDT == null ? "Expires never." : formattedDT));
            items.add(new Item("Add Minimum Applicant Reputation", minRepSet ? "" + minReputation : "None set."));

            ListView list = (ListView) view.findViewById(R.id.optionalListingList);
            final Adapter adapter = new Adapter(getActivity(), items);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 0) {
                        final Calendar c = Calendar.getInstance();
                        final DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.DatePickerTheme, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                year = y;
                                month = m;
                                day = d;

                                final TimePickerDialog timeDialog = new TimePickerDialog(getActivity(), R.style.DatePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int h, int m) {
                                        hour = h;
                                        minute = m;
                                        boolean isPM = false;

                                        if (h >= 12) {
                                            isPM = true;
                                        }

                                        if (h == 0)
                                            h = 12;

                                        if (h > 12) {
                                            h -= 12;
                                        }

                                        String minuteFormat = Integer.toString(m);
                                        if (m < 10)
                                            minuteFormat = "0" + minuteFormat;

                                        formattedDT = (month + 1) + "/" + day + "/" + year + " " + h + ":" + minuteFormat;

                                        if (isPM)
                                            formattedDT += "PM";
                                        else
                                            formattedDT += "AM";

                                        dateTimeDescription.setText(formattedDT);
                                        adapter.notifyDataSetChanged();
                                    }
                                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
                                timeDialog.setCancelable(true);
                                timeDialog.show();
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                        dialog.setCancelable(true);
                        dialog.show();
                    } else if (i == 1) {
                        final com.rey.material.app.Dialog dialog = new com.rey.material.app.Dialog(getActivity());
                        dialog.setTitle("Minimum Reputation");
                        dialog.titleColor(0xff444444);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.create_listing_min_reputation_picker);

                        final NumberPicker picker = (NumberPicker) dialog.findViewById(R.id.min_rep_picker);
                        picker.setMinValue(0);
                        picker.setMaxValue(100);
                        picker.setValue(10);
                        picker.setValue(minReputation);

                        dialog.positiveAction("Save");
                        dialog.positiveActionTextColor(0xfff05d5e);
                        dialog.positiveActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                minReputation = picker.getValue();
                                minRepSet = true;
                                minRepDescription.setText("" + minReputation);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });

                        dialog.negativeAction("Cancel");
                        dialog.negativeActionTextColor(0xfff05d5e);

                        dialog.negativeActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                }
            });


            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(getActivity(), view);
                    CreateListing.nextFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(getActivity(), view);
                    CreateListing.previousFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return view;
        }

        @SuppressWarnings("unchecked")
        private HashMap<String, String> updateListing() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("listing");
            map.put("listing_expiration", formattedDT);
            map.put("min_reputation", minRepSet ? "" + minReputation : null);
            return map;
        }

        private class Adapter extends ArrayAdapter<Item> {
            private ArrayList<Item> items;

            public Adapter(Context context, ArrayList<Item> items) {
                super(context, R.layout.create_listing_optional_listview_item, items);
                this.items = items;
            }

            @Override
            public View getView(final int position, View currentView, ViewGroup parent) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View row = inflater.inflate(R.layout.create_listing_optional_listview_item, parent, false);


                if (position == 0) {
                    dateTimeDescription = (TextView) row.findViewById(R.id.description);

                    if (formattedDT == null)
                        dateTimeDescription.setText(items.get(position).description);
                    else
                        dateTimeDescription.setText(formattedDT);
                } else if (position == 1) {
                    minRepDescription = (TextView) row.findViewById(R.id.description);

                    if (minRepSet)
                        minRepDescription.setText("" + minReputation);
                    else
                        minRepDescription.setText(items.get(position).description);
                }

                TextView title = (TextView) row.findViewById(R.id.title);
                title.setText(items.get(position).title);

                return row;
            }

            public int getCount() {
                return items.size();
            }
        }

        private class Item {
            public String title;
            public String description;

            public Item(String title, String description) {
                this.title = title;
                this.description = description;
            }
        }
    }

    public static class ListingPrice extends Fragment {
        private EditText price;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.create_listing_price, container, false);
            Button next = (Button) view.findViewById(R.id.listingPriceNext);
            Button prev = (Button) view.findViewById(R.id.listingPricePrev);
            next.setTypeface(robotoMedium);
            prev.setTypeface(robotoMedium);

            price = (EditText) view.findViewById(R.id.listingMaxbid);

            price.setFilters(new InputFilter[]{new CurrencyFormatInputFilter()});

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(getActivity(), view);
                    CreateListing.nextFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(getActivity(), view);
                    CreateListing.previousFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return view;
        }

        @SuppressWarnings("unchecked")
        private HashMap<String, String> updateListing() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("listing");
            map.put("listing_price", price.getText().toString());
            return map;
        }
    }

    public static class ListingAddress extends Fragment {
        private EditText address, city, state;
        private String stateCode;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final Global global = (Global) getActivity().getApplicationContext();
            View view = inflater.inflate(R.layout.create_listing_address, container, false);
            Button next = (Button) view.findViewById(R.id.listingAddressNext);
            Button prev = (Button) view.findViewById(R.id.listingAddressPrev);
            next.setTypeface(robotoMedium);
            prev.setTypeface(robotoMedium);

            address = (EditText) view.findViewById(R.id.listingStreet);
            address.setTypeface(robotoRegular);
            city = (EditText) view.findViewById(R.id.listingCity);
            city.setTypeface(robotoRegular);
            state = (EditText) view.findViewById(R.id.listingState);
            state.setTypeface(robotoRegular);
            final com.rey.material.widget.CheckBox currentLoc = (com.rey.material.widget.CheckBox)
                    view.findViewById(R.id.current_location_checkbox);
            TextView checkboxText = (TextView) view.findViewById(R.id.checkboxText);
            checkboxText.setTypeface(robotoRegular);

            currentLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentLoc.isChecked()) {
                        Geocoder coder = new Geocoder(getActivity());
                        List<Address> addresses = null;

                        try {
                            addresses = coder.getFromLocation(global.getCurrentLatitude(), global.getCurrentLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (addresses == null || addresses.size() == 0) {
                            Toast.makeText(getActivity().getApplicationContext(), "Could not get location data!", Toast.LENGTH_LONG).show();
                            currentLoc.setChecked(false);
                            return;
                        }

                        Address addr = addresses.get(0);

                        address.setText(addr.getThoroughfare());
                        city.setText(addr.getLocality());
                        state.setText(addr.getAdminArea());


                    } else {
                        address.setText("");
                        city.setText("");
                        state.setText("");
                    }
                }
            });


            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = state.getText().toString();
                    if (s.length() != 2) {
                        String correction = Helper.stateToStateCode(s);
                        if (correction == null) {
                            Toast.makeText(getActivity(), "Check spelling of the state!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        stateCode = correction;
                    } else {
                        stateCode = s;
                    }

                    hideKeyboard(getActivity(), view);
                    CreateListing.nextFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(getActivity(), view);
                    CreateListing.previousFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return view;
        }

        @SuppressWarnings("unchecked")
        private HashMap<String, String> updateListing() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("listing");
            map.put("listing_address", address.getText().toString());
            map.put("listing_city", city.getText().toString());
            map.put("listing_state", stateCode);
            return map;
        }
    }

    public static class ReviewListing extends Fragment {
        private ImageView[] images = new ImageView[3];
        private String jobName_val, startingBid_val, address_val, city_val, state_val, description_val, expiration_val, minRep_val;
        private String[] picturePaths;
        private JSONObject userData;
        private ProgressDialog creatingProgress;

        @SuppressWarnings("unchecked")
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.create_listing_review, container, false);
            Button prev = (Button) view.findViewById(R.id.previous);
            prev.setTypeface(robotoMedium);
            Button create = (Button) view.findViewById(R.id.create_listing);
            create.setTypeface(robotoMedium);
            final HashMap<String, String> data = (HashMap<String, String>) getArguments().getSerializable("listing");
            userData = ((Global) getActivity().getApplicationContext()).getUserData();

            TextView jobName = (TextView) view.findViewById(R.id.description_job_name);
            jobName.setTypeface(robotoRegular);
            TextView startingBid = (TextView) view.findViewById(R.id.description_starting_bid);
            startingBid.setTypeface(robotoRegular);
            TextView address = (TextView) view.findViewById(R.id.description_address);
            address.setTypeface(robotoRegular);
            TextView description = (TextView) view.findViewById(R.id.description_job_description);
            description.setTypeface(robotoRegular);
            TextView expiration = (TextView) view.findViewById(R.id.description_expiration_date);
            expiration.setTypeface(robotoRegular);
            TextView minRep = (TextView) view.findViewById(R.id.description_min_rep);
            minRep.setTypeface(robotoRegular);
            TextView more = (TextView) view.findViewById(R.id.more);
            more.setTypeface(robotoRegular);

            images[0] = (ImageView) view.findViewById(R.id.preview_image_0);
            images[1] = (ImageView) view.findViewById(R.id.preview_image_1);
            images[2] = (ImageView) view.findViewById(R.id.preview_image_2);

            jobName_val = data.get("listing_name");
            startingBid_val = data.get("listing_price");
            address_val = data.get("listing_address");
            city_val = data.get("listing_city");
            state_val = data.get("listing_state");
            String fullAddress = address_val + ", " + city_val + ", " + state_val;

            if (data.get("listing_description") != null)
                description_val = data.get("listing_description");

            if (data.get("listing_expiration") != null)
                expiration_val = data.get("listing_expiration");

            if (data.get("min_reputation") != null)
                minRep_val = data.get("min_reputation");

            jobName.setText(jobName_val);
            startingBid.setText("$" + startingBid_val);
            address.setText(fullAddress);
            description.setText(description_val);
            expiration.setText(expiration_val);
            minRep.setText(minRep_val);

            int numPictures = Integer.parseInt(data.get("num_pictures"));
            picturePaths = new String[numPictures];

            for (int i = 0; i < numPictures; i++) {
                picturePaths[i] = data.get("picture_" + i);
            }

            if (numPictures > 3) {
                more.setVisibility(TextView.VISIBLE);
                numPictures = 3;
            }

            for (int i = 0; i < numPictures; i++) {
                setPic(picturePaths[i], i);
            }

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(getActivity(), view);
                    CreateListing.previousFragment(getActivity(), data, getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createListing();
                }
            });

            return view;
        }

        private void setPic(String path, int image) {
            int targetW = 50;
            int targetH = 50;

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
            images[image].setImageBitmap(bitmap);
        }

        private void createListing() {
            creatingProgress = ProgressDialog.show(getActivity(), "Creating Listing", "Please wait while we create your listing...", true, false);

            new AsyncTask<String, Void, String>() {
                private int response;
                private String listingID;

                protected String doInBackground(String... urls) {
                    Geocoder geo = new Geocoder(getActivity());
                    String loc = Resource.formatLocation(address_val, city_val, state_val);
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

                    String email = null;
                    String password = null;

                    try {
                        email = userData.getString("email");
                        password = userData.getString("password");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject obj = Listing.create(jobName_val, startingBid_val, minRep_val, expiration_val, address_val, city_val,
                            state_val, latitude, longitude, email, password, description_val);

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
                        if (picturePaths.length != 0)
                            uploadPictures(listingID);
                        else
                            alertCreateListingSuccess(listingID);
                    } else if (response == Error.ERROR_SERVER_COMMUNICATION) {
                        creatingProgress.dismiss();
                        alertErrorServer();
                    } else if (response == Error.ERROR_LOCATION_NOT_FOUND) {
                        creatingProgress.dismiss();
                        alertLocationNotFound();
                    }
                }
            }.execute();
        }

        private void uploadPictures(final String listingID) {
            new AsyncTask<String, Void, String>() {
                private int response;

                protected String doInBackground(String... urls) {
                    String email = null;
                    String password = null;
                    try {
                        email = userData.getString("email");
                        password = userData.getString("password");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < picturePaths.length; i++) {
                        response = Listing.upload(picturePaths[i], listingID, email, password);
                    }

                    return null;
                }

                protected void onPostExecute(String result) {
                    if (response == -1) {
                        alertCreateListingSuccess(listingID);
                    }
                }
            }.execute();
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
    }

    private static void hideKeyboard(Activity a, View v){
        InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
