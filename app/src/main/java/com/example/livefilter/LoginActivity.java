package com.example.livefilter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btLogin;
    private Button btSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btLogin = findViewById(R.id.btLogin);
        btSignup = findViewById(R.id.btSignUp);

        // set up custom actionbar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        // if someone is logged in (persisting signin), navigate to main activity instead of login activity
        if(ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Login button clicked");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);

            }
        });

        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // navigate to signup activity when button is clicked
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);

            }
        });
    }


    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        // logs user in in background thread
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            // if requests succeeds, exception will be null
            public void done(ParseUser user, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Login issue", e);
                    return;
                }
                // if login is correct, navigate to mainActivity
                goMainActivity();

            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // finish activity so user can't go back to signin page once logged in
        finish();
    }
}