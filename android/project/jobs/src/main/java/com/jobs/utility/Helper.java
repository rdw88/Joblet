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
}
