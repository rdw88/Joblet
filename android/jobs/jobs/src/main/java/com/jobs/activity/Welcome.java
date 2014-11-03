package com.jobs.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.jobs.R;

public class Welcome extends Activity {
    private Button login, createAccount, forgotPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
    }

    protected void onStart() {
        super.onStart();

        login = (Button) findViewById(R.id.button_login);
        createAccount = (Button) findViewById(R.id.button_createacc);
        forgotPassword = (Button) findViewById(R.id.button_forgot_pass);

        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this, Login.class));
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this, CreateAccount.class));
            }
        });
    }
}
