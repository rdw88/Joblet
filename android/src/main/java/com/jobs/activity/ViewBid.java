package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jobs.R;

public class ViewBid extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.view_bid, null);

        TextView email = (TextView) v.findViewById(R.id.bidder_email);
        TextView bid = (TextView) v.findViewById(R.id.bid_amount);

        email.setText(getIntent().getExtras().getString("email"));
        bid.setText("$" + getIntent().getExtras().getString("amount"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                finish();
            }
        };

        builder.setView(v);
        builder.setPositiveButton(R.string.accept, listener);
        builder.setNegativeButton(R.string.decline, listener);
        builder.setTitle("Respond to Offer");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
