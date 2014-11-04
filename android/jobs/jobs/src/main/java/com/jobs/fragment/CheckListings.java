package com.jobs.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.jobs.R;

import java.util.ArrayList;
import java.util.List;

public class CheckListings extends Fragment {
    private ListView listings;

    public void onStart() {
        super.onStart();

        ArrayList<Item> list = new ArrayList<>();
        list.add(new Item("Accounting Job", new String[] {"Accounting", "Finance"}, 10));
        list.add(new Item("Programmer Wanted!", new String[] {"Programming", "Computers", "IT"}, 12));
        list.add(new Item("Yard Work", new String[] {"Gardening"}, 8));
        list.add(new Item("Help wanted with filework", new String[] {"Office"}, 4));
        list.add(new Item("Accounting Job", new String[] {"Accounting", "Finance"}, 10));
        list.add(new Item("Programmer Wanted!", new String[] {"Programming", "Computers", "IT"}, 12));
        list.add(new Item("Yard Work", new String[] {"Gardening"}, 8));
        list.add(new Item("Help wanted with filework", new String[] {"Office"}, 4));
        list.add(new Item("Accounting Job", new String[] {"Accounting", "Finance"}, 10));
        list.add(new Item("Programmer Wanted!", new String[] {"Programming", "Computers", "IT"}, 12));
        list.add(new Item("Yard Work", new String[] {"Gardening"}, 8));
        list.add(new Item("Help wanted with filework", new String[] {"Office"}, 4));
        list.add(new Item("Accounting Job", new String[] {"Accounting", "Finance"}, 10));
        list.add(new Item("Programmer Wanted!", new String[] {"Programming", "Computers", "IT"}, 12));
        list.add(new Item("Yard Work", new String[] {"Gardening"}, 8));
        list.add(new Item("Help wanted with filework", new String[] {"Office"}, 4));

        listings = (ListView) getActivity().findViewById(R.id.listing_list);
        ListingAdapter adapter = new ListingAdapter(getActivity(), list);
        listings.setAdapter(adapter);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.check_listings, container, false);
    }

    private class ListingAdapter extends ArrayAdapter<Item> {
        private Context context;
        private ArrayList<Item> items;

        public ListingAdapter(Context context, ArrayList<Item> items) {
            super(context, R.layout.list_item, items);
            this.context = context;
            this.items = items;
        }

        @Override
        public View getView(int position, View currentView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.list_item, parent, false);

            TextView title = (TextView) row.findViewById(R.id.job_title);
            TextView tags = (TextView) row.findViewById(R.id.listing_tags);
            TextView reputation = (TextView) row.findViewById(R.id.owner_reputation);

            title.setText(items.get(position).title);
            String[] ts = items.get(position).tags;
            String label = "Tags: ";

            for (int i = 0; i < ts.length - 1; i++) {
                label += ts[i] + ", ";
            }
            label += ts[ts.length - 1];

            tags.setText(label);
            reputation.setText(Integer.toString(items.get(position).reputation));

            return row;
        }
    }

    private class Item {
        public String title;
        public String[] tags;
        public int reputation;

        public Item(String title, String[] tags, int reputation) {
            this.title = title;
            this.tags = tags;
            this.reputation = reputation;
        }
    }
}
