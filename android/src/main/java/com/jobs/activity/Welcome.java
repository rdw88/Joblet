package com.jobs.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import com.jobs.R;
import com.jobs.backend.Resource;

public class Welcome extends Activity {
    private Button login, createAccount, forgotPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);
        Resource.initLocations(this);
        Resource.initTags(this);
    }

    protected void onStart() {
        super.onStart();
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/verdana.ttf");
        login = (Button) findViewById(R.id.welcome_button_login);
        login.setTypeface(customFont);
        createAccount = (Button) findViewById(R.id.welcome_button_createacc);
        createAccount.setTypeface(customFont);
        forgotPassword = (Button) findViewById(R.id.welcome_button_forgotpass);
        forgotPassword.setTypeface(customFont);

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
