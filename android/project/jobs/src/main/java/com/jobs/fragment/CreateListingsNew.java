package com.jobs.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jobs.R;
import com.jobs.backend.Resource;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateListingsNew extends Fragment {
    private static final String FRAGMENT_INDEX_KEY = "fragment_index";
    private static final Fragment[] FRAGMENT_ORDER = {new ListingName(), new ListingPrice(), new ListingAddress(), new RecommendedSettings(),
                new OptionalSettings(), new Review()};

    private static Typeface robotoRegular, robotoMedium, robotoThin;
    private ProgressDialog generatingListing;
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
        TextView jobTitle = (TextView) view.findViewById(R.id.instructionsTitle);
        jobTitle.setTypeface(robotoRegular);
        TextView desc = (TextView) view.findViewById(R.id.instructionsSubTitle);
        desc.setTypeface(robotoRegular);
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
            TextView instructionsTitle = (TextView) view.findViewById(R.id.instructionsTitle);
            instructionsTitle.setTypeface(robotoRegular);
            TextView instructionsSubTitle = (TextView) view.findViewById(R.id.instructionsSubTitle);
            instructionsSubTitle.setTypeface(robotoRegular);
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
        private Bitmap[] pictures;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.create_listing_recommended, container, false);
            Button next = (Button) view.findViewById(R.id.listingDescriptionReview);
            Button prev = (Button) view.findViewById(R.id.listingDescriptionPrev);

            TextView header = (TextView) view.findViewById(R.id.header);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView description = (TextView) view.findViewById(R.id.description);
            header.setTypeface(robotoRegular);
            title.setTypeface(robotoRegular);
            description.setTypeface(robotoRegular);

            ArrayList<Item> items = new ArrayList<>();
            items.add(new Item("Add Description", "<No description>"));
            items.add(new Item("Add Pictures", "No pictures set."));

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
                title.setTypeface(robotoRegular);
                TextView description = (TextView) row.findViewById(R.id.description);
                description.setTypeface(robotoRegular);
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
            Button review = (Button) view.findViewById(R.id.listingDescriptionReview);
            Button prev = (Button) view.findViewById(R.id.listingDescriptionPrev);
            TextView header = (TextView) view.findViewById(R.id.header);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView description = (TextView) view.findViewById(R.id.description);
            header.setTypeface(robotoRegular);
            title.setTypeface(robotoRegular);
            description.setTypeface(robotoRegular);

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


            review.setOnClickListener(new View.OnClickListener() {
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
            TextView header = (TextView) view.findViewById(R.id.header);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView description = (TextView) view.findViewById(R.id.description);
            header.setTypeface(robotoRegular);
            title.setTypeface(robotoRegular);
            description.setTypeface(robotoRegular);
            price = (EditText) view.findViewById(R.id.listingMaxbid);
            price.setTypeface(robotoRegular);
             class CurrencyFormatInputFilter implements InputFilter {
                Pattern mPattern = Pattern.compile("(0|[1-9]+[0-9]*)?(\\.[0-9]{0,2})?");
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                        int dstart, int dend) {
                    String result =
                            dest.subSequence(0, dstart)
                                    + source.toString()
                                    + dest.subSequence(dend, dest.length());
                    Matcher matcher = mPattern.matcher(result);
                    if (!matcher.matches()) return dest.subSequence(dstart, dend);
                    return null;
                }
            }
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
            View view = inflater.inflate(R.layout.create_listing_address, container, false);
            Button next = (Button) view.findViewById(R.id.listingAddressNext);
            Button prev = (Button) view.findViewById(R.id.listingAddressPrev);
            TextView header = (TextView) view.findViewById(R.id.header);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView description = (TextView) view.findViewById(R.id.description);
            header.setTypeface(robotoRegular);
            title.setTypeface(robotoRegular);
            description.setTypeface(robotoRegular);
            address = (EditText) view.findViewById(R.id.listingStreet);
            address.setTypeface(robotoRegular);
            city = (EditText) view.findViewById(R.id.listingCity);
            city.setTypeface(robotoRegular);
            state = (EditText) view.findViewById(R.id.listingState);
            state.setTypeface(robotoRegular);

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

    //Todo: Finish class Review
    public static class Review extends Fragment{
        private String
                sa,
               // mr,
                cityStr,
                addrStr,
                stateStr,
                description;
        private String pID = null;
        private String title = info.get("listing_name");
        private String mr = info.get("m");

    }

    // TODO: Finish createListing method
    /*
    private void createListing(final String password){
        // TODO: Add a loading bar to this dialog
        generatingListing = ProgressDialog.show(getActivity(),
                "Creating a listing preview",
                "Please wait while we create your job listing.",
                true, //indeterminate
                false); //cancelable
        new AsyncTask<String, Void, String>(){
            private int response;
            private String listingID;

            protected String doInBackground(String... urls){
                Geocoder geo = new Geocoder(getActivity());
                String loc = Resource.formatLocation(address.)
            }
        }
    }
    */
}
