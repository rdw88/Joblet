package com.jobs.utility;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageButton;

import com.jobs.R;
import com.rey.material.app.Dialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Helper {
    public static String lastCameraPicturePath;

    public static final int RESULT_CAMERA = 0xac;
    public static final int RESULT_GALLERY = 0xab;

    public static final boolean IN_SERVER_DEBUG_MODE = false;

    public static Dialog galleryOrCameraDialog(final Fragment context) {
        final Dialog selector = new Dialog(context.getActivity());
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
        if (!context.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            camera.setVisibility(ImageButton.GONE);
        }

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                context.startActivityForResult(intent, RESULT_GALLERY);
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

                if (takePicture.resolveActivity(context.getActivity().getPackageManager()) != null) {
                    if (imageFile != null) {
                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                        context.startActivityForResult(takePicture, RESULT_CAMERA);
                    }
                }
            }
        });

        return selector;
    }

    public static String stateToStateCode(String state) {
        Map<String, String> states = new HashMap<String, String>();
        states.put("Alabama","AL");
        states.put("Alaska","AK");
        states.put("Alberta","AB");
        states.put("American Samoa","AS");
        states.put("Arizona","AZ");
        states.put("Arkansas","AR");
        states.put("Armed Forces (AE)","AE");
        states.put("Armed Forces Americas","AA");
        states.put("Armed Forces Pacific","AP");
        states.put("British Columbia","BC");
        states.put("California","CA");
        states.put("Colorado","CO");
        states.put("Connecticut","CT");
        states.put("Delaware","DE");
        states.put("District Of Columbia","DC");
        states.put("Florida","FL");
        states.put("Georgia","GA");
        states.put("Guam","GU");
        states.put("Hawaii","HI");
        states.put("Idaho","ID");
        states.put("Illinois","IL");
        states.put("Indiana","IN");
        states.put("Iowa","IA");
        states.put("Kansas","KS");
        states.put("Kentucky","KY");
        states.put("Louisiana","LA");
        states.put("Maine","ME");
        states.put("Manitoba","MB");
        states.put("Maryland","MD");
        states.put("Massachusetts","MA");
        states.put("Michigan","MI");
        states.put("Minnesota","MN");
        states.put("Mississippi","MS");
        states.put("Missouri","MO");
        states.put("Montana","MT");
        states.put("Nebraska","NE");
        states.put("Nevada","NV");
        states.put("New Brunswick","NB");
        states.put("New Hampshire","NH");
        states.put("New Jersey","NJ");
        states.put("New Mexico","NM");
        states.put("New York","NY");
        states.put("Newfoundland","NF");
        states.put("North Carolina","NC");
        states.put("North Dakota","ND");
        states.put("Northwest Territories","NT");
        states.put("Nova Scotia","NS");
        states.put("Nunavut","NU");
        states.put("Ohio","OH");
        states.put("Oklahoma","OK");
        states.put("Ontario","ON");
        states.put("Oregon","OR");
        states.put("Pennsylvania","PA");
        states.put("Prince Edward Island","PE");
        states.put("Puerto Rico","PR");
        states.put("Quebec","PQ");
        states.put("Rhode Island","RI");
        states.put("Saskatchewan","SK");
        states.put("South Carolina","SC");
        states.put("South Dakota","SD");
        states.put("Tennessee","TN");
        states.put("Texas","TX");
        states.put("Utah","UT");
        states.put("Vermont","VT");
        states.put("Virgin Islands","VI");
        states.put("Virginia","VA");
        states.put("Washington","WA");
        states.put("West Virginia","WV");
        states.put("Wisconsin","WI");
        states.put("Wyoming","WY");
        states.put("Yukon Territory","YT");

        if (!states.containsKey(state)) {
            return null;
        }

        return states.get(state);
    }
}
