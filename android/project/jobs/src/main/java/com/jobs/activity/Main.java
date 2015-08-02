package com.jobs.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.PopupMenu;

import com.jobs.R;
import com.jobs.backend.Profile;
import com.jobs.fragment.CreateListing;
import com.jobs.fragment.CreateListingsNew;
import com.jobs.fragment.EditProfile;
import com.jobs.fragment.Home;
import com.jobs.fragment.MyListings;
import com.jobs.fragment.Notifications;
import com.jobs.utility.Global;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.adapter.DrawerAdapter;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.rey.material.app.ToolbarManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;


public class Main extends AppCompatActivity {
    private Drawer drawer = null;
    private Toolbar mToolbar;
    private JSONObject data;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        data = ((Global) getApplicationContext()).getUserData();

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.softwhite));

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withFullscreen(false)
                .withActionBarDrawerToggle(true)
                .withSelectedItem(0)
                .withToolbar(mToolbar)
                .withTranslucentStatusBar(false)
                .addDrawerItems(
                        //new PrimaryDrawerItem().withName("Edit Profile").withIcon(R.drawable.icon_user_64),
                        //new DividerDrawerItem(),
                       // new PrimaryDrawerItem().withName("Active Jobs").withIcon(R.drawable.icon_briefcase_64),
                        //new PrimaryDrawerItem().withName("Active Listings").withIcon(R.drawable.icon_list_64),
                       // new PrimaryDrawerItem().withName("My Bids").withIcon(R.drawable.icon_gavel_64),
                       // new PrimaryDrawerItem().withName("Watchlist").withIcon(R.drawable.icon_star_64)
                        new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.icon_user_64),
                        new PrimaryDrawerItem().withName("Edit Profile").withIcon(R.drawable.icon_user_64),
                        new PrimaryDrawerItem().withName("Notifications").withIcon(R.drawable.icon_user_64),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Create Listing").withIcon(R.drawable.icon_user_64),
                        new PrimaryDrawerItem().withName("Active Jobs (TODO)").withIcon(R.drawable.icon_briefcase_64),
                        new PrimaryDrawerItem().withName("Active Listings").withIcon(R.drawable.icon_list_64),
                        new PrimaryDrawerItem().withName("Watchlist (TODO)").withIcon(R.drawable.icon_star_64),
                        new PrimaryDrawerItem().withName("Logout").withIcon(R.drawable.icon_user_32)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int position, long id, IDrawerItem iDrawerItem) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        switch(position){
                            case 0:
                                transaction.replace(R.id.fragment_holder, new Home()).commit();
                                break;
                            case 1:
                                transaction.replace(R.id.fragment_holder, new EditProfile()).commit();
                                break;
                            case 2:
                                transaction.replace(R.id.fragment_holder, new Notifications()).commit();
                                break;
                            case 4:
                                transaction.replace(R.id.fragment_holder, new CreateListingsNew()).commit();
                                break;
                            case 5:
                                break;
                            case 6:
                                transaction.replace(R.id.fragment_holder, new MyListings()).commit();
                                break;
                            case 7:
                                //Intent intent4 = new Intent(Main.this, Watchlist.class);
                                //startActivity(intent4);
                                break;
                            case 8:
                                logout();
                                break;
                        }

                        return false;
                    }


                })
                .build();

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }

       FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
       transaction.add(R.id.fragment_holder, new Home()).commit();
    }

    private void logout() {
        new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... params) {
                String email = "";
                try {
                    email = data.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Profile.logout(email);
                return null;
            }

            protected void onPostExecute(String res) {
                setResult(0xff);
                finish();
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
