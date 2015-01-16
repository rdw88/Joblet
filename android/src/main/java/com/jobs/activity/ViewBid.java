package com.jobs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
<<<<<<< HEAD
=======
import android.content.Intent;
import android.os.AsyncTask;
>>>>>>> d73d70b16a338df19375feee7cad38c5800316f8
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

<<<<<<< HEAD
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                finish();
=======
                    protected void onPostExecute(String message) {
                        Intent intent = new Intent();
                        intent.putExtra("position", getIntent().getExtras().getInt("position"));
                        intent.putExtra("action", 1);
                        setResult(0xff, intent);
                        finish();
                    }
                }.execute();
>>>>>>> d73d70b16a338df19375feee7cad38c5800316f8
            }
        };

<<<<<<< HEAD
        builder.setView(v);
        builder.setPositiveButton(R.string.accept, listener);
        builder.setNegativeButton(R.string.decline, listener);
        builder.setTitle("Respond to Offer");

        AlertDialog dialog = builder.create();
        dialog.show();
=======
                    protected void onPostExecute(String message) {
                        Intent intent = new Intent();
                        intent.putExtra("position", getIntent().getExtras().getInt("position"));
                        intent.putExtra("action", 2);
                        setResult(0xff, intent);
                        finish();
                    }
                }.execute();
            }
        });

        close.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("position", getIntent().getExtras().getInt("position"));
                intent.putExtra("action", 0);
                setResult(0xff, intent);
                finish();
            }
        });
>>>>>>> d73d70b16a338df19375feee7cad38c5800316f8
    }
}
