package com.jobs.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jobs.R;
import com.jobs.backend.Resource;
import com.jobs.utility.CurrencyFormatInputFilter;
import com.jobs.utility.Global;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CreateListingsNew extends Fragment {
    private static final String FRAGMENT_INDEX_KEY = "fragment_index";
    private static final Fragment[] FRAGMENT_ORDER = {new ListingName(), new ListingPrice(), new ListingAddress(), new RecommendedSettings(),
                new OptionalSettings()};

    private static Typeface robotoRegular, robotoMedium, robotoThin;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf");
        robotoThin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_listing_listingname, container, false);

        final EditText name = (EditText) view.findViewById(R.id.listingTitle);
        Button next = (Button) view.findViewById(R.id.listingTitleNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> args = new HashMap<>();
                args.put("listing_name", name.getText().toString());
                CreateListingsNew.nextFragment(getActivity(), args, 0);
            }
        });

        return view;
    }

    private static void nextFragment(FragmentActivity parent, HashMap<String, String> args, int currentIndex) {
        if (currentIndex >= FRAGMENT_ORDER.length - 1)
            return;

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
            Button next = (Button) view.findViewById(R.id.listingTitleNext);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, String> args = new HashMap<>();
                    args.put("listing_name", name.getText().toString());
                    CreateListingsNew.nextFragment(getActivity(), args, 0);
                }
            });

            return view;
        }
    }

    public static class RecommendedSettings extends Fragment {
        private String description;
        private List<String> picturePaths = new ArrayList<>();

        private String lastCameraPicturePath;
        private ImageView[] images = new ImageView[2];
        private TextView more, viewDescription;
        private EditText enterDescription;

        private Dialog selector;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.create_listing_recommended, container, false);
            Button next = (Button) view.findViewById(R.id.listingDescriptionNext);
            Button prev = (Button) view.findViewById(R.id.listingDescriptionPrev);

            ArrayList<Item> items = new ArrayList<>();
            items.add(new Item("Add Description", "<No description>"));
            items.add(new Item("Add Pictures", "No pictures set."));

            ListView list = (ListView) view.findViewById(R.id.optionalListingList);
            Adapter adapter = new Adapter(getActivity(), items);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 0) {
                        viewDescription.setText("Max 500 characters.");
                        enterDescription.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        selector = new Dialog(getActivity());
                        selector.setCancelable(true);
                        selector.negativeAction("Cancel");
                        selector.negativeActionTextColor(0xfff05d5e);
                        selector.negativeActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                selector.dismiss();
                            }
                        });

                        selector.setContentView(R.layout.from_gallery_or_camera);
                        selector.setTitle("Choose a Method");
                        selector.titleColor(0xff444444);

                        ImageButton gallery = (ImageButton) selector.findViewById(R.id.button_gallery);
                        ImageButton camera = (ImageButton) selector.findViewById(R.id.button_camera);

                        gallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, 0xab);
                            }
                        });

                        camera.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                String imageFileName = "JPEG_" + timeStamp + "_";
                                File imageFile = null;

                                try {
                                    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                                    imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
                                    lastCameraPicturePath = imageFile.getAbsolutePath();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                                    if (imageFile != null) {
                                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                                        startActivityForResult(takePicture, 0xac);
                                    }
                                }
                            }
                        });

                        selector.show();
                    }
                }
            });


            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateListingsNew.nextFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateListingsNew.previousFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return view;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case 0xab:
                    if (resultCode == Activity.RESULT_OK) {
                        picturePaths.add(Resource.getRealPathFromURI(getActivity(), data.getData()));
                    }

                    break;

                case 0xac:
                    if (resultCode == Activity.RESULT_OK) {
                        picturePaths.add(lastCameraPicturePath);
                    }

                    break;
            }

            // TODO: Fix this bug. image1 not getting set, "more" TextView getting ignored.
            if (resultCode == Activity.RESULT_OK) {
                if (picturePaths.size() == 1) {
                    setPic(picturePaths.get(0), 0);
                } else if (picturePaths.size() > 1) {
                    setPic(picturePaths.get(0), 0);
                    setPic(picturePaths.get(1), 1);
                }

                if (picturePaths.size() > 2)
                    more.setVisibility(View.VISIBLE);

                selector.dismiss();
            }
        }

        private void setPic(String path, int image) {
            int targetW = images[image].getMeasuredWidth();
            int targetH = images[image].getMeasuredHeight();

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
                    enterDescription = (EditText) row.findViewById(R.id.enter_description);
                    enterDescription.setTextColor(0xff555555);
                    viewDescription = (TextView) row.findViewById(R.id.description);
                } else if (position == 1) {
                    row = inflater.inflate(R.layout.create_listing_add_pictures_list_item, parent, false);

                    images[0] = (ImageView) row.findViewById(R.id.add_pictures_preview_0);
                    images[1] = (ImageView) row.findViewById(R.id.add_pictures_preview_1);

                    more = (TextView) row.findViewById(R.id.more);
                }

                TextView title = (TextView) row.findViewById(R.id.title);
                TextView description = (TextView) row.findViewById(R.id.description);

                title.setText(items.get(position).title);
                description.setText(items.get(position).descrption);

                return row;
            }

            public int getCount() {
                return items.size();
            }
        }

        private class Item {
            public String title;
            public String descrption;

            public Item(String title, String descrption) {
                this.title = title;
                this.descrption = descrption;
            }
        }
    }

    public static class OptionalSettings extends Fragment {
        private String description;
        private Bitmap[] pictures;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.create_listing_optional_settings, container, false);
            Button next = (Button) view.findViewById(R.id.listingDescriptionNext);
            Button prev = (Button) view.findViewById(R.id.listingDescriptionPrev);

            ArrayList<Item> items = new ArrayList<>();
            items.add(new Item("Add Expiration Date/Time", "Expires never."));
            items.add(new Item("Add Minimum Applicant Reputation", "None set."));

            ListView list = (ListView) view.findViewById(R.id.optionalListingList);
            Adapter adapter = new Adapter(getActivity(), items);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }
            });


            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateListingsNew.nextFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateListingsNew.previousFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return view;
        }

        @SuppressWarnings("unchecked")
        private HashMap<String, String> updateListing() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("listing");
            map.put("listing_description", description);
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

                TextView title = (TextView) row.findViewById(R.id.title);
                TextView description = (TextView) row.findViewById(R.id.description);

                title.setText(items.get(position).title);
                description.setText(items.get(position).descrption);

                return row;
            }

            public int getCount() {
                return items.size();
            }
        }

        private class Item {
            public String title;
            public String descrption;

            public Item(String title, String descrption) {
                this.title = title;
                this.descrption = descrption;
            }
        }
    }

    public static class ListingPrice extends Fragment {
        private EditText price;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.create_listing_price, container, false);
            Button next = (Button) view.findViewById(R.id.listingPriceNext);
            Button prev = (Button) view.findViewById(R.id.listingPricePrev);
            price = (EditText) view.findViewById(R.id.listingMaxbid);

            price.setFilters(new InputFilter[]{new CurrencyFormatInputFilter()});

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateListingsNew.nextFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateListingsNew.previousFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
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

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final Global global = (Global) getActivity().getApplicationContext();
            View view = inflater.inflate(R.layout.create_listing_address, container, false);
            Button next = (Button) view.findViewById(R.id.listingAddressNext);
            Button prev = (Button) view.findViewById(R.id.listingAddressPrev);
            address = (EditText) view.findViewById(R.id.listingStreet);
            city = (EditText) view.findViewById(R.id.listingCity);
            state = (EditText) view.findViewById(R.id.listingState);
            final CheckBox currentLoc = (CheckBox) view.findViewById(R.id.current_location_checkbox);

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
                    CreateListingsNew.nextFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateListingsNew.previousFragment(getActivity(), updateListing(), getArguments().getInt(FRAGMENT_INDEX_KEY));
                }
            });

            return view;
        }

        @SuppressWarnings("unchecked")
        private HashMap<String, String> updateListing() {
            HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable("listing");
            map.put("listing_address", address.getText().toString());
            map.put("listing_city", city.getText().toString());
            map.put("listing_state", state.getText().toString());
            return map;
        }
    }
}
